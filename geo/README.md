This module can be used to get geo information (like country or city) of a location identified by latitude and longitude. This is possible both offline and over https using Nominatim or Photon.

# Retrieving data

## Offline ([ReverseGeocoder](https://github.com/kno10/reversegeocode))

First you need to create a RgcHandler. It utilizes the [ReverseGeocoder](https://github.com/kno10/reversegeocode) class by kno10. You need to provide an OSM data file (find them in [their repo](https://github.com/kno10/reversegeocode/tree/master/data)). The most accurate results can be achieved using files with high resolutions.

Using the RgcHandler's method "locationFromCoordinates" you can get a RgcGeoLocation with all information the ReverseGeocoder found.

Access it by using the "get" Method. You need to provide an AdminLevel (find more information below). This will use the AdminLevels Integer "level" property.

## [Nominatim](https://nominatim.org/release-docs/develop/api/Reverse/)

First you need to create a NominatimHandler. It utilizes the Nominatim reverse API to access OSM data. Because of that you need to provide your applications userAgent for the http requests.

Using the NominatimHandler's method "locationFromCoordinates" you can get a NominatimGeoLocation with all information of the "address" JSON object of the Nominatim response.

Access it by using the "get" Method. You need to provide an AdminLevel (find more information below). This will use the AdminLevels String "nominatimKey" property.

## [Photon](https://github.com/komoot/photon/blob/master/docs/api-v1.md)

First you need to create a PhotonHandler. It utilizes the Photon reverse API to access OSM data.

Using the PhotonHandler's method "locationFromCoordinates" you can get a PhotonGeoLocation with all information of the "properties" JSON object of the Photon response.

Access it by using the "get" Method. You need to provide an AdminLevel (find more information below). This will use the AdminLevels String "photonKey" property.

# AdminLevel

AdminLevels provide a constant way to access the parts of the queried data that you want to use. For example, you might want to define an AdminLevel for States in Germany:

```
public enum DEAdminLevel implements AdminLevel {

    STATE(4);

    private final int level;

    DEAdminLevel(int level) {
        this.level = level;
    }

    @Override
    public Integer getLevel() {
        return this.level;
    }

    @Override
    public String getNominatimKey() {
        return null;
    }
    
    @Override
    public String getPhotonKey() {
        return null;
    }

}
```

For Rgc: An AdminLevels "level" refers to [OSM admin_levels](https://wiki.openstreetmap.org/wiki/Tag:boundary%3Dadministrative). As you can see, their meaning is not the same for every country.

For Nominatim: An AdminLevels "nominatimKey" refers to the keys used for the data in the "address" JSON response of a [Nominatim request](https://nominatim.org/release-docs/develop/api/Reverse/).

For Photon: An AdminLevels "photonKey" refers to the keys used for the data in the "properties" JSON response of a [Photon request](https://github.com/komoot/photon/blob/master/docs/api-v1.md).

The "level", "nominatimKey" and "photonKey" are allowed to be null as some AdminLevels only exist for RgcGeoLocations and others for NominationGeoLocations or PhotonGeoLocations.

The "AdminLevel.COUNTRY" is the only one implemented by default as it is the same for all countries.

# Other people's work used

- OSM (OpenStreetMap) data: © OpenStreetMap contributors
- [ReverseGeocoder](https://github.com/kno10/reversegeocode): Copyright (c) 2015, Erich Schubert