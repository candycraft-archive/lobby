package de.pauhull.lobby.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LobbyItems {

    public static final ItemStack NAVIGATOR = new ItemBuilder(Material.COMPASS).setDisplayName("§dSpiele §7§o<Rechtsklick>").build();
    public static final ItemBuilder PROFILE = new ItemBuilder(Material.SKULL_ITEM, 1, 3).setDisplayName("§dProfil §7§o<Rechtsklick>");
    public static final ItemStack LOBBY_SWITCHER = new ItemBuilder(Material.NETHER_STAR).setDisplayName("§dLobbyswitcher §7§o<Rechtsklick>").build();
    public static final ItemStack SHOP = new ItemBuilder(Material.CHEST).setDisplayName("§dShop §7§o<Rechtsklick>").build();
    public static final ItemStack PLAYER_HIDER = new ItemBuilder(Material.BLAZE_ROD).setDisplayName("§dSpieler verstecken §7§o<Rechtsklick>").build();
    public static final ItemStack NICK = new ItemBuilder(Material.NAME_TAG).setDisplayName("§dAutonick §7§o<Rechtsklick>").build();

    public void giveItems(Player player) {

        player.getInventory().clear();

        player.getInventory().setItem(1, NAVIGATOR);
        player.getInventory().setItem(2, SHOP);
        player.getInventory().setItem(4, LOBBY_SWITCHER);
        player.getInventory().setItem(6, PLAYER_HIDER);
        player.getInventory().setItem(7, PROFILE.setOwner(player.getName()).build());
    }

    public void giveTeamItems(Player player) {

        player.getInventory().clear();

        player.getInventory().setItem(1, NAVIGATOR);
        player.getInventory().setItem(2, PLAYER_HIDER);
        player.getInventory().setItem(3, NICK);
        player.getInventory().setItem(5, PROFILE.setOwner(player.getName()).build());
        player.getInventory().setItem(6, SHOP);
        player.getInventory().setItem(7, LOBBY_SWITCHER);
    }

}
