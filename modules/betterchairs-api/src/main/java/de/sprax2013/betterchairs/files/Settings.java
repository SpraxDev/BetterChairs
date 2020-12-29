package de.sprax2013.betterchairs.files;

import de.sprax2013.betterchairs.api.ChairManager;
import de.sprax2013.lime.configuration.Config;
import de.sprax2013.lime.configuration.ConfigEntry;
import de.sprax2013.lime.configuration.validation.IntEntryValidator;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Settings {
    protected static final int CURR_VERSION = 1;
    protected static final String HEADER = "BetterChairs Remastered\n\n" +
            "Support: https://Sprax.me/Discord\n" +
            "Updates and Information:\n" +
            "Statistics: https://bstats.org/plugin/bukkit/BetterChairs%20Remastered/8214\n" +
            "Information for developers: https://github.com/SpraxDev/BetterChairs/wiki";

    private static final Config config = new Config(
            new File(Objects.requireNonNull(ChairManager.getPlugin()).getDataFolder(), "config.yml"), HEADER)
            .withEntry("version", CURR_VERSION, "You shouldn't make any changes to this");

    public static final ConfigEntry ALLOWED_DISTANCE_TO_CHAIR = config.createEntry(
            "Chairs.AllowedDistanceToChair", -1,
            "Allowed distance a player is allowed to have when trying to sit? (-1 to ignore)")
            .setEntryValidator(IntEntryValidator.get());
    public static final ConfigEntry AUTO_ROTATE_PLAYER = config.createEntry(
            "Chairs.AutoRotatePlayer", true,
            "Should a player automatically look forward when starting to sit");
    public static final ConfigEntry NEEDS_EMPTY_HANDS = config.createEntry(
            "Chairs.NeedEmptyHands", true,
            "Does a player need his hands empty when trying to sit?");
    public static final ConfigEntry NEEDS_SIGNS = config.createEntry(
            "Chairs.NeedsSignsOnBothSides", false,
            "Does a chair need signs on both sides attached to be detected as an chair");
    public static final ConfigEntry IGNORES_INTERACT_PREVENTION = config.createEntry(
            "Chairs.IgnoreOtherPluginsPreventingInteract", false,
            "Enable this if you want players to be able to sit on chairs\n" +
                    "while other plugins (like WorldGuard or PlotSquared) are not\n" +
                    "allowing interactions/use with the chair blocks.");

    public static final ConfigEntry CHAIR_NEED_AIR_ABOVE = config.createEntry(
            "Chairs.Position.NeedAirAbove", true,
            "Set to false, if you do not care about a player suffocating while sitting");
    public static final ConfigEntry CHAIR_ALLOW_AIR_BELOW = config.createEntry(
            "Chairs.Position.AllowAirBelow", true,
            "Set to false, to force chairs to have a block below them");

    public static final ConfigEntry USE_STAIRS = config.createEntry(
            "Chairs.UseStairs", true, "Can stairs be chairs?");
    public static final ConfigEntry USE_SLABS = config.createEntry(
            "Chairs.UseSlabs", false, "Can half slabs be chairs too?");

    public static final ConfigEntry LEAVING_CHAIR_TELEPORT_TO_OLD_LOCATION = config.createEntry(
            "Chairs.LeavingChair.TeleportPlayerToOldLocation", true,
            "Should a player be teleported to its original position when leaving a chair");
    public static final ConfigEntry LEAVING_CHAIR_KEEP_HEAD_ROTATION = config.createEntry(
            "Chairs.LeavingChair.KeepHeadRotation", true,
            "Should a player keep his head rotation when teleported to its original position");

    public static final ConfigEntry MSG_ALREADY_OCCUPIED = config.createEntry(
            "Chairs.Messages.AlreadyOccupied", false,
            "Should the player receive a message when the chair is already occupied");
    public static final ConfigEntry MSG_NEEDS_SIGNS = config.createEntry(
            "Chairs.Messages.NeedsSignsOnBothSides", false,
            "Should the player receive a message when a chair is missing signs on both sided");
    public static final ConfigEntry MSG_NOW_SITTING = config.createEntry(
            "Chairs.Messages.NowSitting", false,
            "Should the player receive a message when he starts sitting");

    public static final ConfigEntry REGENERATION_ENABLED = config.createEntry(
            "Chairs.Regeneration.Enabled", false,
            "Should player receive regeneration effect when sitting? (Needs permission BetterChairs.regeneration)");
    public static final ConfigEntry REGENERATION_AMPLIFIER = config.createEntry(
            "Chairs.Regeneration.Amplifier", 1, "What amplifier should be applied?")
            .setEntryValidator(IntEntryValidator.get());

    public static final ConfigEntry WORLD_FILTER_ENABLED = config.createEntry(
            "Filter.Worlds.Enabled", false,
            "Should we only enable chairs in specific worlds?");
    public static final ConfigEntry WORLD_FILTER_AS_BLACKLIST = config.createEntry(
            "Filter.Worlds.UseAsBlacklist", false,
            "Should be the list below be used as blacklist or whitelist?");
    public static final ConfigEntry WORLD_FILTER_NAMES = config.createEntry(
            "Filter.Worlds.Names", new String[] {"worldname", "worldname2"},
            "List of all enabled/disabled worlds");

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
            "Should we check for new versions and report to the console? (Recommended)");
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
        return ConfigHelper.reload(config, (version, file, yaml) -> {
            if (version.equals(String.valueOf(CURR_VERSION))) return true;  // already valid

            // No version, means the file has been created by the original BetterChairs
            if (version.equals("-1")) {
                Object autoRotatePlayer = yaml.get("AutoTurn"), /* boolean */
                        checkForUpdate = yaml.get("Update Checker"), /* boolean */
                        needsEmptyHands = yaml.get("No item in hand"), /* boolean */
                        needsSignsOnBothSides = yaml.get("Need to sign or chair on each side"), /* boolean */
                        useSlabs = yaml.get("Use slab"), /* boolean */

                        sendMsgWhenChairNeedsSigns = yaml.get("Send message if the Chairs need sign or chair"), /* boolean */
                        sendMsgWhenChairOccupied = yaml.get("Send message if the chairs is already occupied"), /* boolean */
                        sendMsgWhenPlayerSit = yaml.get("Send message when player sit"), /* boolean */

                        regenerationAmplifier = yaml.get("Amplifier"), /* int */
                        regenerationWhenSitting = yaml.get("Regen when sit"), /* boolean */

                        allowedDistanceToStairs = yaml.get("Distance of the stairs"), /* int */
                        disabledWorlds = yaml.get("Disable world"); /* List<String> */

                // Chairs.*
                if (allowedDistanceToStairs instanceof Integer) {
                    ALLOWED_DISTANCE_TO_CHAIR.setValue(allowedDistanceToStairs);
                }
                if (autoRotatePlayer instanceof Boolean) {
                    AUTO_ROTATE_PLAYER.setValue(autoRotatePlayer);
                }
                if (needsEmptyHands instanceof Boolean) {
                    NEEDS_EMPTY_HANDS.setValue(needsEmptyHands);
                }
                if (needsSignsOnBothSides instanceof Boolean) {
                    NEEDS_SIGNS.setValue(needsSignsOnBothSides);
                }
                if (useSlabs instanceof Boolean) {
                    USE_SLABS.setValue(useSlabs);
                }

                // Chairs.Messages.*
                if (sendMsgWhenChairOccupied instanceof Boolean) {
                    MSG_ALREADY_OCCUPIED.setValue(sendMsgWhenChairOccupied);
                }
                if (sendMsgWhenChairNeedsSigns instanceof Boolean) {
                    MSG_NEEDS_SIGNS.setValue(sendMsgWhenChairNeedsSigns);
                }
                if (sendMsgWhenPlayerSit instanceof Boolean) {
                    MSG_NOW_SITTING.setValue(sendMsgWhenPlayerSit);
                }

                // Chairs.Regeneration.*
                if (regenerationWhenSitting instanceof Boolean) {
                    REGENERATION_ENABLED.setValue(regenerationWhenSitting);
                }
                if (regenerationAmplifier instanceof Integer) {
                    REGENERATION_AMPLIFIER.setValue(regenerationAmplifier);
                }

                // Filter.Worlds.Names
                if (disabledWorlds instanceof List) {
                    List<String> newDisabledWorlds = new ArrayList<>();

                    //noinspection rawtypes
                    for (Object obj : (List) disabledWorlds) {
                        newDisabledWorlds.add(obj.toString());
                    }

                    WORLD_FILTER_NAMES.setValue(newDisabledWorlds);
                }

                // Updater.CheckForUpdates
                if (checkForUpdate instanceof Boolean) {
                    UPDATER_ENABLED.setValue(checkForUpdate);
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

    protected interface ConfigUpgradeTask {
        /**
         * This method is called <strong>after</strong> a backup file has been created
         * Returning {@code false}, indicates that the upgrade failed and that the current config should be replaced with the default one
         *
         * @param version The current config version or {@code "-1"} if none set
         * @param file    The config file
         * @param yaml    The current YAML-File
         *
         * @return true on success, false on failure
         */
        boolean doUpgrade(String version, File file, YamlConfiguration yaml);
    }
}