/*
 * The MIT License (MIT)
 *
 *  Copyright © 2023, Alps BTE <bte.atchli@gmail.com>
 *
 *  Permission is hereby granted, free of charge, to any person obtaining a copy
 *  of this software and associated documentation files (the "Software"), to deal
 *  in the Software without restriction, including without limitation the rights
 *  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 *  copies of the Software, and to permit persons to whom the Software is
 *  furnished to do so, subject to the following conditions:
 *
 *  The above copyright notice and this permission notice shall be included in all
 *  copies or substantial portions of the Software.
 *
 *  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 *  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 *  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 *  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 *  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 *  SOFTWARE.
 */

package com.alpsbte.alpslib.hologram;

import eu.decentsoftware.holograms.api.DHAPI;
import eu.decentsoftware.holograms.api.holograms.Hologram;
import eu.decentsoftware.holograms.api.holograms.HologramLine;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@SuppressWarnings("unused")
public abstract class HolographicDisplay implements HolographicContent {
    public static final List<HolographicDisplay> activeDisplays = new ArrayList<>();

    public static void registerPlugin(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(new HolographicEventListener(), plugin);
    }

    public static final String EMPTY_TAG = "{empty}";

    private final String id;
    private Location position;
    private final boolean isPlaceholdersEnabled;

    protected final HashMap<UUID, String> holograms = new HashMap<>();

    protected HolographicDisplay(@NotNull String id, Location position, boolean enablePlaceholders) {
        this.id = id;
        this.position = position;
        this.isPlaceholdersEnabled = enablePlaceholders;
        activeDisplays.add(this);
    }

    public void create(Player player) {
        if (!hasViewPermission(player.getUniqueId())) return;
        if (holograms.containsKey(player.getUniqueId())) {
            reload(player.getUniqueId());
            return;
        }

        DHAPI.createHologram(id, position);
        Hologram hologram = DHAPI.getHologram(id);
        assert hologram != null;
        hologram.setDefaultVisibleState(false);
        hologram.setShowPlayer(player);
        holograms.put(player.getUniqueId(), id);
        reload(player.getUniqueId());
    }

    public abstract boolean hasViewPermission(UUID playerUUID);

    public boolean isVisible(UUID playerUUID) {
        return holograms.containsKey(playerUUID);
    }

    @Override
    public List<DataLine<?>> getHeader(UUID playerUUID) {
        List<DataLine<?>> header = new ArrayList<>();

        ItemStack item = getItem();
        if (item != null) header.add(new ItemLine(item));
        if (getTitle(playerUUID) != null) header.add(new TextLine(getTitle(playerUUID)));
        return header;
    }

    public void reload(UUID playerUUID) {
        if (!holograms.containsKey(playerUUID)) return;
        List<DataLine<?>> dataLines = new ArrayList<>();

        List<DataLine<?>> header = getHeader(playerUUID);
        if (header != null) dataLines.addAll(header);

        List<DataLine<?>> content = getContent(playerUUID);
        if (content != null) dataLines.addAll(content);

        updateDataLines(holograms.get(playerUUID), 0, dataLines);
    }

    public void reloadAll() {
        for (UUID playerUUID : holograms.keySet()) reload(playerUUID);
    }

    public void remove(UUID playerUUID) {
        if (holograms.containsKey(playerUUID)) Objects.requireNonNull(DHAPI.getHologram(holograms.get(playerUUID))).delete();
        holograms.remove(playerUUID);
    }

    public void removeAll() {
        List<UUID> playerUUIDs = new ArrayList<>(holograms.keySet());
        for (UUID playerUUID : playerUUIDs) remove(playerUUID);
    }

    public void delete() {
        removeAll();
        holograms.clear();
        activeDisplays.remove(this);
    }

    public String getId() {
        return id;
    }

    public Location getPosition() {
        return position;
    }

    public void setPosition(Location newPosition) {
        this.position = newPosition;
        for (Map.Entry<UUID, String> hologram : holograms.entrySet()) Objects.requireNonNull(DHAPI.getHologram(hologram.getValue())).setLocation(newPosition);
    }

    public boolean isPlaceholdersEnabled() {
        return isPlaceholdersEnabled;
    }

    public String getHologramName(UUID playerUUID) {
        return holograms.get(playerUUID);
    }

    public HashMap<UUID, String> getHolograms() {
        return holograms;
    }

    public interface DataLine<T> {
        T getLine();
    }

    protected static void updateDataLines(String hologramName, int startIndex, List<DataLine<?>> dataLines) {
        int index = startIndex;
        List<HologramLine> lines = DHAPI.getHologram(hologramName).getPage(0).getLines();
        if (index == 0 && lines.size() > dataLines.size()) {
            int removeCount = lines.size() - dataLines.size();
            for (int i = 0; i < removeCount; i++) {
                int lineIndex = lines.size() - 1;
                if (lineIndex >= 0) lines.remove(lineIndex);
            }
        }

        for (DataLine<?> data : dataLines) {
            replaceLine(hologramName, index, data);
            index++;
        }
    }

    protected static void replaceLine(String hologramName, int line, DataLine<?> data) {
        Hologram hologram = DHAPI.getHologram(hologramName);
        List<HologramLine> lines = hologram.getPage(0).getLines();
        
        boolean isText = data instanceof TextLine;
        boolean isItem = data instanceof ItemLine;
        

        if (lines.size() < line + 1) {
            if (isText) {
                DHAPI.insertHologramLine(hologram, line, ((TextLine) data).getLine());
            } else if (isItem) {
                DHAPI.insertHologramLine(hologram, line, ((ItemLine) data).getLine());
            }
        } else {
            if (isText) {
                DHAPI.setHologramLine(hologram, line, ((TextLine) data).getLine());
            } else if (isItem) {
                DHAPI.setHologramLine(hologram, line, ((ItemLine) data).getLine());
            }
        }
    }

    public static class TextLine implements DataLine<String> {
        private final String line;
        public TextLine(String line) {
            this.line = line;
        }
        @Override
        public String getLine() {
            return line;
        }
    }

    public static class ItemLine implements DataLine<ItemStack> {
        private final ItemStack line;
        public ItemLine(ItemStack line) {
            this.line = line;
        }
        @Override
        public ItemStack getLine() {
            return line;
        }
    }

    public static HolographicDisplay getById(String id) {
        return activeDisplays.stream().filter(holo -> holo.getId().equals(id)).findFirst().orElse(null);
    }
}
