package de.marvin2k0.monopoly.listener;

import de.marvin2k0.monopoly.Monopoly;
import de.marvin2k0.monopoly.utils.Text;
import de.marvinleiers.minigameapi.MinigameAPI;
import de.marvinleiers.minigameapi.game.Game;
import org.bukkit.block.Sign;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class SignListener implements Listener
{
    @EventHandler
    public void onSign(SignChangeEvent event)
    {
        final Player player = event.getPlayer();

        if (!player.hasPermission("monopoly.sign"))
            return;

        if (event.getLine(0).equalsIgnoreCase("[Monopoly]") && !event.getLine(1).isEmpty() && MinigameAPI.exists(event.getLine(1)))
        {
            event.setLine(0, Text.get("prefix"));
            event.setLine(1, "§f" + event.getLine(1));
        }
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent event)
    {
        if (!event.hasBlock())
            return;

        if (event.getClickedBlock().getType().toString().contains("SIGN"))
        {
            final Player player = event.getPlayer();
            final Sign sign = (Sign) event.getClickedBlock().getState();

            if (sign.getLine(0).equals(Text.get("prefix")) && MinigameAPI.exists(sign.getLine(1).substring(2)))
            {
                Game game = MinigameAPI.getGameFromName(sign.getLine(1).substring(2));
                game.join(player);

                event.setCancelled(true);
            }
        }
    }
}
