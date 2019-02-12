package de.godtitan.lobby.command;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.manager.LobbyLocationManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SetLocationCommand implements CommandExecutor {

    private Lobby lobby;

    public SetLocationCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("setlocation").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (player.hasPermission("lobby.setlocation")) {
            if (args.length == 1) {
                LobbyLocationManager locationManager = lobby.getLocationManager();

                String[] availableLocations = {"PaintWars", "Spawn", "Bedwars", "Freebuild", "Oneline", "BuildFFA", "SkyWars", "SoupFFA", "Sea", "Hill", "Lighthouse"};
                for (String location : availableLocations) {
                    if (args[0].equalsIgnoreCase(location)) {
                        locationManager.setLocation(player.getLocation(), location);
                        player.sendMessage(Messages.PREFIX + "§7Du hast die Location §e" + location + "§7 gesetzt!");
                        return true;
                    }
                }

                player.sendMessage(Messages.PREFIX + "§cDiese Location ist nicht bekannt!");
            } else {
                player.sendMessage(Messages.PREFIX + "§c/setlocation <Location>");
            }
        } else {
            player.sendMessage(Messages.NO_PERMISSIONS);
        }
        return true;
    }

}
