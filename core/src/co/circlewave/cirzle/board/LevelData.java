package co.circlewave.cirzle.board;

import co.circlewave.cirzle.board.component.*;
import com.badlogic.gdx.graphics.Color;

import java.util.ArrayList;
import java.util.Arrays;

public class LevelData {

    private int row;
    private int coll;

    private int name;

    private ArrayList<Integer> pieceTypes;
    private ArrayList<Color[]> pieceColors;
    private ArrayList<ArrayList<Integer>> pieceRules;
    private ArrayList<Integer> portalLinks;


    private ArrayList<Integer> signPositions;
    private ArrayList<Color> signColors;

    public LevelData() {
    }

    private Color getPieceColor(final int index) {
        return pieceColors.get(index)[0];
    }

    private ArrayList<Color> getPieceColors(final int index) {
        return new ArrayList<>(Arrays.asList(pieceColors.get(index)));
    }


    private ArrayList<Integer> getPieceRules(final int index) {
        return pieceRules.get(index);
    }

    Piece getPiece(final int index, final Board board) {
        if (getPieceType(index) == 0) {
            return new NormalPiece(board.getTile(index), index, getPieceColor(index), board, getPieceRules(index));
        } else if (getPieceType(index) == 1) {
            return new PortalPiece(board.getAtlas().findRegion("spiral"),
                    board.getTile(index), index, getPieceRules(index), board, getPieceColor(index));
        } else if (getPieceType(index) == 2) {
            return new ConvertPiece(board.getTile(index), index, getPieceRules(index), board, getPieceColors(index));
        } else {
            return null;
        }
    }


    int getOtherSide(final int index) {
        return portalLinks.get(index);
    }

    private int getPieceType(final int index) {
        return pieceTypes.get(index);
    }

    int getPieceCount() {
        return pieceTypes.size();
    }

    private Color getSignColor(final int index) {
        return signColors.get(index);
    }

    ArrayList<Sign> getAllSigns(final Board board) {
        ArrayList<Sign> signs = new ArrayList<>();
        for (int index = 0; index < signPositions.size(); index++) {
            signs.add(new Sign(board.getTile(signPositions.get(index)), getSignColor(index), board.getAtlas()));
        }
        return signs;
    }

    public int getName() {
        return name;
    }

    int getRow() {
        return row;
    }

    int getColl() {
        return coll;
    }
}

