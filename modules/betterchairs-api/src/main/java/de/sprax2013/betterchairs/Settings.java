package de.sprax2013.betterchairs;

import de.sprax2013.lime.configuration.Config;
import de.sprax2013.lime.configuration.ConfigEntry;
import de.sprax2013.lime.configuration.validation.IntEntryValidator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Settings {
    private static final int LATEST_VERSION = 2;
    protected static final String HEADER = "BetterChairs Remastered\n\n" +
            "Support: https://Sprax.me/Discord\n" +
            "Updates and Information:\n" +
            "Statistics: https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214\n" +
            "Information for developers: https://github.com/SpraxDev/BetterChairs/wiki";

    private static final Config config = new Config(
            new File(Objects.requireNonNull(ChairManager.getPlugin()).getDataFolder(), "config.yml"), HEADER)
            .withVersion(LATEST_VERSION, 0, "version", () -> "You shouldn't make any changes to this");

    public static final ConfigEntry ALLOWED_DISTANCE_TO_CHAIR = config.createEntry(
                    "Chairs.AllowedDistanceToChair", -1,
                    "Allowed distance a player is allowed to have when trying to sit? (-1 to ignore)")
            .setEntryValidator(IntEntryValidator.get())
            .setLegacyKey(0, "Distance of the stairs");
    public static final ConfigEntry AUTO_ROTATE_PLAYER = config.createEntry(
                    "Chairs.AutoRotatePlayer", true,
                    "Should a player automatically look forward when starting to sit")
            .setLegacyKey(0, "AutoTurn");
    public static final ConfigEntry NEEDS_EMPTY_HANDS = config.createEntry(
                    "Chairs.NeedEmptyHands", true,
                    "Does a player need his hands empty when trying to sit?")
            .setLegacyKey(0, "No item in hand");
    public static final ConfigEntry NEEDS_SIGNS = config.createEntry(
                    "Chairs.NeedsSignsOnBothSides", false,
                    "Does a chair need signs on both sides attached to be detected as an chair")
            .setLegacyKey(0, "Need to sign or chair on each side");
    public static final ConfigEntry IGNORES_INTERACT_PREVENTION = config.createEntry(
            "Chairs.IgnoreOtherPluginsPreventingInteract", false,
            "Enable this if you want players to be able to sit on chairs\n" +
                    "while other plugins (like WorldGuard or PlotSquared) are not\n" +
                    "allowing interactions/use with the chair blocks.");
    public static final ConfigEntry REMEMBER_IF_PLAYER_DISABLED_CHAIRS = config.createEntry(
                    "Chairs.RememberIfPlayerDisabledChairsAfterRelogin", true,
                    "Enable this if you want BetterChairs to remember a player who used /bc <toggle|on|off> after a plugin reload or him rejoining")
            .setLegacyKey(1, null, (value) -> {
                if (value != null) {
                    return value;
                }

                return false;
            });

    public static final ConfigEntry CHAIR_NEED_AIR_ABOVE = config.createEntry(
            "Chairs.Position.NeedAirAbove", true,
            "Set to false, if you do not care about a player suffocating while sitting");
    public static final ConfigEntry CHAIR_ALLOW_AIR_BELOW = config.createEntry(
            "Chairs.Position.AllowAirBelow", true,
            "Set to false, to force chairs to have a block below them");

    public static final ConfigEntry USE_STAIRS = config.createEntry(
            "Chairs.UseStairs", true, "Can stairs be chairs?");
    public static final ConfigEntry USE_SLABS = config.createEntry(
                    "Chairs.UseSlabs", false, "Can half slabs be chairs too?")
            .setLegacyKey(0, "Use slab");

    public static final ConfigEntry LEAVING_CHAIR_TELEPORT_TO_OLD_LOCATION = config.createEntry(
            "Chairs.LeavingChair.TeleportPlayerToOldLocation", true,
            "Should a player be teleported to its original position when leaving a chair");
    public static final ConfigEntry LEAVING_CHAIR_KEEP_HEAD_ROTATION = config.createEntry(
            "Chairs.LeavingChair.KeepHeadRotation", true,
            "Should a player keep his head rotation when teleported to its original position");

    public static final ConfigEntry MSG_ALREADY_OCCUPIED = config.createEntry(
                    "Chairs.Messages.AlreadyOccupied", false,
                    "Should the player receive a message when the chair is already occupied")
            .setLegacyKey(0, "Send message if the chairs is already occupied");
    public static final ConfigEntry MSG_NEEDS_SIGNS = config.createEntry(
                    "Chairs.Messages.NeedsSignsOnBothSides", false,
                    "Should the player receive a message when a chair is missing signs on both sided")
            .setLegacyKey(0, "Send message if the Chairs need sign or chair");
    public static final ConfigEntry MSG_NOW_SITTING = config.createEntry(
                    "Chairs.Messages.NowSitting", false,
                    "Should the player receive a message when he starts sitting")
            .setLegacyKey(0, "Send message when player sit");

    public static final ConfigEntry REGENERATION_ENABLED = config.createEntry(
                    "Chairs.Regeneration.Enabled", false,
                    "Should player receive regeneration effect when sitting? (Needs permission BetterChairs.regeneration)")
            .setLegacyKey(0, "Regen when sit");
    public static final ConfigEntry REGENERATION_AMPLIFIER = config.createEntry(
                    "Chairs.Regeneration.Amplifier", 1, "What amplifier should be applied?")
            .setEntryValidator(IntEntryValidator.get())
            .setLegacyKey(0, "Amplifier");

    public static final ConfigEntry WORLD_FILTER_ENABLED = config.createEntry(
            "Filter.Worlds.Enabled", false,
            "Should we only enable chairs in specific worlds?");
    public static final ConfigEntry WORLD_FILTER_AS_BLACKLIST = config.createEntry(
            "Filter.Worlds.UseAsBlacklist", false,
            "Should be the list below be used as blacklist or whitelist?");
    public static final ConfigEntry WORLD_FILTER_NAMES = config.createEntry(
                    "Filter.Worlds.Names", new String[] {"worldname", "worldname2"},
                    "List of all enabled/disabled worlds")
            .setLegacyKey(0, "Disable world", value -> {
                if (value instanceof List) {
                    List<String> newDisabledWorlds = new ArrayList<>();

                    //noinspection rawtypes
                    for (Object obj : (List) value) {
                        newDisabledWorlds.add(obj.toString());
                    }

                    return newDisabledWorlds;
                }

                return null;
            });

    public static final ConfigEntry MATERIAL_FILTER_ENABLED = config.createEntry(
            "Filter.Blocks.Enabled", false,
            "Should we only enable specific blocks as chairs?");
    public static final ConfigEntry MATERIAL_FILTER_ALLOW_ALL_TYPES = config.createEntry(
            "Filter.Blocks.AllowAllTypes", false,
            "Setting this to true, won't check if a chair\n" +
                    "is a stair or slab but only look if it is in the list below\n\n" +
                    "This is kinda experimental.\nEnabling overwrites 'UseStairs' and 'UseSlabs' further above");
    public static final ConfigEntry MATERIAL_FILTER_AS_BLACKLIST = config.createEntry(
            "Filter.Blocks.UseAsBlacklist", false,
            "Should be the list below be used as blacklist or whitelist?");
    public static final ConfigEntry MATERIAL_FILTER_NAMES = config.createEntry(
            "Filter.Blocks.Names", new String[] {"blockname", "blockname2"},
            "List of all enabled/disabled block types\n\n" +
                    "The names from Minecraft do not always work\n" +
                    "Full list: https://hub.spigotmc.org/javadocs/spigot/org/bukkit/Material.html");

    public static final ConfigEntry UPDATER_ENABLED = config.createEntry(
                    "Updater.CheckForUpdates", true,
                    "Should we check for new versions and report to the console? (Recommended)")
            .setLegacyKey(0, "Update Checker");
    public static final ConfigEntry UPDATER_NOTIFY_ON_JOIN = config.createEntry(
            "Updater.NotifyOnJoin", true,
            () -> "Should be notify admins when they join the server? (Permission: " +
                    ChairManager.getPlugin().getName() + ".updater)");

    private Settings() {
        throw new IllegalStateException("Utility class");
    }

    public static Config getConfig() {
        return config;
    }

    public static boolean reload() {
        return ConfigHelper.reload(config);
    }

    public static void reset() {
        ConfigHelper.reset(config);
    }
}