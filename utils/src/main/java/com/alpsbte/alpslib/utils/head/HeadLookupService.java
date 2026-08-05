package com.alpsbte.alpslib.utils.head;

import com.cryptomorin.xseries.XMaterial;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import io.papermc.paper.datacomponent.DataComponentTypes;
import io.papermc.paper.datacomponent.item.TooltipDisplay;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import net.kyori.adventure.text.logger.slf4j.ComponentLogger;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Resolves custom and player heads through an optional {@link HeadProvider}.
 * <p>
 * Create one instance per plugin via {@link #createForPlugin(JavaPlugin)} and register
 * player join/quit caching with {@link #registerEvents(JavaPlugin)}.
 * <p>
 * Returned {@link ItemStack}s are clones of the cached values so callers can safely mutate them.
 */
public class HeadLookupService {
    private final Cache<String, ItemStack> customHeads = CacheBuilder.newBuilder().build();
    private final Cache<UUID, ItemStack> playerHeads = CacheBuilder.newBuilder().build();
    private final Set<String> pendingCustomHeads = ConcurrentHashMap.newKeySet();
    private volatile HeadProvider provider;
    private boolean providerWarningLogged;
    private final ComponentLogger logger;

    /**
     * Creates a lookup service and auto-detects an available head provider for the plugin.
     *
     * @param plugin owning plugin used for provider detection, event registration helpers, and logging
     * @return a new lookup service instance
     */
    public static @NotNull HeadLookupService createForPlugin(@NotNull JavaPlugin plugin) {
        return new HeadLookupService(plugin);
    }

    /**
     * Creates a lookup service and auto-detects an available head provider for the plugin.
     *
     * @param plugin owning plugin used for provider detection and logging
     */
    public HeadLookupService(@NotNull JavaPlugin plugin) {
        this.logger = plugin.getComponentLogger();
        HeadProviderFactory.createForPlugin(plugin, this);
    }

    /**
     * Creates a lookup service with an explicit provider.
     *
     * @param provider head provider, or {@code null} if none is available
     * @param logger   plugin logger used for warnings
     */
    public HeadLookupService(@Nullable HeadProvider provider, @NotNull ComponentLogger logger) {
        this.logger = logger;
        setProvider(provider);
    }

    /**
     * Registers join/quit listeners that cache and invalidate player heads.
     *
     * @param plugin plugin used to register the listener
     * @return this service for chaining
     */
    public @NotNull HeadLookupService registerEvents(@NotNull JavaPlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new PlayerHeadEventListener(this), plugin);
        return this;
    }

    /**
     * @return the active provider, or {@code null} if none is configured
     */
    public @Nullable HeadProvider getProvider() {
        return provider;
    }

    /**
     * Sets the active provider and flushes any custom heads that were registered before it was ready.
     *
     * @param provider head provider, or {@code null}
     */
    public void setProvider(@Nullable HeadProvider provider) {
        this.provider = provider;
        flushPendingCustomHeads();
    }

    /**
     * Registers a custom head for eager caching.
     * <p>
     * If the provider is not ready yet (for example Arcaniax HeadDatabase before
     * {@code DatabaseLoadEvent}), the ID is queued and resolved once the provider becomes usable.
     *
     * @param headDbId provider-specific head id
     */
    public void registerCustomHead(@NotNull String headDbId) {
        if (provider == null || !provider.hasAnyProvider()) {
            pendingCustomHeads.add(headDbId);
            return;
        }

        ItemStack head = provider.getItemHead(headDbId);
        if (head != null) {
            customHeads.put(headDbId, head);
        }
        pendingCustomHeads.remove(headDbId);
    }

    /**
     * Registers multiple custom heads for eager caching.
     *
     * @param headDbIds provider-specific head ids
     */
    public void registerCustomHeads(@NotNull Collection<String> headDbIds) {
        headDbIds.forEach(this::registerCustomHead);
    }

    /**
     * Removes a custom head from the cache and pending registration queue.
     *
     * @param headDbId provider-specific head id
     */
    public void unregisterCustomHead(@NotNull String headDbId) {
        customHeads.invalidate(headDbId);
        pendingCustomHeads.remove(headDbId);
    }

    /**
     * Clears all cached custom heads and pending registrations.
     */
    public void unregisterCustomHeads() {
        customHeads.invalidateAll();
        pendingCustomHeads.clear();
    }

    /**
     * Caches a player head for the given UUID.
     *
     * @param playerUUID player unique id
     */
    public void registerPlayerHead(@NotNull UUID playerUUID) {
        getPlayerHead(playerUUID);
    }

    /**
     * Removes a cached player head.
     *
     * @param playerUUID player unique id
     */
    public void unregisterPlayerHead(@NotNull UUID playerUUID) {
        playerHeads.invalidate(playerUUID);
    }

    /**
     * Resolves a custom head by provider id.
     * <p>
     * Returns a skeleton skull placeholder when the provider is missing or the id cannot be resolved.
     * Successful resolutions are cached; placeholders are not.
     *
     * @param headDbId provider-specific head id
     * @return a clone of the cached or resolved head item
     */
    public ItemStack getCustomHead(@NotNull String headDbId) {
        ItemStack cached = customHeads.getIfPresent(headDbId);
        if (cached != null) {
            return cached.clone();
        }

        if (provider == null) {
            if (!providerWarningLogged) {
                logger.warn("HeadLookupService: provider not initialized; returning placeholder head.");
                providerWarningLogged = true;
            }
            return XMaterial.SKELETON_SKULL.parseItem();
        }

        ItemStack head = provider.getItemHead(headDbId);
        if (head != null) {
            customHeads.put(headDbId, head);
            pendingCustomHeads.remove(headDbId);
            return head.clone();
        }

        return XMaterial.SKELETON_SKULL.parseItem();
    }

    /**
     * Resolves a player head for the given UUID.
     *
     * @param playerUUID player unique id
     * @return a clone of the cached or newly created player head, or {@code null} if a skull item cannot be created
     */
    public @Nullable ItemStack getPlayerHead(@NotNull UUID playerUUID) {
        ItemStack cached = playerHeads.getIfPresent(playerUUID);
        if (cached != null) {
            return cached.clone();
        }

        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        if (skull == null) {
            return null;
        }

        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) {
            return skull;
        }

        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        skull.setItemMeta(meta);

        skull.setData(DataComponentTypes.TOOLTIP_DISPLAY, TooltipDisplay.tooltipDisplay().addHiddenComponents(DataComponentTypes.PROFILE).build());
        playerHeads.put(playerUUID, skull);
        return skull.clone();
    }

    /**
     * Resolves custom heads that were registered before the provider was ready.
     */
    public void flushPendingCustomHeads() {
        if (provider == null || !provider.hasAnyProvider() || pendingCustomHeads.isEmpty()) {
            return;
        }

        for (String headDbId : Set.copyOf(pendingCustomHeads)) {
            ItemStack head = provider.getItemHead(headDbId);
            if (head != null) {
                customHeads.put(headDbId, head);
            }
            pendingCustomHeads.remove(headDbId);
        }
    }

    /**
     * Attaches an Arcaniax HeadDatabase API instance to the active provider when applicable,
     * then flushes pending custom-head registrations.
     *
     * @param api HeadDatabase API instance
     */
    public void setArcaniaxApi(@Nullable HeadDatabaseAPI api) {
        HeadProvider current = this.provider;
        if (current instanceof ArcaniaxHeadProvider arcaniax) {
            arcaniax.setApi(api);
            flushPendingCustomHeads();
        }
    }
}
