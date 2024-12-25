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

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alpsbte.alpslib.utils.item.LoreBuilder.LORE_COMPONENT;

public class ItemBuilder {
    private final ItemStack item;
    protected final ItemMeta itemMeta;

    public ItemBuilder(ItemStack item) {
        itemMeta = item.getItemMeta();
        if (itemMeta != null) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.item = item;
    }

    @Deprecated
    public ItemBuilder(Material material, int amount, byte color) {
        item = new ItemStack(material, amount, color);
        itemMeta = item.getItemMeta();
        if (itemMeta != null) itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public ItemBuilder(Material material) {
        this(material, 1, (byte) 0);
    }

    public ItemBuilder(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    @Deprecated
    public ItemBuilder setName(String name) {
        itemMeta.displayName(LORE_COMPONENT.append(LegacyComponentSerializer.legacySection().deserialize(name)));
        return this;
    }

    public ItemBuilder setName(TextComponent component) {
        itemMeta.displayName(LORE_COMPONENT.append(component));
        return this;
    }

    @Deprecated
    public ItemBuilder setLore(List<String> lore) {
        List<TextComponent> components = new ArrayList<>();
        for (String loreStr : lore)
            components.add(LORE_COMPONENT.append(LegacyComponentSerializer.legacySection().deserialize(loreStr)));
        itemMeta.lore(components);
        return this;
    }

    public ItemBuilder setLore(ArrayList<TextComponent> components) {
        itemMeta.lore(components);
        return this;
    }

    public ItemBuilder setEnchanted(boolean setEnchanted) {
        if(setEnchanted) {
            itemMeta.addEnchant(Enchantment.UNBREAKING,1,true);
            itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        } else {
            itemMeta.removeEnchant(Enchantment.UNBREAKING);
            itemMeta.removeItemFlags(ItemFlag.HIDE_ENCHANTS);
        }
        return this;
    }

    public ItemBuilder setItemModel(int model) {
        if (model != 0) itemMeta.setCustomModelData(model);
        return this;
    }

    public ItemBuilder setItemModel(String model) {
        if (Objects.equals(model, "")) return this;

        CustomModelDataComponent modelDataComp = itemMeta.getCustomModelDataComponent();
        List<String> strings = new ArrayList<>();
        strings.add(model);
        modelDataComp.setStrings(strings);
        itemMeta.setCustomModelDataComponent(modelDataComp);
        return this;
    }

    /**
     * @param model The model to set, must be a int or string else nothing will be changed.
     */
    public ItemBuilder setItemModel(Object model) {
        if (model instanceof Integer modelInt) {
            setItemModel(modelInt);
        } else if (model instanceof String modelString) {
            setItemModel(modelString);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }
}
