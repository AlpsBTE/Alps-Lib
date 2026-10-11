package com.alpsbte.alpslib.utils.head;

import lombok.experimental.UtilityClass;
import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Creates a {@link HeadProvider} by detecting optional head-database plugins.
 * <p>
 * Detection order:
 * <ol>
 *   <li>SilentDevelopment HeadDB</li>
 *   <li>Arcaniax HeadDatabase</li>
 * </ol>
 * Returns {@code null} when neither integration is available.
 */
@UtilityClass
public final class HeadProviderFactory {

    /**
     * Detects and creates a head provider for the given plugin.
     * <p>
     * When {@code lookupService} is non-null, the created provider is attached to it.
     * For Arcaniax HeadDatabase, a load listener is registered so pending custom heads
     * are flushed once the database API becomes available.
     *
     * @param plugin        owning plugin
     * @param lookupService optional lookup service to update when the provider is ready
     * @return a provider, or {@code null} if none is available
     */
    public static @Nullable HeadProvider createForPlugin(
            @NotNull JavaPlugin plugin,
            @Nullable HeadLookupService lookupService
    ) {
        HeadProvider provider = createProvider(plugin, lookupService);
        if (lookupService != null) {
            lookupService.setProvider(provider);
        }
        return provider;
    }

    private static @Nullable HeadProvider createProvider(
            @NotNull JavaPlugin plugin,
            @Nullable HeadLookupService lookupService
    ) {
        // Prefer SilentDevelopment HeadDB if available.
        // Class.forName first so SilentDevHeadDBProvider is never linked when the API is absent.
        try {
            Class.forName("io.github.silentdevelopment.headdb.HeadDBService");
            SilentDevHeadDBProvider silent = SilentDevHeadDBProvider.detect(plugin.getComponentLogger());
            if (silent != null) {
                return silent;
            }
        } catch (ClassNotFoundException ignored) {
            // SilentDevelopment HeadDB not present
        } catch (Throwable t) {
            plugin.getComponentLogger().error("Failed to initialize SilentDev HeadDB provider", t);
        }

        // Fall back to Arcaniax HeadDatabase when its API is present.
        try {
            Class.forName("me.arcaniax.hdb.api.HeadDatabaseAPI");
            ArcaniaxHeadProvider arcaniax = new ArcaniaxHeadProvider();
            plugin.getServer().getPluginManager().registerEvents(new Listener() {
                @EventHandler
                public void onDatabaseLoad(DatabaseLoadEvent event) {
                    try {
                        arcaniax.setApi(new HeadDatabaseAPI());
                        if (lookupService != null) {
                            lookupService.flushPendingCustomHeads();
                        }
                    } catch (Throwable t) {
                        plugin.getComponentLogger().error("Failed to initialize Arcaniax HeadDatabase provider", t);
                    }
                }
            }, plugin);
            return arcaniax;
        } catch (ClassNotFoundException ignored) {
            // Arcaniax HeadDatabase not present
        } catch (Throwable t) {
            plugin.getComponentLogger().error("Failed to initialize Arcaniax HeadDatabase provider", t);
        }

        return null;
    }
}
