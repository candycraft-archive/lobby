package de.godtitan.lobby.command;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.pauhull.utils.misc.TextWriter;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 15.12.2018
 *
 * @author pauhull
 */
public class TextCommand implements CommandExecutor {

    private Lobby lobby;

    public TextCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("text").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!sender.hasPermission("lobby.text")) {
            sender.sendMessage(Messages.NO_PERMISSIONS);
            return true;
        }

        if (!(sender instanceof Player)) {
            sender.sendMessage(Messages.PREFIX + "Nur §cSpieler §7können diesen Command benutzen!");
            return true;
        }

        if (args.length < 2 || !isMaterial(args[0])) {
            sender.sendMessage(Messages.PREFIX + "§c/text <Material> <Text...>");
            return true;
        }

        Player player = (Player) sender;
        Material type = Material.valueOf(args[0]);
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i < args.length; i++) {
            if (i > 1) {
                builder.append(" ");
            }

            builder.append(args[i]);
        }

        new TextWriter(builder.toString()).place(player.getLocation(), yawToFace(player.getLocation().getYaw()), true, type);

        return true;
    }

    private boolean isMaterial(String material) {
        try {
            Material.valueOf(material);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private BlockFace yawToFace(float yaw) {
        return new BlockFace[]{BlockFace.SOUTH, BlockFace.WEST, BlockFace.NORTH, BlockFace.EAST}[(Math.round(yaw / 90.0F) & 0x3)];
    }

}
