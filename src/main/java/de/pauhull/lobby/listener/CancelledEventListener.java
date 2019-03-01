package de.pauhull.lobby.listener;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.command.BuildCommand;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.hanging.HangingBreakByEntityEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;

public class CancelledEventListener implements Listener {

    private Lobby lobby;

    public CancelledEventListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryInteract(InventoryInteractEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onDropItemEvent(PlayerDropItemEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItemEvent(PlayerPickupItemEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPickupItemEvent(EntityDamageEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onSignChange(SignChangeEvent event) {
        for (int i = 0; i < 4; i++) {
            String line = event.getLine(i);
            event.setLine(i, ChatColor.translateAlternateColorCodes('&', line));
        }
    }

    @EventHandler
    public void onWeatherChange(WeatherChangeEvent event) {
        if (!lobby.isWeather()) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onFoodLevelChange(FoodLevelChangeEvent event) {
        event.setCancelled(true);
        event.setFoodLevel(20);
    }

    @EventHandler
    public void onHangingBreakByEntity(HangingBreakByEntityEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getRemover().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onCreatureSpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() != CreatureSpawnEvent.SpawnReason.CUSTOM) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAchievementAwarded(PlayerAchievementAwardedEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerInteractEntityEvent(PlayerInteractEntityEvent event) {
        if (!BuildCommand.getBuilding().contains(event.getPlayer().getName())) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerBedEnter(PlayerBedEnterEvent event) {
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        event.setDeathMessage(null);
        event.setKeepInventory(true);
        event.setDroppedExp(0);
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        Lobby.getInstance().getBuyItemInventory().onInventoryClick(event);
        if (!BuildCommand.getBuilding().contains(event.getWhoClicked().getName())) {
            event.setCancelled(true);
        }
    }

}