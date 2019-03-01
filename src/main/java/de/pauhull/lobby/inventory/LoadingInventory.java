package de.pauhull.lobby.inventory;

import de.pauhull.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;

/**
 * Created by Paul
 * on 30.12.2018
 *
 * @author pauhull
 */
public class LoadingInventory implements Listener {

    private static final String TITLE = "§cLädt...";

    public LoadingInventory(Lobby lobby) {
        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }
    }

}
