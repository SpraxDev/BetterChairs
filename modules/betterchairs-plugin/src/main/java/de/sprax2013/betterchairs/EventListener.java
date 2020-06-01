package de.sprax2013.betterchairs;

import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import static de.sprax2013.betterchairs.BetterChairsPlugin.getManager;

public class EventListener implements Listener {
    /* Spawn and Destroy Chairs */

    /**
     * If a player is interacting with a valid block to be used as a Chair,
     * we spawn a Chair and sit the player on it
     */
    @EventHandler
    private void onInteract(PlayerInteractEvent e) {
        //TODO: Check if player has chairs disabled
        //TODO: Check if world is disabled in config
        //TODO: Check if Chair has (and needs) signs on the sides
        //TODO: Check if type of chair is disabled in config

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

    /**
     * Found out this Event exists used this instead of some other method...
     * Turns out the event is not fired in all versions equally (not every version fires for ArmorStand)... *sight*<br>
     * TODO: Check if this event is still required with the new workaround is in place (+ {@link #onTeleport(PlayerTeleportEvent)}
     */
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

    /**
     * Correct player teleport after {@link EntityDismountEvent} when required<br>
     * Leaving the ArmorStand sometimes overwrites our teleport causing the ejection
     * so the server teleports the player because of the ejection on the next tick
     */
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

    /**
     * Check if a player is sitting on a chair when logging out<br>
     * Using {@link EventPriority#LOWEST} so other plugins have the right {@link Location}
     * after the player leaving the chair
     */
    @EventHandler(priority = EventPriority.LOWEST)
    private void onQuit(PlayerQuitEvent e) {
        Entity vehicle = e.getPlayer().getVehicle();

        if (vehicle instanceof ArmorStand) {
            Chair c = getManager().getChair((ArmorStand) vehicle);

            if (c != null) {
                getManager().destroy(c, true);
            }
        }
    }

    /* Protect Chairs */

    /**
     * Prevent a chair's ArmorStand to be teleported by accident
     */
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    private void onTeleport(EntityTeleportEvent e) {
        if (e.getEntity().getType() == EntityType.ARMOR_STAND &&
                getManager().isChair((ArmorStand) e.getEntity())) {
            // We don't want our ArmorStand used as a chair to be teleported by accident
            e.setCancelled(true);
        }
    }

    /**
     * Check if another plugin insists on a chair's ArmorStand to be teleported<br>
     * if so, destroy the chair
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onTeleportMonitor(EntityTeleportEvent e) {
        if (e.getEntity().getType() == EntityType.ARMOR_STAND) {
            Chair chair = getManager().getChair((ArmorStand) e.getEntity());

            if (chair != null) {
                getManager().destroy(chair, true);
            }
        }
    }

    /**
     * Protect a chair's block from being destroyed while a player is sitting on it
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onBlockBreak(BlockBreakEvent e) {
        if (getManager().isOccupied(e.getBlock())) {
            e.setCancelled(true);
        }
    }

    /**
     * Check if another plugin insists on breaking a chair's block<br>
     * if so, destroy the chair too
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onBlockBreakMonitor(BlockBreakEvent e) {
        Chair chair = getManager().getChair(e.getBlock());

        if (chair != null) {
            getManager().destroy(chair, true);
        }
    }

    /**
     * Check if a piston tries to move a chair's block
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onPistonExtend(BlockPistonExtendEvent e) {
        for (Block b : e.getBlocks()) {
            if (getManager().isOccupied(b)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    /**
     * Check if another plugin insists on a chair's block being moved by an piston<br>
     * if so, destroy the chair
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPistonExtendMonitor(BlockPistonExtendEvent e) {
        for (Block b : e.getBlocks()) {
            Chair chair = getManager().getChair(b);

            if (chair != null) {
                getManager().destroy(chair, true);
            }
        }
    }

    /**
     * Check if a piston tries to move a chair's block
     */
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    private void onPistonRetract(BlockPistonRetractEvent e) {
        for (Block b : e.getBlocks()) {
            if (getManager().isOccupied(b)) {
                e.setCancelled(true);
                break;
            }
        }
    }

    /**
     * Check if another plugin insists on a chair's block being moved by an piston<br>
     * if so, destroy the chair
     */
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    private void onPistonRetractMonitor(BlockPistonRetractEvent e) {
        for (Block b : e.getBlocks()) {
            Chair chair = getManager().getChair(b);

            if (chair != null) {
                getManager().destroy(chair, true);
            }
        }
    }
}