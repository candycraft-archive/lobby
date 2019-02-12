package de.godtitan.lobby.display;

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

        this.coins = new NovusScore("§f§f Lädt...");
        new NovusScore("§cCoins:");
        new NovusScore();
        new NovusScore("§f frag moritz");
        new NovusScore("§cTeamSpeak:");
        new NovusScore();
        this.rank = new NovusScore("§f Lädt...");
        new NovusScore("§cRang:");
        new NovusScore();
        new NovusScore("§f " + TimoCloudAPI.getBukkitAPI().getThisServer().getName().replace('-', ' '));
        new NovusScore("§cServer:");

        super.show();
    }

    @Override
    public void update() {
        String rankScore = "§f " + getPlayerGroup(player);
        if (!rank.getScore().getEntry().equals(rankScore)) {
            rank.setName(rankScore);
        }

        CoinAPI.getInstance().getCoins(player.getUniqueId(), coins -> {
            NumberFormat format = NumberFormat.getInstance(Locale.GERMAN);
            String coinsScore = "§f " + format.format(coins);
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
