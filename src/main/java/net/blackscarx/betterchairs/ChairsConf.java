/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.BlockState;
import org.bukkit.entity.Player;

public class ChairsConf {

    private BlockState state;

    private Player p;

    private Location loc;

    /**
     * Create new Chairs
     * @param state
     * @param p
     * @param loc
     */

    public ChairsConf(BlockState state, Player p, Location loc) {
        this.state = state;
        this.p = p;
        this.loc = loc;
    }

    /**
     * Check if a block is used for sit player
     * @param state
     * @return boolean
     */

    public static boolean isUsed(BlockState state) {
        for (ChairsConf conf : ChairsPlugin.list.values()) {
            if (conf.getState().equals(state))
                return true;
        }
        return false;
    }

    /**
     * Check if the player is sit
     * @param p
     * @return boolean
     */

    public static boolean isSit(Player p) {
        for (ChairsConf conf : ChairsPlugin.list.values()) {
            if (conf.getP().equals(p))
                return true;
        }
        return false;
    }

    /**
     * Get the block used for sit player
     * @return state
     */

    public BlockState getState() {
        return this.state;
    }

    /**
     * Get the player
     * @return p
     */

    public Player getP() {
        return this.p;
    }

    /**
     * Get location
     * @return loc
     */

    public Location getLoc() {
        return this.loc;
    }

}
