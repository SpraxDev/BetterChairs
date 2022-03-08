package betterchairs.nms.v1_8_R3;

import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import net.minecraft.server.v1_8_R3.EntityArmorStand;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.World;
import org.bukkit.Location;

class CustomArmorStand extends EntityArmorStand implements CustomChairEntity {
    private boolean remove = false;
    private final int regenerationAmplifier;

    private final Location expectedLocation;

    /**
     * @param regenerationAmplifier provide a negative value to disable regeneration
     */
    public CustomArmorStand(World world, double d0, double d1, double d2, int regenerationAmplifier) {
        super(world, d0, d1, d2);

        this.regenerationAmplifier = regenerationAmplifier;
        this.expectedLocation = new Location(null, d0, d1, d2);
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
        this.setYawPitch(this.passenger.yaw, 0);

        if (ChairUtils.didChairEntityMove(expectedLocation, this.locX, this.locY, this.locZ)) {
            this.enderTeleportTo(expectedLocation.getX(), Math.min(this.locY, expectedLocation.getY()), expectedLocation.getZ());
        }

        ChairUtils.applyRegeneration(((EntityHuman) this.passenger).getBukkitEntity(), this.regenerationAmplifier);
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
