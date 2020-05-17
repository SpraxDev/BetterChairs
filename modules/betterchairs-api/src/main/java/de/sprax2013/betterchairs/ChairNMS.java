package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class ChairNMS {
    /**
     * Spawns an ArmorStand with:
     * <ul>
     *     <li>Invisible</li>
     *     <li>Invincible</li>
     *     <li>NoGravity</li>
     *     <li>DisabledSlots</li>
     * </ul>
     */
    @NotNull
    protected abstract ArmorStand spawnChairArmorStand(Location loc);

    /**
     * @throws IllegalArgumentException if {@code armorStand} is not an instance of CustomArmorStand
     */
    protected abstract void killChairArmorStand(ArmorStand armorStand);

    protected abstract boolean isStair(Block b);

    protected abstract boolean isSlab(Block b);

    protected abstract boolean hasEmptyHands(Player p);

    @Nullable
    protected Listener getListener() {
        return null;
    }
}