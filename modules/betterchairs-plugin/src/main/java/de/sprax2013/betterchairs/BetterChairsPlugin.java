package de.sprax2013.betterchairs;

import org.bstats.bukkit.MetricsLite;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
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

public class BetterChairsPlugin extends JavaPlugin {
    private static BetterChairsPlugin plugin;
    private static ChairManager chairManager;
    private static Updater updater;

    @Override
    public void onEnable() {
        plugin = this;

        ChairNMS chairNMS = getNewNMS();

        if (chairNMS.getListener() != null) {
            Bukkit.getPluginManager().registerEvents(chairNMS.getListener(), this);
        }

        // Should be initiated as soon as possible (required in API submodule)
        chairManager = new ChairManager(this, chairNMS);

        // Start Updater
        updater = new Updater(this);

        // Register Bukkit Event Listener
        Bukkit.getPluginManager().registerEvents(new EventListener(), this);

        // Register CommandExecutor
        BetterChairsCommand cmdExecutor = new BetterChairsCommand(this);
        getCommand(getName()).setExecutor(cmdExecutor);
        getCommand("toggleChairs").setExecutor(cmdExecutor);

        // Load bStats
        //TODO: Sign plugin-jar and append '-UNOFFICIAL' to reported plugin version if missing/invalid signature
        //  This Fork should be considered unofficial as long as it is not approved by the author (but I have maintainer rights,
        //  so reporting to the original bStats page should be fine [hopefully ,_,])
        try {
            // TODO: Add Custom ServerVersion-Pie that shows NMS-Versions when clicked
            new MetricsLite(this, 768); // TODO: Does not work on Spigot 1.8.0? (Can't find gson)
        } catch (Throwable th) {
            System.err.println(Settings.PREFIX_CONSOLE + "Could not load bStats (" + th.getClass().getSimpleName() + "): " +
                    th.getMessage());
        }
    }

    @Override
    public void onDisable() {
        if (getManager() != null) {
            getManager().destroyAll(true, true);
        }

        Settings.reset();

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
    private ChairNMS getNewNMS() {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];

        try {
            // Try loading NMS class (package is remapped by maven-shade-plugin)
            return (ChairNMS) Class.forName("nms." + version).newInstance();
        } catch (Throwable ignore) {
            System.err.println(Settings.PREFIX_CONSOLE + "Your server version (" + version +
                    ") is not fully supported - Loading fallback...");

            // Loading fallback when NMS not available
            return new ChairNMS() {
                // Surrounding some code with try-catch because this is meant to be a fallback
                // So in theory it should even work on 1.4.7 servers (please no >_<)

                @Override
                @NotNull
                public ArmorStand spawnChairArmorStand(@NotNull Location loc, int regenerationAmplifier) {
                    ArmorStand armorStand = loc.getWorld().spawn(loc, ArmorStand.class);
                    ChairUtils.applyBasicChairModifications(armorStand);

                    // TODO: Support regeneration effect without overwriting #tick()

                    return armorStand;
                }

                @Override
                public void killChairArmorStand(@NotNull ArmorStand armorStand) {
                    armorStand.remove();
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
                                    getManager().isChair((ArmorStand) e.getEntity())) {
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
                    } catch (Throwable ignore) {
                    }

                    return false;
                }

                @Override
                @NotNull
                public BlockFace getStairRotation(@NotNull Block block) {
                    try {
                        // TODO: deduplicate code
                        BlockFace blockFace = ((Stairs) block.getState().getData()).getFacing();

                        if (blockFace == BlockFace.NORTH) return BlockFace.SOUTH;
                        if (blockFace == BlockFace.SOUTH) return BlockFace.NORTH;
                        if (blockFace == BlockFace.WEST) return BlockFace.EAST;
                        if (blockFace == BlockFace.EAST) return BlockFace.WEST;

                        return blockFace;
                    } catch (Throwable ignore) {
                    }

                    return BlockFace.SELF;
                }

                @Override
                public boolean isSlab(@NotNull Block block) {
                    return (block.getState().getData() instanceof Step ||
                            block.getState().getData() instanceof WoodenStep) &&
                            block.getType() != Material.DOUBLE_STEP &&
                            block.getType() != Material.WOOD_DOUBLE_STEP;
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
                public boolean hasEmptyHands(@NotNull Player player) {
                    return player.getInventory().getItemInHand().getType() == Material.AIR;
                }
            };
        }
    }

    protected static ChairManager getManager() {
        return chairManager;
    }

//    protected static Updater getUpdater() {
//        return updater;
//    }

    public static BetterChairsPlugin getInstance() {
        return plugin;
    }
}