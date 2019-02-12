package de.godtitan.lobby.inventory;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.util.LobbyItems;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class NickInventory implements Listener {

    private Lobby lobby;

    public NickInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (stack != null && stack.equals(LobbyItems.NICK)) {
                Bukkit.dispatchCommand(player, "nick");
            }
        }
    }

}
