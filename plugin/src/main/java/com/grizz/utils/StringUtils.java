package com.grizz.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

/**
 * Created by Gbtank.
 */
public class StringUtils {

    public static String capitalise(String string) {
        String result = "";
        if(string.contains("_")) {
            String[] split = string.split("_");
            for(String s : split) {
                String first = s.substring(0, 1);
                String end = s.substring(1);
                first.toUpperCase();
                end.toLowerCase();
                result = first + end + " ";
            }
        }
        return result.substring(0, result.length() - 1);
    }

    public static Location getLocationFromString(String string) {
        if(string.contains(":")) {
            String[] split = string.split(":");
            return new Location(Bukkit.getWorld(split[0]),
                    Integer.valueOf(split[1]),
                    Integer.valueOf(split[2]),
                    Integer.valueOf(split[3]));
        }
        return null;
    }

    public static Location getLocationWithWorld(World world, String string) {
        if(string.contains(":")) {
            String[] split = string.split(":");
            return new Location(world,
                    Integer.valueOf(split[0]),
                    Integer.valueOf(split[1]),
                    Integer.valueOf(split[2]));
        }
        return null;
    }

}
