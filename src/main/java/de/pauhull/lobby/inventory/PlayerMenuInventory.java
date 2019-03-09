package de.pauhull.lobby.inventory;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.util.ItemBuilder;
import de.pauhull.uuidfetcher.common.communication.message.RunCommandMessage;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 09.03.2019
 *
 * @author pauhull
 */
public class PlayerMenuInventory implements Listener {

    private static final String TITLE = "§cSpieler: ";

    private Lobby lobby;

    public PlayerMenuInventory(Lobby lobby) {
        this.lobby = lobby;
        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player, Player seen) {
        Inventory inventory = Bukkit.createInventory(null, 9, TITLE + seen.getName());

        ItemStack addFriend = new ItemBuilder(Material.NETHER_STAR).setDisplayName("§8» §aFreund hinzufügen").setLore("§7Klicke hier, um " + seen.getName(), "§7als Freund zu adden").build();
        ItemStack inviteToParty = new ItemBuilder(Material.FIREWORK).setDisplayName("§8» §5In Party einladen").setLore("§7Klicke hier, um " + seen.getName(), "§7in eine Party einzuladen").build();

        inventory.setItem(3, addFriend);
        inventory.setItem(5, inviteToParty);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Inventory inventory = event.getInventory();
        Player player = (Player) event.getWhoClicked();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().startsWith(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.getType() == Material.NETHER_STAR) {
                String playerName = stack.getItemMeta().getLore().get(0).replace("§7Klicke hier, um ", "");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                new RunCommandMessage(player.getName(), "friend add " + playerName).sendToProxy("Proxy");
            } else if (stack.getType() == Material.FIREWORK) {
                String playerName = stack.getItemMeta().getLore().get(0).replace("§7Klicke hier, um ", "");
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                new RunCommandMessage(player.getName(), "party invite " + playerName).sendToProxy("Proxy");
            }
        }
    }

}
