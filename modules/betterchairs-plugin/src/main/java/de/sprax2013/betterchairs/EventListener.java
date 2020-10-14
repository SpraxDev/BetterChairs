package de.sprax2013.betterchairs;

import com.cryptomorin.xseries.XMaterial;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.entity.EntityTeleportEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static de.sprax2013.betterchairs.BetterChairsPlugin.getManager;

public class EventListener implements Listener {
    private static final List<XMaterial> WALL_SIGN_MATERIALS = Arrays.asList(XMaterial.ACACIA_WALL_SIGN,
            XMaterial.BIRCH_WALL_SIGN, XMaterial.DARK_OAK_WALL_SIGN, XMaterial.JUNGLE_WALL_SIGN,
            XMaterial.OAK_WALL_SIGN, XMaterial.SPRUCE_WALL_SIGN);

    /* Spawn and Destroy Chairs */

    /**
     * If a player is interacting with a valid block to be used as a Chair,
     * we spawn a Chair and sit the player on it
     */
    @EventHandler(priority = EventPriority.HIGH)
    private void onInteract(PlayerInteractEvent e) {
        if (e.isCancelled() && !Settings.IGNORES_INTERACT_PREVENTION.getValueAsBoolean()) return;
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;

        // Check Player
        if (e.getPlayer().isSneaking()) return;
        if (getManager().hasChairsDisabled(e.getPlayer())) return;
        if (getManager().getChair(e.getPlayer()) != null) return;   // Destroy zombie chair on old spigot versions
        if (e.getPlayer().getVehicle() != null) return; // Already sitting on something else
        if (!e.getPlayer().hasPermission(BetterChairsPlugin.getInstance().getName() + ".use")) return;
        if (Settings.NEEDS_EMPTY_HANDS.getValueAsBoolean() &&
                !getManager().chairNMS.hasEmptyHands(e.getPlayer())) return;
        if (Settings.ALLOWED_DISTANCE_TO_CHAIR.getValueAsInt() > 0 &&
                e.getPlayer().getLocation()  // TODO: Use center of Block for distance
                        .distance(e.getClickedBlock().getLocation()) >
                        Settings.ALLOWED_DISTANCE_TO_CHAIR.getValueAsInt()) return;

        // Is world disabled?
        if (Settings.WORLD_FILTER_ENABLED.getValueAsBoolean()) {
            boolean worldInFilter = Objects.requireNonNull(Settings.WORLD_FILTER_NAMES.getValueAsStringList())
                    .contains(e.getPlayer().getWorld().getName());

            // World on Blacklist or not on Whitelist
            if (worldInFilter == Settings.WORLD_FILTER_AS_BLACKLIST.getValueAsBoolean()) return;
        }

        /* Check Block */
        if (!getManager().chairNMS.isStair(e.getClickedBlock()) &&
                !getManager().chairNMS.isSlab(e.getClickedBlock())) return; // Not a Stair or Slab

        // Block disabled in config?
        if ((!Settings.USE_STAIRS.getValueAsBoolean() && getManager().chairNMS.isStair(e.getClickedBlock())) ||
                (!Settings.USE_SLABS.getValueAsBoolean() && getManager().chairNMS.isSlab(e.getClickedBlock()))) return;

        if (getManager().chairNMS.isStair(e.getClickedBlock()) &&
                getManager().chairNMS.isStairUpsideDown(e.getClickedBlock())) return;   // Stair but upside down

        // Check Chair
        if (getManager().isOccupied(e.getClickedBlock())) {
            if (Settings.MSG_ALREADY_OCCUPIED.getValueAsBoolean()) {
                e.getPlayer().sendMessage(Messages.getString(Messages.USE_ALREADY_OCCUPIED));
            }

            return;
        }

        // Check if Chair needs Signs
        if (Settings.NEEDS_SIGNS.getValueAsBoolean()) {
            BlockFace rotation = getManager().chairNMS.getBlockRotation(e.getClickedBlock());

            BlockFace side1 = (rotation == BlockFace.NORTH || rotation == BlockFace.SOUTH) ? BlockFace.WEST : BlockFace.NORTH;
            BlockFace side2 = (rotation == BlockFace.NORTH || rotation == BlockFace.SOUTH) ? BlockFace.EAST : BlockFace.SOUTH;

            Block block1 = e.getClickedBlock().getRelative(side1);
            Block block2 = e.getClickedBlock().getRelative(side2);

            // Are WALL_SIGNs placed?
            if (!WALL_SIGN_MATERIALS.contains(XMaterial.matchXMaterial(block1.getType())) ||
                    !WALL_SIGN_MATERIALS.contains(XMaterial.matchXMaterial(block2.getType()))) {
                if (Settings.MSG_NEEDS_SIGNS.getValueAsBoolean()) {
                    e.getPlayer().sendMessage(Messages.getString(Messages.USE_NEEDS_SIGNS));
                }

                return; // No
            }

            // Are they attached to the chair?
            if (side1 != getManager().chairNMS.getBlockRotation(block1).getOppositeFace() ||
                    side2 != getManager().chairNMS.getBlockRotation(block2).getOppositeFace()) {
                if (Settings.MSG_NEEDS_SIGNS.getValueAsBoolean()) {
                    e.getPlayer().sendMessage(Messages.getString(Messages.USE_NEEDS_SIGNS));
                }

                return; // No
            }
        }

        // Spawn Chair
        if (getManager().create(e.getPlayer(), e.getClickedBlock())) {
            e.setCancelled(true);

            if (Settings.MSG_NOW_SITTING.getValueAsBoolean()) {
                e.getPlayer().sendMessage(Messages.getString(Messages.USE_NOW_SITTING));
            }
        }
    }

    /**
     * This Event only works in latest spigot-1.8.8 and newer
     */
    @EventHandler(priority = EventPriority.MONITOR)
    private void onDismount(EntityDismountEvent e) {
        Entity armorStand = e.getDismounted();

        if (armorStand instanceof ArmorStand) {
            Chair c = getManager().getChair((ArmorStand) armorStand);

            if (c != null) {
                getManager().destroy(c, true);
            }
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
                getManager().destroy(c, true, true);
                getManager().setChairsDisabled(e.getPlayer(), false);
            }
        }
    }

    /* Protect Chairs */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntitySpawn(EntitySpawnEvent e) {
        if (!e.isCancelled()) return;
        if (!(e.getEntity() instanceof ArmorStand)) return;

        if (getManager().isChair((ArmorStand) e.getEntity())) {
            e.setCancelled(false);
        }
    }

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