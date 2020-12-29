package de.sprax2013.betterchairs.files;

import de.sprax2013.betterchairs.api.ChairManager;
import de.sprax2013.lime.configuration.Config;
import de.sprax2013.lime.configuration.ConfigEntry;
import de.sprax2013.lime.configuration.validation.StringEntryValidator;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.Objects;

public class Messages {
    public static final String ERR_ASYNC_API_CALL = "Async API call";
    public static final String ERR_ANOTHER_PLUGIN_PREVENTING_SPAWN = "Looks like another plugin is preventing BetterChairs from spawning chairs";
    public static final String ERR_NOT_CUSTOM_ARMOR_STAND = "The provided ArmorStand is not an instance of '%s'";

    private static final Config config = new Config(
            new File(Objects.requireNonNull(ChairManager.getPlugin()).getDataFolder(), "messages.yml"), Settings.HEADER)
            .withEntry("version", Settings.CURR_VERSION, "You shouldn't make any changes to this")
            .withCommentEntry("ToggleChairs", "What should we tell players when they enable or disable chairs for themselves");

    private static final ConfigEntry PREFIX = config.createEntry(
            "General.Prefix", "&7[&2" + Objects.requireNonNull(ChairManager.getPlugin()).getName() + "&7]",
            "The prefix that can be used in all other messages")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry NO_PERMISSION = config.createEntry(
            "General.NoPermission", "${Prefix} &cYou do not have permission to use this command!",
            "What should we tell players that are not allowed to use an command?")
            .setEntryValidator(StringEntryValidator.get());

    public static final ConfigEntry TOGGLE_ENABLED = config.createEntry(
            "ToggleChairs.Enabled", "${Prefix} &eYou now can use chairs again")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry TOGGLE_DISABLED = config.createEntry(
            "ToggleChairs.Disabled",
            "${Prefix} &eChairs are now disabled until you leave the server or run the command again")
            .setEntryValidator(StringEntryValidator.get());

    public static final ConfigEntry USE_ALREADY_OCCUPIED = config.createEntry(
            "ChairUse.AlreadyOccupied", "${Prefix} &cThis chair is already occupied",
            "What should we tell players when an chair is already occupied")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry USE_NEEDS_SIGNS = config.createEntry(
            "ChairUse.NeedsSignsOnBothSides", "${Prefix} &cA chair needs a sign attached to it on both sides",
            "What should we tell players when an chair is missing signs on both sides")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry USE_NOW_SITTING = config.createEntry(
            "ChairUse.NowSitting", "${Prefix} &cYou are taking a break now",
            "What should we tell players when he/she is now sitting")
            .setEntryValidator(StringEntryValidator.get());

    private Messages() {
        throw new IllegalStateException("Utility class");
    }

    public static Config getConfig() {
        return config;
    }

    public static String getPrefix() {
        return ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(PREFIX.getValueAsString()));
    }

    public static String getString(ConfigEntry cfgEntry) {
        return ChatColor.translateAlternateColorCodes('&',
                Objects.requireNonNull(cfgEntry.getValueAsString()))
                .replace("${Prefix}", getPrefix());
    }

    public static boolean reload() {
        return ConfigHelper.reload(config, (version, file, yaml) -> {
            if (version.equals(String.valueOf(Settings.CURR_VERSION))) return true;  // already valid

            // No version, means the file has been created by the original BetterChairs
            if (version.equals("-1")) {
                Object noPermission = yaml.get("Cant use message"),
                        toggleChairsDisabled = yaml.get("Message to send when player toggle chairs to off"),
                        toggleChairsEnabled = yaml.get("Message to send when player toggle chairs to on"),
                        chairOccupied = yaml.get("Message to send if the chairs is occupied"),
                        needsSign = yaml.get("Message to send if the chairs need sign or chair");

                // General.*
                if (noPermission instanceof String) {
                    NO_PERMISSION.setValue(noPermission);
                }

                // ToggleChairs.*
                if (toggleChairsDisabled instanceof String) {
                    TOGGLE_ENABLED.setValue(toggleChairsEnabled);
                }
                if (toggleChairsEnabled instanceof String) {
                    TOGGLE_DISABLED.setValue(toggleChairsDisabled);
                }

                // ChairUse.*
                if (chairOccupied instanceof String) {
                    USE_ALREADY_OCCUPIED.setValue(chairOccupied);
                }
                if (needsSign instanceof String) {
                    USE_NEEDS_SIGNS.setValue(needsSign);
                }

                // Override old config with the new/converted one
                config.save();
                return true;
            }

            return false;
        });
    }

    public static void reset() {
        ConfigHelper.reset(config);
    }
}