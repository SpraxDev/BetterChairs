package de.sprax2013.betterchairs;

import de.tr7zw.changeme.nbtapi.NBTEntity;
import org.bukkit.entity.ArmorStand;

public class ChairUtils {
    private ChairUtils() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * <ul>
     *  <li>{@link ArmorStand#hasGravity()} = {@code false}</li>
     *  <li>{@link ArmorStand#isVisible()} = {@code false}</li>
     *  <li>{@link ArmorStand#getRemoveWhenFarAway()} = {@code true}</li>
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
}