package de.pauhull.lobby.listener;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.command.BuildCommand;
import de.pauhull.lobby.shop.Balloon;
import de.pauhull.lobby.util.Title;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class PlayerJoinListener implements Listener {

    private Lobby lobby;

    public PlayerJoinListener(Lobby lobby) {
        this.lobby = lobby;

        Bukkit.getPluginManager().registerEvents(this, lobby);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) throws ParseException {
        Player player = event.getPlayer();

        if (player.isOp()) {
            player.setOp(false);
        }

        BuildCommand.getBuilding().remove(player.getName());

        event.setJoinMessage(null);
        Title.sendTitle(player, "§eWillkommen:", "§8➜ §c" + player.getDisplayName(), 20, 60, 40);
        player.playSound(player.getLocation(), Sound.FIREWORK_LAUNCH, 1, 1);

        lobby.teleportToSpawn(player);


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
    }

}
