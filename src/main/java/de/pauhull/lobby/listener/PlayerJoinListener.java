package de.pauhull.lobby.listener;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.command.BuildCommand;
import de.pauhull.lobby.shop.Balloon;
import de.pauhull.lobby.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.SkullMeta;

public class PlayerJoinListener implements Listener {

    private Lobby lobby;

    public PlayerJoinListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (player.isOp()) {
            player.setOp(false);
        }

        BuildCommand.getBuilding().remove(player.getName());

        event.setJoinMessage(null);
        Title.sendTitle(player, "§eWillkommen:", "§8➜ §c" + player.getDisplayName(), 20, 60, 40);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);

        Location locationInAir = player.getLocation().clone();
        locationInAir.setY(5000);
        player.teleport(locationInAir);
        lobby.getLastLocationTable().getLocation(player.getUniqueId(), location -> {
            Bukkit.getScheduler().runTask(lobby, () -> {
                lobby.teleportToSpawn(player, location);
            });
        });

        lobby.getSelectedGadgetsTable().getSelectedGadget(player.getUniqueId(), "BALLOON", balloonString -> {
            if (balloonString == null) {
                return;
            }

            Bukkit.getScheduler().runTask(lobby, () -> {
                String[] splitted = balloonString.split("/");
                for (String string : splitted) {
                    Balloon balloon = Balloon.valueOf(string);
                    lobby.getBalloonManager().addBalloon(player, balloon);
                }
            });
        });

        lobby.getSelectedGadgetsTable().getSelectedGadget(player.getUniqueId(), "SKULL", owner -> {
            if (owner == null) {
                return;
            }

            Bukkit.getScheduler().runTask(lobby, () -> {
                ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
                SkullMeta meta = (SkullMeta) stack.getItemMeta();
                meta.setOwner(owner);
                meta.setDisplayName("§8» §e" + owner);
                stack.setItemMeta(meta);
                player.getInventory().setHelmet(stack);
            });
        });

        /*
        Date start = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-02-17 08:00:00");
        Date end = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2019-03-01 12:00:00");
        Date now = new Date();

        if (now.getTime() > start.getTime() && now.getTime() < end.getTime()) {
            lobby.getBalloonTable().hasBalloon(player.getUniqueId(), Balloon.STYLEX.toString(), hasBalloon -> {
                if (!hasBalloon) {
                    lobby.getBalloonTable().addBalloon(player.getUniqueId(), Balloon.STYLEX.toString());

                    player.sendMessage(" ");
                    player.sendMessage("§8➜ §d§lDanke, dass du auf dem §oCandyCraft.de§d§l Server spielst!");
                    player.sendMessage("§8➜ §d§lAls Dankeschön erhältst du:");
                    player.sendMessage("§a§l§o+ §d§o leStylex Special-Ballon");
                    player.sendMessage("§8➜ §b§lHol deine Freunde bis zum 01.03.2019 um 12:00 Uhr, damit sie ihn auch erhalten!");
                    player.sendMessage(" ");
                }
            });
        }
        */
    }

}
