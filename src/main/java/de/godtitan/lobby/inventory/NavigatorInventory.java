package de.godtitan.lobby.inventory;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
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

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class NavigatorInventory implements Listener {

    private static final String TITLE = "§cNavigator";
    private static final Map<Inventory, Integer> ANIMATION_STEPS = new HashMap<>();
    private static final List<String> TELEPORT_LORE = Arrays.asList(" ", "§8× §cLädt...", "§8➥ §6§oLinksklick zum Teleportieren", " ");
    private static final ItemStack BEDWARS = new ItemBuilder(Material.BED).setDisplayName("§8» §eBedWars").setLore(TELEPORT_LORE).build();
    private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 5).setDisplayName(" ").build();
    private static final ItemStack PAINT_WARS = new ItemBuilder(Material.WOOL).setDisplayName("§8» §4P§ca§6i§en§2t§aW§ba§3r§9s").setLore(TELEPORT_LORE).build();
    private static final ItemStack SPAWN = new ItemBuilder(Material.MAGMA_CREAM).setDisplayName("§8» §eSpawn").setLore(Arrays.asList(" ", "§8➥ §6§oLinksklick zum Teleportieren", " ")).build();
    private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 4).setDisplayName(" ").build();

    private Random random;
    private Lobby lobby;
    private short[] availableColors = new short[]{1, 2, 3, 4, 5, 6, 9, 10, 11, 14};
    private char[] chatColors = new char[]{'4', 'c', '6', 'e', '2', 'a', 'b', '3', /*'1' too dark, */'9', 'd', '5'};

    public NavigatorInventory(Lobby lobby) {
        this.lobby = lobby;
        this.random = new Random();

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 45, TITLE);

        int[] whiteGlass = new int[]{0, 1, 3, 5, 7, 8, 11, 13, 15, 21, 23, 29, 31, 33, 36, 37, 39, 41, 43, 44};
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_GLASS);
        }
        for (int i = 0; i < whiteGlass.length; i++) {
            inventory.setItem(whiteGlass[i], WHITE_GLASS);
        }

        inventory.setItem(4, PAINT_WARS);
        inventory.setItem(16, BEDWARS);
        inventory.setItem(22, SPAWN);

        ANIMATION_STEPS.put(inventory, 0);
        final AtomicInteger task = new AtomicInteger();
        task.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(lobby, () -> {
            if (inventory.getViewers().isEmpty() || !ANIMATION_STEPS.containsKey(inventory)) {
                ANIMATION_STEPS.remove(inventory);
                Bukkit.getScheduler().cancelTask(task.get());
                return;
            }

            final AtomicInteger currentTick = new AtomicInteger();
            ANIMATION_STEPS.compute(inventory, (ignored, lastTick) -> {
                currentTick.set(Objects.requireNonNull(lastTick) + 1);
                return currentTick.get();
            });

            animate(inventory, currentTick.get());

        }, 0, 1));

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    public void animate(Inventory inventory, int tick) {

        Player player = (Player) inventory.getViewers().get(0);

        if (player == null)
            return;

        if ((tick + 1) % 20 == 0) {
            ItemBuilder builder = new ItemBuilder(inventory.getItem(4).clone());
            builder.setLore(" ", "§8× §e" + countPlayers("PaintWars") + "§7 Spieler online", "§8➥ §6§oLinksklick zum Teleportieren", " ");
            inventory.setItem(4, builder.build());

            builder = new ItemBuilder(inventory.getItem(16).clone());
            builder.setLore(" ", "§8× §e" + countPlayers("BedWars") + "§7 Spieler online", "§8➥ §6§oLinksklick zum Teleportieren", " ");
            inventory.setItem(16, builder.build());
        }

        if (tick % 2 != 0)
            return;

        StringBuilder colouredName = null;

        // name animation is buggy on newer versions
        if (getVersion(player) <= 47) {
            colouredName = new StringBuilder("§8» ");
            String name = "PaintWars";
            for (int i = 0; i < name.length(); i++) {
                colouredName.append('§');
                colouredName.append(chatColors[((tick / 2) + i) % chatColors.length]);
                colouredName.append(name.charAt(i));
            }
        }

        ItemStack stack = inventory.getItem(4);
        if (stack != null) {
            if (tick % 10 == 0) {
                short color;
                do { // not the same color again
                    color = availableColors[random.nextInt(availableColors.length)];
                } while (color == stack.getDurability());
                stack.setDurability(color);
            }
            ItemMeta meta = stack.getItemMeta();
            if (meta != null && colouredName != null) {
                meta.setDisplayName(colouredName.toString());
                stack.setItemMeta(meta);
            }

            inventory.setItem(4, stack);

            if (getVersion(player) <= 47) {
                player.updateInventory();
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack stack = event.getItem();

        if (event.getAction() == Action.RIGHT_CLICK_BLOCK || event.getAction() == Action.RIGHT_CLICK_AIR) {
            if (stack != null && stack.equals(LobbyItems.NAVIGATOR)) {
                show(player);
            }
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        ItemStack stack = event.getCurrentItem();
        Inventory inventory = event.getClickedInventory();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().equals(TITLE)) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.equals(SPAWN)) {
                lobby.getLocationManager().teleport(player, "Spawn");
            } else if (stack.getType() == BEDWARS.getType()) {
                lobby.getLocationManager().teleport(player, "Bedwars");
            } else if (stack.getType() == PAINT_WARS.getType()) {
                lobby.getLocationManager().teleport(player, "PaintWars");
            }
        }
    }

    private int countPlayers(String... groups) {
        int amount = 0;
        for (String group : groups) {
            amount += countPlayers(TimoCloudAPI.getUniversalAPI().getServerGroup(group));
        }
        return amount;
    }

    private int countPlayers(ServerGroupObject... groups) {
        int amount = 0;
        for (ServerGroupObject group : groups) {
            for (ServerObject server : group.getServers()) {
                amount += server.getOnlinePlayerCount();
            }
        }
        return amount;
    }

    private int getVersion(Player player) {
        try {
            Class api = Class.forName("us.myles.ViaVersion.api.ViaVersion");
            Object instance = api.getMethod("getInstance").invoke(null);
            return (int) instance.getClass().getMethod("getPlayerVersion", Player.class).invoke(instance, player);
        } catch (IllegalAccessException | NoSuchMethodException | InvocationTargetException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
