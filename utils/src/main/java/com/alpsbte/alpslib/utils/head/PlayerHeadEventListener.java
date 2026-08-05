package com.alpsbte.alpslib.utils.head;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

/**
 * Caches player heads on join and invalidates them on quit.
 */
public class PlayerHeadEventListener implements Listener {
    private final HeadLookupService headLookupService;

    /**
     * @param headLookupService lookup service that owns the player-head cache
     */
    public PlayerHeadEventListener(@NotNull HeadLookupService headLookupService) {
        this.headLookupService = Objects.requireNonNull(headLookupService, "headLookupService");
    }

    @EventHandler
    private void onPlayerJoinEvent(@NotNull PlayerJoinEvent event) {
        headLookupService.registerPlayerHead(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerQuitEvent(@NotNull PlayerQuitEvent event) {
        headLookupService.unregisterPlayerHead(event.getPlayer().getUniqueId());
    }
}
