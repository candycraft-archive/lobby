package de.pauhull.lobby.inventory;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import de.pauhull.lobby.shop.Boots;
import de.pauhull.lobby.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class BootsInventory implements Listener {

    private static final String TITLE = "§cSchuhe";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setDisplayName(" ").build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack REMOVE = new ItemBuilder(Material.BARRIER).setDisplayName("§8» §cSchuhe entfernen").build();
    private static final ItemStack BACK = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14).setDisplayName("§8» §cZurück").build();

    private Map<String, Boots> boots;
    private Lobby lobby;

    public BootsInventory(Lobby lobby) {
        this.lobby = lobby;
        this.boots = new HashMap<>();
        this.playEffects();

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);

        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i > 17) {
                inventory.setItem(i, BLACK_GLASS);
            } else {
                inventory.setItem(i, WHITE_GLASS);
            }
        }

        inventory.setItem(4, REMOVE);
        placeInInventory(player, inventory, 9, Boots.HEART);
        placeInInventory(player, inventory, 11, Boots.CLOUD);
        placeInInventory(player, inventory, 13, Boots.ENDER);
        placeInInventory(player, inventory, 15, Boots.WATER);
        placeInInventory(player, inventory, 17, Boots.FIRE);
        inventory.setItem(22, BACK);

        player.playSound(player.getLocation(), Sound.CLICK, 1F, 1F);
        player.openInventory(inventory);
    }

    private void placeInInventory(Player player, Inventory inventory, int slot, Boots boots) {
        boots.hasBought(player, hasBoots -> {
            if (hasBoots) {
                inventory.setItem(slot, boots.getItemBought());
            } else {
                inventory.setItem(slot, boots.getItem());
            }
        });
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
                if (!boots.containsKey(player.getName())) {
                    player.sendMessage(Messages.PREFIX + "Du hast keine §cBoots §7aktiv!");
                    player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                } else {
                    boots.remove(player.getName());
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player.sendMessage(Messages.PREFIX + "Du hast deine Boots §aerfolgreich §7abgelegt!");
                    player.getInventory().setBoots(null);
                    player.closeInventory();
                }
            } else {
                for (Boots boots : Boots.values()) {
                    if (stack.equals(boots.getItem())) {
                        lobby.getBuyItemInventory().show(player, boots);
                        break;
                    } else if (stack.equals(boots.getItemBought())) {
                        player.closeInventory();
                        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                        ItemStack bootsStack = new ItemBuilder(boots.getItem()).stripLore().build();
                        player.getInventory().setBoots(bootsStack);
                        this.boots.put(player.getName(), boots);
                        break;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        player.getInventory().setBoots(null);
        Boots boots = this.boots.get(player.getName());
        lobby.getSelectedGadgetsTable().saveSelectedGadget(player.getUniqueId(), "BOOTS", boots == null ? null : boots.name());
        this.boots.remove(player.getName());
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        lobby.getSelectedGadgetsTable().getSelectedGadget(player.getUniqueId(), "BOOTS", bootsName -> {
            if (bootsName == null) {
                return;
            }

            Boots boots = Boots.valueOf(bootsName);

            Bukkit.getScheduler().runTask(lobby, () -> {
                ItemStack bootsStack = new ItemBuilder(boots.getItem()).stripLore().build();
                player.getInventory().setBoots(bootsStack);
                this.boots.put(player.getName(), boots);
            });
        });
    }

    private void playEffects() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(lobby, () -> {
            Iterator<String> iterator = boots.keySet().iterator();

            while (iterator.hasNext()) {
                Player player = Bukkit.getPlayer(iterator.next());

                if (player == null) {
                    iterator.remove();
                } else {
                    boots.get(player.getName()).playEffect(player);
                }
            }
        }, 0, 1);
    }

}
