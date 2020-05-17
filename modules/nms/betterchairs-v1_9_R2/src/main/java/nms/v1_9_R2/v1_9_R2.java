package nms.v1_9_R2;

import net.blackscarx.betterchairs.NMS;
import net.minecraft.server.v1_9_R2.EntityArmorStand;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.Stairs;

public class v1_9_R2 implements NMS {
    @Override
    public String getVersion() {
        return "v1_9_R2";
    }

    @Override
    public ArmorStand spawn(Location loc, Player p) {
        return CustomArmorStand_9_R2.spawn(loc, p);
    }

    @Override
    public void kill(ArmorStand armorStand) {
        EntityArmorStand nmsArmor = ((CraftArmorStand) armorStand).getHandle();
        ((CustomArmorStand_9_R2) nmsArmor).killArmorStand();
    }

    @Override
    public boolean check(ArmorStand armorStand) {
        return ((CraftArmorStand) armorStand).getHandle() instanceof CustomArmorStand_9_R2;
    }

    @Override
    public boolean isStair(Block b) {
        return b.getState() instanceof Stairs;
    }
}