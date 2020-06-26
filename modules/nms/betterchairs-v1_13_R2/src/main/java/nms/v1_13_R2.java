package nms;

import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.ChairUtils;
import net.minecraft.server.v1_13_R2.Entity;
import net.minecraft.server.v1_13_R2.EntityArmorStand;
import net.minecraft.server.v1_13_R2.EntityHuman;
import net.minecraft.server.v1_13_R2.World;
import net.minecraft.server.v1_13_R2.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftArmorStand;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftHumanEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.material.Directional;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class v1_13_R2 extends ChairNMS {
    @Override
    public @NotNull ArmorStand spawnChairArmorStand(@NotNull Location loc, int regenerationAmplifier) {
        WorldServer nmsWorld = ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle();
        CustomArmorStand nmsArmorStand = new CustomArmorStand(
                nmsWorld, loc.getX(), loc.getY(), loc.getZ(), regenerationAmplifier);
        ArmorStand armorStand = (ArmorStand) nmsArmorStand.getBukkitEntity();

        try {
            setValue(nmsArmorStand, "bH", 2031616);    // DisabledSlots
        } catch (NoSuchFieldException | IllegalAccessException ex) {
            // fail gracefully
            System.err.println("BetterChairs could not apply protections to a Chair at " +
                    armorStand.getLocation().getBlock().getLocation() +
                    " (" + ex.getClass().getName() + ": " + ex.getMessage() + ")");
        }

        nmsArmorStand.setInvulnerable(true);
        ChairUtils.applyBasicChairModifications(armorStand);

        nmsWorld.addEntity(nmsArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM);

        return armorStand;
    }

    @Override
    public void killChairArmorStand(@NotNull ArmorStand armorStand) {
        EntityArmorStand nmsArmorStand = ((CraftArmorStand) armorStand).getHandle();

        if (!(nmsArmorStand instanceof CustomArmorStand))
            throw new IllegalArgumentException("The provided ArmorStand is not an instance of " +
                    CustomArmorStand.class.getName());

        ((CustomArmorStand) nmsArmorStand).remove = true;
        armorStand.remove();
    }

    @Override
    public boolean isStair(@NotNull Block block) {
        return block.getBlockData() instanceof Stairs;
    }

    @Override
    public boolean isStairUpsideDown(@NotNull Block block) {
        return ((Stairs) block.getBlockData()).getHalf() == Bisected.Half.TOP;
    }

    @Override
    public @NotNull BlockFace getBlockRotation(@NotNull Block block) {
        return ((Directional) block.getBlockData()).getFacing();
    }

    @Override
    public boolean isSlab(@NotNull Block block) {
        return block.getBlockData() instanceof Slab && ((Slab) block.getBlockData()).getType() != Slab.Type.DOUBLE;
    }

    @Override
    public boolean isSlabTop(@NotNull Block block) {
        return ((Slab) block.getBlockData()).getType() == Slab.Type.TOP;
    }

    @Override
    public boolean hasEmptyHands(@NotNull Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.AIR &&
                player.getInventory().getItemInOffHand().getType() == Material.AIR;
    }

    private static class CustomArmorStand extends EntityArmorStand {
        public boolean remove = false;
        private final int regenerationAmplifier;

        /**
         * @param regenerationAmplifier provide a negative value to disable regeneration
         */
        public CustomArmorStand(World world, double d0, double d1, double d2, int regenerationAmplifier) {
            super(world, d0, d1, d2);

            this.regenerationAmplifier = regenerationAmplifier;
        }

        @Override
        public void tick() {
            if (remove) return; // If the ArmorStand is being removed, no need to bother
            if (this.ticksLived % 10 == 0) return;  // Only run every 10 ticks

            Entity passenger = this.passengers.size() != 0 ? this.passengers.get(0) : null;

            if (!(passenger instanceof EntityHuman)) {
                remove = true;
                this.getBukkitEntity().remove();
                return;
            }

            // Rotate the ArmorStand together with its passenger
            this.setYawPitch(passenger.yaw, passenger.pitch * .5F);
            this.aS = this.yaw;

            if (this.regenerationAmplifier >= 0) {
                CraftHumanEntity p = ((EntityHuman) passenger).getBukkitEntity();

                if (!p.hasPotionEffect(PotionEffectType.REGENERATION)) {
                    p.addPotionEffect(new PotionEffect(
                            PotionEffectType.REGENERATION, ChairNMS.regenerationEffectDuration, this.regenerationAmplifier,
                            false, false), true);
                }
            }
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