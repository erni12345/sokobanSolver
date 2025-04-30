package game.board.custom;

import game.board.compact.CustomEntity;

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
