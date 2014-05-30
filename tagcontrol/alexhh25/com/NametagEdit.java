package tagcontrol.alexhh25.com;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.LinkedHashMap;

/**
 * This is the main class for the NametagEdit server plugin.
 * 
 * @author Levi Webb
 *
 */
public class NametagEdit extends JavaPlugin {
        
        public static LinkedHashMap<String, LinkedHashMap<String, String>> groups = null;
        static LinkedHashMap<String, LinkedHashMap<String, String>> config = null;
        
        static boolean tabListEnabled = false;
        static boolean deathMessageEnabled = false;
    static boolean checkForUpdatesEnabled = false;
        
        public static NametagEdit plugin = null;
        
        /**
         * Called when the plugin is loaded, registering command executors and event handlers, intializes the {@link ca.wacos.nametagedit.NametagManager} class, and loads plugin information.
         * @see #load()
         */
        
        public void onEnable() {

        plugin = this;
        NametagManager.load();
                this.getServer().getPluginManager().registerEvents(new NametagEventHandler(), this);
                getCommand("tc").setExecutor(new NametagCommand());
                load();

        }
    public void onDisable() {
        NametagManager.reset();
    }
        /**
         * Loads groups, players, configurations, and refreshes information for in-game players.
         */
        public void load() {
                        NametagUtils.box(new String[]{"This is a development plugin build."}, null);

                groups = GroupLoader.load(this);

                config = ConfigLoader.load(this);

                NametagEdit.tabListEnabled = ConfigLoader.parseBoolean("tab-list-mask", "enabled", config, false);
                NametagEdit.deathMessageEnabled = ConfigLoader.parseBoolean("death-message-mask", "enabled", config, false);
        NametagEdit.checkForUpdatesEnabled = ConfigLoader.parseBoolean("check-for-updates", "enabled", config, true);

        this.getServer().getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
           public void run() {
               LinkedHashMap<String, LinkedHashMap<String, String>> players = PlayerLoader.load(plugin);
               Player[] onlinePlayers = Bukkit.getOnlinePlayers();

               for (Player p : onlinePlayers) {

                   NametagManager.clear(p.getName());

                   boolean setGroup = true;

                   for (String key : players.keySet().toArray(new String[players.keySet().size()])) {
                       if (p.getName().equals(key)) {

                           String prefix = players.get(key).get("prefix");
                           String suffix = players.get(key).get("suffix");
                           if (prefix != null)
                               prefix = NametagUtils.formatColors(prefix);
                           if (suffix != null)
                               suffix = NametagUtils.formatColors(suffix);
                           NametagManager.overlap(p.getName(), prefix, suffix);

                           setGroup = false;
                       }
                   }
                   if (setGroup) {
                       for (String key : groups.keySet().toArray(new String[groups.keySet().size()])) {
                           if (p.hasPermission(key)) {
                               String prefix = groups.get(key).get("prefix");
                               String suffix = groups.get(key).get("suffix");
                               if (prefix != null)
                                   prefix = NametagUtils.formatColors(prefix);
                               if (suffix != null)
                                   suffix = NametagUtils.formatColors(suffix);
                               NametagCommand.setNametagHard(p.getName(), prefix, suffix, NametagChangeEvent.NametagChangeReason.GROUP_NODE);

                               break;
                           }
                       }
                   }
                   if (NametagEdit.tabListEnabled) {
                       String str = "§f" + p.getName();
                       String tab = "";
                       for (int t = 0; t < str.length() && t < 16; t++)
                           tab += str.charAt(t);
                       p.setPlayerListName(tab);
                   }
                   else {
                       p.setPlayerListName(p.getName());
                   }
               }
           }
        });
        }
        File getPluginFile() {
                return getFile();
        }
        
}
