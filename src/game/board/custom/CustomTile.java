package game.board.custom;

public class CustomTile {


    public static boolean isFree(CustomEntity entity) {
        return entity == CustomEntity.EMPTY || entity == CustomEntity.DESTINATION;
    }

    public static boolean forAnyBox(CustomEntity entity) {
        return entity == CustomEntity.DESTINATION;
    }

    public static boolean isWall(CustomEntity entity) {
        return entity == CustomEntity.WALL;
    }
}
