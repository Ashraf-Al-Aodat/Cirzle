package co.circlewave.cirzle.board.component;

import co.circlewave.cirzle.board.Board;
import co.circlewave.cirzle.board.Tile;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.ArrayList;
import java.util.Objects;


public class PortalPiece extends Piece {

    private PortalPiece otherSide;

    public PortalPiece(final TextureRegion region, final Tile tile, final int piecePosition, final ArrayList<Integer> rules,
                       final Board board, final Color color) {
        super(tile, piecePosition, rules, board);
        this.otherSide = null;
        setColor(color);
        setRegion(region);
        setShadow(region);
        addAction(Actions.forever(Actions.rotateBy(-10, 0.25f)));
        setName("portal");
    }

    @Override
    public void draw(final Batch batch, final float parentAlpha) {
        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        drawPiece(batch);
        batch.setColor(0.875f, 0.875f, 0.875f, parentAlpha);
        drawRules(batch);
        batch.setColor(Color.WHITE);
    }

    public void setOtherSide(final PortalPiece otherSide) {
        this.otherSide = otherSide;
    }

    @Override
    public ArrayList<Piece> pieceEffect(final Piece fromPiece) {
        final ArrayList<Piece> matchedPiece = new ArrayList<>();
        if (fromPiece != null && fromPiece.equals(otherSide)) {
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
                    Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece() instanceof ConvertPiece &&
                    !Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().equals(fromPiece) &&
                    Objects.requireNonNull(getBoard().getDownTile(getTile(), false)).getPiece().checkRule(Piece.RULE_UP) &&
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
        } else {
            matchedPiece.add(otherSide);
        }
        return matchedPiece;
    }
}

/*
        Gdx.gl.glColorMask(false, false, false, false);
        Gdx.gl.glDepthFunc(GL30.GL_ALWAYS);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glDepthMask(true);

        getBoard().getShapeRenderer().begin(ShapeRenderer.ShapeType.Filled);
        getBoard().getShapeRenderer().setColor(0f, 0f, 0f, 0.5f);
        getBoard().getShapeRenderer().circle(getBoard().getX() + getX() + getWidth() / 2, getBoard().getY() + getY() + getWidth() / 2, getWidth() / 4, 360);
        getBoard().getShapeRenderer().end();

        batch.begin();
        Gdx.gl.glColorMask(true, true, true, true);
        Gdx.gl.glEnable(GL30.GL_DEPTH_TEST);
        Gdx.gl.glDepthFunc(GL30.GL_LESS);

        batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
        batch.draw(getBoard().getAtlas().findRegion("circle"), getX(), getY(), getOriginX(), getOriginY(),
                getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());

        batch.flush();
        Gdx.gl.glDisable(GL30.GL_DEPTH_TEST);
*/
