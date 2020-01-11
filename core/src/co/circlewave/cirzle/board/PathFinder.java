package co.circlewave.cirzle.board;

import co.circlewave.cirzle.Game;
import co.circlewave.cirzle.board.component.Piece;
import co.circlewave.cirzle.board.component.Sign;

import java.util.ArrayList;

public class PathFinder {

    private final Sign sign;
    private final ArrayList<Node<Piece>> realPath;
    private final ArrayList<Node<Piece>> fakePath;

    private boolean type;

    PathFinder(final Sign sign) {
        this.sign = sign;
        this.realPath = new ArrayList<>();
        this.fakePath = new ArrayList<>();
        this.type = false;
        findChildren(new Node<>(sign.getTile().getPiece(), null));
    }

    private void findChildren(final Node<Piece> node) {
        Node<Piece> childNode;
        final ArrayList<Piece> pieces = node.getData().pieceEffect(node.hasParent() ? node.getParent().getData() : null);
        for (final Piece piece : pieces) {
            childNode = new Node<>(piece, node);
            if (!pathContainsNode(childNode)) {
                node.addChild(childNode);
            }
        }
        for (final Node<Piece> child : node.getChildren()) {
            findChildren(child);
        }
        if (chickIfDifferentSign(node)) {
            realPath.add(node);
        } else if (node.getChildren().isEmpty()) {
            fakePath.add(node);
        }
    }

    private Boolean pathContainsNode(Node<Piece> node) {
        final Piece piece = node.getData();
        while (node.hasParent()) {
            if (node.getParent().getData().equals(piece)) {
                return true;
            }
            node = node.getParent();
        }
        return false;
    }

    private boolean chickIfDifferentSign(final Node<Piece> node) {
        return node.getData().getTile().hasASign() && node.getData().getTile().getSign().isPieceColorMatched() &&
                !node.getData().getTile().getSign().equals(sign);
    }

    public boolean getType() {
        return type;
    }

    Node<Piece> getData() {
        Game.print("P: " + sign.getTile().getTileId());
        if (!realPath.isEmpty()) {
            type = true;
            return getDeepestNode(realPath);
        } else if (!fakePath.isEmpty()) {
            type = false;
            Game.print("f");
            return getDeepestNode(fakePath);
        } else {
            return new Node<>(sign.getTile().getPiece(), null);
        }
    }

    private Node<Piece> getDeepestNode(final ArrayList<Node<Piece>> path) {
        Node<Piece> deepestNode = path.get(0);
        for (int index = 1; index < path.size(); index++) {
            if (deepestNode.getBranchSize() < path.get(index).getBranchSize())
                deepestNode = path.get(index);
        }
        return deepestNode;
    }


    public class Node<T> {

        private final T data;
        private final Node<T> parent;
        private final ArrayList<Node<T>> children;

        Node(final T data, final Node<T> parent) {
            this.data = data;
            this.parent = parent;
            this.children = new ArrayList<>();
        }

        boolean hasParent() {
            return parent != null;
        }

        void addChild(final Node<T> child) {
            children.add(child);
        }

        ArrayList<Node<T>> getChildren() {
            return children;
        }

        T getData() {
            return data;
        }

        Node<T> getParent() {
            return parent;
        }

        int getBranchSize() {
            int size = 0;
            Node<T> node = this;
            while (node.hasParent()) {
                size++;
                node = node.parent;
            }
            return size;
        }
    }
}
