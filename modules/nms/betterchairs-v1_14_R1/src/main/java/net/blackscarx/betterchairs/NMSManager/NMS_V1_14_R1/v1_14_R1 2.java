package net.blackscarx.betterchairs.NMSManager.NMS_V1_14_R1;

import net.blackscarx.betterchairs.NMS;
import net.minecraft.server.v1_14_R1.ChatMessageType;
import net.minecraft.server.v1_14_R1.EntityArmorStand;
import net.minecraft.server.v1_14_R1.IChatBaseComponent;
import net.minecraft.server.v1_14_R1.PacketPlayOutChat;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;

public class v1_14_R1 implements NMS {
    @Override
    public String getVersion() {
        return "v1_14_R1";
    }

    @Override
    public ArmorStand spawn(Location loc, Player p) {
        return CustomArmorStand_14_R1.spawn(loc, p);
    }

    @Override
    public void kill(ArmorStand armorStand) {
        EntityArmorStand nmsArmor = ((CraftArmorStand) armorStand).getHandle();
        ((CustomArmorStand_14_R1) nmsArmor).killArmorStand();
    }

    @Override
    public void sendUpdate(Player p) {
        IChatBaseComponent msg = IChatBaseComponent.ChatSerializer.a("[\"\",{\"text\":\"[UPDATE]\",\"color\":\"aqua\",\"bold\":true,\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.spigotmc.org/resources/18705/\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":{\"text\":\"\",\"extra\":[{\"text\":\"Click for go on the plugin page\",\"color\":\"green\"}]}}}]");
        PacketPlayOutChat packet = new PacketPlayOutChat(msg, ChatMessageType.CHAT);
        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(packet);
    }

    @Override
    public boolean check(ArmorStand armorStand) {
        return ((CraftArmorStand) armorStand).getHandle() instanceof CustomArmorStand_14_R1;
    }

}
