package de.pauhull.lobby.inventory;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import de.pauhull.lobby.shop.Balloon;
import de.pauhull.lobby.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public class BalloonsInventory implements Listener {

    private static final String TITLE = "§cBallons";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setDisplayName(" ").build();
    private static final ItemStack REMOVE = new ItemBuilder(Material.BARRIER).setDisplayName("§8» §cBallons entfernen").build();
    private static final ItemStack BACK = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14).setDisplayName("§8» §cZurück").build();

    private Lobby lobby;

    public BalloonsInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i > inventory.getSize() - 10) {
                inventory.setItem(i, BLACK_GLASS);
            }
        }

        inventory.setItem(inventory.getSize() - 5, BACK);
        inventory.setItem(4, REMOVE);

        final AtomicInteger slot = new AtomicInteger(9);
        for (int i = 0; i < Balloon.values().length; i++) {
            Balloon balloon = Balloon.values()[i];

            balloon.hasBought(player, hasBalloon -> {

                if (balloon.isSpecial() && !hasBalloon) {
                    return;
                }

                if (hasBalloon) {
                    inventory.setItem(slot.getAndIncrement(), balloon.getItemBought());
                } else {
                    inventory.setItem(slot.getAndIncrement(), balloon.getItem());
                }
                player.updateInventory();
            });
        }

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.equals(BACK)) {
                lobby.getShopInventory().show(player);
            } else if (stack.equals(REMOVE)) {
                if (lobby.getBalloonManager().hasBalloonActive(player)) {
                    lobby.getBalloonManager().removeAllBalloons(player);
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.sendMessage(Messages.PREFIX + "Du hast deine Ballons §aerfolgreich§7 entfernt.");
                } else {
                    player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
                    player.sendMessage(Messages.PREFIX + "Du hast §ckeine §7Ballons aktiv.");
                }
            } else if (!stack.equals(BLACK_GLASS)) {
                for (Balloon balloon : Balloon.values()) {

                    if (stack.equals(balloon.getItemBought())) {

                        if (!balloon.isSpecial()) {
                            if (balloon.isPremium() && !player.hasPermission("lobby.balloons")) {
                                player.sendMessage(Messages.PREFIX + "Dieser Ballon ist nur für §eLebkuchen-Spieler§7!");
                                return;
                            }
                        }

                        if (lobby.getBalloonManager().addBalloon(player, balloon)) {
                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                            player.sendMessage(Messages.PREFIX + "Ballon §aerfolgreich §7ausgewählt!");
                        } else {
                            player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                            player.sendMessage(Messages.PREFIX + "Du hast §czu viele§7 Ballons aktiv!");
                        }
                        return;
                    } else if (stack.equals(balloon.getItem())) {
                        lobby.getBuyItemInventory().show(player, balloon);
                    }
                }
            }
        }
    }

}
