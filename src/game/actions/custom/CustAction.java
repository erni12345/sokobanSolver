package game.actions.custom;

import game.actions.EDirection;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import game.board.custom.BoardCustom;

public abstract class CustAction {

    public abstract EActionType getType();
    public abstract EDirection getDirection();
    public abstract EDirection[] getDirections();
    public abstract int getSteps();
    public abstract boolean isPossible(BoardCustom board);
    public abstract void perform(BoardCustom board);
    public abstract void reverse(BoardCustom board);


    protected boolean onBoard(BoardCustom board, int tileX, int tileY, EDirection dir) {
        return isOnBoard(board, tileX, tileY, dir);
    }

    public static boolean isOnBoard(BoardCustom board, int tileX, int tileY, EDirection dir) {
        int targetX = tileX + dir.dX;
        if (targetX < 0 || targetX >= board.width()) return false;
        int targetY = tileY + dir.dY;
        if (targetY < 0 || targetY >= board.height()) return false;
        return true;
    }
}
