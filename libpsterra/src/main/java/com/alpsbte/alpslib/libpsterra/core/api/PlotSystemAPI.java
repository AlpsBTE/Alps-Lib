package com.alpsbte.alpslib.libpsterra.core.api;

import com.alpsbte.alpslib.libpsterra.core.plotsystem.*;
import com.alpsbte.alpslib.libpsterra.utils.API;
import com.google.common.reflect.TypeToken;
import com.google.gson.*;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.bukkit.util.Vector;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlotSystemAPI {
    private class BooleanDeserializer implements JsonDeserializer<Boolean> {
        @Override
        public Boolean deserialize(JsonElement json, java.lang.reflect.Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            // Customize the deserialization logic based on your needs
            int intValue = json.getAsInt();
            return intValue != 0;
        }
    }

    private String host;
    private int port;


    private static final String GET_PS_BUILDERS_URL = "/api/plotsystem/builders";
    private static final String GET_PS_DIFFICULTIES_URL = "/api/plotsystem/difficulties";
    private static final String GET_PS_CITIES_URL = "/api/plotsystem/teams/%API_KEY%/cities";
    private static final String GET_PS_COUNTRIES_URL = "/api/plotsystem/teams/%API_KEY%/countries";
    private static final String GET_PS_SERVERS_URL = "/api/plotsystem/teams/%API_KEY%/servers";
    private static final String PS_PlOTS_URL = "/api/plotsystem/teams/%API_KEY%/plots";//for GET, PUT, CREATE and POST
    private static final String GET_PS_FTP_URL = "/api/plotsystem/teams/%API_KEY%/ftp";


    public PlotSystemAPI(String host, int port){
        this.host = host;
        this.port = port;
    }


    // A function that returns the content of a GET Request from a given URL
    private String httpGET(String endpoint) throws Exception{
        String apiUrl = host + endpoint;

        return API.get(apiUrl);
    }

    private String httpPUT(String endpoint, String jsonBodyString) throws Exception{
        String apiUrl = host + endpoint;

        RequestBody requestBody = RequestBody.create(jsonBodyString, MediaType.parse("application/json"));
        return API.put(apiUrl, requestBody);
    }

    private String httpPOST(String endpoint, String jsonBodyString) throws Exception{
        String apiUrl = host + endpoint;

        RequestBody requestBody = RequestBody.create(jsonBodyString, MediaType.parse("application/json"));
        return API.post(apiUrl, requestBody);
    }

    private void httpPUTAsync(String endpoint, String jsonBodyString, API.ApiResponseCallback callback) throws Exception{
        String apiUrl = host + endpoint;

        RequestBody requestBody = RequestBody.create(jsonBodyString, MediaType.parse("application/json"));
        API.putAsync(apiUrl, requestBody, callback);
    }
    private void httpPOSTAsync(String endpoint, String jsonBodyString, API.ApiResponseCallback callback) throws Exception{
        String apiUrl = host + endpoint;

        RequestBody requestBody = RequestBody.create(jsonBodyString, MediaType.parse("application/json"));
        API.postAsync(apiUrl, requestBody, callback);
    }
    private void httpDELETEAsync(String endpoint, String jsonBodyString, API.ApiResponseCallback callback) throws Exception{
        String apiUrl = host + endpoint;

        RequestBody requestBody = RequestBody.create(jsonBodyString, MediaType.parse("application/json"));
        API.deleteAsync(apiUrl, requestBody, callback);
    }


    public int getPSBuilderCount() throws Exception{
        String jsonResponse = httpGET(GET_PS_BUILDERS_URL);

        //response looks like this: array with single object with "builders:X"
        /*
                [
                    {
                        "builders": "17"
                    }
                ]
         */
        try {
            JsonArray responseArray = new JsonParser().parse(jsonResponse).getAsJsonArray();
            JsonObject firstObject = responseArray.get(0).getAsJsonObject();
            int builderCount = firstObject.get("builders").getAsInt();

            return builderCount;
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or throw an exception based on your requirements
        return -1;
    }

    public List<Difficulty> getPSDifficulties() throws Exception{
        List<Difficulty> difficulties = new ArrayList<>();
        String jsonResponse = httpGET(GET_PS_DIFFICULTIES_URL);

        //response looks like this: array with difficulty objects
        /*
[
    {
        "id": 1,
        "multiplier": 1,
        "name": "EASY",
        "score_requirment": 0
    },
    ...
]
         */
        try {
            JsonArray responseArray = new JsonParser().parse(jsonResponse).getAsJsonArray();
            for (JsonElement element : responseArray){
                JsonObject o = element.getAsJsonObject();
                difficulties.add(new Difficulty(o.get("name").getAsString(),
                                     o.get("id").getAsInt(), o.get("multiplier").getAsFloat(), o.get("score_requirment").getAsInt()));
                //api actually returns difficulty with a typo "requirment"
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or throw an exception based on your requirements
        return difficulties;
    }

    public List<CityProject> getPSTeamCities(String teamApiKey) throws Exception{
        List<CityProject> cities = new ArrayList<>();
        String jsonResponse = httpGET(GET_PS_CITIES_URL.replace("%API_KEY%", teamApiKey));
        //List<Country> allCountries = getPSTeamCountries(teamApiKey);
        //response looks like this: array with city objects
        /*
            [
                {
                    "country_id": 1,
                    "description": "Test-City in the beautiful Test-Country",
                    "id": 1,
                    "name": "Test-City",
                    "visible": 1
                },
            ]
         */
        try {
            JsonArray responseArray = new JsonParser().parse(jsonResponse).getAsJsonArray();
            for (JsonElement element : responseArray){
                JsonObject o = element.getAsJsonObject();
                int countryID = o.get("country_id").getAsInt();

                //Country country = findCountryWithID(countryID, allCountries);
                CityProject city = new CityProject(
                    o.get("id").getAsInt(), countryID, o.get("name").getAsString()/* country.head_id*/);
                cities.add(city);    
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or throw an exception based on your requirements
        return cities;
    }

    public List<Country> getPSTeamCountries(String teamApiKey) throws Exception{
        List<Country> countries = new ArrayList<>();
        String jsonResponse = httpGET(GET_PS_COUNTRIES_URL.replace("%API_KEY%", teamApiKey));

        //response looks like this: object with map (countryID => countryObject) with country objects
        /*
        {
            "1": {
                "continent": "asia",
                "head_id": "24208",
                "id": 1,
                "name": "Test-Country",
                "server_id": 1
            }
        }
         */
        Gson gson = new Gson();
        try {
            JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();

            Type mapType = new TypeToken<Map<String, Country>>() {}.getType();
            Map<String, Country> countryMap = gson.fromJson(responseObject, mapType);
            for (Country c :  countryMap.values()){
                //Country country = gson.fromJson(element, Country.class);
                countries.add(c);
                }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or throw an exception based on your requirements
        return countries;
    }

    public List<Plot> getPSTeamPlots(String teamApiKey) throws Exception {
        List<Plot> plots = new ArrayList<>();
        String jsonResponse = httpGET(PS_PlOTS_URL.replace("%API_KEY%", teamApiKey));


        /** array of objects
         *[
            {
                "city_project_id": 1,
                "create_date": "2022-07-14T00:00:00.000Z",
                "create_player": "3b350308-d857-4ecc-8b71-c93a2cf3c87b",
                "difficulty_id": 1,
                "id": 1,
                "last_activity": "2022-07-17T00:00:00.000Z",
                "mc_coordinates": "3190699.5,690.5,-4673990.0",
                "member_uuids": null,
                "outline": "3190689.0,-4673973.0|3190718.0,-4673987.0|3190712.0,-4674007.0|3190689.0,-4674001.0|3190681.0,-4673994.0",
                "owner_uuid": "3b350308-d857-4ecc-8b71-c93a2cf3c87b",
                "pasted": 1,
                "review_id": 5,
                "score": 16,
                "status": "completed",
                "type": 2,
                "version": 3
            },...
         */
        try {
            JsonArray responseArray = new JsonParser().parse(jsonResponse).getAsJsonArray();
            for (JsonElement element : responseArray){
                JsonObject obj = element.getAsJsonObject();
                String[] splitCoordinates = obj.get("mc_coordinates").getAsString().split(",");
                Vector mcCoordinates = new Vector(
                        Float.parseFloat(splitCoordinates[0]),
                        Float.parseFloat(splitCoordinates[1]),
                        Float.parseFloat(splitCoordinates[2])
                );
                Plot p = new Plot(obj.get("id").getAsInt(), obj.get("status").getAsString(), 
                    obj.get("city_project_id").getAsInt(), mcCoordinates,
                    obj.get("pasted").getAsInt(), obj.get("version").getAsFloat());
                    
                plots.add(p);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or throw an exception based on your requirements
        return plots;
    }
    
    public List<Server> getPSTeamServers(String teamApiKey) throws Exception{
        List<Server> servers = new ArrayList<>();
        String jsonResponse = httpGET(GET_PS_SERVERS_URL.replace("%API_KEY%", teamApiKey));

        //response looks like this: object with map (serverid => server data)
        /*
            {
                "1": {
                    "ftp_configuration_id": 2,
                    "id": 1,
                    "name": "BT-1"
                },...
         */
        Gson gson = new Gson();
        try {
            JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
            for(Map.Entry<String, JsonElement> serverEntry : responseObject.entrySet()) {
                //System.out.println("Key = " + serverEntry.getKey() + " Value = " + serverEntry.getValue() );
                //values are jsonobjects that can be parsed into server objects
                Server s = gson.fromJson(serverEntry.getValue(), Server.class);
                servers.add(s);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Return a default value or throw an exception based on your requirements
        return servers;
    }
    
    public List<FTPConfiguration> getPSTeamFTPConfigurations(String teamApiKey) throws Exception{
        List<FTPConfiguration> configs = new ArrayList<>();
        String jsonResponse = httpGET(GET_PS_FTP_URL.replace("%API_KEY%", teamApiKey));

        //response object with map serverID => array (!?) of ftp configs
        /*
            {
                "1": [
                    {
                        "address": "xx.xxx.xxx.xxx",
                        "id": 2,
                        "isSFTP": 1,
                        "password": "<redacted>",
                        "port": 22,
                        "schematics_path": "/home/PlotSystem/",
                        "username": "<redacted>"
                    }
                ],...
            }
         */
        //register type adapter for boolean from integer
        Gson gson = new GsonBuilder()
                .registerTypeAdapter(boolean.class, new BooleanDeserializer())
                .create();
        try {
            JsonObject responseObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
            Type mapType = new TypeToken<Map<String, JsonArray>>() {}.getType();
            Map<String, JsonArray> serverMap = gson.fromJson(responseObject, mapType);
            for (JsonArray ftpArray : serverMap.values()){
                for (JsonElement element : ftpArray){

                    FTPConfiguration c = gson.fromJson(element, FTPConfiguration.class);

                    configs.add(c);
                }
            }

            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return configs;
    }
    public void updatePSPlot(int plotID, List<String> changeList, String teamApiKey) throws Exception{
        //Request body is an array with a single element, usind identifier and any parameters to change
        String requestBody = "[\n\t{\n"
                    + String.join(",\n\t\t", changeList ) +"\n\t}\n]";

        httpPUTAsync(PS_PlOTS_URL.replace("%API_KEY%", teamApiKey) + "?id="+plotID, requestBody, new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }
        });
    }


    public int createPSPlot(int cityProjectID, int difficultyID, Vector plotCoords, String polyOutline, double plotVersion, String teamApiKey) throws Exception{
        String vectorString = plotCoords.getX() + "," + plotCoords.getY() + "," + plotCoords.getZ();
        String requestBody = "[\n\t{\n"
            +"\t\t\"city_project_id\": "+cityProjectID+",\n"
            +"\t\t\"difficulty_id\": "+difficultyID+",\n"
            +"\t\t\"mc_coordinates\": \""+vectorString+"\",\n"
            +"\t\t\"outline\": \""+polyOutline+"\",\n"
            +"\t\t\"version\": "+plotVersion+",\n"
            +"\t\t\"is_order\": false\n"            
            +"\n\t}\n]";


        String jsonResponse = httpPOST(PS_PlOTS_URL.replace("%API_KEY%", teamApiKey), requestBody);

        //get plot id from json-response

        JsonObject jsonObject = new JsonParser().parse(jsonResponse).getAsJsonObject();
        int plotID = jsonObject.get("plot_id").getAsInt();
        return plotID;     
    }

    public void deletePSPlot(int plotID, String teamApiKey) throws Exception {
        //System.out.println("DELETE " +PS_PlOTS_URL.replace("%API_KEY%", teamApiKey));
        httpDELETEAsync(PS_PlOTS_URL.replace("%API_KEY%", teamApiKey) + "?id="+plotID, "", new API.ApiResponseCallback() {
            @Override
            public void onResponse(String response) {
            }

            @Override
            public void onFailure(IOException e) {
                e.printStackTrace();
            }
        });
    }
}

