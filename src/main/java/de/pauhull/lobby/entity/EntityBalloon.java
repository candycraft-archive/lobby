package de.pauhull.lobby.entity;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.shop.Balloon;
import de.pauhull.lobby.util.Util;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public abstract class EntityBalloon {

    @Getter
    private static List<EntityBalloon> allBalloons = new ArrayList<>();

    @Getter
    protected Location location;

    @Getter
    protected Player player;

    @Getter
    protected Bat bat;

    @Getter
    protected Balloon balloon;

    @Getter
    protected boolean alive = true;

    protected EntityBalloon(Balloon balloon, Location location, Player player) {
        this.balloon = balloon;
        this.location = location;
        this.player = player;
    }

    public static void schedule() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(Lobby.getInstance(), () -> {
            for (World world : Bukkit.getWorlds()) {
                for (Bat bat : world.getEntitiesByClass(Bat.class)) {
                    if (bat.getPassenger() != null && bat.getPassenger() instanceof FallingBlock) {
                        ((EntityFallingBlock) ((CraftEntity) bat.getPassenger()).getHandle()).ticksLived = 1;
                    }

                    if (bat.isLeashed() && bat.getLeashHolder() != null && bat.getLeashHolder() instanceof Player) {
                        Player player = (Player) bat.getLeashHolder();

                        double motionX = (player.getLocation().getX() - bat.getLocation().getX()) / 5.0;
                        double motionY = bat.getLocation().getY() - player.getLocation().getY() < 5.0 ? 0.35 : -0.35;
                        double motionZ = (player.getLocation().getZ() - bat.getLocation().getZ()) / 5.0;

                        bat.setVelocity(new Vector(motionX, motionY, motionZ));
                    }
                }
            }
        }, 1, 1);
    }

    public EntityBalloon spawn() {
        allBalloons.add(this);
        this.bat = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
        this.bat.setLeashHolder(player);
        EntityBat nmsBat = (EntityBat) ((CraftEntity) bat).getHandle();
        this.setSilent(nmsBat);
        this.bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        this.bat.setRemoveWhenFarAway(false);
        return this;
    }

    public EntityBalloon remove() {
        allBalloons.remove(this);
        alive = false;
        this.bat.getWorld().playSound(bat.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        Util.playCloudEffect(bat.getLocation());
        this.bat.remove();
        return this;
    }

    protected void setSilent(EntityBat bat) {
        bat.b(true);
    }

}
