package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import static de.sprax2013.betterchairs.BetterChairsPlugin.getManager;

public class EventListener implements Listener {
    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        //TODO: Check if player has chairs disabled
        //TODO: Check if world is disabled in config
        //TODO: Check if Chair has (and needs) signs on the sides

        // Check Player
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        if (e.getPlayer().isSneaking()) return;
        if (e.getPlayer().getVehicle() != null) return; // Already sitting on something
        if (!getManager().chairNMS.hasEmptyHands(e.getPlayer())) return;  //TODO: Check enabled in config?

        // Check Block
        if (!getManager().chairNMS.isStair(e.getClickedBlock()) &&
                !getManager().chairNMS.isSlab(e.getClickedBlock())) return; // Not a Stair or Slab
        if (getManager().chairNMS.isStair(e.getClickedBlock()) &&
                getManager().chairNMS.isStairUpsideDown(e.getClickedBlock())) return;   // Stair but upside down

        // Check Chair
        if (getManager().isOccupied(e.getClickedBlock())) return;    //TODO: Send message to player? (config)

        // Spawn Chair
        getManager().create(e.getPlayer(), e.getClickedBlock());
    }

    @EventHandler(priority = EventPriority.LOW)
    private void onTeleport(PlayerTeleportEvent e) {
        Chair chair = getManager().chairsAwaitTeleport.get(e.getPlayer());

        if (chair == null) return;  // Player not sitting

        // Check if only the y coordinates changed (= teleport probably caused by EntityDismountEvent)
        if (e.getCause() == PlayerTeleportEvent.TeleportCause.UNKNOWN &&
                e.getFrom().getWorld() == e.getTo().getWorld() &&
                e.getFrom().getX() == e.getTo().getX() &&
                e.getFrom().getZ() == e.getTo().getZ() &&
                e.getFrom().getYaw() == e.getTo().getYaw() &&
                e.getFrom().getPitch() == e.getTo().getPitch() &&
                Math.abs(e.getTo().getY() - e.getFrom().getY()) < 1 /* Making sure y only change by +-1 */) {
            getManager().chairsAwaitTeleport.remove(e.getPlayer());

            Location loc = chair.player.getLocation();  // Keep Yaw/Pitch and only clone Location once for it

            // Set the coordinates the player came from
            loc.setX(chair.playerOriginalLoc.getX());
            loc.setY(chair.playerOriginalLoc.getY());
            loc.setZ(chair.playerOriginalLoc.getZ());

            e.setTo(loc);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onDismount(EntityDismountEvent e) {
        Entity armorStand = e.getDismounted();

        if (armorStand instanceof ArmorStand) {
            Chair c = getManager().getChair((ArmorStand) armorStand);

            if (c != null) {
                getManager().destroy(c, false);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    private void onQuit(PlayerQuitEvent e) {
        Entity vehicle = e.getPlayer().getVehicle();

        if (vehicle instanceof ArmorStand) {
            Chair c = getManager().getChair((ArmorStand) vehicle);

            if (c != null) {
                getManager().destroy(c, true);
            }
        }
    }
}