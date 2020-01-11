package co.circlewave.cirzle.gui.screens;

import co.circlewave.cirzle.Game;
import co.circlewave.cirzle.gui.utils.Assets;
import co.circlewave.cirzle.player.Player;
import co.circlewave.cirzle.services.ServicesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;

import static com.badlogic.gdx.Gdx.graphics;

public class LogoScene extends Table {

    private final Game game;
    private final Table logo;

    private boolean finished;

    public LogoScene(final Game game) {
        this.game = game;
        setFillParent(true);

        this.logo = new Table();
        logo.setPosition(getWidth() / 2, getHeight() / 2, Align.center);

        final Label.LabelStyle bigLabelStyle = new Label.LabelStyle();
        bigLabelStyle.font = game.getAssets().getFont(Assets.LIGHT);
        bigLabelStyle.fontColor = Color.WHITE;

        final Label.LabelStyle smallLabelStyle = new Label.LabelStyle();
        smallLabelStyle.font = game.getAssets().getFont(Assets.REGULAR);
        smallLabelStyle.fontColor = Color.WHITE;

        logo.add(new Label("Cirzle", bigLabelStyle)).padBottom(graphics.getWidth() / 10f);
        logo.row();
        logo.add(new Image(game.getAssets().getLogo())).size(graphics.getWidth() / 6f).padBottom(graphics.getWidth() / 32f);
        logo.row();
        logo.add(new Label("Circle Wave", smallLabelStyle));
        logo.row();
        final Label loading = new Label("Loading", smallLabelStyle);
        loading.addAction(Actions.alpha(0.5f));
        logo.add(loading).padTop(graphics.getWidth() / 3f);
        loading.addAction(Actions.forever(Actions.sequence(Actions.alpha(0f, 0.5f), Actions.alpha(0.5f, 0.5f))));


        add(logo);

        this.finished = false;
    }

