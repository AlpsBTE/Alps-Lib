package com.alpsbte.alpslib.geo.rgc;

import com.alpsbte.alpslib.geo.AdminLevel;
import com.alpsbte.alpslib.geo.GeoLocation;

import java.util.Map;

/**
 * Represents a location with data from {@link ReverseGeocoder}
 * @param latitude The locations latitude
 * @param longitude The locations longitude
 * @param adminLevelsValues The rgc data with {@link AdminLevel#getLevel()} values as keys
 */
public record RgcGeoLocation(double latitude, double longitude, Map<Integer, String> adminLevelsValues) implements GeoLocation {

    @Override
    public String get(AdminLevel adminLevel) {
        if (adminLevel.getLevel() == null) {
            return null;
        }

        return this.adminLevelsValues.get(adminLevel.getLevel());
    }

}
