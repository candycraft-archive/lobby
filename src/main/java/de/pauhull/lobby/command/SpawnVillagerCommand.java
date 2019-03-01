package de.pauhull.lobby.command;

import de.pauhull.lobby.Lobby;
import net.minecraft.server.v1_8_R3.EntityVillager;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftVillager;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;

/**
 * Created by Paul
 * on 13.02.2019
 *
 * @author pauhull
 */
public class SpawnVillagerCommand implements CommandExecutor {

    private Lobby lobby;

    public SpawnVillagerCommand(Lobby lobby) {
        this.lobby = lobby;

        lobby.getCommand("spawnvillager").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("lobby.spawnvillager")) {
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 0 || (!args[0].equals("0") && !args[0].equals("1"))) {
            return true;
        }

        for (World world : Bukkit.getWorlds()) {
            for (Villager v : world.getEntitiesByClass(Villager.class)) {
                if (v.getCustomName() != null && v.getCustomName().equals(args[0].equals("0") ? "§9§lPaint§f§lWars" : "§a§lBed§b§lWars")) {
                    v.remove();
                }
            }
        }

        Villager villager = (Villager) player.getWorld().spawnEntity(player.getLocation(), EntityType.VILLAGER);
        villager.setCustomNameVisible(true);
        if (args[0].equals("0")) {
            villager.setCustomName("§9§lPaint§f§lWars");
        } else {
            villager.setCustomName("§a§lBed§b§lWars");
        }

        EntityVillager nmsVillager = ((CraftVillager) villager).getHandle();
        NBTTagCompound tag = new NBTTagCompound();
        nmsVillager.c(tag);
        tag.setInt("NoAI", 1);
        nmsVillager.f(tag);
        nmsVillager.b(true);
        return true;
    }

}
