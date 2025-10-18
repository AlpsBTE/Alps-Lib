package com.alpsbte.alpslib.utils.item;

import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.*;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.components.CustomModelDataComponent;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.alpsbte.alpslib.utils.item.LoreBuilder.LORE_COMPONENT;

public class ItemBuilder {
    private final ItemStack item;
    protected final ItemMeta itemMeta;

    public ItemBuilder(@NotNull ItemStack item) {
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
    public ItemBuilder setLore(@NotNull List<String> lore) {
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

        if (model instanceof Integer) {
            setItemModel((int)model);
        } else if (model instanceof String) {
            setItemModel((String)model);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }
}
