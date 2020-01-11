package co.circlewave.cirzle.services;

import co.circlewave.cirzle.Game;
import co.circlewave.cirzle.player.Player;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.pay.*;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;
import de.golfgl.gdxgamesvcs.GameServiceException;
import de.golfgl.gdxgamesvcs.IGameServiceClient;
import de.golfgl.gdxgamesvcs.IGameServiceListener;
import de.golfgl.gdxgamesvcs.gamestate.ILoadGameStateResponseListener;
import de.golfgl.gdxgamesvcs.gamestate.ISaveGameStateResponseListener;
import pl.mk5.gdx.fireapp.GdxFIRApp;
import pl.mk5.gdx.fireapp.GdxFIRAuth;
import pl.mk5.gdx.fireapp.GdxFIRCrash;
import pl.mk5.gdx.fireapp.GdxFIRDatabase;
import pl.mk5.gdx.fireapp.auth.GdxFirebaseUser;
import pl.mk5.gdx.fireapp.functional.BiConsumer;
import pl.mk5.gdx.fireapp.functional.Consumer;

import java.util.List;

public class ServicesManager implements PurchaseObserver {

    public final static String PLAY_FUL = "CgkIoafM6Z0PEAIQAg";
    public final static String MASTER = "CgkIoafM6Z0PEAIQAw";

    public final static String CHEAP = "help1";
    public final static String MEDIUM = "help2";
    public final static String EXPENSIVE = "help3";
    public final static int LEVELS_INITIALIZED = 1;
    public final static int LEVELS_INITIALIZING_ERROR = 2;
    public final static int PLAYER_INITIALIZED = 1;
    private final static int NULL = -1;
    private final static int LEVELS_INITIALIZING = 0;
    private final static int PLAYER_INITIALIZING = 0;
    private final String SAVE_PATH = "cirzle_local.json";
    private final String SAVE_ID = "cirzle";
    private final long ONE_DAY_MILLISECONDS = 86400000L;


    private final Game game;
    private final AndroidAPI androidAPI;
    private final IGameServiceClient gameServiceClient;
    private final PurchaseManager purchaseManager;


    private int levelsState;
    private int playerState;

    public ServicesManager(final Game game, final AndroidAPI androidAPI, final IGameServiceClient gameServiceClient,
                           final PurchaseManager purchaseManager) {
        this.game = game;
        this.purchaseManager = purchaseManager;
        this.androidAPI = androidAPI;
        this.gameServiceClient = gameServiceClient;
        this.levelsState = NULL;
        this.playerState = NULL;
    }

    public void initializeLevels() {
        levelsState = LEVELS_INITIALIZING;
        GdxFIRApp.inst().configure();
        GdxFIRCrash.inst().initialize();
        GdxFIRAuth.inst().signInAnonymously().then(new Consumer<GdxFirebaseUser>() {
            @Override
            public void accept(GdxFirebaseUser gdxFirebaseUser) {
                Gdx.app.log("GdxFIRAuth", "signing in anonymously accepted: " + gdxFirebaseUser.getUserInfo().isAnonymous());
                loadLevels();
            }
        }).fail(new BiConsumer<String, Throwable>() {
            @Override
            public void accept(String s, Throwable throwable) {
                Gdx.app.log(s, throwable.getMessage());
                levelsState = LEVELS_INITIALIZING_ERROR;
            }
        });
    }

    public void initializePlayer() {
        playerState = PLAYER_INITIALIZING;
        loadLocalPlayer();
        if (gameServiceClient.resumeSession()) {
            gameServiceClient.setListener(new IGameServiceListener() {
                @Override
                public void gsOnSessionActive() {
                    gameServiceClient.loadGameState(SAVE_ID, new ILoadGameStateResponseListener() {
                        @Override
                        public void gsGameStateLoaded(byte[] gameState) {
                            loadPlayer(gameState);
                        }
                    });
                }

                @Override
                public void gsOnSessionInactive() {
                    playerState = PLAYER_INITIALIZED;
                }

                @Override
                public void gsShowErrorToUser(GsErrorType et, String msg, Throwable t) {
                    Gdx.app.log("Resume session error", et + "  " + msg, t);
                }
            });
        } else {
            playerState = PLAYER_INITIALIZED;
        }
    }

    public void initializePurchaseManager() {
        purchaseManager.install(this, getPurchaseManagerConfig(), true);
    }

    private PurchaseManagerConfig getPurchaseManagerConfig() {
        PurchaseManagerConfig purchaseManagerConfig = new PurchaseManagerConfig();
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("help1"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("help2"));
        purchaseManagerConfig.addOffer(new Offer().setType(OfferType.ENTITLEMENT).setIdentifier("help3"));
        return purchaseManagerConfig;
    }

    public void initializeAdvertisement() {
        androidAPI.initializeAdvertisement(game.getPlayer());
    }

    public void purchases(final String id) {
        purchaseManager.purchase(id);
    }

    public String getPurchasesInformation(final String id) {
        if (purchaseManager.installed()) return purchaseManager.getInformation(id).getLocalPricing();
        else return "Unavailable";
    }

    public void restorePurchase() {
        purchaseManager.purchaseRestore();
    }

    public void setConsent(final boolean state) {
        androidAPI.setConsent(state, game.getPlayer());
    }

