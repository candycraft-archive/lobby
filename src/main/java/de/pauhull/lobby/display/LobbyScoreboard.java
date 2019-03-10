package de.pauhull.lobby.display;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.pauhull.coins.api.CoinAPI;
import de.pauhull.lobby.Lobby;
import de.pauhull.scoreboard.CustomScoreboard;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class LobbyScoreboard extends CustomScoreboard {

    private DisplayScore rank, coins, onlineTime;

    public LobbyScoreboard(Player player) {
        super(player, player.getName(), "§d§lCandyCraft");
        this.descending = false;
    }

    @Override
    public void show() {

        this.onlineTime = new DisplayScore("§e Lädt...");
        new DisplayScore("Onlinezeit:");
        new DisplayScore();
        this.coins = new DisplayScore("§d Lädt...");
        new DisplayScore("Coins:");
        new DisplayScore();
        new DisplayScore("§c CandyCraft.de");
        new DisplayScore("TeamSpeak:");
        new DisplayScore();
        this.rank = new DisplayScore("§b Lädt...");
        new DisplayScore("Rang:");
        new DisplayScore();
        new DisplayScore("§a " + TimoCloudAPI.getBukkitAPI().getThisServer().getName().replace('-', ' '));
        new DisplayScore("Server:");
        new DisplayScore();

        super.show();
    }

    @Override
    public void update() {
        String rankScore = "§b " + getPlayerGroup(player);
        if (!rank.getScore().getEntry().equals(rankScore)) {
            rank.setName(rankScore);
        }

        CoinAPI.getInstance().getCoins(player.getUniqueId(), coins -> {
            NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
            String coinsScore = "§d " + format.format(coins);
            if (!this.coins.getScore().getEntry().equals(coinsScore)) {
                this.coins.setName(coinsScore);
            }
        });

        if (this.onlineTime.getScore().getEntry().contains("Lädt")) {
            Lobby.getInstance().getPlaytimeTable().getTime(player.getUniqueId(), onlineTime -> {
                int hours = (int) Math.floor(onlineTime / (double) TimeUnit.HOURS.toMillis(1));
                this.onlineTime.setName("§e " + hours + "h");
            });
        }
    }

    private String getPlayerGroup(Player player) {
        PermissionGroup group = super.getHighestPermissionGroup(player);

        if (group != null) {
            return group.getName();
        } else {
            return "§4Nicht gefunden";
        }
    }

}
