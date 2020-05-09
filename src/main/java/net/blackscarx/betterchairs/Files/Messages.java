/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs.Files;

import net.blackscarx.betterchairs.ChairsPlugin;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class Messages {

    private static FileConfiguration messages = null;
    private static File messagesFile = null;
    private static Plugin plugin = ChairsPlugin.getPlugin(ChairsPlugin.class);

    public static void init() {
        reload();
        check();
        load();
        reload();
    }

    private static void load() {
        String header = "BlackScarx All right reserved\n";
        getConfig().options().header(header);
        getConfig().addDefault("Cant use message", "&cYou don't have permission for that");
        getConfig().addDefault("Message to send when player toggle chairs to off", "&cYou can't sit now");
        getConfig().addDefault("Message to send when player toggle chairs to on", "&aYou can sit now");
        getConfig().addDefault("Message to send when player sit", "&aYou are now sitting. Take a break.");
        getConfig().addDefault("Message to send if the word is disable", "&cThe Chairs is not enable in this world");
        getConfig().addDefault("Message to send if the chairs is occupied", "&cThis chairs is already occupied");
        getConfig().addDefault("Message to send if the chairs need sign or chair", "&cIf you want to sit on this stairs you need to place a sign or stairs on each side");
        getConfig().options().copyDefaults(true);
        save();
    }

    private static void check() {
        if (Config.getConfig().contains("Cant use message")) {
            getConfig().set("Cant use message", Config.getConfig().getString("Cant use message", "&cYou don't have permission for that"));
            Config.getConfig().set("Cant use message", null);
        }
        if (Config.getConfig().contains("Message to send when player toggle chairs to off")) {
            getConfig().set("Message to send when player toggle chairs to off", Config.getConfig().getString("Message to send when player toggle chairs to off", "&cYou can't sit now"));
            Config.getConfig().set("Message to send when player toggle chairs to off", null);
        }
        if (Config.getConfig().contains("Message to send when player toggle chairs to on")) {
            getConfig().set("Message to send when player toggle chairs to on", Config.getConfig().getString("Message to send when player toggle chairs to on", "&aYou can sit now"));
            Config.getConfig().set("Message to send when player toggle chairs to on", null);
        }
        if (Config.getConfig().contains("Message to send when player sit")) {
            getConfig().set("Message to send when player sit", Config.getConfig().getString("Message to send when player sit", "&aYou are now sitting. Take a break."));
            Config.getConfig().set("Message to send when player sit", null);
        }
        if (Config.getConfig().contains("Message to send if the word is disable")) {
            getConfig().set("Message to send if the word is disable", Config.getConfig().getString("Message to send if the word is disable", "&cThe Chairs is not enable in this world"));
            Config.getConfig().set("Message to send if the word is disable", null);
        }
        Config.save();
    }

    public static void reload() {
        if (messagesFile == null) {
            messagesFile = new File(plugin.getDataFolder(), "messages.yml");
        }
        messages = YamlConfiguration.loadConfiguration(messagesFile);
    }

    public static FileConfiguration getConfig() {
        if (messages == null) reload();
        return messages;
    }

    public static void save() {
        if (messages == null || messagesFile == null) {
            return;
        }

        try {
            messages.save(messagesFile);
        } catch (IOException ex) {
            plugin.getLogger().severe("Could not save messages.yml to " + messagesFile.getAbsolutePath());
            ex.printStackTrace();
        }
    }
}
