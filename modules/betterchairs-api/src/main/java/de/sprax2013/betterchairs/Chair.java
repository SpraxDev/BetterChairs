package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

/**
 * Holds a spawned chair<br>
 * Instances of this class get disposed
 * as soon the the player leaves the chair
 */
public class Chair {
    public final Block block;
    public final ArmorStand armorStand;
    public final Player player;
    public final Location playerOriginalLoc;

    public Chair(Block block, ArmorStand armorStand, Player player) {
        this.block = block;
        this.armorStand = armorStand;
        this.player = player;
        this.playerOriginalLoc = player.getLocation();
    }

    /**
     * This method checks if it is a stair block.<br>
     * Currently only Stairs and Slabs may be used for chairs.
     *
     * @return true if the chair-block is a stair, false otherwise
     */
    public boolean isStair() {
        if (ChairManager.getInstance() == null)
            throw new IllegalStateException("ChairManager is not available yet - Did BetterChairs successfully enable?");

        return ChairManager.getInstance().chairNMS.isStair(block);
    }
}