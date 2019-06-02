/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs.NMSManager.NMS_V1_14_R1;

import net.blackscarx.betterchairs.ChairsConf;
import net.blackscarx.betterchairs.ChairsPlugin;
import net.blackscarx.betterchairs.Files.Config;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class CustomArmorStand_14_R1 extends EntityArmorStand {

    private boolean protect = true;

    public CustomArmorStand_14_R1(World world, double d0, double d1, double d2) {
        super(world, d0, d1, d2);
    }

    public static ArmorStand spawn(Location location, Player p) {
        World mcWorld = ((CraftWorld) location.getWorld()).getHandle();

        CustomArmorStand_14_R1 customEntity = new CustomArmorStand_14_R1(mcWorld, location.getX(), location.getY(), location.getZ());

        customEntity.setNoGravity(true);

        ((CraftLivingEntity) customEntity.getBukkitEntity()).setRemoveWhenFarAway(false);

        mcWorld.addEntity(customEntity, CreatureSpawnEvent.SpawnReason.CUSTOM);

        NBTTagCompound nbt = new NBTTagCompound();
        customEntity.save(nbt);
        nbt.setBoolean("Invulnerable", true);
        customEntity.f(nbt);
        customEntity.b(nbt);
        nbt.setBoolean("Invisible", true);
        nbt.setInt("DisabledSlots", 2031616);
        customEntity.a(nbt);
        customEntity.getBukkitEntity().setPassenger(p);

        return (ArmorStand) customEntity.getBukkitEntity();
    }

    public void tick() {
        if (shouldDie(this)) return;
        if (this.passengers.get(0) != null && this.passengers.get(0) instanceof EntityHuman) {
            this.lastYaw = this.yaw = this.passengers.get(0).yaw;
            this.pitch = passengers.get(0).pitch * 0.5F;
            this.setYawPitch(this.yaw, this.pitch);
            this.aK = this.yaw;
        }
    }

    public void die() {
        if (!protect)
            dead = true;
    }

    public void killArmorStand() {
        protect = false;
        dead = true;
    }

    private boolean shouldDie(CustomArmorStand_14_R1 mount) {
        if (mount.passengers.size() == 0 || !(mount.passengers.get(0) instanceof EntityHuman)) {
            if (ChairsPlugin.list.containsKey(mount.getId())) {
                ChairsConf chairsConf = ChairsPlugin.list.get(mount.getId());
                Player p = chairsConf.getP();
                if (p != null) {
                    p.teleport(chairsConf.getLoc());
                }
                ChairsPlugin.list.remove(mount.getId());
            }
            protect = false;
            mount.die();
            return true;
        } else if (Config.getConfig().getBoolean("Regen when sit", false)) {
            EntityPlayer p = (EntityPlayer) mount.passengers.get(0);
            if (Config.getConfig().getBoolean("Regen need permission", false))
                if (!p.getBukkitEntity().hasPermission("betterchairs.regen"))
                    return false;
            PotionEffect potion = new PotionEffect(PotionEffectType.REGENERATION, 60, Config.getConfig().getInt("Amplifier", 1) - 1, false, false);
            if (p.getBukkitEntity().getActivePotionEffects() != null) {
                boolean regen = false;
                for (PotionEffect popo : p.getBukkitEntity().getActivePotionEffects()) {
                    if (popo.getType().equals(PotionEffectType.REGENERATION))
                        regen = true;
                }
                if (!regen)
                    p.getBukkitEntity().addPotionEffect(potion);
            } else {
                p.getBukkitEntity().addPotionEffect(potion);
            }
        }
        return false;
    }
}
