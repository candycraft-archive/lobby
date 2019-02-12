package de.godtitan.lobby.entity;

import de.godtitan.lobby.Lobby;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EntityBat;
import net.minecraft.server.v1_8_R3.EntityFallingBlock;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftEntity;
import org.bukkit.entity.Bat;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.Random;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public abstract class EntityBalloon {

    protected static Random random = new Random();

    @Getter
    protected Location location;

    @Getter
    protected Player player;

    @Getter
    protected Bat bat;

    protected EntityBalloon(Location location, Player player) {
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
        this.bat = (Bat) location.getWorld().spawnEntity(location, EntityType.BAT);
        this.bat.setLeashHolder(player);
        EntityBat nmsBat = (EntityBat) ((CraftEntity) bat).getHandle();
        this.setSilent(nmsBat);
        this.bat.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, Integer.MAX_VALUE, 0));
        this.bat.setRemoveWhenFarAway(false);
        return this;
    }

    public EntityBalloon remove() {
        this.bat.getWorld().playSound(bat.getLocation(), Sound.CHICKEN_EGG_POP, 1, 1);
        this.bat.getWorld().playEffect(bat.getLocation(), Effect.CLOUD, 1, 1);
        this.bat.remove();
        return this;
    }

    protected void setSilent(EntityBat bat) {
        bat.b(true);
    }

}
