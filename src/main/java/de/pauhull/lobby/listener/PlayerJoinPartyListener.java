package de.pauhull.lobby.listener;

import de.pauhull.friends.spigot.event.PlayerJoinPartyEvent;
import de.pauhull.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Paul
 * on 14.12.2018
 *
 * @author pauhull
 */
public class PlayerJoinPartyListener implements Listener {

    private Lobby lobby;

    public PlayerJoinPartyListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerJoinParty(PlayerJoinPartyEvent event) {
        lobby.getScoreboardManager().updateTeam(event.getPlayer());
    }

}
