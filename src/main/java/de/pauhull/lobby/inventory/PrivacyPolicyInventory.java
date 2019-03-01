package de.pauhull.lobby.inventory;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import de.pauhull.lobby.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PrivacyPolicyInventory implements Listener {

    private static final String TITLE = "§cDatenschutz";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 15).setDisplayName(" ").build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack ACCEPT = new ItemBuilder(Material.INK_SACK, 1, 10).setDisplayName("§8» §aAkzeptieren").build();
    private static final ItemStack DENY = new ItemBuilder(Material.INK_SACK, 1, 1).setDisplayName("§8» §cAblehnen").build();
    private static final ItemStack INFO = new ItemBuilder(Material.PAPER).setDisplayName("§8» §eDatenschutzerklärung").setLore(Arrays.asList(" ", "§7Du musst unsere §eDatenschutzerklärung §7bestätigen, um spielen zu können!", " ", "§7Wir speichern:", "§8» §eDeinen Benutzernamen", "§8» §eDeine IP", "§8» §eDeine UUID", " ", "§7Deine Daten werden nicht an Dritte weitergegeben.", " ")).build();

    private Lobby lobby;
    private List<Player> notAccepted;

    public PrivacyPolicyInventory(Lobby lobby) {
        this.lobby = lobby;
        this.notAccepted = new ArrayList<>();

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 9, TITLE);

        inventory.setItem(0, WHITE_GLASS);
        inventory.setItem(1, BLACK_GLASS);
        inventory.setItem(2, ACCEPT);
        inventory.setItem(3, BLACK_GLASS);
        inventory.setItem(4, INFO);
        inventory.setItem(5, BLACK_GLASS);
        inventory.setItem(6, DENY);
        inventory.setItem(7, BLACK_GLASS);
        inventory.setItem(8, WHITE_GLASS);

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
            if (stack.equals(ACCEPT)) {
                lobby.getPrivacyPolicyTable().setAccepted(player.getUniqueId(), true);
                notAccepted.remove(player);

                player.playSound(player.getLocation(), Sound.SUCCESSFUL_HIT, 1, 1);
                player.sendMessage(Messages.PREFIX + "§7Du hast unsere §eDatenschutzerklärung §7soeben §aakzeptiert§7!");
                player.closeInventory();
            } else if (stack.equals(DENY)) {
                lobby.getPrivacyPolicyTable().setAccepted(player.getUniqueId(), false);

                player.closeInventory();
                player.kickPlayer("\n"
                        + "§8§m          |§d CandyCraft §8§m|          \n"
                        + "\n"
                        + "§7Du wurdest vom Server §cgekickt§7!\n"
                        + "\n"
                        + "§eGrund §8» §7Du §cmusst §7unsere §eDatenschutzerklärung §7bestätigen, um spielen zu können!\n"
                        + "\n"
                        + "§8§m          |§d CandyCraft §8§m|          ");
            }
        }
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (inventory != null && inventory.getTitle() != null && inventory.getTitle().equals(TITLE)) {
            lobby.getPrivacyPolicyTable().hasAccepted(player.getUniqueId(), accepted -> {
                if (!accepted) {
                    player.openInventory(inventory);
                }
            });
        }
    }

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event) {
        Player player = (Player) event.getPlayer();
        Inventory inventory = event.getInventory();

        if (notAccepted.contains(player) && !inventory.getTitle().equals(TITLE)) {
            event.setCancelled(true);
            show(player);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        Bukkit.getScheduler().scheduleSyncDelayedTask(lobby, () -> {
            lobby.getPrivacyPolicyTable().hasAccepted(player.getUniqueId(), accepted -> {
                if (!accepted) {
                    show(player);
                    notAccepted.add(player);
                } else {
                    notAccepted.remove(player);
                }
            });
        }, 10);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        notAccepted.remove(event.getPlayer());
    }

}
