package de.pauhull.lobby.entity;

import de.pauhull.lobby.shop.Balloon;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.FallingBlock;
import org.bukkit.entity.Player;

/**
 * Created by Paul
 * on 24.11.2018
 *
 * @author pauhull
 */
public class EntityBalloonBlock extends EntityBalloon {

    @Getter
    private String displayName;

    @Getter
    private FallingBlock fallingBlock;

    @Getter
    private Material material;

    @Getter
    private byte data;

    public EntityBalloonBlock(Balloon balloon, Location location, Player player, Material material, byte data, String displayName) {
        super(balloon, location, player);
        this.material = material;
        this.data = data;
        this.displayName = displayName;
    }

    @Override
    public EntityBalloon spawn() {
        super.spawn();
        this.fallingBlock = location.getWorld().spawnFallingBlock(location, material, data);
        this.fallingBlock.setCustomName(displayName);
        this.fallingBlock.setCustomNameVisible(true);
        this.fallingBlock.setDropItem(false);
        this.fallingBlock.setHurtEntities(false);
        this.bat.setPassenger(fallingBlock);
        return this;
    }

    @Override
    public EntityBalloon remove() {
        super.remove();
        this.fallingBlock.remove();
        return this;
    }

}
