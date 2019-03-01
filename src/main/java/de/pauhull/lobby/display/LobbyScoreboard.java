package de.pauhull.lobby.display;

import cloud.timo.TimoCloud.api.TimoCloudAPI;
import de.pauhull.coins.api.CoinAPI;
import de.pauhull.scoreboard.NovusScoreboard;
import org.bukkit.entity.Player;
import ru.tehkode.permissions.PermissionGroup;

import java.text.NumberFormat;
import java.util.Locale;

public class LobbyScoreboard extends NovusScoreboard {

    private NovusScore rank, coins;

    public LobbyScoreboard(Player player) {
        super(player, player.getName(), "§d§lCandyCraft");
        this.descending = false;
    }

    @Override
    public void show() {

        this.coins = new NovusScore("§d Lädt...");
        new NovusScore("Coins:");
        new NovusScore();
        new NovusScore("§c CandyCraft.de");
        new NovusScore("TeamSpeak:");
        new NovusScore();
        this.rank = new NovusScore("§b Lädt...");
        new NovusScore("Rang:");
        new NovusScore();
        new NovusScore("§a " + TimoCloudAPI.getBukkitAPI().getThisServer().getName().replace('-', ' '));
        new NovusScore("Server:");
        new NovusScore();

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
