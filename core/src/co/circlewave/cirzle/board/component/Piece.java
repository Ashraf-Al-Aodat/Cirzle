package co.circlewave.cirzle.board.component;

import co.circlewave.cirzle.board.Board;
import co.circlewave.cirzle.board.Tile;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.utils.ScissorStack;
import com.badlogic.gdx.utils.Align;

import java.util.ArrayList;

public abstract class Piece extends Actor {

    static public int RULE_LEFT = 0;
    static public int RULE_UP = 1;
    static public int RULE_RIGHT = 2;
    static public int RULE_DOWN = 3;
    private final Board board;
    private final Rectangle scissor;
    private final ArrayList<Integer> rules;
    private TextureRegion region;
    private TextureRegion shadow;
    private int piecePosition;
    private Tile tile;
    private boolean swap;
    private boolean linked;

    Piece(final Tile tile, final int piecePosition, final ArrayList<Integer> rules, final Board board) {
        setBounds(tile.getX() + board.getPiecePad(), tile.getY() + board.getPiecePad(),
                board.getTileSize() * 0.5f, board.getTileSize() * 0.5f);
        setOrigin(Align.center);
        this.board = board;
        this.piecePosition = piecePosition;
        this.scissor = new Rectangle(getX(), getY(), getWidth(), getHeight());
        this.rules = rules;
        this.tile = tile;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    public TextureRegion getRegion() {
        return region;
    }

    public void setRegion(final TextureRegion region) {
        this.region = region;
    }

    public TextureRegion getShadow() {
        return shadow;
    }

    public void setShadow(final TextureRegion region) {
        shadow = region;
    }

    public int getPiecePosition() {
        return this.piecePosition;
    }

    public void setPiecePosition(final int position) {
        this.piecePosition = position;
    }

    public void swap() {
        swap = !swap;
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(final Tile tile) {
        this.tile = tile;
    }

    public int getType() {
        if (getName().equals("normal")) {
            return 0;
        } else if (getName().equals("portal")) {
            return 1;
        } else {
            return -1;
        }
    }

    boolean checkRule(final int rule) {
        return !rules.contains(rule);
    }

    public boolean isLinked() {
        return linked;
    }

    public void enableLink() {
        this.linked = true;
    }

    public void disableLink() {
        this.linked = false;
    }

    public abstract ArrayList<Piece> pieceEffect(final Piece fromPiece);

    public Vector2 getCenterPoint() {
        return new Vector2(tile.getX() + tile.getWidth() / 2, tile.getY() + tile.getHeight() / 2);
    }

    public Board getBoard() {
        return board;
    }

    void drawPiece(final Batch batch) {
        if (getX() + getWidth() >= getBoard().getWidth()) {
            drawWithClippedArea(region, batch, getX(), getY(), getOriginX(), getOriginY(), getX(), getY(),
                    getBoard().getWidth() - getX(), getHeight(), getRotation());
            drawWithClippedArea(region, batch, getX() - getBoard().getWidth(), getY(), getOriginX(), getOriginY(),
                    getX() + getWidth() - getBoard().getWidth(), getY(), -((getX() + getWidth()) - getBoard().getWidth()), getHeight(), getRotation());
        } else if (getY() + getHeight() >= getBoard().getHeight()) {
            drawWithClippedArea(region, batch, getX(), getY(), getOriginX(), getOriginY(), getX(), getY(), getWidth(),
                    getBoard().getHeight() - getY(), getRotation());
            drawWithClippedArea(region, batch, getX(), getY() - getBoard().getHeight(), getOriginX(), getOriginY(),
                    getX(), 0, getWidth(), getY() + getHeight() - getBoard().getHeight(), getRotation());
        } else if (getX() <= 0) {
            drawWithClippedArea(region, batch, getX(), getY(), getOriginX(), getOriginY(), getX() + getWidth(), getY(),
                    -((getX() + getWidth())), getHeight(), getRotation());
            drawWithClippedArea(region, batch, getX() + getBoard().getWidth(), getY(), getOriginX(), getOriginY(),
                    getBoard().getWidth(), getY(), getX(), getHeight(), getRotation());
        } else if (getY() <= 0) {
            drawWithClippedArea(region, batch, getX(), getY(), getOriginX(), getOriginY(), getX(), getY() + getHeight(), getWidth(),
                    -(getY() + getHeight()), getRotation());
            drawWithClippedArea(region, batch, getX(), getY() + getBoard().getHeight(), getOriginX(), getOriginY(),
                    getX(), getBoard().getHeight(), getWidth(), getY(), getRotation());
        } else {
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    void drawRules(final Batch batch) {
        if (board != null) {
            for (final int rule : rules) {
                if (getX() + getWidth() >= board.getWidth()) {
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX(), getY(), getOriginX(), getOriginY(), getX(), getY(),
                            board.getWidth() - getX(), getHeight(), chooseRotation(rule));
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX() - board.getWidth(), getY(), getOriginX(), getOriginY(),
                            getX() + getWidth() - board.getWidth(), getY(), -((getX() + getWidth()) - board.getWidth()), getHeight(),
                            chooseRotation(rule));
                } else if (getY() + getHeight() >= board.getHeight()) {
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX(), getY(), getOriginX(), getOriginY(),
                            getX(), getY(), getWidth(), board.getHeight() - getY(), chooseRotation(rule));
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX(), getY() - board.getHeight(), getOriginX(), getOriginY(),
                            getX(), 0, getWidth(), getY() + getHeight() - board.getHeight(), chooseRotation(rule));
                } else if (getX() <= 0) {
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX(), getY(), getOriginX(), getOriginY(),
                            getX() + getWidth(), getY(), -((getX() + getWidth())), getHeight(), chooseRotation(rule));
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX() + board.getWidth(), getY(), getOriginX(), getOriginY(),
                            board.getWidth(), getY(), getX(), getHeight(), chooseRotation(rule));
                } else if (getY() <= 0) {
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX(), getY(), getOriginX(), getOriginY(), getX(),
                            getY() + getHeight(), getWidth(), -(getY() + getHeight()), chooseRotation(rule));
                    drawWithClippedArea(board.getAtlas().findRegion("rule"), batch, getX(), getY() + board.getHeight(), getOriginX(), getOriginY(),
                            getX(), board.getHeight(), getWidth(), getY(), chooseRotation(rule));
                } else {
                    batch.draw(board.getAtlas().findRegion("rule"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), chooseRotation(rule));
                }
            }
        }
    }

    public void drawShadow(final Batch batch) {
        if (getX() + getWidth() >= getBoard().getWidth()) {
            drawWithClippedArea(shadow, batch, getX(), getShadowY(), getOriginX(), getOriginY(), getX(), getShadowY(),
                    getBoard().getWidth() - getX(), getHeight(), getRotation());
            drawWithClippedArea(shadow, batch, getX() - getBoard().getWidth(), getShadowY(), getOriginX(), getOriginY(),
                    getX() + getWidth() - getBoard().getWidth(), getShadowY(), -((getX() + getWidth()) - getBoard().getWidth()), getHeight(), getRotation());
        } else if (getY() + getHeight() >= getBoard().getHeight()) {
            drawWithClippedArea(shadow, batch, getX(), getShadowY(), getOriginX(), getOriginY(), getX(), getShadowY(), getWidth(),
                    getBoard().getHeight() - getShadowY(), getRotation());
            drawWithClippedArea(shadow, batch, getX(), getShadowY() - getBoard().getHeight(), getOriginX(), getOriginY(),
                    getX(), 0, getWidth(), getShadowY() + getHeight() - getBoard().getHeight(), getRotation());
        } else if (getX() <= 0) {
            drawWithClippedArea(shadow, batch, getX(), getShadowY(), getOriginX(), getOriginY(), getX() + getWidth(), getShadowY(),
                    -((getX() + getWidth())), getHeight(), getRotation());
            drawWithClippedArea(shadow, batch, getX() + getBoard().getWidth(), getShadowY(), getOriginX(), getOriginY(),
                    getBoard().getWidth(), getShadowY(), getX(), getHeight(), getRotation());
        } else if (getY() <= 0) {
            drawWithClippedArea(shadow, batch, getX(), getShadowY(), getOriginX(), getOriginY(), getX(), getShadowY() + getHeight(), getWidth(),
                    -(getShadowY() + getHeight()), getRotation());
            drawWithClippedArea(shadow, batch, getX(), getShadowY() + getBoard().getHeight(), getOriginX(), getOriginY(),
                    getX(), getBoard().getHeight(), getWidth(), getShadowY(), getRotation());
        } else {
            batch.draw(shadow, getX(), getShadowY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        }
    }

    private float getShadowY() {
        return getY() - Gdx.graphics.getHeight() / 250f;
    }

    @Override
    public void act(final float delta) {
        super.act(delta);
        if (!swap && board != null && (board.isTouch() || tile.getTileId() != piecePosition ||
                getX() != tile.getX() || getY() != tile.getY())) {
            setPosition(tile.getX() + board.getPiecePad() + tile.getHorizontalContainer().getShift(),
                    tile.getY() + board.getPiecePad() + tile.getVerticalContainer().getShift());
            if (getX() >= board.getWidth()) {
                setPosition(tile.getX() + board.getPiecePad() + tile.getHorizontalContainer().getShift() - board.getWidth(), getY());
            }
            if (getY() >= board.getHeight()) {
                setPosition(getX(), tile.getY() + board.getPiecePad() + tile.getVerticalContainer().getShift() - board.getHeight());
            }
            if (getX() + getWidth() <= 0) {
                setPosition(tile.getX() + board.getPiecePad() + tile.getHorizontalContainer().getShift() + board.getWidth(), getY());
            }
            if (getY() + getHeight() <= 0) {
                setPosition(getX(), tile.getY() + board.getPiecePad() + tile.getVerticalContainer().getShift() + board.getHeight());
            }
            setPiecePosition(tile.getTileId());
        }
    }

    private int chooseRotation(final int rule) {
        if (rule == Piece.RULE_LEFT) {
            return 0;
        } else if (rule == Piece.RULE_UP) {
            return 270;
        } else if (rule == Piece.RULE_RIGHT) {
            return 180;
        } else if (rule == Piece.RULE_DOWN) {
            return 90;
        } else {
            return 45;
        }
    }

    private void drawWithClippedArea(final TextureRegion region, final Batch batch, final float drawPositionX, final float drawPositionY,
                                     final float originX, final float originY, final float x, final float y, final float width,
                                     final float height, final float rotation) {
        ScissorStack.calculateScissors(getStage().getCamera(), batch.getTransformMatrix(),
                new Rectangle(x, y, width, height), scissor);
        batch.flush();
        final boolean done = ScissorStack.pushScissors(scissor);
        if (done) {
            batch.draw(region, drawPositionX, drawPositionY, originX, originY, getWidth(), getHeight(), getScaleX(), getScaleY(), rotation);
            batch.flush();
            ScissorStack.popScissors();
        }
    }

    @Override
    public boolean remove() {
        if (this instanceof ConvertPiece || this instanceof NormalPiece) {
            getRegion().getTexture().dispose();
        }
        shadow.getTexture().dispose();
        return super.remove();
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Piece)) {
            return false;
        }
        final Piece otherPiece = (Piece) other;
        return this.piecePosition == otherPiece.piecePosition && this.getColor().equals(otherPiece.getColor()) &&
                this.getName().equals(((Piece) other).getName());
    }
}