package de.sprax2013.betterchairs;

import de.sprax2013.lime.configuration.Config;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class ConfigHelper {
    private ConfigHelper() {
        throw new IllegalStateException("Utility class");
    }

    static boolean reload(Config cfg, Settings.ConfigUpgradeTask upgradeTask) {
        File cfgFile = cfg.getFile();

        boolean loaded = false;

        if (cfgFile != null && cfgFile.exists()) {
            YamlConfiguration yamlCfg = YamlConfiguration.loadConfiguration(cfgFile);

            String version = yamlCfg.getString("version", "-1");

            if (!version.equals(String.valueOf(Settings.CURR_VERSION))) {
                // Convert from old config or delete when upgrade failed (=invalid version)
                try {
                    ChairManager.getLogger()
                            .info("Found old BetterChairs " + cfgFile.getName() + " - Converting into new format...");

                    cfg.backupFile();
                    loaded = upgradeTask.doUpgrade(version, cfgFile, yamlCfg);

                    if (!loaded) {
                        Files.deleteIfExists(cfgFile.toPath());

                        throw new IllegalStateException("Invalid version (=" + version + ") provided inside " + cfgFile.getName());
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
        }

        // If loaded has been set to true, we don't need to load the file again
        return loaded || (cfg.load() && cfg.save());
    }

    static void reset(Config cfg) {
        cfg.clearListeners();
        cfg.reset();
    }
}
