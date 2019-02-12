package de.godtitan.lobby.data.table;

import de.godtitan.lobby.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BalloonTable {

    private static final String TABLE = "lobby_balloons";

    private MySQL mySQL;
    private ExecutorService executorService;

    public BalloonTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `balloon` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void hasBalloon(UUID uuid, String balloon, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `balloon`='" + balloon + "'");
                consumer.accept(result.isBeforeFirst());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void addBalloon(UUID uuid, String balloon) {
        hasBalloon(uuid, balloon, hasBalloon -> {
            if (!hasBalloon) {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + balloon + "')");
            }
        });
    }

    public void removeBalloon(UUID uuid, String balloon) {
        executorService.execute(() -> {
            mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `balloon`='" + balloon + "'");
        });
    }

}
