package net.blackscarx.betterchairs;

import net.blackscarx.betterchairs.Files.Config;
import net.blackscarx.betterchairs.Files.Messages;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class ChairsPlugin extends JavaPlugin implements Listener {
    public ChairsPlugin plugin;
    //    public boolean isRegister = false;
    public List<UUID> disableList = new ArrayList<>();
    public List<UUID> uuidList = new ArrayList<>();

    @Override
    public void onEnable() {
        Config.init(this);
        Messages.init(this);
        Objects.requireNonNull(getCommand("betterchairsreload")).setExecutor(new CmdReload());
        Objects.requireNonNull(getCommand("betterchairstoggle")).setExecutor(new ChairsToggle());
        Objects.requireNonNull(getCommand("betterchairsreset")).setExecutor(new ChairsReset());
        plugin = this;
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        for (ChairsConf cc : TempGlobal.list.values()) {
            Player p = cc.getP();
            p.teleport(cc.getLoc());
        }
    }

    /**
     * Sit the player on stairs
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

        //Check if the config use sign or stairs
//        if (Config.getConfig().getBoolean("Need to sign or chair on each side")) {
//            short data = b.getState().getData().toItemStack().getDurability();
//            Block right = b.getRelative(data == 0 ? BlockFace.SOUTH : data == 1 ? BlockFace.NORTH : data == 2 ? BlockFace.WEST : BlockFace.EAST);
//            Block left = b.getRelative(data == 0 ? BlockFace.NORTH : data == 1 ? BlockFace.SOUTH : data == 2 ? BlockFace.EAST : BlockFace.WEST);
//            if (!((/*right.getType().equals(Material.WALL_SIGN) ||*/ getConfig().getStringList("Enable Stairs Block")
//                    .contains(StairsBlock.from(right.getType()))) && (/*left.getType().equals(Material.WALL_SIGN) ||*/
//                    getConfig().getStringList("Enable Stairs Block").contains(StairsBlock.from(left.getType()))))) {
//                if (getConfig().getBoolean("Send message if the Chairs need sign or chair", false)) {
//                    p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig().getString("Message to send if the chairs need sign or chair", "&cIf you want to sit on this stairs you need to place a sign or stairs on each side")));
//                }
//                return;
//            }
//        }

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
                        uuidList.remove(pf.getUniqueId());
                    }
                }.runTaskLater(this, 2L);
                return;
            }
        }
        //Check the distance of the chairs
        if (p.getLocation().distance(b.getLocation().add(0.5, 0, 0.5)) >= Config.getConfig().getDouble("Distance of the stairs", 2.0)) {
            return;
        }

        Location loc = b.getLocation().add(0.5, -1.2, 0.5);

//        if (!event.isCancelled()) {
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

        if (Config.getConfig().getBoolean("Send message when player sit", false)) {
            if (!uuidList.contains(p.getUniqueId()))
                p.sendMessage(ChatColor.translateAlternateColorCodes('&', Messages.getConfig()
                        .getString("Message to send when player sit", "&aYou are now sitting. Take a break.")));
            uuidList.add(p.getUniqueId());
            final Player pf = p;
            new BukkitRunnable() {
                @Override
                public void run() {
                    uuidList.remove(pf.getUniqueId());
                }
            }.runTaskLater(this, 2L);
        }
//        }
    }

    @SuppressWarnings("unused")
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
                        uuidList.remove(pf.getUniqueId());
                    }
                }.runTaskLater(this, 2L);
            }
            return false;
        }
        return true;
    }

    /**
     * Command for reload the config
     */
    public class CmdReload implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
            Config.reload();
            reloadConfig();
            Messages.init(plugin);
//            if (Config.getConfig().getBoolean("Update Checker", true)) {
//                if (!isRegister) {
//                    Bukkit.getPluginManager().registerEvents(um, plugin);
//                    isRegister = true;
//                }
//            } else {
//                if (isRegister) {
//                    HandlerList.unregisterAll(um);
//                    isRegister = false;
//                }
//            }
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
    private static class ChairsReset implements CommandExecutor {
        @Override
        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
//            for (World world : Bukkit.getWorlds()) {
//                for (Entity entity : world.getEntities()) {
//                    if (entity instanceof ArmorStand) {
//                        if (nms.check((ArmorStand) entity)) {
//                            nms.kill((ArmorStand) entity);
//                        }
//                    }
//                }
//            }

            TempGlobal.list.clear();
            sender.sendMessage("§aBetterChairs reset");
            return true;
        }
    }
}