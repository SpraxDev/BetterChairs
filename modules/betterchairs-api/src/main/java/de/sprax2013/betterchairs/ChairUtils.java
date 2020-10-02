package de.sprax2013.betterchairs;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.HumanEntity;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.logging.Logger;

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
     *  <li>DisabledSlots = {@code 0b11111} <em>(all)</em></li>
     * </ul>
     */
    public static void applyChairProtections(ArmorStand armorStand) {
        armorStand.setGravity(false);
        armorStand.setVisible(false);

        // Chairs should always be removed... But just in case
        armorStand.setRemoveWhenFarAway(true);

        NBTEntity nbt = new NBTEntity(armorStand);
        nbt.setBoolean("Invulnerable", true);
        nbt.setInteger("DisabledSlots", 0b11111);
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

    /**
     * This method is used to log an error with the plugin's logger or
     * the global one, in case {@link ChairManager#getPlugin()} is {@code null}.
     *
     * @param msg The error message that should be logged
     */
    public static void logNmsErr(String msg) {
        Logger logger = ChairManager.getPlugin() != null ? ChairManager.getPlugin().getLogger() : Logger.getGlobal();

        logger.warning(msg);
    }
}