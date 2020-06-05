package net.blackscarx.betterchairs;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ChairsPlugin extends JavaPlugin implements Listener {
    public ChairsPlugin plugin;
    public List<UUID> disableList = new ArrayList<>();

    @Override
    public void onEnable() {
        plugin = this;
        PluginManager pm = Bukkit.getPluginManager();
        pm.registerEvents(this, this);
    }

    /**
     * Sit the player on stairs
     */
    @EventHandler
    public void spawnStairs(PlayerInteractEvent e) {
        Player p = e.getPlayer();
        //Get the clicked block
//        Block b = e.getClickedBlock();

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

//        Location loc = b.getLocation().add(0.5, -1.2, 0.5);

//        if (!event.isCancelled()) {
        // Check auto turn
//        if (Config.getConfig().getBoolean("AutoTurn", true)) {
//            short data = b.getState().getData().toItemStack().getDurability();
//            switch (data) {
//                case 0:
//                    p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
//                            p.getLocation().getZ(), 90f, 0f));
//                    loc.setYaw(90f);
//                    break;
//                case 1:
//                    p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
//                            p.getLocation().getZ(), -90f, 0f));
//                    loc.setYaw(-90f);
//                    break;
//                case 2:
//                    p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
//                            p.getLocation().getZ(), -180f, 0f));
//                    loc.setYaw(-180f);
//                    break;
//                case 3:
//                    p.teleport(new Location(p.getWorld(), p.getLocation().getX(), p.getLocation().getY(),
//                            p.getLocation().getZ(), 0f, 0f));
//                    loc.setYaw(0f);
//                    break;
//                default:
//                    break;
//            }
//        }
//        }
    }
}