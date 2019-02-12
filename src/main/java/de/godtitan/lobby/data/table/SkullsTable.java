package de.godtitan.lobby.data.table;

import de.godtitan.lobby.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class SkullsTable {

    private static final String TABLE = "lobby_skulls";

    private MySQL mySQL;
    private ExecutorService executorService;

    public SkullsTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `skull` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void hasSkull(UUID uuid, String skull, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `skull`='" + skull + "'");
                consumer.accept(result.isBeforeFirst());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void addSkull(UUID uuid, String skull) {
        hasSkull(uuid, skull, hasSkull -> {
            if (!hasSkull) {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + skull + "')");
            }
        });
    }

    public void removeSkull(UUID uuid, String skull) {
        executorService.execute(() -> {
            mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `skull`='" + skull + "'");
        });
    }

}
