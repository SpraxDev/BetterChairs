package de.sprax2013.betterchairs;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;

public class PlaceholderApiExpansion extends PlaceholderExpansion {
    private final BetterChairsPlugin plugin;

    public PlaceholderApiExpansion(BetterChairsPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public @NotNull String getIdentifier() {
        return this.plugin.getName();
    }

    @Override
    public @NotNull String getVersion() {
        return "1.0.0";
    }

    @Override
    public @NotNull String getAuthor() {
        return this.plugin.getDescription().getAuthors().get(0);
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public @NotNull List<String> getPlaceholders() {
        return Arrays.asList("chairs_enabled", "sitting");
    }

    @Override
    public String onRequest(OfflinePlayer player, @NotNull String placeholder) {
        if (placeholder.equalsIgnoreCase("chairs_enabled")) {
            if (ChairManager.getInstance() == null) {
                return Boolean.TRUE.toString();
            }

            return Boolean.valueOf(!ChairManager.getInstance().hasChairsDisabled(player)).toString();
        }

        if (placeholder.equalsIgnoreCase("sitting")) {
            if (ChairManager.getInstance() == null || player.getPlayer() == null) {
                return Boolean.FALSE.toString();
            }

            return Boolean.valueOf(ChairManager.getInstance().getChair(player.getPlayer()) != null).toString();
        }

        return null;
    }
}
