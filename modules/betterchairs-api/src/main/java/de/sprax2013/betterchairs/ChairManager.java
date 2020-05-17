package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ChairManager {
    protected static ChairManager instance;

    protected final ChairNMS chairNMS;
    protected final List<Chair> chairs = new ArrayList<>();

    protected ChairManager(@NotNull ChairNMS chairNMS) {
        this.chairNMS = Objects.requireNonNull(chairNMS);

        instance = this;
    }

    public void create(Player p, Block b) {
        if (!chairNMS.isStair(b) && !chairNMS.isSlab(b))
            throw new IllegalArgumentException("The provided block is neither a stair nor a slab");

        ArmorStand armorStand = instance.chairNMS.spawnChairArmorStand(b.getLocation().add(0.5, -1.2, 0.5));

        chairs.add(new Chair(b, armorStand, p));
        armorStand.setPassenger(p);
    }

    public void destroy(Chair chair) {
        chair.player.teleport(new Location(
                chair.playerOriginalLoc.getWorld(), chair.playerOriginalLoc.getX(),
                chair.playerOriginalLoc.getY(), chair.playerOriginalLoc.getZ()));
        chairNMS.killChairArmorStand(chair.armorStand);
        chairs.remove(chair);
    }

    /**
     * @param b The block to check
     *
     * @return true if a player is sitting on it, false otherwise
     */
    public boolean isOccupied(@NotNull Block b) {
        for (Chair c : chairs) {
            if (b.equals(c.block)) {
                return true;
            }
        }

        return false;
    }

    @Nullable
    public Chair getChair(@NotNull Player p) {
        for (Chair c : chairs) {
            if (p == c.player) {
                return c;
            }
        }

        return null;
    }

    @Nullable
    public Chair getChair(@NotNull ArmorStand armorStand) {
        for (Chair c : chairs) {
            if (armorStand == c.armorStand) {
                return c;
            }
        }

        return null;
    }

    public boolean isChair(@NotNull ArmorStand armorStand) {
        for (Chair c : chairs) {
            if (armorStand.equals(c.armorStand)) {
                return true;
            }
        }

        return false;
    }

    // TODO: Methods to create and destroy chairs (and automatically sit players on them)

    /**
     * May be null if BetterChairs is not enabled
     *
     * @return The {@link ChairManager} instance created by BetterChairs, or null
     */
    @Nullable
    public static ChairManager getInstance() {
        return instance;
    }
}