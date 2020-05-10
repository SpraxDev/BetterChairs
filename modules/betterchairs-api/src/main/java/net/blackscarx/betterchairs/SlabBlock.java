package net.blackscarx.betterchairs;

import net.blackscarx.betterchairs.xseries.XMaterial;
import org.bukkit.Material;

import java.util.ArrayList;
import java.util.List;

public class SlabBlock {

    private static List<SlabBlock> list = new ArrayList<>();

    private XMaterial material;

    private String name;

    public SlabBlock(XMaterial material) {
        this(material, material.name().toLowerCase());
    }

    public SlabBlock(XMaterial material, String name) {
        this.material = material;
        this.name = name;
        list.add(this);
    }

    public static List<SlabBlock> getList() {
        return list;
    }

    public Material getType() {
        return material.parseMaterial();
    }

    public Short getData() {
        return material.getData();
    }

    public String getName() {
        return name;
    }

}
