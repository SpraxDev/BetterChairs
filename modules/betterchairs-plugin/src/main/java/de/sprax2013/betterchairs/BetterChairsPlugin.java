package de.sprax2013.betterchairs;

import net.blackscarx.betterchairs.xseries.XMaterial;
import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.material.Stairs;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

import java.util.ArrayList;

public class BetterChairsPlugin extends JavaPlugin {
    private static BetterChairsPlugin plugin;
    private static ChairManager chairManager;

    @Override
    public void onEnable() {
        plugin = this;

        ChairNMS chairNMS = getNewNMS();

        if (chairNMS.getListener() != null) {
            Bukkit.getPluginManager().registerEvents(chairNMS.getListener(), this);
        }

        chairManager = new ChairManager(chairNMS);

        // Load bStats
        //TODO: Sign plugin-jar and append '-UNOFFICIAL' to reported plugin version if missing/invalid signature
        //  This Fork should be considered unofficial as long as it is not approved by the author (but I have maintainer rights,
        //  so reporting to the original bStats page should be fine [hopefully ,_,])
        new MetricsLite(this, 768);

        // Start Updater
        //TODO: Check if enabled in config
        Bukkit.getPluginManager().registerEvents(new Updater(this), this);

        //TODO: Move listener into own class
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onInteract(PlayerInteractEvent e) {
                //TODO: Check if player has chairs disabled
                //TODO: Check if world is disabled in config
                //TODO: Check chair rotation (upside down?)
                //TODO: Check if Chair has (and needs) signs on the sides
                if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
                if (e.getPlayer().isSneaking()) return;
                if (e.getPlayer().getVehicle() != null) return;
                if (!getManager().chairNMS.isStair(e.getClickedBlock()) &&
                        !getManager().chairNMS.isSlab(e.getClickedBlock())) return;
                if (!getManager().chairNMS.hasEmptyHands(e.getPlayer())) return;  //TODO: Check enabled in config?
                if (getManager().isOccupied(e.getClickedBlock())) return;    //TODO: Send message to player? (config)

                getManager().create(e.getPlayer(), e.getClickedBlock());
            }

            @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
            private void onDismount(EntityDismountEvent e) {
                Entity armorStand = e.getDismounted();

                if (armorStand instanceof ArmorStand) {
                    Chair c = getManager().getChair((ArmorStand) armorStand);

                    if (c != null) {
                        getManager().destroy(c);
                    }
                }
            }

            @EventHandler(priority = EventPriority.MONITOR)
            private void onQuit(PlayerQuitEvent e) {
                Entity vehicle = e.getPlayer().getVehicle();

                if (vehicle instanceof ArmorStand) {
                    Chair c = getManager().getChair((ArmorStand) vehicle);

                    if (c != null) {
                        getManager().destroy(c);
                    }
                }
            }
        }, this);
    }

    @Override
    public void onDisable() {
        // Remove all chairs
        if (getManager() != null) {
            for (Chair c : new ArrayList<>(getManager().chairs)) {
                getManager().destroy(c);
            }
            getManager().chairs.clear();
        }

        chairManager = null;
        ChairManager.instance = null;
    }

    /**
     * Instantiates an ChairNMS <i>class</i> depending on the server version
     *
     * @return new instance of {@code ChairNMS}, never null
     */
    @NotNull
    private ChairNMS getNewNMS() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            // Try loading NMS class
            return (ChairNMS) Class.forName("nms." + version).newInstance();
        } catch (Throwable th) {
            System.out.println("[" + plugin.getName() + "] Your server version (" + version +
                    ") is not fully supported - Loading fallback...");

            // Loading fallback when NMS not available
            return new ChairNMS() {
                @Override
                protected @NotNull ArmorStand spawnChairArmorStand(Location loc) {
                    ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
                    ChairUtils.applyBasicChairModifications(armorStand);

                    return armorStand;
                }

                @Override
                protected void killChairArmorStand(ArmorStand armorStand) {
                    armorStand.remove();
                }

                @Override
                protected Listener getListener() {
                    return new Listener() {
                        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
                        private void onManipulate(PlayerArmorStandManipulateEvent e) {
                            if (getManager().isChair(e.getRightClicked())) {
                                e.setCancelled(true);
                            }
                        }
                    };
                }

                @Override
                protected boolean isStair(Block b) {
                    return b.getState() instanceof Stairs;
                }

                @Override
                protected boolean isSlab(Block b) {
                    try {
                        return XMaterial.matchXMaterial(b.getType()).name().endsWith("_SLAB");
                    } catch (Throwable ignore) {
                    }

                    return false;
                }

                @Override
                protected boolean hasEmptyHands(Player p) {
                    return p.getItemInHand().getType() == Material.AIR;
                }
            };
        }
    }

    public static ChairManager getManager() {
        return chairManager;
    }

    public static BetterChairsPlugin getInstance() {
        return plugin;
    }
}