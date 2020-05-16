package de.sprax2013.betterchairs;

import org.bukkit.event.EventHandler;
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
    // TODO: Use SpigotMC as DownloadURL
    public static final String DOWNLOAD_URL = "https://github.com/Sprax2013/BetterChairs/releases";

    private final JavaPlugin plugin;

    private final Timer timer;
    private String newerVersion;

    public Updater(JavaPlugin plugin) {
        this.plugin = plugin;
        this.timer = new Timer(true);

        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!plugin.isEnabled()) {
                    timer.cancel();
                    return;
                }

                try {
                    checkForUpdates();
                } catch (Throwable th) {
                    System.err.println("[" + plugin.getName() + "] Could not check for updates" +
                            (th.getMessage() == null ? "!" : ": " + th.getMessage()));

                    if (th.getMessage() == null) {
                        th.printStackTrace();
                    }
                }
            }
        }, 2000, 1000 * 60 * 60 * 3);   // Check every 3h
    }

    private void checkForUpdates() throws IOException {
        URL website = new URL("https://sprax2013.github.io/BetterChairs/version.txt");
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));

        StringBuilder versionTxt = new StringBuilder();

        String inputLine;
        while ((inputLine = in.readLine()) != null) {
            versionTxt.append(inputLine.trim());
        }
        in.close();

        String versionStr = versionTxt.toString().split("\n")[0];

        if (!versionStr.equals(this.newerVersion)) {
            String[] versionArgs = versionStr.split("\\.");
            String[] currVersion = plugin.getDescription().getVersion().split("\\.");

            boolean newer = false;
            for (int i = 0; i < 3; i++) {
                try {
                    if (!currVersion[i].equals(versionArgs[i]) &&
                            Integer.parseInt(currVersion[i]) < Integer.parseInt(versionArgs[i])) {
                        newer = true;
                        break;
                    }
                } catch (Throwable ignore) {
                }
            }

            if (newer) {
                this.newerVersion = versionStr;
                System.out.println("[" + plugin.getName() + "] Found a newer version: " + versionTxt);
            } else {
                this.newerVersion = null;
                System.out.println("[" + plugin.getName() + "] You are using the latest version!");
            }
        }
    }

    @EventHandler
    private void onJoin(PlayerJoinEvent e) {
        if (newerVersion == null) return;
        if (!e.getPlayer().hasPermission(plugin.getName() + ".update")) return;

        // TODO: Fix colors and cleanup raw message
        e.getPlayer().sendMessage("§a[" + plugin.getName() + "] Found new update! §6Version: " + newerVersion);
        e.getPlayer().sendRawMessage("[\"\",{\"text\":\"[UPDATE]\",\"color\":\"aqua\",\"bold\":true,\"clickEvent\":{\"action\":\"open_url\",\"value\":\"" + DOWNLOAD_URL + "\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click for go on the plugin page\",\"color\":\"green\"}]}}}]");
    }

    @EventHandler
    private void onPluginDisable(PluginDisableEvent e) {
        if (e.getPlugin() == plugin) {
            timer.cancel();
        }
    }
}