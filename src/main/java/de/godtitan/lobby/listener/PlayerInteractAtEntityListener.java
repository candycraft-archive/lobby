package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.command.BuildCommand;
import org.bukkit.Bukkit;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;

public class PlayerInteractAtEntityListener implements Listener {

    private Lobby lobby;

    public PlayerInteractAtEntityListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerInteractAtEntityEvent(PlayerInteractAtEntityEvent event) {
        Player player = event.getPlayer();

        if (BuildCommand.getBuilding().contains(player.getName())) {
            return;
        }

        event.setCancelled(true);

        if (event.getRightClicked().getType() == EntityType.VILLAGER) {
            Villager clicked = (Villager) event.getRightClicked();
            if (clicked.getCustomName().equals("§9§lPaint§f§lWars")) {
                lobby.getServerInventory().show(player, "PaintWars");
            } else if (clicked.getCustomName().equals("§a§lBed§b§lWars")) {
                lobby.getServerInventory().show(player, "BedWars");
            }
        }
    }
}
