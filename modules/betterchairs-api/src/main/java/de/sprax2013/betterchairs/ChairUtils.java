package de.sprax2013.betterchairs;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import de.tr7zw.changeme.nbtapi.NbtApiException;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 * This class provides utility methods intended to be used by BetterChairs's nms-classes
 */
public class ChairUtils {
    private ChairUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * <ul>
     *  <li>{@link ArmorStand#hasGravity()} = {@code false}</li>
     *  <li>{@link ArmorStand#isVisible()} = {@code false}</li>
     *  <li>{@link ArmorStand#getRemoveWhenFarAway()} = {@code true}</li>
     *  <li>Invulnerable = {@code true}</li>
     *  <li>Silent = {@code 1}</li>
     *  <li>DisabledSlots = {@code 0b11111} <em>(all)</em></li>
     * </ul>
     *
     * @param entity The Entity to apply the protection to
     */
    public static void applyChairProtections(Entity entity) {
        if (entity instanceof ArmorStand) {
            ((ArmorStand) entity).setGravity(false);
            ((ArmorStand) entity).setVisible(false);
        } else {
            ((Projectile) entity).setBounce(false);
        }

        if (entity instanceof LivingEntity) {
            // Chairs should always be removed... Just making sure.
            ((LivingEntity) entity).setRemoveWhenFarAway(true);
        }

        try {
            NBTEntity nbt = new NBTEntity(entity);
            nbt.setBoolean("Invulnerable", true);
            nbt.setInteger("Silent", 1);

            if (entity instanceof ArmorStand) {
                nbt.setInteger("DisabledSlots", 0b11111);
            }
        } catch (NbtApiException ex) {
            ChairManager.getLogger().warning("Could not apply chair modifications (" + ex.getMessage() + ")!");
        }
    }

    /**
     * This method checks {@link Directional#getFacing()} (legacy API in the current Bukkit api) and
     * inverts it to show the Block's rotation.
     *
     * <b>If the block is not facing to {@code NORTH}, {@code SOUTH}, {@code EAST} or {@code WEST},
     * the value is returned without inverting it</b>
     *
     * @param b The block to check
     *
     * @return The inverted BlockFace as described above, or {@link BlockFace#SELF} if the Bukkit-api is too old
     */
    public static BlockFace getBlockRotationLegacy(Block b) {
        try {
            BlockFace blockFace = ((Directional) b.getState().getData()).getFacing();

            if (blockFace == BlockFace.NORTH) return BlockFace.SOUTH;
            if (blockFace == BlockFace.SOUTH) return BlockFace.NORTH;
            if (blockFace == BlockFace.WEST) return BlockFace.EAST;
            if (blockFace == BlockFace.EAST) return BlockFace.WEST;

            return blockFace;
        } catch (Exception ignore) {
            // Feature not supported on this version of the Bukkit-api
        }

        return BlockFace.SELF;
    }

    public static void applyRegeneration(HumanEntity p, int regenerationAmplifier) {
        if (regenerationAmplifier >= 0 && !p.hasPotionEffect(PotionEffectType.REGENERATION)) {
            p.addPotionEffect(new PotionEffect(
                    PotionEffectType.REGENERATION, ChairNMS.REGENERATION_EFFECT_DURATION, regenerationAmplifier,
                    false, false), true);
        }
    }
}
