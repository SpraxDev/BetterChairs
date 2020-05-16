package de.sprax2013.betterchairs;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.event.player.PlayerToggleSneakEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.spigotmc.event.entity.EntityDismountEvent;

public class BetterChairsPlugin extends JavaPlugin {
    private static BetterChairsPlugin plugin;
    private static ChairManager chairManager;

    @Override
    public void onEnable() {
        plugin = this;

        ChairNMS chairNMS = getNMS();

        if (chairNMS.getListener() != null) {
            Bukkit.getPluginManager().registerEvents(chairNMS.getListener(), this);
        }

        chairManager = new ChairManager(chairNMS);

        // Load bStats
        new MetricsLite(this, 768);

        // Start Updater
        //TODO: Check if enabled in config
        Bukkit.getPluginManager().registerEvents(new Updater(this), this);

        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            private void onSneak(PlayerToggleSneakEvent e) {
                if (!e.isSneaking()) {
                    ArmorStand armorStand = getManager().chairNMS.spawnChairArmorStand(e.getPlayer().getLocation());
                    getManager().armorStands.add(armorStand);

                    armorStand.setPassenger(e.getPlayer());
                }
            }

            @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
            private void onDismount(EntityDismountEvent e) {
                Entity armorStand = e.getDismounted();

                if (armorStand instanceof ArmorStand && getManager().armorStands.remove(armorStand)) {
                    ChairUtils.destroyChair((ArmorStand) armorStand);
                }
            }
        }, this);
    }

    @Override
    public void onDisable() {
        // Reset all Chairs
        if (getManager() != null) {
            for (ArmorStand armorStand : getManager().armorStands) {
                ChairUtils.destroyChair(armorStand);
            }
            getManager().armorStands.clear();
        }

        chairManager = null;
        ChairManager.instance = null;
    }

    @NotNull
    private ChairNMS getNMS() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            // Try loading NMS class
            return (ChairNMS) Class.forName("nms." + version).newInstance();
        } catch (Throwable th) {
            System.out.println("[" + plugin.getName() + "] Your server version (" + version + ") is not fully supported - Loading fallback...");

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
                            if (getManager().armorStands.contains(e.getRightClicked())) {
                                e.setCancelled(true);
                            }
                        }
                    };
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