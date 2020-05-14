package co.circlewave.cirzle.board;

import co.circlewave.cirzle.board.component.Piece;
import co.circlewave.cirzle.board.component.Sign;

import java.util.ArrayList;

public class PathFinder {

    private final Sign sign;

    private Node<Piece> lastNode;


    PathFinder(final Sign sign) {
        this.sign = sign;
        lastNode = new Node<>(sign.getTile().getPiece(), null);
        findChildren(lastNode);
    }

    private void findChildren(final Node<Piece> node) {
        Node<Piece> childNode;
        final ArrayList<Piece> pieces = node.getData().pieceEffect(node.hasParent() ? node.getParent().getData() : null);
        for (final Piece piece : pieces) {
            childNode = new Node<>(piece, node);
            if (!pathContainsNode(childNode) && !childNode.getData().isLinked()) {
                node.addChild(childNode);
            }
        }

        if(chickIfDifferentSign(node) && lastNode.getBranchSize() <= node.getBranchSize()){
            lastNode = node;
        } else if(!chickIfDifferentSign(lastNode) && node.getChildren().isEmpty() &&
                lastNode.getBranchSize() <= node.getBranchSize()){
            lastNode = node;
        }

        for (final Node<Piece> child : node.getChildren()) {
            findChildren(child);
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
        return chickIfDifferentSign(lastNode);
    }

    Node<Piece> getData() {
        Node<Piece> closestNode = lastNode;
        while (closestNode.hasParent()){
            closestNode = closestNode.getParent();
            if(chickIfDifferentSign(closestNode)){
                lastNode = closestNode;
            }
        }
        return lastNode;
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
