package tagcontrol.alexhh25.com;

import java.util.ArrayList;

/**
 * This class contains various tools for formatting strings, printing information, and saving information.
 * 
 * @author Alex Howard-Harris
 *
 */
public class NametagUtils {
        
        public static String formatColors(String str) {
                
                char[] chars = { '1', '2', '3', '4', '5', '6', '7', '8', '9', '0',
                                'a', 'b', 'c', 'd', 'e', 'f', 'n', 'r', 'l', 'k', 'o', 'm'};
                char[] array = str.toCharArray();
                for (int t = 0; t < array.length - 1; t++) {
                        if (array[t] == '&') {
                                for (char c : chars) {
                                        if (c == array[t + 1])
                                                array[t] = '§';
                                }
                        }
                }
                return new String(array);
        }
        
        static void box(String [] paragraph, String title) {
                
                ArrayList<String> buffer = new ArrayList<String>();
                String at = "";
                
                int side1 = (int) Math.round(25 - ((title.length() + 4) / 2d));
                int side2 = (int) (26 - ((title.length() + 4) / 2d));
                at += '+';
                for (int t = 0; t < side1; t++)
                        at += '-';
                at += "{ ";
                at += title;
                at += " }";
                for (int t = 0; t < side2; t++)
                        at += '-';
                at += '+';
                buffer.add(at);
                at = "";
                buffer.add("|                                                   |");
                for (String s : paragraph) {
                        at += "| ";
                        int left = 49;
                        for (int t = 0; t < s.length(); t++) {
                                at += s.charAt(t);
                                left--;
                                if (left == 0) {
                                        at += " |";
                                        buffer.add(at);
                                        at = "";
                                        at += "| ";
                                        left = 49;
                                }
                        }
                        while (left-- > 0)
                                at += ' ';
                        at += " |";
                        buffer.add(at);
                        at = "";
                }
                buffer.add("|                                                   |");
                buffer.add("+---------------------------------------------------+");
                
                System.out.println(" ");
                for (String line : buffer.toArray(new String[buffer.size()])) {
                        System.out.println(line);
                }
                System.out.println(" ");
        }
        public static String trim(String input) {
                if (input.length() > 16) {
                        String temp = input;
                        input = "";
                        for (int t = 0; t < 16; t++)
                                input += temp.charAt(t);
                }
                return input;
        }
        public static String getValue(String rawValue) {
                if (!(rawValue.startsWith("\"") && rawValue.endsWith("\""))) {
                        return rawValue;
                }
                rawValue = rawValue.trim();
                String f1 = "";
                for (int t = 1; t < rawValue.length() - 1; t++) {
                        f1 += rawValue.charAt(t);
                }
                return f1;
        }
        static boolean compareVersion(String old, String newer) {
                ArrayList<Integer> oldValues = new ArrayList<Integer>();
                ArrayList<Integer> newValues = new ArrayList<Integer>();
                String at = "";
                for (char c : old.toCharArray()) {
                        if (c != '.') {
                                at += c;
                        }
                        else {
                                try {
                                        oldValues.add(Integer.parseInt(at));
                                }
                                catch (Exception e) {
                                        return false;
                                }
                                at = "";
                        }
                }
                try {
                        oldValues.add(Integer.parseInt(at));
                }
                catch (Exception e) {
                        return false;
                }
                at = "";
                for (char c : newer.toCharArray()) {
                        if (c != '.') {
                                at += c;
                        }
                        else {
                                try {
                                        newValues.add(Integer.parseInt(at));
                                }
                                catch (Exception e) {
                                        return false;
                                }
                                at = "";
                        }
                }
                try {
                        newValues.add(Integer.parseInt(at));
                }
                catch (Exception e) {
                        return false;
                }
                int size = oldValues.size();
                boolean defaultToOld = true;
                if (newValues.size() < size) {
                        size = newValues.size();
                        defaultToOld = false;
                }
                for (int t = 0; t < size; t++) {
                        if (oldValues.get(t) < newValues.get(t)) {
                                return true;
                        }
                        else if (oldValues.get(t) > newValues.get(t)) {
                                return false;
                        }
                }
                if (oldValues.size() == newValues.size())
                        return false;
                if (defaultToOld)
                        return true;
                else return false;
        }
}
