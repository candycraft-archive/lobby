package de.pauhull.lobby.data.table;

import de.pauhull.lobby.data.MySQL;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.function.Consumer;

/**
 * Created by Paul
 * on 06.03.2019
 *
 * @author pauhull
 */
public class LastLocationTable {

    private static final String TABLE = "lobby_last_locations";

    private MySQL mySQL;
    private ExecutorService executorService;

    public LastLocationTable(MySQL mySQL, ExecutorService executorService) {
        this.mySQL = mySQL;
        this.executorService = executorService;

        mySQL.update("CREATE TABLE IF NOT EXISTS `" + TABLE + "` (`id` INT AUTO_INCREMENT, `uuid` VARCHAR(36), `location` VARCHAR(255), PRIMARY KEY (`id`))");
    }

    public void saveLocation(UUID uuid, Location location) {
        getLocation(uuid, currentLocation -> {

            if (currentLocation == null) {
                mySQL.update("INSERT INTO `" + TABLE + "` VALUES (0, '" + uuid + "', '" + stringFromLocation(location) + "')");
            } else {
                mySQL.update("UPDATE `" + TABLE + "` SET `location`='" + stringFromLocation(location) + "' WHERE `uuid`='" + uuid + "'");
            }

        });
    }

    public void getLocation(UUID uuid, Consumer<Location> consumer) {
        executorService.execute(() -> {
            try {

                ResultSet set = mySQL.query("SELECT * FROM `" + TABLE + "` WHERE `uuid`='" + uuid + "'");
                if (set.next()) {
                    consumer.accept(locationFromString(set.getString("location")));
                    return;
                }
                consumer.accept(null);

            } catch (SQLException e) {
                e.printStackTrace();
                consumer.accept(null);
            }
        });
    }

    public Location locationFromString(String s) {
        String[] arr = s.split("/");
        World world = Bukkit.getWorld(arr[0]);
        double x = Double.valueOf(arr[1]);
        double y = Double.valueOf(arr[2]);
        double z = Double.valueOf(arr[3]);
        float yaw = Float.valueOf(arr[4]);
        float pitch = Float.valueOf(arr[5]);
        return new Location(world, x, y, z, yaw, pitch);
    }

    public String stringFromLocation(Location location) {
        return location.getWorld().getName() + "/"
                + location.getX() + "/"
                + location.getY() + "/"
                + location.getZ() + "/"
                + location.getYaw() + "/"
                + location.getPitch();
    }

}
