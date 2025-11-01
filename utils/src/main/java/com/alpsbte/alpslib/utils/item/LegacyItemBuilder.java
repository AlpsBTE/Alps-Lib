 package com.alpsbte.alpslib.utils.item;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

@SuppressWarnings("unused")
@Deprecated
public class LegacyItemBuilder {

    private final ItemStack item;
    private final ItemMeta itemMeta;

    public LegacyItemBuilder(ItemStack item) {
        itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        this.item = item;
    }

    public LegacyItemBuilder(Material material, int amount, byte color) {
        item = new ItemStack(material, amount, color);
        itemMeta = item.getItemMeta();
        itemMeta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
    }

    public LegacyItemBuilder(Material material) {
        this(material, 1, (byte) 0);
    }

    public LegacyItemBuilder(Material material, int amount) {
        this(material, amount, (byte) 0);
    }

    public LegacyItemBuilder setName(String name) {
        itemMeta.setDisplayName(name);
        return this;
    }

    public LegacyItemBuilder setLore(List<String> lore) {
        itemMeta.setLore(lore);
        return this;
    }

    public LegacyItemBuilder setEnchantment(boolean setEnchanted) {
        if(setEnchanted) {
            itemMeta.addEnchant(Enchantment.UNBREAKING,1,true);
        } else {
            itemMeta.removeEnchant(Enchantment.UNBREAKING);
        }
        return this;
    }

    public ItemStack build() {
        item.setItemMeta(itemMeta);
        return item;
    }

}
