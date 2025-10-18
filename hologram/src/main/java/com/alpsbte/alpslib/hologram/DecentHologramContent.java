package com.alpsbte.alpslib.hologram;

import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

/**
 * Display contents the hologram will be using.<br/>
 * [1] ItemStack - Display item at the top of hologram.<br/>
 * [2] Title - Header message atop the hologram.<br/>
 * [3] Content - the main content to display.<br/>
 * [4] Footer - footer line, this is default as a separator line.
 */
public interface DecentHologramContent {
    /**
     * Display entity as minecraft item.
     * This will be display at the top of hologram
     * @return Minecraft ItemStack
     */
    ItemStack getItem();

    /**
     * Title message as String.
     * @param playerUUID Focused player.
     * @return The message.
     */
    String getTitle(UUID playerUUID);

    /**
     * Header message as DataLine.
     * By default, this is using the value from getTitle as a header with a separator line
     * @param playerUUID Focused player.
     * @return The DataLine.
     */
    List<DecentHologramDisplay.DataLine<?>> getHeader(UUID playerUUID);

    /**
     * Main content to be written in the hologram
     * @param playerUUID Focused player.
     * @return The DataLine.
     */
    List<DecentHologramDisplay.DataLine<?>> getContent(UUID playerUUID);

    /**
     * The footer line at the bottom of the hologram.
     * By default, this is a separator line.
     * @param playerUUID Focused player.
     * @return The DataLine.
     */
    List<DecentHologramDisplay.DataLine<?>> getFooter(UUID playerUUID);
}