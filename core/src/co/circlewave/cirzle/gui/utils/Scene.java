package co.circlewave.cirzle.gui.utils;

import co.circlewave.cirzle.Game;
import co.circlewave.cirzle.gui.screens.LogoScene;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Scene implements Screen {

    private final Game game;
    private final Stage stage;
    private final ShapeRenderer shapeRenderer;
    private final Background background;

    public Scene(final Game game) {
        this.game = game;
        this.stage = new Stage();
        this.shapeRenderer = new ShapeRenderer(stage.getViewport().getCamera().combined);
        this.background = new Background(shapeRenderer, stage.getRoot().getX(), stage.getRoot().getY(), stage.getWidth(),
                stage.getHeight(), Color.BLACK, Color.BLACK);
        game.setScreen(this);
    }

    @Override
    public void show() {
        Gdx.input.setInputProcessor(stage);
        Gdx.input.setCatchKey(Input.Buttons.BACK, true);
        stage.addActor(background);
        stage.addActor(new LogoScene(game));
    }

    @Override
    public void render(final float delta) {
        Gdx.gl.glClearColor(0, 0, 0, 1);
        Gdx.gl.glClear(GL30.GL_COLOR_BUFFER_BIT | GL30.GL_DEPTH_BUFFER_BIT);
        stage.draw();
        stage.act(Gdx.graphics.getDeltaTime());
        if (Gdx.input.isKeyJustPressed(Input.Keys.BACK)) {
            Gdx.app.exit();
        }
    }

    public void addActor(final Actor actor) {
        stage.addActor(actor);
    }

    public void startAnimation() {
        background.startAnimation();
    }

    public void setGradientColors(final Color top, final Color bottom, final float duration) {
        background.addAction(Actions.sequence(Actions.fadeOut(duration * 2),
                Actions.run(() -> background.setColors(top, bottom)), Actions.fadeIn(duration * 2)));

    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public Batch getBatch() {
        return stage.getBatch();
    }

    @Override
    public void resize(final int width, final int height) {
        stage.getViewport().update(width, height);
    }

    @Override
    public void pause() {
    }

    @Override
    public void resume() {
    }

    @Override
    public void hide() {
    }

    @Override
    public void dispose() {
        stage.dispose();
        shapeRenderer.dispose();
    }
}
