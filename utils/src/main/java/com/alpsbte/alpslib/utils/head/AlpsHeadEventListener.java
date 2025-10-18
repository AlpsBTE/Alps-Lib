package com.alpsbte.alpslib.utils.head;

import me.arcaniax.hdb.api.DatabaseLoadEvent;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AlpsHeadEventListener implements Listener {
    @EventHandler
    private void onPlayerJoinEvent(PlayerJoinEvent event) {
        AlpsHeadUtils.registerPlayerHead(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onPlayerQuitEvent(PlayerQuitEvent event) {
        AlpsHeadUtils.unregisterPlayerHead(event.getPlayer().getUniqueId());
    }

    @EventHandler
    private void onDatabaseLoadEvent(DatabaseLoadEvent event) {
        AlpsHeadUtils.setHeadDatabaseAPI(new HeadDatabaseAPI());
    }
}
