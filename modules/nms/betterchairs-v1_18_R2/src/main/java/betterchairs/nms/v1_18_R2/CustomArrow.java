package betterchairs.nms.v1_18_R2;

import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import de.tr7zw.changeme.nbtapi.NBTEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.entity.projectile.EntityTippedArrow;
import net.minecraft.world.level.World;

import java.lang.reflect.InvocationTargetException;

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
    public void k() {   // tick
        if (this.remove) return; // If the entity is being removed, no need to bother
        if (this.S % 10 == 0) return;  // Only run every 10 ticks

        Entity passenger = this.cF().isEmpty() ? null : this.cF().get(0);

        if (!(passenger instanceof EntityHuman)) {
            remove = true;
            this.getBukkitEntity().remove();
            return;
        }

        // Rotate the entity together with its passenger
        try {
            float xRot = (float) this.getClass().getMethod("do").invoke(this); // getXRot

            this.a(this.dn() /* getYRot */, xRot);   // setYawPitch
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException ex) {
            ex.printStackTrace();
        }
        this.l(this.dn() /* getYRot */); // setHeadRotation

        ChairUtils.applyRegeneration(((EntityHuman) passenger).getBukkitEntity(), this.regenerationAmplifier);
    }

    @Override
    public void ag() {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.ag();    // killEntity
    }

    @Override
    public void a(Entity.RemovalReason removalReason) {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.a(removalReason);
    }

    private boolean shouldDie() {
        return remove || this.cF().isEmpty() || !(this.cF().get(0) instanceof EntityHuman);
    }
}
