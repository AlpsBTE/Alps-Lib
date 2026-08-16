package com.alpsbte.alpslib.utils;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Root configuration container for map link providers.
 * <p>
 * Entries are keyed by provider ID and stored in insertion order.
 */
public class MapLinksConfig {
    private Map<String, MapLinkEntryConfig> links = new LinkedHashMap<>();

    /**
     * No-arg constructor for object mappers.
     */
    public MapLinksConfig() {
    }

    /**
     * Creates a config container from existing entries.
     *
     * @param links provider entries keyed by provider ID
     */
    public MapLinksConfig(Map<String, MapLinkEntryConfig> links) {
        if (links != null) {
            this.links = new LinkedHashMap<>(links);
        }
    }

    /**
     * @return provider entries keyed by ID
     */
    public Map<String, MapLinkEntryConfig> getLinks() {
        return links;
    }

    /**
     * Replaces current entries.
     *
     * @param links provider entries keyed by ID; {@code null} clears to an empty map
     */
    public void setLinks(Map<String, MapLinkEntryConfig> links) {
        this.links = links == null ? new LinkedHashMap<>() : new LinkedHashMap<>(links);
    }
}


