package de.godtitan.lobby.data.table;

import de.godtitan.lobby.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BetaTesterTable {

    private static final String TABLE = "lobby_betatester";

    private MySQL mySQL;
    private ExecutorService executorService;

    public BetaTesterTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void isBetaTester(UUID uuid, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {
                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
                consumer.accept(result.isBeforeFirst());
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
    }

    public void setBetaTester(UUID uuid, boolean betaTester) {
        isBetaTester(uuid, isBetaTester -> {
            if (betaTester) {
                if (!isBetaTester) {
                    mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "')");
                }
            } else {
                if (isBetaTester) {
                    mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
                }
            }
        });
    }
}
