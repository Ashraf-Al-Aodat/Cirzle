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

public class NormalPiece extends Piece {

    public NormalPiece(final Tile tile, final int piecePosition, final Color color, final Board board, final ArrayList<Integer> rules) {
        super(tile, piecePosition, rules, board);
        setColor(color);
        setName("normal");
        setRegion(buildTextureRegion());
        setShadow(buildShadow());
    }

    @Override
    public ArrayList<Piece> pieceEffect(final Piece fromPiece) {
        final ArrayList<Piece> matchedPiece = new ArrayList<>();
        if (getBoard().getLeftTile(getTile(), false) != null &&
                checkRule(Piece.RULE_LEFT) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece() instanceof ConvertPiece &&
                !Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().checkRule(Piece.RULE_RIGHT) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece())
                        .getColor(Piece.RULE_RIGHT).equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece());
        } else if (getBoard().getLeftTile(getTile(), false) != null &&
                checkRule(Piece.RULE_LEFT) &&
                !Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().checkRule(Piece.RULE_RIGHT) &&
                Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece().getColor().equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getLeftTile(getTile(), false)).getPiece());
        }


        if (getBoard().getUpTile(getTile(), false) != null &&
                checkRule(Piece.RULE_UP) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece() instanceof ConvertPiece &&
                !Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().checkRule(Piece.RULE_DOWN) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece())
                        .getColor(Piece.RULE_DOWN).equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece());
        } else if (getBoard().getUpTile(getTile(), false) != null &&
                checkRule(Piece.RULE_UP) &&
                !Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().checkRule(Piece.RULE_DOWN) &&
                Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece().getColor().equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getUpTile(getTile(), false)).getPiece());
        }


        if (getBoard().getRightTile(getTile(), false) != null &&
                checkRule(Piece.RULE_RIGHT) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece() instanceof ConvertPiece &&
                !Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().checkRule(Piece.RULE_LEFT) &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece())
                        .getColor(Piece.RULE_LEFT).equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece());
        } else if (getBoard().getRightTile(getTile(), false) != null &&
                checkRule(Piece.RULE_RIGHT) &&
                !Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().checkRule(Piece.RULE_LEFT) &&
                Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece().getColor().equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getRightTile(getTile(), false)).getPiece());
        }


        if (getBoard().getDownTile(getTile(), false) != null &&
                checkRule(Piece.RULE_DOWN) &&
                !Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().checkRule(Piece.RULE_UP) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece() instanceof ConvertPiece &&
                ((ConvertPiece) Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece())
                        .getColor(Piece.RULE_UP).equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece());
        } else if (getBoard().getDownTile(getTile(), false) != null &&
                checkRule(Piece.RULE_DOWN) &&
                !Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().equals(fromPiece) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().checkRule(Piece.RULE_UP) &&
                Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().getColor().equals(getColor())) {
            matchedPiece.add(Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece());
        }

        return matchedPiece;
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.setColor(Color.WHITE.r, Color.WHITE.g, Color.WHITE.b, Color.WHITE.a * parentAlpha);
        drawPiece(batch);
        batch.setColor(0.875f, 0.875f, 0.875f, parentAlpha);
        drawRules(batch);
        batch.setColor(Color.WHITE);
    }

    private TextureRegion buildTextureRegion() {
        final FrameBuffer buffer = new FrameBuffer(Pixmap.Format.RGBA8888, 518, 518, false);

        final Matrix4 temp = new Matrix4();
        temp.setToOrtho2D(0, 0, buffer.getWidth(), buffer.getHeight());

        getBoard().getShapeRenderer().setProjectionMatrix(temp);
        getBoard().getShapeRenderer().setColor(getColor());

        buffer.begin();

        getBoard().getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);

        float radius = buffer.getWidth() / 2f;
        float alpha = 0.125f;
        for (int index = 0; index < 4; index++) {
            getBoard().getShapeRenderer().setColor(getColor().r, getColor().g, getColor().b, alpha);
            getBoard().getShapeRenderer().circle(buffer.getWidth() / 2f, buffer.getHeight() / 2f, radius);
            alpha /= 0.5f;
            radius -= 1.5f;
        }

        getBoard().getShapeRenderer().end();

        buffer.end();
        buffer.dispose();

        getBoard().getShapeRenderer().setToDefaultMatrix();

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
    public boolean equals(final Object other) {
        return this == other || other instanceof NormalPiece && (super.equals(other));
    }
}