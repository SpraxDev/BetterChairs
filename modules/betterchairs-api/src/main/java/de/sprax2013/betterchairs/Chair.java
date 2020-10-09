package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Holds a spawned chair<br>
 * Instances of this class get disposed
 * as soon the the player leaves the chair
 */
public class Chair {
    protected final Block block;
    protected final ArmorStand armorStand;
    protected final Player player;
    private final Location playerOriginalLoc;

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
     * @return true if the chair's block is a stair, false otherwise
     */
    public boolean isStair() {
        if (ChairManager.getInstance() == null)
            throw new IllegalStateException("ChairManager is not available yet - Did BetterChairs successfully enable?");

        return ChairManager.getInstance().chairNMS.isStair(block);
    }

    @NotNull
    public Location getOriginPlayerLocation() {
        return this.playerOriginalLoc.clone();
    }

    @NotNull
    public Block getBlock() {
        return this.block;
    }

    @NotNull
    public ArmorStand getArmorStand() {
        return this.armorStand;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @Nullable
    public Location getPlayerLeavingLocation() {
        if (!Settings.leavingChairTeleportPlayerToOldLocation()) return null;

        Location loc = playerOriginalLoc.clone();

        if (Settings.leavingChairKeepHeadRotation()) {
            loc.setDirection(player.getLocation().getDirection());
        }

        return loc;
    }

    /**
     * Sometimes the {@link org.spigotmc.event.entity.EntityDismountEvent}
     * is not fired for ArmorStands (e.g. Fallback NMS)<br>
     * This will check for passengers and destroy the {@link Chair} if there is none
     *
     * @return true if the chair is being destroyed, false otherwise
     */
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    protected boolean destroyOnNoPassenger() {
        if (this.armorStand.getPassenger() == null) {
            if (ChairManager.getInstance() == null)
                throw new IllegalStateException("ChairManager is not available yet - Did BetterChairs successfully enable?");

            ChairManager.getInstance().destroy(this, false);
            return true;
        }

        return false;
    }
}