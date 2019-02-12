package de.godtitan.lobby.shop;

import de.godtitan.lobby.Lobby;
import de.godtitan.lobby.Messages;
import de.godtitan.lobby.util.ItemBuilder;
import de.pauhull.coins.spigot.buyable.SpigotBuyable;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.text.NumberFormat;
import java.util.Arrays;
import java.util.Locale;
import java.util.function.Consumer;

public class BetaPass implements SpigotBuyable {

    public static BetaPass BETA_PASS = new BetaPass();

    @Getter
    private int cost;

    @Getter
    private String name;

    @Getter
    private ItemStack item;

    @Getter
    private ItemStack itemBought;

    private BetaPass() {
        Lobby.getInstance().getBuyItemInventory().register(this);

        this.cost = 10000;
        this.name = "§8» §c§lBeta-Pass";
        this.item = new ItemBuilder(Material.NAME_TAG).setDisplayName(name).setLore(Arrays.asList(" ", "§eKosten §8» §7" + NumberFormat.getInstance(Locale.GERMAN).format(cost), " ")).build();
        this.itemBought = item;
    }


    @Override
    public void onCancel(Player player) {
        player.closeInventory();
        player.playSound(player.getLocation(), Sound.CLICK, 1, 1);
    }

    @Override
    public void onBuy(Player player) {
        player.sendMessage(Messages.PREFIX + "Du bist nun ein §aBeta-Tester§7!");
        Lobby.getInstance().getBetaTesterTable().setBetaTester(player.getUniqueId(), true);
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
        player.sendMessage(Messages.PREFIX + "Du bist bereits ein §cBeta-Tester§7!");
        player.playSound(player.getLocation(), Sound.BAT_DEATH, 1, 1);
        player.closeInventory();
    }

    @Override
    public void hasBought(Player player, Consumer consumer) {
        Lobby.getInstance().getBetaTesterTable().isBetaTester(player.getUniqueId(), consumer);
    }

}
