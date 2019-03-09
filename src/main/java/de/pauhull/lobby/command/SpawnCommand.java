package de.pauhull.lobby.command;

import de.pauhull.lobby.Lobby;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class SpawnCommand implements CommandExecutor {

    private Lobby lobby;

    public SpawnCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("spawn").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (BuildCommand.getBuilding().contains(player.getName())) {
            lobby.getLocationManager().teleport(player, "Spawn");
        } else {
            lobby.teleportToSpawn(player, null);
        }

        return true;
    }
}
