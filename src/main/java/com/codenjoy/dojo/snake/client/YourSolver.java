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
    private Point rock;
    private final Node[][] graph;
    List<Point> snake;
    LinkedList<Node> path;

    public YourSolver() {
        this.graph = new Node[15][15];
        this.path = new LinkedList<>();
        currDir = Direction.UP;
    }


    public Node checkIfTargetReached(Node node, Node to) {
        if (node.visited) return null;
        node.visited = true;
        if (node.point.getX() == to.point.getX()
                && node.point.getY() == to.point.getY()) {
            System.out.printf("target's been found at:  [%d,%d]  ", node.point.getX(), node.point.getY());
            return node;
        }
        return null;
    }

    private void getPath(Node root, Node dest) {
        if (ap == null || head == null) return;
        path.clear();
        this.path.push(dest);
        boolean stop = false;
        int counter = 0;
        while (!stop && counter < 100000) {
            counter++;
            boolean shouldPop = false;
            for (int i = 0; i < dest.adj.size(); i++) {
                Node neighbour = dest.adj.get(i);
                if (dest.level == 0) continue;

                if (dest.level == 1 /*|| dest.equals(root)*/) {
                    System.out.println("dest.equals(root)   has just triggered!");
                    stop = true;
                    break;
                }

                if (neighbour.level < dest.level) {
                    if (shouldPop) {
                        path.pop();
                    }
                    shouldPop = true;
//                    neighbour.adj.remove(dest); //todo  ?????? ???????? ???????????????? currupted data !!
                    dest = neighbour;
                    this.path.push(dest);
                } else {
                    shouldPop = false;
                }
            }
        }
    }

    public Node runWaveAlgorythmLi(Point from, Point to) {
        if (from.getX() == to.getX() && from.getY() == to.getY()) return null;
        if (ap == null || head == null) return null;
        Node root = graph[from.getX()][from.getY()];
        Node target = graph[to.getX()][to.getY()];
        Queue<Node> q = new LinkedList<>();
        int step = 1;
        Node current = null;
        boolean allowed = true;
        root.level = step;
        q.offer(root);
        int counter = 0;

        while (q.size() > 0 && allowed && counter < 100000) {
            counter++;
            current = q.poll();

            Node res = checkIfTargetReached(current, target);
            if (res != null) {
                allowed = false;
//                System.out.println("we've found target!!!!");
                return res;

            }

            ++step;
            for (int i = 0; i < current.adj.size(); i++) {
                Node newNode = current.adj.get(i);
                if (newNode.level == 0) {
                    newNode.level = step;
                    q.offer(newNode);
                }
            }
        }
        return null;
    }


    @Override
    public String get(Board board) {
        initVariables(board);
        if (b != null && head != null) {

            if (this.snake.size() < 2) {
                currDir = getShortDirToTarget(this.head, this.ap); //???? ?????????? ??????????, ?????????????????? ???? ?????????????????????? ????????
            }

            else {
                Node res = null;
                Node root = new Node(this.head);
                res = runWaveAlgorythmLi(this.head, this.ap);
                if (res != null) {
                    getPath(root, res);
                }

                Point momentaryTarget = null;
                try {
                    if (root.getDistance(this.ap) < 3) {
                        currDir = getShortDirToTarget(this.head, this.ap);
                    } else {
                        momentaryTarget = path.get(3).point;
                        System.out.println("momentaryTarget" + momentaryTarget);
                        currDir = getShortDirToTarget(this.head, (momentaryTarget!=null) ?
                                momentaryTarget: this.ap ); //?????????????? ?????????? ??????????- ???????????????????? ???????????????? ????????????????
                    }

                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    currDir = getShortDirToTarget(this.head, this.ap);
                }


                System.out.println("path: ");
                for (int i = 0; i < path.size(); i++) {
                    System.out.print(this.path.get(i));
                }
//                printGraph();

            }
            avoidWalls();
        }
        return currDir.toString();
    }


    public static void main(String[] args) {
        WebSocketRunner.runClient(
                // paste here board page url from browser after registration
                "http://164.90.213.43/codenjoy-contest/board/player/7sxxtmlzkv2r19329d3m?code=2396662759495329739",
                new YourSolver(),
                new Board());
    }


    // ????????????????????????, ???????????????? ???????????????????? ??????:
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
        for (int y = b.size() - 2; y > 0; y--) {
            System.out.println();
            for (int x = 1; x < b.size() - 1; x++) {
                if (graph[x][y] != null) {
                    System.out.print("[" + graph[x][y].point.getX() + ",");
                    System.out.print(graph[x][y].point.getY() + "]");
                    System.out.printf("%7d\t", graph[x][y].level);

                } else {
                    if (x == rock.getX() && y == rock.getY()) {
                        System.out.print("[  rock   ]     ");
                    } else {
                        System.out.print("[null]          ");
                    }
                }
            }
        }
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

    // done -??????, ?????????????? ???????????????? ?? ??????????????
    private void initVariables(Board board) {
        this.b = board;
        this.snake = b.getSnake();
        this.head = b.getHead();
        this.ap = b.getApples().get(0);
        this.rock = b.getStones().get(0);
        buildGraph();
    }


    private boolean checkForSnakebody(int x, int y) {
        return this.snake.stream().anyMatch(el -> el.getX() == x && el.getY() == y);
    }

    private void buildGraph() {  //?????????????? ??????????
        if (b == null || b.size() == 0 || b.getHead() == null) return;

        for (int x = 1; x < b.size(); x++) {  // ?????????????? ???????????? ????????
            for (int y = 1; y < b.size(); y++) {
                graph[x][y] = null;
            }
        }

// ?????????????? ?????????? ????????
        for (int x = 1; x < b.size(); x++) {
            for (int y = 1; y < b.size(); y++) {

                if (
                        (x == b.getHead().getX() && y == b.getHead().getY()) // -head ???????? ???????????????? ?? ????????!!!!
                                ||
                                (!checkForSnakebody(x, y) //?????????????????? ???????????? ???????????? ???? ??????????
                                        && !b.isAt(x, y, Elements.BAD_APPLE) //?????????????????? ???????????? ???? ??????????
                                        && b.isAt(x, y, Elements.NONE, Elements.GOOD_APPLE)
                                )

                ) {
                    graph[x][y] = new Node(new PointImpl(x, y)); //?????????????? ??????????????
                    handleNeighborIfExists(x, y, graph[x - 1][y]);
                    handleNeighborIfExists(x, y, graph[x + 1][y]);
                    handleNeighborIfExists(x, y, graph[x][y + 1]);
                    handleNeighborIfExists(x, y, graph[x][y - 1]);
                } else {
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
        boolean visited;
        int level;

        private double getDistance(Point to) {
            return this.point.distance(to);
        }


//        private double getDistance(Point to) {
//
//            double dist = Math.sqrt(
//                    (Math.abs(this.point.getX() - to.getX())) ^ 2
//                            + (Math.abs(this.point.getX() - to.getX())) ^ 2
//            );
//            System.out.println("distance:  " + dist);
//            return dist;
//        }

        public Node(Point p) {
            this.point = p;
            this.adj = new LinkedList<>();
            this.visited = false;
            this.level = 0;
        }


        public Node(Point p, int level) {
            this(p);
            this.level = level;
        }

        @Override
        public int hashCode() {
            return 6 * point.getX()
                    + 37 * point.getY();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || this.getClass() != o.getClass()) return false;
            Node n = (Node) o;
            if (this.point.getY() == n.point.getY()
                    && this.point.getY() == n.point.getY()) {
                return true;
            } else
                return false;
        }

        @Override
        public String toString() {
            return "{ " +
                    "level:" + level +
                    " [" + point.getX() + "," + point.getY() + "] }\n";
        }
    }

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
        Node rootWithOldAdj = graph[from.getX()][from.getY()];
        Node root = removeAdjContainingTail(rootWithOldAdj); //?????????????? ?????????????????? root.adj ???????????????????? ?????????? ????????????

        return root.adj.stream()    // ???????????? ???????????? ?? ???????????????????? ??????????
                .min(Comparator.comparingDouble(el -> el.getDistance(to)))
                .map(el -> getDirection(el.point, this.head, this.currDir))
                .orElse(this.currDir);
    }

    public Direction getDirection(Point p, Point head, Direction currDir) {
        Direction dir = null;
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

}
