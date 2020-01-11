package co.circlewave.cirzle.board;

import co.circlewave.cirzle.board.component.Piece;
import co.circlewave.cirzle.gui.screens.PlayScene;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;

import java.util.List;
import java.util.Objects;
import java.util.Stack;

class MoveHistory {

    private final Board board;
    private final Stack<Move> history;

    private int moves;

    MoveHistory(final Board board, final int moves) {
        this.board = board;
        this.moves = moves;
        this.history = new Stack<>();
    }


    void executeMove(final int currentCoordinate, final int destinationCoordinate, final TileContainer container,
                     final int movementDirection) {
        final int steps = Math.abs(destinationCoordinate - currentCoordinate);
        if (steps != 0) {
            for (final Piece piece : container.getPieces()) {
                int counter = 0;
                Tile destinationTile = piece.getTile();
                while (counter < steps && movementDirection == Board.MOVE_RIGHT) {
                    assert destinationTile != null;
                    destinationTile = board.getRightTile(destinationTile, true);
                    counter++;
                }
                while (counter < steps / board.getCol() && movementDirection == Board.MOVE_UP) {
                    assert destinationTile != null;
                    destinationTile = board.getUpTile(destinationTile, true);
                    counter++;
                }
                while (counter < steps && movementDirection == Board.MOVE_LEFT) {
                    assert destinationTile != null;
                    destinationTile = board.getLeftTile(destinationTile, true);
                    counter++;
                }
                while (counter < steps / board.getCol() && movementDirection == Board.MOVE_DOWN) {
                    assert destinationTile != null;
                    destinationTile = board.getDownTile(destinationTile, true);
                    counter++;
                }
                Objects.requireNonNull(destinationTile).setPiece(piece);
            }
            history.push(new Move(flipMovementDirection(movementDirection), currentCoordinate, destinationCoordinate, false));
        }
    }

    void undoMove(final PlayScene playScene) {
        if (!history.empty()) {
            final Move move = history.pop();
            final float deX = board.getTile(move.currentCoordinate).getPiece().getX() -
                    board.getTile(move.destinationCoordinate).getPiece().getX();
            final float deY = board.getTile(move.currentCoordinate).getPiece().getY() -
                    board.getTile(move.destinationCoordinate).getPiece().getY();
            if (!move.special) {
                board.swap();
                final List<Piece> pieces = getMovedPiece(move).getPieces();
                for (int index = 0; index < pieces.size() - 1; index++) {
                    pieces.get(index).addAction(Actions.moveTo(pieces.get(index).getX() + deX,
                            pieces.get(index).getY() + deY, 0.25f));
                }
                pieces.get(pieces.size() - 1).addAction(Actions.sequence(Actions.moveTo(
                        pieces.get(pieces.size() - 1).getX() + deX,
                        pieces.get(pieces.size() - 1).getY() + deY, 0.25f), Actions.run(() -> {
                    executeMove(move.destinationCoordinate, move.currentCoordinate, getMovedPiece(move),
                            move.movementDirection);
                    history.pop();
                    board.swap();
                    board.checkSign();
                })));
            } else {
                board.swap();
                executeSwap(move.destinationCoordinate, move.currentCoordinate, playScene);
                history.pop();
            }
        }
    }

    void executeSwap(final int currentCoordinate, final int destinationCoordinate, final PlayScene playScene) {
        final Tile sourceTile = board.getTile(currentCoordinate);
        final Tile destinationTile = board.getTile(destinationCoordinate);
        final Piece currentPiece = sourceTile.getPiece();
        final Piece otherPiece = destinationTile.getPiece();
        currentPiece.addAction(Actions.sequence(Actions.moveTo(destinationTile.getX() + board.getPiecePad(),
                destinationTile.getY() + board.getPiecePad(), 0.1f), Actions.run(() -> otherPiece.addAction(Actions.sequence(Actions.moveTo(sourceTile.getX() + board.getPiecePad(),
                sourceTile.getY() + board.getPiecePad(), 0.1f), Actions.run(() -> {
            destinationTile.setPiece(currentPiece);
            currentPiece.setTile(destinationTile);
            sourceTile.setPiece(otherPiece);
            otherPiece.setTile(sourceTile);
            board.swap();
            board.checkSign();
            if (board.win()) {
                playScene.winAnimation();
            }
        }))))));
        history.push(new Move(-1, currentCoordinate, destinationCoordinate, true));
    }

    void addMove() {
        if (!history.empty()) {
            if (history.peek().special) {
                moves += 2;
            } else {
                moves++;
            }
        }
    }

    void deleteMove() {
        if (!history.empty()) {
            if (history.peek().special) {
                moves -= 2;
            } else {
                moves--;
            }
        }
    }

    int getMoves() {
        return moves;
    }

    private int flipMovementDirection(final int movementDirection) {
        if (movementDirection == Board.MOVE_LEFT) {
            return Board.MOVE_RIGHT;
        } else if (movementDirection == Board.MOVE_UP) {
            return Board.MOVE_DOWN;
        } else if (movementDirection == Board.MOVE_RIGHT) {
            return Board.MOVE_LEFT;
        } else {
            return Board.MOVE_UP;
        }
    }

    private TileContainer getMovedPiece(final Move move) {
        if (move.movementDirection == Board.MOVE_LEFT || move.movementDirection == Board.MOVE_RIGHT) {
            return board.getTile(move.destinationCoordinate).getHorizontalContainer();
        } else {
            return board.getTile(move.destinationCoordinate).getVerticalContainer();
        }
    }

    private class Move {

        private final int movementDirection;
        private final int currentCoordinate;
        private final int destinationCoordinate;
        private final boolean special;

        Move(final int movementDirection, final int currentCoordinate, final int destinationCoordinate, final boolean special) {
            this.movementDirection = movementDirection;
            this.currentCoordinate = currentCoordinate;
            this.destinationCoordinate = destinationCoordinate;
            this.special = special;
        }
    }
}
