package de.godtitan.lobby.util;

import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Field;
import java.util.UUID;

/**
 * Created by Paul
 * on 13.02.2019
 *
 * @author pauhull
 */
public class Skull {

    public static ItemStack getFromBase64(String base64) {
        ItemStack stack = new ItemStack(Material.SKULL_ITEM, 1, (short) 3);
        ItemMeta meta = stack.getItemMeta();
        GameProfile profile = new GameProfile(UUID.randomUUID(), null);
        profile.getProperties().put("textures", new Property("textures", base64));
        try {
            Field profileField = meta.getClass().getDeclaredField("profile");
            profileField.setAccessible(true);
            profileField.set(meta, profile);
        } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
            e.printStackTrace();
        }
        stack.setItemMeta(meta);
        return stack;
    }

}
