package com.alpsbte.alpslib.utils.head;

import com.alpsbte.alpslib.utils.AlpsUtils;
import com.cryptomorin.xseries.XMaterial;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SuppressWarnings("unused")
public class AlpsHeadUtils {
    private static HeadDatabaseAPI headDatabaseAPI;

    private static final Cache<String, ItemStack> customHeads = CacheBuilder.newBuilder().build();
    private static final List<String> unregisteredCustomHeads = new ArrayList<>();

    private static final Cache<UUID, ItemStack> playerHeads = CacheBuilder.newBuilder().build();

    public static void registerCustomHead(String headDbId) {
        if (headDatabaseAPI == null) {
            unregisteredCustomHeads.add(headDbId);
        } else customHeads.put(headDbId, getCustomHead(headDbId));
    }

    public static void registerCustomHeads(List<String> headDbIds) {
        headDbIds.forEach(AlpsHeadUtils::registerCustomHead);
    }

    public static void unregisterCustomHead(String headDbId) {
        customHeads.invalidate(headDbId);
    }

    public static void unregisterCustomHeads() {
        customHeads.invalidateAll();
    }

    public static void registerPlayerHead(UUID playerUUID) {
        playerHeads.put(playerUUID, getPlayerHead(playerUUID));
    }

    public static void unregisterPlayerHead(UUID playerUUID) {
        playerHeads.invalidate(playerUUID);
    }

    public static ItemStack getCustomHead(String headDbId) {
        ItemStack customHead = customHeads.getIfPresent(headDbId);
        if (customHead != null) return customHead;
        if (headDatabaseAPI == null || AlpsUtils.tryParseInt(headDbId) == null || !headDatabaseAPI.isHead(headDbId))
            return XMaterial.SKELETON_SKULL.parseItem();
        return headDatabaseAPI.getItemHead(headDbId);
    }

    public static ItemStack getPlayerHead(UUID playerUUID) {
        ItemStack playerHead = customHeads.getIfPresent(playerUUID.toString());
        if (playerHead != null) return playerHead;
        ItemStack skull = XMaterial.PLAYER_HEAD.parseItem();
        if (skull == null) return null;
        SkullMeta meta = (SkullMeta) skull.getItemMeta();
        if (meta == null) return skull;
        meta.setOwningPlayer(Bukkit.getOfflinePlayer(playerUUID));
        skull.setItemMeta(meta);
        return skull;
    }

    public static void setHeadDatabaseAPI(HeadDatabaseAPI headDatabaseAPI) {
        AlpsHeadUtils.headDatabaseAPI = headDatabaseAPI;
        if (headDatabaseAPI == null) return;
        unregisteredCustomHeads.forEach(id -> customHeads.put(id, getCustomHead(id)));
        unregisteredCustomHeads.clear();
    }

    public static HeadDatabaseAPI getHeadDatabaseAPI() {
        return headDatabaseAPI;
    }

    public static Cache<String, ItemStack> getCustomHeads() {
        return customHeads;
    }

    public static Cache<UUID, ItemStack> getPlayerHeads() {
        return playerHeads;
    }
}
