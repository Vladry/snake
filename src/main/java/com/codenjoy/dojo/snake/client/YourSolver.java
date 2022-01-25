package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.ArrayList;
import java.util.List;

public class YourSolver implements Solver<Board> {
    private Board b;
    private Direction currDir;
    private Point head;
    private Point ap;
    private final Node[][] graph;

    public YourSolver() {

        this.graph = new Node[15][15];
    }

    private void addNeighborsToCurrentVertice(int x, int y) {
        if (x > 1) { //если есть хоть одно место до левой стенки -создаём соседа слева от вершины
            graph[x - 1][y] = handleLeftNeighbor(x, y);
        }
        if (x < b.size() - 1) { //если есть хоть одно место до правой стенки -создаём соседа справа от вершины
            graph[x + 1][y] = handleRightNeighbor(x, y);
        }
        if (y < b.size() - 1) { //если есть хоть одно место снизу от y до нижней границы
            graph[x][y + 1] = handleUpperNeighbor(x, y);
        }
        if (y > 1) { //если есть хоть одно место снизу от y до нижней границы
            graph[x][y - 1] = handleDownNeighbor(x, y);
        }
    }

    private Node handleLeftNeighbor(int x, int y) {
        Node neighbor = graph[x - 1][y];
        if (b.isAt(x - 1, y, Elements.NONE)) {
            if (neighbor == null) {
                neighbor = new Node(new PointImpl(x - 1, y));
            }
            neighbor.neighbors.add(new Node(new PointImpl(x, y)));
        }
        return neighbor;
    }

    private Node handleRightNeighbor(int x, int y) {
        Node neighbor = graph[x + 1][y];
        if (b.isAt(x + 1, y, Elements.NONE)) {
            if (neighbor == null) {
                neighbor = new Node(new PointImpl(x + 1, y));
            }
            neighbor.neighbors.add(new Node(new PointImpl(x, y)));
        }
        return neighbor;
    }

    private Node handleUpperNeighbor(int x, int y) {
        Node neighbor = graph[x][y + 1];
        if (b.isAt(x, y + 1, Elements.NONE)) {
            if (neighbor == null) {
                neighbor = new Node(new PointImpl(x, y + 1));
            }
            neighbor.neighbors.add(new Node(new PointImpl(x, y)));
        }
        return neighbor;
    }

    private Node handleDownNeighbor(int x, int y) {
        Node neighbor = graph[x][y - 1];
        if (b.isAt(x, y - 1, Elements.NONE)) {
            if (neighbor == null) {
                neighbor = new Node(new PointImpl(x, y - 1));
            }
            neighbor.neighbors.add(new Node(new PointImpl(x, y)));
        }
        return neighbor;
    }

    private void buildGraph() {  //матрица графа
        for (int x = 1; x < b.size(); x++) {
            for (int y = 1; y < b.size(); y++) {
                if (b.isAt(x, y, Elements.NONE, Elements.GOOD_APPLE)) {
                    graph[x][y] = new Node(new PointImpl(x, y)); //создаём вершину
                    addNeighborsToCurrentVertice(x, y);
                }
            }
        }
    }

    public void printGraph() {
        for (int x = 1; x < b.size(); x++) {
            for (int y = 1; y < b.size(); y++) {
                System.out.print("graph Point at: [" + x + " " + y +"]" );
                if(graph[x][y] != null ){
                    System.out.print(" PointX: " + graph[x][y].point.getX());
                    System.out.println(" PointY: " + graph[x][y].point.getY());
                } else {
                    System.out.println("null");
                }
            }
        }
    }

    private class Node {
        List<Node> neighbors;
        Point point;

        public Node(Point p) {
            this.point = p;
            neighbors = new ArrayList<>();
        }
    }

    private void avoidWalls() {
        if (head.getX() == 1 && currDir == Direction.LEFT) {

            if (head.getY() < ap.getY()) {
                currDir = Direction.UP;
            } else {
                currDir = Direction.UP;
            }
        }

        if (head.getX() == b.size() - 2 && currDir == Direction.RIGHT) {
            if (head.getY() < ap.getY()) {
                currDir = Direction.UP;
            } else {
                currDir = Direction.UP;
            }
        }
        if (head.getY() == 1 && currDir == Direction.DOWN) {
            currDir = Direction.UP;
        }
        if (head.getY() == b.size() - 2 && currDir == Direction.UP) {
            currDir = Direction.DOWN;
        }

    }

    @Override
    public String get(Board board) {
//      System.out.println(board.toString());
        this.b = board;
        buildGraph();
//        printGraph();

        this.ap = b.getApples().get(0);
        this.head = b.getHead();
        Point ap = b.getApples().get(0);
        System.out.println("ap: " + ap);
        Point st = b.getStones().get(0);
        List<Point> snake = b.getSnake();
        List<Point> walls = b.getWalls();
        Elements el = b.getAt(0, 0);
        System.out.println("el: " + el);
        System.out.println("el isAt(0,0) " + b.isAt(0, 0, el));
        System.out.println("el isAt(1,1) " + b.isAt(1, 1, el));
        int bSize = b.size();

        currDir = Direction.LEFT;
        avoidWalls();
        return currDir.toString();
    }

    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://164.90.213.43/codenjoy-contest/board/player/7sxxtmlzkv2r19329d3m?code=2396662759495329739",
                new YourSolver(),
                new Board());
    }

}
