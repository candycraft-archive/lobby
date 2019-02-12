package de.godtitan.lobby.entity;

import de.godtitan.lobby.Lobby;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public class EntityBalloonSkull extends EntityBalloon {

    @Getter
    private ArmorStand armorStand;

    @Getter
    private String owner;

    @Getter
    private String displayName;

    public EntityBalloonSkull(Location location, Player player, String owner, String displayName) {
        super(location, player);
        this.owner = owner;
        this.displayName = displayName;
    }

    @Override
    public EntityBalloon spawn() {
        super.spawn();
        ItemStack stack = Lobby.getInstance().getHeadCache().getHead(owner);
        this.armorStand = (ArmorStand) location.getWorld().spawnEntity(location, EntityType.ARMOR_STAND);
        this.armorStand.setVisible(false);
        this.armorStand.setBasePlate(false);
        this.armorStand.setHelmet(stack);
        this.armorStand.setCustomNameVisible(true);
        this.armorStand.setCustomName(displayName);
        this.bat.setPassenger(armorStand);
        return this;
    }

    @Override
    public EntityBalloon remove() {
        super.remove();
        armorStand.remove();
        return this;
    }

}
