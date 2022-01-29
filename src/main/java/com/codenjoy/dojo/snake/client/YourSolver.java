package com.codenjoy.dojo.snake.client;

import com.codenjoy.dojo.client.Solver;
import com.codenjoy.dojo.client.WebSocketRunner;
import com.codenjoy.dojo.services.Direction;
import com.codenjoy.dojo.services.Point;
import com.codenjoy.dojo.services.PointImpl;
import com.codenjoy.dojo.snake.model.Elements;

import java.util.*;
import java.util.stream.Collectors;

public class YourSolver implements Solver<Board> {
    private Board b;
    private Direction currDir;
    private Point head;
    private Point ap;
    private final Node[][] graph;
    LinkedList<Node> stack;
    List<Point> snake;

    public YourSolver() {
        this.graph = new Node[15][15];
        this.stack = new LinkedList<>();
        currDir = Direction.UP;
    }


//    @Override
//    public boolean equals(Object o) {
//        if(o == null) return false;
//        if (this == o) return true;
//        if(this.getClass() != o.getClass()) {
//            return false;
//        } else {
//            Node val = (Node) o;
//            if (this.point.getX() == val.point.getX() && this.point.getY() == val.point.getY()) return true;
//            return false;
//        }
//    }

    private Node removeAdjContainingTail(Node root) {
        List<Node> newAdj = root.adj.stream()
                .filter(el -> {
                    for (int i = 0; i < this.snake.size(); i++) {
                        if (el.point.getX() == snake.get(i).getX()
                                && el.point.getY() == snake.get(i).getY()
                        ) {
                            return false;
                        }
                    }
                    return true;
                })
                .collect(Collectors.toList());
        root.adj = newAdj;
        return root;
    }

    public Direction getShortDirToTarget(Point from, Point to) {
        if (b == null || b.size() == 0 || b.getHead() == null) return currDir;
//        Node target = graph[to.getX()][to.getY()];
        Node rootWithOldAdj = graph[from.getX()][from.getY()];
        Node root = removeAdjContainingTail(rootWithOldAdj); //удаляем возможные root.adj содержащие хвост змейки
//        Node root = rootWithOldAdj;

        return root.adj.stream()
                .min(Comparator.comparingDouble(el -> el.getDistance(to)))
                .map(el -> getDirection(el.point, this.head, this.currDir))
                .orElse(this.currDir);
    }



    public Direction getDirection(Point p, Point head, Direction currDir) {
        Direction dir = currDir;
        if (p.getX() < head.getX()) {
            dir = Direction.LEFT;
        } else if (p.getX() > head.getX() && currDir != Direction.LEFT) {
            dir = Direction.RIGHT;
        } else if (p.getY() < head.getY() && currDir != Direction.UP) {
            dir = Direction.DOWN;
        } else if (p.getY() > head.getY() && currDir != Direction.DOWN) {
            dir = Direction.UP;
        } else {
            dir = currDir;
        }
        return dir;
    }




