package me.lolok.strongholdfinder.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import javax.annotation.Nullable;

public class LocationsParser {
    public @Nullable static Location parse(World world, String s) {
        String[] args = s.split(":");
        double x, y, z;
        try {
            x = Double.parseDouble(args[0]);
            y = Double.parseDouble(args[1]);
            z = Double.parseDouble(args[2]);
        } catch (NumberFormatException e) {
            Bukkit.getLogger().warning(String.format("Invalid number for location World: %s, X: %s, Y: %s, Z: %s", world.getName(), args[0], args[1], args[2]));
            return null;
        }
        return new Location(world, x, y, z);
    }

    public @Nullable static Location parse(String world, String s) {
        World w = Bukkit.getWorld(world);
        if (w == null) {
            Bukkit.getLogger().warning(String.format("Unable to find world %s", world));
            return null;
        }
        return parse(w, s);
    }
}
