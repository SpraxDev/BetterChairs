package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Holds a spawned chair<br>
 * Instances of this class get disposed
 * as soon the player leaves the chair
 */
public class Chair {
    private static final String ERR_MANAGER_NOT_AVAILABLE = "ChairManager is not available yet - Did BetterChairs successfully enable?";

    protected final Block block;
    protected final Entity chairEntity;
    protected final Player player;
    private final Location playerOriginalLoc;

    Chair(Block block, Entity chairEntity, Player player) {
        this.block = block;
        this.chairEntity = chairEntity;
        this.player = player;
        this.playerOriginalLoc = player.getLocation();
    }

    /**
     * This method checks if it is a stair block.<br>
     * <s>Currently only Stairs and Slabs may be used for chairs.</s>
     *
     * @return true if the chair's block is a stair, false otherwise
     * @see #getType()
     * @deprecated Since v1.1.0 Chairs may be any block and not just stairs and slabs
     */
    @Deprecated
    public boolean isStair() {
        if (ChairManager.getInstance() == null) throw new IllegalStateException(ERR_MANAGER_NOT_AVAILABLE);

        return ChairManager.getInstance().chairNMS.isStair(block);
    }

    /**
     * This method checks if it is a stair, slab or different block.
     */
    public @NotNull ChairType getType() {
        if (ChairManager.getInstance() == null) throw new IllegalStateException(ERR_MANAGER_NOT_AVAILABLE);

        if (ChairManager.getInstance().chairNMS.isStair(block))
            return ChairType.STAIR;

        if (ChairManager.getInstance().chairNMS.isSlab(block))
            return ChairType.SLAB;

        return ChairType.CUSTOM;
    }

    @SuppressWarnings("unused")
    @NotNull
    public Location getOriginPlayerLocation() {
        return this.playerOriginalLoc.clone();
    }

    @NotNull
    public Block getBlock() {
        return this.block;
    }

    @NotNull
    public Entity getChairEntity() {
        return this.chairEntity;
    }

    @NotNull
    public Player getPlayer() {
        return this.player;
    }

    @NotNull
    public Location getPlayerLeavingLocation() {
        Location loc = playerOriginalLoc.clone();

        if (Settings.LEAVING_CHAIR_KEEP_HEAD_ROTATION.getValueAsBoolean()) {
            loc.setDirection(this.player.getLocation().getDirection());
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
        if (this.chairEntity.getPassenger() == null) {
            if (ChairManager.getInstance() == null) throw new IllegalStateException(ERR_MANAGER_NOT_AVAILABLE);

            ChairManager.getInstance().destroy(this, true);
            return true;
        }

        return false;
    }
}