    private void loadLevels() {
        FileHandle file = Gdx.files.local("levels/");
        if ((file.list().length <= 125 || (System.currentTimeMillis() - game.getPlayer().getTimeToUpdateLevels()) > (ONE_DAY_MILLISECONDS * 7)) && isInternetAvailable()) {
            GdxFIRDatabase.inst().inReference("/levelsV2").readValue(List.class).then(new Consumer<List<String>>() {
                @Override
                public void accept(final List<String> list) {
                    file.emptyDirectory();
                    for (int name = 1; name <= list.size(); name++) {
                        Gdx.files.local("levels/" + name + ".json")
                                .writeString(new Json().toJson(list.get(name - 1)), false);
                    }
                    game.getPlayer().resetTimeToUpdateLevels();
                    levelsState = LEVELS_INITIALIZED;
                }
            }).silentFail();
        } else if (file.exists() && file.list().length > 0) {
            levelsState = LEVELS_INITIALIZED;
        } else {
            levelsState = LEVELS_INITIALIZING_ERROR;
        }
    }

    public int getLevelsStateCode() {
        return levelsState;
    }

    public int getPlayerStateCode() {
        return playerState;
    }


    private void loadPlayer(final byte[] data) {
        if (data != null && data.length > 0) {
            final Player cloud = loadCloudPlayer(data);
            if (game.getPlayer().getCurrentLevel() < cloud.getCurrentLevel()) {
                game.setPlayer(cloud);
            }
        }
        playerState = PLAYER_INITIALIZED;
    }

    private Player loadCloudPlayer(final byte[] data) {
        return new Json().fromJson(Player.class, Base64Coder.decodeString(new String(data)));
    }

    private void loadLocalPlayer() {
        if (Gdx.files.local(SAVE_PATH).exists() && Gdx.files.local(SAVE_PATH).parent().list().length > 0) {
            game.setPlayer(new Json().fromJson(Player.class, Base64Coder.decodeString(Gdx.files.local(SAVE_PATH).readString())));
        } else {
            game.setPlayer(new Player());
        }
    }

    public void saveLocalPlayer() {
        Gdx.files.local(SAVE_PATH).writeString(game.getPlayer().toJsonString(), false);
    }

    private void savePlayer() {
        if (game.getPlayer() != null) {
            gameServiceClient.saveGameState(SAVE_ID, game.getPlayer().toByte(), 0, new ISaveGameStateResponseListener() {
                @Override
                public void onGameStateSaved(boolean success, String errorCode) {
                    if (success) {
                        Gdx.app.log("save to play games", "Success");
                    } else {
                        Gdx.app.log("save to play games error code", errorCode);
                    }
                    gameServiceClient.pauseSession();
                }
            });
        }
        saveLocalPlayer();
    }

    public boolean isSessionActive() {
        return gameServiceClient.isSessionActive();
    }

    public void pause() {
        savePlayer();
    }

    public void resume() {
        gameServiceClient.resumeSession();
        loadLocalPlayer();
    }

    public String calculateRemainingDaysUntilUpdate() {
        final int second = 1000;
        final int minute = 60 * second;
        final int hour = 60 * minute;
        final int day = 24 * hour;

        long milliSecond = ONE_DAY_MILLISECONDS * 7 - (System.currentTimeMillis() - game.getPlayer().getTimeToUpdateLevels());

        StringBuilder text = new StringBuilder();

        if (milliSecond > day) {
            text.append(milliSecond / day).append("d ");
            milliSecond %= day;
        }

        if (milliSecond > hour) {
            text.append(milliSecond / hour).append("h ");
            milliSecond %= hour;
        }

        text.append(milliSecond / minute).append("m");

        return text.toString();
    }

    public void rate() {
        Gdx.net.openURI("https://play.google.com/store/apps/details?id=co.circlewave.cirzle");
    }

    public void share() {
        androidAPI.share();
    }

    public boolean showAchievements() {
        try {
            gameServiceClient.showAchievements();
            return false;
        } catch (final GameServiceException e) {
            Gdx.app.log("Services Manager Class, Share", e.toString());
            if (!gameServiceClient.isSessionActive()) gameServiceClient.resumeSession();
            return true;
        }
    }

    public void unlockAchievement(final String id) {
        gameServiceClient.unlockAchievement(id);
    }

    public boolean signIn() {
        return gameServiceClient.logIn();
    }

    public void signOut() {
        gameServiceClient.logOff();
    }

    public boolean isInternetAvailable() {
        return androidAPI.isConnected();
    }

    public void showInterstitial() {
        androidAPI.showInterstitial();
        game.getPlayer().resetAdCount();
    }

    @Override
    public void handleInstall() {
        Gdx.app.log("Purchase Manager installed", "" + purchaseManager.installed());
    }

    @Override
    public void handleInstallError(final Throwable e) {
        Gdx.app.log("Purchase Manager", "Error when trying to install purchase manager", e);
    }

    @Override
    public void handleRestore(final Transaction[] transactions) {
        if (transactions != null && transactions.length > 0) {
            for (Transaction transaction : transactions) {
                purchaseManager.purchase(transaction.getIdentifier());
                if (!game.getPlayer().isPremium()) {
                    game.getPlayer().activatePremium();
                }
            }
        }
    }

    @Override
    public void handleRestoreError(final Throwable e) {
        Gdx.app.log("Purchase Manager", "Error when trying to restore purchase", e);
    }

    @Override
    public void handlePurchase(final Transaction transaction) {
        Gdx.app.log("Purchase Manager", "purchases item with id " + transaction.getIdentifier() +
                "\nand price: " + transaction.getPurchaseCost() + " " + transaction.getPurchaseCostCurrency() +
                "\ndone with " + transaction.isPurchased());
        purchaseManager.purchase(transaction.getIdentifier());
        game.getPlayer().activatePremium();
    }

    @Override
    public void handlePurchaseError(final Throwable e) {
        Gdx.app.log("Purchase Manager", "Error on buying", e);
    }

    @Override
    public void handlePurchaseCanceled() {
        Gdx.app.log("Purchase Manager", "buying was canceled!");
    }


    public void dispose() {
        purchaseManager.dispose();
    }
}
