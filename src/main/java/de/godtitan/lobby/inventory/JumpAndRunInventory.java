package de.godtitan.lobby.inventory;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Arrays;
import java.util.List;

public class JumpAndRunInventory implements Listener {

    private static final List<String> TELEPORT_LORE = Arrays.asList(" ", "§8➥ §6§oLinksklick zum Teleportieren", " ");
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, (short) 15).setDisplayName(" ").build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE).setDisplayName(" ").build();
    private static final ItemStack SEA = new ItemBuilder(Material.WATER_LILY).setDisplayName("§8» §eSee").setLore(TELEPORT_LORE).build();
    private static final ItemStack LIGHTHOUSE = new ItemBuilder(Material.REDSTONE_LAMP_OFF).setDisplayName("§8» §eLeuchtturm").setLore(TELEPORT_LORE).build();
    private static final ItemStack HILL = new ItemBuilder(Material.STONE).setDisplayName("§8» §eHügel").setLore(TELEPORT_LORE).build();
    private static final ItemStack BACK = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 14).setDisplayName("§8» §cZurück").build();

    private static final String TITLE = "§cJump and Runs";

    private Lobby lobby;

    public JumpAndRunInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);

        int[] whiteGlass = new int[]{1, 3, 5, 9, 16, 17, 19, 21, 23, 25};
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_GLASS);
        }
        for (int i = 0; i < whiteGlass.length; i++) {
            inventory.setItem(whiteGlass[i], WHITE_GLASS);
        }

        inventory.setItem(10, SEA);
        inventory.setItem(12, LIGHTHOUSE);
        inventory.setItem(14, HILL);
        inventory.setItem(26, BACK);

        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
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
            if (stack.equals(SEA)) {
                lobby.getLocationManager().teleport(player, "Sea");
            } else if (stack.equals(LIGHTHOUSE)) {
                lobby.getLocationManager().teleport(player, "Lighthouse");
            } else if (stack.equals(HILL)) {
                lobby.getLocationManager().teleport(player, "Hill");
            } else if (stack.equals(BACK)) {
                lobby.getNavigatorInventory().show(player);
            }
        }
    }

}
