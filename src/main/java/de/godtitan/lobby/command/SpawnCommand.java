package de.godtitan.lobby.command;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.listener.JumpAndRunListener;
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

        if (JumpAndRunListener.getInstance().getJumpAndRuns().containsKey(player.getName())) {
            player.sendMessage(Messages.PREFIX + "§7Bitte §cverlasse §7zuerst das Jump and Run!");
            return true;
        }

        if (BuildCommand.getBuilding().contains(player.getName())) {
            lobby.getLocationManager().teleport(player, "Spawn");
        } else {
            lobby.teleportToSpawn(player);
        }

        return true;
    }
}
