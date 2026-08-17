package com.alpsbte.alpslib.geo.photon;


import com.alpsbte.alpslib.geo.GeoHandler;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * Provides access to retrieving location data from Photon (OSM).
 * @see <a href="https://github.com/komoot/photon/blob/master/docs/api-v1.md">Photon documentation</a>
 */
public class PhotonHandler implements GeoHandler<PhotonGeoLocation> {

    private final Logger logger;
    private final String language;
    private final String serverUrl;

    /**
     * Create a PhotonHandler object with the default serverUrl "https://photon.komoot.io"
     * @param logger The logger to use
     * @param language The expected language of the returned data (e.g. en, de, ...)
     */
    public PhotonHandler(Logger logger, String language) {
        this.logger = logger;
        this.language = language;
        this.serverUrl = "https://photon.komoot.io";
    }

    /**
     * Create a PhotonHandler object
     * @param logger The logger to use
     * @param language The expected language of the returned data (e.g. en, de, ...)
     * @param serverUrl The serverUrl of the Photon instance you want to use
     */
    public PhotonHandler(Logger logger, String language, String serverUrl) {
        this.logger = logger;
        this.language = language;
        this.serverUrl = serverUrl;
    }

    @Override
    public CompletableFuture<PhotonGeoLocation> locationFromCoordinates(float latitude, float longitude) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                JSONObject response;

                URL url = URI.create(this.serverUrl + "/reverse?lat=" + latitude + "&lon=" + longitude + "&limit=1&lang=" + this.language).toURL();
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestProperty("Accept", "application/json");

                BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8));
                StringBuilder builder = new StringBuilder();
                reader.lines().forEach(builder::append);
                response = new JSONObject(builder.toString());
                con.disconnect();

                if (!response.has("features")) {
                    this.logger.warn("Invalid photon response: %s".formatted(response.toString()));
                    return null;
                }

                JSONObject properties = response
                        .getJSONArray("features")
                        .getJSONObject(0)
                        .getJSONObject("properties");

                Map<String, String> locValues = new HashMap<>();
                for (String key : properties.keySet()) {
                    if (key.startsWith("osm_")) {
                        continue;
                    }
                    locValues.put(key, properties.getString(key));
                }

                return new PhotonGeoLocation(latitude, longitude, locValues);

            } catch (IOException e) {
                this.logger.error("Error photon", e);
                return null;
            }
        });
    }

}
