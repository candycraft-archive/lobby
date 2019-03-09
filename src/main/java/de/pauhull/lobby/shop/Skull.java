package de.pauhull.lobby.shop;

import de.pauhull.coins.spigot.buyable.SpigotBuyable;
import de.pauhull.lobby.Lobby;
import de.pauhull.lobby.Messages;
import lombok.Getter;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.function.Consumer;

public class Skull implements SpigotBuyable {

    public static Skull GODTITAN = new Skull("GodTitan", true, 0);
    public static Skull DRAGONFIGHTER = new Skull("Dr4gonFighter", true, 0);
    public static Skull CODEEXCEPTION = new Skull("CodeException", true, 0);
    public static Skull PAUHULL = new Skull("pauhull", true, 0);
    public static Skull BASTIGHG = new Skull("leStylex", false, 1500);
    public static Skull NORISKK = new Skull("LOGO", false, 1000);
    public static Skull MINIMICHECKER = new Skull("DerNiccl", false, 1250);
    public static Skull BYQUADRIX = new Skull("LeKoopa", false, 1100);

    @Getter
    private static List<Skull> skulls = new ArrayList<>();

    static {
        skulls.add(GODTITAN);
        skulls.add(DRAGONFIGHTER);
        skulls.add(CODEEXCEPTION);
        skulls.add(PAUHULL);
        skulls.add(BASTIGHG);
        skulls.add(NORISKK);
        skulls.add(MINIMICHECKER);
        skulls.add(BYQUADRIX);
    }

    @Getter
    private boolean premium;

    @Getter
    private int cost;

    @Getter
    private String owner;

    @Getter
    private String displayName;

    public Skull(String owner, boolean premium, int cost, String displayName) {
        Lobby.getInstance().getBuyItemInventory().register(this);

        this.owner = owner;
        this.premium = premium;
        this.cost = cost;
        this.displayName = displayName;
    }

    public Skull(String owner, boolean premium, int cost) {
        this(owner, premium, cost, "§8» §e" + owner);
    }

    public ItemStack getItem() {
        if (premium) {
            ItemStack item = Lobby.getInstance().getHeadCache().getHead(owner);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(displayName);
            meta.setLore(Arrays.asList(" ", "§b§lUNICORN FEATURE", " "));
            item.setItemMeta(meta);
            return item;
        } else {
            if (cost == 0) {
                ItemStack item = Lobby.getInstance().getHeadCache().getHead(owner);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(displayName);
                meta.setLore(Arrays.asList(" ", "§a§lKOSTENLOS", " "));
                item.setItemMeta(meta);
                return item;
            } else {
                ItemStack item = Lobby.getInstance().getHeadCache().getHead(owner);
                ItemMeta meta = item.getItemMeta();
                meta.setDisplayName(displayName);
                meta.setLore(Arrays.asList(" ", "§eKosten §8» §7" + NumberFormat.getInstance(Locale.GERMAN).format(cost), " "));
                item.setItemMeta(meta);
                return item;
            }
        }
    }

    public ItemStack getItemBought() {
        if (premium) {
            return getItem();
        } else {
            if (cost == 0) {
                return getItem();
            } else {
                ItemStack itemBought = getItem();
                ItemMeta boughtMeta = itemBought.getItemMeta();
                boughtMeta.setLore(Arrays.asList(" ", "§eKosten §8» §7" + NumberFormat.getInstance(Locale.GERMAN).format(cost), " ", "§a§lGEKAUFT", " "));
                itemBought.setItemMeta(boughtMeta);
                return itemBought;
            }
        }
    }

    @Override
    public void onCancel(Player player) {
        Lobby.getInstance().getSkullInventory().show(player);
    }

    @Override
    public void onBuy(Player player) {
        Lobby.getInstance().getSkullsTable().addSkull(player.getUniqueId(), toString());
        player.sendMessage(Messages.PREFIX + "Du hast dir diesen Kopf §aerfolgreich §7gekauft!");
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
        player.sendMessage(Messages.PREFIX + "Du §chast§7 diesen Kopf bereits!");
        player.closeInventory();
    }

    @Override
    public void hasBought(Player player, Consumer<Boolean> consumer) {
        if (player.hasPermission("lobby.skull." + owner.toLowerCase())) {
            consumer.accept(true);
            return;
        }

        Lobby.getInstance().getSkullsTable().hasSkull(player.getUniqueId(), toString(), consumer);
    }

    @Override
    public String getName() {
        return displayName;
    }

    @Override
    public String toString() {
        return owner;
    }

}