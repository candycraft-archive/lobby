package de.godtitan.lobby.command;

import de.godtitan.lobby.Lobby;
import de.pauhull.uuidfetcher.common.communication.message.ConnectMessage;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class EventServerCommand implements CommandExecutor {

    private Lobby lobby;

    public EventServerCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("eventserver").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        new ConnectMessage(player.getName(), "jasspis-1").sendToProxy("Proxy");

        return true;
    }
}
