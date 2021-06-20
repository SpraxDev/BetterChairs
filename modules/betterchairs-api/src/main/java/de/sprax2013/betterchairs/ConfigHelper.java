package de.sprax2013.betterchairs;

import de.sprax2013.lime.configuration.Config;

public class ConfigHelper {
    private ConfigHelper() {
        throw new IllegalStateException("Utility class");
    }

    static boolean reload(Config cfg) {
        return cfg.load() && cfg.save();
    }

    static void reset(Config cfg) {
        cfg.clearListeners();
        cfg.reset();
    }
}