package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provides abstraction to be used in maven modules with the specified spigot version<br><br>
 * <b>Why not just use the Fallback-ChairNMS in {@code #onEnable()}?</b><br>
 * <ul>
 *     <li>Using the fallback in newer versions requires Spigot to enable old material support</li>
 *     <li>NMS allows us to easily rotate the ArmorStand together with the player
 *     (without using {@link org.bukkit.event.player.PlayerMoveEvent})</li>
 * </ul>
 */
public abstract class ChairNMS {
    public static final int REGENERATION_EFFECT_DURATION = 3 * 20;    // In Ticks

    /**
     * Spawns an Entity that is/has:
     * <ul>
     *     <li>Invisible</li>
     *     <li>Invincible</li>
     *     <li>NoGravity</li>
     *     <li>Silent</li>
     *     <li>DisabledSlots (if it is an ArmorStand)</li>
     *     <li>NoBounce (if it is an Arrow)</li>
     * </ul>
     * The entity may fulfill the above with the help of {@link #getListener()} and {@link ChairUtils#applyChairProtections(Entity)}
     *
     * @param loc                   The location for the Chair-Entity
     * @param regenerationAmplifier The amplifier for the regeneration effect
     * @param useArmorStand         Whether an ArmorStand should be used as Entity
     *
     * @return The created Entity to be used for a Chair
     *
     * @see #getRegenerationAmplifier(Player)
     * @see ChairUtils#applyChairProtections(Entity)
     */
    @NotNull
    public abstract Entity spawnChairEntity(@NotNull Location loc, int regenerationAmplifier, boolean useArmorStand);

    /**
     * @param entity The {@link Entity} that should be deleted
     *
     * @throws IllegalArgumentException if {@code entity} is not an instance of the custom Entity
     */
    public abstract void killChairEntity(@NotNull Entity entity);

    public abstract boolean isStair(@NotNull Block block);

    public abstract boolean isStairUpsideDown(@NotNull Block block);

    @NotNull
    public abstract BlockFace getBlockRotation(@NotNull Block block);

    /**
     * @param block The {@link Block} to check
     *
     * @return true if the block is a half slab (not double slab!), false otherwise
     */
    public abstract boolean isSlab(@NotNull Block block);

    public abstract boolean isSlabTop(@NotNull Block block);

    public abstract boolean hasEmptyMainHand(@NotNull Player player);

    /**
     * Checks if an Entity is or will be used as {@link Chair} with NMS.<br>
     * This method should be used to identify a {@link Chair} before it has been spawned into the world.<br>
     * This can for example be used to un-cancel an {@link org.bukkit.event.entity.EntitySpawnEvent}
     *
     * @param entity {@link Entity} to check
     *
     * @return true if Entity is or will be used as Chair, false otherwise
     *
     * @see ChairManager#isChair(Entity)
     */
    public abstract boolean isChair(@NotNull Entity entity);

    @Nullable
    public Listener getListener() {
        return null;
    }

    public static int getRegenerationAmplifier(Player p) {
        if (!Settings.REGENERATION_ENABLED.getValueAsBoolean() ||
                Settings.REGENERATION_AMPLIFIER.getValueAsInt() <= 0 ||
                (Settings.REGENERATION_CHECK_PERMISSION.getValueAsBoolean() &&
                        !p.hasPermission(ChairManager.plugin.getName() + ".regeneration"))) return -1;

        return Settings.REGENERATION_AMPLIFIER.getValueAsInt() - 1;
    }
}
