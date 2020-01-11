package co.circlewave.cirzle.board;

import co.circlewave.cirzle.board.component.Piece;

import java.util.ArrayList;
import java.util.List;

public class TileContainer {

    private final List<Tile> tiles;

    private float shift;

    TileContainer() {
        this.tiles = new ArrayList<>();
        this.shift = 0;
    }

    public float getShift() {
        return shift;
    }

    public void setShift(final float shift) {
        this.shift = shift;
    }

    void addTile(final Tile tile) {
        this.tiles.add(tile);
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    List<Piece> getPieces() {
        final List<Piece> pieces = new ArrayList<>();
        for (Tile tile : tiles) {
            pieces.add(tile.getPiece());
        }
        return pieces;
    }
}
