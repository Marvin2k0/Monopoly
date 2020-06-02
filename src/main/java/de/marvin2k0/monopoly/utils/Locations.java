package de.marvin2k0.monopoly.utils;

import de.marvin2k0.monopoly.Monopoly;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

public class Locations
{
    public static Location get(String path)
    {
        World world = Bukkit.getWorld(Monopoly.plugin.getConfig().getString(path + ".world"));

        double y = Monopoly.plugin.getConfig().getDouble(path + ".y");
        double x = Monopoly.plugin.getConfig().getDouble(path + ".x");
        double z = Monopoly.plugin.getConfig().getDouble(path + ".z");
        double yaw = Monopoly.plugin.getConfig().getDouble(path + ".yaw");
        double pitch = Monopoly.plugin.getConfig().getDouble(path + ".pitch");

        return new Location(world, x, y, z, (float) yaw, (float) pitch);
    }

    public static void setLocation(String path, Location location)
    {
        Monopoly.plugin.getConfig().set(path + ".world", location.getWorld().getName());
        Monopoly.plugin.getConfig().set(path + ".x", location.getX());
        Monopoly.plugin.getConfig().set(path + ".y", location.getY());
        Monopoly.plugin.getConfig().set(path + ".z", location.getZ());
        Monopoly.plugin.getConfig().set(path + ".yaw", location.getYaw());
        Monopoly.plugin.getConfig().set(path + ".pitch", location.getPitch());

        Monopoly.plugin.saveConfig();
    }
}
