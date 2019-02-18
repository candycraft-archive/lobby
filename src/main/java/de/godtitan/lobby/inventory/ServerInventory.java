package de.godtitan.lobby.inventory;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerGroupObject;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.util.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by Paul
 * on 13.02.2019
 *
 * @author pauhull
 */
public class ServerInventory implements Listener {

    private Lobby lobby;

    public ServerInventory(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    public void show(Player player, String groupName) {

        ServerGroupObject group = TimoCloudAPI.getUniversalAPI().getServerGroup(groupName);
        if (group == null)
            return;

        int onlineServers = group.getOnlineAmount();
        Inventory inventory = Bukkit.createInventory(null, (int) Math.ceil(onlineServers / 9.0) * 9, "§cAlle " + group.getName() + " Server");
        refresh(inventory, group);

        AtomicInteger task = new AtomicInteger();
        task.set(Bukkit.getScheduler().scheduleSyncRepeatingTask(lobby, () -> {
            if (inventory.getViewers().isEmpty()) {
                Bukkit.getScheduler().cancelTask(task.get());
                return;
            }

            ServerGroupObject newGroup = TimoCloudAPI.getUniversalAPI().getServerGroup(groupName);
            int size = (int) Math.ceil(newGroup.getOnlineAmount() / 9.0) * 9;
            if (size != inventory.getSize()) {
                show(player, groupName);
                Bukkit.getScheduler().cancelTask(task.get());
                return;
            }

            refresh(inventory, newGroup);

        }, 20, 20));

        player.playSound(player.getLocation(), Sound.ITEM_PICKUP, 1, 1);
        player.openInventory(inventory);
    }

    public void refresh(Inventory inventory, ServerGroupObject group) {
        inventory.clear();
        int slot = 0;
        for (ServerObject server : group.getServers()) {
            Material material = Material.EMERALD_BLOCK;
            String lore = "§aLobby";
            if (server.getState().equals("INGAME")) {
                material = Material.LAPIS_BLOCK;
                lore = "§9Ingame";
            } else if (server.getOnlinePlayerCount() >= server.getMaxPlayerCount()) {
                if (server.getMaxPlayerCount() == 0) {
                    material = Material.REDSTONE_BLOCK;
                    lore = "§cWird gestartet...";
                } else {
                    material = Material.GOLD_BLOCK;
                    lore = "§6Voll";
                }
            }
            ItemBuilder builder = new ItemBuilder(material).setDisplayName("§6" + server.getName());
            if (server.getMap() != null) {
                builder.setLore("§7§o" + server.getMap(), "§b" + server.getOnlinePlayerCount() + "§7/§b" + server.getMaxPlayerCount(), lore);
            } else {
                builder.setLore("§b" + server.getOnlinePlayerCount() + "§7/§b" + server.getMaxPlayerCount(), lore);
            }
            int id = Integer.parseInt(server.getName().split("-")[1]);
            builder.setAmount(id);
            inventory.setItem(slot, builder.build());
            slot++;
        }
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        Inventory inventory = event.getClickedInventory();
        ItemStack stack = event.getCurrentItem();

        if (inventory == null || inventory.getTitle() == null || !inventory.getTitle().startsWith("§cAlle ") || !inventory.getTitle().endsWith(" Server")) {
            return;
        } else {
            event.setCancelled(true);
        }

        if (stack != null) {
            if (stack.getItemMeta() == null) return;

            if (stack.getType() != Material.EMERALD_BLOCK) {
                player.playSound(player.getLocation(), Sound.NOTE_BASS, 1, 1);
                return;
            }

            player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
            String server = ChatColor.stripColor(stack.getItemMeta().getDisplayName());
            ByteArrayDataOutput out = ByteStreams.newDataOutput();
            out.writeUTF("Connect");
            out.writeUTF(server);
            player.sendPluginMessage(lobby, "BungeeCord", out.toByteArray());
        }
    }

}
