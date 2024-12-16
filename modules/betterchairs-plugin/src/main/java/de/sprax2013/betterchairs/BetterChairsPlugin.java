package de.sprax2013.betterchairs;

import de.sprax2013.lime.spigot.LimeDevUtilitySpigot;
import de.sprax2013.lime.spigot.nms.NmsVersionDetector;
import de.tr7zw.changeme.nbtapi.utils.MinecraftVersion;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
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

import java.util.logging.Level;

public class BetterChairsPlugin extends JavaPlugin {
    private static BetterChairsPlugin plugin;
    private static ChairManager chairManager;

    @Override
    public void onEnable() {
        plugin = this;

        LimeDevUtilitySpigot.init(this);  // Initialize LimeDevUtility
        MinecraftVersion.disableUpdateCheck();
        MinecraftVersion.getLogger().setLevel(Level.WARNING);   // Hide info messages from NBT-API

        ChairNMS chairNMS = getNewNMSInstance();

        if (chairNMS.getListener() != null) {
            Bukkit.getPluginManager().registerEvents(chairNMS.getListener(), this);
        }

        // Should be initiated as soon as possible (required in API submodule)
        chairManager = new ChairManager(this, chairNMS);

        // Init configuration files
        Settings.reload();
        Messages.reload();

        // Start Updater
        new Updater(this);

        // Register Bukkit Event Listener
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        // Register CommandExecutor
        BetterChairsCommand cmdExecutor = new BetterChairsCommand(this);
        getCommand(getName()).setExecutor(cmdExecutor);
        getCommand("toggleChairs").setExecutor(cmdExecutor);
        getCommand("sit").setExecutor(cmdExecutor);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceholderApiExpansion(this).register();
        }

        // Load bStats
        try {
            Metrics bStats = new Metrics(this, 8214);

            // Custom chart: NMS Version
            String nmsVersion = NmsVersionDetector.detect().orElse(Bukkit.getServer().getBukkitVersion());
            bStats.addCustomChart(new SimplePie("nms_version", () -> nmsVersion));
        } catch (Exception ex) {
            // Does not work on Spigot 1.8.0 (gson is missing)
            getLogger().warning("Could not load bStats (" + ex.getClass().getSimpleName() + "): " + ex.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (getManager() != null) {
            getManager().destroyAll(true, true);

            for (Player online : Bukkit.getOnlinePlayers()) {
                getManager().onQuit(online.getUniqueId());
            }

            Settings.reset();
            Messages.reset();
        }

        chairManager = null;
        ChairManager.instance = null;
        ChairManager.plugin = null;
    }

    /**
     * Instantiates an ChairNMS <i>class</i> depending on the server version
     *
     * @return new instance of {@code ChairNMS}, never null
     */
    @NotNull
    private ChairNMS getNewNMSInstance() {
        String version = NmsVersionDetector.detect().orElse(null);

        try {
            assert version != null;
            return (ChairNMS) Class.forName("betterchairs.nms." + version + "." + version).getConstructors()[0].newInstance();
        } catch (Exception ignore) {
            getLogger().warning("Your server version (" + (version != null ? version : "UNKNOWN") + ") is not fully supported - Loading fallback...");

            // Loading fallback when NMS not available
            return new ChairNMS() {
                // Surrounding some code with try-catch because this is meant to be a fallback
                // So in theory it should even work on 1.4.7 servers (please don't try >_<)

                @Override
                @NotNull
                public Entity spawnChairEntity(@NotNull Location loc, int regenerationAmplifier, boolean useArmorStand) {
                    Entity chairEntity;

                    if (useArmorStand) {
                        chairEntity = loc.getWorld().spawn(loc, ArmorStand.class);
                    } else {
                        chairEntity = loc.getWorld().spawn(loc, Arrow.class);
                    }

                    ChairUtils.applyChairProtections(chairEntity);

                    // TODO: Support regeneration effect without overwriting #tick()

                    return chairEntity;
                }

                @Override
                public void killChairEntity(@NotNull Entity entity) {
                    entity.remove();
                }

                @Override
                public Listener getListener() {
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
                                    getManager().isChair(e.getEntity())) {
                                e.setCancelled(true);
                            }
                        }
                    };
                }

                @Override
                public boolean isStair(@NotNull Block block) {
                    return block.getState().getData() instanceof Stairs;
                }

                @Override
                public boolean isStairUpsideDown(@NotNull Block block) {
                    try {
                        return ((Stairs) block.getState().getData()).isInverted();
                    } catch (Exception ignore) {
                        // Feature not supported on this version of the Bukkit-api
                    }

                    return false;
                }

                @Override
                @NotNull
                public BlockFace getBlockRotation(@NotNull Block block) {
                    return ChairUtils.getBlockRotationLegacy(block);
                }

                @Override
                public boolean isSlab(@NotNull Block block) {
                    return (block.getState().getData() instanceof Step ||
                            block.getState().getData() instanceof WoodenStep) &&
                            !block.getType().name().equals("DOUBLE_STEP") &&
                            !block.getType().name().equals("WOOD_DOUBLE_STEP");
                }

                @Override
                public boolean isSlabTop(@NotNull Block block) {
                    if (block.getState().getData() instanceof Step) {
                        return ((Step) block.getState().getData()).isInverted();
                    } else if (block.getState().getData() instanceof WoodenStep) {
                        return ((WoodenStep) block.getState().getData()).isInverted();
                    }

                    return false;
                }

                @Override
                public boolean hasEmptyMainHand(@NotNull Player player) {
                    return player.getInventory().getItemInHand().getType() == Material.AIR;
                }

                @Override
                public boolean isChair(@NotNull Entity entity) {
                    // We cannot check using NMS when fallback NMS does not use NMS to spawn an ArmorStand
                    // Other plugins preventing our ArmorStand from spawning can thus not be prevented from such behavior

                    return false;
                }
            };
        }
    }

    protected static ChairManager getManager() {
        return chairManager;
    }

    public static BetterChairsPlugin getInstance() {
        return plugin;
    }
}
