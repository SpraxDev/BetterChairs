/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs;

import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SlabBlock {

    private static List<SlabBlock> list = new ArrayList<>();

    private Material material;

    private Short data;

    private String name;

    public SlabBlock(Material material, Short data, String name) {
        this.material = material;
        this.data = data;
        this.name = name;
        list.add(this);
    }

    public static List<SlabBlock> getList() {
        return list;
    }

    public Material getType() {
        return material;
    }

    public Short getData() {
        return data;
    }

    public String getName() {
        return name;
    }

}
