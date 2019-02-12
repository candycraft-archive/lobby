package de.godtitan.lobby.shop;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.util.ItemBuilder;
import de.pauhull.coins.spigot.buyable.SpigotBuyable;
import lombok.Getter;
import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.*;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

public enum Boots implements SpigotBuyable {

    HEART("§8» §cHerz-Schuhe", Color.RED, 6500, player -> {
        player.getWorld().playEffect(player.getLocation(), Effect.HEART, 1);
    }),

    ENDER("§8» §5Ender-Schuhe", Color.PURPLE, 5250, player -> {
        for (int i = 0; i < 5; i++) {
            player.getWorld().playEffect(player.getLocation(), Effect.WITCH_MAGIC, 1);
        }
    }),

    FIRE("§8» §4Feuer-Schuhe", Color.ORANGE, 6950, player -> {
        for (int i = 0; i < 3; i++) {
            player.getWorld().playEffect(player.getLocation(), Effect.LAVA_POP, 1);
        }
    }),

    WATER("§8» §9Wasser-Schuhe", Color.BLUE, 5500, player -> {
        for (int i = 0; i < 2; i++) {
            player.getWorld().playEffect(player.getLocation(), Effect.WATERDRIP, 1);
        }
    }),

    CLOUD("§8» §fWolken-Schuhe", Color.WHITE, 7500, player -> {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(EnumParticle.EXPLOSION_NORMAL, true,
                (float) player.getLocation().getX(), (float) player.getLocation().getY(), (float) player.getLocation().getZ(),
                0.25f, 0f, 0.25f, 0, 3);

        for (Player online : Bukkit.getOnlinePlayers()) {
            ((CraftPlayer) online).getHandle().playerConnection.sendPacket(packet);
        }
    });

    @Getter
    private String name;

    @Getter
    private Color color;

    @Getter
    private int cost;

    @Getter
    private ItemStack item;

    @Getter
    private ItemStack itemBought;

    private Consumer<Player> consumer;

    Boots(String name, Color color, int cost, Consumer<Player> consumer) {
        Lobby.getInstance().getBuyItemInventory().register(this);

        this.name = name;
        this.color = color;
        this.cost = cost;
        String costAsString = NumberFormat.getNumberInstance(Locale.GERMAN).format(cost);
        this.item = new ItemBuilder(Material.LEATHER_BOOTS).setColor(color).setDisplayName(name)
                .setLore(Arrays.asList(" ", "§eKosten §8» §7" + costAsString, " ")).build();
        this.itemBought = new ItemBuilder(Material.LEATHER_BOOTS).setColor(color).setDisplayName(name)
                .setLore(Arrays.asList(" ", "§eKosten §8» §7" + costAsString, " ", "§a§lGEKAUFT", " ")).build();
        this.consumer = consumer;
    }

    public void playEffect(Player player) {
        consumer.accept(player);
    }

    @Override
    public String toString() {
        String name = this.name();
        return Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
    }


    @Override
    public void onCancel(Player player) {
        Lobby.getInstance().getBootsInventory().show(player);
    }

    @Override
    public void onBuy(Player player) {
        Lobby.getInstance().getBootsTable().addBoots(player.getUniqueId(), toString());
        player.sendMessage(Messages.PREFIX + "Du hast dir die Schuhe §aerfolgreich §7gekauft!");
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.LEVEL_UP, 1, 1);
    }

    @Override
    public void onNotEnoughCoins(Player player) {
        player.sendMessage(Messages.PREFIX + "Dafür hast du nicht genug §cCoins§7!");
        player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
        player.closeInventory();
    }

    @Override
    public void onAlreadyBought(Player player) {
        player.sendMessage(Messages.PREFIX + "Du §chast§7 diese Schuhe bereits!");
        player.closeInventory();
    }

    @Override
    public void hasBought(Player player, Consumer consumer) {
        Lobby.getInstance().getBootsTable().hasBoots(player.getUniqueId(), toString(), consumer);
    }

}