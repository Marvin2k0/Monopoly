package de.marvin2k0.monopoly.commands;

import de.marvin2k0.monopoly.Monopoly;
import de.marvin2k0.monopoly.utils.Locations;
import de.marvin2k0.monopoly.utils.Text;
import de.marvinleiers.minigameapi.MinigameAPI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Map;

public class FeldCommand implements CommandExecutor
{
    private static final MinigameAPI api = Monopoly.getAPI();
    private static final int MAX_FIELDS = 40;

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Text.get("noplayer"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 2)
        {
            player.sendMessage("§cUsage: /setfeld <game> <name>");
            return true;
        }

        String gameName = args[0];
        String fieldName = args[1];

        api.createGame(gameName);

        int id = getFeldCount(gameName);

        Locations.setLocation("games." + gameName + ".felder." + id + "." + fieldName, player.getLocation());

        player.sendMessage(Text.get("spawnset").replace("%game%", gameName));

        if (MAX_FIELDS - getFeldCount(gameName) == 0)
            player.sendMessage("§7Für dieses Spiel müssen keine Felder mehr gesetzt werden!");
        else
            player.sendMessage("§9" + (MAX_FIELDS - getFeldCount(gameName)) + " §7Felder müssen noch für dieses Spiel gesetzt werden!");

        return true;
    }

    private int getFeldCount(String name)
    {
        int count = 0;

        if (!Monopoly.plugin.getConfig().isSet("games." + name + ".felder"))
            return count;

        for (Map.Entry<String, Object> entry : Monopoly.plugin.getConfig().getConfigurationSection("games." + name + ".felder").getValues(false).entrySet())
            ++count;

        return count;
    }
}
