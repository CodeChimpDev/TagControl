package tagcontrol.alexhh25.com;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import tagcontrol.alexhh25.com.NametagAPI;
import tagcontrol.alexhh25.com.NametagChangeEvent;
import tagcontrol.alexhh25.com.NametagEdit;
import tagcontrol.alexhh25.com.PlayerLoader;
import tagcontrol.alexhh25.com.NametagUtils;
import tagcontrol.alexhh25.com.NametagManager;

/**
 * This class is responsible for handling the /fnt command.
 * 
 * @author Alex Howard-Harris
 *
 */

public class NametagCommand implements CommandExecutor {

        public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
                Player senderPlayer = null;
                if (sender instanceof Player) {
                        senderPlayer = (Player) sender;
                }

                if (cmd.getName().equalsIgnoreCase("tc")) {
                        if (senderPlayer != null) {
                                if (!senderPlayer.hasPermission("tagcontrol.use")) {
                                        sender.sendMessage("§cYou don't have permission to use this plugin.");
                                        return true;
                                }
                        }
                        if (args.length >= 1 && args[0].equalsIgnoreCase("reload")) {
                                if (senderPlayer != null) {
                                        if (!senderPlayer.hasPermission("tagcontrol.reload")) {
                                                sender.sendMessage("§cYou don't have permission to reload this plugin.");
                                                return true;
                                        }
                                }
                                NametagEdit.plugin.load();
                                sender.sendMessage("§eReloaded group nodes and players.");
                                return true;
                        }
                        if (args.length >= 2) {
                                String operation = args[0];
                                String text = NametagUtils.trim(NametagUtils.getValue(getText(args)));
                                String target = args[1];

                                if (senderPlayer != null) {
                                        Player tp = Bukkit.getPlayer(target);
                                        if (tp != null && senderPlayer != tp) {
                                                if (!senderPlayer.hasPermission("tagcontrol.useall")) {
                                                        sender.sendMessage("§cYou can only edit your own nametag.");
                                                        return true;
                                                }
                                        }
                                        else if (!target.equalsIgnoreCase(senderPlayer.getName())) {
                                                if (!senderPlayer.hasPermission("tagcontrol.useall")) {
                                                        sender.sendMessage("§cYou can only edit your own nametag.");
                                                        return true;
                                                }
                                        }
                                }

                                if (operation.equalsIgnoreCase("prefix") || operation.equalsIgnoreCase("suffix")) {
                                        Player targetPlayer;


                                        targetPlayer = Bukkit.getPlayer(target);

                                        if (text.isEmpty()) {
                                                sender.sendMessage("§eNo " + operation.toLowerCase() + " given!");
                                                return true;
                                        }

                                        if (targetPlayer != null) {
                                                if (PlayerLoader.getPlayer(targetPlayer.getName()) == null) {
                            NametagManager.clear(targetPlayer.getName());
                                                }
                                        }

                                        String prefix = "";
                                        String suffix = "";
                    NametagChangeEvent.NametagChangeReason reason = null;
                                        if (operation.equalsIgnoreCase("prefix")) {
                                                prefix = NametagUtils.formatColors(text);
                        reason = NametagChangeEvent.NametagChangeReason.SET_PREFIX;
                    }
                                        else if (operation.equalsIgnoreCase("suffix")) {
                                                suffix = NametagUtils.formatColors(text);
                        reason = NametagChangeEvent.NametagChangeReason.SET_SUFFIX;
                    }

                                        if (targetPlayer != null)
                        setNametagSoft(targetPlayer.getName(), prefix, suffix, reason);
                                        if (targetPlayer != null)
                        PlayerLoader.update(targetPlayer.getName(), prefix, suffix);
                                        else
                                                PlayerLoader.update(target, prefix, suffix);
                                        if (targetPlayer != null)
                                                sender.sendMessage("§eSet " + targetPlayer.getName() + "\'s " + operation.toLowerCase() + " to \'" + text + "\'.");
                                        else
                                                sender.sendMessage("§eSet " + target + "\'s " + operation.toLowerCase() + " to \'" + text + "\'.");
                                }
                                else if (operation.equalsIgnoreCase("clear")) {
                                        Player targetPlayer;


                                        targetPlayer = Bukkit.getPlayer(target);
                                        if (targetPlayer != null)
                                                sender.sendMessage("§eReset " + targetPlayer.getName() + "\'s prefix and suffix.");
                                        else
                                                sender.sendMessage("§eReset " + target + "\'s prefix and suffix.");
                                        if (targetPlayer != null)
                        NametagManager.clear(targetPlayer.getName());
                                        if (targetPlayer != null)
                                                PlayerLoader.removePlayer(targetPlayer.getName(), null);
                                        else
                                                PlayerLoader.removePlayer(target, null);

                                        if (targetPlayer != null)
                                                for (String key : NametagEdit.groups.keySet().toArray(new String[NametagEdit.groups.keySet().size()])) {
                                                        if (targetPlayer.hasPermission(key)) {
                                                                String prefix = NametagEdit.groups.get(key).get("prefix");
                                                                String suffix = NametagEdit.groups.get(key).get("suffix");
                                                                if (prefix != null)
                                                                        prefix = NametagUtils.formatColors(prefix);
                                                                if (suffix != null)
                                                                        suffix = NametagUtils.formatColors(suffix);
                                setNametagHard(targetPlayer.getName(), prefix, suffix, NametagChangeEvent.NametagChangeReason.GROUP_NODE);

                                                                break;
                                                        }
                                                }
                                }
                                else {
                                        sender.sendMessage("§eUnknown operation \'" + operation + "\', type §a/ne§e for help.");
                                        return true;
                                }
                        }
                        else {
                                sender.sendMessage("§e§nTagControl v" + NametagEdit.plugin.getDescription().getVersion() + " command usage:");
                                sender.sendMessage("");
                                sender.sendMessage("§a/tc prefix [player] [text]§e - Sets a player's prefix");
                                sender.sendMessage("§a/tc suffix [player] [text]§e - Sets a player's suffix");
                                sender.sendMessage("§a/tc clear [player]§e - Clears both a player's prefix and suffix.");
                                if (sender instanceof Player && ((Player) sender).hasPermission("fuse.nametag.reload") || !(sender instanceof Player))
                                        sender.sendMessage("§a/tc reload§e - Reloads the configs");
                        }
                }
                return true;
        }

        private String getText(String[] args) {
                String rv = "";
                for (int t = 2; t < args.length; t++) {
                        if (t == args.length - 1)
                                rv += args[t];
                        else
                                rv += args[t] + " ";
                }
                return rv;
        }

    public static void setNametagHard(String player, String prefix, String suffix, NametagChangeEvent.NametagChangeReason reason) {
        NametagChangeEvent e = new NametagChangeEvent(player, NametagAPI.getPrefix(player), NametagAPI.getSuffix(player), prefix, suffix, NametagChangeEvent.NametagChangeType.HARD, reason);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled())
            NametagManager.overlap(player, prefix, suffix);
    }

    static void setNametagSoft(String player, String prefix, String suffix, NametagChangeEvent.NametagChangeReason reason) {
        NametagChangeEvent e = new NametagChangeEvent(player, NametagAPI.getPrefix(player), NametagAPI.getSuffix(player), prefix, suffix, NametagChangeEvent.NametagChangeType.SOFT, reason);
        Bukkit.getServer().getPluginManager().callEvent(e);
        if (!e.isCancelled())
            NametagManager.update(player, prefix, suffix);
    }

}
