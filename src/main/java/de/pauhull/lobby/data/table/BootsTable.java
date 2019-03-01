package de.pauhull.lobby.data.table;

import de.pauhull.lobby.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class BootsTable {

    private static final String TABLE = "lobby_boots";

    private MySQL mySQL;
    private ExecutorService executorService;

    public BootsTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), `boots` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void hasBoots(UUID uuid, String boots, Consumer<Boolean> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet result = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `boots`='" + boots + "'");
                consumer.accept(result.isBeforeFirst());

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(false);
            }
        });
    }

    public void addBoots(UUID uuid, String boots) {
        hasBoots(uuid, boots, hasBoots -> {
            if (!hasBoots) {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "', '" + boots + "')");
            }
        });
    }

    public void removeBoots(UUID uuid, String boots) {
        executorService.execute(() -> {
            mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "' AND `boots`='" + boots + "'");
        });
    }

}
