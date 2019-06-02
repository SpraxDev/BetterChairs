/*
 * Copyright (c) BlackScarx
 */

package net.blackscarx.betterchairs;

import net.blackscarx.betterchairs.Files.Config;
import net.blackscarx.betterchairs.Files.Messages;
import net.blackscarx.betterchairs.NMSManager.NMS_V1_10_R1.v1_10_R1;
import net.blackscarx.betterchairs.NMSManager.NMS_V1_11_R1.v1_11_R1;
import net.blackscarx.betterchairs.NMSManager.NMS_V1_13_R2.v1_13_R2;
import net.blackscarx.betterchairs.NMSManager.NMS_V1_14_R1.v1_14_R1;
import net.blackscarx.betterchairs.NMSManager.NMS_v1_8_R1.v1_8_R1;
import net.blackscarx.betterchairs.NMSManager.NMS_v1_8_R2.v1_8_R2;
import net.blackscarx.betterchairs.NMSManager.NMS_v1_8_R3.v1_8_R3;
import net.blackscarx.betterchairs.NMSManager.NMS_v1_9_R1.v1_9_R1;
import net.blackscarx.betterchairs.NMSManager.NMS_v1_9_R2.v1_9_R2;
import net.blackscarx.betterchairs.events.PlayerEnteringChairEvent;
import net.blackscarx.betterchairs.events.PlayerLeavingChairEvent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPistonExtendEvent;
import org.bukkit.event.block.BlockPistonRetractEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.material.Stairs;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class ChairsPlugin extends JavaPlugin implements Listener {

    public static Map<Integer, ChairsConf> list = new HashMap<>();
    public static NMS nms;
    public UpdateManager um;
    public ChairsPlugin plugin;
    public boolean isRegister = false;
    public List<UUID> disableList = new ArrayList<>();
    public List<UUID> uuidList = new ArrayList<>();

    public static NMS getNMS() {
        return nms;
    }

    @Override
    public void onEnable() {
        if (setUpNms()) {
            getLogger().info("NMS hooked !");
        } else {
            getLogger().severe("Failed to setup nms !");
            getLogger().severe("Your server version is not compatible with this plugin!");
            Bukkit.getPluginManager().disablePlugin(this);
            return;
        }
        new SlabBlock(Material.STEP, (short) 0, "stone_slab");
        new SlabBlock(Material.STEP, (short) 1, "sandstone_slab");
        new SlabBlock(Material.STEP, (short) 3, "cobblestone_slab");
        new SlabBlock(Material.STEP, (short) 4, "bricks_slab");
        new SlabBlock(Material.STEP, (short) 5, "stone_bricks_slab");
        new SlabBlock(Material.STEP, (short) 6, "nether_brick_slab");
        new SlabBlock(Material.STEP, (short) 7, "quartz_slab");
        new SlabBlock(Material.WOOD_STEP, (short) 0, "oak_wood_slab");
        new SlabBlock(Material.WOOD_STEP, (short) 1, "spruce_wood_slab");
        new SlabBlock(Material.WOOD_STEP, (short) 2, "brich_wood_slab");
        new SlabBlock(Material.WOOD_STEP, (short) 3, "jungle_wood_slab");
        new SlabBlock(Material.WOOD_STEP, (short) 4, "acacia_wood_slab");
        new SlabBlock(Material.WOOD_STEP, (short) 5, "dark_oak_wood_slab");
        new SlabBlock(Material.STONE_SLAB2, (short) 0, "red_sandstone_slab");
        if (nms.getVersion().equals("v1_9_R1") || nms.getVersion().equals("v1_10_R1") || nms.getVersion().equals("v1_11_R1"))
            new SlabBlock(Material.PURPUR_SLAB, (short) 0, "purpur_slab");
        Config.init();
        Messages.init();
        getCommand("betterchairsreload").setExecutor(new CmdReload());
        getCommand("betterchairstoggle").setExecutor(new ChairsToggle());
        getCommand("betterchairsreset").setExecutor(new ChairsReset());
        um = new UpdateManager();
        plugin = this;
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
        if (Config.getConfig().getBoolean("Update Checker", true)) {
            pm.registerEvents(um, this);
            isRegister = true;
        }
        try {
            Metrics metrics = new Metrics(this);
            metrics.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set the nms version
     * @return nms
     */

    private Boolean setUpNms() {
        String version;
        try {
            version = Bukkit.getServer().getClass().getPackage().getName().replace(".", ",").split(",")[3];
        } catch (ArrayIndexOutOfBoundsException whatVersionAreYouUsingException) {
            return false;
        }
        getLogger().info("Your server is running version " + version);
        switch (version) {
            case "v1_14_R1":
                nms = new v1_14_R1();
                break;
            case "v1_13_R2":
                nms = new v1_13_R2();
                break;
            case "v1_11_R1":
                nms = new v1_11_R1();
                break;
            case "v1_10_R1":
                nms = new v1_10_R1();
                break;
            case "v1_9_R2":
                nms = new v1_9_R2();
                break;
            case "v1_9_R1":
                nms = new v1_9_R1();
                break;
            case "v1_8_R3":
                nms = new v1_8_R3();
                break;
            case "v1_8_R2":
                nms = new v1_8_R2();
                break;
            case "v1_8_R1":
                nms = new v1_8_R1();
                break;
        }
        return nms != null;
    }

    @Override
    public void onDisable() {
        for (ChairsConf cc : list.values()) {
            Player p = cc.getP();
            p.teleport(cc.getLoc());
        }
    }

    /**
     * Sit the player on stairs
     * @param e
     */

    @EventHandler
    public void spawnStairs(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //Check if the player have chairs disable
        if (disableList.contains(p.getUniqueId()))
            return;
        //Check if the action is right click
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        //Get the clicked block
        Block b = e.getClickedBlock();
        //Check if the block is a Stairs
        if (!(b.getState().getData() instanceof Stairs))
            return;
        //Check if the stairs face down
        if (b.getState().getData().toItemStack().getDurability() > 3)
            return;
        //Check if the stairs if the stairs is valide
        if (StairsBlock.from(b.getType()) == null)
            return;
        //Check if the stairs is enable
        if (!getConfig().getStringList("Enable Stairs Block").contains(StairsBlock.from(b.getType())))
            return;
        //Check if the player sneak
        if (p.isSneaking())
            return;
        //Check if the config use sign or stairs
        if (Config.getConfig().getBoolean("Need to sign or chair on each side")) {
            short data = b.getState().getData().toItemStack().getDurability();
            Block right = b.getRelative(data == 0 ? BlockFace.SOUTH : data == 1 ? BlockFace.NORTH : data == 2 ? BlockFace.WEST : BlockFace.EAST);
            Block left = b.getRelative(data == 0 ? BlockFace.NORTH : data == 1 ? BlockFace.SOUTH : data == 2 ? BlockFace.EAST : BlockFace.WEST);
            if (!((right.getType().equals(Material.WALL_SIGN) || getConfig().getStringList("Enable Stairs Block").contains(StairsBlock.from(right.getType()))) && (left.getType().equals(Material.WALL_SIGN) || getConfig().getStringList("Enable Stairs Block").contains(StairsBlock.from(left.getType()))))) {
                if (getConfig().getBoolean("Send message if the Chairs need sign or chair", false)) {
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send if the chairs need sign or chair", "&cIf you want to sit on this stairs you need to place a sign or stairs on each side")));
                }
                return;
            }
        }
        //Check no item in hand
        if (getConfig().getBoolean("No item in hand")) {
            if (!(nms.getVersion().equals("v1_9_R1") || nms.getVersion().equals("v1_9_R2") || nms.getVersion().equals("v1_10_R1") || nms.getVersion().equals("v1_11_R1"))) {
                if (p.getItemInHand().getType() != Material.AIR)
                    return;
            } else {
                if (p.getInventory().getItemInMainHand().getType() != Material.AIR || p.getInventory().getItemInOffHand().getType() != Material.AIR)
                    return;

            }
        }
        e.setCancelled(true);
        //Check if the plugin use permission for sit
        if (Config.getConfig().getBoolean("Use permission for sit", false)) {
            if (!p.hasPermission("betterchairs.use")) {
                if (!uuidList.contains(p.getUniqueId()))
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Cant use message")));
                uuidList.add(p.getUniqueId());
                final Player pf = p;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (uuidList.contains(pf.getUniqueId()))
                            uuidList.remove(pf.getUniqueId());
                    }
                }.runTaskLater(this, 2L);
                return;
            }
        }
        //Check if the world is valid
        if(!isValidWorld(p))
            return;
        Location loc = b.getLocation().add(0.5, -1.2, 0.5);
        //Check the distance of the chairs
        if (p.getLocation().distance(b.getLocation().add(0.5, 0, 0.5)) >= Config.getConfig().getDouble("Distance of the stairs", 2.0))
            return;
        //Check if the block is used by another player
        if (ChairsConf.isUsed(b.getState())) {
            if (Config.getConfig().getBoolean("Send message if the chairs is already occupied", false))
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send if the chairs is occupied")));
            return;
        }
        //Check if the player is already sit
        if (ChairsConf.isSit(p))
            return;
        
		PlayerEnteringChairEvent event = new PlayerEnteringChairEvent(p, ChairType.STAIR, b);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			Location pLoc = p.getLocation();
			// Check auto turn
			if (Config.getConfig().getBoolean("AutoTurn", true)) {
				short data = b.getState().getData().toItemStack().getDurability();
				switch (data) {
				case 0:
					p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
							p.getLocation().getZ(), 90f, 0f));
					loc.setYaw(90f);
					break;
				case 1:
					p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
							p.getLocation().getZ(), -90f, 0f));
					loc.setYaw(-90f);
					break;
				case 2:
					p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
							p.getLocation().getZ(), -180f, 0f));
					loc.setYaw(-180f);
					break;
				case 3:
					p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
							p.getLocation().getZ(), 0f, 0f));
					loc.setYaw(0f);
					break;
				default:
					break;
				}
			}
			// Spawn the armostand
			ArmorStand stand = nms.spawn(loc, p);
			// Add the armorstand and the ChairsConf in the list
			list.put(getEntityId(stand), new ChairsConf(b.getState(), p, pLoc));
			if (Config.getConfig().getBoolean("Send message when player sit", false)) {
				if (!uuidList.contains(p.getUniqueId()))
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig()
							.getString("Message to send when player sit", "&aYou are now sitting. Take a break.")));
				uuidList.add(p.getUniqueId());
				final Player pf = p;
				new BukkitRunnable() {
					@Override
					public void run() {
						if (uuidList.contains(pf.getUniqueId()))
							uuidList.remove(pf.getUniqueId());
					}
				}.runTaskLater(this, 2L);
			}
		}
    }

    /**
     * Sit the player on slab
     * @param e
     */

    @EventHandler
    public void slabSpawn(PlayerInteractEvent e) {
        //Same of chairsSpawn but for slab
        Player p = e.getPlayer();
        if (disableList.contains(p.getUniqueId()))
            return;
        if (!e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
            return;
        if (!getConfig().getBoolean("Use slab"))
            return;
        Block b = e.getClickedBlock();
        SlabBlock slabBlock = null;
        for (SlabBlock slab : SlabBlock.getList()) {
            if (!slab.getType().equals(b.getType()))
                continue;
            if (!slab.getData().equals(b.getState().getData().toItemStack().getDurability()))
                continue;
            slabBlock = slab;
            break;
        }
        if (slabBlock == null)
            return;
        if (!getConfig().getStringList("Enable Slab Block").contains(slabBlock.getName()))
            return;
        if (p.isSneaking())
            return;
        if (getConfig().getBoolean("No item in hand")) {
            if (!(nms.getVersion().equals("v1_9_R1") || nms.getVersion().equals("v1_9_R2") || nms.getVersion().equals("v1_10_R1") || nms.getVersion().equals("v1_11_R1"))) {
                if (p.getItemInHand().getType() != Material.AIR)
                    return;
            } else {
                if (p.getInventory().getItemInMainHand().getType() != Material.AIR || p.getInventory().getItemInOffHand().getType() != Material.AIR)
                    return;
            }
        }
        e.setCancelled(true);
        if (Config.getConfig().getBoolean("Use permission for sit", false)) {
            if (!p.hasPermission("betterchairs.use")) {
                if (!uuidList.contains(p.getUniqueId()))
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Cant use message")));
                uuidList.add(p.getUniqueId());
                final Player pf = p;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (uuidList.contains(pf.getUniqueId()))
                            uuidList.remove(pf.getUniqueId());
                    }
                }.runTaskLater(this, 2L);
                return;
            }
        }
        if (!isValidWorld(p))
            return;
        Location loc = b.getLocation().add(0.5, -1.2, 0.5);
        if (p.getLocation().distance(b.getLocation().add(0.5, 0, 0.5)) >= Config.getConfig().getDouble("Distance of the stairs", 2.0))
            return;
        if (ChairsConf.isUsed(b.getState())) {
            if (Config.getConfig().getBoolean("Send message if the chairs is already occupied", false))
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send if the chairs is occupied")));
            return;
        }
        if (ChairsConf.isSit(p))
            return;
        
        PlayerEnteringChairEvent event = new PlayerEnteringChairEvent(p, ChairType.SLAP, b);
		Bukkit.getPluginManager().callEvent(event);

		if (!event.isCancelled()) {
			Location pLoc = p.getLocation();
			ArmorStand stand = nms.spawn(loc, p);
			list.put(getEntityId(stand), new ChairsConf(b.getState(), p, pLoc));
			if (Config.getConfig().getBoolean("Send message when player sit", false)) {
				if (!uuidList.contains(p.getUniqueId()))
					p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig()
							.getString("Message to send when player sit", "&aYou are now sitting. Take a break.")));
				uuidList.add(p.getUniqueId());
				final Player pf = p;
				new BukkitRunnable() {
					@Override
					public void run() {
						if (uuidList.contains(pf.getUniqueId()))
							uuidList.remove(pf.getUniqueId());
					}
				}.runTaskLater(this, 2L);
			}
		}
    }

    /**
     * Check if the world is in the config
     * @param p
     * @return boolean
     */

    private boolean isValidWorld(Player p) {
        if (Config.getConfig().getStringList("Disable world").contains(p.getWorld().getName())) {
            if (Config.getConfig().getBoolean("Send message if the word is disable", false)) {
                if (!uuidList.contains(p.getUniqueId()))
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send if the word is disable", "&cThe Chairs is not enable in this world")));
                uuidList.add(p.getUniqueId());
                final Player pf = p;
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (uuidList.contains(pf.getUniqueId()))
                            uuidList.remove(pf.getUniqueId());
                    }
                }.runTaskLater(this, 2L);
            }
            return false;
        }
        return true;
    }

    /**
     * Cancel the break of the chairs
     * @param e
     */

    @EventHandler
    public void blockBreak(BlockBreakEvent e) {
        if (ChairsConf.isUsed(e.getBlock().getState()))
            e.setCancelled(true);
    }

    /**
     * Override the spawn of the ArmorStand
     * @param e
     */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn(EntitySpawnEvent e) {
        if (!(e.getEntity() instanceof ArmorStand))
            return;
        ArmorStand entity = (ArmorStand) e.getEntity();
        if (nms.check(entity)) {
            e.setCancelled(false);
        }
    }

    /**
     * Override the spawn of the ArmorStand
     * @param e
     */

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onSpawn2(CreatureSpawnEvent e) {
        if (!(e.getEntity() instanceof ArmorStand))
            return;
        ArmorStand entity = (ArmorStand) e.getEntity();
        if (nms.check(entity)) {
            e.setCancelled(false);
        }
    }

    /**
     * Teleport fixer
     * @param e
     */

    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        Location loc = e.getTo();
        Collection<Entity> list = getNearbyEntities(loc, 0.1, 0.1, 0.1);
        for (Entity ent : list) {
            if (ent instanceof Player) {
                Player targetPlayer = (Player) ent;
                if (ChairsConf.isSit(targetPlayer)) {
                    e.setTo(loc.add(0, 1, 0));
                    break;
                }
            }
        }
        if (ChairsConf.isSit(e.getPlayer())) {
			if (e.getPlayer().getVehicle() instanceof ArmorStand) {
				PlayerLeavingChairEvent event = new PlayerLeavingChairEvent(e.getPlayer(), null);
				Bukkit.getPluginManager().callEvent(event);

				if (!event.isCancelled()) {
					ArmorStand armorStand = (ArmorStand) e.getPlayer().getVehicle();
					if (nms.check(armorStand)) {
						ChairsPlugin.list.remove(getEntityId(armorStand));
						nms.kill(armorStand);
					}
				} else {
					e.setCancelled(true);
				}
			}
        }
    }

    /**
     * get nearbies entity because the method don't exist in 1.8
     * @param loc
     * @param x
     * @param y
     * @param z
     * @return list
     */

    private Collection<Entity> getNearbyEntities(Location loc, double x, double y, double z) {
        List<Entity> list = loc.getWorld().getEntities();
        Collection<Entity> finalColl = new ArrayList<>();
        for (Entity e : list) {
            Location l = e.getLocation();
            if (loc.getX() - x < l.getX() && loc.getX() + x > l.getX())
                if (loc.getY() - y < l.getY() && loc.getY() + y > l.getY())
                    if (loc.getZ() - z < l.getZ() && loc.getZ() + z > l.getZ())
                        finalColl.add(e);
        }
        return finalColl;
    }

    /**
     * remove chairs if the player is sit on disconnect
     * @param e
     */

    @EventHandler(priority = EventPriority.LOWEST)
    public void quit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        if (ChairsConf.isSit(p)) {
            if (p.getVehicle() != null) {
				PlayerLeavingChairEvent event = new PlayerLeavingChairEvent(p, null);
				Bukkit.getPluginManager().callEvent(event);

				Integer id = getEntityId(p.getVehicle());
				if (list.containsKey(id)) {
					ChairsConf cc = list.get(id);
					p.teleport(cc.getLoc());
					list.remove(id);
				}
            }
        }
    }

    /**
     * Cancel the push of the chairs
     * @param e
     */

    @EventHandler
    public void pistonExtend(BlockPistonExtendEvent e) {
        for (Block b : e.getBlocks()) {
            if (ChairsConf.isUsed(b.getState()))
                e.setCancelled(true);
        }
    }

    /**
     * Cancel the push of the chairs
     * @param e
     */

    @EventHandler
    public void pistonRetract(BlockPistonRetractEvent e) {
        for (Block b : e.getBlocks()) {
            if (ChairsConf.isUsed(b.getState()))
                e.setCancelled(true);
        }
    }

    /**
     * Because method don't exist in 1.8
     * @param i
     * @return
     */

    public Integer getEntityId(Entity i) {
        try {
            Class<?> CraftEntity = Class.forName("org.bukkit.craftbukkit." + nms.getVersion() + ".entity.CraftEntity");
            Class<?> EntityNMS = Class.forName("net.minecraft.server." + nms.getVersion() + ".Entity");
            Method getHandle = CraftEntity.getDeclaredMethod("getHandle");
            Method getId = EntityNMS.getDeclaredMethod("getId");
            getHandle.setAccessible(true);
            Object nms = getHandle.invoke(i);
            getId.setAccessible(true);
            int id = (int) getId.invoke(nms);
            getId.setAccessible(false);
            getHandle.setAccessible(false);
            return id;
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Command for reload the config
     */

    public class CmdReload implements CommandExecutor {

        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            Config.reload();
            reloadConfig();
            Messages.init();
            if (Config.getConfig().getBoolean("Update Checker", true)) {
                if (!isRegister) {
                    Bukkit.getPluginManager().registerEvents(um, plugin);
                    isRegister = true;
                }
            } else {
                if (isRegister) {
                    HandlerList.unregisterAll(um);
                    isRegister = false;
                }
            }
            commandSender.sendMessage(ChatColor.GREEN + "[BC] Reload successful !");
            return true;
        }
    }

    /**
     * Command for toggle chairs
     */

    public class ChairsToggle implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            if (!(commandSender instanceof Player))
                return true;
            Player p = (Player) commandSender;
            if (disableList.contains(p.getUniqueId())) {
                disableList.remove(p.getUniqueId());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send when player toggle chairs to on", "&aYou can sit now")));
            } else {
                disableList.add(p.getUniqueId());
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send when player toggle chairs to off", "&cYou can't sit now")));
            }
            return true;
        }
    }

    /**
     * Reset the chairs
     */

    private class ChairsReset implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
            for (World world : Bukkit.getWorlds()) {
                for (Entity entity : world.getEntities()) {
                    if (entity instanceof ArmorStand) {
                        if (nms.check((ArmorStand) entity)) {
                            nms.kill((ArmorStand) entity);
                        }
                    }
                }
            }
            list.clear();
            sender.sendMessage("§aBetterChairs reset");
            return true;
        }
    }

}
