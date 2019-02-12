package de.godtitan.lobby.inventory;

import de.godtitan.lobby.Lobby;
import de.pauhull.friends.spigot.SpigotFriends;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class ProfileInventory implements Listener {

    private Lobby lobby;

    public ProfileInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {
        SpigotFriends.getInstance().getMainMenu().show(player);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (stack != null && stack.getType() == Material.SKULL_ITEM && stack.getDurability() == 3) {
                show(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClicK(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        ItemStack stack = event.getCurrentItem();

        if ("§cProfil".equals(inventory.getTitle()) && stack != null) {
            if (stack.getType() == Material.EMERALD) {
                lobby.getLoadingInventory().show((Player) event.getWhoClicked());
            }
        }
    }

}
