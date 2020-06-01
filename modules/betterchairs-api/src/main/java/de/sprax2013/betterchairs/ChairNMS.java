package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;

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
    protected static final int regenerationEffectDuration = 60;    // In Ticks

    /**
     * Spawns an ArmorStand that is/has:
     * <ul>
     *     <li>Invisible</li>
     *     <li>Invincible</li>
     *     <li>NoGravity</li>
     *     <li>DisabledSlots</li>
     * </ul>
     * The ArmorStand may fulfil the above with the help of {@link #getListener()}.
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

    /**
     * Uses reflections to change the value of a specific field of an object.<br>
     * This method recursively searches for the first {@link Field} through all Superclasses
     *
     * @throws NoSuchFieldException   If a {@link Field} with the given {@code name} does
     *                                not exist on {@code obj} or one of its superclasses
     * @throws IllegalAccessException May be thrown by {@link Field#set(Object, Object)}
     */
    protected static void setValue(Object obj, String name, Object value) throws NoSuchFieldException, IllegalAccessException {
        Field field = null;
        Class<?> clazz = obj.getClass();


        while (field == null && clazz != null) {
            try {
                field = clazz.getDeclaredField(name);
            } catch (NoSuchFieldException ignore) {
                clazz = clazz.getSuperclass();
            }
        }

        if (field == null) throw new NoSuchFieldException(name);

        boolean isAccessible = field.isAccessible();
        field.setAccessible(true);
        field.set(obj, value);
        field.setAccessible(isAccessible);
    }
}