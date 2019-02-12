package de.godtitan.lobby.data.table;

import de.godtitan.lobby.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

public class PrivacyPolicyTable {

    private static final String TABLE = "lobby_privacy";

    private MySQL mySQL;
    private ExecutorService executorService;

    public PrivacyPolicyTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void hasAccepted(UUID uuid, Consumer<Boolean> consumer) {
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

    public void setAccepted(UUID uuid, boolean accepted) {
        hasAccepted(uuid, hasAccepted -> {
            if (accepted) {
                if (!hasAccepted) {
                    mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid.toString() + "')");
                }
            } else {
                if (hasAccepted) {
                    mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid.toString() + "'");
                }
            }
        });
    }

}
