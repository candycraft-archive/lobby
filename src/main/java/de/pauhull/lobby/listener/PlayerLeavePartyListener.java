package de.pauhull.lobby.listener;

import de.pauhull.friends.spigot.event.PlayerLeavePartyEvent;
import de.pauhull.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

/**
 * Created by Paul
 * on 05.01.2019
 *
 * @author pauhull
 */
public class PlayerLeavePartyListener implements Listener {

    private Lobby lobby;

    public PlayerLeavePartyListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerLeaveParty(PlayerLeavePartyEvent event) {
        this.lobby.getScoreboardManager().updateTeam(event.getPlayer());
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (event.getParty().getMembers().contains(player.getName())) {
                this.lobby.getScoreboardManager().updateTeam(player);
            }
        }
    }

}
