package betterchairs.nms.v1_20_R3;

import de.sprax2013.betterchairs.ChairManager;
import de.sprax2013.betterchairs.ChairUtils;
import de.sprax2013.betterchairs.CustomChairEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import org.bukkit.Bukkit;
import org.bukkit.Location;

class CustomArmorStand extends ArmorStand implements CustomChairEntity {
    private boolean remove = false;
    private final int regenerationAmplifier;

    private final Location expectedLocation;

    /**
     * @param regenerationAmplifier provide a negative value to disable regeneration
     */
    public CustomArmorStand(ServerLevel world, double d0, double d1, double d2, int regenerationAmplifier) {
        super(world, d0, d1, d2);

        this.regenerationAmplifier = regenerationAmplifier;
        this.expectedLocation = new Location(null, d0, d1, d2);
    }

    @Override
    public void markAsRemoved() {
        this.remove = true;
    }

    @Override
    public void tick() {
        if (this.remove) return; // If the ArmorStand is being removed, no need to bother
        if (this.tickCount % 10 == 0) return;  // Only run every 10 ticks

        Entity passenger = getFirstPassenger();

        if (!(passenger instanceof Player)) {
            this.remove = true;
            getBukkitEntity().remove();
            return;
        }

        // Rotate the ArmorStand together with its passenger
        // Not happy about using Bukkit API here (+ scheduling) but I don't see a good alternative with all the obfuscation
        Bukkit.getScheduler().runTask(ChairManager.getPlugin(), () -> getBukkitEntity().setRotation(passenger.getBukkitYaw(), 0));

        if (ChairUtils.didChairEntityMove(this.expectedLocation, getX(), getY(), getZ())) {
            this.expectedLocation.setY(Math.min(getY(), this.expectedLocation.getY()));

            teleportToWithTicket(this.expectedLocation.getX(), this.expectedLocation.getY(), this.expectedLocation.getZ());
        }

        ChairUtils.applyRegeneration(((Player) passenger).getBukkitEntity(), this.regenerationAmplifier);
    }

    @Override
    public void kill() {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.kill();
    }

    @Override
    public void remove(Entity.RemovalReason removalReason) {
        // Prevents the ArmorStand from getting killed unexpectedly
        if (shouldDie()) super.remove(removalReason);
    }

    private boolean shouldDie() {
        return this.remove || getPassengers().isEmpty() || !(getFirstPassenger() instanceof Player);
    }
}
