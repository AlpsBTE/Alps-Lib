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

package com.alpsbte.alpslib.http.utils.item;

import org.bukkit.ChatColor;

import java.util.ArrayList;
import java.util.List;

public class LoreBuilder {
    public static int MAX_LINE_LENGTH = 35;
    public static String LINE_BAKER = "%newline%";

    private final List<String> lore = new ArrayList<>();
    private String defaultColor = "§7";

    public LoreBuilder addLine(String line) {
        String[] splitLines = line.split(LINE_BAKER);

        for(String textLine : splitLines) {
            lore.add(defaultColor + textLine.replace(LINE_BAKER, ""));
        }
        return this;
    }

    public LoreBuilder addLines(String... lines) {
        for (String line : lines) {
            addLine(line);
        }
        return this;
    }

    public LoreBuilder addLines(List<String> lines) {
        for (String line : lines) {
            addLine(line);
        }
        return this;
    }

    public LoreBuilder emptyLine() {
        lore.add("");
        return this;
    }

    public LoreBuilder setDefaultColor(ChatColor defaultColor) {
        this.defaultColor = "§" + defaultColor.getChar();
        return this;
    }

    public List<String> build() {
        return lore;
    }
}