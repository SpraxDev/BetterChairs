/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs.Files;

import net.blackscarx.betterchairs.ChairsPlugin;
import net.blackscarx.betterchairs.SlabBlock;
import net.blackscarx.betterchairs.StairsBlock;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Config {
    private static FileConfiguration config = null;
    private static File configFile = null;
    private static Plugin plugin = ChairsPlugin.getPlugin(ChairsPlugin.class);

    public static void init() {
        reload();
        load();
        reload();
    }

    private static void load() {
        List<String> stairs = new ArrayList<>();
        for (StairsBlock stairsBlock : StairsBlock.values()) {
            stairs.add(stairsBlock.getName());
        }
        List<String> slab = new ArrayList<>();
        for (SlabBlock slabBlock : SlabBlock.getList())
            slab.add(slabBlock.getName());
        String header = "BlackScarx All right reserved\n";
        header = header.concat("The list of the stairs\n");
        for (String s : stairs)
            header = header.concat("    - " + s + "\n");
        header = header.concat("The list of the slabs\n");
        for (SlabBlock s : SlabBlock.getList())
            header = header.concat("    - " + s.getName() + "\n");
        getConfig().options().header(header);
        getConfig().addDefault("Update Checker", true);
        getConfig().addDefault("Use permission for sit", false);
        getConfig().addDefault("Enable Stairs Block", stairs);
        getConfig().addDefault("Use slab", false);
        getConfig().addDefault("Enable Slab Block", slab);
        getConfig().addDefault("Send message when player sit", false);
        getConfig().addDefault("Send message if the chairs is already occupied", false);
        getConfig().addDefault("Send message if the word is disable", false);
        List<String> exampleWord = new ArrayList<>();
        exampleWord.add("example1");
        exampleWord.add("example2");
        getConfig().addDefault("Disable world", exampleWord);
        getConfig().addDefault("AutoTurn", true);
        getConfig().addDefault("Distance of the stairs", 2);
        getConfig().addDefault("Need to sign or chair on each side", false);
        getConfig().addDefault("Send message if the Chairs need sign or chair", false);
        getConfig().addDefault("No item in hand", true);
        getConfig().addDefault("Regen need permission", false);
        getConfig().addDefault("Regen when sit", false);
        getConfig().addDefault("Amplifier", 1);
        getConfig().options().copyDefaults(true);
        save();
    }

    public static void reload() {
        if (configFile == null) {
            configFile = new File(plugin.getDataFolder(), "config.yml");
        }
        config = YamlConfiguration.loadConfiguration(configFile);
    }

    public static FileConfiguration getConfig() {
        if (config == null) reload();
        return config;
    }

    public static void save() {
        if (config == null || configFile == null) {
            return;
        }

        try {
            config.save(configFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save config.yml to " + configFile.getAbsolutePath());
            ex.printStackTrace();
        }
    }
}
