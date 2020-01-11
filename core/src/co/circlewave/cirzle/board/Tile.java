package co.circlewave.cirzle.board;

import co.circlewave.cirzle.board.component.Piece;
import co.circlewave.cirzle.board.component.Sign;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.utils.ActorGestureListener;
import com.badlogic.gdx.utils.Align;

public class Tile extends Actor {

    private final int tileId;
    private final TileContainer horizontalContainer;
    private final TileContainer verticalContainer;
    private final TextureAtlas atlas;

    private Sign sign;
    private Piece piece;
    private boolean touch;
    private boolean direction;

    Tile(final int tileId, final TextureAtlas atlas, final TileContainer horizontalContainer, final TileContainer verticalContainer, final float x,
         final float y, final float size) {
        this.tileId = tileId;
        this.atlas = atlas;
        this.horizontalContainer = horizontalContainer;
        this.verticalContainer = verticalContainer;
        setBounds(x, y, size, size);
        setOrigin(Align.center);
        addAction(Actions.alpha(0.2f));
        addListener(new ActorGestureListener() {

            private float startX;
            private float startY;
            private boolean fistTime;

            @Override
            public void touchDown(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0) {
                    startX = Gdx.input.getX();
                    startY = Gdx.input.getY();
                    fistTime = true;
                    touch = true;
                }
            }


            @Override
            public void pan(InputEvent event, float x, float y, float deltaX, float deltaY) {
                if (fistTime) {
                    direction = Math.abs(startX - Gdx.input.getX()) > Math.abs(startY - Gdx.input.getY());
                    fistTime = false;
                }
            }

            @Override
            public void touchUp(InputEvent event, float x, float y, int pointer, int button) {
                if (pointer == 0) {
                    touch = false;
                    horizontalContainer.setShift(0);
                    verticalContainer.setShift(0);
                }
            }
        });
    }

    public Piece getPiece() {
        return this.piece;
    }

    void setPiece(final Piece piece) {
        this.piece = piece;
        piece.setPiecePosition(tileId);
        piece.setTile(this);
    }

    public int getTileId() {
        return tileId;
    }

    public TileContainer getHorizontalContainer() {
        return horizontalContainer;
    }

    public TileContainer getVerticalContainer() {
        return verticalContainer;
    }

    public TileContainer getContainer() {
        if (direction) {
            return horizontalContainer;
        } else {
            return verticalContainer;
        }
    }

    boolean hasASign() {
        return sign != null;
    }

    public Sign getSign() {
        return sign;
    }

    void setSign(final Sign sign) {
        this.sign = sign;
    }

    public boolean isTouch() {
        return touch;
    }

    public boolean isTouch(final Piece point) {
        return point.getX() + point.getWidth() / 2 > getX() &&
                point.getY() + point.getHeight() / 2 > getY() &&
                point.getX() + point.getWidth() / 2 < getX() + getWidth() &&
                point.getY() + point.getHeight() / 2 < getY() + getHeight();
    }

    public void setHovering(final boolean stat) {
        if (stat) {
            addAction(Actions.alpha(0.8f));
        } else {
            addAction(Actions.alpha(0.2f));
        }
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        batch.draw(atlas.findRegion("tile"), getX(), getY(), getWidth(), getHeight());
        batch.setColor(Color.WHITE);
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Tile)) {
            return false;
        }
        final Tile otherTile = (Tile) other;
        return this.tileId == otherTile.tileId && this.piece.equals(otherTile.getPiece());
    }
}
