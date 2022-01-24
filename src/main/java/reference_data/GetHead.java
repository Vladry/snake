package reference_data;

public class GetHead {


    Point head = board.getHead();
    Point goodApple = board.getApples().get(0);

    Direction directionX = head.getX() > goodApple.getX() ? Direction.LEFT : Direction.RIGHT;
    Direction directionY = head.getY() > goodApple.getY() ? Direction.DOWN : Direction.UP;

    boolean isLeftPossible = board.isAt(head.getX() - 1, head.getY(), Elements.NONE, Elements.GOOD_APPLE);
    boolean isRightPossible = board.isAt(head.getX() + 1, head.getY(), Elements.NONE, Elements.GOOD_APPLE);
    boolean isUpPossible = board.isAt(head.getX(), head.getY() + 1, Elements.NONE, Elements.GOOD_APPLE);
    boolean isDownPossible = board.isAt(head.getX(), head.getY() - 1, Elements.NONE, Elements.GOOD_APPLE);

        if (isLeftPossible && head.getX() > goodApple.getX()){
        return Direction.LEFT.toString();
    }else if (isRightPossible && head.getX() < goodApple.getX()) {
        return Direction.RIGHT.toString();
    }else if (isUpPossible && head.getY() < goodApple.getY()) {
        return Direction.UP.toString();
    }
        return Direction.DOWN.toString();


}
