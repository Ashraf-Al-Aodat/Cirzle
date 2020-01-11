package co.circlewave.cirzle.board.component;

import co.circlewave.cirzle.board.Board;
import co.circlewave.cirzle.board.Tile;
import co.circlewave.cirzle.gui.utils.FrameBuffer;
import co.circlewave.cirzle.gui.utils.ShapeRenderer;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Matrix4;

import java.util.ArrayList;
import java.util.Objects;

public class ConvertPiece extends Piece {

    final private ArrayList<Color> colors;

    public ConvertPiece(final Tile tile, final int piecePosition, final ArrayList<Integer> rules, final Board board, final ArrayList<Color> colors) {
        super(tile, piecePosition, rules, board);
        this.colors = colors;
        setColor(Color.GRAY);
        setName("convert");
        setRegion(buildTextureRegion());
        setShadow(buildShadow());
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a * parentAlpha);
        drawPiece(batch);
        batch.setColor(0.875f, 0.875f, 0.875f, parentAlpha);
        drawRules(batch);
        batch.setColor(Color.WHITE);
    }

    public Color getColor(final int side) {
        return colors.get(side);
    }

    private TextureRegion buildTextureRegion() {
        final FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, 518, 518, false);

        final Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, buffer.getWidth(), buffer.getHeight());
        getBoard().getShapeRenderer().setProjectionMatrix(matrix);

        buffer.begin();

        getBoard().getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);

        float radius = buffer.getWidth() / 2f;
        float alpha = 0.03125f;
        for (int index = 0; index < 4; index++) {
            getBoard().getShapeRenderer().setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, alpha);
            getBoard().getShapeRenderer().circle(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius);
            alpha /= 0.5f;
            radius -= 1.5f;
        }

        radius = buffer.getWidth() / 2f;
        alpha = 0.125f;


        if (checkRule(RULE_LEFT) && checkRule(RULE_UP) && checkRule(RULE_RIGHT) && checkRule(RULE_DOWN)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(0)).lerp(colors.get(1), 0.5f).lerp(colors.get(2), 0.5f).lerp(colors.get(3), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(0), colors.get(0), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(1), colors.get(1), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(2), colors.get(2), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(3), colors.get(3), color);

        } else if (checkRule(RULE_LEFT) && checkRule(RULE_UP) && checkRule(RULE_RIGHT)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(0)).lerp(colors.get(1), 0.5f).lerp(colors.get(2), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(0), colors.get(0), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(1), colors.get(1), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(2), colors.get(2), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(0), colors.get(2), color);

        } else if (checkRule(RULE_UP) && checkRule(RULE_RIGHT) && checkRule(RULE_DOWN)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(1)).lerp(colors.get(2), 0.5f).lerp(colors.get(3), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(3), colors.get(1), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(1), colors.get(1), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(2), colors.get(2), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(3), colors.get(3), color);

        } else if (checkRule(RULE_RIGHT) && checkRule(RULE_DOWN) && checkRule(RULE_LEFT)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(2)).lerp(colors.get(3), 0.5f).lerp(colors.get(0), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(0), colors.get(0), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(0), colors.get(2), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(2), colors.get(2), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(3), colors.get(3), color);

        } else if (checkRule(RULE_DOWN) && checkRule(RULE_LEFT) && checkRule(RULE_UP)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(3)).lerp(colors.get(0), 0.5f).lerp(colors.get(1), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(0), colors.get(0), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(1), colors.get(1), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(3), colors.get(1), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(3), colors.get(3), color);

        } else if (checkRule(RULE_LEFT) && checkRule(RULE_RIGHT)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            getBoard().getShapeRenderer().rect(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 3.335f, buffer.getHeight() - buffer.getHeight() / 3.335f,
                    colors.get(0), colors.get(2), colors.get(2), colors.get(0));

        } else if (checkRule(RULE_UP) && checkRule(RULE_DOWN)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            getBoard().getShapeRenderer().rect(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 3.335f, buffer.getHeight() - buffer.getHeight() / 3.335f,
                    colors.get(1), colors.get(1), colors.get(3), colors.get(3));


        } else if (checkRule(RULE_LEFT) && checkRule(RULE_UP)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(0)).lerp(colors.get(1), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(0), color, color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, color, colors.get(1), color);

        } else if (checkRule(RULE_UP) && checkRule(RULE_RIGHT)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(1).r, colors.get(1).g, colors.get(1).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 225, 90);

                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(1)).lerp(colors.get(2), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, colors.get(1), color, color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(2), color, color);

        } else if (checkRule(RULE_RIGHT) && checkRule(RULE_DOWN)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(2).r, colors.get(2).g, colors.get(2).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 315, 90);

                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(2)).lerp(colors.get(3), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, color, colors.get(2), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, colors.get(3), color, color);

        } else if (checkRule(RULE_DOWN) && checkRule(RULE_LEFT)) {
            for (int index = 0; index < 4; index++) {
                getBoard().getShapeRenderer().setColor(colors.get(3).r, colors.get(3).g, colors.get(3).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 45, 90);

                getBoard().getShapeRenderer().setColor(colors.get(0).r, colors.get(0).g, colors.get(0).b, alpha);
                getBoard().getShapeRenderer().arc(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius, 135, 90);

                alpha /= 0.5f;
                radius -= 1.5f;
            }

            final Color color = new Color(colors.get(3)).lerp(colors.get(0), 0.5f);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getHeight() - buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 6.67f, buffer.getHeight() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getHeight() / 2f, color, this.colors.get(0), color);

            getBoard().getShapeRenderer().triangle(buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() - buffer.getWidth() / 6.67f, buffer.getWidth() - buffer.getWidth() / 6.67f,
                    buffer.getWidth() / 2f, buffer.getWidth() / 2f, color, colors.get(3), color);
        }

        getBoard().getShapeRenderer().end();

        buffer.end();
        buffer.dispose();

        getBoard().getShapeRenderer().setToDefaultMatrix();
        getBoard().getShapeRenderer().setColor(Color.WHITE);

        return new TextureRegion(buffer.getColorBufferTexture());
    }


    private TextureRegion buildShadow() {
        final FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, 518, 518, false);

        final Matrix4 matrix = new Matrix4();
        matrix.setToOrtho2D(0, 0, buffer.getWidth(), buffer.getHeight());
        getBoard().getShapeRenderer().setProjectionMatrix(matrix);

        buffer.begin();

        getBoard().getShapeRenderer().begin(co.circlewave.cirzle.gui.utils.ShapeRenderer.ShapeType.Filled);

        float radius = buffer.getWidth() / 2f;
        float alpha = 0.125f;
        for (int index = 0; index < 4; index++) {
            getBoard().getShapeRenderer().setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, alpha);
            getBoard().getShapeRenderer().circle(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius);
            alpha /= 0.5f;
            radius -= 1.5f;
        }

        getBoard().getShapeRenderer().end();

        buffer.end();
        buffer.dispose();

        getBoard().getShapeRenderer().setToDefaultMatrix();
        getBoard().getShapeRenderer().setColor(Color.WHITE);

        return new TextureRegion(buffer.getColorBufferTexture());
    }

    @Override
    public ArrayList<Piece> pieceEffect(final Piece fromPiece) {
        final ArrayList<Piece> matchedPiece = new ArrayList<>();
        if (getBoard().getLeftTile(getTile(), false) != null && checkRule(Piece.RULE_LEFT) &&
                !Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().checkRule(Piece.RULE_RIGHT) &&
                (Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece() instanceof ConvertPiece) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece()).getColor(Piece.RULE_RIGHT)
                        .equals(getColor(Piece.RULE_LEFT))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece());
        } else if (getBoard().getLeftTile(getTile(), false) != null && checkRule(Piece.RULE_LEFT) &&
                !Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().checkRule(Piece.RULE_RIGHT) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().getColor().equals(getColor(Piece.RULE_LEFT))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece());
        }

        if (getBoard().getUpTile(getTile(), false) != null && checkRule(Piece.RULE_UP) &&
                !Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().checkRule(Piece.RULE_DOWN) &&
                (Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece() instanceof ConvertPiece) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece()).getColor(Piece.RULE_DOWN)
                        .equals(getColor(RULE_UP))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece());
        } else if (getBoard().getUpTile(getTile(), false) != null && checkRule(Piece.RULE_UP) &&
                !Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().checkRule(Piece.RULE_DOWN) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().getColor().equals(getColor(RULE_UP))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece());
        }

        if (getBoard().getRightTile(getTile(), false) != null && checkRule(Piece.RULE_RIGHT) &&
                !Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().checkRule(Piece.RULE_LEFT) &&
                (Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece() instanceof ConvertPiece) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece()).getColor(RULE_LEFT)
                        .equals(getColor(RULE_RIGHT))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece());
        } else if (getBoard().getRightTile(getTile(), false) != null && checkRule(Piece.RULE_RIGHT) &&
                !Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().checkRule(Piece.RULE_LEFT) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().getColor().equals(getColor(RULE_RIGHT))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece());
        }

        if (getBoard().getDownTile(getTile(), false) != null &&
                checkRule(Piece.RULE_DOWN) &&
                !Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().checkRule(Piece.RULE_UP) &&
                (Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece() instanceof ConvertPiece) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece()).getColor(RULE_UP)
                        .equals(getColor(RULE_DOWN))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece());
        } else if (getBoard().getDownTile(getTile(), false) != null && checkRule(Piece.RULE_DOWN) &&
                !Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().checkRule(Piece.RULE_UP) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().getColor().equals(getColor(RULE_DOWN))) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece());
        }
        return matchedPiece;
    }
}
