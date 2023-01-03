package de.sprax2013.betterchairs;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static de.sprax2013.betterchairs.BetterChairsPlugin.getManager;

// TODO: Put all strings into messages.yml
// TODO: Split file into smaller ones
public class BetterChairsCommand implements CommandExecutor, TabCompleter {
    private final String permsSit, permsToggle, permsReload, permsReset;

    protected BetterChairsCommand(JavaPlugin plugin) {
        this.permsSit = plugin.getName() + ".cmd.sit";
        this.permsToggle = plugin.getName() + ".cmd.toggle";

        this.permsReload = plugin.getName() + ".cmd.reload";
        this.permsReset = plugin.getName() + ".cmd.reset";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(this.permsToggle) &&
                !sender.hasPermission(this.permsReload) &&
                !sender.hasPermission(this.permsReset) &&
                !sender.hasPermission(this.permsSit)) {
            sender.sendMessage(Messages.getString(Messages.NO_PERMISSION));
        }

        boolean showHelp = false;

        if (cmd.getName().equalsIgnoreCase("toggleChairs")) {
            if (args.length > 0) {
                if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                    handleToggleChairs(sender, args[0].equalsIgnoreCase("off"));
                } else if (args[0].equalsIgnoreCase("status")) {
                    if (!(sender instanceof Player)) {
                        sender.sendMessage(Messages.getPrefix() + " §cOnly players may toggle chairs");
                        return true;
                    }

                    sender.sendMessage(Messages.getString(
                            getManager().hasChairsDisabled((Player) sender) ?
                                    Messages.TOGGLE_STATUS_DISABLED :
                                    Messages.TOGGLE_STATUS_ENABLED));
                } else {
                    showHelp = true;
                }
            } else {
                handleToggleChairs(sender);
            }
        } else if (cmd.getName().equalsIgnoreCase("sit")) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Messages.getPrefix() + " §cOnly players are able to sit");
                return true;
            }

            Player p = (Player) sender;

            Block b = null;

            if (p.getLocation().getBlock().getType().isSolid()) {
                b = p.getLocation().getBlock();
            } else if (p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().isSolid()) {
                b = p.getLocation().getBlock().getRelative(BlockFace.DOWN);
            }

            if (b != null) {
                getManager().create(p, b);
            } else {
                sender.sendMessage(Messages.getPrefix() + " §cYou need to be on solid ground");
            }
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                handleToggleChairs(sender);
            } else if (args[0].equalsIgnoreCase("on") || args[0].equalsIgnoreCase("off")) {
                handleToggleChairs(sender, args[0].equalsIgnoreCase("off"));
            } else if (args[0].equalsIgnoreCase("status")) {
                if (!(sender instanceof Player)) {
                    sender.sendMessage(Messages.getPrefix() + " §cOnly players may toggle chairs");
                    return true;
                }

                sender.sendMessage(Messages.getString(
                        getManager().hasChairsDisabled((Player) sender) ?
                                Messages.TOGGLE_STATUS_DISABLED :
                                Messages.TOGGLE_STATUS_ENABLED));
            } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (sender.hasPermission(this.permsReload)) {
                    if (Messages.reload()) {
                        sender.sendMessage(Messages.getPrefix() + " §aSuccessfully reloaded §6messages.yml§a!");
                    } else {
                        sender.sendMessage(Messages.getPrefix() + " §cCould not reload §6messages.yml §7- §cCheck server logs for more information");
                    }

                    if (Settings.reload()) {
                        sender.sendMessage(Messages.getPrefix() + " §aSuccessfully reloaded §6config.yml§a!");
                    } else {
                        sender.sendMessage(Messages.getPrefix() + " §cCould not reload §6config.yml §7- §cCheck server logs for more information");
                    }
                } else {
                    sender.sendMessage(Messages.getString(Messages.NO_PERMISSION));
                }
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (sender.hasPermission(this.permsReset)) {
                    int chairCount = getManager().destroyAll(true);

                    if (chairCount > 0) {
                        sender.sendMessage(Messages.getPrefix() + " §aSuccessfully removed §6" + chairCount + " players§a from their chairs");
                    } else {
                        sender.sendMessage(Messages.getPrefix() + " §4There are no chairs that could be removed");
                    }
                } else {
                    sender.sendMessage(Messages.getString(Messages.NO_PERMISSION));
                }
            } else {
                showHelp = true;
            }
        } else {
            showHelp = true;
        }

        if (showHelp) {
            sender.sendMessage(Messages.getPrefix() + " §3/" + cmd.getName() + " toggle §7(§eor §3/bct§7, §3/toggleChairs§7)");   // TODO: allow player(+ @a,@r,@p) as arg
            sender.sendMessage(Messages.getPrefix() + " §3/" + cmd.getName() + " on§7/§3off §7(§eor §3/bct on§7/§3off§7)");   // TODO: allow player(+ @a,@r,@p) as arg
            sender.sendMessage(Messages.getPrefix() + " §3/" + cmd.getName() + " reload§7/§3rl");
            sender.sendMessage(Messages.getPrefix() + " §3/" + cmd.getName() + " reset"); // TODO: allow player(+ @a,@r,@p) as arg
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (!cmd.getName().equalsIgnoreCase("sit")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();

                if (sender.hasPermission(this.permsToggle)) {
                    if ("toggle".startsWith(arg)) {
                        result.add("toggle");
                    }

                    if ("on".startsWith(arg)) {
                        result.add("on");
                    }

                    if ("off".startsWith(arg)) {
                        result.add("off");
                    }

                    if ("status".startsWith(arg)) {
                        result.add("status");
                    }
                }

                if (!cmd.getName().equalsIgnoreCase("toggleChairs")) {
                    if ("reload".startsWith(arg) && sender.hasPermission(this.permsReload)) {
                        result.add("reload");
                    }

                    if ("reset".startsWith(arg) && sender.hasPermission(this.permsReset)) {
                        result.add("reset");
                    }
                }
            }
        }

        return result;
    }

    private void handleToggleChairs(CommandSender sender) {
        handleToggleChairs(sender, sender instanceof Player && !getManager().hasChairsDisabled((Player) sender));
    }

    private void handleToggleChairs(CommandSender sender, boolean disableChairs) {
        if (!sender.hasPermission(this.permsToggle)) {
            sender.sendMessage(Messages.getString(Messages.NO_PERMISSION));
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.getPrefix() + " §cOnly players may toggle chairs");
            return;
        }

        getManager().setChairsDisabled((Player) sender, disableChairs);

        sender.sendMessage(Messages.getString(disableChairs ? Messages.TOGGLE_DISABLED : Messages.TOGGLE_ENABLED));
    }
}
