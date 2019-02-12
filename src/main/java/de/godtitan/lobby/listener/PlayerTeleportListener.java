package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerTeleportEvent;

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
        lobby.getBalloonManager().removeAllBalloons(event.getPlayer());
    }

}
