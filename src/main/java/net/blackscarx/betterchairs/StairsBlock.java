/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs;

import org.bukkit.Material;

public enum StairsBlock {

    ACACIA("acacia_stairs"),
    BIRCH_WOOD("birch_wood_stairs"),
    BRICK("brick_stairs"),
    COBBLESTONE("cobblestone_stairs"),
    DARK_OAK("dark_oak_stairs"),
    JUNGLE_WOOD("jungle_wood_stairs"),
    NETHER_BRICK("nether_brick_stairs"),
    QUARTZ("quartz_stairs"),
    RED_SANDSTONE("red_sandstone_stairs"),
    SANDSTONE("sandstone_stairs"),
    SMOOTH("smooth_stairs"),
    SPRUCE_WOOD("spruce_wood_stairs"),
    WOOD("wood_stairs"),
    PURPUR_STAIRS("purpur_stairs");

    final private String name;

    StairsBlock(String name) {
        this.name = name;
    }

    public static String from(Material stair) {
        for (StairsBlock stairs : values()) {
            if (stairs.getName().equalsIgnoreCase(stair.name())) {
                return stairs.getName();
            }
        }
        return "null";
    }

    public String getName() {
        return name;
    }

}
