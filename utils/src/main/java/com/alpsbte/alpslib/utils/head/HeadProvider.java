package com.alpsbte.alpslib.utils.head;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Abstraction over optional head-database integrations used by {@link HeadLookupService}.
 */
public interface HeadProvider {
    /**
     * Resolves a head item for the given provider-specific id.
     *
     * @param id head id
     * @return the head item, or {@code null} if it cannot be resolved
     */
    @Nullable ItemStack getItemHead(@NotNull String id);

    /**
     * @return {@code true} if this provider currently has a usable data source
     */
    default boolean hasAnyProvider() {
        return true;
    }
}
