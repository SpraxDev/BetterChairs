package de.sprax2013.betterchairs;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Timer;
import java.util.TimerTask;

public class Updater implements Listener {
    public static final String VERSION_TXT_URL = "https://spraxdev.github.io/BetterChairs/version.txt";

    public static final String SPIGOT_MC_URL = "https://r.spiget.org/84809";
    public static final String CRAFTARO_URL = "https://craftaro.com/marketplace/product/489";
    public static final String GITHUB_URL = "https://github.com/SpraxDev/BetterChairs/releases";

    public static final String CHANGELOG_URL = "https://github.com/SpraxDev/BetterChairs/blob/master/CHANGELOG.md";

    private final JavaPlugin plugin;

    private Timer timer;
    private String newerVersion;

    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;

        Settings.getConfig().addListener(this::reInit);

        reInit();
    }

    private void reInit() {
        if (Settings.UPDATER_ENABLED.getValueAsBoolean()) {
            if (this.timer != null) return;

            this.timer = new Timer(true);
            this.timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (Updater.this.plugin.isEnabled() && Settings.UPDATER_ENABLED.getValueAsBoolean()) {
                        try {
                            checkForUpdates();
                        } catch (Exception ex) {
                            ChairManager.getLogger()
                                    .warning("Could not check for updates" +
                                            (ex.getMessage() == null ? "!" : ": " + ex.getMessage()));
                        }
                    } else {
                        Updater.this.timer.cancel();
                    }
                }
            }, 2000, 1000 * 60 * 60 * 3);   // Check every 3h

            Bukkit.getPluginManager().registerEvents(this, this.plugin);
        } else if (this.timer != null) {
            // Stop Update-Timer
            HandlerList.unregisterAll(this);
            this.timer.cancel();
            this.timer = null;
        }
    }

    private void checkForUpdates() throws IOException {
        URL website = new URL(VERSION_TXT_URL);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder versionTxt = new StringBuilder();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            versionTxt.append(inputLine.trim());
        }
        in.close();

        String versionStr = versionTxt.toString().split("\n")[0];
        String currVersion = this.plugin.getDescription().getVersion();

        if (isNewerVersion(currVersion, versionStr)) {
            this.newerVersion = versionStr;

            ChairManager.getLogger()
                    .info(() -> String.format("Found a new update v%s -> v%s — Download the update from: %nSpigotMC: %s%nCraftaro: %s%nGitHub: %s%n%nChangelog: %s",
                            currVersion, versionStr, SPIGOT_MC_URL, CRAFTARO_URL, GITHUB_URL, getChangelogUrl(versionStr)));
        } else {
            this.newerVersion = null;
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (this.newerVersion == null) return;
        if (!Settings.UPDATER_ENABLED.getValueAsBoolean()) return;
        if (!Settings.UPDATER_NOTIFY_ON_JOIN.getValueAsBoolean()) return;
        if (!e.getPlayer().hasPermission(this.plugin.getName() + ".updater")) return;

        e.getPlayer().spigot().sendMessage(
                new ComponentBuilder("[").color(ChatColor.GRAY)
                        .append(BetterChairsPlugin.getInstance().getName()).color(ChatColor.GOLD)
                        .append("] ").color(ChatColor.GRAY)
                        .append("Found a new update v" +
                                this.plugin.getDescription().getVersion() + " -> v" + this.newerVersion).color(ChatColor.YELLOW)
                        .append(" [SpigotMC] ")
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, SPIGOT_MC_URL))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§2Click to visit the download page on SpigotMC")))
                        .append("[Craftaro] ")
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, CRAFTARO_URL))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§2Click to visit the download page on Craftaro")))
                        .append("[GitHub] ")
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_URL))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§2Click to visit the download page on GitHub")))
                        .append("[Changelog]")
                        .color(ChatColor.DARK_GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, getChangelogUrl(this.newerVersion)))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§aClick to see the Changelogs at GitHub.com")))
                        .create());
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin() == this.plugin) {
            this.timer.cancel();
        }
    }

    /**
     * Check if {@code ver1} is newer that {@code ver2}.<br>
     * Expects <a href="https://semver.org/">SemVer</a>
     *
     * @return true, if {@code ver1} is newer than {@code ver2}, false otherwise
     */
    public boolean isNewerVersion(String ver1, String ver2) {
        int[] ver1Num;
        int[] ver2Num;

        String[] ver1Args = ver1.split("-")[0].split("\\.");
        String[] ver2Args = ver2.split("-")[0].split("\\.");

        ver1Num = new int[ver1Args.length];
        for (int i = 0; i < ver1Args.length; i++) {
            ver1Num[i] = Integer.parseInt(ver1Args[i]);
        }

        ver2Num = new int[ver2Args.length];
        for (int i = 0; i < ver2Args.length; i++) {
            ver2Num[i] = Integer.parseInt(ver2Args[i]);
        }

        for (int i = 0; i < Math.max(ver1Num.length, ver2Num.length); i++) {
            int left = i < ver1Num.length ? ver1Num[i] : 0;
            int right = i < ver2Num.length ? ver2Num[i] : 0;

            if (left != right) {
                return left < right;
            }
        }

        if (ver1.contains("-")) {
            if (!ver2.contains("-"))
                return true; // true, if they have same SemVer but ver1 has a suffix attached while ver2 does not

            String suffix1 = ver1.substring(ver1.lastIndexOf('-'));
            String suffix2 = ver2.substring(ver2.lastIndexOf('-'));

            return !suffix1.equals(suffix2);    // true, if versions have suffix like '-SNAPSHOT' while being same SemVer
        }

        return false;   // Same version
    }

    private static String getChangelogUrl(@Nullable String semVersion) {
        if (semVersion != null) {
            return CHANGELOG_URL + "#version-" + semVersion.replace(".", "");
        }

        return CHANGELOG_URL;
    }
}
