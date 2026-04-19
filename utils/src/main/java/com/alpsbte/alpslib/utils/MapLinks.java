package com.alpsbte.alpslib.utils;

public class MapLinks {
    private final double latitude;
    private final double longitude;

    public MapLinks(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public MapLink getOSM() {
        return new MapLink("Open Street Map", "https://www.openstreetmap.org/#map=19/" + latitude + "/" + longitude);
    }

    public MapLink getGoogleMaps() {
        return new MapLink("Google Maps", "https://www.google.com/maps/place/" + latitude + "," + longitude);
    }

    public MapLink getGoogleEarth() {
        return new MapLink("Google Earth Web", "https://earth.google.com/web/search/" + latitude + "," + longitude);
    }

    public MapLink getAppleLookAround() {
        return new MapLink("Apple Maps Look Around", "https://lookmap.eu.pythonanywhere.com/#c=20/" + latitude + "/" + longitude);
    }
}
