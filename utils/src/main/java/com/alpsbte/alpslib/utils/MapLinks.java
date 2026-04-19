package com.alpsbte.alpslib.utils;

import org.jetbrains.annotations.Contract;
import org.jspecify.annotations.NonNull;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * Resolves map provider links for a fixed latitude/longitude pair.
 * <p>
 * Instances are immutable and iterable. Provider entries are resolved from built-in defaults and
 * optionally merged with user-defined {@link MapLinksConfig} entries.
 */
public class MapLinks implements Iterable<MapLink> {
    private final double latitude;
    private final double longitude;
    private final Map<String, MapLink> linksById;
    private final List<MapLink> links;

    /** Stable provider ID for Open Street Map. */
    public static final String OSM = "osm";
    /** Stable provider ID for Google Maps. */
    public static final String GOOGLE_MAPS = "googleMaps";
    /** Stable provider ID for Google Earth Web. */
    public static final String GOOGLE_EARTH = "googleEarth";
    /** Stable provider ID for Apple Maps Look Around. */
    public static final String APPLE_LOOK_AROUND = "appleLookAround";

    private static final Map<String, MapLinkEntryConfig> DEFAULT_ENTRIES = Collections.unmodifiableMap(createDefaultEntries());

    /**
     * Creates map links using built-in providers only.
     *
     * @param latitude coordinate latitude
     * @param longitude coordinate longitude
     */
    public MapLinks(double latitude, double longitude) {
        this(latitude, longitude, null);
    }

    /**
     * Creates map links by merging built-in providers with optional user configuration.
     * <p>
     * User entries override defaults by provider ID and can disable entries via
     * {@link MapLinkEntryConfig#getEnabled()}.
     *
     * @param latitude coordinate latitude
     * @param longitude coordinate longitude
     * @param config optional link configuration, may be {@code null}
     */
    public MapLinks(double latitude, double longitude, MapLinksConfig config) {
        this.latitude = latitude;
        this.longitude = longitude;

        int userEntryCount = config != null && config.getLinks() != null ? config.getLinks().size() : 0;
        Map<String, MapLinkEntryConfig> mergedConfig = new LinkedHashMap<>(mapCapacity(DEFAULT_ENTRIES.size() + userEntryCount));
        mergedConfig.putAll(DEFAULT_ENTRIES);
        if (config != null && config.getLinks() != null) {
            for (Map.Entry<String, MapLinkEntryConfig> entry : config.getLinks().entrySet()) {
                MapLinkEntryConfig userConfig = entry.getValue();
                if (userConfig == null) {
                    continue;
                }

                String id = hasText(userConfig.getId()) ? userConfig.getId() : entry.getKey();
                if (!hasText(id)) {
                    continue;
                }

                MapLinkEntryConfig existing = mergedConfig.get(id);
                mergedConfig.put(id, mergeEntry(id, existing, userConfig));
            }
        }

        this.linksById = Collections.unmodifiableMap(resolveLinks(mergedConfig, latitude, longitude));
        this.links = List.copyOf(this.linksById.values());
    }

    /**
     * Factory equivalent to {@link #MapLinks(double, double, MapLinksConfig)}.
     *
     * @param latitude coordinate latitude
     * @param longitude coordinate longitude
     * @param config optional link configuration, may be {@code null}
     * @return resolved map links instance
     */
    @Contract("_, _, _ -> new")
    public static @NonNull MapLinks fromConfig(double latitude, double longitude, MapLinksConfig config) {
        return new MapLinks(latitude, longitude, config);
    }

    /**
     * Returns a mutable copy of default provider configuration.
     *
     * @return configuration containing all built-in providers enabled
     */
    @Contract(" -> new")
    public static @NonNull MapLinksConfig defaultConfig() {
        return new MapLinksConfig(DEFAULT_ENTRIES);
    }

    /**
     * @return latitude used for link resolution
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return longitude used for link resolution
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Returns a resolved link by provider ID.
     *
     * @param id provider identifier
     * @return resolved link or {@code null} if missing/disabled
     */
    public MapLink getById(String id) {
        return linksById.get(id);
    }

