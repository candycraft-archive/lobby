package de.godtitan.lobby;

import de.godtitan.lobby.command.*;
import de.godtitan.lobby.data.MySQL;
import de.godtitan.lobby.data.table.*;
import de.godtitan.lobby.display.LobbyScoreboard;
import de.godtitan.lobby.entity.EntityBalloon;
import de.godtitan.lobby.inventory.*;
import de.godtitan.lobby.listener.*;
import de.godtitan.lobby.manager.BalloonManager;
import de.godtitan.lobby.manager.LobbyLocationManager;
import de.godtitan.lobby.util.ActionBar;
import de.godtitan.lobby.util.HeadCache;
import de.godtitan.lobby.util.LobbyItems;
import de.pauhull.coins.spigot.inventory.BuyItemInventory;
import de.pauhull.scoreboard.NovusScoreboardManager;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class Lobby extends JavaPlugin {

    /* INSTANCE */
    @Getter
    private static Lobby instance;

    /* DATA */
    @Getter
    private ExecutorService executorService;
    @Getter
    private ScheduledExecutorService scheduledExecutorService;
    @Getter
    private FileConfiguration config;
    @Getter
    private MySQL mySQL;

    /* MISC */
    @Getter
    private LobbyLocationManager locationManager;
    @Getter
    private LobbyItems lobbyItems;
    @Getter
    private HeadCache headCache;
    @Getter
    private BalloonManager balloonManager;
    @Getter
    private NovusScoreboardManager scoreboardManager;

    /* INVENTORIES */
    @Getter
    private BootsInventory bootsInventory;
    @Getter
    private BuyItemInventory buyItemInventory;
    @Getter
    private ProfileInventory profileInventory;
    @Getter
    private SkullInventory skullInventory;
    @Getter
    private JumpAndRunInventory jumpAndRunInventory;
    @Getter
    private LobbySwitcherInventory lobbySwitcherInventory;
    @Getter
    private NavigatorInventory navigatorInventory;
    @Getter
    private PlayerHiderInventory playerHiderInventory;
    @Getter
    private PrivacyPolicyInventory privacyPolicyInventory;
    @Getter
    private ShopInventory shopInventory;
    @Getter
    private NickInventory nickInventory;
    @Getter
    private BalloonsInventory balloonsInventory;
    @Getter
    private LoadingInventory loadingInventory;

    /* TABLES */
    @Getter
    private BetaTesterTable betaTesterTable;
    @Getter
    private BootsTable bootsTable;
    @Getter
    private PrivacyPolicyTable privacyPolicyTable;
    @Getter
    private PlayerHiderTable playerHiderTable;
    @Getter
    private SkullsTable skullsTable;
    @Getter
    private BalloonTable balloonTable;

    @Setter
    @Getter
    private boolean weather = false;
    private List<String> messages = Arrays.asList(
            "§8» §6§lNEU: §a§lBuildFFA §8§l● §a§lrelease §8«",
            "§8» §7Du benötigst Hilfe? §6§l/support§7! §8«",
            "§8» §6§lTS³: §b/teamspeak §8«",
            "§8» §6§lNEU: §a§l/forum §8«",
            "§8» §7Du hast einen Hacker gefunden? §a§l/report <Spieler>§7! §8«"
    );
    private int messageTime = 10 * 20; // 10 seconds
    private int message = 0;
    private long ticks = 0;

    @Override
    public void onDisable() {
        this.mySQL.close();
        this.executorService.shutdown();
        this.scheduledExecutorService.shutdown();
    }

    public void onEnable() {
        /* INSTANCE */
        instance = this;

        /* DATA */
        this.executorService = Executors.newSingleThreadExecutor();
        this.scheduledExecutorService = Executors.newSingleThreadScheduledExecutor();
        this.config = copyAndLoad("config.yml", new File(getDataFolder(), "config.yml"));
        this.mySQL = new MySQL(config.getString("MySQL.Host"),
                config.getString("MySQL.Port"),
                config.getString("MySQL.Database"),
                config.getString("MySQL.User"),
                config.getString("MySQL.Password"),
                config.getBoolean("MySQL.SSL"));

        if (!this.mySQL.connect()) {
            return;
        }

        /* MISC */
        this.balloonManager = new BalloonManager();
        this.locationManager = new LobbyLocationManager(this);
        this.scoreboardManager = new NovusScoreboardManager(this, LobbyScoreboard.class);
        this.lobbyItems = new LobbyItems();
        this.headCache = new HeadCache();

        /* INVENTORIES */
        this.bootsInventory = new BootsInventory(this);
        this.buyItemInventory = new BuyItemInventory();
        this.profileInventory = new ProfileInventory(this);
        this.skullInventory = new SkullInventory(this);
        this.jumpAndRunInventory = new JumpAndRunInventory(this);
        this.lobbySwitcherInventory = new LobbySwitcherInventory(this);
        this.navigatorInventory = new NavigatorInventory(this);
        this.playerHiderInventory = new PlayerHiderInventory(this);
        this.privacyPolicyInventory = new PrivacyPolicyInventory(this);
        this.shopInventory = new ShopInventory(this);
        this.nickInventory = new NickInventory(this);
        this.loadingInventory = new LoadingInventory(this);
        this.balloonsInventory = new BalloonsInventory(this);

        /* MYSQL TABLES */
        this.betaTesterTable = new BetaTesterTable(mySQL, executorService);
        this.skullsTable = new SkullsTable(mySQL, executorService);
        this.bootsTable = new BootsTable(mySQL, executorService);
        this.playerHiderTable = new PlayerHiderTable(mySQL, executorService);
        this.privacyPolicyTable = new PrivacyPolicyTable(mySQL, executorService);
        this.balloonTable = new BalloonTable(mySQL, executorService);

        /* COMMANDS */
        new BuildCommand(this);
        new EventServerCommand(this);
        new WeatherCommand(this);
        new SetLocationCommand(this);
        new SpawnCommand(this);
        new TextCommand(this);
        new SpawnCommunityCommand(this);

        /* EVENTS */
        new CancelledEventListener(this);
        new JumpAndRunListener(this);
        new PlayerInteractAtEntityListener(this);
        new PlayerJoinListener(this);
        new PlayerTeleportListener(this);
        new PlayerMoveListener(this);
        new PlayerLeavePartyListener(this);
        new PlayerQuitListener(this);
        new PlayerJoinPartyListener(this);
        new ServerListPingListener(this);
        new SignClickListener(this);

        /* ACTION BAR */
        this.sendActionBar();

        /* BUNGEECORD CHANNEL REGISTER */
        SpawnCommunityCommand.updateCrystals(this);

        EntityBalloon.schedule();
        balloonManager.removeAllUnactiveBalloons();

        for (Player player : Bukkit.getOnlinePlayers()) {
            player.setAllowFlight(true);
        }
    }

    public void teleportToSpawn(Player player) {
        if (BuildCommand.getBuilding().contains(player.getName())) {
            BuildCommand.getBuilding().remove(player.getName());
            player.sendMessage(Messages.PREFIX + "§7Du hast den Buildmodus §cverlassen§7!");
        }

        player.setFoodLevel(20);
        player.setHealth(20);
        player.setGameMode(GameMode.ADVENTURE);

        player.setExp(0);
        player.setLevel(0);

        if (player.hasPermission("lobby.team")) {
            lobbyItems.giveTeamItems(player);
        } else {
            lobbyItems.giveItems(player);
        }

        locationManager.teleport(player, "Spawn");

        Bukkit.getScheduler().scheduleSyncDelayedTask(this, () -> {
            player.setAllowFlight(true);
        }, 10);
    }

    private void sendActionBar() {
        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, () -> {
            if (++ticks % messageTime == 0)
                message++;

            if (ticks % 20 == 0) { // every second
                for (Player player : Bukkit.getOnlinePlayers()) {
                    ActionBar.sendActionBar(player, messages.get(message % messages.size()));
                }
            }

            if (ticks % 20 == 0) { // update every xp bar segment
                for (Player player : Bukkit.getOnlinePlayers()) {
                    if (JumpAndRunListener.getInstance().getJumpAndRuns().containsKey(player.getName())) {
                        continue;
                    }

                    player.setExp(1f - (ticks % messageTime) / (float) messageTime);
                }
            }
        }, 0, 1);
    }

    private FileConfiguration copyAndLoad(String resource, File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();

            try {
                Files.copy(getResource(resource), file.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return YamlConfiguration.loadConfiguration(file);
    }

} 