    @Override
    public String get(Board board) {

        this.b = board;
        this.snake = b.getSnake();
        buildGraph();
        this.head = b.getHead();
        this.ap = b.getApples().get(0);

        currDir = getShortDirToTarget(this.head, this.ap);

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



    // Недоделанный, временно отложенный код:
    private void doStep(Direction d) {
        int newX = d.changeX(head.getX());
        int newY = d.changeY(head.getY());
        if (graph[newX][newY] != null) {
            this.currDir = d;
        } else {

        }
    }

    private boolean checkIfStepAllowed(Direction d) {
        int x, y;
        switch (d) {
            case UP:
                x = head.getX();
                y = head.getY() + 1;
                if (this.graph[x][y] == null) return false;
            case DOWN:
                x = head.getX();
                y = head.getY() - 1;
                if (this.graph[x][y] == null) return false;
            case LEFT:
                x = head.getX() - 1;
                y = head.getY();
                if (this.graph[x][y] == null) return false;
            case RIGHT:
                x = head.getX() + 1;
                y = head.getY();
                if (this.graph[x][y] == null) return false;
            default:
                return false;
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



    int counter;

    public Node step(Node node, Node to, int step) {
        if (node.visitedForward) {
            return null;
        } else {
            node.visitedForward = true;
        }
        node.level = step;
//        System.out.println("step: " + step);
        counter++;
//        System.out.println("step Path: " + counter);
        if (node.point.getX() == to.point.getX()
                && node.point.getY() == to.point.getY()) {
//            System.out.printf("target's been found at:  [%d,%d]  ", node.point.getX(), node.point.getY());
            return node;
        }
        return null;
    }

    public void getPath(Node node) {
        this.stack.push(node);
        while (node.previous != null) {
            stack.push(node.previous);
            if (stack.size() % 1000 == 0) {
                System.out.println("stack.size(): " + stack.size());
            }
        }
        System.out.println("path is: ");
        while (stack.peek() != null) {
            Point p = stack.pop().point;
            System.out.printf("[%d,%d]", p.getX(), p.getY());
        }
    }

    public Node genWave2(Point from, Point to) {

        Node root = graph[from.getX()][from.getY()];
        Node target = graph[to.getX()][to.getY()];
        Queue<Node> q = new LinkedList<>();
        int step = 1;
        counter = 0;
        q.add(root);
        Node newNode = null;
        Node current = null;
        Node res;
        double prevDistance = Double.MAX_VALUE;
        double currentDistance = 0;
        while (q.size() > 0) {
            current = q.poll();
            currentDistance = current.point.distance(to);
            if (currentDistance <= prevDistance) {
                res = step(current, target, step);

                if (counter == 1) {
                    System.out.printf("RETURNING:  current.point[%d,%d]\n", current.point.getX(), current.point.getY());
                }

                if (res != null) {
//                System.out.println("we've found target!!!!");
//                getPath(res);
//                    System.out.printf("RETURNING:  res.point[%d,%d]\n", res.point.getX(), res.point.getY());
                    return res;  // выйдем из genWave()
                }
            }
            prevDistance = currentDistance;

            for (int i = 0; i < current.adj.size(); i++) {
                newNode = current.adj.get(i);
                newNode.previous = current;
                newNode.level = step;
                q.add(newNode);
            }
            step++;
        }
        return current;
    }

    /*        System.out.println("ap: " + ap);
        Point st = b.getStones().get(0);
        List<Point> snake = b.getSnake();
        List<Point> walls = b.getWalls();
        Elements el = b.getAt(0, 0);
        System.out.println("el: " + el);
        System.out.println("el isAt(0,0) " + b.isAt(0, 0, el));
        System.out.println("el isAt(1,1) " + b.isAt(1, 1, el));
        int bSize = b.size();*/
//      System.out.println(board.toString());

    // done -код, который завершен и доделан
    private void buildGraph() {  //матрица графа
        if (b == null || b.size() == 0 || b.getHead() == null) return;

        for (int x = 1; x < b.size(); x++) {
            for (int y = 1; y < b.size(); y++) {
                if ((b.isAt(x, y, Elements.NONE, Elements.GOOD_APPLE)
                        && (!b.isAt(x, y, Elements.BAD_APPLE))) //вычленили камень из графа
                        || (x == b.getHead().getX() && y == b.getHead().getY())) { //-head тоже добавить в граф!!!!
                    graph[x][y] = new Node(new PointImpl(x, y)); //создаём вершину

                    handleNeighborIfExists(x, y, graph[x - 1][y]);
                    handleNeighborIfExists(x, y, graph[x + 1][y]);
                    handleNeighborIfExists(x, y, graph[x][y + 1]);
                    handleNeighborIfExists(x, y, graph[x][y - 1]);
                }
            }
        }
    }
    private void handleNeighborIfExists(int x, int y, Node neighbor) {
        Node current = graph[x][y];
        if (neighbor != null) {
            neighbor.adj.add(current);
            current.adj.add(neighbor);
        }
    }
    private void avoidWalls() {
        checkoutLeftBorder();
        checkoutRightBorder();
        checkoutBottomBorder();
        checkoutUpperBorder();
    }
    public void checkoutLeftBorder() {
        if (head.getX() == 1 && currDir == Direction.LEFT) {

            if (head.getY() < ap.getY()) {
                currDir = Direction.UP;
            } else {
                currDir = Direction.DOWN;
            }
        }
    }
    public void checkoutRightBorder() {
        if (head.getX() == b.size() - 1 && currDir == Direction.RIGHT) {

            if (head.getY() < ap.getY()) {
                currDir = Direction.UP;
            } else {
                currDir = Direction.DOWN;
            }
        }
    }
    public void checkoutBottomBorder() {
        if (head.getY() == 1 && currDir == Direction.DOWN) {

            if (head.getX() < ap.getX()) {
                currDir = Direction.RIGHT;
            } else {
                currDir = Direction.LEFT;
            }
        }
    }
    public void checkoutUpperBorder() {
        if (head.getY() == b.size() - 2 && currDir == Direction.UP) {
            if (head.getX() < ap.getX()) {
                currDir = Direction.RIGHT;
            } else {
                currDir = Direction.LEFT;
            }
        }
    }
    private static class Node {
        List<Node> adj;
        Point point;
        Node previous;
        boolean visitedForward;
        boolean visitedBackward;
        int level;

        private double getDistance(Point target) {
            return this.point.distance(target);
        }

        public Node(Point p) {
            this.point = p;
            this.adj = new LinkedList<>();
            this.visitedForward = this.visitedBackward = false;
            this.level = 0;
        }


        public Node(Point p, int level) {
            this(p);
            this.level = level;
        }


    }

}
