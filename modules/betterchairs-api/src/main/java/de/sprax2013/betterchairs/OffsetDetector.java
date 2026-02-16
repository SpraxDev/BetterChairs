package de.sprax2013.betterchairs;

import org.bukkit.block.Block;

public abstract class OffsetDetector {
    /**
     * Returns the Y offset to apply to the chair entity's spawn position relative to the block.
     */
    public double getSitOffset(Block block, boolean sitsOnArmorStand) {
        return -1.2 + (getCenterTopY(block) - 0.5) + (sitsOnArmorStand ? 0 : 1);
    }

    /**
     * Returns the highest collision Y at the center of the block, expressed as a value in [0, 1]
     * where 0 is the block's bottom face and 1 is the top face.
     */
    protected abstract double getCenterTopY(Block block);
}
