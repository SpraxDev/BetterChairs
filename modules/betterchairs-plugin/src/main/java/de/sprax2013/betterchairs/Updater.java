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
    public static final String GITHUB_URL = "https://github.com/SpraxDev/BetterChairs/releases";
    public static final String SONGODA_URL = "https://songoda.com/marketplace/product/489";

    // TODO: User should get a message like "You can download the update from [SpigotMC], [Songoda] or [GitHub]"
    //  same for the console but multiline, listing all URLs

    private final JavaPlugin plugin;

    private Timer timer;
    private String newerVersion;

    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;

        Settings.addListener(this::reInit);

        reInit();
    }

    private void reInit() {
        if (Settings.UPDATER_ENABLED.getValueAsBoolean()) {
            if (this.timer != null) return;

            this.timer = new Timer(true);
            timer.scheduleAtFixedRate(new TimerTask() {
                @Override
                public void run() {
                    if (plugin.isEnabled() && Settings.UPDATER_ENABLED.getValueAsBoolean()) {
                        try {
                            checkForUpdates();
                        } catch (Exception ex) {
                            ChairManager.getLogger()
                                    .warning("Could not check for updates" +
                                            (ex.getMessage() == null ? "!" : ": " + ex.getMessage()));
                        }
                    } else {
                        timer.cancel();
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
        String currVersion = plugin.getDescription().getVersion();

        if (isNewerVersion(currVersion, versionStr)) {
            this.newerVersion = versionStr;

            ChairManager.getLogger()
                    .info(() -> String.format("Found a new update v%s -> v%s — Download the update from:\nSpigotMC: %s\nSongoda: %s\nGitHub: %s",
                            currVersion, versionTxt, SPIGOT_MC_URL, SONGODA_URL, GITHUB_URL));
        } else {
            this.newerVersion = null;
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (newerVersion == null) return;
        if (!Settings.UPDATER_ENABLED.getValueAsBoolean()) return;
        if (!Settings.UPDATER_NOTIFY_ON_JOIN.getValueAsBoolean()) return;
        if (!e.getPlayer().hasPermission(plugin.getName() + ".updater")) return;

        e.getPlayer().spigot().sendMessage(
                new ComponentBuilder("[").color(ChatColor.GRAY)
                        .append(BetterChairsPlugin.getInstance().getName()).color(ChatColor.GOLD)
                        .append("] ").color(ChatColor.GRAY)
                        .append("Found a new update v" +
                                plugin.getDescription().getVersion() + " -> v" + newerVersion).color(ChatColor.YELLOW)
                        .append("[SpigotMC] ")
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, SPIGOT_MC_URL))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§2Click to visit the download page on SpigotMC")))
                        .append("[Songoda] ")
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, SONGODA_URL))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§2Click to visit the download page on Songoda")))
                        .append("[GitHub]")
                        .color(ChatColor.GREEN)
                        .event(new ClickEvent(ClickEvent.Action.OPEN_URL, GITHUB_URL))
                        .event(new HoverEvent(HoverEvent.Action.SHOW_TEXT, TextComponent.fromLegacyText("§2Click to visit the download page on GitHub")))
                        .create());
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin() == plugin) {
            timer.cancel();
        }
    }

    /**
     * Check if {@code ver1} is newer that {@code ver2}.<br>
     * Expects SemVer (https://semver.org/)
     *
     * @return true, if {@code ver1} is newer than {@code ver2}, false otherwise
     */
    public boolean isNewerVersion(String ver1, String ver2) {
        int[] ver1Num, ver2Num;

        String[] ver1Args = ver1.split("-")[0].split("\\."),
                ver2Args = ver2.split("-")[0].split("\\.");

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

        return false;   // Same version
    }
}