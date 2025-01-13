package com.alpsbte.alpslib.libpsterra.core.plotsystem;

import com.alpsbte.alpslib.libpsterra.core.Connection;
import com.alpsbte.alpslib.libpsterra.core.config.PSInitializer;
import com.alpsbte.alpslib.utils.item.LegacyItemBuilder;
import com.alpsbte.alpslib.utils.item.LegacyLoreBuilder;
import com.cryptomorin.xseries.XMaterial;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.ipvp.canvas.Menu;
import org.ipvp.canvas.mask.BinaryMask;
import org.ipvp.canvas.mask.Mask;
import org.ipvp.canvas.type.ChestMenu;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;

public class CreatePlotMenu {
    private final Menu createPlotMenu = ChestMenu.builder(6).title(Component.text("Create Plot")).redraw(true).build();
    private final Menu difficultyMenu = ChestMenu.builder(3).title(Component.text("Select Plot Difficulty")).redraw(true).build();

    private final List<CityProject> cityProjects;
    private int selectedCityID = -1;

    private final Player player;
    private Connection connection;
    private PlotCreator plotCreator;

    private PSInitializer initializer;

    public CreatePlotMenu(@NotNull PSInitializer initializer, Player player, Connection connection, PlotCreator plotCreator) {
        this.player = player;
        this.connection = connection;
        this.plotCreator = plotCreator;
        this.initializer = initializer;
        this.cityProjects = getCityProjects();
        getCityProjectUI().open(player);

        
    }

    public Menu getCityProjectUI() {
        Mask mask = BinaryMask.builder(createPlotMenu)
                .item(new LegacyItemBuilder(Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())).setName(" ").build())
                .pattern("111101111") // First row
                .pattern("000000000") // Second row
                .pattern("000000000") // Third row
                .pattern("000000000") // Fourth row
                .pattern("000000000") // Fifth row
                .pattern("111010111") // Sixth row
                .build();
        mask.apply(createPlotMenu);

        createPlotMenu.getSlot(4).setItem(getStats(player.getLocation()));

        for(int i = 0; i < cityProjects.size(); i++) {
            int cityID = i;
            createPlotMenu.getSlot(9 + i).setClickHandler((clickPlayer, clickInformation) -> {
                if(selectedCityID != cityID) {

                    selectedCityID = cityID;
                    createPlotMenu.getSlot(4).setItem(getStats(player.getLocation()));

                    clickPlayer.playSound(clickPlayer.getLocation(), Sound.ENTITY_ITEM_PICKUP, 1f, 1f);
                }
            });
        }

        createPlotMenu.getSlot(48).setItem(
                new LegacyItemBuilder(Objects.requireNonNull(XMaterial.GREEN_WOOL.parseItem()))
                        .setName("§a§lContinue")
                        .build());
        createPlotMenu.getSlot(48).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            getDifficultyMenu().open(clickPlayer);
        });

        createPlotMenu.getSlot(50).setItem(
                new LegacyItemBuilder(Objects.requireNonNull(XMaterial.RED_WOOL.parseItem()))
                        .setName("§c§lCancel")
                        .build());
        createPlotMenu.getSlot(50).setClickHandler((clickPlayer, clickInformation) -> clickPlayer.closeInventory());

        return createPlotMenu;
    }

    public Menu getDifficultyMenu() {
        CityProject cityProject = cityProjects.get(selectedCityID);

        // Set glass border
        Mask mask = BinaryMask.builder(createPlotMenu)
                .item(new LegacyItemBuilder(Objects.requireNonNull(XMaterial.GRAY_STAINED_GLASS_PANE.parseItem())).setName(" ").build())
                .pattern("111111111") // First row
                .pattern("000000000") // Second row
                .pattern("111111111") // Third row
                .build();
        mask.apply(difficultyMenu);

        difficultyMenu.getSlot(10).setItem(
                new LegacyItemBuilder(Objects.requireNonNull(XMaterial.LIME_WOOL.parseItem()))
                        .setName("§a§lEasy")
                        .build()
        );
        difficultyMenu.getSlot(10).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            plotCreator.createPlot(clickPlayer, cityProject, 1);
        });

        difficultyMenu.getSlot(13).setItem(
                new LegacyItemBuilder(Objects.requireNonNull(XMaterial.ORANGE_WOOL.parseItem()))
                        .setName("§6§lMedium")
                        .build()
        );
        difficultyMenu.getSlot(13).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            plotCreator.createPlot(clickPlayer, cityProject, 2);
        });

        difficultyMenu.getSlot(16).setItem(
                new LegacyItemBuilder(Objects.requireNonNull(XMaterial.RED_WOOL.parseItem()))
                        .setName("§c§lHard")
                        .build()
        );
        difficultyMenu.getSlot(16).setClickHandler((clickPlayer, clickInformation) -> {
            clickPlayer.closeInventory();
            plotCreator.createPlot(clickPlayer, cityProject, 3);
        });

        return difficultyMenu;
    }

    private List<CityProject> getCityProjects() {
        try {
            List<CityProject> listProjects = new ArrayList<>();
            boolean success = connection.getAllCityProjects(listProjects);

            if(initializer.isDevMode())
                Bukkit.getConsoleSender().sendMessage("loading city projects to build menu. found " + listProjects.size() + " cityprojects");

            int counter = 0;
            for (CityProject city : listProjects){
                Country cityCountry = connection.getCountry(city.country_id);
                createPlotMenu.getSlot(9 + counter).setItem(city.getItem(cityCountry.head_id));
                counter++;
            }

            
            if (!success){
                createPlotMenu.getSlot(9 + counter).setItem(new LegacyItemBuilder(Material.BARRIER)
                    .setName("§c§lError")
                    .setLore(new LegacyLoreBuilder()
                            .addLine("Could not load city project.")
                            .build())
                .build());
            }

            return listProjects;
        } catch (Exception ex)
        {
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while reading all city projects!", ex);
            return null;
        }
    }

    private ItemStack getStats(Location coords) {
        //TODO double-check this line. cityProject.get() returns index in list, not by cityID!
        //get country head
        if (selectedCityID == -1){
            return new ItemStack(Material.NAME_TAG);
        }
        try {         
            CityProject city = cityProjects.get(selectedCityID); //TOTO fixme!
            Country c = connection.getCountry(city.country_id);
            return new LegacyItemBuilder(cityProjects.get(selectedCityID).getItem(c.head_id))
                    .setName("§6§lSTATS")
                    .setLore(new LegacyLoreBuilder()
                            .addLines("§bX: §7" + coords.getX(),
                                    "§bY: §7" + coords.getY(),
                                    "§bZ: §7" + coords.getZ(),
                                    "§bCity: §7" +  city.name)
                            .build())
                    .build();
        } catch (Exception ex){
            Bukkit.getLogger().log(Level.SEVERE, "An error occurred while getting stats for location", ex);
            
            return null;
        }
    }
}