    /**
     * Returns all resolved links keyed by provider ID.
     *
     * @return unmodifiable map preserving insertion order
     */
    public Map<String, MapLink> getLinksById() {
        return linksById;
    }

    /**
     * Returns all resolved links in insertion order.
     *
     * @return unmodifiable list of links
     */
    public List<MapLink> getAllLinks() {
        return links;
    }

    /**
     * Creates a sequential stream over resolved links.
     *
     * @return stream of resolved links
     */
    public Stream<MapLink> stream() {
        return links.stream();
    }

    /**
     * Returns an iterator over resolved links in insertion order.
     *
     * @return iterator for resolved links
     */
    @Override
    public Iterator<MapLink> iterator() {
        return links.iterator();
    }

    private static @NonNull Map<String, MapLinkEntryConfig> createDefaultEntries() {
        Map<String, MapLinkEntryConfig> defaults = new LinkedHashMap<>();
        defaults.put(OSM, new MapLinkEntryConfig(
                OSM,
                "Open Street Map",
                "https://www.openstreetmap.org/#map=19/{lat}/{lon}",
                true
        ));
        defaults.put(GOOGLE_MAPS, new MapLinkEntryConfig(
                GOOGLE_MAPS,
                "Google Maps",
                "https://www.google.com/maps/place/{lat},{lon}",
                true
        ));
        defaults.put(GOOGLE_EARTH, new MapLinkEntryConfig(
                GOOGLE_EARTH,
                "Google Earth Web",
                "https://earth.google.com/web/search/{lat},{lon}",
                true
        ));
        defaults.put(APPLE_LOOK_AROUND, new MapLinkEntryConfig(
                APPLE_LOOK_AROUND,
                "Apple Maps Look Around",
                "https://lookmap.eu.pythonanywhere.com/#c=20/{lat}/{lon}",
                true
        ));
        return defaults;
    }

    @Contract("_, null, _ -> new")
    private static @NonNull MapLinkEntryConfig mergeEntry(String id, MapLinkEntryConfig existing, @NonNull MapLinkEntryConfig incoming) {
        String name = incoming.getName();
        String template = incoming.getUrlTemplate();
        Boolean enabled = incoming.getEnabled();

        if (existing != null) {
            if (!hasText(name)) {
                name = existing.getName();
            }
            if (!hasText(template)) {
                template = existing.getUrlTemplate();
            }
            if (enabled == null) {
                enabled = existing.getEnabled();
            }
        }

        return new MapLinkEntryConfig(id, name, template, enabled);
    }

    private static @NonNull Map<String, MapLink> resolveLinks(@NonNull Map<String, MapLinkEntryConfig> entries, double latitude, double longitude) {
        Map<String, MapLink> result = new LinkedHashMap<>(mapCapacity(entries.size()));
        String latitudeValue = Double.toString(latitude);
        String longitudeValue = Double.toString(longitude);

        for (Map.Entry<String, MapLinkEntryConfig> entry : entries.entrySet()) {
            MapLinkEntryConfig config = entry.getValue();
            if (config == null || Boolean.FALSE.equals(config.getEnabled())) {
                continue;
            }

            if (!hasText(config.getName()) || !hasText(config.getUrlTemplate())) {
                continue;
            }

            String url = resolveTemplate(config.getUrlTemplate(), latitudeValue, longitudeValue);
            result.put(entry.getKey(), new MapLink(config.getName(), url));
        }

        return result;
    }

    private static @NonNull String resolveTemplate(String template, String latitude, String longitude) {
        Objects.requireNonNull(template, "template");
        if (template.indexOf('{') < 0) {
            return template;
        }
        return template
                .replace("{lat}", latitude)
                .replace("{lon}", longitude);
    }

    private static int mapCapacity(int size) {
        return Math.max(16, (int) (size / 0.75f) + 1);
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
