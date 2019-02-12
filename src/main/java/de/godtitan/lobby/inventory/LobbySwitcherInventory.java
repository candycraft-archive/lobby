package de.godtitan.lobby.inventory;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.util.ItemBuilder;
import de.godtitan.lobby.util.LobbyItems;
import de.pauhull.uuidfetcher.common.communication.message.ConnectMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class LobbySwitcherInventory implements Listener {

    private static final String TITLE = "§cLobbyswitcher";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(" ").build();

    private Lobby lobby;

    public LobbySwitcherInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {

        Collection<ServerObject> servers = TimoCloudAPI.getUniversalAPI().getServerGroup("Lobby").getServers();
        ServerObject currentServer = TimoCloudAPI.getBukkitAPI().getThisServer();

        Inventory inventory = Bukkit.createInventory(null, (int) (Math.ceil((double) servers.size() / 9.0) * 9) + 18, TITLE);
        for (int i = 0; i < inventory.getSize(); i++) {
            if (i < 9 || i > inventory.getSize() - 10) {
                inventory.setItem(i, BLACK_GLASS);
            }
        }

        int index = 9;
        for (ServerObject server : servers) {
            Material material;
            if (server.getName().equals(currentServer.getName())) {
                material = Material.MAGMA_CREAM;
            } else {
                material = Material.SLIME_BALL;
            }

            List<String> lore = new ArrayList<>();
            lore.add(" ");
            if (server.getMaxPlayerCount() == 0) {
                material = Material.SNOW_BALL;
                lore.add("§8➥ §b§lSTARTET");
            } else {
                lore.add("§8➥ §e" + server.getOnlinePlayerCount() + "§8/§e" + server.getMaxPlayerCount() + "§7 Spieler");
            }
            lore.add(" ");

            ItemStack stack = new ItemBuilder(material).setDisplayName("§8» §e" + server.getName()).setLore(lore).build();
            inventory.setItem(index++, stack);
        }

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.getType() == Material.SLIME_BALL) {
                String serverName = ChatColor.stripColor(stack.getItemMeta().getDisplayName()).substring(2);

                new ConnectMessage(player.getName(), serverName).sendToProxy("Proxy");

            } else if (stack.getType() == Material.MAGMA_CREAM) {
                player.sendMessage(Messages.PREFIX + "Du bist bereits auf diesem §cServer§7!");
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (stack != null && stack.equals(LobbyItems.LOBBY_SWITCHER)) {
                show(player);
            }
        }
    }

}
