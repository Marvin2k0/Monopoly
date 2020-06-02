package de.marvin2k0.monopoly.utils;

import de.marvin2k0.monopoly.Monopoly;
import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

public class Text
{

    public static String get(String path)
    {
        return path.equalsIgnoreCase("prefix") ? get(path, false) : get(path, true);
    }

    public static String get(String path, boolean prefix)
    {
        return ChatColor.translateAlternateColorCodes('&', prefix ? Monopoly.plugin.getConfig().getString("prefix") + " " + Monopoly.plugin.getConfig().getString(path) : Monopoly.plugin.getConfig().getString(path));
    }

    public static void setUp(Plugin plugin)
    {
        Monopoly.plugin.getConfig().options().copyDefaults(true);
        Monopoly.plugin.getConfig().addDefault("prefix" , "&6[Monopoly]");
        Monopoly.plugin.getConfig().addDefault("noplayer", "&cDieser Befehl ist nur für Spieler!");
        Monopoly.plugin.getConfig().addDefault("spawnset", "&aErfolgreich &7für Spiel &b%game% &7gesetzt!");
        Monopoly.plugin.getConfig().addDefault("notset", "&cFür dieses Spiel wurden noch nicht alle Spawns gesetzt!");
        Monopoly.plugin.getConfig().addDefault("join", "&7[&a+&7] &9%player% &7hat das Spiel betreten.");
        Monopoly.plugin.getConfig().addDefault("teamjoin", "&7Du bist Team &9%team% &7beigetreten!");
        Monopoly.plugin.getConfig().addDefault("alreadyinteam", "&7Du bist bereits in einem Team!");
        Monopoly.plugin.getConfig().addDefault("teamfull", "&7Dieses Team ist voll!");
        Monopoly.plugin.getConfig().addDefault("maxplayers", 4);

        saveConfig();
    }

    private static void saveConfig()
    {
        Monopoly.plugin.saveConfig();
    }
}
