package tagcontrol.alexhh25.com;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

/**
 * This class dynamically creates teams with numerical names and certain prefixes/suffixes (it ignores teams with other characters)
 * to assign unique prefixes and suffixes to specific players in the game. This class makes edits to the <b>scoreboard.dat</b> file,
 * adding and removing teams on the fly.
 * 
 * @author Alex Howard-Harris
 *
 */
public class NametagManager {

    private static final String TEAM_NAME_PREFIX = "TCE";

        private static List<Integer> list = new ArrayList<Integer>();

    private static HashMap<TeamInfo, List<String>> teams = new HashMap<TeamInfo, List<String>>();

    private static void addToTeam(TeamInfo team, String player) {
        removeFromTeam(player);
        List<String> list = teams.get(team);
        if (list != null) {
            list.add(player);
            Player p = Bukkit.getPlayerExact(player);
            if (p != null)
                sendPacketsAddToTeam(team, p);
        }
    }
    private static void register(TeamInfo team) {
        teams.put(team, new ArrayList<String>());
        sendPacketsAddTeam(team);
    }
    @SuppressWarnings("unused")
	private static boolean removeTeam(String name) {
        for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
            if (team.getName().equals(name)) {
                removeTeam(team);
                return true;
            }
        }
        return false;
    }
    private static void removeTeam(TeamInfo team) {

        List<String> list = teams.get(team);
        if (list != null) {
            for (String p : list.toArray(new String[list.size()])) {
                Player player = Bukkit.getPlayerExact(p);
                if (player != null)
                    sendPacketsRemoveFromTeam(team, player);
            }
            sendPacketsRemoveTeam(team);
            teams.remove(team);
        }

    }
    private static TeamInfo removeFromTeam(String player) {

        for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
            List<String> list = teams.get(team);
            for (String p : list.toArray(new String[list.size()])) {
                if (p.equals(player)) {
                    Player pl = Bukkit.getPlayerExact(player);
                    if (pl != null)
                        sendPacketsRemoveFromTeam(team, pl);
                    list.remove(p);
                    return team;
                }
            }
        }
        return null;
    }
    private static TeamInfo getTeam(String name) {

        for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
            if (team.getName().equals(name))
                return team;
        }
        return null;
    }
    private static TeamInfo[] getTeams() {
        TeamInfo[] list = new TeamInfo[teams.size()];
        int at = 0;
        for (TeamInfo team : teams.keySet().toArray(new TeamInfo[teams.size()])) {
            list[at] = team;
            at++;
        }
        return list;
    }
    private static String[] getTeamPlayers(TeamInfo team) {
        List<String> list = teams.get(team);
        if (list != null) {
            for (String p : list.toArray(new String[list.size()])) {
                Player pl = Bukkit.getPlayerExact(p);
                if (pl == null) {
                    list.remove(p);
                    sendPacketsRemoveFromTeam(team, pl);
                }
            }
            return list.toArray(new String[list.size()]);
        }
        else return new String[0];
    }
        static void load() {
                for (TeamInfo t : getTeams()) {
                        int entry = -1;
                        try {
                                entry = Integer.parseInt(t.getName());
                        }
                        catch (Exception e) {};
                        if (entry != -1) {
                                list.add(entry);
                        }
                }
        }


        public static void update(String player, String prefix, String suffix) {

                if (prefix == null || prefix.isEmpty())
                        prefix = getPrefix(player);
                if (suffix == null || suffix.isEmpty())
                        suffix = getSuffix(player);

                TeamInfo t = get(prefix, suffix);

        addToTeam(t, player);

        }
        public static void overlap(String player, String prefix, String suffix) {

                if (prefix == null)
                        prefix = "";
                if (suffix == null)
                        suffix = "";

        TeamInfo t = get(prefix, suffix);

        addToTeam(t, player);

        }
        public static void clear(String player) {

        removeFromTeam(player);

        }

        static String getPrefix(String player) {
                for (TeamInfo team : getTeams()) {
            for (String p : getTeamPlayers(team)) {
                if (p.equals(player))
                    return team.getPrefix();
            }
                }
                return "";
        }
        static String getSuffix(String player) {
        for (TeamInfo team : getTeams()) {
            for (String p : getTeamPlayers(team)) {
                if (p.equals(player))
                    return team.getSuffix();
            }
        }
        return "";
        }
        static String getFormattedName(String player) {
                return getPrefix(player) + player + getSuffix(player);
        }

        private static TeamInfo declareTeam(String name, String prefix, String suffix) {
                if (getTeam(name) != null) {
            TeamInfo team = getTeam(name);
            removeTeam(team);
                }

        TeamInfo team = new TeamInfo(name);

                team.setPrefix(prefix);
                team.setSuffix(suffix);

        register(team);

                return team;
        }
        private static TeamInfo get(String prefix, String suffix) {

                update();

                for (int t : list.toArray(new Integer[list.size()])) {

                        if (getTeam(TEAM_NAME_PREFIX + t) != null) {
                                TeamInfo team = getTeam(TEAM_NAME_PREFIX + t);
                                if (team.getSuffix().equals(suffix) && team.getPrefix().equals(prefix)) {
                                        return team;
                                }
                        }
                }
                return declareTeam(TEAM_NAME_PREFIX + nextName(), prefix, suffix);

        }
        private static int nextName() {
                int at = 0;
                boolean cont = true;
                while (cont) {
                        cont = false;
                        for (int t : list.toArray(new Integer[list.size()])) {
                                if (t == at) {
                                        at++;
                                        cont = true;
                                }

                        }
                }
                list.add(at);
                return at;
        }
        private static void update() {

                for (TeamInfo team : getTeams()) {
                        int entry = -1;
                        try {
                                entry = Integer.parseInt(team.getName());
                        }
                        catch (Exception e) {};
                        if (entry != -1) {
                                if (getTeamPlayers(team).length == 0) {
                                        removeTeam(team);
                                        list.remove(new Integer(entry));
                                }
                        }
                }
        }

    static void sendTeamsToPlayer(Player p) {
        try {

            for (TeamInfo team : getTeams()) {
                Packet209Mod mod = new Packet209Mod(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList<String>(), 0);
                mod.sendToPlayer(p);
                mod = new Packet209Mod(team.getName(), Arrays.asList(getTeamPlayers(team)), 3);
                mod.sendToPlayer(p);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            e.printStackTrace();
        }
    }

    private static void sendPacketsAddTeam(TeamInfo team) {

        try {

            for (Player p : Bukkit.getOnlinePlayers()) {
                Packet209Mod mod = new Packet209Mod(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList<String>(), 0);
                mod.sendToPlayer(p);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            e.printStackTrace();
        }
    }

    private static void sendPacketsRemoveTeam(TeamInfo team) {

        boolean cont = false;
        for (TeamInfo t : getTeams()) {
            if (t == team)
                cont = true;
        }
        if (!cont) return;

        try {

            for (Player p : Bukkit.getOnlinePlayers()) {
                Packet209Mod mod = new Packet209Mod(team.getName(), team.getPrefix(), team.getSuffix(), new ArrayList<String>(), 1);
                mod.sendToPlayer(p);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            e.printStackTrace();
        }
    }

    private static void sendPacketsAddToTeam(TeamInfo team, Player player) {

        boolean cont = false;
        for (TeamInfo t : getTeams()) {
            if (t == team)
                cont = true;
        }
        if (!cont) return;

        try {

            for (Player p : Bukkit.getOnlinePlayers()) {
                Packet209Mod mod = new Packet209Mod(team.getName(), Arrays.asList(player.getName()), 3);
                mod.sendToPlayer(p);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            e.printStackTrace();
        }
    }

    private static void sendPacketsRemoveFromTeam(TeamInfo team, Player player) {

        boolean cont = false;
        for (TeamInfo t : getTeams()) {
            if (t == team) {
                for (String p : getTeamPlayers(t)) {
                    if (p.equals(player))
                        cont = true;
                }
            }
        }
        if (!cont) return;

        try {

            for (Player p : Bukkit.getOnlinePlayers()) {
                Packet209Mod mod = new Packet209Mod(team.getName(), null, null, Arrays.asList(player), 4);
                mod.sendToPlayer(p);
            }
        }
        catch (Exception e) {
            System.out.println("Failed to send packet for player (Packet209SetScoreboardTeam) : ");
            e.printStackTrace();
        }
    }

    /**
     * Clears out all teams and removes them for all the players. Called when the plugin is disabled.
     */
    static void reset() {
        for (TeamInfo team : getTeams()) {
            removeTeam(team);
        }
    }
}
