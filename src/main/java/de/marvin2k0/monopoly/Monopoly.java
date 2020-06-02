package de.marvin2k0.monopoly;

import de.marvin2k0.monopoly.commands.FeldCommand;
import de.marvin2k0.monopoly.listener.GameListener;
import de.marvin2k0.monopoly.listener.SignListener;
import de.marvin2k0.monopoly.utils.Locations;
import de.marvin2k0.monopoly.utils.Text;
import de.marvinleiers.minigameapi.MinigameAPI;
import de.marvinleiers.minigameapi.MinigameMain;
import de.marvinleiers.minigameapi.game.Game;
import de.marvinleiers.minigameapi.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

//TODO: Liste von Spawns und Startteleportation

public class Monopoly extends JavaPlugin
{
    private static MinigameAPI api;
    public static Monopoly plugin;

    @Override
    public void onEnable()
    {
        api = MinigameAPI.getAPI(this);
        plugin = this;

        Text.setUp(this);

        Inventory lobbyItems = Bukkit.createInventory(null, 27, "");
        lobbyItems.setItem(4, ItemUtils.create(Material.NETHER_STAR, "§9Team wählen"));
        api.setLobbyItems(lobbyItems);

        getCommand("setspawn").setExecutor(this);
        getCommand("setlobby").setExecutor(this);
        getCommand("setfeld").setExecutor(new FeldCommand());

        getServer().getPluginManager().registerEvents(new GameListener(), this);
        getServer().getPluginManager().registerEvents(new SignListener(), this);
    }

    @Override
    public void onDisable()
    {
        MinigameMain.disable();
    }

    public static MinigameAPI getAPI()
    {
        return api;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
    {
        if (!(sender instanceof Player))
        {
            sender.sendMessage(Text.get("noplayer"));
            return true;
        }

        Player player = (Player) sender;

        if (args.length != 1)
        {
            player.sendMessage("§cUsage: /" + label + " <game>");
            return true;
        }

        String game = args[0];
        String spawnName = label.substring(3).toLowerCase();

        if (!MinigameAPI.exists(game))
            api.createGame(game);

        Locations.setLocation("games." + game + "." + spawnName, player.getLocation());

        player.sendMessage(Text.get("spawnset").replace("%game%", game));
        return true;
    }
}
