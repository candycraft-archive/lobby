package de.godtitan.lobby.command;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

/**
 * Created by Paul
 * on 31.12.2018
 *
 * @author pauhull
 */
public class WeatherCommand implements CommandExecutor {

    private Lobby lobby;

    public WeatherCommand(Lobby lobby) {
        this.lobby = lobby;
        lobby.getCommand("w").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lobby.weather")) {
            sender.sendMessage(Messages.NO_PERMISSIONS);
            return true;
        }

        lobby.setWeather(!lobby.isWeather());
        if (lobby.isWeather()) {
            sender.sendMessage(Messages.PREFIX + "Das Wetter kann sich nun ändern.");
        } else {
            sender.sendMessage(Messages.PREFIX + "Das Wetter ändert sich nun nicht mehr.");
        }
        return true;
    }

}
