package de.pauhull.lobby.manager;

import de.pauhull.lobby.entity.EntityBalloon;
import de.pauhull.lobby.shop.Balloon;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Bat;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public class BalloonManager {

    private Map<String, List<EntityBalloon>> activeBalloons;

    public BalloonManager() {
        this.activeBalloons = new HashMap<>();
    }

    public void removeBalloon(EntityBalloon balloon) {
        if (balloon.isAlive()) {
            balloon.remove();
        }
        for (List<EntityBalloon> balloonList : activeBalloons.values()) {
            balloonList.remove(balloon);
        }
    }

    public boolean addBalloon(Player player, Balloon balloon) {
        if (!canAddBalloon(player)) {
            return false;
        }

        addBalloon(player, balloon.getEntity(player).spawn());
        return true;
    }

    public boolean hasBalloonActive(Player player) {
        return getActiveBalloons(player).size() > 0;
    }

    public void removeAllBalloons(Player player) {
        for (EntityBalloon balloon : getActiveBalloons(player)) {
            balloon.remove();
        }

        activeBalloons.remove(player.getName());
    }

    public List<EntityBalloon> getActiveBalloons(Player player) {
        return activeBalloons.containsKey(player.getName()) ? activeBalloons.get(player.getName()) : new ArrayList<>();
    }

    private void addBalloon(Player player, EntityBalloon balloon) {
        if (activeBalloons.containsKey(player.getName())) {
            activeBalloons.get(player.getName()).add(balloon);
        } else {
            List<EntityBalloon> balloons = new ArrayList<>();
            balloons.add(balloon);
            activeBalloons.put(player.getName(), balloons);
        }
    }

    public boolean canAddBalloon(Player player) {
        return !activeBalloons.containsKey(player.getName()) || activeBalloons.get(player.getName()).size() < 5;
    }

    public void removeAllInactiveBalloons() {
        for (World world : Bukkit.getWorlds()) {
            for (Bat bat : world.getEntitiesByClass(Bat.class)) {
                if (bat.getLeashHolder() != null) {
                    continue;
                }

                if (bat.getPassenger() != null) {
                    bat.getPassenger().remove();
                }

                bat.remove();
            }
        }
    }

}
