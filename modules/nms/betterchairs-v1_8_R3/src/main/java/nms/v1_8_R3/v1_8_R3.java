package nms.v1_8_R3;

import net.blackscarx.betterchairs.NMS;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.IChatBaseComponent;
import net.minecraft.server.v1_8_R3.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.material.Stairs;

public class v1_8_R3 implements NMS {
    @Override
    public String getVersion() {
        return "v1_8_R3";
    }

    @Override
    public ArmorStand spawn(Location loc, Player p) {
        return CustomArmorStand_R3.spawn(loc, p);
    }

    @Override
    public void kill(ArmorStand armorStand) {
        EntityArmorStand nmsArmor = ((CraftArmorStand) armorStand).getHandle();
        ((CustomArmorStand_R3) nmsArmor).killArmorStand();
    }

    @Override
    public boolean check(ArmorStand armorStand) {
        return ((CraftArmorStand) armorStand).getHandle() instanceof CustomArmorStand_R3;
    }

    @Override
    public boolean isStair(Block b) {
        return b.getState() instanceof Stairs;
    }
}