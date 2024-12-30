package com.alpsbte.alpslib.libpsterra.core.plotsystem;

import com.alpsbte.alpslib.libpsterra.core.Connection;
import com.alpsbte.alpslib.libpsterra.core.PSTerraSetup;
import com.alpsbte.alpslib.libpsterra.core.config.ConfigPaths;
import com.alpsbte.alpslib.libpsterra.utils.FTPManager;
import com.alpsbte.alpslib.libpsterra.utils.Utils;
import com.alpsbte.alpslib.libpsterra.utils.VersionUtil;
import com.alpsbte.alpslib.libpsterra.utils.WorldEditUtil;
import com.cryptomorin.xseries.XMaterial;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.regions.AbstractRegion;
import com.sk89q.worldedit.regions.CylinderRegion;
import com.sk89q.worldedit.regions.Polygonal2DRegion;
import com.sk89q.worldedit.regions.Region;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Sign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.util.Vector;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;

public class PlotCreator {
    @FunctionalInterface
    public interface IPlotRegionsAction {
        void onSchematicsCreationComplete(Polygonal2DRegion plotRegion, CylinderRegion environmentRegion, Vector plotCenter);
    }

    public final static double PLOT_VERSION = 3.0;
    private final String schematicsPath;
    public final static int MIN_OFFSET_Y = 5;
    //private Plugin plugin;
    private final Connection connection;
    private final FileConfiguration config;
    private final Plugin plugin;

    public PlotCreator(Plugin plugin, FileConfiguration config, Connection connection, String pluginConfigPath){
        this.schematicsPath = Paths.get(pluginConfigPath, "schematics") + File.separator;
        this.connection = connection;
        this.plugin = plugin;    
        this.config = config;
    }

    public void create(Player player, int environmentRadius, IPlotRegionsAction plotRegionsAction) {
        Polygonal2DRegion plotRegion;
        Vector plotCenter;
        CylinderRegion environmentRegion = null;

        // Get WorldEdit selection of player
        Region rawPlotRegion;
        try {
            rawPlotRegion = Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(player.getName())).getSelection(
                    Objects.requireNonNull(WorldEdit.getInstance().getSessionManager().findByName(player.getName())).getSelectionWorld());
        } catch (Exception ex) {
            player.sendMessage(Utils.getErrorMessageFormat("Please select a plot using WorldEdit!", config));
            return;
        }