    @Override
    public void act(final float delta) {
        super.act(delta);
        if (!finished && game.getAssets().finishLoading() && game.getServicesManager().getLevelsStateCode() == ServicesManager.LEVELS_INITIALIZED &&
                game.getServicesManager().getPlayerStateCode() == ServicesManager.PLAYER_INITIALIZED) {
            finished = true;
            initialize();
        } else if (!finished && game.getAssets().finishLoading() && game.getServicesManager().getLevelsStateCode() == ServicesManager.LEVELS_INITIALIZING_ERROR) {
            finished = true;

            logo.row();

            final Label.LabelStyle smallLabelStyle = new Label.LabelStyle();
            smallLabelStyle.font = game.getAssets().getFont(Assets.REGULAR);
            smallLabelStyle.fontColor = Color.WHITE;
            final Label internetRequiredLabel = new Label("Internet connection is required\n\n" +
                    "when opening the game for the first time!", smallLabelStyle);
            internetRequiredLabel.setAlignment(Align.center);
            logo.add(internetRequiredLabel).pad(graphics.getWidth() / 16f).padTop(graphics.getWidth() / 4f);

            logo.row();

            final TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
            textButtonStyle.font = game.getAssets().getFont(Assets.REGULAR);
            textButtonStyle.fontColor = Color.WHITE;
            textButtonStyle.downFontColor = Color.LIGHT_GRAY;
            textButtonStyle.overFontColor = Color.LIGHT_GRAY;
            final TextButton retry = new TextButton("Retry", textButtonStyle) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    batch.end();
                    game.getShapeRenderer().begin();
                    Gdx.gl.glEnable(GL20.GL_BLEND);
                    Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                    if (isPressed() || isOver()) {
                        game.getShapeRenderer().setColor(getStyle().downFontColor.r, getStyle().downFontColor.g, getStyle().downFontColor.b,
                                getStyle().downFontColor.a * parentAlpha);
                    } else {
                        game.getShapeRenderer().setColor(getStyle().fontColor.r, getStyle().fontColor.g, getStyle().fontColor.b,
                                getStyle().fontColor.a * parentAlpha);
                    }
                    game.getShapeRenderer().set(ShapeRenderer.ShapeType.Filled);
                    game.getShapeRenderer().roundRect(getX(), getY(), getWidth(), getHeight(), getParent().getWidth() / 50);
                    game.getShapeRenderer().setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, Color.BLACK.a * parentAlpha);
                    game.getShapeRenderer().roundRect(getX() + getWidth() / 32, getY() + getWidth() / 32,
                            getWidth() - getWidth() / 16, getHeight() - getWidth() / 16, getParent().getWidth() / 50);
                    game.getShapeRenderer().end();
                    Gdx.gl.glDisable(GL20.GL_BLEND);
                    batch.begin();
                    super.draw(batch, parentAlpha);
                }
            };
            retry.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    if (game.getServicesManager().isInternetAvailable() && retry.getActions().isEmpty()) {
                        retry.addAction(Actions.forever(Actions.sequence(Actions.run(() -> {
                            if (game.getServicesManager().getLevelsStateCode() == ServicesManager.LEVELS_INITIALIZING_ERROR) {
                                game.getServicesManager().initializeLevels();
                            }
                            if (game.getServicesManager().getLevelsStateCode() == ServicesManager.LEVELS_INITIALIZED) {
                                initialize();
                            }
                        }))));
                    }
                }
            });
            logo.add(retry).width(graphics.getWidth() / 3f).height(graphics.getHeight() / 16f);
        }
    }

    private void initialize() {
        if (game.getPlayer().isAgreedToPolicy()) {
            fadeOutThanRemove();
        } else {

            final Table policy = new Table();

            final Label.LabelStyle labelStyle = new Label.LabelStyle();
            labelStyle.font = game.getAssets().getFont(Assets.REGULAR);
            labelStyle.fontColor = Color.WHITE;

            final Label policyText = new Label(getAgreementText(), labelStyle);
            policyText.setAlignment(Align.center);
            policy.add(policyText).pad(getWidth() / 16);

            policy.row();

            final Label.LabelStyle linkStyle = new Label.LabelStyle();
            linkStyle.font = game.getAssets().getFont(Assets.REGULAR);
            linkStyle.fontColor = Color.SKY;

            final Label link = new Label("See more information.", linkStyle) {
                @Override
                public void draw(Batch batch, float parentAlpha) {
                    batch.setColor(getStyle().fontColor.r, getStyle().fontColor.g, getStyle().fontColor.b, getStyle().fontColor.a * parentAlpha);
                    batch.draw(game.getAssets().getAtlas().findRegion("pixel"), getX(), getY(),
                            getWidth(), getHeight() / 16f);
                    super.draw(batch, parentAlpha);
                }
            };
            link.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    Gdx.net.openURI("http://circlewave.co/policy.html?i=1");
                }
            });
            link.setAlignment(Align.center);
            policy.add(link);

            policy.row();

            final Image line = new Image(game.getAssets().getAtlas().findRegion("pixel"));
            line.setColor(Color.WHITE);
            policy.add(line).size(policy.getPrefWidth() - policy.getPrefHeight() / 4, policy.getPrefHeight() / 150)
                    .spaceTop(policy.getPrefWidth() / 16);

            policy.row();

            final TextButton.TextButtonStyle buttonStyle = new TextButton.TextButtonStyle();
            buttonStyle.fontColor = Color.WHITE;
            buttonStyle.font = game.getAssets().getFont(Assets.LIGHT);
            buttonStyle.overFontColor = Color.LIGHT_GRAY;
            buttonStyle.downFontColor = Color.LIGHT_GRAY;

            final TextButton continueTextButton = new TextButton("Continue", buttonStyle);
            continueTextButton.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    game.getPlayer().agreeToPolicy();
                    game.getServicesManager().setConsent(true);
                    if (game.getServicesManager().signIn()) game.getPlayer().signIn();
                    fadeOutThanRemove();
                }
            });
            policy.add(continueTextButton).pad(getHeight() / 20);

            final Stack stack = new Stack();
            stack.setBounds(getWidth() / 2 - policy.getPrefWidth() / 2, getHeight() / 2 - policy.getPrefHeight() / 2,
                    policy.getPrefWidth(), policy.getPrefHeight());
            stack.setVisible(false);
            addActor(stack);
            stack.addAction(Actions.sequence(Actions.alpha(0), Actions.run(() -> stack.setVisible(true))));
            final Image background = new Image(game.getAssets().getAtlas().findRegion("pixel"));
            background.setColor(Color.DARK_GRAY);
            stack.add(background);

            stack.add(policy);

            logo.addAction(Actions.sequence(Actions.fadeOut(0.5f), Actions.delay(0.1f), Actions.run(() ->
                    stack.addAction(Actions.fadeIn(0.5f))), Actions.removeActor()));
        }
    }

    private void fadeOutThanRemove() {
        game.setSelectedLevel(game.getPlayer().getCurrentLevel());
        if (game.getPlayer().getSoundStatue() == Player.SOUND) {
            game.getAssets().playSoundEffect();
            game.getAssets().playMusic();
        } else if (game.getPlayer().getSoundStatue() == Player.EFFECT) {
            game.getAssets().playSoundEffect();
            game.getAssets().pauseMusic();
        } else if (game.getPlayer().getSoundStatue() == Player.MUSIC) {
            game.getAssets().pauseSoundEffect();
            game.getAssets().playMusic();
        } else if (game.getPlayer().getSoundStatue() == Player.MUTE) {
            game.getAssets().pauseMusic();
            game.getAssets().pauseSoundEffect();
        }
        addAction(Actions.sequence(Actions.fadeOut(0.5f),
                Actions.run(() -> {
                    final Group playScene = new PlayScene(game);
                    playScene.addAction(Actions.sequence(Actions.fadeIn(0.5f)));
                    game.addActorToScene(playScene);
                }), Actions.removeActor()));
    }

    private String getAgreementText() {
        return "by clicking the CONTINUE button under, i\n\n" +
                "conform that i have read and agreed with\n\n" +
                "the Privacy Policy and state that my age\n\n" +
                "is older than 16, furthermore i hereby\n\n" +
                "consent that CircleWave can process/use\n\n" +
                "my personal data to personalize and\n\n" +
                "improve the game and serving targeted\n\n" +
                "advertisements in the game through\n\n" +
                "advertising networks and their partners.";
    }
}
