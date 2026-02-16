package de.sprax2013.betterchairs;

import org.bukkit.block.Block;
import org.bukkit.util.BoundingBox;

import java.util.Collection;

/**
 * Determines sitting offset using {@link Block#getCollisionShape()} and its bounding boxes.
 * Requires Spigot 1.18+.
 */
class ModernOffsetDetector extends OffsetDetector {
    @Override
    protected double getCenterTopY(Block block) {
        Collection<BoundingBox> boxes = block.getCollisionShape().getBoundingBoxes();
        double[] samples = {0.375, 0.625};
        double minTopY = Double.MAX_VALUE;

        for (double x : samples) {
            for (double z : samples) {
                // Height at this sample point = top of the highest box covering it
                double pointTopY = boxes.stream()
                        .filter(bb -> bb.getMinX() <= x && bb.getMaxX() >= x && bb.getMinZ() <= z && bb.getMaxZ() >= z)
                        .mapToDouble(BoundingBox::getMaxY)
                        .max()
                        .orElse(0);
                minTopY = Math.min(minTopY, pointTopY);
            }
        }

        return minTopY == Double.MAX_VALUE ? 1.0 : minTopY;
    }
}
