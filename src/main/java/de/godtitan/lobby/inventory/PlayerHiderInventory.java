package de.godtitan.lobby.inventory;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.util.ItemBuilder;
import de.godtitan.lobby.util.LobbyItems;
import de.pauhull.friends.spigot.SpigotFriends;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PlayerHiderInventory implements Listener {

    private static final String TITLE = "§cSpieler verstecken";
    private static final List<String> SELECTED_LORE = Arrays.asList(" ", "§a§oAktiv", " ");
    private static final ItemStack PLAYERS = new ItemBuilder(Material.STAINED_CLAY, (short) 5).setDisplayName("§8» §a§lAlle Spieler anzeigen").build();
    private static final ItemStack PLAYERS_SELECTED = new ItemBuilder(Material.STAINED_CLAY, (short) 5).setDisplayName("§8» §a§lAlle Spieler anzeigen").addEnchant(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).setLore(SELECTED_LORE).build();
    private static final ItemStack TEAM = new ItemBuilder(Material.STAINED_CLAY, (short) 10).setDisplayName("§8» §5§lNur Freunde, VIPs und Teammitglieder anzeigen").build();
    private static final ItemStack TEAM_SELECTED = new ItemBuilder(Material.STAINED_CLAY, (short) 10).setDisplayName("§8» §5§lNur Freunde, VIPs und Teammitglieder anzeigen").addEnchant(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).setLore(SELECTED_LORE).build();
    private static final ItemStack NONE = new ItemBuilder(Material.STAINED_CLAY, (short) 14).setDisplayName("§8» §c§lAlle Spieler verstecken").build();
    private static final ItemStack NONE_SELECTED = new ItemBuilder(Material.STAINED_CLAY, (short) 14).setDisplayName("§8» §5§lAlle Spieler verstecken").addEnchant(Enchantment.DURABILITY, 1).addItemFlag(ItemFlag.HIDE_ENCHANTS).setLore(SELECTED_LORE).build();

    private Lobby lobby;
    private Map<String, HideMode> hideModes;

    public PlayerHiderInventory(Lobby lobby) {
        this.lobby = lobby;
        this.hideModes = new HashMap<>();

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {
        Inventory inventory = Bukkit.createInventory(null, 9, TITLE);

        if (hideModes.get(player.getName()) == HideMode.ALL || !hideModes.containsKey(player.getName())) {
            inventory.setItem(1, PLAYERS_SELECTED);
        } else {
            inventory.setItem(1, PLAYERS);
        }

        if (hideModes.get(player.getName()) == HideMode.TEAM) {
            inventory.setItem(4, TEAM_SELECTED);
        } else {
            inventory.setItem(4, TEAM);
        }

        if (hideModes.get(player.getName()) == HideMode.NONE) {
            inventory.setItem(7, NONE_SELECTED);
        } else {
            inventory.setItem(7, NONE);
        }

        player.openInventory(inventory);
        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (stack != null && stack.equals(LobbyItems.PLAYER_HIDER)) {
                show(player);
            }
        }
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
            if (stack.equals(PLAYERS)) {
                player.sendMessage(Messages.PREFIX + "Du siehst nun §aalle Spieler§7!");
                selectMode(player, HideMode.ALL);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1, 1);
            } else if (stack.equals(TEAM)) {
                player.sendMessage(Messages.PREFIX + "Du siehst nun §5nur Freunde, VIPs und Teammitglieder§7!");
                selectMode(player, HideMode.TEAM);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1, 1);
            } else if (stack.equals(NONE)) {
                player.sendMessage(Messages.PREFIX + "Du siehst nun §4keine Spieler§7!");
                selectMode(player, HideMode.NONE);
                player.closeInventory();
                player.playSound(player.getLocation(), Sound.WOOD_CLICK, 1, 1);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        lobby.getPlayerHiderTable().getMode(player.getUniqueId(), mode -> {
            selectMode(player, mode);
        });

        for (Player all : Bukkit.getOnlinePlayers()) {
            if (player.getUniqueId().equals(all.getUniqueId()) || !hideModes.containsKey(all.getName())) {
                continue;
            }

            HideMode hideMode = hideModes.get(all.getName());
            if (hideMode == HideMode.ALL) {
                all.showPlayer(player);
            } else if (hideMode == HideMode.TEAM) {
                if (player.hasPermission("lobby.team") || player.hasPermission("lobby.premium")) {
                    all.showPlayer(player);
                } else {
                    SpigotFriends.getInstance().getFriendTable().areFriends(player.getUniqueId(), all.getUniqueId(), areFriends -> {
                        Bukkit.getScheduler().runTask(lobby, () -> {
                            if (areFriends) {
                                all.showPlayer(player);
                            } else {
                                all.hidePlayer(player);
                            }
                        });
                    });
                }
            } else if (hideMode == HideMode.NONE) {
                all.hidePlayer(player);
            }
        }
    }

    private void selectMode(Player player, HideMode hideMode) {
        lobby.getPlayerHiderTable().setMode(player.getUniqueId(), hideMode);
        hideModes.put(player.getName(), hideMode);

        if (hideMode == HideMode.ALL) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId().equals(all.getUniqueId()))
                    continue;

                player.showPlayer(all);
            }
        } else if (hideMode == HideMode.TEAM) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId().equals(all.getUniqueId()))
                    continue;

                if (all.hasPermission("lobby.team")) {
                    player.showPlayer(all);
                } else {
                    SpigotFriends.getInstance().getFriendTable().areFriends(player.getUniqueId(), all.getUniqueId(), areFriends -> {
                        Bukkit.getScheduler().runTask(lobby, () -> {
                            if (areFriends) {
                                player.showPlayer(all);
                            } else {
                                player.hidePlayer(all);
                            }
                        });
                    });
                }
            }
        } else if (hideMode == HideMode.NONE) {
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (player.getUniqueId().equals(all.getUniqueId()))
                    continue;

                player.hidePlayer(all);
            }
        }
    }

    public enum HideMode {
        ALL, TEAM, NONE
    }

}
