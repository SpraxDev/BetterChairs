package de.sprax2013.advanced_dev_utils.spigot.files.yaml;

import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Using my old FileManager for Settings - Needs a recode
 */
public class YAMLFileManager {
    private static final HashMap<JavaPlugin, HashMap<File, YAMLFile>> files = new HashMap<>();

    /**
     * Creates a file inside the plugin's DataFolder
     * <p>
     * Short for:
     * <code>getFile(<i>plugin</i>, new File(plugin.getDataFolder(), fileName));</code>
     *
     * @param plugin   The plugin
     * @param fileName Filename
     *
     * @return A YAMLFile-Object representing the file
     *
     * @see #getFile(JavaPlugin, File)
     */
    public static YAMLFile getFile(JavaPlugin plugin, String fileName) {
        return getFile(plugin, new File(plugin.getDataFolder(), fileName));
    }

    /**
     * Creates a file and instantiates a YAMLFile-Object representing it.
     *
     * @param plugin The plugin
     * @param file   The file
     *
     * @return A YAMLFile-Object representing the file
     *
     * @see #getFile(JavaPlugin, String)
     */
    public static YAMLFile getFile(JavaPlugin plugin, File file) {
        if (files.containsKey(plugin) && files.get(plugin).containsKey(file)) {
            return files.get(plugin).get(file);
        }

        HashMap<File, YAMLFile> map = files.getOrDefault(plugin, new HashMap<>());
        YAMLFile yamlF = new YAMLFile(plugin, file);

        map.put(file, yamlF);

        files.put(plugin, map);

        return yamlF;
    }

    /**
     * Returns a list of cached YAMLFile-Objects created by a specific plugin.
     *
     * @param plugin The plugin
     *
     * @return A list of files
     */
    public static List<YAMLFile> getFiles(JavaPlugin plugin) {
        return new ArrayList<>(files.getOrDefault(plugin, new HashMap<>()).values());
    }

    public static boolean removeFileFromCache(JavaPlugin plugin, YAMLFile file) {
        if (files.getOrDefault(plugin, new HashMap<>()).containsValue(file)) {
            files.get(plugin).remove(file.getFile());
            return true;
        }

        return false;
    }
}