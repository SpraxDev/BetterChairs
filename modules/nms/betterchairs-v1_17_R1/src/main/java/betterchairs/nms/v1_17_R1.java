package betterchairs.nms;

import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.Messages;
import net.minecraft.server.level.WorldServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Slab;
import org.bukkit.block.data.type.Stairs;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
public class v1_17_R1 extends ChairNMS {
    @Override
    public @NotNull
    org.bukkit.entity.Entity spawnChairEntity(@NotNull Location loc, int regenerationAmplifier) {
        WorldServer nmsWorld = ((CraftWorld) Objects.requireNonNull(loc.getWorld())).getHandle();
        CustomArrow nmsArrow = new CustomArrow(
                nmsWorld, loc.getX(), loc.getY() + 1.1, loc.getZ(), regenerationAmplifier);
        Arrow arrow = (Arrow) nmsArrow.getBukkitEntity();

        ChairUtils.applyChairProtections(arrow);

        if (!nmsWorld.addEntity(nmsArrow, CreatureSpawnEvent.SpawnReason.CUSTOM)) {
            ChairManager.getLogger().warning(Messages.ERR_ANOTHER_PLUGIN_PREVENTING_SPAWN);
        }

        return arrow;
    }

    @Override
    public void killChairEntity(@NotNull org.bukkit.entity.Entity entity) {
        Entity nmsArrow = ((CraftEntity) entity).getHandle();

        if (!(nmsArrow instanceof CustomArrow))
            throw new IllegalArgumentException(String.format(Messages.ERR_NOT_CUSTOM_ARMOR_STAND,
                    CustomArrow.class.getName()));

        ((CustomArrow) nmsArrow).remove = true;
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
        return ((CraftEntity) entity).getHandle() instanceof CustomArrow;
    }

    private static class CustomArrow extends EntityTippedArrow {
        private boolean remove = false;
        private final int regenerationAmplifier;

        /**
         * @param regenerationAmplifier provide a negative value to disable regeneration
         */
        public CustomArrow(World world, double d0, double d1, double d2, int regenerationAmplifier) {
            super(world, d0, d1, d2);

            this.regenerationAmplifier = regenerationAmplifier;
        }

        @Override
        public void tick() {
            if (remove) return; // If the entity is being removed, no need to bother
            if (this.R % 10 == 0) return;  // Only run every 10 ticks

            Entity passenger = this.getPassengers().isEmpty() ? null : this.getPassengers().get(0);

            if (!(passenger instanceof EntityHuman)) {
                remove = true;
                this.getBukkitEntity().remove();
                return;
            }

            // Rotate the entity together with its passenger
            this.setYawPitch(this.getYRot(), this.getXRot());
            this.setHeadRotation(this.getYRot());

            ChairUtils.applyRegeneration(((EntityHuman) passenger).getBukkitEntity(), this.regenerationAmplifier);
        }

        @Override
        public void killEntity() {
            // Prevents the ArmorStand from getting killed unexpectedly
            if (shouldDie()) super.killEntity();
        }

        @Override
        public void a(Entity.RemovalReason removalReason) {
            // Prevents the ArmorStand from getting killed unexpectedly
            if (shouldDie()) super.a(removalReason);
        }

        private boolean shouldDie() {
            return remove || this.getPassengers().isEmpty() || !(this.getPassengers().get(0) instanceof EntityHuman);
        }
    }
}
