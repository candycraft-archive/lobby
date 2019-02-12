package de.godtitan.lobby.util;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class LobbyItems {

    public static final ItemStack NAVIGATOR = new ItemBuilder(Material.COMPASS).setDisplayName("§8■ §cNavigator §8× §7§oRechtsklick").build();
    public static final ItemBuilder PROFILE = new ItemBuilder(Material.SKULL_ITEM, (short) 3).setDisplayName("§8■ §cProfil §8× §7§oRechtsklick");
    public static final ItemStack LOBBY_SWITCHER = new ItemBuilder(Material.NETHER_STAR).setDisplayName("§8■ §cLobby-Switcher §8× §7§oRechtsklick").build();
    public static final ItemStack SHOP = new ItemBuilder(Material.CHEST).setDisplayName("§8■ §cShop §8× §7§oRechtsklick").build();
    public static final ItemStack PLAYER_HIDER = new ItemBuilder(Material.BLAZE_ROD).setDisplayName("§8■ §cSpieler verstecken §8× §7§oRechtsklick").build();
    public static final ItemStack NICK = new ItemBuilder(Material.NAME_TAG).setDisplayName("§8■ §cAuto-Nick §8× §7§oRechtsklick").build();
    public static final ItemStack BACK = new ItemBuilder(Material.INK_SACK, (short) 1).setDisplayName("§8■ §eZurück zum Anfang §8× §7§oRechtsklick").build();
    public static final ItemStack SPAWN = new ItemBuilder(Material.SLIME_BALL).setDisplayName("§8■ §cJump and Run verlassen §8× §7§oRechtsklick").build();

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

    public void giveJumpAndRunItems(Player player) {

        player.getInventory().clear();

        player.getInventory().setItem(1, BACK);
        player.getInventory().setItem(7, SPAWN);
    }
}
