package de.godtitan.lobby.command;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import cloud.timo.TimoCloud.api.objects.ServerObject;
import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.EnderCrystal;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

public class SpawnCommunityCommand implements CommandExecutor {

    private Lobby lobby;

    public SpawnCommunityCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("spawncommunity").setExecutor(this);
    }

    public static void updateCrystals(Lobby lobby) {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(lobby, () -> {

            try {
                ServerObject server = TimoCloudAPI.getUniversalAPI().getServer("Community-1");

                if (server == null)
                    return;

                int onlineCount = server.getOnlinePlayerCount();
                int maxPlayers = server.getMaxPlayerCount();

                for (World world : Bukkit.getWorlds()) {
                    for (EnderCrystal crystal : world.getEntitiesByClass(EnderCrystal.class)) {
                        crystal.setCustomName("§cCommunity §8× §7§oRechtsklick §8[§f" + onlineCount + "§8/§f" + maxPlayers + "§8]");
                        crystal.setCustomNameVisible(true);
                    }
                }
            } catch (NoClassDefFoundError ignored) {
            }

        }, 20, 20);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (player.hasPermission("system.spawncommunity")) {

            Location location = player.getLocation();

            for (Entity entity : Bukkit.getWorld("Lobby").getEntities()) {
                if (entity instanceof EnderCrystal) {
                    entity.remove();
                }
            }

            location.getWorld().spawn(location, EnderCrystal.class);

        } else {
            player.sendMessage(Messages.NO_PERMISSIONS);
        }
        return true;
    }

}
