package co.circlewave.cirzle.board.component;

import co.circlewave.cirzle.board.Tile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.utils.Align;

public class Sign extends Actor {

    private final TextureRegion region;
    private final Tile tile;

    private boolean link;

    public Sign(final Tile tile, final Color color, final TextureAtlas atlas) {
        setColor(color);
        setBounds(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight());
        setOrigin(Align.center);
        setScale(-0.6f);
        addAction(Actions.sequence(Actions.delay((float) Math.random()), Actions.run(this::startAnimation)));
        this.region = atlas.findRegion("donut");
        this.tile = tile;
        this.link = false;
    }

    public boolean isPieceColorMatched() {
        final boolean match = getColor().equals(tile.getPiece().getColor());
        if (!match && getActions().isEmpty()) {
            startAnimation();
        }
        return match;
    }

    public void link() {
        link = true;
    }

    public void unlink() {
        link = false;
    }

    public boolean isLink() {
        return link;
    }

    public Tile getTile() {
        return tile;
    }

    private void startAnimation() {
        addAction(Actions.sequence(Actions.scaleBy(-0.1f, -0.1f, 0.25f),
                Actions.scaleBy(0.1f, 0.1f, 0.5f), Actions.run(() -> {
                    if (!getColor().equals(tile.getPiece().getColor())) {
                        startAnimation();
                    }
                })));
    }

    @Override
    public void act(float delta) {
        super.act(delta);
        toBack();
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return null;
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.setColor(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, Color.DARK_GRAY.a * parentAlpha);
        batch.draw(region, getX(), getShadowY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
        batch.setColor(Color.WHITE);
    }

    private float getShadowY() {
        return getY() - getParent().getWidth() / 250;
    }

    @Override
    public boolean equals(final Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Sign)) {
            return false;
        }
        final Sign otherSign = (Sign) other;
        return this.tile.equals(otherSign.tile);
    }
}
