package de.sprax2013.betterchairs;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.block.Block;

/**
 * Determines sitting offset using {@link ChairNMS} block-type queries and XMaterial.
 * Used as a fallback on servers older than 1.18.
 */
public class LegacyOffsetDetector extends OffsetDetector {
    private final ChairNMS chairNMS;

    LegacyOffsetDetector(ChairNMS chairNMS) {
        this.chairNMS = chairNMS;
    }

    @Override
    protected double getCenterTopY(Block block) {
        double topY = (!chairNMS.isStair(block) && !chairNMS.isSlab(block)) ||
                (chairNMS.isSlab(block) && chairNMS.isSlabTop(block)) ? 1.0 : 0.5;

        XMaterial blockType = XMaterial.matchXMaterial(block.getType());
        if (blockType.name().endsWith("_TRAPDOOR")) {
            topY = 0.25;
        }

        return topY;
    }
}
