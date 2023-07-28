package dev.razorni.hcfactions.utils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationSerializer {

    public static Location destringifyLocation(String string) {
        String[] split = string.substring(1, string.length() - 2).split(",");
        World world = Bukkit.getWorld(split[0]);
        if (world == null) {
            return null;
        }
        double x = Double.parseDouble(split[1]);
        double y = Double.parseDouble(split[2]);
        double z = Double.parseDouble(split[3]);
        float yaw = Float.parseFloat(split[4]);
        float pitch = Float.parseFloat(split[5]);
        Location loc = new Location(world, x, y, z);
        loc.setYaw(yaw);
        loc.setPitch(pitch);
        return loc;
    }

    public static String stringifyLocation(Location location) {
        return "[" + location.getWorld().getName() + "," + location.getX() + "," + location.getY() + ","
                + location.getZ() + "," + location.getYaw() + "," + location.getPitch() + "]";
    }
}
