package de.pauhull.lobby.listener;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.entity.EntityBalloon;
import de.pauhull.lobby.shop.Balloon;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 25.11.2018
 *
 * @author pauhull
 */
public class PlayerTeleportListener implements Listener {

    private Lobby lobby;

    public PlayerTeleportListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        List<Balloon> activeBalloons = new ArrayList<>();
        for (EntityBalloon balloon : lobby.getBalloonManager().getActiveBalloons(player)) {
            activeBalloons.add(balloon.getBalloon());
        }
        lobby.getBalloonManager().removeAllBalloons(player);

        Bukkit.getScheduler().scheduleSyncDelayedTask(lobby, () -> {
            for (Balloon balloon : activeBalloons) {
                lobby.getBalloonManager().addBalloon(player, balloon);
            }
        }, 1);
    }

}
