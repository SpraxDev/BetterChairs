package betterchairs.nms.v1_8_R2;

import de.sprax2013.betterchairs.ChairNMS;
import de.sprax2013.betterchairs.CustomChairEntity;
import net.minecraft.server.v1_8_R2.EntityArmorStand;
import net.minecraft.server.v1_8_R2.EntityHuman;
import net.minecraft.server.v1_8_R2.World;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftHumanEntity;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

class CustomArmorStand extends EntityArmorStand implements CustomChairEntity {
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
    public void markAsRemoved() {
        this.remove = true;
    }

    @Override
    public void g(float f, float f1) {
        if (remove) return; // If the ArmorStand is being removed, no need to bother
        if (this.ticksLived % 10 == 0) return;  // Only run every 10 ticks

        if (!(this.passenger instanceof EntityHuman)) {
            remove = true;
            this.bukkitEntity.remove();
            return;
        }

        // Rotate the ArmorStand together with its passenger
        this.setYawPitch(this.passenger.yaw, this.passenger.pitch * .5F);
        this.aK = this.yaw;

        if (this.regenerationAmplifier >= 0) {
            CraftHumanEntity p = ((EntityHuman) this.passenger).getBukkitEntity();

            if (!p.hasPotionEffect(PotionEffectType.REGENERATION)) {
                p.addPotionEffect(new PotionEffect(
                        PotionEffectType.REGENERATION, ChairNMS.REGENERATION_EFFECT_DURATION, this.regenerationAmplifier,
                        false, false), true);
            }
        }
    }

    @Override
    public void die() {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.die();
    }

    private boolean shouldDie() {
        return remove || this.passenger == null || !(this.passenger instanceof EntityHuman);
    }
}
