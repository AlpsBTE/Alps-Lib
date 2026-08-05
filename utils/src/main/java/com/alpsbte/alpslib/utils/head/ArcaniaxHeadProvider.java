package com.alpsbte.alpslib.utils.head;

import me.arcaniax.hdb.api.HeadDatabaseAPI;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * {@link HeadProvider} backed by Arcaniax HeadDatabase.
 * <p>
 * The API is typically attached after {@code DatabaseLoadEvent}; until then
 * {@link #hasAnyProvider()} is {@code false} and lookups return {@code null}.
 */
public class ArcaniaxHeadProvider implements HeadProvider {
    private volatile HeadDatabaseAPI api;

    /**
     * @param api HeadDatabase API, or {@code null} until the database has loaded
     */
    public ArcaniaxHeadProvider(@Nullable HeadDatabaseAPI api) {
        this.api = api;
    }

    /**
     * Creates a provider with no API attached yet.
     */
    public ArcaniaxHeadProvider() {
        this(null);
    }

    /**
     * Attaches or replaces the HeadDatabase API instance.
     *
     * @param api HeadDatabase API, or {@code null}
     */
    public synchronized void setApi(@Nullable HeadDatabaseAPI api) {
        this.api = api;
    }

    /**
     * @return the attached HeadDatabase API, or {@code null} if not loaded yet
     */
    public @Nullable HeadDatabaseAPI getApi() {
        return api;
    }

    @Override
    public @Nullable ItemStack getItemHead(@NotNull String id) {
        HeadDatabaseAPI current = this.api;
        if (current == null) {
            return null;
        }
        return current.getItemHead(id);
    }

    @Override
    public boolean hasAnyProvider() {
        return api != null;
    }
}
