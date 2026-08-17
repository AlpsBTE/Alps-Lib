package com.alpsbte.alpslib.geo.photon;

import com.alpsbte.alpslib.geo.AdminLevel;
import com.alpsbte.alpslib.geo.GeoLocation;

import java.util.Map;

/**
 * Represents a location with data from Photon rgc
 * @param latitude The locations latitude
 * @param longitude The locations longitude
 * @param adminLevelsValues The Photon data with {@link AdminLevel#getPhotonKey()} values as keys
 */
public record PhotonGeoLocation(double latitude, double longitude, Map<String, String> adminLevelsValues) implements GeoLocation {

    @Override
    public String get(AdminLevel adminLevel) {
        if (adminLevel.getPhotonKey() == null) {
            return null;
        }

        return this.adminLevelsValues.get(adminLevel.getPhotonKey());
    }

}
