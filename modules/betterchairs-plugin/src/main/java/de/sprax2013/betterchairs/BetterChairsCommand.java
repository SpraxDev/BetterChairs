package de.sprax2013.betterchairs;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;

import static de.sprax2013.betterchairs.BetterChairsPlugin.getManager;

public class BetterChairsCommand implements CommandExecutor, TabCompleter {
    private final String PERMS_TOGGLE, PERMS_RELOAD, PERMS_RESET;

    protected BetterChairsCommand(JavaPlugin plugin) {
        this.PERMS_TOGGLE = plugin.getName() + ".cmd.toggle";
        this.PERMS_RELOAD = plugin.getName() + ".cmd.reload";
        this.PERMS_RESET = plugin.getName() + ".cmd.reset";
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!sender.hasPermission(PERMS_TOGGLE) &&
                !sender.hasPermission(PERMS_RELOAD) &&
                !sender.hasPermission(PERMS_RESET)) {
            sender.sendMessage(Messages.noPermission());
        }

        boolean showHelp = false;

        if (cmd.getName().equalsIgnoreCase("toggleChairs")) {
            handleToggleChairs(sender);
        } else if (args.length >= 1) {
            if (args[0].equalsIgnoreCase("toggle")) {
                handleToggleChairs(sender);
            } else if (args[0].equalsIgnoreCase("reload") || args[0].equalsIgnoreCase("rl")) {
                if (sender.hasPermission(PERMS_RELOAD)) {
                    if (Settings.reload()) {
                        sender.sendMessage(Messages.getPrefix() + "§aSuccessfully reloaded §6config.yml§a!");
                    } else {
                        sender.sendMessage(Messages.getPrefix() + "§cCould not reload §6config.yml §7- §cCheck server logs for more information");
                    }

                    if (Messages.reload()) {
                        sender.sendMessage(Messages.getPrefix() + "§aSuccessfully reloaded §6messages.yml§a!");
                    } else {
                        sender.sendMessage(Messages.getPrefix() + "§cCould not reload §6messages.yml §7- §cCheck server logs for more information");
                    }
                } else {
                    sender.sendMessage(Messages.noPermission());
                }
            } else if (args[0].equalsIgnoreCase("reset")) {
                if (sender.hasPermission(PERMS_RESET)) {
                    int chairCount = getManager().destroyAll(true);

                    if (chairCount > 0) {
                        sender.sendMessage(Messages.getPrefix() + "§aSuccessfully removed §6" + chairCount + " players§a from their chairs");
                    } else {
                        sender.sendMessage(Messages.getPrefix() + "§4There are no chairs that could be removed");
                    }
                } else {
                    sender.sendMessage(Messages.noPermission());
                }
            } else {
                showHelp = true;
            }
        } else {
            showHelp = true;
        }

        if (showHelp) {
            sender.sendMessage(Messages.getPrefix() + "§3/" + cmd.getName() + " toggle §7(§eor §3/bct§7, §3/toggleChairs§7)");   // TODO: allow player(+ @a,@r,@p) as arg
            sender.sendMessage(Messages.getPrefix() + "§3/" + cmd.getName() + " reload§7/§3rl");
            sender.sendMessage(Messages.getPrefix() + "§3/" + cmd.getName() + " reset"); // TODO: allow player(+ @a,@r,@p) as arg
        }

        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
        List<String> result = new ArrayList<>();

        if (!cmd.getName().equalsIgnoreCase("toggleChairs")) {
            if (args.length == 1) {
                String arg = args[0].toLowerCase();

                if ("toggle".startsWith(arg) && sender.hasPermission(PERMS_TOGGLE)) {
                    result.add("toggle");
                }

                if ("reload".startsWith(arg) && sender.hasPermission(PERMS_RELOAD)) {
                    result.add("reload");
                }

                if ("reset".startsWith(arg) && sender.hasPermission(PERMS_RESET)) {
                    result.add("reset");
                }
            }
        }

        return result;
    }

    private void handleToggleChairs(CommandSender sender) {
        if (!sender.hasPermission(PERMS_TOGGLE)) {
            sender.sendMessage(Messages.noPermission());
            return;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.getPrefix() + "§cOnly players may toggle chairs");
            return;
        }

        boolean nowDisabled = !getManager().hasChairsDisabled((Player) sender);
        getManager().setChairsDisabled((Player) sender, nowDisabled);

        sender.sendMessage(nowDisabled ? Messages.toggleChairsDisabled() : Messages.toggleChairsEnabled());
    }
}