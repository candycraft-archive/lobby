package de.pauhull.lobby.command;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class BuildCommand implements CommandExecutor {

    @Getter
    private static ArrayList<String> building = new ArrayList<>();

    private Lobby lobby;

    public BuildCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("build").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player)) {
            return true;
        }
        Player player = (Player) sender;

        if (player.hasPermission("lobby.build")) {

            if (args.length < 1) {
                if (building.contains(player.getName())) {
                    building.remove(player.getName());

                    player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                    player.sendMessage(Messages.PREFIX + "§7Du hast den Buildmodus §cverlassen§7!");
                    player.getInventory().clear();
                    player.setLevel(0);
                    player.setExp(0);
                    player.setGameMode(GameMode.ADVENTURE);
                    player.setAllowFlight(true);

                    if (!player.hasPermission("lobby.team")) {
                        lobby.getLobbyItems().giveItems(player);
                    } else {
                        lobby.getLobbyItems().giveTeamItems(player);
                    }

                } else {
                    building.add(player.getName());
                    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1, 1);
                    player.getInventory().clear();
                    player.sendMessage(Messages.PREFIX + "§7Du hast den Buildmodus §abetreten§7!");
                    player.setGameMode(GameMode.CREATIVE);
                }

            } else if (args.length == 1) {

                Player buildingPlayer = Bukkit.getPlayer(args[0]);
                if (buildingPlayer != null) {

                    if (building.contains(buildingPlayer.getName())) {
                        building.remove(buildingPlayer.getName());
                        buildingPlayer.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
                        player.sendMessage(Messages.PREFIX + "§7Du hast §e" + buildingPlayer.getName() + " §7aus dem Buildmodus §centfernt§7!");
                        buildingPlayer.sendMessage(Messages.PREFIX + "§7Der Spieler §e" + player.getName() + " §7hat dich aus dem Buildmodus §centfernt§7!");
                        buildingPlayer.getInventory().clear();
                        buildingPlayer.setGameMode(GameMode.ADVENTURE);

                        if (!buildingPlayer.hasPermission("Lobby.team")) {
                            lobby.getLobbyItems().giveItems(buildingPlayer);
                        } else {
                            lobby.getLobbyItems().giveTeamItems(buildingPlayer);
                        }
                    } else {
                        building.add(buildingPlayer.getName());
                        buildingPlayer.playSound(buildingPlayer.getLocation(), Sound.NOTE_PLING, 1, 1);
                        buildingPlayer.setGameMode(GameMode.CREATIVE);
                        buildingPlayer.getInventory().clear();
                        buildingPlayer.setLevel(0);
                        buildingPlayer.setExp(0);

                        player.sendMessage(Messages.PREFIX + "§7Du hast §e" + buildingPlayer.getName() + " §7in den Buildmodus §agesetzt§7!");
                        buildingPlayer.sendMessage(Messages.PREFIX + "§7Der Spieler §e" + player.getName() + " §7hat dich in den Buildmodus §agesetzt§7!");
                    }
                } else {
                    player.sendMessage(Messages.NOT_ONLINE);
                }
            } else {
                player.sendMessage(Messages.PREFIX + "§c/build <Spieler>");
            }
        } else {
            player.sendMessage(Messages.NO_PERMISSIONS);
        }

        return true;
    }
}
