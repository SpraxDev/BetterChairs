package de.sprax2013.betterchairs;

import de.sprax2013.advanced_dev_utils.spigot.files.yaml.YAMLFile;
import de.sprax2013.advanced_dev_utils.spigot.files.yaml.YAMLFileManager;
import org.bukkit.ChatColor;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

// TODO: Create messages.yml
// TODO: Comments inside config.yml
public class Settings {
    public static final String PREFIX = "§7[§2BetterChairs§7] ", // TODO: dynamically generate default value
            PREFIX_CONSOLE = ChatColor.stripColor(PREFIX);  // TODO: Move to messages.yml (console one is hard-coded!)
    private static final int CURR_VERSION = 1;

    private static final List<SettingsReloadListener> reloadListeners = new ArrayList<>();

    /* Chair-Settings */
    public static boolean chairNeedsEmptyHands() {
        return getSettings().getCfg().getBoolean("Chairs.NeedEmptyHands");
    }

    public static boolean useStairs() {
        return getSettings().getCfg().getBoolean("Chairs.UseStairs");
    }

    public static boolean useSlabs() {
        return getSettings().getCfg().getBoolean("Chairs.UseSlabs");
    }

    public static boolean chairRegeneration() {
        return getSettings().getCfg().getBoolean("Chairs.Regeneration.Enabled");
    }

    public static int chairRegenerationAmplifier() {
        return getSettings().getCfg().getInt("Chairs.Regeneration.Amplifier");
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

        if (yamlFile.getCfg().getKeys(false).size() > 0) {
            if (!yamlFile.getCfg().contains("version")) {
                // TODO: Keys that still need to be converted:
                // Use slab (Boolean)
                // Send message when player sit (Boolean)
                // Update Checker (Boolean)
                // Send message if the chairs is already occupied (Boolean)
                // Send message if the word is disable (Boolean)
                // Disable world (StringList)
                // AutoTurn (Boolean)
                // Distance of the stairs (Int)
                // Need to sign or chair on each side (Boolean)
                // Send message if the Chairs need sign or chair (Boolean)
                // No item in hand (Boolean)
                // Regen need permission (Boolean)
                // Regen when sit (Boolean)
                // Amplifier (Int)

                backupConfig(yamlFile);

                // Generate new file
                yamlFile = YAMLFileManager.getFile(ChairManager.getPlugin(), "config.yml");

                // TODO: Convert original BetterChairs config into new one
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

        if (yamlFile.getCountOfDefaultValues() == 0) {
            yamlFile.addDefault("version", CURR_VERSION);

            yamlFile.addDefault("Chairs.NeedEmptyHands", true);
            yamlFile.addDefault("Chairs.UseStairs", true);
            yamlFile.addDefault("Chairs.UseSlabs", false);
            yamlFile.addDefault("Chairs.Regeneration.Enabled", false);
            yamlFile.addDefault("Chairs.Regeneration.Amplifier", 1);

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
        if (file.renameTo(new File(file.getParentFile(), "config-" + System.currentTimeMillis() + ".yml"))) {
            // TODO: Store console prefix in Messages.java (static/final)
            System.err.println(Settings.PREFIX_CONSOLE +
                    "Your " + yamlFile.getFile().getName() + " is invalid! Created backup: " + file.getName());
        } else {
            System.err.println(Settings.PREFIX_CONSOLE + "Could not create a copy of config.yml");
        }
    }

    public interface SettingsReloadListener {
        void onReload();
    }
}