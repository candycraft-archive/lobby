package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.ServerListPingEvent;

public class ServerListPingListener implements Listener {

    private Lobby lobby;

    public ServerListPingListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onServerListPing(ServerListPingEvent event) {
        event.setMotd("§b§lJoin");
    }

}
