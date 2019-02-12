package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityUnleashEvent;

/**
 * Created by Paul
 * on 25.11.2018
 *
 * @author pauhull
 */
public class EntityUnleashListener implements Listener {

    private Lobby lobby;

    public EntityUnleashListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onEntityUnleash(EntityUnleashEvent event) {
        event.getEntity().remove();
    }

}
