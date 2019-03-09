package de.pauhull.lobby.entity;

import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.shop.Balloon;
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

    public EntityBalloonSkull(Balloon balloon, Location location, Player player, String owner, String displayName) {
        super(balloon, location, player);
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
