package de.godtitan.lobby.manager;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import java.io.File;
import java.io.IOException;

public class LobbyLocationManager {

    private File file;
    private FileConfiguration config;
    private Lobby lobby;

    public LobbyLocationManager(Lobby lobby) {
        this.lobby = lobby;
        this.file = new File(lobby.getDataFolder(), "locations.yml");
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public void setLocation(Location location, String name) {
        config.set(name.toUpperCase(), location);

        try {
            config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public boolean exists(String name) {
        return config.isSet(name.toUpperCase());
    }

    public Location getLocation(String name) {
        return (Location) config.get(name.toUpperCase());
    }

    public void teleport(Player player, String location) {
        if (lobby.getLocationManager().exists(location)) {
            player.teleport(lobby.getLocationManager().getLocation(location));
            player.playSound(player.getLocation(), Sound.ENDERMAN_TELEPORT, 1, 1);
        } else {
            player.sendMessage(Messages.PREFIX + "§7Diese §eLocation §7wurde noch nicht gesetzt!");
        }
    }
}
