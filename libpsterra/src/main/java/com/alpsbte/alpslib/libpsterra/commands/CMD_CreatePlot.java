package com.alpsbte.alpslib.libpsterra.commands;

import com.alpsbte.alpslib.libpsterra.core.Connection;
import com.alpsbte.alpslib.libpsterra.core.config.PSInitializer;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.CreatePlotMenu;
import com.alpsbte.alpslib.libpsterra.core.plotsystem.PlotCreator;
import com.alpsbte.alpslib.libpsterra.utils.Utils;
import com.alpsbte.alpslib.utils.AlpsUtils;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.logging.Level;

public class CMD_CreatePlot implements CommandExecutor {
    private PlotCreator plotCreator;
    private Connection connection;
    private FileConfiguration config;

    private PSInitializer initializer;

    public CMD_CreatePlot(@NotNull PSInitializer initializer, PlotCreator creator, Connection connection, FileConfiguration config){
        this.plotCreator = creator;
        this.connection = connection;
        this.config = config;
        this.initializer = initializer;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player) {
            if(Utils.hasPermission(sender, "createplot")) {
                try {
                    if (args.length > 1) {
                        if (args[0].equalsIgnoreCase("tutorial") && AlpsUtils.tryParseInt(args[1]) != null) {
                            plotCreator.createTutorialPlot(((Player) sender).getPlayer(), Integer.parseInt(args[1]));
                            return true;
                        }
                    }
                    new CreatePlotMenu(initializer, ((Player) sender).getPlayer(), connection, plotCreator);
                } catch (Exception ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "An error occurred while opening create plot menu!", ex);
                    sender.sendMessage(Utils.getErrorMessageFormat("An error occurred while opening create plot menu!", config));
                }
            }
        }
        return true;
    }
}
