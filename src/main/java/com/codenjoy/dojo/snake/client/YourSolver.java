package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;

public class YourSolver implements Solver<Board> {
    private Board b;
    private Direction currDir;
    private Point head;
    private Point ap;
    private final Node[][] graph;
    LinkedList<Node> stack;

    public YourSolver() {
        this.graph = new Node[15][15];
        this.stack = new LinkedList<>();
    }

    public boolean step(Node node, Node to, int step, Queue<Node> q) {
        if (node.visitedForward) {
            return false;
        } else {
            node.visitedForward = true;
        }
        node.step = node.level = step;
        step++;

        if (node.point.getX() == to.point.getX()
                && node.point.getY() == to.point.getY()) {
            System.out.printf("target's been found at:  [%d,%d]  ",node.point.getX(), node.point.getY());
            return true;
        }
        return step(node, to, step, q);
    }

    public void genWave(Point from, Point to) {
        Node root = graph[from.getX()][from.getY()];
        Node target = graph[to.getX()][to.getY()];
        Queue<Node> q = new LinkedList<>();
        int step = 1;
        q.add(root);
        Node current;
        while (q.size() > 0) {
            current = q.poll();
            current.step = current.level = step;
            boolean res = step(current, target, step, q);
            if (res){
                System.out.println("we've found target!!!!");
            }
//            System.out.print("node[" + current.point.getX() + "][" + current.point.getY() + "]:  ");
//            System.out.println("level: " + current.level);
            step++;
            for (int i = 0; i < current.adj.size(); i++) {
                q.add(current.adj.get(i));
            }


        }


    }


    private void handleNeighbor(int x, int y, Node neighbor) {
        Node current = graph[x][y];
        if (neighbor != null) {
            neighbor.adj.add(current);
            current.adj.add(neighbor);
        }
    }

    private void buildGraph() {  //матрица графа
        for (int x = 1; x < b.size(); x++) {
            for (int y = 1; y < b.size(); y++) {
                if (b.isAt(x, y, Elements.NONE, Elements.GOOD_APPLE)
                        || (x == b.getHead().getX() && y == b.getHead().getY())) { //-head тоже добавить в граф!!!!
                    graph[x][y] = new Node(new PointImpl(x, y)); //создаём вершину

                    handleNeighbor(x, y, graph[x - 1][y]);
                    handleNeighbor(x, y, graph[x + 1][y]);
                    handleNeighbor(x, y, graph[x][y + 1]);
                    handleNeighbor(x, y, graph[x][y - 1]);
                }
            }
        }
    }

    public void printGraph() {
        for (int x = 1; x < b.size(); x++) {
            for (int y = 1; y < b.size(); y++) {
                System.out.print("graph Point at: [" + x + " " + y + "]");
                if (graph[x][y] != null) {
                    System.out.print(" PointX: " + graph[x][y].point.getX());
                    System.out.println(" PointY: " + graph[x][y].point.getY());
                } else {
                    System.out.println("null");
                }
            }
        }
    }

    private class Node {
        List<Node> adj;
        Point point;
        Node previous;
        boolean visitedForward;
        boolean visitedBackward;
        int step;
        int level;

        public Node(Point p) {
            this.point = p;
            this.adj = new ArrayList<>();
            this.visitedForward = this.visitedBackward = false;
            this.step = 0;
            this.level = 0;
        }

        public Node(Point p, int level) {
            this(p);
            this.level = level;
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
        buildGraph();  // printGraph();

        this.head = b.getHead();
        this.ap = b.getApples().get(0);

        genWave(this.head, this.ap);


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
