package de.pauhull.lobby.inventory;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import de.pauhull.lobby.shop.Skull;
import de.pauhull.lobby.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;

public class SkullInventory implements Listener {

    private static final String TITLE = "§cKöpfe";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setDisplayName(" ").build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack REMOVE = new ItemBuilder(Material.BARRIER).setDisplayName("§8» §cKopf absetzen").build();
    private static final ItemStack BACK = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14).setDisplayName("§8» §cZurück").build();

    private Lobby lobby;

    public SkullInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 54, TITLE);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i > 17) {
                inventory.setItem(i, BLACK_GLASS);
            } else {
                inventory.setItem(i, WHITE_GLASS);
            }
        }

        inventory.setItem(1, Skull.GODTITAN.getItem());
        inventory.setItem(3, Skull.DRAGONFIGHTER.getItem());
        inventory.setItem(5, Skull.CODEEXCEPTION.getItem());
        inventory.setItem(7, Skull.PAUHULL.getItem());

        placeInInventory(player, inventory, 20, Skull.BASTIGHG);
        placeInInventory(player, inventory, 29, Skull.NORISKK);
        placeInInventory(player, inventory, 24, Skull.MINIMICHECKER);
        placeInInventory(player, inventory, 33, Skull.BYQUADRIX);

        inventory.setItem(44, WHITE_GLASS);
        inventory.setItem(52, WHITE_GLASS);
        inventory.setItem(31, REMOVE);
        inventory.setItem(49, BACK);

        inventory.setItem(53, new Skull(player.getName(), false, 0, "§8» §eDein Kopf").getItem());

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.getPlayer().getInventory().setHelmet(null);
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
                if (player.getInventory().getHelmet() == null) {
                    player.sendMessage(Messages.PREFIX + "Du hast keinen §cKopf §7aufgesetzt!");
                } else {
                    player.getInventory().setHelmet(null);
                    player.sendMessage(Messages.PREFIX + "Du hast deinen Kopf §aerfolgreich §7abgesetzt!");
                }
            } else {
                if (stack.getType() == Material.SKULL_ITEM) {
                    for (Skull skull : Skull.getSkulls()) {
                        if (stack.equals(skull.getItemBought())) {
                            if (skull.isPremium() && !player.hasPermission("lobby.skulls")) {
                                player.sendMessage(Messages.PREFIX + "Diese Köpfe sind nur für §bUnicorn-Spieler§7!");
                                player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                                return;
                            }

                            player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                            ItemStack headStack = skull.getItem().clone();
                            ItemMeta meta = headStack.getItemMeta();
                            meta.setLore(new ArrayList<>());
                            headStack.setItemMeta(meta);
                            player.getInventory().setHelmet(headStack);
                            player.closeInventory();
                            return;
                        } else if (stack.equals(skull.getItem())) {
                            lobby.getBuyItemInventory().show(player, skull);
                            return;
                        }
                    }

                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                    ItemStack headStack = new Skull(player.getName(), false, 0).getItem();
                    ItemMeta meta = headStack.getItemMeta();
                    meta.setLore(new ArrayList<>());
                    headStack.setItemMeta(meta);
                    player.getInventory().setHelmet(headStack);
                    player.closeInventory();
                }
            }
        }
    }

    private void placeInInventory(Player player, Inventory inventory, int slot, Skull skull) {
        skull.hasBought(player, hasHead -> {
            if (hasHead) {
                inventory.setItem(slot, skull.getItemBought());
            } else {
                inventory.setItem(slot, skull.getItem());
            }
        });
    }

}
