package com.alpsbte.alpslib.geo;

/**
 * Holds data of a location that can be accessed using {@link AdminLevel}s.
 */
public interface GeoLocation {

    /**
     * Access a locations data value by an {@link AdminLevel}
     * @param adminLevel Defines the data key
     * @return The value for the key. Or null if the key is null or there is no value for the key in the data
     */
    String get(AdminLevel adminLevel);

}
