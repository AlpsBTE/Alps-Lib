package com.alpsbte.alpslib.libpsterra.core;


import com.alpsbte.alpslib.libpsterra.core.api.PlotSystemAPI;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.*;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class NetworkAPIConnection implements Connection{
    private String teamApiKey;
    private PlotSystemAPI api;

    public NetworkAPIConnection(String host, int port, String teamApiKey) {    
        // this.host = host;
        // this.port = port;
        this.teamApiKey = teamApiKey;
        this.api = new PlotSystemAPI(host, port);
    }

    @Override
    public CityProject getCityProject(int cityID) {
        try {
            List<CityProject> cities = api.getPSTeamCities(teamApiKey);
            for (CityProject c : cities){
                if (c.id == cityID)
                    return c;
            }
            return null;
        } catch (Exception ex) {
             return null;
        }
    }

    @Override
    public boolean getAllCityProjects(List<CityProject> resultList) {
        try {
            resultList.addAll(api.getPSTeamCities(teamApiKey));
            return true;
        } catch (Exception ex) {
             return false;
        }
    }
    
    @Override
    public int createPlotTransaction(CityProject cityProject, int difficultyID, Vector plotCoords, String polyOutline, Player player, double plotVersion) throws Exception{
        return api.createPSPlot(cityProject.id, difficultyID, plotCoords, polyOutline, plotVersion, teamApiKey);
               
    }




    @Override
    public void commitPlot() throws Exception{
        //nothing to do, plot was already created

    }

    @Override
    public void rollbackPlot(int plotID) throws Exception{
        //undo plot creation
        if (plotID >= 0)
            api.deletePSPlot(plotID, teamApiKey);
    }


    @Override
    public Plot getPlot(int plotID) throws Exception {
        
        try {
            List<Plot> plots = api.getPSTeamPlots(teamApiKey);
            for (Plot p : plots){
                if (p.id == plotID)
                    return p;
        }
            return null;
        } catch (Exception ex) {
             return null;
        }
    }

    @Override
    public List<Plot> getCompletedAndUnpastedPlots() throws Exception {
        List<Plot> unpastedPlots = new ArrayList<>();

        List<Plot> allPlots = api.getPSTeamPlots(teamApiKey);
        for (Plot p : allPlots){
            if (p.status.equals("completed") && p.pasted==0)
                unpastedPlots.add(p);
        }
        return unpastedPlots; 
    }


    @Override
    public void setPlotPasted(int plotID) throws Exception {
        api.updatePSPlot(plotID, Arrays.asList("\"pasted\":1 "), teamApiKey);

    }

    @Override
    public Server getServer(int serverID) throws Exception {
        List<Server> servers = api.getPSTeamServers(teamApiKey);
        for (Server s : servers){
            if (s.id == serverID)
                return s;
        }
        throw new java.io.IOException("Could not find server with id " + serverID);
    }

    @Override
    public FTPConfiguration getFTPConfiguration(int ftp_configuration_id) throws Exception {

        List<FTPConfiguration> configs = api.getPSTeamFTPConfigurations(teamApiKey);
        for (FTPConfiguration c : configs){
            if (c.id == ftp_configuration_id)
                return c;
        }
        throw new java.io.IOException("Could not find ftp config with id " + ftp_configuration_id + " (or it is from another build team).");
    }

    @Override
    public List<Country> getTeamCountries() throws Exception
    {
        return api.getPSTeamCountries(teamApiKey);
    }

    @Override
    public Country getCountry(int countryID) throws Exception {
        List<Country> countries = getTeamCountries();
        for (Country c : countries){
            if (c.id == countryID)
                return c;
        }
        throw new java.io.IOException("Could not find country with id " + countryID);
    }

}
