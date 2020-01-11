package co.circlewave.cirzle.board;

import co.circlewave.cirzle.board.component.ConvertPiece;
import co.circlewave.cirzle.board.component.Piece;
import co.circlewave.cirzle.board.component.PortalPiece;
import co.circlewave.cirzle.board.component.Sign;
import co.circlewave.cirzle.gui.screens.PlayScene;
import co.circlewave.cirzle.gui.utils.ShapeRenderer;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.Group;
import com.badlogic.gdx.scenes.scene2d.actions.Actions;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Align;
import com.badlogic.gdx.utils.Base64Coder;
import com.badlogic.gdx.utils.Json;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public final class Board extends Group {


    public static int MOVE_LEFT = 0;
    public static int MOVE_UP = 1;
    public static int MOVE_RIGHT = 2;
    public static int MOVE_DOWN = 3;
    private final LevelData data;
    private final List<Tile> gameBoard;
    private final List<Sign> allSignOnBoard;
    private final ShapeRenderer shapeRenderer;
    private final Batch batch;
    private final List<TileContainer> rowContainers;
    private final List<TileContainer> colContainers;
    private final TextureAtlas atlas;
    private final MoveHistory history;
    private final ArrayList<ArrayList<Piece>> paths;
    private final int tileNumber;
    private final int row;
    private final int col;
    private final float tileSize;
    private final float piecePad;
    private int movementDirection;
    private boolean touch;

    public Board(final String name, final int levelNumber, final float width, final float height, final TextureAtlas atlas,
                 final ShapeRenderer shapeRenderer, final Batch batch) {
        this.atlas = atlas;
        this.shapeRenderer = shapeRenderer;
        this.batch = batch;
        this.data = loadLevel(levelNumber);
        this.row = data.getRow();
        this.col = data.getColl();
        this.tileNumber = row * col;
        this.tileSize = (width - ((width / 100f) * 2)) / 6;
        this.piecePad = tileSize * 0.25f;
        setBounds(width * 0.5f - tileSize * col / 2, height * 0.5f - tileSize * row / 2,
                tileSize * col, tileSize * row);
        setOrigin(Align.center);
        setName(name);
        this.rowContainers = new ArrayList<>();
        this.colContainers = new ArrayList<>();
        for (int index = 0; index < row; index++) {
            rowContainers.add(new TileContainer());
        }
        for (int index = 0; index < col; index++) {
            colContainers.add(new TileContainer());
        }
        this.allSignOnBoard = new ArrayList<>();
        this.gameBoard = new ArrayList<>();
        this.paths = new ArrayList<>();
        this.history = new MoveHistory(this, 0/*levelData.getMoves()*/);
        fillBoard();
        drawBoard();
        checkSign();
    }

    public void checkSign() {
        for (final Sign sign : allSignOnBoard) {
            sign.unlink();
        }
        paths.clear();
        removeLines();
        for (Sign sign : allSignOnBoard) {
            checkBridge(sign);
        }
    }

    private void checkBridge(final Sign sign) {
        if (sign.isPieceColorMatched() && !sign.isLink()) {
            final ArrayList<Piece> path = new ArrayList<>();
            final PathFinder pathFinder = new PathFinder(sign);
            PathFinder.Node<Piece> node = pathFinder.getData();
            if (node != null) {
                sign.link();
                path.add(node.getData());
                while (node.hasParent()) {
                    if (!(node.getParent().getData() instanceof PortalPiece) || !(node.getData() instanceof PortalPiece)) {
                        final Line line = new Line(node.getData().getCenterPoint(), node.getParent().getData().getCenterPoint(),
                                (Gdx.graphics.getWidth() + Gdx.graphics.getHeight()) / 150f, getLineColor(node),
                                node.getData().getRegion(), node.getParent().getData().getRegion());
                        addActor(line);
                        line.toBack();
                    }
                    if (pathFinder.getType()) {
                        node.getData().enableLink();
                    }
                    node = node.getParent();
                    path.add(node.getData());
                }
                if (node.getData().getTile().hasASign()) node.getData().getTile().getSign().link();
            }
            paths.add(path);
        }
    }

    private Color getLineColor(PathFinder.Node<Piece> node) {
        if (node.getData() instanceof ConvertPiece && node.getParent().getData() instanceof ConvertPiece) {
            if (getLeftTile(node.getData().getTile(), false) != null &&
                    Objects.requireNonNull(getLeftTile(node.getData().getTile(), false)).getPiece().equals(node.getParent().getData())) {
                return ((ConvertPiece) node.getData()).getColor(Piece.RULE_LEFT);
            } else if (getUpTile(node.getData().getTile(), false) != null &&
                    Objects.requireNonNull(getUpTile(node.getData().getTile(), false)).getPiece().equals(node.getParent().getData())) {
                return ((ConvertPiece) node.getData()).getColor(Piece.RULE_UP);
            } else if (getRightTile(node.getData().getTile(), false) != null &&
                    Objects.requireNonNull(getRightTile(node.getData().getTile(), false)).getPiece().equals(node.getParent().getData())) {
                return ((ConvertPiece) node.getData()).getColor(Piece.RULE_RIGHT);
            } else if (getDownTile(node.getData().getTile(), false) != null &&
                    Objects.requireNonNull(getDownTile(node.getData().getTile(), false)).getPiece().equals(node.getParent().getData())) {
                return ((ConvertPiece) node.getData()).getColor(Piece.RULE_DOWN);
            }
        } else if (node.getData() instanceof ConvertPiece) {
            return node.getParent().getData().getColor();
        }
        return node.getData().getColor();
    }

    private void removeLines() {
        for (Actor actor : getChildren()) {
            if (actor instanceof Line) {
                actor.addAction(Actions.removeActor());
            }
        }
    }

    private void fillBoard() {

        int index = 0;
        int col = 0;
        int row = 0;

        while (index < tileNumber) {
            if (col == this.col) {
                col = 0;
                row++;
            }
            final Tile tile = new Tile(index, atlas, rowContainers.get(row), colContainers.get(col),
                    tileSize * col, tileSize * row, tileSize);
            gameBoard.add(tile);
            rowContainers.get(row).addTile(tile);
            colContainers.get(col).addTile(tile);
            index++;
            col++;
        }

        for (index = 0; index < data.getPieceCount(); index++) {
            gameBoard.get(index).setPiece(data.getPiece(index, this));
        }

        for (Sign sign : data.getAllSigns(this)) {
            gameBoard.get(sign.getTile().getTileId()).setSign(sign);
            allSignOnBoard.add(sign);
        }

        index = 0;
        for (Tile tile : gameBoard) {
            if (tile.getPiece().getType() == 1) {
                ((PortalPiece) tile.getPiece()).setOtherSide(((PortalPiece) gameBoard.get(data.getOtherSide(index)).getPiece()));
                index++;
            }
        }
    }

    public void touch() {
        touch = true;
    }

    public void unTouch() {
        touch = false;
    }

    public boolean isTouch() {
        return touch;
    }

    public int getTouchTileId(final Piece point) {
        for (Tile tile : gameBoard) {
            if (tile.isTouch(point)) {
                return tile.getTileId();
            }
        }
        return -1;
    }

    public Tile getTile(final int tileId) {
        return this.gameBoard.get(tileId);
    }

    public void swapping(final Tile sourceTile, final PlayScene playScene) {
        history.executeSwap(sourceTile.getTileId(), getTouchTileId(sourceTile.getPiece()), playScene);
        history.addMove();
    }

    public void swap() {
        for (Piece piece : getAllPieces()) {
            piece.swap();
        }
    }

    public void undo(final PlayScene playScene) {
        history.deleteMove();
        history.undoMove(playScene);
    }

    public void moving(final Tile sourceTile) {
        if (!(sourceTile.getTileId() == getTouchTileId(sourceTile.getPiece()))) {
            history.executeMove(sourceTile.getTileId(), getTouchTileId(sourceTile.getPiece()), sourceTile.getContainer(),
                    movementDirection);
            history.addMove();
        }
    }

    private void drawBoard() {
        for (final Tile tile : gameBoard) {
            if (tile.hasASign()) {
                addActor(tile.getSign());
            }
            addActor(tile);
        }
        for (final Piece piece : getAllPieces()) {
            addActor(piece);
        }
    }

    @Override
    public void draw(Batch batch, float parentAlpha) {
        applyTransform(batch, computeTransform());
        batch.setColor(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, Color.DARK_GRAY.a * parentAlpha);
        for (final Piece piece : getAllPieces()) {
            piece.drawShadow(batch);
        }
        resetTransform(batch);
        batch.setColor(Color.WHITE);
        super.draw(batch, parentAlpha);
    }

    public List<Piece> getAllPieces() {
        List<Piece> pieces = new ArrayList<>();
        for (Tile tile : gameBoard) {
            pieces.add(tile.getPiece());
        }
        return pieces;
    }

    public void unlinkPieces() {
        for (final Piece piece : getAllPieces()) {
            piece.disableLink();
        }
    }

    public int getMoves() {
        return history.getMoves();
    }

    public int getMovementDirection() {
        return movementDirection;
    }

    public void setMovementDirection(final int direction) {
        movementDirection = direction;
    }

    public int getRow() {
        return row;
    }

    int getCol() {
        return col;
    }

    public float getTileSize() {
        return tileSize;
    }

    public float getPiecePad() {
        return piecePad;
    }

    public Tile getLeftTile(final Tile tile, final boolean allowWarp) {
        if (tile.getTileId() % col == 0) {
            if (allowWarp) return getTile(tile.getTileId() + col - 1);
            else return null;
        }
        return getTile(tile.getTileId() - 1);
    }

    public Tile getRightTile(final Tile tile, final boolean allowWarp) {
        if ((tile.getTileId() + 1) % col == 0) {
            if (allowWarp) return getTile(tile.getTileId() - col + 1);
            else return null;
        }
        return getTile(tile.getTileId() + 1);
    }

    public Tile getUpTile(final Tile tile, final boolean allowWarp) {
        if ((tile.getTileId() + col) >= tileNumber) {
            if (allowWarp) return getTile(tile.getTileId() + col - tileNumber);
            else return null;
        }
        return getTile(tile.getTileId() + col);
    }

    public Tile getDownTile(final Tile tile, final boolean allowWarp) {
        if ((tile.getTileId() - col) < 0) {
            if (allowWarp) return getTile(tileNumber + tile.getTileId() - col);
            else return null;
        }
        return getTile(tile.getTileId() - col);
    }

    public TextureAtlas getAtlas() {
        return atlas;
    }

    public ShapeRenderer getShapeRenderer() {
        return shapeRenderer;
    }

    public Batch getBatch() {
        return batch;
    }

    public boolean win() {
        boolean win = true;
        for (Piece piece : getAllPieces()) {
            if (!piece.isLinked()) {
                win = false;
                break;
            }
        }
        if (win && findActor("TutorialPoint") != null) findActor("TutorialPoint").addAction(Actions.removeActor());
        return win;
    }

    public void winAnimation(final float duration) {
        for (ArrayList<Piece> pieces : paths) {
            new Follower((getParent().getWidth() + getParent().getHeight()) / 150, duration, pieces, this);
            new Follower((getParent().getWidth() + getParent().getHeight()) / 150, duration, invertArray(pieces), this);
        }
    }

    private ArrayList<Piece> invertArray(final ArrayList<Piece> pieces) {
        final Piece[] invert = pieces.toArray(new Piece[0]);
        for (int index = 0; index < invert.length / 2; index++) {
            final Piece temp = invert[index];
            invert[index] = invert[invert.length - 1 - index];
            invert[invert.length - 1 - index] = temp;
        }
        return new ArrayList<>(Arrays.asList(invert));
    }

    private LevelData loadLevel(final int name) {
        return new Json().fromJson(LevelData.class, Base64Coder.decodeString(
                //"eyJjb2xsIjozLCJuYW1lIjo1NCwicGllY2VDb2xvcnMiOlt7ImEiOjEsImIiOjAuMjU4ODIzNTQsImciOjEsInIiOjAuMDExNzY0NzA2fSx7ImEiOjEsImIiOjAuMjU4ODIzNTQsImciOjEsInIiOjAuMDExNzY0NzA2fSx7ImEiOjEsInIiOjF9LHsiYSI6MSwiYiI6MSwiZyI6MC4xOTIxNTY4NywiciI6MC4wMTE3NjQ3MDZ9LHsiYSI6MSwiciI6MX0seyJhIjoxLCJiIjoxLCJnIjowLjE5MjE1Njg3LCJyIjowLjAxMTc2NDcwNn0seyJhIjoxLCJiIjoxLCJnIjowLjE5MjE1Njg3LCJyIjowLjAxMTc2NDcwNn0seyJhIjoxLCJiIjowLjI1ODgyMzU0LCJnIjoxLCJyIjowLjAxMTc2NDcwNn0seyJhIjoxLCJiIjowLjI1ODgyMzU0LCJnIjoxLCJyIjowLjAxMTc2NDcwNn1dLCJwaWVjZVJ1bGVzIjpbW10sW10sW10sW10sW10sW10sW10sW10sW11dLCJwaWVjZVR5cGVzIjpbMSwwLDAsMCwwLDAsMCwwLDFdLCJwb3J0YWxMaW5rcyI6WzgsMF0sInJvdyI6Mywic2lnbkNvbG9ycyI6W3siYSI6MSwiYiI6MC4yNTg4MjM1NCwiZyI6MSwiciI6MC4wMTE3NjQ3MDZ9LHsiYSI6MSwiYiI6MSwiZyI6MC4xOTIxNTY4NywiciI6MC4wMTE3NjQ3MDZ9LHsiYSI6MSwiciI6MX0seyJhIjoxLCJiIjoxLCJnIjowLjE5MjE1Njg3LCJyIjowLjAxMTc2NDcwNn0seyJhIjoxLCJyIjoxfSx7ImEiOjEsImIiOjAuMjU4ODIzNTQsImciOjEsInIiOjAuMDExNzY0NzA2fV0sInNpZ25Qb3NpdGlvbnMiOlsxLDIsMyw0LDYsN119"
                Gdx.files.local("levels/" + name + ".json").readString()
        ));
    }

    public Color[] getFrequentColors() {
        final ArrayList<Color> colors = new ArrayList<>();
        for (final Piece piece : getAllPieces()) {
            if (!colors.contains(piece.getColor())) {
                colors.add(piece.getColor());
            }
        }
        final int[] bins = new int[colors.size()];
        for (int i = 0; i < bins.length; i++) {
            for (final Piece piece : getAllPieces()) {
                if (colors.get(i).equals(piece.getColor())) {
                    bins[i]++;
                }
            }
        }
        int max = -1;
        int i = -1;
        int j = -1;
        for (int index = 0; index < bins.length; index++) {
            if (max < bins[index]) {
                max = bins[index];
                i = index;
            }
        }
        bins[i] = -1;
        max = -1;
        for (int index = 0; index < bins.length; index++) {
            if (max < bins[index]) {
                max = bins[index];
                j = index;
            }
        }
        if (j < 0) {
            j = i;
        }
        return new Color[]{new Color(colors.get(i)),
                new Color(colors.get(j))};
    }

    public void showTutorialPoint(final Actor gap, final int[] startPositions, final int[] endPositions, final Color[] colors) {
        addActor(new TutorialPoint(startPositions, endPositions, colors, gap, atlas.findRegion("tutorial")));
    }

    private class TutorialPoint extends Image {

        private final int[] startPositions;
        private final int[] endPositions;
        private final Color[] colors;
        private final Actor gap;

        private int index;

        TutorialPoint(final int[] startPositions, final int[] endPositions, final Color[] colors, final Actor gap, final TextureRegion region) {
            super(region);
            this.gap = gap;
            this.startPositions = startPositions;
            this.endPositions = endPositions;
            this.colors = colors;
            this.index = 0;
            setName("TutorialPoint");
            setSize(getTile(0).getPiece().getWidth(), getTile(0).getPiece().getHeight());
            setPosition(getVectorPoint(startPositions[index]).x, getVectorPoint(startPositions[index]).y);
            addAction(Actions.alpha(0));
            run();
            toFront();
        }

        @Override
        public Actor hit(float x, float y, boolean touchable) {
            return null;
        }

        void run() {
            if (index < endPositions.length) {
                addAction(Actions.sequence(Actions.moveTo(getVectorPoint(startPositions[index]).x, getVectorPoint(startPositions[index]).y),
                        Actions.fadeIn(0.1f), Actions.moveTo(getVectorPoint(endPositions[index]).x,
                                getVectorPoint(endPositions[index]).y, 0.5f), Actions.fadeOut(0.1f), Actions.run(() -> {
                            if (getTile(endPositions[index]).getPiece().getColor().equals(colors[index])) {
                                update();
                                if (index < colors.length)
                                    gap.addAction(Actions.parallel(Actions.moveTo(screenX(startPositions[index]), screenY(startPositions[index]), 0.1f),
                                            Actions.sizeTo(Math.abs((screenX(endPositions[index]) + tileSize) - screenX(startPositions[index])),
                                                    Math.abs((screenY(endPositions[index]) + tileSize) - screenY(startPositions[index])), 0.1f)));
                            }
                            if (index >= endPositions.length) getActions().clear();
                            run();
                        })));
            }
        }

        private float screenX(final int index) {
            return Board.this.getX() + getTile(index).getX();
        }

        private float screenY(final int index) {
            return Board.this.getY() + getTile(index).getY();
        }

        private Vector2 getVectorPoint(final int id) {
            return new Vector2(getTile(id).getX() + getTileSize() / 2 - getPiecePad(),
                    getTile(id).getY() + getTileSize() / 2 - getPiecePad());
        }

        @Override
        public void act(float delta) {
            super.act(delta);
            if (touch && isVisible()) {
                pause();
            } else if (!touch && !isVisible()) {
                resume();
            }
        }

        void pause() {
            setVisible(false);
        }

        void resume() {
            setVisible(true);
        }

        private void update() {
            pause();
            index++;
            run();
            resume();
        }

    }

    private class Follower extends Actor {

        private final Board board;
        private final float duration;

        Follower(final float size, final float duration, final ArrayList<Piece> path, final Board board) {
            this.board = board;
            this.duration = duration / path.size();
            setSize(size, size);
            setPosition(path.get(0).getCenterPoint().x, path.get(0).getCenterPoint().y, Align.center);
            setOrigin(Align.center);
            setColor(Color.WHITE);
            board.addActor(this);
            board.addActor(new Circle(path.get(0), duration, board));
            follow(path, 0);
        }

        private void follow(final ArrayList<Piece> pieces, final int index) {
            addAction(Actions.sequence(Actions.delay(duration), pieces.get(index).getName().equals("portal") ?
                            Actions.sequence(Actions.parallel(Actions.fadeOut(duration / 2), Actions.rotateBy(360, duration / 2)),
                                    Actions.moveTo(pieces.get(index).getCenterPoint().x - getWidth() / 2,
                                            pieces.get(index).getCenterPoint().y - getHeight() / 2),
                                    Actions.parallel(Actions.fadeIn(duration / 2), Actions.rotateBy(-360, duration / 2))) :
                            Actions.moveTo(pieces.get(index).getCenterPoint().x - getWidth() / 2,
                                    pieces.get(index).getCenterPoint().y - getHeight() / 2, duration),
                    Actions.run(() -> {
                        if (index < pieces.size() - 1) {
                            if (index != 0) board.addActor(new Circle(pieces.get(index), duration, board));
                            toFront();
                            follow(pieces, index + 1);
                        } else {
                            board.addActor(new Circle(pieces.get(index), duration, board));
                            toFront();
                            addAction(Actions.sequence(Actions.delay(duration), Actions.removeActor()));
                        }
                    })));
        }

        @Override
        public void draw(final Batch batch, final float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
            batch.draw(board.getAtlas().findRegion("pixel"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            batch.setColor(Color.WHITE);
        }

    }

    private class Circle extends Actor {

        private final TextureRegion region;

        Circle(final Piece piece, final float duration, final Board board) {
            this.region = piece.getRegion();
            setSize(piece.getWidth(), piece.getHeight());
            setPosition(piece.getCenterPoint().x, piece.getCenterPoint().y, Align.center);
            setColor(piece.getColor());
            setOrigin(Align.center);
            addAction(Actions.sequence(Actions.parallel(Actions.scaleBy(1, 1, duration),
                    Actions.fadeOut(duration)), Actions.run(this::remove)));
            board.addActor(this);
        }

        @Override
        public void draw(final Batch batch, final float parentAlpha) {
            super.draw(batch, parentAlpha);
            batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
            batch.draw(region, getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
            batch.setColor(Color.WHITE);
        }
    }

    private class Line extends Actor {

        private final Vector2 startPoint;
        private final Vector2 endPoint;
        private final TextureRegion regionStart;
        private final TextureRegion regionEnd;
        private final float size;

        Line(final Vector2 startPoint, final Vector2 endPoint, final float size, final Color color, final TextureRegion regionStart,
             final TextureRegion regionEnd) {
            this.regionStart = regionStart;
            this.regionEnd = regionEnd;
            this.startPoint = startPoint;
            this.endPoint = endPoint;
            this.size = size;
            setColor(color);
            if (startPoint.x < endPoint.x) {
                setBounds(startPoint.x, startPoint.y - size / 2, endPoint.x - startPoint.x, size);
                setName("H");
            } else if (startPoint.x > endPoint.x) {
                setBounds(endPoint.x, endPoint.y - size / 2, startPoint.x - endPoint.x, size);
                setName("H");
            } else if (startPoint.y < endPoint.y) {
                setBounds(startPoint.x - size / 2, startPoint.y, size, endPoint.y - startPoint.y);
                setName("V");
            } else if (startPoint.y > endPoint.y) {
                setBounds(endPoint.x - size / 2, endPoint.y, size, startPoint.y - endPoint.y);
                setName("V");
            }
            startPoint.set(startPoint.x - size / 2, startPoint.y - size / 2);
            endPoint.set(endPoint.x - size / 2, endPoint.y - size / 2);
        }

        @Override
        public void draw(final Batch batch, final float parentAlpha) {
            super.draw(batch, parentAlpha);
            if (touch) {
                batch.setColor(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, 0.2f * parentAlpha);
                batch.draw(atlas.findRegion("pixel"), getX(), getShadowY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                batch.setColor(getColor().r, getColor().g, getColor().b, 0.2f * parentAlpha);
                batch.draw(atlas.findRegion("pixel"), getX(), getY(), getOriginX(), getOriginY(), getWidth(), getHeight(), getScaleX(), getScaleY(), getRotation());
                batch.draw(regionStart, startPoint.x, startPoint.y, getOriginX(), getOriginY(), size, size, getScaleX(), getScaleY(), getRotation());
                batch.draw(regionEnd, endPoint.x, endPoint.y, getOriginX(), getOriginY(), size, size, getScaleX(), getScaleY(), getRotation());
            } else {
                batch.setColor(Color.DARK_GRAY.r, Color.DARK_GRAY.g, Color.DARK_GRAY.b, Color.DARK_GRAY.a * parentAlpha);
                if (getName().equals("H")) {
                    batch.draw(atlas.findRegion("pixel"), getX() + size * 2, getShadowY(), getOriginX(), getOriginY(), getWidth() - size * 4, getHeight(), getScaleX(), getScaleY(), getRotation());
                    batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
                    batch.draw(atlas.findRegion("pixel"), getX() + size * 2, getY(), getOriginX(), getOriginY(), getWidth() - size * 4, getHeight(), getScaleX(), getScaleY(), getRotation());
                } else {
                    batch.draw(atlas.findRegion("pixel"), getX(), getShadowY() + size * 2, getOriginX(), getOriginY(), getWidth(), getHeight() - size * 4, getScaleX(), getScaleY(), getRotation());
                    batch.setColor(getColor().r, getColor().g, getColor().b, getColor().a * parentAlpha);
                    batch.draw(atlas.findRegion("pixel"), getX(), getY() + size * 2, getOriginX(), getOriginY(), getWidth(), getHeight() - size * 4, getScaleX(), getScaleY(), getRotation());
                }
            }
            batch.setColor(Color.WHITE);
        }

        private float getShadowY() {
            return getY() - Gdx.graphics.getHeight() / 250f;
        }
    }
}
