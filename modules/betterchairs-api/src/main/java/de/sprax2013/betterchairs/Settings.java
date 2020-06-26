package de.sprax2013.betterchairs;

import de.sprax2013.advanced_dev_utils.spigot.files.yaml.YAMLFile;
import de.sprax2013.advanced_dev_utils.spigot.files.yaml.YAMLFileManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// TODO: Comments inside config.yml
public class Settings {
    private static final int CURR_VERSION = 1;

    private static final List<SettingsReloadListener> reloadListeners = new ArrayList<>();

    /* Chair-Settings */
    public static int allowedDistance() {
        return getSettings().getCfg().getInt("Chairs.AllowedDistanceToChair");
    }

    public static boolean autoRotate() {
        return getSettings().getCfg().getBoolean("Chairs.AutoRotatePlayer");
    }

    public static boolean needsEmptyHands() {
        return getSettings().getCfg().getBoolean("Chairs.NeedEmptyHands");
    }

    public static boolean needsSignsOnBothSides() {
        return getSettings().getCfg().getBoolean("Chairs.NeedsSignsOnBothSides");
    }

    public static boolean useSlabs() {
        return getSettings().getCfg().getBoolean("Chairs.UseSlabs");
    }

    public static boolean useStairs() {
        return getSettings().getCfg().getBoolean("Chairs.UseStairs");
    }

    public static boolean leavingChairTeleportPlayerToOldLocation() {
        return getSettings().getCfg().getBoolean("Chairs.LeavingChair.TeleportPlayerToOldLocation");
    }

    public static boolean leavingChairKeepHeadRotation() {
        return getSettings().getCfg().getBoolean("Chairs.LeavingChair.KeepHeadRotation");
    }

    public static boolean sendMessageWhenOccupied() {
        return getSettings().getCfg().getBoolean("Chairs.Messages.AlreadyOccupied");
    }

    public static boolean sendMessageWhenNeedsSignsOnBothSides() {
        return getSettings().getCfg().getBoolean("Chairs.Messages.NeedsSignsOnBothSides");
    }

    public static int chairRegenerationAmplifier() {
        return getSettings().getCfg().getInt("Chairs.Regeneration.Amplifier");
    }

    public static boolean chairRegeneration() {
        return getSettings().getCfg().getBoolean("Chairs.Regeneration.Enabled");
    }

    /* Filter: Worlds */
    public static boolean isWorldFilterEnabled() {
        return getSettings().getCfg().getBoolean("Filter.Worlds.Enabled");
    }

    public static boolean isWorldFilterBlacklist() {
        return getSettings().getCfg().getBoolean("Filter.Worlds.UseAsBlacklist");
    }

    public static List<String> getWorldFilter() {
        return getSettings().getCfg().getStringList("Filter.Worlds.Names");
    }

    /* Updater */
    public static boolean checkForUpdates() {
        return getSettings().getCfg().getBoolean("Updater.CheckForUpdates");
    }

    public static boolean updaterNotifyOnJoin() {
        return getSettings().getCfg().getBoolean("Updater.NotifyOnJoin");
    }

    private static YAMLFile getSettings() {
        YAMLFile yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "config.yml");

        boolean save = false;

