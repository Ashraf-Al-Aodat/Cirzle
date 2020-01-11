package co.circlewave.cirzle.gui.utils;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

public class Background extends Actor {

    private final ShapeRenderer shapeRenderer;

    private boolean animate;
    private Color top;
    private Color bottom;

    private int lineNumber;

    Background(final ShapeRenderer shapeRenderer, final float x, final float y, final float width, final float height, final Color top, final Color bottom) {
        this.shapeRenderer = shapeRenderer;
        shapeRenderer.setAutoShapeType(true);
        this.top = top;
        this.bottom = bottom;
        this.lineNumber = 0;
        animate = false;
        setName("background");
        setBounds(x, y, width, height);
        addAction(Actions.sequence(Actions.alpha(0), Actions.delay(1), Actions.alpha(1, 1)));
    }

    void startAnimation() {
        animate = true;
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.end();
        top.set(top.r, top.g, top.b, getColor().a * parentAlpha);
        bottom.set(bottom.r, bottom.g, bottom.b, getColor().a * parentAlpha);
        shapeRenderer.begin();
        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);
        shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
        shapeRenderer.rect(getX(), getY(), getWidth(), getHeight(), top, top, bottom, bottom);
        if (animate) {
            for (Actor actor : getStage().getActors().toArray()) {
                if (actor instanceof Line)
                    ((Line) actor).draw(new Color(getColor().r, getColor().g, getColor().b, 0.25f * parentAlpha));
            }
            if (lineNumber < 200 && Gdx.graphics.getFramesPerSecond() >= 60) {
                getStage().addActor(new Line(shapeRenderer, (getWidth() + getHeight()) * 0.02f));
                lineNumber++;
            }
        }
        shapeRenderer.end();
        Gdx.gl.glDisable(GL20.GL_BLEND);
        batch.begin();
    }

    void setColors(final Color top, final Color bottom) {
        this.top = new Color(top).sub(Color.GRAY);
        this.bottom = new Color(bottom).sub(Color.GRAY);
        if (this.top.toHsv(new float[3])[2] <= 0.1f) {
            this.top = new Color(top).sub(new Color(0.125f, 0.125f, 0.125f, 1));
        }
        if (this.bottom.toHsv(new float[3])[2] <= 0.1f) {
            this.bottom = new Color(bottom).sub(new Color(0.125f, 0.125f, 0.125f, 1));
        }
    }

    private class Line extends Actor {

        private final ShapeRenderer shapeRenderer;
        private final float space;

        private Vector2[] points;

        Line(final ShapeRenderer shapeRenderer, final float space) {
            this.shapeRenderer = shapeRenderer;
            this.space = space;
            this.points = new Vector2[3];
            this.points[0] = new Vector2(space * (int) (Math.random() * (Background.this.getWidth() / space)),
                    space * (int) (Math.random() * (Background.this.getHeight() / space)));
            this.points[1] = nextPoint(points[0].x, points[0].y);
            this.points[2] = nextPoint(points[1].x, points[1].y);
            addAction(Actions.forever(Actions.sequence(Actions.delay(1), Actions.run(this::shift))));
        }

        void draw(final Color color) {
            if (isVisible()) {
                shapeRenderer.setColor(color);
                shapeRenderer.set(ShapeRenderer.ShapeType.Filled);
                if (points.length > 1) shapeRenderer.line(points[0].x, points[0].y, points[1].x, points[1].y);
                if (points.length == 3) shapeRenderer.line(points[1].x, points[1].y, points[2].x, points[2].y);
            }
        }

        private Vector2 nextPoint(final float x, final float y) {
            final int dir = (int) (Math.random() * 8);
            final Vector2 point;
            if (dir == 0) {
                point = new Vector2(x - space, y);
            } else if (dir == 1) {
                point = new Vector2(x - space, y + space);
            } else if (dir == 2) {
                point = new Vector2(x, y + space);
            } else if (dir == 3) {
                point = new Vector2(x + space, y + space);
            } else if (dir == 4) {
                point = new Vector2(x + space, y);
            } else if (dir == 5) {
                point = new Vector2(x + space, y - space);
            } else if (dir == 6) {
                point = new Vector2(x, y - space);
            } else {
                point = new Vector2(x - space, y - space);
            }
            if (point.x >= 0 && point.y >= 0 && point.x <= Background.this.getWidth() && point.y <= Background.this.getHeight()
                    && points[1] != point) {
                return point;
            } else {
                return new Vector2(x, y);
            }
        }

        private void shift() {
            points[0] = points[1];
            points[1] = points[2];
            points[2] = nextPoint(points[1].x, points[1].y);
        }
    }
}
