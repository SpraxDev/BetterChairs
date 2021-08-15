package betterchairs.nms.v1_17_R1;

import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.level.World;

class CustomArrow extends EntityTippedArrow implements CustomChairEntity {
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
    public void markAsRemoved() {
        this.remove = true;
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
