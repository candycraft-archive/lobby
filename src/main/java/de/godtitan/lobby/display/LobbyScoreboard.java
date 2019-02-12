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
    }

    @Override
    public void show() {
        new NovusScore("§cServer:");
        new NovusScore("§f " + TimoCloudAPI.getBukkitAPI().getThisServer().getName());
        new NovusScore();
        new NovusScore("§cRang:");
        this.rank = new NovusScore("§f Lädt...");
        new NovusScore();
        new NovusScore("§cTeamSpeak:");
        new NovusScore("§f frag moritz");
        new NovusScore();
        new NovusScore("§cCoins:");
        this.coins = new NovusScore("§f§f Lädt...");

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
