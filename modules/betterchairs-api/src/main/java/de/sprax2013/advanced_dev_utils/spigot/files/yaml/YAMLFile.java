package de.sprax2013.advanced_dev_utils.spigot.files.yaml;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Using my old FileManager for Settings - Needs a recode
 */
public class YAMLFile {
    JavaPlugin plugin;

    private final File file;
    private FileConfiguration cfg;

    private final HashMap<String, Object> defaultValues = new HashMap<>();

    /**
     * Instantiates a new YAMLFile.
     *
     * @param plugin Your plugin
     * @param file   The file, where the {@link YAMLFile} should be written to.
     */
    public YAMLFile(JavaPlugin plugin, File file) {
        this.plugin = plugin;
        this.file = file;

        refresh();
    }

    /**
     * @return The plugin witch instantiated this object
     */
    public JavaPlugin getPlugin() {
        return plugin;
    }

    /**
     * @return The file
     */
    public File getFile() {
        return file;
    }

    /**
     * Use this object to get values from file
     *
     * @return The FileConfiguration-Object
     */
    public FileConfiguration getCfg() {
        return cfg;
    }

    /**
     * Use this method to add default values that apply in case no value is set.<br>
     * They will be stored in the file<br>
     * You have to call one of the save methods to save them to file.
     *
     * @return This YAMLFile
     *
     * @see #save()
     * @see #saveNonBoolean()
     */
    public YAMLFile addDefault(String key, Object obj) {
        defaultValues.put(key, obj);

        insertDefaultValue(key, obj);

        return this;
    }

    /**
     * You can use this method to determine if already default values has been set.
     *
     * @return Count of default values set
     */
    public int getCountOfDefaultValues() {
        return defaultValues.keySet().size();
    }

    /**
     * This method will re-parse the file.<br>
     * Use this mehthod if changes to the file were made
     *
     * @return false if an exception occurred
     */
    public boolean refresh() {
        if (!file.exists()) {
            //noinspection ResultOfMethodCallIgnored
            file.getParentFile().mkdirs();

            try {
                //noinspection ResultOfMethodCallIgnored
                file.createNewFile();
            } catch (IOException ex) {
                getPlugin().getLogger().warning(() -> "Could not create YAML-File " + file.getAbsolutePath() + " (" + ex.getMessage() + ")");

                cfg = null;

                return false;
            }
        }

        cfg = YamlConfiguration.loadConfiguration(file);

        insertDefaultValues();

        return true;
    }

    /**
     * Saves all keys and values to the file
     *
     * @return false when an exception occurred
     */
    public boolean save() {
        try {
            cfg.save(file);

            return true;
        } catch (IOException ex) {
            getPlugin().getLogger().warning(() -> "Could not save YAML-File " + file.getAbsolutePath() + " (" + ex.getMessage() + ")");
        }

        return false;
    }

    /**
     * Does the exact same as {@link #save()}!<br>
     * This method only returns <code>YAMLFile</code> instead of boolean
     *
     * @return This YAMLFile
     */
    public YAMLFile saveNonBoolean() {
        save();

        return this;
    }

    /**
     * Puts a default value into the {@link FileConfiguration} if the key does not
     * already exist
     */
    private void insertDefaultValue(String key, Object obj) {
        if (!getCfg().contains(key)) {
            getCfg().set(key, obj);
        }
    }

    /**
     * Puts default values into the {@link FileConfiguration} if the key does not
     * already exist<br>
     * This method will save the file automatically
     *
     * @return false when an exception occurred while saving
     */
    private boolean insertDefaultValues() {
        if (!defaultValues.isEmpty()) {
            for (Map.Entry<String, Object> entry : defaultValues.entrySet()) {
                if (!getCfg().contains(entry.getKey())) {
                    getCfg().set(entry.getKey(), entry.getValue());
                }
            }

            return save();
        }

        return true;
    }
}