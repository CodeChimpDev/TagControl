package tagcontrol.alexhh25.com;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import tagcontrol.alexhh25.com.NametagChangeEvent.NametagChangeReason;
import tagcontrol.alexhh25.com.NametagChangeEvent.NametagChangeType;

import java.util.LinkedHashMap;

/**
 * This API class is used to set prefixes and suffixes at a high level,
 * much alike what the in-game /fnt commands do. These methods fire
 * events, which can be listened to, and cancelled.
 *
 * It is recommended to use this class for light use of NametagEdit.
 */
public class NametagAPI {

    public static void setPrefix(final String player, final String prefix) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {
                NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, null, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    NametagManager.update(player, prefix, null);
                    PlayerLoader.update(player, prefix, null);
                }
            }
        });
    }

    public static void setSuffix(final String player, final String suffix) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {
                NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), null, suffix, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    NametagManager.update(player, null, suffix);
                    PlayerLoader.update(player, null, suffix);
                }
            }
        });
    }

    public static void setNametagHard(final String player, final String prefix, final String suffix) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {
                NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, NametagChangeType.HARD, NametagChangeReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    NametagManager.overlap(player, prefix, suffix);
                    PlayerLoader.overlap(player, prefix, suffix);
                }
            }
        });
    }

    public static void setNametagSoft(final String player, final String prefix, final String suffix) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {
                NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    NametagManager.update(player, prefix, suffix);
                    PlayerLoader.update(player, prefix, suffix);
                }
            }
        });
    }

    public static void updateNametagHard(final String player, final String prefix, final String suffix) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {
                NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, NametagChangeType.HARD, NametagChangeReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    NametagManager.overlap(player, prefix, suffix);
                }
            }
        });
    }

    public static void updateNametagSoft(final String player, final String prefix, final String suffix) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {
                NametagChangeEvent e = new NametagChangeEvent(player, getPrefix(player), getSuffix(player), prefix, suffix, NametagChangeType.SOFT, NametagChangeReason.CUSTOM);
                Bukkit.getServer().getPluginManager().callEvent(e);
                if (!e.isCancelled()) {
                    NametagManager.update(player, prefix, suffix);
                }
            }
        });
    }

    public static void resetNametag(final String player) {
        NametagEdit.plugin.getServer().getScheduler().scheduleSyncDelayedTask(NametagEdit.plugin, new Runnable() {
            public void run() {

                NametagManager.clear(player);
                PlayerLoader.removePlayer(player, null);

                Player targetPlayer = Bukkit.getPlayerExact(player);

                if (targetPlayer != null)
                    for (String key : NametagEdit.groups.keySet().toArray(new String[NametagEdit.groups.keySet().size()])) {
                        if (targetPlayer.hasPermission(key)) {
                            String prefix = NametagEdit.groups.get(key).get("prefix");
                            String suffix = NametagEdit.groups.get(key).get("suffix");
                            if (prefix != null)
                                prefix = NametagUtils.formatColors(prefix);
                            if (suffix != null)
                                suffix = NametagUtils.formatColors(suffix);
                            NametagCommand.setNametagHard(targetPlayer.getName(), prefix, suffix, NametagChangeReason.GROUP_NODE);

                            break;
                        }
                    }
            }
        });
    }

    public static String getPrefix(String player) {
        return NametagManager.getPrefix(player);
    }

    public static String getSuffix(String player) {
        return NametagManager.getSuffix(player);
    }

    public static String getNametag(String player) {
        return NametagManager.getFormattedName(player);
    }

    public static String getVersion() {
        return NametagEdit.plugin.getDescription().getVersion();
    }

    public static boolean hasCustomNametag(String player) {
        LinkedHashMap<String, String> map = PlayerLoader.getPlayer(player);
        if (map == null)
            return false;
        String prefix = map.get("prefix");
        String suffix = map.get("suffix");
        if ((prefix == null || prefix.isEmpty()) && (suffix == null || suffix.isEmpty()))
            return false;
        else return true;
    }
}
