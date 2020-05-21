package nms;

import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.ChairUtils;
import net.minecraft.server.v1_15_R1.EntityArmorStand;
import net.minecraft.server.v1_15_R1.EntityHuman;
import net.minecraft.server.v1_15_R1.NBTTagCompound;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftArmorStand;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class v1_15_R1 extends ChairNMS {
    @Override
    public @NotNull ArmorStand spawnChairArmorStand(Location loc) {
        CraftWorld nmsWorld = (CraftWorld) Objects.requireNonNull(loc.getWorld());
        CustomArmorStand nmsArmorStand = new CustomArmorStand(nmsWorld.getHandle(), loc.getX(), loc.getY(), loc.getZ());
        ArmorStand armorStand = (ArmorStand) nmsArmorStand.getBukkitEntity();

        NBTTagCompound nbt = new NBTTagCompound();
        nmsArmorStand.b(nbt);
        nbt.setInt("DisabledSlots", 2031616);
        nmsArmorStand.a(nbt);

        nmsArmorStand.setInvulnerable(true);
        ChairUtils.applyBasicChairModifications(armorStand);

        nmsWorld.addEntity(nmsArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return armorStand;
    }

    @Override
    public void killChairArmorStand(ArmorStand armorStand) {
        EntityArmorStand nmsArmorStand = ((CraftArmorStand) armorStand).getHandle();

        if (!(nmsArmorStand instanceof CustomArmorStand))
            throw new IllegalArgumentException("The provided ArmorStand is not an instance of " +
                    CustomArmorStand.class.getName());

        ((CustomArmorStand) nmsArmorStand).remove = true;
        armorStand.remove();
    }

    @Override
    protected boolean isStair(Block block) {
        return block.getBlockData() instanceof Stairs;
    }

    @Override
    protected boolean isStairUpsideDown(Block block) {
        return ((Stairs) block.getBlockData()).getHalf() == Bisected.Half.TOP;
    }

    @Override
    protected boolean isSlab(Block block) {
        return block.getBlockData() instanceof Slab && ((Slab) block.getBlockData()).getType() != Slab.Type.DOUBLE;
    }

    @Override
    protected boolean isSlabTop(Block block) {
        return ((Slab) block.getBlockData()).getType() == Slab.Type.TOP;
    }

    @Override
    protected boolean hasEmptyHands(Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.AIR &&
                player.getInventory().getItemInOffHand().getType() == Material.AIR;
    }

    public static class CustomArmorStand extends EntityArmorStand {
        public boolean remove = false;

        public CustomArmorStand(World world, double d0, double d1, double d2) {
            super(world, d0, d1, d2);
        }

        @Override
        public void tick() {
            if (remove) return;

            if (this.passengers.size() > 0) {
                this.setYawPitch(this.passengers.get(0).yaw, passengers.get(0).pitch * .5F);
                this.aK = this.yaw;
            }

//            if (this.ticksLived % 20 == 0) {
//                // TODO: Apply regeneration
//                if (Config.getConfig().getBoolean("Regen when sit", false)) {
//                    EntityPlayer p = (EntityPlayer) mount.passengers.get(0);
//                    if (Config.getConfig().getBoolean("Regen need permission", false))
//                        if (!p.getBukkitEntity().hasPermission("betterchairs.regen"))
//                            return false;
//                    PotionEffect potion = new PotionEffect(PotionEffectType.REGENERATION, 60, Config.getConfig().getInt("Amplifier", 1) - 1, false, false);
//                    p.getBukkitEntity().getActivePotionEffects();
//                    boolean regen = false;
//                    for (PotionEffect popo : p.getBukkitEntity().getActivePotionEffects()) {
//                        if (popo.getType().equals(PotionEffectType.REGENERATION))
//                            regen = true;
//                    }
//                    if (!regen)
//                        p.getBukkitEntity().addPotionEffect(potion);
//                }
//            }
        }

        @Override
        public void killEntity() {
            // Prevents the ArmorStand from getting killed unexpectedly
            if (shouldDie()) super.killEntity();
        }

        @Override
        public void die() {
            // Prevents the ArmorStand from getting killed unexpectedly
            if (shouldDie()) super.die();
        }

        private boolean shouldDie() {
            return remove || this.passengers.size() == 0 || !(this.passengers.get(0) instanceof EntityHuman);
        }
    }
}