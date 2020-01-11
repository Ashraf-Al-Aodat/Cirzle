package co.circlewave.cirzle.gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL30;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.Align;

public class Foreground extends Table {

    public final static int CIRCLE_SHAPE = 0;
    public final static int RECT_SHAPE = 1;
    public final static int ROUND_RECT_SHAPE = 2;

    private final ShapeRenderer shapeRenderer;
    private final Actor gap;
    private final Actor shadow;

    private int shape;
    private boolean clickable;

    public Foreground(final ShapeRenderer shapeRenderer) {
        this.shapeRenderer = shapeRenderer;
        this.gap = new Actor() {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                return null;
            }
        };
        this.shadow = new Actor() {
            @Override
            public Actor hit(float x, float y, boolean touchable) {
                if (!Foreground.this.isVisible()) {
                    return null;
                }
                if (clickable && x > gap.getX() && y > gap.getY() && x < gap.getX() + gap.getWidth() && y < gap.getY() + gap.getHeight()) {
                    return null;
                }
                return super.hit(x, y, touchable);
            }
        };
        this.shape = RECT_SHAPE;

        setSize(Gdx.graphics.getWidth(), Gdx.graphics.getHeight());
        shadow.setSize(getWidth(), getHeight());
        gap.setOrigin(Align.center);

        add(shadow);
        addActor(gap);

        gap.setBounds(0, 0, 0, 0);
    }

    public void deleteGap() {
        gap.setBounds(0, 0, 0, 0);
    }

    public void setGapShape(final int shape) {
        this.shape = shape;
    }

    public void addActionToShadow(final Action action) {
        shadow.addAction(action);
    }

    public Actor getGap() {
        return gap;
    }

    public void setClickable(boolean clickable) {
        this.clickable = clickable;
    }

    @Override
    public Actor hit(float x, float y, boolean touchable) {
        return shadow.hit(x, y, touchable);
    }

    @Override
    public boolean addListener(EventListener listener) {
        return shadow.addListener(listener);
    }

    @Override
    public void clearListeners() {
        shadow.clearListeners();
    }

    @Override
    public void setColor(final Color color) {
        shadow.setColor(color);
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        super.draw(batch, parentAlpha);
        batch.end();
        Gdx.gl.glClearDepthf(1f);
        Gdx.gl.glClear(GL30.GL_DEPTH_BUFFER_BIT);
        Gdx.gl.glColorMask(false, false, false, false);
        Gdx.gl.glDepthFunc(GL30.GL_ALWAYS);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);
        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(0f, 0f, 0f, 0.5f);
        if (shape == CIRCLE_SHAPE) {
            shapeRenderer.circle(gap.getX() + gap.getWidth() / 2, gap.getY() + gap.getWidth() / 2, gap.getWidth() / 2);
        } else if (shape == RECT_SHAPE) {
            shapeRenderer.rect(gap.getX(), gap.getY(), gap.getOriginX(), gap.getOriginY(), gap.getWidth(), gap.getHeight(),
                    gap.getScaleX(), gap.getScaleY(), gap.getRotation());
        } else {
            shapeRenderer.roundRect(gap.getX(), gap.getY(), gap.getWidth(), gap.getHeight(), (gap.getWidth() + gap.getHeight()) / 10);
        }
        shapeRenderer.end();

        shapeRenderer.begin(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.setColor(shadow.getColor().r, shadow.getColor().g, shadow.getColor().b, shadow.getColor().a * parentAlpha);
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glDepthFunc(GL30.GL_LESS);
        Gdx.gl.glEnable(GL30.GL_BLEND);
        Gdx.gl.glBlendFunc(GL30.GL_SRC_ALPHA, GL30.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight());
        shapeRenderer.end();
        Gdx.gl.glDisable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glDisable(GL30.GL_BLEND);

        batch.begin();
    }
}
