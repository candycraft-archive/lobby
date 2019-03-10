package de.pauhull.lobby.data.table;

import de.pauhull.lobby.data.MySQL;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 10.03.2019
 *
 * @author pauhull
 */
public class PlaytimeTable {

    private static final String TABLE = "playtime";

    private MySQL mySQL;
    private ExecutorService executorService;

    public PlaytimeTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;
    }

    public void getTime(UUID uuid, Consumer<Long> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet set = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "'");
                if (set.next()) {
                    consumer.accept(set.getLong("time"));
                    return;
                }

                consumer.accept(0L);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(0L);
            }
        });
    }

}
