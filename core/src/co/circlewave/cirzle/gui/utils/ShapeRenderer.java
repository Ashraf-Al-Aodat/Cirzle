package co.circlewave.cirzle.gui.utils;

import com.badlogic.gdx.math.Matrix4;

public class ShapeRenderer extends com.badlogic.gdx.graphics.glutils.ShapeRenderer {

    /**
     * Draws a rectangle with rounded corners of the given radius.
     */

    private final Matrix4 matrix;

    public ShapeRenderer(final Matrix4 matrix4) {
        super();
        this.matrix = matrix4;
        setToDefaultMatrix();
    }

    public void roundRect(final float x, final float y, final float width, final float height, final float radius) {
        // Central rectangle
        super.rect(x, y + radius, width, height - 2 * radius);

        // Four side rectangles, in clockwise order
        super.rect(x + radius, y, width - 2 * radius, radius); // down
        // super.rect(x + width - radius, y + radius, radius, height - 2*radius); // lift
        super.rect(x + radius, y + height - radius, width - 2 * radius, radius); // up
        // super.rect(x, y + radius, radius, height - 2*radius); // right

        // arches, in clockwise order
        super.arc(x + radius, y + radius, radius, 180f, 90f); // down-lift
        super.arc(x + width - radius, y + radius, radius, 270f, 90f); // down-right
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f); // up-right
        super.arc(x + radius, y + height - radius, radius, 90f, 90f); // up left
    }

    /**
     * Draws a rectangle with top rounded corners of the given radius.
     */
    public void topRoundedRect(final float x, final float y, final float width, final float height, final float radius) {
        // Central rectangle
        super.rect(x, y, width, height - radius);

        // Four side rectangles, in clockwise order
        super.rect(x + radius, y + height - radius, width - 2 * radius, radius); // up

        // arches, in clockwise order
        super.arc(x + width - radius, y + height - radius, radius, 0f, 90f); // up-right
        super.arc(x + radius, y + height - radius, radius, 90f, 90f); // up left
    }

    public void setToDefaultMatrix() {
        setProjectionMatrix(matrix);
    }
}
