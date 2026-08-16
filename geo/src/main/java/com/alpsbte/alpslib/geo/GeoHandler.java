package com.alpsbte.alpslib.geo;

import java.util.concurrent.CompletableFuture;

public interface GeoHandler<T> {

    /**
     * Get location data for coordinates
     * @param latitude The locations latitude
     * @param longitude The locations longitude
     * @return A CompletableFuture with the location data or null if there were errors
     */
    CompletableFuture<T> locationFromCoordinates(float latitude, float longitude);

    /**
     * @see #locationFromCoordinates(float, float)
     */
    default CompletableFuture<T> locationFromCoordinates(double latitude, double longitude) {
        return this.locationFromCoordinates((float) latitude, (float) longitude);
    }

}
