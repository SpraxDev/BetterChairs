package de.sprax2013.betterchairs;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerArmorStandManipulateEvent;
import org.bukkit.material.Stairs;
import org.bukkit.material.Step;
import org.bukkit.material.WoodenStep;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;

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

        Bukkit.getPluginManager().registerEvents(new EventListener(), this);
    }

    @Override
    public void onDisable() {
        // Remove all chairs
        if (getManager() != null) {
            for (Chair c : new ArrayList<>(getManager().chairs)) {
                getManager().destroy(c, true);
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
            // Try loading NMS class (package is remapped by maven-shade-plugin)
            return (ChairNMS) Class.forName("nms." + version).newInstance();
        } catch (Throwable ignore) {
            System.err.println("[" + plugin.getName() + "] Your server version (" + version +
                    ") is not fully supported - Loading fallback...");

            // Loading fallback when NMS not available
            return new ChairNMS() {
                // Surrounding some code with try-catch because this is meant to be a fallback
                // So in theory it should even work on 1.4.7 servers (please no >_<)

                // TODO: Support regeneration effect without overwriting #tick()

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

                        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
                        private void onDamage(EntityDamageEvent e) {
                            if (e.getEntity() instanceof ArmorStand &&
                                    getManager().isChair((ArmorStand) e.getEntity())) {
                                e.setCancelled(true);
                            }
                        }
                    };
                }

                @Override
                protected boolean isStair(Block block) {
                    return block.getState().getData() instanceof Stairs;
                }

                @Override
                protected boolean isStairUpsideDown(Block block) {
                    try {
                        return ((Stairs) block.getState().getData()).isInverted();
                    } catch (Throwable ignore) {
                    }

                    return false;
                }

                @Override
                protected boolean isSlab(Block block) {
                    return block.getState().getData() instanceof Step ||
                            block.getState().getData() instanceof WoodenStep;
                }

                @Override
                protected boolean isSlabTop(Block block) {
                    if (block.getState().getData() instanceof Step) {
                        return ((Step) block.getState().getData()).isInverted();
                    } else if (block.getState().getData() instanceof WoodenStep) {
                        return ((WoodenStep) block.getState().getData()).isInverted();
                    }

                    return false;
                }

                @Override
                protected boolean hasEmptyHands(Player player) {
                    return player.getInventory().getItemInHand().getType() == Material.AIR;
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