package betterchairs.nms.v1_18_R2;

import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.EntityArmorStand;
import net.minecraft.world.entity.player.EntityHuman;
import net.minecraft.world.level.World;
import org.bukkit.Bukkit;
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
    public void k() {   // tick
        if (this.remove) return; // If the ArmorStand is being removed, no need to bother
        if (this.S % 10 == 0) return;  // Only run every 10 ticks

        Entity passenger = this.cF().isEmpty() ? null : this.cF().get(0);

        if (!(passenger instanceof EntityHuman)) {
            remove = true;
            this.getBukkitEntity().remove();
            return;
        }

        // Rotate the ArmorStand together with its passenger
        // Not happy about using Bukkit API here (+ scheduling) but I don't see a good alternative with all the obfuscation
        Bukkit.getScheduler().runTask(ChairManager.getPlugin(),
                () -> this.getBukkitEntity().setRotation(passenger.getBukkitYaw(), 0));

        if (ChairUtils.didChairEntityMove(this.expectedLocation, this.dc() /* locX */, this.de() /* locY */, this.di() /* locZ */)) {
            this.expectedLocation.setY(Math.min(this.de() /* locY */, this.expectedLocation.getY()));

            this.m(expectedLocation.getX(), this.expectedLocation.getY(), this.expectedLocation.getZ()); // teleportAndSync
        }

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
