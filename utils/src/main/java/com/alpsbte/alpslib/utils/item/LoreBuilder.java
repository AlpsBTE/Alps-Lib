package com.alpsbte.alpslib.utils.item;

import com.alpsbte.alpslib.utils.AlpsUtils;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.jspecify.annotations.NonNull;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class LoreBuilder {
    public static final Component LORE_COMPONENT = empty().decoration(ITALIC, TextDecoration.State.FALSE);
    public static final int MAX_LORE_LINE_LENGTH = 40;
    private final List<Component> lore = new ArrayList<>();

    public LoreBuilder addLine(Component line) {
        addLineToLore(line);
        return this;
    }

    public LoreBuilder addLines(Component... lines) {
        addLines(false, lines);
        return this;
    }

    public LoreBuilder addLines(boolean createMultiline, Component @NonNull ... lines) {
        for (Component line : lines) addLine(line, createMultiline);
        return this;
    }

    public LoreBuilder addLines(List<Component> lines) {
        addLines(lines, false);
        return this;
    }

    public LoreBuilder addLines(@NonNull List<Component> lines, boolean createMultiline) {
        for (Component line : lines) addLine(line, createMultiline);
        return this;
    }

    public LoreBuilder emptyLine() {
        lore.add(Component.empty());
        return this;
    }

    public List<Component> build() {
        return lore;
    }

    /**
     * @deprecated since 1.5.1, use {@link #addLine(Component)} instead.
     */
    @Deprecated(since = "1.5.1", forRemoval = true)
    public LoreBuilder addLine(String line) {
       addLine(line, false);
       return this;
    }

    /**
     * @deprecated since 1.5.1, use {@link #addLines(Component...)} instead.
     */
    @Deprecated(since = "1.5.1", forRemoval = true)
    public LoreBuilder addLine(String line, boolean createMultiline) {
        if (createMultiline) {
            List<String> lines = AlpsUtils.createMultilineFromString(line, MAX_LORE_LINE_LENGTH, AlpsUtils.LINE_BREAKER);
            for (String l : lines) addLineToLore(l);
        } else addLineToLore(line);
        return this;
    }

    /**
     * @deprecated since 1.5.1, use {@link #addLines(Component...)} instead.
     */
    @Deprecated(since = "1.5.1", forRemoval = true)
    public LoreBuilder addLine(Component line, boolean createMultiline) {
        if (createMultiline && line instanceof TextComponent tc) {
            List<String> lines = AlpsUtils.createMultilineFromString(tc.content(), MAX_LORE_LINE_LENGTH, AlpsUtils.LINE_BREAKER);
            for (String l : lines) addLineToLore(text(l).style(line.style()));
        } else addLineToLore(line);
        return this;
    }

    /**
     * @deprecated since 1.5.1, use {@link #addLines(Component...)} instead.
     */
    @Deprecated(since = "1.5.1", forRemoval = true)
    public LoreBuilder addLines(String... lines) {
        addLines(false, lines);
        return this;
    }

    /**
     * @deprecated since 1.5.1, use {@link #addLines(Component...)} instead.
     */
    @Deprecated(since = "1.5.1", forRemoval = true)
    public LoreBuilder addLines(boolean createMultiline, String @NonNull ... lines) {
        for (String line : lines) addLine(line, createMultiline);
        return this;
    }

    private void addLineToLore(String line) {
        lore.add(LORE_COMPONENT.append(text(line).color(NamedTextColor.GRAY)));
    }

    private void addLineToLore(Component line) {
        lore.add(LORE_COMPONENT.append(line));
    }
}
