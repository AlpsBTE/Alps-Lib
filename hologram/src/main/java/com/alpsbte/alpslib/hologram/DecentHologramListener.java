package com.alpsbte.alpslib.hologram;

import eu.decentsoftware.holograms.event.HologramClickEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.Objects;

/**
 * Listener class for DecentHologram.<br/>
 * This class listened for:<br/>
 * [1] Create displays everytime a player joined.<br/>
 * [2] Delete displays when a player quit.<br/>
 * [3] Re-create and delete displays when a player changes world.<br/>
 * [4] HologramClickEvent callback to any registered hologram.<br/>
 *
 * @see PlayerJoinEvent
 * @see PlayerQuitEvent
 * @see PlayerChangedWorldEvent
 * @see HologramClickEvent
 */
public class DecentHologramListener implements Listener {
    public DecentHologramListener() {}

    @EventHandler
    public void onPlayerJoinEvent(PlayerJoinEvent event) {
        for (DecentHologramDisplay display : DecentHologramDisplay.activeDisplays) {
            if (display.getLocation() == null) return;
            if (Objects.requireNonNull(display.getLocation().getWorld()).getName().equals(event.getPlayer().getWorld().getName()))
                display.create(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerQuitEvent(PlayerQuitEvent event) {
        for (DecentHologramDisplay display : DecentHologramDisplay.activeDisplays) {
            if (display.getHolograms().containsKey(event.getPlayer().getUniqueId())) display.remove(event.getPlayer().getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent event) {
        for (DecentHologramDisplay display : DecentHologramDisplay.activeDisplays) {
            if (display.getLocation() == null) return;
            if (Objects.requireNonNull(display.getLocation().getWorld()).getName().equals(event.getFrom().getName())) display.remove(event.getPlayer().getUniqueId());
            else if (display.getLocation().getWorld().getName().equals(event.getPlayer().getWorld().getName())) display.create(event.getPlayer());
        }

    }

    @EventHandler
    public void onHologramClick(HologramClickEvent event) {
        for (DecentHologramDisplay display : DecentHologramDisplay.activeDisplays) {
            if (display.getLocation() == null
                    | display.getClickListener() == null
                    | display.getHologram(event.getPlayer().getUniqueId()) == null) continue;
            if (display.getHologram(event.getPlayer().getUniqueId()).equals(event.getHologram()))
                display.getClickListener().onClick(event);
        }
    }
}