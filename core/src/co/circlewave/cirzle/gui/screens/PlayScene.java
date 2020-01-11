package co.circlewave.cirzle.gui.screens;

import co.circlewave.cirzle.Game;
import co.circlewave.cirzle.board.Board;
import co.circlewave.cirzle.board.Tile;
import co.circlewave.cirzle.board.component.Piece;
import co.circlewave.cirzle.gui.utils.Assets;
import co.circlewave.cirzle.gui.utils.Foreground;
import co.circlewave.cirzle.gui.utils.Quote;
import co.circlewave.cirzle.player.Player;
import co.circlewave.cirzle.services.ServicesManager;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.PixmapIO;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.*;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.BufferUtils;
import com.badlogic.gdx.utils.ScreenUtils;

public class PlayScene extends Group {

    private final Game game;
    private final Foreground foreground;
    private final ClickListener optionsListener;
    private final ClickListener winListener;
    private final Label skillNumber;
    private final Button option;
    private final Button share;
    private final Button restart;
    private final Button swap;
    //private final Button undo;
    private final Quote quote;
    private final Stack swapView;
    private final Table optionTable;
    private final Table levelSelectionTable;

    private final float duration;

    private Board board;
    private Tile sourceTile;

    PlayScene(final Game game) {
        this.game = game;
        duration = 0.1f;
        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        game.startBackgroundAnimation();


        this.quote = new Quote(game.getServicesManager(), game.getAssets().getFont(Assets.REGULAR), 5, 150);
        quote.setPosition(getWidth() / 2, getHeight() - getHeight() / 8);
        quote.setOrigin(Align.center);
        addActor(quote);


        this.board = new Board("board", game.getSelectedLevel(), getWidth(), getHeight(), game.getAssets().getAtlas(),
                game.getShapeRenderer(), game.getBatch());
        game.setGradientColors(board.getFrequentColors(), 0);

        this.optionsListener = new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                hideOptions();
            }
        };

        this.winListener = new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                openNextLevel();
            }
        };

        this.foreground = new Foreground(game.getShapeRenderer());
        foreground.setColor(Color.BLACK);
        foreground.setVisible(false);
        foreground.addActionToShadow(Actions.alpha(0));

        this.option = new Button(game.getAssets().getSkin(), "option");
        option.setBounds(getWidth() / 2 - ((getWidth() / 8) / 2), getWidth() / 16,
                getWidth() / 8, getWidth() / 8);
        option.addListener(new ActorGestureListener() {

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                option.setColor(Color.LIGHT_GRAY);
                game.getAssets().playClickSoundEffect();
                super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                option.setColor(Color.WHITE);
                showOptions();
            }
        });

        this.share = new Button(game.getAssets().getSkin(), "screenshot");
        share.setBounds(getWidth() / 2 - ((getWidth() / 8) / 2), getWidth() / 16,
                getWidth() / 8, getWidth() / 8);
        share.setVisible(false);
        share.addAction(Actions.alpha(0));
        share.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getAssets().playPieceCameraSoundEffect();
                takeScreenshot();
            }
        });
        this.optionTable = optionsTable();

        this.swap = new Button(game.getAssets().getSkin(), "swap");
        swap.addListener(new ClickListener() {

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getAssets().playClickSoundEffect();
                if (game.getPlayer().isPremium() || game.getPlayer().checkAdCount()) {
                    board.swap();
                } else {
                    if (game.getServicesManager().isInternetAvailable()) {
                        game.getServicesManager().showInterstitial();
                        game.getPlayer().checkAdCount();
                        board.swap();
                    } else {
                        swap.toggle();
                        showInternetBlock();
                    }
                }
            }
        });


        this.restart = new Button(game.getAssets().getSkin(), "restart");
        restart.setBounds(getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16,
                getWidth() / 8, getWidth() / 8);
        restart.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                game.getAssets().playClickSoundEffect();
                restart.setColor(Color.GRAY);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                restart.setColor(Color.WHITE);
                restart();
            }

        });

       /* this.undo = new Button(game.getAssets().getSkin(), "undo");
        undo.setBounds(getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16,
                getWidth() / 8 , getWidth() / 8);
        undo.addListener(new ClickListener(){

            @Override
            public boolean touchDown(InputEvent event, float x, float y, int pointer, int button) {
                undo.setColor(Color.LIGHT_GRAY);
                return super.touchDown(event, x, y, pointer, button);
            }

            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if(game.getPlayer().getKeys() > 0 && board.getMoves() > 0) {
                    board.undo(game.getPlayer(), PlayScene.this);
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                undo.setColor(Color.WHITE);
            }
        });*/


        final Label.LabelStyle levelNumberStyle = new Label.LabelStyle();
        levelNumberStyle.fontColor = Color.WHITE;
        levelNumberStyle.font = game.getAssets().getFont(Assets.BOLD);

        final Label levelNumber = new Label(new StringBuilder().append(game.getSelectedLevel()), levelNumberStyle);

        final ImageButton previous = new ImageButton(game.getAssets().getSkin(), "arrow") {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                super.draw(batch, parentAlpha);
            }
        };
        previous.setTransform(true);
        previous.setSize(getWidth() / 10, getWidth() / 10);
        previous.setOrigin(Align.center);
        previous.setRotation(180);
        previous.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                game.getAssets().playClickSoundEffect();
                previous.setColor(Color.GRAY);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                previous.setColor(Color.WHITE);
                if (!((ImageButton) levelSelectionTable.getChildren().get(0)).isDisabled() &&
                        !((ImageButton) levelSelectionTable.getChildren().get(2)).isDisabled()) {
                    previousBoard();
                }
            }
        });

        final ImageButton next = new ImageButton(game.getAssets().getSkin(), "arrow");
        next.addListener(new ActorGestureListener() {
            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                super.touchDown(event, x, y, pointer, button);
                game.getAssets().playClickSoundEffect();
                next.setColor(Color.GRAY);
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                super.touchUp(event, x, y, pointer, button);
                next.setColor(Color.WHITE);
                if (!((ImageButton) levelSelectionTable.getChildren().get(0)).isDisabled() &&
                        !((ImageButton) levelSelectionTable.getChildren().get(2)).isDisabled()) {
                    nextBoard();
                }
            }
        });

        levelSelectionTable = new Table();

        levelSelectionTable.add(previous).size(getWidth() / 10).spaceRight(getWidth() / 32);
        levelSelectionTable.add(levelNumber);
        levelSelectionTable.add(next).size(getWidth() / 10).spaceLeft(getWidth() / 32);
        levelSelectionTable.setPosition(getWidth() / 2, getHeight() - getHeight() / 12, Align.center);
        levelSelectionTable.setVisible(false);
        levelSelectionTable.addAction(Actions.alpha(0));
        addActor(levelSelectionTable);
        final Label.LabelStyle skillNumberStyle = new Label.LabelStyle();
        skillNumberStyle.fontColor = Color.WHITE;
        skillNumberStyle.font = game.getAssets().getFont(Assets.REGULAR);

        this.skillNumber = new Label("", skillNumberStyle) {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                return null;
            }
        };

        swapView = new Stack();
        swapView.setBounds(getWidth() - getWidth() / 6 / 2 - getWidth() / 8 / 2,
                getWidth() / 16, getWidth() / 8, getWidth() / 8);
        swapView.add(swap);

        /*if(!game.getPlayer().isPremium()) {
            skillNumber.setText("" + game.getPlayer().getKeys());
            skillNumber.setSize(swap.getWidth(), swap.getWidth());
            skillNumber.setAlignment(Align.center);
            swapView.add(skillNumber);
        }*/


        addActor(swapView);
        addActor(board);
        addActor(optionTable);
        addActor(option);
        addActor(share);
        addActor(restart);
        addActor(foreground);
        //addActor(undo);

        playTutorial();

        addListener(new ActorGestureListener() {

            private float startX;
            private float startY;
            private boolean fistTime;
            private int pointer;
            private boolean direction;

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                this.pointer = pointer;
                if (this.pointer == 0 && !board.win()) {
                    startX = Gdx.input.getX();
                    startY = Gdx.input.getY();
                    if (Gdx.input.getX() > board.getX() && Gdx.input.getX() < board.getX() + board.getWidth() &&
                            (Gdx.graphics.getHeight() - Gdx.input.getY()) > board.getY() &&
                            (Gdx.graphics.getHeight() - Gdx.input.getY()) < board.getY() + board.getHeight()) {
                        fistTime = true;
                        for (Piece piece : board.getAllPieces()) {
                            if (piece.getTile().isTouch()) {
                                sourceTile = board.getTile(piece.getPiecePosition());
                                game.getAssets().playPieceSelectionSoundEffect();
                            }
                        }
                        board.unlinkPieces();
                    }
                }
            }

            @Override
            public void pan(final InputEvent event, final float x, final float y, final float deltaX, final float deltaY) {
                if (pointer == 0 && !board.win() && Gdx.input.getX() > board.getX() && Gdx.input.getX() < board.getX() + board.getWidth() &&
                        (Gdx.graphics.getHeight() - Gdx.input.getY()) > board.getY() &&
                        (Gdx.graphics.getHeight() - Gdx.input.getY()) < board.getY() + board.getHeight()) {
                    if (!foreground.isVisible() && x >= board.getX() && x <= board.getX() + board.getWidth() &&
                            y > board.getY() && y < board.getY() + board.getHeight()) {
                        board.touch();
                    }
                    if (fistTime) {
                        direction = Math.abs(startX - x) > Math.abs(startY - Gdx.input.getY());
                        if (sourceTile != null) {
                            sourceTile.getPiece().toFront();
                        }
                        fistTime = false;
                    }
                    if (direction) {
                        if (startX > Gdx.input.getX()) {
                            board.setMovementDirection(Board.MOVE_LEFT);
                        } else {
                            board.setMovementDirection(Board.MOVE_RIGHT);
                        }
                    } else {
                        if (startY > Gdx.input.getY()) {
                            board.setMovementDirection(Board.MOVE_UP);
                        } else {
                            board.setMovementDirection(Board.MOVE_DOWN);
                        }
                    }
                    if (sourceTile != null) {
                        if (swap.isChecked() && (game.getPlayer().getKeys() > 0 || game.getPlayer().isPremium())) {
                            if (sourceTile != null) {
                                sourceTile.getPiece().setPosition(Gdx.input.getX() - board.getX(),
                                        Gdx.graphics.getHeight() - Gdx.input.getY() - board.getY(),
                                        Align.center);
                                for (final Piece piece : board.getAllPieces()) {
                                    if (piece.getTile().isTouch()) {
                                        piece.getTile().setHovering(true);
                                    } else {
                                        piece.getTile().setHovering(false);
                                    }
                                }
                            }
                        } else {
                            if (direction) {
                                sourceTile.getHorizontalContainer().setShift(Gdx.input.getX() - (sourceTile.getX() + board.getX()
                                        + board.getTileSize() / 2));
                                sourceTile.getVerticalContainer().setShift(0);
                            } else {
                                sourceTile.getVerticalContainer().setShift((Gdx.graphics.getHeight() - Gdx.input.getY()) -
                                        (sourceTile.getY() + board.getY() + board.getTileSize() / 2));
                                sourceTile.getHorizontalContainer().setShift(0);
                            }
                        }
                        for (final Piece piece : board.getAllPieces()) {
                            if (piece.getTile().isTouch(sourceTile.getPiece())) {
                                piece.getTile().setHovering(true);
                            } else {
                                piece.getTile().setHovering(false);
                            }
                        }
                    }
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (this.pointer == 0 && !board.win()) {
                    if (sourceTile != null) {
                        game.getAssets().playPieceSelectionSoundEffect();
                        if (swap.isChecked() && (game.getPlayer().getKeys() > 0 || game.getPlayer().isPremium())) {
                            if (sourceTile.getPiece().getPiecePosition() != board.getTouchTileId(sourceTile.getPiece()) &&
                                    board.getTouchTileId(sourceTile.getPiece()) != -1) {
                                board.swapping(sourceTile, PlayScene.this);
                                swap.toggle();
                                sourceTile = null;
                            } else {
                                sourceTile.getPiece().addAction(Actions.sequence(Actions.moveTo(sourceTile.getX() + board.getPiecePad(),
                                        sourceTile.getY() + board.getPiecePad(), duration)));
                            }
                        } else {
                            if (board.getTouchTileId(sourceTile.getPiece()) != -1 &&
                                    sourceTile.getContainer().getTiles().contains(board.getTile(board.getTouchTileId(sourceTile.getPiece())))) {
                                board.moving(sourceTile);
                            }
                        }
                    }
                    board.unTouch();
                    sourceTile = null;
                    board.checkSign();
                    if (board.win()) {
                        winAnimation();
                    }
                }
            }
        });
    }

    private Table optionsTable() {
        final float size = getWidth() / 8;
        final Table optionsTable = new Table();
        optionsTable.setVisible(false);
        optionsTable.setBounds(getWidth() / 8, getHeight() / 32, getWidth() - getWidth() / 4, getWidth() / 5);
        optionsTable.addAction(Actions.alpha(0));

        final Button sound = new Button(game.getAssets().getSkin(), "sound");
        if (game.getPlayer().getSoundStatue() == Player.MUTE) {
            sound.setStyle(game.getAssets().getSkin().get("mute", Button.ButtonStyle.class));
        } else if (game.getPlayer().getSoundStatue() == Player.EFFECT) {
            sound.setStyle(game.getAssets().getSkin().get("effect", Button.ButtonStyle.class));
        } else if (game.getPlayer().getSoundStatue() == Player.MUSIC) {
            sound.setStyle(game.getAssets().getSkin().get("music", Button.ButtonStyle.class));
        }
        sound.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getAssets().playClickSoundEffect();
                if (game.getPlayer().getSoundStatue() == Player.SOUND) {
                    sound.setStyle(game.getAssets().getSkin().get("effect", Button.ButtonStyle.class));
                    game.getPlayer().setSoundStatue(Player.EFFECT);
                    game.getAssets().pauseMusic();
                    game.getAssets().playSoundEffect();
                } else if (game.getPlayer().getSoundStatue() == Player.EFFECT) {
                    sound.setStyle(game.getAssets().getSkin().get("music", Button.ButtonStyle.class));
                    game.getPlayer().setSoundStatue(Player.MUSIC);
                    game.getAssets().playMusic();
                    game.getAssets().pauseSoundEffect();
                } else if (game.getPlayer().getSoundStatue() == Player.MUSIC) {
                    sound.setStyle(game.getAssets().getSkin().get("mute", Button.ButtonStyle.class));
                    game.getPlayer().setSoundStatue(Player.MUTE);
                    game.getAssets().pauseMusic();
                    game.getAssets().pauseSoundEffect();
                } else {
                    sound.setStyle(game.getAssets().getSkin().get("sound", Button.ButtonStyle.class));
                    game.getPlayer().setSoundStatue(Player.SOUND);
                    game.getAssets().playMusic();
                    game.getAssets().playSoundEffect();
                }
            }
        });

        final Button achievements = new Button(game.getAssets().getSkin(), "achievements");
        achievements.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                if (!achievements.isDisabled()) {
                    game.getAssets().playClickSoundEffect();
                    if (game.getServicesManager().showAchievements()) {
                        buildTable("No Achievements", "Something went wrong, please try again later", Color.DARK_GRAY, null, Color.DARK_GRAY);
                    }
                }
            }
        });
        achievements.setName("achievements");

        final Button playGames = new Button(game.getAssets().getSkin(), "googlePlayOn");
        playGames.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getAssets().playClickSoundEffect();
                if (game.getPlayer().isSign()) {
                    playGames.setStyle(game.getAssets().getSkin().get("googlePlayOff", Button.ButtonStyle.class));
                    achievements.setDisabled(true);
                    game.getServicesManager().signOut();
                    game.getPlayer().signOut();
                } else {
                    achievements.setDisabled(false);
                    playGames.setStyle(game.getAssets().getSkin().get("googlePlayOn", Button.ButtonStyle.class));
                    updatePlayScene();
                }
            }
        });
        playGames.setName("playGames");

        if (!game.getPlayer().isPremium()) {
            final Button shop = new Button(game.getAssets().getSkin(), "shop");
            shop.setName("shop");
            shop.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    showShopTable();
                }
            });

            final Button privacy = new Button(game.getAssets().getSkin(), "privacy");
            privacy.setName("privacy");
            privacy.addListener(new ClickListener() {
                @Override
                public void clicked(InputEvent event, float x, float y) {
                    super.clicked(event, x, y);
                    showConsentTable();
                }
            });

            optionsTable.add(privacy).align(Align.center).size(size).spaceRight(size / 3);
            optionsTable.add(shop).align(Align.center).size(size).spaceRight(size / 3);
        }


        optionsTable.add(sound).align(Align.center).size(size).spaceRight(size / 3);
        optionsTable.add(playGames).align(Align.center).size(size).spaceRight(size / 3);
        optionsTable.add(achievements).align(Align.center).size(size).spaceRight(size / 3);

        return optionsTable;
    }

    private void updatePlayScene() {
        final Image image = new Image() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
            }
        };
        image.setBounds(getX(), getY(), getWidth(), getHeight());
        addActor(image);
        if (!game.getPlayer().isSign()) {
            if (game.getServicesManager().signIn())
                game.getPlayer().signIn();
        } else {
            game.getServicesManager().initializePlayer();
        }
        image.addAction(Actions.sequence(Actions.delay(duration * 10), Actions.run(() -> {
            if (game.getServicesManager().getPlayerStateCode() == ServicesManager.PLAYER_INITIALIZED) {
                game.setSelectedLevel(game.getPlayer().getCurrentLevel());
                board.addAction(Actions.sequence(Actions.scaleBy(0.1f, 0.1f, duration * 0.1f),
                        Actions.parallel(Actions.fadeOut(duration), Actions.scaleBy(-1, -1, duration)),
                        Actions.run(() -> {
                            board.remove();
                            board = new Board("board", game.getSelectedLevel(), getWidth(), getHeight(), game.getAssets().getAtlas(), game.getShapeRenderer(), getStage().getBatch());
                            board.setVisible(false);
                            board.addAction(Actions.sequence(Actions.parallel(Actions.alpha(0), Actions.scaleBy(-1, -1)),
                                    Actions.run(() -> {
                                        game.setGradientColors(board.getFrequentColors(), duration);
                                        board.setVisible(true);
                                        board.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(duration), Actions.scaleBy(1, 1, duration))));
                                    })));
                            addActor(board);
                            board.toBack();
                        })));
            } else {
                updatePlayScene();
            }
        }), Actions.removeActor()));
    }

    private void showShopTable() {
        final Table donationTable = new Table();

        /*final TextButton watchAd = buildBackgroundTextButton(fromOne255Color(63, 155, 177), Color.WHITE,
                "Watch Advertisement");
        watchAd.addListener(new ClickListener(){
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getAssets().playClickSoundEffect();
                game.getAssets().playTransitionSoundEffect();
                if(game.getServicesManager().isInternetAvailable()) {
                    if(game.getServicesManager().hasAdvertisement()){
                        donationTable.getParent().getParent().getParent().addAction(Actions.sequence(
                                Actions.moveTo(donationTable.getParent().getParent().getParent().getX(), getHeight(), duration),
                                Actions.run(() -> {
                                    game.getServicesManager().showAdvertisement();
                                    game.getPlayer().resetAdCount();
                                }),Actions.run(donationTable.getParent().getParent().getParent()::remove)));
                    } else {
                        donationTable.getParent().getParent().getParent().addAction(Actions.sequence(
                                Actions.moveTo(donationTable.getParent().getParent().getParent().getX(), getHeight(), duration),
                                Actions.run(() -> buildTable("noAdvertisementTable", noAdvertisementText(), Color.DARK_GRAY,null, Color.DARK_GRAY)),
                                        Actions.run(donationTable.getParent().getParent().getParent()::remove)));
                    }
                } else {
                    donationTable.getParent().getParent().getParent().addAction(Actions.sequence(
                            Actions.moveTo(donationTable.getParent().getParent().getParent().getX(), getHeight(), duration),
                            Actions.run(() -> buildTable("noInternetTable", noInternetText(), Color.DARK_GRAY,null, Color.DARK_GRAY)),
                            Actions.run(donationTable.getParent().getParent().getParent()::remove)));
                }
            }
        });*/

        final TextButton cheap = buildBackgroundTextButton(fromOne255Color(63, 155, 177), Color.WHITE,
                game.getServicesManager().getPurchasesInformation(ServicesManager.CHEAP));
        cheap.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getServicesManager().purchases(ServicesManager.CHEAP);
                updateGameMode();
            }
        });

        final TextButton medium = buildBackgroundTextButton(fromOne255Color(142, 181, 66), Color.WHITE,
                game.getServicesManager().getPurchasesInformation(ServicesManager.MEDIUM));
        medium.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getServicesManager().purchases(ServicesManager.MEDIUM);
                updateGameMode();
            }
        });

        final TextButton expensive = buildBackgroundTextButton(fromOne255Color(250, 208, 26), Color.WHITE,
                game.getServicesManager().getPurchasesInformation(ServicesManager.EXPENSIVE));
        expensive.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getServicesManager().purchases(ServicesManager.EXPENSIVE);
                updateGameMode();
            }
        });

        final TextButton restorePurchase = buildBackgroundTextButton(fromOne255Color(245, 124, 33), Color.WHITE, "Restore Purchase");
        expensive.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getServicesManager().restorePurchase();
                updateGameMode();
            }
        });


        final float width = getWidth() / 2;
        final float height = getWidth() / 10;
        final float pad = getWidth() / 64;

        //donationTable.add(watchAd).width(width).height(height).padBottom(pad);
        //donationTable.row();
        donationTable.add(cheap).width(width).height(height).padBottom(pad);
        donationTable.row();
        donationTable.add(medium).width(width).height(height).padBottom(pad);
        donationTable.row();
        donationTable.add(expensive).width(width).height(height).padBottom(pad);
        donationTable.row();
        donationTable.add(restorePurchase).width(width).height(height);

        buildTable("donationTable", donationText(), fromOne255Color(93, 63, 182), donationTable,
                fromOne255Color(245, 33, 124));//fromOne255Color (230, 40, 40));
    }

    private void updateGameMode() {
        Gdx.app.postRunnable(() -> {
            if (game.getPlayer().isPremium()) {
                findActor("shop").addAction(Actions.removeActor());
                optionTable.getCell(findActor("shop")).reset();
                findActor("privacy").addAction(Actions.removeActor());
                optionTable.getCell(findActor("privacy")).reset();
                skillNumber.addAction(Actions.removeActor());
            }
        });
    }

    private void nextBoard() {
        if (Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()) <= game.getLevelsNumber() &&
                Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()) < game.getPlayer().getCurrentLevel()) {
            ((ImageButton) levelSelectionTable.getChildren().get(0)).setDisabled(true);
            ((ImageButton) levelSelectionTable.getChildren().get(2)).setDisabled(true);
            ((Label) levelSelectionTable.getChildren().get(1)).setText(Integer.toString(game.getSelectedLevel() + 1));
            levelSelectionTable.getChildren().get(1).act(Gdx.graphics.getDeltaTime());
            final Board nextBoard = new Board("nextBoard", Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()),
                    getWidth(), getHeight(), game.getAssets().getAtlas(), game.getShapeRenderer(), getStage().getBatch());
            game.setGradientColors(nextBoard.getFrequentColors(), duration);
            addActor(nextBoard);
            nextBoard.setX(getWidth());
            nextBoard.toBack();
            game.getAssets().playTransitionSoundEffect();
            nextBoard.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(getWidth() / 2 - nextBoard.getWidth() / 2,
                    getHeight() / 2 - nextBoard.getHeight() / 2, duration * 2),
                    Actions.fadeIn(duration), Actions.run(() -> board.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-board.getWidth(), board.getY(), duration * 2),
                            Actions.fadeOut(duration)), Actions.run(() -> {
                        board = nextBoard;
                        findActor("board").remove();
                        board.setName("board");
                        game.setSelectedLevel(Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()));
                        ((ImageButton) levelSelectionTable.getChildren().get(0)).setDisabled(false);
                        ((ImageButton) levelSelectionTable.getChildren().get(2)).setDisabled(false);
                    })))))));
        }
    }

    private void previousBoard() {
        if (Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()) > 1) {
            ((ImageButton) levelSelectionTable.getChildren().get(0)).setDisabled(true);
            ((ImageButton) levelSelectionTable.getChildren().get(2)).setDisabled(true);
            ((Label) levelSelectionTable.getChildren().get(1)).setText(Integer.toString(game.getSelectedLevel() - 1));
            levelSelectionTable.getChildren().get(1).act(Gdx.graphics.getDeltaTime());
            final Board previousBoard = new Board("previousBoard", Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1))
                    .getText().toString()), getWidth(), getHeight(), game.getAssets().getAtlas(), game.getShapeRenderer(), getStage().getBatch());
            game.setGradientColors(previousBoard.getFrequentColors(), duration);
            previousBoard.setX(-previousBoard.getWidth());
            addActor(previousBoard);
            previousBoard.toBack();
            game.getAssets().playTransitionSoundEffect();
            previousBoard.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(getWidth() / 2 - previousBoard.getWidth() / 2,
                    getHeight() / 2 - previousBoard.getHeight() / 2, duration * 2), Actions.fadeIn(duration),
                    Actions.run(() -> board.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(getWidth(), board.getY(), duration * 2),
                            Actions.fadeOut(duration)), Actions.run(() -> {
                        board = previousBoard;
                        findActor("board").remove();
                        board.setName("board");
                        game.setSelectedLevel(Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()));
                        ((ImageButton) levelSelectionTable.getChildren().get(0)).setDisabled(false);
                        ((ImageButton) levelSelectionTable.getChildren().get(2)).setDisabled(false);
                    })))))));
        }
    }

    private void showOptions() {
        boardToBackground();
        foreground.addListener(optionsListener);
        optionTable.addAction(Actions.sequence(Actions.run(() -> {
            optionTable.setVisible(true);
            optionTable.toFront();
        }), Actions.fadeIn(duration)));
        levelSelectionTable.addAction(Actions.sequence(Actions.run(() -> {
            levelSelectionTable.setVisible(true);
            levelSelectionTable.toFront();
        }), Actions.fadeIn(duration)));
        quote.hide();
    }

    private void hideOptions() {
        boardToFocus();
        foreground.clearListeners();
        optionTable.addAction(Actions.sequence(Actions.alpha(0, duration), Actions.run(() -> optionTable.setVisible(false))));
        levelSelectionTable.addAction(Actions.sequence(Actions.alpha(0, duration), Actions.run(() -> levelSelectionTable.setVisible(false))));
        quote.show();
    }

    private void restart() {
        if (game.getPlayer().checkAdCount() || game.getPlayer().isPremium()) {
            hideOptions();
            game.getAssets().playRestartSoundEffect();
            board.addAction(Actions.sequence(Actions.scaleBy(0.1f, 0.1f, duration * 0.1f),
                    Actions.parallel(Actions.fadeOut(duration), Actions.scaleBy(-1, -1, duration)),
                    Actions.run(() -> {
                        board.remove();
                        board = new Board("board", game.getSelectedLevel(), getWidth(), getHeight(), game.getAssets().getAtlas(),
                                game.getShapeRenderer(), getStage().getBatch());
                        board.setVisible(false);
                        board.addAction(Actions.sequence(Actions.parallel(Actions.alpha(0), Actions.scaleBy(-1, -1)),
                                Actions.run(() -> {
                                    board.setVisible(true);
                                    board.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(duration), Actions.scaleBy(1, 1, duration))));
                                })));
                        addActor(board);
                        board.toBack();
                    })));
        } else if (game.getServicesManager().isInternetAvailable()) {
            hideOptions();
            game.getAssets().playRestartSoundEffect();
            board.addAction(Actions.sequence(Actions.scaleBy(0.1f, 0.1f, duration * 0.1f),
                    Actions.parallel(Actions.fadeOut(duration), Actions.scaleBy(-1, -1, duration)),
                    Actions.run(() -> {
                        board.remove();
                        board = new Board("board", game.getSelectedLevel(), getWidth(), getHeight(), game.getAssets().getAtlas(),
                                game.getShapeRenderer(), getStage().getBatch());
                        board.setVisible(false);
                        board.addAction(Actions.sequence(Actions.parallel(Actions.alpha(0), Actions.scaleBy(-1, -1)),
                                Actions.run(() -> {
                                    board.setVisible(true);
                                    game.getServicesManager().showInterstitial();
                                    board.addAction(Actions.sequence(Actions.parallel(Actions.fadeIn(duration), Actions.scaleBy(1, 1, duration))));
                                })));
                        addActor(board);
                        board.toBack();
                    })));
        } else {
            showInternetBlock();
        }
    }

    private void boardToBackground() {
        foreground.setVisible(true);
        foreground.addActionToShadow(Actions.alpha(0.2f, duration));
        restart.addAction(Actions.moveTo(-restart.getWidth() - getX(), getY(), duration));
        restart.addAction(Actions.alpha(0, duration));
        swapView.addAction(Actions.moveTo(getWidth(), getY(), duration));
        swapView.addAction(Actions.alpha(0, duration));
        option.addAction(Actions.moveTo(option.getX(), -option.getHeight(), duration));
        option.addAction(Actions.alpha(0, duration));
    }

    private void boardToFocus() {
        foreground.setVisible(false);
        foreground.addActionToShadow(Actions.sequence(Actions.fadeOut(duration), Actions.run(this::playTutorial)));
        restart.addAction(Actions.moveTo(getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16, duration));
        restart.addAction(Actions.alpha(1, duration));
        swapView.addAction(Actions.moveTo(getWidth() - getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16, duration));
        swapView.addAction(Actions.alpha(1, duration));
        option.addAction(Actions.moveTo(option.getX(), getWidth() / 16, duration));
        option.addAction(Actions.alpha(1, duration));
    }


    public void winAnimation() {
        game.getAssets().playWinSoundEffect();
        foreground.addActionToShadow(Actions.fadeOut(0));
        board.winAnimation(duration * 10);
        foreground.addListener(winListener);
        foreground.deleteGap();
        foreground.setVisible(true);
        foreground.toFront();
        restart.addAction(Actions.moveTo(-restart.getWidth() - getX(), getY(), duration));
        restart.addAction(Actions.alpha(0, duration));
        swapView.addAction(Actions.moveTo(getWidth(), getY(), duration));
        swapView.addAction(Actions.alpha(0, duration));
        option.addAction(Actions.moveTo(option.getX(), -option.getHeight(), duration));
        option.addAction(Actions.alpha(0, duration));
        share.addAction(Actions.sequence(Actions.delay(duration * 20), Actions.fadeIn(duration)));
        share.toFront();
        share.setVisible(true);
    }

    private void openNextLevel() {
        if (game.getPlayer().checkAdCount() || game.getPlayer().isPremium()) {
            openLevel();
        } else if (game.getServicesManager().isInternetAvailable()) {
            game.getServicesManager().showInterstitial();
            openLevel();
        } else {
            showInternetBlock();
        }
    }

    private void openLevel() {
        if (Integer.parseInt(((Label) levelSelectionTable.getChildren().get(1)).getText().toString()) < game.getLevelsNumber()) {
            final int level;
            if (game.getSelectedLevel() < game.getPlayer().getCurrentLevel()) {
                level = game.getSelectedLevel() + 1;
                game.setSelectedLevel(level);
            } else {
                level = game.getPlayer().getCurrentLevel() + 1;
                game.getPlayer().setCurrentLevel(level, game.getLevelsNumber());
                game.setSelectedLevel(game.getPlayer().getCurrentLevel());
                game.getServicesManager().saveLocalPlayer();

            }
            foreground.setVisible(true);
            foreground.addActionToShadow(Actions.alpha(0.2f, duration));
            share.addAction(Actions.fadeOut(duration));
            share.setVisible(false);
            final Board nextLevel = new Board("nextLevel", level, getWidth(), getHeight(), game.getAssets().getAtlas(),
                    game.getShapeRenderer(), getStage().getBatch());
            game.setGradientColors(nextLevel.getFrequentColors(), duration);
            addActor(nextLevel);
            nextLevel.setX(getWidth());
            nextLevel.toBack();
            game.getAssets().playTransitionSoundEffect();
            nextLevel.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(getWidth() / 2 - nextLevel.getWidth() / 2,
                    getHeight() / 2 - nextLevel.getHeight() / 2, duration * 2),
                    Actions.fadeIn(duration), Actions.run(() -> board.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-board.getWidth(), board.getY(), duration * 2),
                            Actions.fadeOut(duration)), Actions.run(() -> {
                        board = nextLevel;
                        findActor("board").remove();
                        board.setName("board");
                        ((Label) levelSelectionTable.getChildren().get(1)).setText(Integer.toString(level));
                        levelSelectionTable.getChildren().get(1).act(Gdx.graphics.getDeltaTime());
                        if (level == 100) game.getServicesManager().unlockAchievement(ServicesManager.PLAY_FUL);
                        if (level == 200) game.getServicesManager().unlockAchievement(ServicesManager.MASTER);
                        foreground.addActionToShadow(Actions.sequence(Actions.fadeOut(duration * 2), Actions.run(() -> {
                            foreground.setVisible(false);
                            playTutorial();
                        })));
                        if (game.getPlayer().isAskForRating() && game.getPlayer().shouldAskForRating()) showRating();
                    })))))));
            restart.addAction(Actions.moveTo(getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16, duration));
            restart.addAction(Actions.alpha(1, duration));
            swapView.addAction(Actions.moveTo(getWidth() - getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16, duration));
            swapView.addAction(Actions.alpha(1, duration));
            option.addAction(Actions.moveTo(option.getX(), getWidth() / 16, duration));
            option.addAction(Actions.alpha(1, duration));
            foreground.clearListeners();
        } else if (findActor("every week update") == null) {
            share.addAction(Actions.fadeOut(duration));
            share.setVisible(false);
            final Label.LabelStyle style = new Label.LabelStyle();
            style.font = game.getAssets().getFont(Assets.BOLD);
            style.fontColor = Color.WHITE;
            final Label label = new Label("A new level(s) every week \n\n" +
                    "And " + game.getServicesManager().calculateRemainingDaysUntilUpdate() + " remaining" +
                    "\n\nfor the next update.", style);
            label.setAlignment(Align.center);
            label.setPosition(getWidth(), getHeight() / 2 - label.getPrefHeight() / 2);
            label.setName("every week update");
            addActor(label);
            board.addAction(Actions.sequence(Actions.parallel(Actions.moveTo(-board.getWidth(), board.getY(), duration * 2),
                    Actions.fadeOut(duration))));
            game.getAssets().playTransitionSoundEffect();
            label.addAction(Actions.sequence(
                    Actions.moveTo(getWidth() / 2 - label.getPrefWidth() / 2, getHeight() / 2 - label.getPrefHeight() / 2, duration * 2),
                    Actions.delay(duration * 50),
                    Actions.run(() -> {
                        board.remove();
                        board = new Board("board", game.getSelectedLevel(), getWidth(), getHeight(), game.getAssets().getAtlas(),
                                game.getShapeRenderer(), getStage().getBatch());
                        game.setGradientColors(board.getFrequentColors(), duration);
                        board.setX(-board.getWidth());
                        addActor(board);
                        board.toBack();
                        board.addAction(Actions.moveTo(getWidth() / 2 - board.getWidth() / 2, board.getY(), duration * 2));
                        game.getAssets().playTransitionSoundEffect();
                        foreground.addActionToShadow(Actions.sequence(Actions.fadeOut(duration * 2), Actions.run(() -> foreground.setVisible(false))));
                        restart.addAction(Actions.moveTo(getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16, duration));
                        restart.addAction(Actions.alpha(1, duration));
                        swapView.addAction(Actions.moveTo(getWidth() - getWidth() / 6 / 2 - getWidth() / 8 / 2, getWidth() / 16, duration));
                        swapView.addAction(Actions.alpha(1, duration));
                        option.addAction(Actions.moveTo(option.getX(), getWidth() / 16, duration));
                        option.addAction(Actions.alpha(1, duration));
                        label.addAction(Actions.sequence(Actions.moveTo(getWidth(), getHeight() / 2 - label.getPrefHeight() / 2, duration * 2),
                                Actions.removeActor()));
                        playTutorial();
                    })));
            foreground.clearListeners();
        }
    }

    @Override
    public void act(final float delta) {
        super.act(delta);
        if (!game.getPlayer().isPremium()) {
            skillNumber.setText(game.getPlayer().getKeys() + "");
            skillNumber.act(delta);
        }
        if (!game.getPlayer().isSign() && !((Button) findActor("achievements")).isDisabled()) {
            ((Button) findActor("playGames")).setStyle(game.getAssets().getSkin().get("googlePlayOff", Button.ButtonStyle.class));
            ((Button) findActor("achievements")).setDisabled(true);
        }
        if (swap.isChecked()) {
            swap.setColor(Color.GRAY);
        } else {
            swap.setColor(Color.WHITE);
        }
    }

    private void showInternetBlock() {
        final Table table = new Table();

        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = game.getAssets().getFont(Assets.REGULAR);
        style.fontColor = Color.WHITE;
        final Label label = new Label("Can you please connect to the Internet\n\n" +
                "We know ads are boring and everything\n" +
                "but it's the only way we can keep our\n" +
                "dream alive or you can bay any amount \n" +
                "you like to get rid of those ads\n" +
                "and support us.\n\n\n" +
                "Thank you for your understanding.", style);
        label.setAlignment(Align.center);
        table.add(label).pad(getWidth() / 16).spaceBottom(getHeight() / 16);
        table.row();

        final Stack stack = new Stack();
        stack.setSize(getWidth(), getHeight());

        final Image image = new Image(game.getAssets().getAtlas().findRegion("pixel"));
        image.setColor(Color.BLACK.r, Color.BLACK.g, Color.BLACK.b, 0.75f);
        stack.add(image);

        stack.add(table);

        addActor(stack);

        stack.addAction(Actions.forever(Actions.sequence(Actions.run(() -> {
            if (game.getServicesManager().isInternetAvailable()) {
                game.getServicesManager().showInterstitial();
                stack.addAction(Actions.sequence(Actions.fadeOut(duration), Actions.removeActor()));
            }
        }))));
    }

    private TextButton buildBackgroundTextButton(final Color fontAndBorderColor, final Color backgroundColor, final String text) {
        final TextButton.TextButtonStyle textButtonStyle = new TextButton.TextButtonStyle();
        textButtonStyle.font = game.getAssets().getFont(Assets.REGULAR);
        textButtonStyle.fontColor = fontAndBorderColor;
        textButtonStyle.downFontColor = new Color(fontAndBorderColor).add(Color.GRAY);
        textButtonStyle.overFontColor = new Color(fontAndBorderColor).add(Color.GRAY);

        return new TextButton(text, textButtonStyle) {
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
                game.getShapeRenderer().setColor(backgroundColor.r, backgroundColor.g, backgroundColor.b, backgroundColor.a * parentAlpha);
                game.getShapeRenderer().roundRect(getX() + getWidth() / 32, getY() + getWidth() / 32,
                        getWidth() - getWidth() / 16, getHeight() - getWidth() / 16, getParent().getWidth() / 50);
                game.getShapeRenderer().end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
                batch.begin();
                super.draw(batch, parentAlpha);
            }
        };
    }

    private void showConsentTable() {
        final Table consentTable = new Table();

        final Label.LabelStyle labelStyle = new Label.LabelStyle();
        labelStyle.font = game.getAssets().getFont(Assets.REGULAR);
        labelStyle.fontColor = fromOne255Color(180, 180, 180);

        final Label consentLabel = new Label("We Care about your privacy and data\n" +
                "security, we keep this game free by\n" +
                "showing advertisement.\n\n" +
                "Can we continue using your data to\n" +
                "tailor advertisement for you?", labelStyle);
        consentLabel.setAlignment(Align.center);

        final Label.LabelStyle linkStyle = new Label.LabelStyle();
        linkStyle.font = game.getAssets().getFont(Assets.BOLD);
        linkStyle.fontColor = fromOne255Color(36, 36, 57);

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

        final TextButton yse = buildBackgroundTextButton(fromOne255Color(115, 83, 114), Color.WHITE, "Yes");
        yse.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                buildTable("yes", "Thank you for supporting us we will\n use your data to tailor advertisements",
                        Color.DARK_GRAY, null, Color.DARK_GRAY);
                consentTable.getParent().getParent().getParent().addAction(Actions.removeActor());
                game.getServicesManager().setConsent(true);
            }
        });

        final TextButton no = buildBackgroundTextButton(fromOne255Color(143, 117, 142), Color.WHITE, "No");
        no.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                buildTable("no", "We respect your choice so we will not use\nyour data to tailor the advertisements",
                        Color.DARK_GRAY, null, Color.DARK_GRAY);
                consentTable.getParent().getParent().getParent().addAction(Actions.removeActor());
                game.getServicesManager().setConsent(false);
            }
        });

        final float width = getWidth() / 2;
        final float height = getWidth() / 10;
        final float pad = getWidth() / 64;

        consentTable.add(consentLabel).padLeft(pad * 3).padRight(pad * 3).padBottom(pad * 2);
        consentTable.row();
        consentTable.add(link).padBottom(pad * 2);
        consentTable.row();
        consentTable.add(yse).width(width).height(height).pad(pad);
        consentTable.row();
        consentTable.add(no).width(width).height(height).pad(pad);

        buildTable("Consent Table", null, null, consentTable, fromOne255Color(138, 223, 220));
    }

    private void showRating() {
        Table table = new Table();

        final Table stars = new Table();
        stars.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getServicesManager().rate();
            }
        });

        for (int counter = 0; counter < 5; counter++) {
            final Image image = new Image(game.getAssets().getAtlas().findRegion("star"));
            image.setColor(fromOne255Color(203, 203, 65));
            stars.add(image).size(getWidth() / 8).pad(getWidth() / 128);
        }

        final TextButton noNever = buildBackgroundTextButton(fromOne255Color(78, 103, 102), Color.WHITE, "No, Never");
        noNever.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getPlayer().neverAskForRating();
                game.getAssets().playClickSoundEffect();
                game.getAssets().playTransitionSoundEffect();
                table.getParent().getParent().getParent().addAction(Actions.sequence(
                        Actions.moveTo(getParent().getParent().getParent().getX(), getHeight(), duration), Actions.removeActor()));
            }
        });

        table.add(stars).padBottom(getWidth() / 16);
        table.row();
        table.add(noNever).width(getWidth() / 2).height(getHeight() / 15);

        buildTable("rating", "Please consider rating the game it dose help.", fromOne255Color(33, 34, 44),
                table, fromOne255Color(60, 62, 74));
    }

    private void buildTable(final String name, final String text, final Color fontColor, final Table subTable, final Color closeColor) {
        final Stack stack = new Stack();

        final Table table = new Table();

        addActor(stack);
        game.getAssets().playTransitionSoundEffect();
        stack.setName(name);

        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = game.getAssets().getFont(Assets.REGULAR);
        style.fontColor = (fontColor != null) ? fontColor : Color.BLACK;
        final Label label = new Label("", style);
        if (text != null) {
            label.setText(text);
            label.setAlignment(Align.center);
        }

        final Table whiteBackground = new Table() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.end();
                game.getShapeRenderer().begin();
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                game.getShapeRenderer().setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a * parentAlpha);
                game.getShapeRenderer().set(ShapeRenderer.ShapeType.Filled);
                game.getShapeRenderer().topRoundedRect(getX(), getY(), getPrefWidth(), getPrefHeight(), getPrefWidth() / 10);
                game.getShapeRenderer().end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
                batch.begin();
                super.draw(batch, parentAlpha);
            }
        };

        if (text != null) whiteBackground.add(label).pad(getWidth() / 20);
        else whiteBackground.add(label);
        whiteBackground.row();

        if (subTable != null) {
            whiteBackground.add(subTable).padBottom(getWidth() / 20);
        }

        table.add(whiteBackground);
        table.row();

        final Button close = new Button(game.getAssets().getSkin(), "close");
        close.setColor(closeColor);
        close.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                super.clicked(event, x, y);
                game.getAssets().playClickSoundEffect();
                game.getAssets().playTransitionSoundEffect();
                stack.addAction(Actions.sequence(Actions.moveTo(stack.getX(), getHeight(), duration), Actions.run(stack::remove)));
            }
        });
        table.add(close).size(getWidth() / 10).pad(getWidth() / 64);


        final Image image = new Image() {
            @Override
            public void draw(Batch batch, float parentAlpha) {
                batch.end();
                game.getShapeRenderer().begin();
                Gdx.gl.glEnable(GL20.GL_BLEND);
                Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
                game.getShapeRenderer().setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, 0.5f);
                game.getShapeRenderer().set(ShapeRenderer.ShapeType.Filled);
                game.getShapeRenderer().roundRect(getX(), getY(), getWidth(), getHeight(), getWidth() / 10);
                game.getShapeRenderer().end();
                Gdx.gl.glDisable(GL20.GL_BLEND);
                batch.begin();
            }
        };
        image.setSize(table.getPrefWidth(), table.getPrefHeight());

        stack.setSize(table.getPrefWidth(), table.getPrefHeight());

        stack.add(image);
        stack.add(table);

        stack.setPosition(getWidth() / 2 - stack.getWidth() / 2, getHeight());
        stack.addAction(Actions.moveTo(stack.getX(), getHeight() / 2 - stack.getHeight() / 2, duration));
    }

    private void playTutorial() {
        if (game.getSelectedLevel() == 1) {
            initializeTutorial();
            quote.setText("Drag and drop");
            foreground.getGap().setBounds(board.getX(), board.getY(), board.getTileSize() * 2, board.getTileSize());
            board.showTutorialPoint(foreground.getGap(), new int[]{0}, new int[]{1}, new Color[]{board.getTile(1).getSign().getColor()});
        } else if (game.getSelectedLevel() == 2) {
            initializeTutorial();
            quote.setText("This moving donut called a Sign\nyou have to build a connection\n" +
                    "between them using those circles\nwith the same colors to win..");
            foreground.getGap().setBounds(board.getX(), board.getY(), board.getTileSize(), board.getTileSize());
            foreground.setClickable(false);
            quote.addAction(Actions.sequence(Actions.delay(duration * 50), Actions.run(() ->
                    foreground.getGap().addAction(Actions.sequence(
                            Actions.moveTo(board.getX() + board.getTileSize(), board.getY() + board.getTileSize(), duration * 10),
                            Actions.moveTo(board.getX() + board.getTileSize() * 2, board.getY() + board.getTileSize(), duration * 10),
                            Actions.moveTo(board.getX() + board.getTileSize() * 2, board.getY(), duration * 10)))), Actions.delay(duration * 50), Actions.run(() -> {
                quote.setText("Let's start");
                foreground.getGap().addAction(Actions.sequence(Actions.parallel(
                        Actions.moveTo(board.getX(), board.getY() + board.getTileSize(), duration * 10),
                        Actions.sizeTo(board.getTileSize() * 2, board.getTileSize(), duration * 10)), Actions.run(() -> {
                    foreground.setClickable(true);
                    board.showTutorialPoint(foreground.getGap(), new int[]{4, 0, 1}, new int[]{3, 3, 4}, new Color[]{
                            board.getTile(0).getSign().getColor(), board.getTile(3).getSign().getColor(),
                            board.getTile(3).getSign().getColor()
                    });
                })));
            })));
        } else if (game.getSelectedLevel() == 3) {
            initializeTutorial();
            foreground.setClickable(false);
            foreground.getGap().setBounds(board.getX() + board.getTileSize(), board.getY() + board.getTileSize(),
                    board.getTileSize() * 2, board.getTileSize());
            quote.setText("If you are stuck then just Swap it");
            quote.addAction(Actions.sequence(Actions.delay(duration * 40), Actions.run(() -> {
                quote.setText("Click here..");
                foreground.getGap().addAction(Actions.sequence(Actions.parallel(Actions.moveTo(swapView.getX() - board.getPiecePad() / 2,
                        swapView.getY() - board.getPiecePad() / 2, duration * 10), Actions.sizeTo(
                        swapView.getWidth() + board.getPiecePad(), swapView.getHeight() + board.getPiecePad(), duration * 10)),
                        Actions.run(() -> foreground.setClickable(true))));
                quote.addAction(Actions.forever(Actions.sequence(Actions.delay(duration), Actions.run(() -> {
                    if (swap.isChecked()) {
                        quote.clearActions();
                        quote.setText("Now drag and drop");
                        foreground.getGap().addAction(Actions.sequence(Actions.parallel(Actions.moveTo(board.getX() + board.getTileSize(),
                                board.getY() + board.getTileSize(), duration),
                                Actions.sizeTo(board.getTileSize() * 2, board.getTileSize(), duration)), Actions.run(() -> board.showTutorialPoint(foreground.getGap(), new int[]{5}, new int[]{6}, new Color[]{board.getTile(2).getSign().getColor()}))));
                    }
                }))));
            })));
        } else if (game.getSelectedLevel() == 4) {
            quote.pause();
            quote.setText("Now try to solve this level on your own");
        } else if (game.getSelectedLevel() == 11) {
            initializeTutorial();
            quote.addAction(Actions.sequence(Actions.fadeOut(duration), Actions.run(() -> quote.setText("Border and their rules")),
                    Actions.fadeIn(duration), Actions.delay(duration * 30), Actions.run(() -> {
                        quote.setText("This one can only be linked from\nthe left");
                        foreground.getGap().setBounds(board.getX(), board.getY(), board.getTileSize(), board.getTileSize());
                    }), Actions.delay(duration * 30), Actions.run(() -> {
                        quote.setText("This one can only be linked from\nthe right");
                        foreground.getGap().addAction(Actions.moveTo(board.getX() + board.getTileSize(), board.getY(), duration * 10));
                    }), Actions.delay(duration * 40), Actions.run(() -> {
                        quote.setText("This one can be linked from\nthe right and the left");
                        foreground.getGap().addAction(Actions.moveTo(board.getX() + board.getTileSize() * 2, board.getY(), duration * 10));
                    }), Actions.delay(duration * 40), Actions.run(() -> {
                        quote.setText("Now let's solve it");
                        foreground.getGap().addAction(Actions.sequence(Actions.parallel(
                                Actions.moveTo(board.getX() + board.getTileSize(), board.getY(), duration),
                                Actions.sizeTo(board.getTileSize() * 2, board.getTileSize(), duration)),
                                Actions.run(() -> board.showTutorialPoint(foreground.getGap(), new int[]{2}, new int[]{1}, new Color[]{Color.BLUE}))));
                    })));
        } else if (game.getSelectedLevel() == 51) {
            initializeTutorial();
            quote.pause();
            quote.setText("This piece called Portal\nIt is linked..");
            foreground.setClickable(false);
            foreground.getGap().setBounds(board.getX(), board.getY(), board.getTileSize(), board.getTileSize());
            quote.addAction(Actions.sequence(Actions.delay(duration * 30), Actions.run(() -> {
                quote.setText("To this one.");
                foreground.getGap().addAction(Actions.moveTo(board.getX() + board.getTileSize() * 2, board.getY() + board.getTileSize() * 2,
                        duration * 5));
            }), Actions.delay(duration * 20), Actions.run(() -> {
                foreground.addActionToShadow(Actions.sequence(Actions.fadeOut(duration), Actions.run(() -> {
                    foreground.setVisible(false);
                    foreground.getGap().setBounds(0, 0, 0, 0);
                })));
                quote.setText("Try to use it to win.");
            })));
        } else if (game.getSelectedLevel() == 101) {
            initializeTutorial();
            quote.pause();
            quote.toFront();
            quote.setText("This piece called Gradient..");
            foreground.setClickable(false);
            foreground.getGap().setBounds(board.getX() + board.getTileSize() * 2, board.getY(), board.getTileSize(), board.getTileSize());
            quote.addAction(Actions.sequence(Actions.delay(duration * 30), Actions.run(() -> {
                quote.setText("It can link two pieces with\ndifferent colors.");
            }), Actions.delay(duration * 20), Actions.run(() -> foreground.getGap().addAction(Actions.sequence(Actions.parallel(
                    Actions.moveTo(board.getX(), board.getY(), duration),
                    Actions.sizeTo(board.getTileSize() * 2, board.getTileSize(), duration)),
                    Actions.run(() -> {
                        foreground.setClickable(true);
                        board.showTutorialPoint(foreground.getGap(), new int[]{1}, new int[]{0}, new Color[]{Color.RED});
                    }))))));
        } else {
            quote.resume();
            if (quote.getText().equals("Drag & Drop") || quote.getText().equals("Let's start") ||
                    quote.getText().equals("Now let's solve it") || quote.getText().equals("Now drag and drop") ||
                    quote.getText().equals("Now try to solve this level on your own") || quote.getText().equals("Try to use it to win.") ||
                    quote.getText().equals("It can link two pieces with\ndifferent colors.")) {
                quote.run();
            }
        }
    }

    private void initializeTutorial() {
        foreground.clearListeners();
        foreground.addActionToShadow(Actions.alpha(0.5f, duration));
        foreground.setVisible(true);
        foreground.setGapShape(Foreground.ROUND_RECT_SHAPE);
        foreground.setClickable(true);
        quote.pause();
    }


    private Color fromOne255Color(final int r, final int g, final int b) {
        return new Color(r / 255f, g / 255f, b / 255f, 1);
    }

    private String donationText() {
        return "Bay any amount you think this game\n" +
                "deserve to support us and to get \n" +
                "rid of advertisements.";
    }


    private void takeScreenshot() {
        final String quoteText = quote.getText();
        quote.setText("" + game.getSelectedLevel());
        share.addAction(Actions.alpha(0));
        final Table table = new Table();
        table.setBounds(0, 0, getWidth(), getHeight() * 0.2f);
        game.getAssets().changeFontSize(Assets.LIGHT, getWidth() / 9);
        final Label.LabelStyle style = new Label.LabelStyle();
        style.font = game.getAssets().getFont(Assets.LIGHT);
        table.add(new Label("Cirzle", style)).spaceRight(table.getWidth() * 0.1f).padTop(table.getHeight() * 0.1f);
        table.add(new Image(game.getAssets().getLogo())).width(getWidth() * 0.2f).height(getWidth() * 0.2f);
        addActor(table);
        foreground.addActionToShadow(Actions.fadeIn(duration));
        foreground.setColor(Color.WHITE);
        foreground.setVisible(true);
        foreground.addActionToShadow(Actions.sequence(Actions.alpha(0.5f, 0.05f), Actions.fadeOut(0.05f), Actions.run(() -> {
            foreground.setColor(Color.BLACK);
            final byte[] pixels = ScreenUtils.getFrameBufferPixels(0, 0, (int) getWidth(), (int) getHeight(), true);
            for (int i = 4; i < pixels.length; i += 4) {
                pixels[i - 1] = (byte) 255;
            }
            final Pixmap pixmap = new Pixmap((int) getWidth(), (int) getHeight(), Pixmap.Format.RGBA8888);
            BufferUtils.copy(pixels, 0, pixmap.getPixels(), pixels.length);
            PixmapIO.writePNG(Gdx.files.local("screenshot.png"), pixmap);
            pixmap.dispose();
            game.getServicesManager().share();
            table.remove();
        }), Actions.delay(duration), Actions.run(() -> {
            share.addAction(Actions.alpha(1));
            quote.setText(quoteText);
        })));
    }
}
