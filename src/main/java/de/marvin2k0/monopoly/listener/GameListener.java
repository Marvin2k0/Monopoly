package de.marvin2k0.monopoly.listener;

import de.marvin2k0.monopoly.Monopoly;
import de.marvin2k0.monopoly.utils.Locations;
import de.marvin2k0.monopoly.utils.Text;
import de.marvinleiers.minigameapi.events.*;
import de.marvinleiers.minigameapi.game.Game;
import de.marvinleiers.minigameapi.game.GamePlayer;
import de.marvinleiers.minigameapi.utils.ItemUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Random;

public class GameListener implements Listener
{
    private static Inventory inv = Bukkit.createInventory(null, 9, "§9Team wählen");
    private static ArrayList<Player> diceAction = new ArrayList<>();
    private static Random random = new Random();
    private boolean init = false;

    @EventHandler
    public void onGameJoin(PlayerGameJoinEvent event)
    {
        Game game = event.getGame();

        if (!Monopoly.plugin.getConfig().isSet("games." + game.getName() + ".spawn") || !Monopoly.plugin.getConfig().isSet("games." + game.getName() + ".lobby"))
        {
            event.getPlayer().sendMessage(Text.get("notset"));
            game.leave(event.getPlayer());
            return;
        }

        event.getPlayer().teleport(Locations.get("games." + game.getName() + ".lobby"));
        game.sendMessage(Text.get("join").replace("%player%", event.getPlayer().getName()));
    }

    @EventHandler
    public void onStart(GameStartEvent event)
    {
        Game game = event.getGame();

        if (!Monopoly.plugin.getConfig().isSet("games." + game.getName() + ".spawn"))
            return;

        Location spawn = Locations.get("games." + game.getName() + ".spawn");

        for (Player player : game.getPlayers())
            player.teleport(spawn);
    }

    @EventHandler
    public void onLeave(PlayerGameLeaveEvent event)
    {
        if (event.getGamePlayer() == null)
            System.out.println("ist null");
        else
            System.out.println("ist nicht null");


        if (event.getGamePlayer().getTeam() != null)
        {
            ItemStack item = getTeam(event.getGamePlayer().getTeam());

            if (!item.hasItemMeta())
                return;

            ItemMeta meta = item.getItemMeta();

            meta.setLore(new ArrayList<String>());
            item.setItemMeta(meta);
        }
    }

    @EventHandler
    public void onItemInLobby(PlayerInLobbyItemInteractEvent event)
    {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();

        if (!item.hasItemMeta())
            return;

        if (item.getType() == Material.NETHER_STAR && item.getItemMeta().getDisplayName().equalsIgnoreCase("§9Team wählen"))
        {
            player.openInventory(getTeamInventory());
        }
    }


    @EventHandler
    public void onItemInGame(PlayerInGameItemInteractEvent event)
    {
        ItemStack item = event.getItem();
        Player player = event.getPlayer();
        GamePlayer gp = event.getGamePlayer();

        if (!item.hasItemMeta())
            return;

        if (item.getType() == Material.SKULL_ITEM && item.getItemMeta().getDisplayName().equals("§f§lWürfel"))
        {
            if (diceAction.contains(player))
                return;

            diceAction.add(player);
            int[] numbers = {1, 2, 3, 4, 5, 6};
            final int[] i = {0};

            long[] delay = {random.nextInt(6) + 3};

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    int num = numbers[random.nextInt(numbers.length)];

                    player.sendTitle( "§6" + num, "");
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);

                    if (i[0] >= 20)
                    {
                        diceAction.remove(player);

                        if (Monopoly.nums.containsKey(gp))
                            Monopoly.nums.remove(gp);

                        Monopoly.nums.put(gp, num);

                        System.out.println(Monopoly.nums.get(gp));

                        this.cancel();
                        return;
                    }

                    i[0]++;
                }
            }.runTaskTimer(Monopoly.plugin, 0, delay[0]);
        }
    }

    @EventHandler
    public void onInLobbyInventoryClick(PlayerInLobbyInventoryClickEvent event)
    {
        Inventory inventory = event.getInventory();

        if (inventory.getName().equalsIgnoreCase("§9Team wählen"))
        {
            event.setCancelled(true);

            Player player = event.getPlayer();
            ItemStack item = event.getClickedItem();

            if (item.getType() != Material.WOOL)
                return;

            player.closeInventory();

            String team = null;

            if (item.getDurability() == 4)
                team = "Gelb";
            else if (item.getDurability()  == 11)
                team = "Blau";
            else if (item.getDurability()  == 13)
                team = "Grün";
            else if (item.getDurability()  == 14)
                team = "Rot";

            if (team != null)
            {
                GamePlayer gp = event.getGamePlayer();

                if (gp.getTeam() != null)
                {
                    player.sendMessage(Text.get("alreadyinteam"));
                    return;
                }

                if (getTeam(team).getItemMeta().getLore() != null)
                {
                    player.sendMessage(Text.get("teamfull"));
                    return;
                }

                ArrayList<String> lore = new ArrayList<>();
                lore.add("§f" + player.getName());

                ItemMeta meta = item.getItemMeta();
                meta.setLore(lore);
                item.setItemMeta(meta);

                player.sendMessage(Text.get("teamjoin").replace("%team%", team));
                event.getGamePlayer().setTeam(team);
            }
        }
    }

    private ItemStack getTeam(String name)
    {
        for (ItemStack item : getTeamInventory())
        {
            if (item == null)
                continue;

            if (item.hasItemMeta() && item.getItemMeta().getDisplayName().contains(name))
                return item;
        }

        return null;
    }

    private Inventory getTeamInventory()
    {
        if (init == true)
            return inv;

        init = true;

        inv.setItem(0, ItemUtils.create(Material.WOOL, (byte) 11, "§9§lBlaues Team"));
        inv.setItem(1, ItemUtils.create(Material.WOOL, (byte) 14, "§c§lRotes Team"));
        inv.setItem(2, ItemUtils.create(Material.WOOL, (byte) 4, "§e§lGelbes Team"));
        inv.setItem(3, ItemUtils.create(Material.WOOL, (byte) 13, "§3§lGrünes Team"));

        return inv;
    }
}