        // Create plot and environment regions
        // Check if WorldEdit selection is polygonal
        if (rawPlotRegion instanceof Polygonal2DRegion) {
            // Cast WorldEdit region to polygonal region
            plotRegion = (Polygonal2DRegion) rawPlotRegion;

            // Check if the polygonal region is valid
            if (plotRegion.getLength() > 100 || plotRegion.getWidth() > 100 || (plotRegion.getHeight() > 256 - MIN_OFFSET_Y)) {
                player.sendMessage(Utils.getErrorMessageFormat("Please adjust your selection size!", config));
                return;
            }

            // Get plot minY and maxY
            double offsetHeight = (256 - plotRegion.getHeight()) / 2d;
            final int minYOffset = plotRegion.getMinimumY() - (int) Math.ceil(offsetHeight);
            final int maxYOffset = plotRegion.getMaximumY() + (int) Math.floor(offsetHeight);
            final int minY = plotRegion.getMinimumY() - MIN_OFFSET_Y;
            final int maxY = maxYOffset + (int) Math.ceil(offsetHeight) - MIN_OFFSET_Y;

            plotRegion.setMinimumY(minY);
            plotRegion.setMaximumY(maxY);

            // Create the environment selection
            if (environmentRadius > 0) {
                // Get min region size for environment radius
                int radius = Math.max(plotRegion.getWidth() / 2 + environmentRadius, plotRegion.getLength() / 2 + environmentRadius);


                // Create a new cylinder region with the size of the plot + the configured radius around it
                Vector plotRegionCenter = WorldEditUtil.getRegionCenter(plotRegion);

                if(plotRegionCenter == null) {
                    player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                    return;
                }

                plotRegionCenter.setX(plotRegionCenter.getBlockX());
                plotRegionCenter.setZ(plotRegionCenter.getBlockZ());

                environmentRegion = WorldEditUtil.createCylinderRegion(plotRegion, plotRegionCenter, radius, minY, maxY);

                if (environmentRegion == null) {
                    player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                    return;
                }


                // Convert environment region to polygonal region and save points
                final List<Vector> environmentRegionPoints = WorldEditUtil.polygonizeRegion(environmentRegion, -1);
                final AtomicInteger newYMin = new AtomicInteger(minY);

                // Iterate over the points and check for the lowest Y value
                final World world = player.getWorld();
                environmentRegionPoints.forEach(p -> {
                    int highestBlock = minYOffset;
                    for (int y = minYOffset; y <= maxYOffset; y++) {
                        if (world.getBlockAt(p.getBlockX(), y, p.getBlockZ()).getType() != Material.AIR) highestBlock = y;
                    }
                    if (highestBlock < newYMin.get()) newYMin.set(highestBlock);
                });

                // Update plot and environment min and max Y to new value if necessary
                if (newYMin.get() < minY) {
                    int heightDif = (minY - newYMin.get()) + MIN_OFFSET_Y;
                    plotRegion.setMinimumY(newYMin.get() - MIN_OFFSET_Y);
                    environmentRegion.setMinimumY(newYMin.get() - MIN_OFFSET_Y);
                    plotRegion.setMaximumY(maxY - heightDif);
                    environmentRegion.setMaximumY(maxY - heightDif);
                }
            }
            plotCenter = WorldEditUtil.getRegionCenter(plotRegion);
            plotRegionsAction.onSchematicsCreationComplete(plotRegion, environmentRegion, plotCenter);
        } else {
            player.sendMessage(Utils.getErrorMessageFormat("Please use polygonal selection to create a new plot!", config));
        }
    }

    public void createPlot(Player player, CityProject cityProject, int difficultyID) {
        CompletableFuture.runAsync(() -> {
            boolean environmentEnabled;

            // Read the config
            environmentEnabled = config.getBoolean(ConfigPaths.ENVIRONMENT_ENABLED);
            int environmentRadius = config.getInt(ConfigPaths.ENVIRONMENT_RADIUS);


            create(player, environmentEnabled ? environmentRadius : -1, (plotRegion, environmentRegion, plotCenter) -> {
                Bukkit.getScheduler().scheduleSyncDelayedTask(PSTerraSetup.getPlugin(), () -> {
                    int plotID = -1;;
                    String plotFilePath;
                    String environmentFilePath = null;
                    String fileType = ".schem";

                    if(VersionUtil.is_1_12())
                        fileType = ".schematic";

                    try {
                        // Check if selection contains sign
                        if (!containsSign(plotRegion, player.getWorld())) {
                            player.sendMessage(Utils.getErrorMessageFormat("Please place a minimum of one sign for the street side!", config));
                            return;
                        }


                        // Inform player about the plot creation
                        player.sendMessage(Utils.getInfoMessageFormat("Creating plot...", config));


                        // Convert polygon outline data to string
                        String polyOutline;
                        List<String> points = new ArrayList<>();
                        for (Vector point : WorldEditUtil.getRegionPoints(plotRegion))
                            points.add(point.getX() + "," + point.getZ());
                        polyOutline = String.join("|", points);

                        // Insert into database (as transaction, needs to be committed or canceled to finalize)
                        plotID = connection.createPlotTransaction(
                            cityProject, difficultyID, plotCenter, polyOutline, player, PLOT_VERSION
                        );

                        // Save plot and environment regions to schematic files
                        // Get plot schematic file path
                        Server server = connection.getServer(cityProject);
                        plotFilePath = createPlotSchematic(plotRegion, Paths.get(schematicsPath, String.valueOf(server.id), String.valueOf(cityProject.id), plotID + fileType).toString());

                        if (plotFilePath == null) {
                            Bukkit.getLogger().log(Level.SEVERE, "Could not create plot schematic file!");
                            player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                            return;
                        }

                        // Get environment schematic file path
                        if (environmentEnabled) {
                            environmentFilePath = createPlotSchematic(environmentRegion, Paths.get(schematicsPath, String.valueOf(server.id), String.valueOf(cityProject.id), plotID + "-env" + fileType).toString());

                            if (environmentFilePath == null) {
                                Bukkit.getLogger().log(Level.SEVERE, "Could not create environment schematic file!");
                                player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                                return;
                            }
                        }


                        // Upload schematic files to SFTP/FTP server if enabled
                        FTPConfiguration ftpConfiguration = connection.getFTPConfiguration(cityProject);

                        if (ftpConfiguration != null) {
                            if (environmentEnabled) FTPManager.uploadSchematics(FTPManager.getFTPUrl(ftpConfiguration, server, cityProject), new File(plotFilePath), new File(environmentFilePath));
                            else FTPManager.uploadSchematics(FTPManager.getFTPUrl(ftpConfiguration, server, cityProject), new File(plotFilePath));
                        }


                        // Place plot markings on plot region
                        placePlotMarker(plotRegion, player, plotID);
                        // TODO: Change top blocks of the plot region to mark plot as created


                        // Finalize database transaction
                        connection.commitPlot();

                        player.sendMessage(Utils.getInfoMessageFormat("Successfully created new plot! §f(City: §6" + cityProject.name + " §f| Plot-ID: §6" + plotID + "§f)", config));
                        player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                    } catch (Exception ex) {
                        try {
                            connection.rollbackPlot(plotID);

                        } catch (Exception rollbackEx) {
                            Bukkit.getLogger().log(Level.SEVERE, "An exception occured during rollback!", rollbackEx);
                        }
                        Bukkit.getLogger().log(Level.SEVERE, "An error occurred while creating plot:" + ex.getMessage(), ex);
                        player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot: " + ex.getMessage(), config));
                    }
                });
            });
        });
    }



    public void createTutorialPlot(Player player, int environmentRadius) {
        CompletableFuture.runAsync(() -> {
            create(player, environmentRadius, (plotRegion, environmentRegion, plotCenter) -> {
                String fileType = ".schem";

                if(VersionUtil.is_1_12())
                    fileType = ".schematic";

                try {
                    // Inform player about the plot creation
                    player.sendMessage(Utils.getInfoMessageFormat("Creating plot...", config));


                    // Convert polygon outline data to string
                    String polyOutline;
                    List<String> points = new ArrayList<>();

                    for (Vector point : WorldEditUtil.getRegionPoints(plotRegion))
                        points.add(point.getX() + "," + point.getZ());
                    polyOutline = String.join("|", points);
                    Bukkit.getLogger().log(Level.INFO, "Tutorial plot outlines: " + polyOutline);

                    // Save plot and environment regions to schematic files
                    // Get plot schematic file path
                    String plotFilePath = createPlotSchematic(plotRegion, Paths.get(schematicsPath, "tutorials", "id-stage" + fileType).toString());

                    if (plotFilePath == null) {
                        Bukkit.getLogger().log(Level.SEVERE, "Could not create plot schematic file!");
                        player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                        return;
                    }
                    Bukkit.getLogger().log(Level.INFO, "Tutorial plot schematic path: " + plotFilePath);

                    // Get environment schematic file path
                    if (environmentRadius > 0) {
                        String environmentFilePath = createPlotSchematic(environmentRegion, Paths.get(schematicsPath, "tutorials", "id-env" + fileType).toString());

                        if (environmentFilePath == null) {
                            Bukkit.getLogger().log(Level.SEVERE, "Could not create environment schematic file!");
                            player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                            return;
                        }
                        Bukkit.getLogger().log(Level.INFO, "Tutorial environment schematic path: " + environmentFilePath);
                    }

                    player.sendMessage(Utils.getInfoMessageFormat("Successfully created new tutorial plot! Check your console for more information!", config));
                    player.playSound(player.getLocation(), Sound.ENTITY_EXPERIENCE_ORB_PICKUP, 1f, 1f);
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "An error occurred while creating plot!", ex);
                    player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
                }
            });
        });
    }



    /**
     * Creates a plot schematic of a selected region from the player.
     *
     * @param region: Selected poly region of the player
     * @return the file path of the created schematic
     */
    private String createPlotSchematic(AbstractRegion region, String filePath) throws IOException, WorldEditException {
        // Create File
        File schematic = new File(filePath);

        // Delete file if exists
        Files.deleteIfExists(schematic.getAbsoluteFile().toPath());
        boolean createdDirs = schematic.getParentFile().mkdirs();
        boolean createdFile = schematic.createNewFile();

        if ((!schematic.getParentFile().exists() && !createdDirs) || (!schematic.exists() && !createdFile))
            return null;


        // Store content of region in schematic
        WorldEditUtil.saveSchematic(region, schematic);

        return filePath;
    }

    /**
     * Checks if polygon region contains a sign and update sign text
     * @param polyRegion WorldEdit region
     * @param world Region world
     * @return true if polygon region contains a sign, false otherwise
     */
    private static boolean containsSign(Polygonal2DRegion polyRegion, World world) {
        boolean hasSign = false;

        List<XMaterial> signs = getSigns();
        Vector min = WorldEditUtil.getRegionMinimumPoint(polyRegion);
        Vector max = WorldEditUtil.getRegionMaximumPoint(polyRegion);
        if(min == null || max == null) return false;

        for (int i = min.getBlockX(); i <= max.getBlockX(); i++) {
            for (int j = min.getBlockY(); j <= max.getBlockY(); j++) {
                for (int k = min.getBlockZ(); k <= max.getBlockZ(); k++) {
                    if (WorldEditUtil.regionContains(polyRegion, new Vector(i, j, k))) {
                        Block block = world.getBlockAt(i, j, k);

                        try {
                            XMaterial material = XMaterial.matchXMaterial(block.getType());

                            if (signs.contains(material)) {
                                hasSign = true;

                                Sign sign = (Sign) block.getState();
                                for (int s = 0; s < 4; s++) {
                                    if (s == 1) {
                                        sign.setLine(s, "§c§lStreet Side");
                                    } else {
                                        sign.setLine(s, "");
                                    }
                                }
                                sign.update();
                            }
                        }catch (IllegalArgumentException ignored){}
                    }
                }
            }
        }
        return hasSign;
    }

    /**
     * Places a plot marker in the center of the polygon region
     * @param plotRegion WorldEdit region
     * @param player Player
     * @param plotID Plot ID
     */
    private void placePlotMarker(Region plotRegion, Player player, int plotID) {
        Vector center = WorldEditUtil.getRegionCenter(plotRegion);

        if(center == null) {
            player.sendMessage(Utils.getErrorMessageFormat("An error occurred while creating plot!", config));
            return;
        }

        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            Location highestBlock = player.getWorld().getHighestBlockAt(center.getBlockX(), center.getBlockZ()).getLocation();

            player.getWorld().getBlockAt(highestBlock).setType(Material.SEA_LANTERN);
            Block signBlock = player.getWorld().getBlockAt(highestBlock.add(0, 1, 0));

            if(getSign() != null)
                signBlock.setType(getSign());
            else {
                player.sendMessage(Utils.getErrorMessageFormat("Could not place plot marker sign!", config));
                return;
            }

            if(!(signBlock.getState() instanceof Sign)) {
                player.sendMessage(Utils.getErrorMessageFormat("Could not place plot marker sign!", config));
                return;
            }

            Sign sign = (Sign) signBlock.getState();
            org.bukkit.material.Sign matSign = new org.bukkit.material.Sign(getSign());
            matSign.setFacingDirection(getPlayerFaceDirection(player).getOppositeFace());
            sign.setData(matSign);
            sign.setLine(0, "§8§lID: §c§l" + plotID);
            sign.setLine(2, "§8§lCreated By:");
            sign.setLine(3, "§c§l" + player.getName());
            sign.update();
        }, 1L);
    }

    /**
     * Gets the direction the player is facing
     * @param player Player
     * @return Direction
     */
    private static BlockFace getPlayerFaceDirection(Player player) {
        float y = player.getLocation().getYaw();
        if( y < 0 ){y += 360;}
        y %= 360;
        int i = (int)((y+8) / 22.5);
        return BlockFace.values()[i];
    }

    private static List<XMaterial> getSigns(){
        List<XMaterial> signs = new ArrayList<>();
        for (XMaterial material : XMaterial.values()) {
            if (material.name().contains("SIGN")) {
                signs.add(material);
            }
        }
        return signs;
    }

    private static Material getSign(){
        if(VersionUtil.is_1_12())
            return Material.getMaterial("SIGN_POST");

        return XMaterial.OAK_SIGN.parseMaterial();
    }
}