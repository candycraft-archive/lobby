package de.pauhull.lobby.shop;

import de.pauhull.coins.spigot.buyable.SpigotBuyable;
import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import de.pauhull.lobby.entity.EntityBalloon;
import de.pauhull.lobby.entity.EntityBalloonBlock;
import de.pauhull.lobby.entity.EntityBalloonSkull;
import de.pauhull.lobby.util.ItemBuilder;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public enum Balloon implements SpigotBuyable {

    WHITE_BALLOON("white", "§8» §fWeißer Ballon", 2500, Material.WOOL, (byte) 0),
    ORANGE_BALLOON("orange", "§8» §6Oranger Ballon", 2500, Material.WOOL, (byte) 1),
    MAGENTA_BALLOON("magenta", "§8» §cMagenta Ballon", 2500, Material.WOOL, (byte) 2),
    LIGHT_BLUE_BALLOON("lightblue", "§8» §bHellblauer Ballon", 2500, Material.WOOL, (byte) 3),
    YELLOW_BALLOON("yellow", "§8» §eGelber Ballon", 2500, Material.WOOL, (byte) 4),
    LIGHT_GREEN_BALLOON("lightgreen", "§8» §aHellgrüner Ballon", 2500, Material.WOOL, (byte) 5),
    PINK_BALLOON("pink", "§8» §dPinker Ballon", 2500, Material.WOOL, (byte) 6),
    GRAY_BALLOON("gray", "§8» §8Grauer Ballon", 2500, Material.WOOL, (byte) 7),
    LIGHT_GRAY_BALLOON("lightgray", "§8» §7Hellgrauer Ballon", 2500, Material.WOOL, (byte) 8),
    CYAN_BALLOON("cyan", "§8» §3Türkiser Ballon", 2500, Material.WOOL, (byte) 9),
    PURPLE_BALLOON("purple", "§8» §5Lila Ballon", 2500, Material.WOOL, (byte) 10),
    BLUE_BALLOON("blue", "§8» §9Blauer Ballon", 2500, Material.WOOL, (byte) 11),
    BROWN_BALLOON("brown", "§8» §8Brauner Ballon", 2500, Material.WOOL, (byte) 12),
    DARK_GREEN_BALLOON("green", "§8» §2Grüner Ballon", 2500, Material.WOOL, (byte) 13),
    RED_BALLOON("red", "§8» §4Roter Ballon", 2500, Material.WOOL, (byte) 14),
    BLACK_BALLOON("black", "§8» §8Schwarzer Ballon", 2500, Material.WOOL, (byte) 15),
    SANTA("santa", "§8» §eWeihnachtsmann", 7500, "Santa"),
    BREAD("bread", "§8» §eBrot", 4000, "_Grime"),
    DOG("dog", "§8» §eDoge", 7500, "Doggy"),
    CHEST("chest", "§8» §eGoldkiste", 6500, "Tom25W"),
    ASTRONAUT("astronaut", "§8» §eAstronaut", 7000, "YoMamasMC"),
    CREEPER("creeper", "§8» §eCreeper", 8000, "Mrman"),
    DUCK("duck", "§8» §eEnte", 4500, "Hacking"),
    PAUHULL("pauhull", "§8» §epauhull", 0, "pauhull"),
    UGANDAN_KNUCKLE("uganda", "§8» §eUgandan Knuckle", 0, "deadmeme_"),
    FANCY_CUBE("cube", "§8» §eFancy Cube", 0, "Pesse_"),
    BETA("beta", "§8» §6Beta Geschenk", -1, "StackedGold"),
    STYLEX("stylex", "§8» §6leStylex", -1, "leStylex");

    @Getter
    private boolean premium = false;

    @Getter
    private boolean special = false;

    @Getter
    private String id;

    @Getter
    private Material material = null;

    @Getter
    private byte data = 0;

    @Getter
    private String name;

    @Getter
    private String owner = null;

    @Getter
    private int cost;

    Balloon(String id, String name, int cost) {
        Lobby.getInstance().getBuyItemInventory().register(this);

        this.id = id;
        this.name = name;
        this.cost = cost;

        if (cost == 0) {
            premium = true;
        } else if (cost == -1) {
            special = true;
        }
    }

    Balloon(String id, String name, int cost, Material material, byte data) {
        this(id, name, cost);
        this.material = material;
        this.data = data;
    }

    Balloon(String id, String name, int cost, String owner) {
        this(id, name, cost);
        this.owner = owner;
    }

    @Override
    public ItemStack getItem() {
        if (owner == null) {
            if (premium) {
                return new ItemBuilder(material, 1, data).setDisplayName(name)
                        .setLore(Arrays.asList(" ", "§e§lLEBKUCHEN FEATURE", " ")).build();
            } else if (special) {
                return new ItemBuilder(material, 1, data).setDisplayName(name)
                        .setLore(Arrays.asList(" ", "§6§lSPEZIALITEM", " ")).build();
            } else {
                String number = NumberFormat.getInstance(Locale.GERMAN).format(cost);
                return new ItemBuilder(material, 1, data).setDisplayName(name)
                        .setLore(Arrays.asList(" ", "§eKosten §8» §7" + number, " ")).build();
            }
        } else {
            ItemStack stack = Lobby.getInstance().getHeadCache().getHead(owner);
            ItemMeta meta = stack.getItemMeta();
            meta.setDisplayName(name);
            if (premium) {
                meta.setLore(Arrays.asList(" ", "§e§lLEBKUCHEN FEATURE", " "));
            } else if (special) {
                meta.setLore(Arrays.asList(" ", "§6§lSPEZIALITEM", " "));
            } else {
                String number = NumberFormat.getInstance(Locale.GERMAN).format(cost);
                meta.setLore(Arrays.asList(" ", "§eKosten §8» §7" + number, " "));
            }
            stack.setItemMeta(meta);
            return stack;
        }
    }

    @Override
    public ItemStack getItemBought() {
        if (premium || special) {
            return getItem();
        }

        ItemStack stack = getItem();
        ItemMeta meta = stack.getItemMeta();
        List<String> lore = meta.getLore();
        lore.add("§a§lGEKAUFT");
        lore.add(" ");
        meta.setLore(lore);
        stack.setItemMeta(meta);
        return stack;
    }

    @Override
    public void onCancel(Player player) {
        Lobby.getInstance().getBalloonsInventory().show(player);
    }

    @Override
    public void onBuy(Player player) {
        Lobby.getInstance().getBalloonTable().addBalloon(player.getUniqueId(), toString());
        player.sendMessage(Messages.PREFIX + "Du hast dir diesen Ballon §aerfolgreich §7gekauft!");
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
        player.sendMessage(Messages.PREFIX + "Du §chast§7 diesen Ballon bereits!");
        player.closeInventory();
    }

    @Override
    public void hasBought(Player player, Consumer<Boolean> consumer) {
        if (player.hasPermission("lobby.balloon." + name().toLowerCase())) {
            consumer.accept(true);
            return;
        }

        Lobby.getInstance().getBalloonTable().hasBalloon(player.getUniqueId(), toString(), consumer);
    }

    public EntityBalloon getEntity(Player player) {
        if (owner == null) {
            return new EntityBalloonBlock(this, player.getLocation().clone().add(0, 2, 0), player, material, data, name);
        } else {
            return new EntityBalloonSkull(this, player.getLocation().clone().add(0, 2, 0), player, owner, name);
        }
    }

    @Override
    public String toString() {
        return id;
    }

}