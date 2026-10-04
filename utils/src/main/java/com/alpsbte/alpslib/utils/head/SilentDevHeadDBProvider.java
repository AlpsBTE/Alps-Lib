package com.alpsbte.alpslib.utils.head;

import com.alpsbte.alpslib.utils.item.Item;
import io.github.silentdevelopment.headdb.HeadDBService;
import io.github.silentdevelopment.headdb.model.Head;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * {@link HeadProvider} backed by SilentDevelopment HeadDB's Bukkit service.
 */
public class SilentDevHeadDBProvider implements HeadProvider {
    private final HeadDBService service;

    /**
     * @param service registered HeadDB service
     */
    public SilentDevHeadDBProvider(@NotNull HeadDBService service) {
        this.service = service;
    }

    /**
     * Detects a registered {@link HeadDBService}.
     * <p>
     * Callers should ensure the HeadDB API is present on the classpath (for example via
     * {@link Class#forName(String)}) before invoking this method.
     *
     * @param logger logger used for unexpected initialization failures
     * @return a provider, or {@code null} if the service is not registered
     */
    public static @Nullable SilentDevHeadDBProvider detect(@NotNull ComponentLogger logger) {
        try {
            RegisteredServiceProvider<HeadDBService> registration =
                    Bukkit.getServicesManager().getRegistration(HeadDBService.class);
            if (registration == null) {
                return null;
            }
            return new SilentDevHeadDBProvider(registration.getProvider());
        } catch (Throwable t) {
            logger.error("Failed to initialize SilentDevelopment HeadDB provider", t);
            return null;
        }
    }

    @Override
    public @Nullable ItemStack getItemHead(@NotNull String id) {
        Optional<Head> head = service.find(id);
        if (head.isEmpty()) {
            return null;
        }

        String hash = head.get().texture().hash();
        if (hash == null || hash.isBlank()) {
            return null;
        }

        // Profileable.detect accepts texture hashes as well as base64 payloads.
        return Item.createCustomHeadBase64(hash, null, null);
    }

    @Override
    public boolean hasAnyProvider() {
        return service != null;
    }
}
