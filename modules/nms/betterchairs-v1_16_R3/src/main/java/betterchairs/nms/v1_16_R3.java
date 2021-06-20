package betterchairs.nms;

import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.Messages;
import net.minecraft.server.v1_16_R3.Entity;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityHuman;
import net.minecraft.server.v1_16_R3.World;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class v1_16_R3 extends ChairNMS {
    @Override
    public @NotNull
    ArmorStand spawnChairEntity(@NotNull Location loc, int regenerationAmplifier) {
        WorldServer nmsWorld = ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle();
        CustomArmorStand nmsArmorStand = new CustomArmorStand(
                nmsWorld, loc.getX(), loc.getY(), loc.getZ(), regenerationAmplifier);
        ArmorStand armorStand = (ArmorStand) nmsArmorStand.getBukkitEntity();

        ChairUtils.applyChairProtections(armorStand);

        if (!nmsWorld.addEntity(nmsArmorStand, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            ChairManager.getLogger().warning(Messages.ERR_ANOTHER_PLUGIN_PREVENTING_SPAWN);
        }

        return armorStand;
    }

    @Override
    public void killChairEntity(@NotNull org.bukkit.entity.Entity entity) {
        Entity nmsArmorStand = ((CraftEntity) entity).getHandle();

        if (!(nmsArmorStand instanceof CustomArmorStand))
            throw new IllegalArgumentException(String.format(Messages.ERR_NOT_CUSTOM_ARMOR_STAND,
                    CustomArmorStand.class.getName()));

        ((CustomArmorStand) nmsArmorStand).remove = true;
        entity.remove();
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
    public @NotNull
    BlockFace getBlockRotation(@NotNull Block block) {
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
    public boolean hasEmptyMainHand(@NotNull Player player) {
        return player.getInventory().getItemInMainHand().getType() == Material.AIR;
    }

    @Override
    public boolean isChair(@NotNull org.bukkit.entity.Entity entity) {
        return ((CraftEntity) entity).getHandle() instanceof CustomArmorStand;
    }

    private static class CustomArmorStand extends EntityArmorStand {
        private boolean remove = false;
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

            Entity passenger = this.passengers.isEmpty() ? null : this.passengers.get(0);

            if (!(passenger instanceof EntityHuman)) {
                remove = true;
                this.getBukkitEntity().remove();
                return;
            }

            // Rotate the ArmorStand together with its passenger
            this.setYawPitch(passenger.yaw, passenger.pitch * .5F);
            this.aC = this.yaw;

            ChairUtils.applyRegeneration(((EntityHuman) passenger).getBukkitEntity(), this.regenerationAmplifier);
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
            return remove || this.passengers.isEmpty() || !(this.passengers.get(0) instanceof EntityHuman);
        }
    }
}
