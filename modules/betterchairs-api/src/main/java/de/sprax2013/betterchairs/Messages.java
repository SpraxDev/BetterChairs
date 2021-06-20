package de.sprax2013.betterchairs;

import de.sprax2013.lime.configuration.Config;
import de.sprax2013.lime.configuration.ConfigEntry;
import de.sprax2013.lime.configuration.validation.StringEntryValidator;
import net.md_5.bungee.api.ChatColor;

import java.io.File;
import java.util.Objects;

public class Messages {
    private static final int LATEST_VERSION = 1;

    public static final String ERR_ASYNC_API_CALL = "Async API call";
    public static final String ERR_ANOTHER_PLUGIN_PREVENTING_SPAWN = "Looks like another plugin is preventing BetterChairs from spawning chairs";
    public static final String ERR_NOT_CUSTOM_ARMOR_STAND = "The provided ArmorStand is not an instance of '%s'";

    private static final Config config = new Config(
            new File(Objects.requireNonNull(ChairManager.getPlugin()).getDataFolder(), "messages.yml"), Settings.HEADER)
            .withVersion(LATEST_VERSION, 0, "version", () -> "You shouldn't make any changes to this")
            .withCommentEntry("ToggleChairs", "What should we tell players when they enable or disable chairs for themselves");

    private static final ConfigEntry PREFIX = config.createEntry(
                    "General.Prefix", "&7[&2" + Objects.requireNonNull(ChairManager.getPlugin()).getName() + "&7]",
                    "The prefix that can be used in all other messages")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry NO_PERMISSION = config.createEntry(
                    "General.NoPermission", "${Prefix} &cYou do not have permission to use this command!",
                    "What should we tell players that are not allowed to use an command?")
            .setEntryValidator(StringEntryValidator.get())
            .setLegacyKey(0, "Cant use message");

    public static final ConfigEntry TOGGLE_ENABLED = config.createEntry(
                    "ToggleChairs.Enabled", "${Prefix} &eYou can now use chairs again")
            .setEntryValidator(StringEntryValidator.get())
            .setLegacyKey(0, "Message to send when player toggle chairs to on");
    public static final ConfigEntry TOGGLE_DISABLED = config.createEntry(
                    "ToggleChairs.Disabled", "${Prefix} &eChairs are now disabled for you")
            .setEntryValidator(StringEntryValidator.get())
            .setLegacyKey(0, "Message to send when player toggle chairs to off");
    public static final ConfigEntry TOGGLE_STATUS_ENABLED = config.createEntry(
                    "ToggleChairs.Status.Enabled", "${Prefix} &eYou currently have chairs&a enabled")
            .setEntryValidator(StringEntryValidator.get());
    public static final ConfigEntry TOGGLE_STATUS_DISABLED = config.createEntry(
                    "ToggleChairs.Status.Disabled", "${Prefix} &eYou currently have chairs &4disabled")
            .setEntryValidator(StringEntryValidator.get());

    public static final ConfigEntry USE_ALREADY_OCCUPIED = config.createEntry(
                    "ChairUse.AlreadyOccupied", "${Prefix} &cThis chair is already occupied",
                    "What should we tell players when an chair is already occupied")
            .setEntryValidator(StringEntryValidator.get())
            .setLegacyKey(0, "Message to send if the chairs is occupied");
    public static final ConfigEntry USE_NEEDS_SIGNS = config.createEntry(
                    "ChairUse.NeedsSignsOnBothSides", "${Prefix} &cA chair needs a sign attached to it on both sides",
                    "What should we tell players when an chair is missing signs on both sides")
            .setEntryValidator(StringEntryValidator.get())
            .setLegacyKey(0, "Message to send if the chairs need sign or chair");
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
        return ConfigHelper.reload(config);
    }

    public static void reset() {
        ConfigHelper.reset(config);
    }
}