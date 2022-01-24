package reference_data;

public class ClosestDistance {

    @Override
    public String get(Board board) {
        if (board.isGameOver()) {
            return Direction.UP.toString();
        }
        this.board = board;
        System.out.println(board);

        Point head = board.getHead();
        Point target = board.getSnake().size() < REDUCE_SIZE ? board.getApples().get(0) : board.getStones().get(0);
        //board.isAt(target.getX(), target.getY(), Elements.NONE);
        List<Point> neighbours = getNearEmpty(board, head, target);
        Direction direction = neighbours.stream()
                .peek(p -> System.out.println(p.toString() + ":" + p.distance(target)))
                .min(Comparator.comparingDouble(target::distance))
                .map(head::direction)
                .orElse(Direction.UP);
        return direction.toString();
    }

    private List<Point> getNearEmpty(Board board, Point head, Point target) {
        List<Point> neighbours = new ArrayList<>();
        Elements targetElement = board.getAt(target);

        Point left = new PointImpl(head.getX() - 1, head.getY());
        Point right = new PointImpl(head.getX() + 1, head.getY());
        Point up = new PointImpl(head.getX(), head.getY() + 1);
        Point down = new PointImpl(head.getX(), head.getY() - 1);

        if ((board.isAt(left, Elements.NONE) || board.isAt(left, targetElement))) {
            neighbours.add(left);
        }
        if ((board.isAt(right, Elements.NONE) || board.isAt(right, targetElement))) {
            neighbours.add(right);
        }
        if ((board.isAt(up, Elements.NONE) || board.isAt(up, targetElement))) {
            neighbours.add(up);
        }
        if ((board.isAt(down, Elements.NONE) || board.isAt(down, targetElement))) {
            neighbours.add(down);
        }
        return neighbours;
    }


}
