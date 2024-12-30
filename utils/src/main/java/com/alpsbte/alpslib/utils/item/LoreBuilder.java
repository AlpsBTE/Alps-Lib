/*
 * The MIT License (MIT)
 *
 *  Copyright © 2025, Alps BTE <bte.atchli@gmail.com>
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

package com.alpsbte.alpslib.utils.item;

import com.alpsbte.alpslib.utils.AlpsUtils;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;

import java.util.ArrayList;
import java.util.List;

import static net.kyori.adventure.text.Component.empty;
import static net.kyori.adventure.text.Component.text;
import static net.kyori.adventure.text.format.TextDecoration.ITALIC;

public class LoreBuilder {
    public static final TextComponent LORE_COMPONENT = empty().decoration(ITALIC, TextDecoration.State.FALSE);
    public static int MAX_LORE_LINE_LENGTH = 40;
    private final ArrayList<TextComponent> lore = new ArrayList<>();

    public LoreBuilder addLine(String line) {
       addLine(line, false);
       return this;
    }

    public LoreBuilder addLine(String line, boolean createMultiline) {
        if (createMultiline) {
            List<String> lines = AlpsUtils.createMultilineFromString(line, MAX_LORE_LINE_LENGTH, AlpsUtils.LINE_BREAKER);
            for (String l : lines) addLineToLore(l);
        } else addLineToLore(line);
        return this;
    }

    public LoreBuilder addLine(TextComponent line) {
        addLine(line, false);
        return this;
    }

    public LoreBuilder addLine(TextComponent line, boolean createMultiline) {
        if (createMultiline) {
            List<String> lines = AlpsUtils.createMultilineFromString(line.content(), MAX_LORE_LINE_LENGTH, AlpsUtils.LINE_BREAKER);
            for (String l : lines) addLineToLore(text(l).style(line.style()));
        } else addLineToLore(line);
        return this;
    }

    public LoreBuilder addLines(String... lines) {
        addLines(false, lines);
        return this;
    }

    public LoreBuilder addLines(boolean createMultiline, String... lines) {
        for (String line : lines) addLine(line, createMultiline);
        return this;
    }

    public LoreBuilder addLines(TextComponent... lines) {
        addLines(false, lines);
        return this;
    }

    public LoreBuilder addLines(boolean createMultiline, TextComponent... lines) {
        for (TextComponent line : lines) addLine(line, createMultiline);
        return this;
    }

    public LoreBuilder addLines(List<TextComponent> lines) {
        addLines(lines, false);
        return this;
    }

    public LoreBuilder addLines(List<TextComponent> lines, boolean createMultiline) {
        for (TextComponent line : lines) addLine(line, createMultiline);
        return this;
    }

    public LoreBuilder emptyLine() {
        lore.add(text(""));
        return this;
    }

    public ArrayList<TextComponent> build() {
        return lore;
    }

    private void addLineToLore(String line) {
        lore.add(LORE_COMPONENT.append(text(line).color(NamedTextColor.GRAY)));
    }

    private void addLineToLore(TextComponent line) {
        lore.add(LORE_COMPONENT.append(line));
    }
}