        // Convert from old config or delete when invalid version
        if (yamlFile.getCfg().getKeys(false).size() > 0) {
            if (!yamlFile.getCfg().contains("version")) {
                System.out.println(Messages.PREFIX_CONSOLE + "Found old BetterChairs config.yml - Converting into new format...");

                Object autoRotatePlayer = yamlFile.getCfg().get("AutoTurn"), /* boolean */
                        checkForUpdate = yamlFile.getCfg().get("Update Checker"), /* boolean */
                        needsEmptyHands = yamlFile.getCfg().get("No item in hand"), /* boolean */
                        needsSignsOnBothSides = yamlFile.getCfg().get("Need to sign or chair on each side"), /* boolean */
                        useSlabs = yamlFile.getCfg().get("Use slab"), /* boolean */

                        sendMsgWhenChairNeedsSigns = yamlFile.getCfg().get("Send message if the Chairs need sign or chair"), /* boolean */
                        sendMsgWhenChairOccupied = yamlFile.getCfg().get("Send message if the chairs is already occupied"), /* boolean */

                        regenerationAmplifier = yamlFile.getCfg().get("Amplifier"), /* int */
                        regenerationWhenSitting = yamlFile.getCfg().get("Regen when sit"), /* boolean */

                        allowedDistanceToStairs = yamlFile.getCfg().get("Distance of the stairs"), /* int */
                        disabledWorlds = yamlFile.getCfg().get("Disable world"); /* List<String> */

                backupConfig(yamlFile);

                // Generate new file
                yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "config.yml");

                yamlFile.getCfg().set("version", CURR_VERSION);

                // Chairs.*
                if (allowedDistanceToStairs instanceof Integer) {
                    yamlFile.getCfg().set("Chairs.AllowedDistanceToChair", allowedDistanceToStairs);
                }
                if (autoRotatePlayer instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.AutoRotatePlayer", autoRotatePlayer);
                }
                if (needsEmptyHands instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.NeedEmptyHands", needsEmptyHands);
                }
                if (needsSignsOnBothSides instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.NeedsSignsOnBothSides", needsSignsOnBothSides);
                }
                if (useSlabs instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.UseSlabs", useSlabs);
                }

                // Chairs.Messages.*
                if (sendMsgWhenChairOccupied instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.Messages.AlreadyOccupied", sendMsgWhenChairOccupied);
                }
                if (sendMsgWhenChairNeedsSigns instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.Messages.NeedsSignsOnBothSides", sendMsgWhenChairNeedsSigns);
                }

                // Chairs.Regeneration.*
                if (regenerationAmplifier instanceof Integer) {
                    yamlFile.getCfg().set("Chairs.Regeneration.Amplifier", regenerationAmplifier);
                }
                if (regenerationWhenSitting instanceof Boolean) {
                    yamlFile.getCfg().set("Chairs.Regeneration.Enabled", regenerationWhenSitting);
                }

                // Filter.Worlds.Names
                if (disabledWorlds instanceof List) {
                    List<String> newDisabledWorlds = new ArrayList<>();

                    //noinspection rawtypes
                    for (Object obj : (List) disabledWorlds) {
                        newDisabledWorlds.add(obj.toString());
                    }

                    yamlFile.getCfg().set("Filter.Worlds.Names", newDisabledWorlds);
                }

                // Updater.CheckForUpdates
                if (checkForUpdate instanceof Boolean) {
                    yamlFile.getCfg().set("Updater.CheckForUpdates", checkForUpdate);
                }

                save = true;
            } else {
                String verStr = yamlFile.getCfg().getString("version");

                try {
                    int ver = Integer.parseInt(verStr);

                    if (ver != 1) {
                        throw new Exception("Invalid version (=" + ver + ") provided inside config.yml");
                    }
                } catch (Exception ex) {
                    backupConfig(yamlFile);

                    // Generate new file
                    yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "config.yml");
                }
            }
        }

        // Insert default values
        if (yamlFile.getCountOfDefaultValues() == 0) {
            yamlFile.addDefault("version", CURR_VERSION);

            yamlFile.addDefault("Chairs.AllowedDistanceToChair", -1);
            yamlFile.addDefault("Chairs.AutoRotatePlayer", true);
            yamlFile.addDefault("Chairs.NeedEmptyHands", true);
            yamlFile.addDefault("Chairs.NeedsSignsOnBothSides", false);
            yamlFile.addDefault("Chairs.UseSlabs", false);
            yamlFile.addDefault("Chairs.UseStairs", true);

            yamlFile.addDefault("Chairs.LeavingChair.TeleportPlayerToOldLocation", true);
            yamlFile.addDefault("Chairs.LeavingChair.KeepHeadRotation", true);

            yamlFile.addDefault("Chairs.Messages.AlreadyOccupied", false);
            yamlFile.addDefault("Chairs.Messages.NeedsSignsOnBothSides", false);

            yamlFile.addDefault("Chairs.Regeneration.Amplifier", 1);
            yamlFile.addDefault("Chairs.Regeneration.Enabled", false);

            yamlFile.addDefault("Filter.Worlds.Enabled", false);
            yamlFile.addDefault("Filter.Worlds.UseAsBlacklist", true);
            yamlFile.addDefault("Filter.Worlds.Names", new String[] {"worldname", "worldname2"});

            yamlFile.addDefault("Updater.CheckForUpdates", true);
            yamlFile.addDefault("Updater.NotifyOnJoin", true);

            save = true;
        }

        if (save) {
            yamlFile.save();
        }

        return yamlFile;
    }

    protected static boolean reload() {
        boolean result = getSettings().refresh();

        if (result) {
            for (SettingsReloadListener listener : reloadListeners) {
                try {
                    listener.onReload();
                } catch (Throwable th) {
                    th.printStackTrace();
                }
            }
        }

        return result;
    }

    protected static void reset() {
        reloadListeners.clear();
        YAMLFileManager.removeFileFromCache(ChairManager.getPlugin(), getSettings());
    }

    protected static void addReloadListener(SettingsReloadListener reloadListener) {
        reloadListeners.add(reloadListener);
    }

    private static void backupConfig(YAMLFile yamlFile) {
        File file = yamlFile.getFile();

        YAMLFileManager.removeFileFromCache(ChairManager.getPlugin(), yamlFile);

        // Backup file
        File newFile = new File(file.getParentFile(), "config-" + System.currentTimeMillis() + ".yml");
        if (file.renameTo(newFile)) {
            // TODO: Store console prefix in Messages.java (static/final)
            System.out.println(Messages.PREFIX_CONSOLE +
                    "Created backup of " + file.getName() + ": " + newFile.getName());
        } else {
            System.err.println(Messages.PREFIX_CONSOLE + "Could not create a backup of " + file.getName());
        }
    }

    public interface SettingsReloadListener {
        void onReload();
    }
}