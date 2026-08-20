package com.alpsbte.alpslib.geo.nominatim;

import com.alpsbte.alpslib.geo.AdminLevel;
import com.alpsbte.alpslib.geo.GeoLocation;

import java.util.Map;

/**
 * Represents a location with data from Nominatim rgc
 * @param latitude The locations latitude
 * @param longitude The locations longitude
 * @param adminLevelsValues The Nominatim data with {@link AdminLevel#getNominatimKey()} values as keys
 */
public record NominatimGeoLocation(double latitude, double longitude, Map<String, String> adminLevelsValues) implements GeoLocation {

    @Override
    public String get(AdminLevel adminLevel) {
        if (adminLevel.getNominatimKey() == null) {
            return null;
        }

        return this.adminLevelsValues.get(adminLevel.getNominatimKey());
    }

}
