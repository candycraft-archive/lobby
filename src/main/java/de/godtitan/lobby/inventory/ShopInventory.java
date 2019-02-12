package de.godtitan.lobby.inventory;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.util.ItemBuilder;
import de.godtitan.lobby.util.LobbyItems;
import org.bukkit.Bukkit;
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
import org.bukkit.inventory.meta.ItemMeta;

public class ShopInventory implements Listener {

    private static final String TITLE = "§cShop";
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(" ").build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack BOOTS = new ItemBuilder(Material.IRON_BOOTS).setDisplayName("§8» §eSchuhe").build();
    private static final ItemStack BALLOONS = new ItemBuilder(Material.LEASH).setDisplayName("§8» §eBallons").build();

    private Lobby lobby;

    public ShopInventory(Lobby lobby) {
        this.lobby = lobby;

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

        ItemStack skull = lobby.getHeadCache().getHead(player.getName());
        ItemMeta meta = skull.getItemMeta();
        meta.setDisplayName("§8» §eKöpfe");
        skull.setItemMeta(meta);
        inventory.setItem(10, skull);
        inventory.setItem(13, BOOTS);
        inventory.setItem(16, BALLOONS);

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (stack != null && stack.equals(LobbyItems.SHOP)) {
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
            if (stack.equals(BOOTS)) {
                lobby.getBootsInventory().show(player);
            } else if (stack.equals(BALLOONS)) {
                lobby.getBalloonsInventory().show(player);
            } else if (stack.getType() == Material.SKULL_ITEM) {
                lobby.getSkullInventory().show(player);
            }
        }
    }

}
