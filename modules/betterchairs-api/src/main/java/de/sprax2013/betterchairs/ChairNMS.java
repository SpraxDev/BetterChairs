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

    protected abstract boolean isStair(Block block);

    protected abstract boolean isStairUpsideDown(Block block);

    /**
     * @param block The {@link Block} to check
     *
     * @return true if the block is a half slab (not double slab!), false otherwise
     */
    protected abstract boolean isSlab(Block block);

    protected abstract boolean isSlabTop(Block block);

    protected abstract boolean hasEmptyHands(Player player);

    @Nullable
    protected Listener getListener() {
        return null;
    }
}