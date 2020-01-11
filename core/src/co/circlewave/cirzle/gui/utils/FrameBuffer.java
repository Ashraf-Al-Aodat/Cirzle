package co.circlewave.cirzle.gui.utils;

import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;

public class FrameBuffer extends com.badlogic.gdx.graphics.glutils.FrameBuffer {

    public FrameBuffer(Pixmap.Format format, int width, int height, boolean hasDepth) {
        super(format, width, height, hasDepth);
    }

    @Override
    protected void disposeColorTexture(Texture colorTexture) {
    }

}
