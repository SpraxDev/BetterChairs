package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
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

    @Nullable
    protected Listener getListener() {
        return null;
    }
}