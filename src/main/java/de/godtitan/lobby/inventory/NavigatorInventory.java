package de.godtitan.lobby.inventory;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.util.ItemBuilder;
import de.godtitan.lobby.util.LobbyItems;
import de.godtitan.lobby.util.Skull;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;

public class NavigatorInventory implements Listener {

    private static final String TITLE = "§cNavigator";
    private static final Map<Inventory, Integer> ANIMATION_STEPS = new HashMap<>();
    private static final ItemStack BEDWARS = new ItemBuilder(Material.BED).setDisplayName("§6§lBed§c§lWars").build();
    //private static final ItemStack BLACK_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 13).setDisplayName(" ").build();
    private static final ItemStack PAINT_WARS = new ItemBuilder(Material.WOOL).setDisplayName("PaintWars").build();
    private static final ItemStack SPAWN = new ItemBuilder(Material.MAGMA_CREAM).setDisplayName("§f§lSpawn").build();
    //private static final ItemStack WHITE_GLASS = new ItemBuilder(Material.STAINED_GLASS_PANE, 1, 4).setDisplayName(" ").build();
    private final ItemStack CANDY_CANE;
    private final ItemStack GINGERBREAD;

    private Random random;
    private Lobby lobby;
    private short[] availableColors = new short[]{1, 2, 3, 4, 5, 6, 9, 10, 11, 14};
    private char[] chatColors = new char[]{'4', 'c', '6', 'e', '2', 'a', 'b', '3', /*'1' too dark, */'9', 'd', '5'};

    public NavigatorInventory(Lobby lobby) {
        this.lobby = lobby;
        this.random = new Random();

        Bukkit.getPluginManager().registerEvents(this, lobby);

        CANDY_CANE = new ItemBuilder(Skull.getFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGNjM2Y3ODFjOTIzYTI4ODdmMTRjMWVlYTExMDUwMTY2OTY2ZjI2MDI1Nzg0MDFmMTQ1MWU2MDk3Yjk3OWRmIn19fQ==")).setDisplayName("§f§lCandy§c§lCane §8§l(§d§l1.8§7 - §d§l1.12§8§l)").build();
        GINGERBREAD = new ItemBuilder(Skull.getFromBase64("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMTQ0MjJhODJjODk5YTljMTQ1NDM4NGQzMmNjNTRjNGFlN2ExYzRkNzI0MzBlNmU0NDZkNTNiOGIzODVlMzMwIn19fQ==")).setDisplayName("§6§lGingerbread §8§l(§d§l1.13§8§l)").build();
    }

    public void show(Player player) {

        Inventory inventory = Bukkit.createInventory(null, 27, TITLE);

        /*
        int[] whiteGlass = new int[]{0, 1, 3, 5, 7, 8, 11, 13, 15, 21, 23, 29, 31, 33, 36, 37, 39, 41, 43, 44};
        for (int i = 0; i < inventory.getSize(); i++) {
            inventory.setItem(i, BLACK_GLASS);
        }
        for (int i = 0; i < whiteGlass.length; i++) {
            inventory.setItem(whiteGlass[i], WHITE_GLASS);
        }
        */

        inventory.setItem(12, PAINT_WARS);
        inventory.setItem(14, BEDWARS);
        inventory.setItem(13, SPAWN);
        inventory.setItem(22, GINGERBREAD);
        inventory.setItem(4, CANDY_CANE);

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
        animate(inventory, -1);
    }

    public void animate(Inventory inventory, int tick) {

        Player player = (Player) inventory.getViewers().get(0);

        if (player == null)
            return;

        if ((tick + 1) % 20 == 0 || tick == -1) {
            ItemBuilder builder = new ItemBuilder(inventory.getItem(12).clone());
            builder.setLore("§7Spieler: §a" + countPlayers("PaintWars"));
            inventory.setItem(12, builder.build());

            builder = new ItemBuilder(CANDY_CANE);
            ServerObject server = TimoCloudAPI.getUniversalAPI().getServer("CandyCane");
            builder.setLore("§7Spieler: §a" + server.getOnlinePlayerCount() + "§7/§a" + server.getMaxPlayerCount());
            inventory.setItem(4, builder.build());

            builder = new ItemBuilder(GINGERBREAD);
            server = TimoCloudAPI.getUniversalAPI().getServer("Lobby-1");
            builder.setLore("§7Spieler: §a" + server.getOnlinePlayerCount() + "§7/§a" + server.getMaxPlayerCount());
            inventory.setItem(22, builder.build());

            builder = new ItemBuilder(inventory.getItem(14).clone());
            builder.setLore("§7Spieler: §a" + countPlayers("BedWars"));
            inventory.setItem(14, builder.build());
        }

        if (tick % 2 != 0 && tick != -1)
            return;

        StringBuilder colouredName = null;

        // name animation is buggy on newer versions
        if (getVersion(player) <= 47) {
            colouredName = new StringBuilder();
            String name = "PaintWars";
            for (int i = 0; i < name.length(); i++) {
                colouredName.append('§');
                colouredName.append(chatColors[((tick / 2) + i) % chatColors.length]);
                colouredName.append("§l");
                colouredName.append(name.charAt(i));
            }
        }

        ItemStack stack = inventory.getItem(12);
        if (stack != null) {
            if (tick % 10 == 0 || tick == -1) {
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

            inventory.setItem(12, stack);

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
                lobby.getServerInventory().show(player, "BedWars");
            } else if (stack.getType() == PAINT_WARS.getType()) {
                lobby.getServerInventory().show(player, "PaintWars");
            } else if (stack.getType() == CANDY_CANE.getType()) {
                ByteArrayDataOutput out = ByteStreams.newDataOutput();
                out.writeUTF("Connect");
                out.writeUTF("CandyCane");
                player.sendPluginMessage(lobby, "BungeeCord", out.toByteArray());
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
