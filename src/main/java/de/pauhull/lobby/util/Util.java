package de.pauhull.lobby.util;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 01.03.2019
 *
 * @author pauhull
 */
public class Util {

    public static void playCloudEffect(Location location) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_NORMAL, true,
                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                0.25f, 0f, 0.25f, 0, 3);

        for (Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
        }
    }

}
