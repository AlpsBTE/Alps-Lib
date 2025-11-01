package com.alpsbte.alpslib.utils.item;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class LegacyLoreBuilder {
    public static String LINE_BAKER = "%newline%";

    protected final List<String> lore = new ArrayList<>();
    private String defaultColor = "§7";

    public LegacyLoreBuilder addLine(String line) {
        String[] splitLines = line.split(LINE_BAKER);

        for(String textLine : splitLines) {
            lore.add(defaultColor + textLine.replace(LINE_BAKER, ""));
        }
        return this;
    }

    public LegacyLoreBuilder addLines(String... lines) {
        for (String line : lines) {
            addLine(line);
        }
        return this;
    }

    public LegacyLoreBuilder addLines(List<String> lines) {
        for (String line : lines) {
            addLine(line);
        }
        return this;
    }

    public LegacyLoreBuilder emptyLine() {
        lore.add("");
        return this;
    }

    public LegacyLoreBuilder setDefaultColor(ChatColor defaultColor) {
        this.defaultColor = "§" + defaultColor.getChar();
        return this;
    }

    public List<String> build() {
        return lore;
    }
}
