package de.godtitan.lobby.listener;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.command.BuildCommand;
import de.godtitan.lobby.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.ParseException;

public class PlayerJoinListener implements Listener {

    private Lobby lobby;

    public PlayerJoinListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws ParseException {
        Player player = event.getPlayer();

        if (player.isOp()) {
            player.setOp(false);
        }

        BuildCommand.getBuilding().remove(player.getName());

        event.setJoinMessage(null);
        Title.sendTitle(player, "§eWillkommen:", "§8➜ §c" + player.getDisplayName(), 20, 60, 40);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);

        lobby.teleportToSpawn(player);

    }

}
