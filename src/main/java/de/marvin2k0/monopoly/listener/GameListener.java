package de.marvin2k0.monopoly.listener;

import de.marvin2k0.monopoly.Monopoly;
import de.marvin2k0.monopoly.utils.Locations;
import de.marvin2k0.monopoly.utils.Text;
import de.marvinleiers.minigameapi.events.*;
import de.marvinleiers.minigameapi.game.Game;
import de.marvinleiers.minigameapi.game.GamePlayer;
import de.marvinleiers.minigameapi.utils.ItemUtils;
import net.minecraft.server.v1_8_R1.ChatSerializer;
import net.minecraft.server.v1_8_R1.EnumTitleAction;
import net.minecraft.server.v1_8_R1.PacketPlayOutTitle;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

        Monopoly.field.put(event.getGamePlayer(), 0);
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
    public void onReset(GameResetEvent event)
    {
        for (GamePlayer gp : event.getGame().getGamePlayers())
            Monopoly.field.remove(gp);
    }

    @EventHandler
    public void onLeave(PlayerGameLeaveEvent event)
    {
        Monopoly.field.remove(event.getGamePlayer());

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
                    if (Monopoly.nums.containsKey(gp))
                    {
                        this.cancel();
                        return;
                    }

                    int num = numbers[random.nextInt(numbers.length)];

                    sendTitle(player, "§6" + num, 0, 5, 0);
                    player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);

                    if (i[0] >= 20)
                    {
                        diceAction.remove(player);

                        Monopoly.nums.remove(gp);
                        Monopoly.nums.put(gp, num);

                        if (check(event.getGame()))
                        {
                            for (GamePlayer gp : event.getGame().getGamePlayers())
                            {
                                tpToField(gp);
                            }

                            Monopoly.nums.clear();
                        }

                        this.cancel();
                        return;
                    }

                    i[0]++;
                }
            }.runTaskTimer(Monopoly.plugin, 0, delay[0]);
        }
    }

    private void tpToField(GamePlayer gp)
    {
        int current = Monopoly.field.get(gp);
        int rolled = Monopoly.nums.get(gp);

        int highestField = 0;

        for (Map.Entry<String, Object> entry : Monopoly.plugin.getConfig().getConfigurationSection("games." + gp.getGame().getName() + ".felder").getValues(false).entrySet())
        {
            if (Integer.parseInt(entry.getKey()) > highestField)
                highestField = Integer.parseInt(entry.getKey());
        }

        int feld = current + rolled;

        if (feld > highestField)
            feld -= (highestField + 1);

        Monopoly.field.remove(gp);
        Monopoly.field.put(gp, feld);

        Location loc = null;

        for (Map.Entry<String, Object> entry : Monopoly.plugin.getConfig().getConfigurationSection("games." + gp.getGame().getName() + ".felder." + feld).getValues(false).entrySet())
            loc = Locations.get("games." + gp.getGame().getName() + ".felder." + feld + "." + entry.getKey());

        gp.getPlayer().teleport(loc);
    }

    private boolean check(Game game)
    {
        HashMap<GamePlayer, Integer> clone = (HashMap<GamePlayer, Integer>) Monopoly.nums.clone();
        for (GamePlayer gp : game.getGamePlayers())
        {
            if (clone.remove(gp) == null)
                return false;
        }

        return true;
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

    public void sendTitle(Player p, String msg, int fadeIn, int stayTime, int fadeOut) {

        PacketPlayOutTitle title = new PacketPlayOutTitle(EnumTitleAction.TITLE, ChatSerializer.a(ChatColor.translateAlternateColorCodes('&', "{\"text\": \"" + msg + "\"}")), fadeIn, stayTime, fadeOut);

        ((CraftPlayer) p).getHandle().playerConnection.sendPacket(title);

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
