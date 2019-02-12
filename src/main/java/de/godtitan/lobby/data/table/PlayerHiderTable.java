package de.godtitan.lobby.data.table;

import de.godtitan.lobby.data.MySQL;
import de.godtitan.lobby.inventory.PlayerHiderInventory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class PlayerHiderTable {

    private static final String TABLE = "lobby_player_hider";

    private MySQL mySQL;
    private ExecutorService executorService;

    public PlayerHiderTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `mode` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void getMode(UUID uuid, Consumer<PlayerHiderInventory.HideMode> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
                if (result.next()) {
                    consumer.accept(PlayerHiderInventory.HideMode.valueOf(result.getString("mode")));
                } else {
                    consumer.accept(PlayerHiderInventory.HideMode.ALL);
                }

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(PlayerHiderInventory.HideMode.ALL);
            }
        });
    }

    public void exists(UUID uuid, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
                consumer.accept(result.isBeforeFirst());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void setMode(UUID uuid, PlayerHiderInventory.HideMode mode) {
        exists(uuid, exists -> {
            if (exists) {
                mySQL.update("UPDATE `" + TABLE + "` SET `mode`='" + mode.name() + "'");
            } else {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + mode.name() + "')");
            }
        });
    }

}
