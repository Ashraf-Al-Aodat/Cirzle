package co.circlewave.cirzle;

import co.circlewave.cirzle.gui.utils.Assets;
import co.circlewave.cirzle.gui.utils.Scene;
import co.circlewave.cirzle.gui.utils.ShapeRenderer;
import co.circlewave.cirzle.player.Player;
import co.circlewave.cirzle.services.AndroidAPI;
import co.circlewave.cirzle.services.ServicesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.pay.PurchaseManager;
import com.badlogic.gdx.scenes.scene2d.Actor;
import de.golfgl.gdxgamesvcs.IGameServiceClient;

public class Game extends com.badlogic.gdx.Game {

    private final Assets assets;
    private final ServicesManager servicesManager;

    private Scene scene;

    private Player player;

    private int selectedLevel;

    public Game(final AndroidAPI androidAPI, final IGameServiceClient gameServiceClient, final PurchaseManager purchaseManager) {
        this.assets = new Assets();
        this.servicesManager = new ServicesManager(this, androidAPI, gameServiceClient, purchaseManager);
        this.selectedLevel = 1;
    }

    public static void print(final String string) {
        Gdx.app.log("Testing ", string);

    }

    @Override
    public void create() {
        assets.load();
        servicesManager.initializeLevels();
        servicesManager.initializePlayer();
        servicesManager.initializePurchaseManager();
        servicesManager.initializeAdvertisement();
        scene = new Scene(this);
    }

    @Override
    public void render() {
        assets.update();
        scene.render(Gdx.graphics.getDeltaTime());
    }

    @Override
    public void resize(final int width, final int height) {
        scene.resize(width, height);
    }

    @Override
    public void pause() {
        scene.pause();
        servicesManager.pause();
    }

    @Override
    public void resume() {
        scene.resume();
        if (scene != null) scene.resume();
        if (!servicesManager.isSessionActive()) getServicesManager().resume();
    }

    @Override
    public void dispose() {
        assets.dispose();
        scene.dispose();
        servicesManager.dispose();
    }

    public Assets getAssets() {
        return assets;
    }

    public void addActorToScene(final Actor actor) {
        scene.addActor(actor);
    }

    public ShapeRenderer getShapeRenderer() {
        return scene.getShapeRenderer();
    }

    public Batch getBatch() {
        return scene.getBatch();
    }

    public void startBackgroundAnimation() {
        scene.startAnimation();
    }

    public void setGradientColors(final Color[] colors, final float duration) {
        scene.setGradientColors(colors[0], colors[1], duration);
    }

    public ServicesManager getServicesManager() {
        return servicesManager;
    }

    public int getLevelsNumber() {
        return Gdx.files.local("levels").list().length;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(final Player player) {
        this.player = player;
    }

    public int getSelectedLevel() {
        return selectedLevel;
    }

    public void setSelectedLevel(final int selectedLevel) {
        this.selectedLevel = selectedLevel;
    }
}
