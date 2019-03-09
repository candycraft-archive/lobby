package de.pauhull.lobby.data.table;

import de.pauhull.lobby.data.MySQL;

import java.sql.ResultSet;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 06.03.2019
 *
 * @author pauhull
 */
public class SelectedGadgetsTable {

    private static final String TABLE = "lobby_selected_gadgets";

    private MySQL mySQL;
    private ExecutorService executorService;

    public SelectedGadgetsTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(36), `type` VARCHAR(255), `gadget` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void saveSelectedGadget(UUID uuid, String type, String gadget) {
        getSelectedGadget(uuid, type, currentGadget -> {

            if (gadget != null) {
                if (currentGadget == null) {
                    mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid + "', '" + type + "', '" + gadget + "')");
                } else {
                    mySQL.update("UPDATE `" + TABLE + "` SET `gadget`='" + gadget + "' WHERE `uuid`='" + uuid + "' AND `type`='" + type + "'");
                }
            } else {
                if (currentGadget != null) {
                    mySQL.update("DELETE FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "' AND `type`='" + type + "'");
                }
            }

        });
    }

    public void getSelectedGadget(UUID uuid, String type, Consumer<String> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet set = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "' AND `type`='" + type + "'");
                if (set.next()) {
                    consumer.accept(set.getString("gadget"));
                    return;
                }
                consumer.accept(null);

            } catch (Exception e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

}
