package de.pauhull.lobby.listener;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.command.BuildCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

public class PlayerMoveListener implements Listener {

    private Lobby lobby;

    public PlayerMoveListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerMove(PlayerMoveEvent event) {
        Player player = event.getPlayer();

        if (player.getLocation().getY() < 0) {
            lobby.teleportToSpawn(player);
            return;
        }

        if (player.getLocation().getBlock().getType() == Material.WATER_LILY) {

            if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                player.setVelocity(player.getLocation().getDirection().setY(1.5));
                player.playSound(player.getLocation(), Sound.CAT_HISS, 1, 1);
            }
        }

        if (!BuildCommand.getBuilding().contains(player.getName()) && !player.hasPermission("lobby.fly")) {
            if (player.isFlying()) {
                player.setFlying(false);

                if (player.getGameMode() != GameMode.CREATIVE && player.getGameMode() != GameMode.SPECTATOR) {
                    player.setVelocity(player.getLocation().getDirection().setY(1.5));
                    player.playSound(player.getLocation(), Sound.ENDERDRAGON_WINGS, 1, 1);
                    player.setAllowFlight(false);
                    final String name = player.getName();

                    Bukkit.getScheduler().scheduleSyncDelayedTask(lobby, () -> {

                        Player onlinePlayer = Bukkit.getPlayer(name);
                        if (onlinePlayer == null)
                            return;

                        onlinePlayer.setAllowFlight(true);
                    }, 35);
                }
            }
        }
    }

}