package game.actions.custom;

import game.actions.EDirection;
import game.actions.compact.CWalk;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.custom.BoardCustom;
import game.board.custom.CustomTile;

public class CustWalk extends CustAction {


    private int x;
    private int y;

    private int fromX = -1;
    private int fromY = -1;

    private EDirection[] path;

    public CustWalk(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public CustWalk(int x, int y, EDirection[] path) {
        this.x = x;
        this.y = y;
        this.path = path;
    }

    @Override
    public EActionType getType() {
        return EActionType.WALK;
    }

    @Override
    public EDirection getDirection() {
        return path == null ? null : path[0];
    }

    @Override
    public EDirection[] getDirections() {
        return path;
    }

    /**
     * How many steps do you need in order to perform the walk; defined only if directions are provided during construction using {@link CWalk#CWalk(int, int, EDirection[])}.
     * @return
     */
    public int getSteps() {
        return path == null ? -1 : path.length;
    }

    @Override
    public boolean isPossible(BoardCustom board) {
        return CustomTile.isFree(board.tile(x, y));
    }

    @Override
    public void perform(BoardCustom board) {
        this.fromX = board.playerX;
        this.fromY = board.playerY;
        if (fromX != x || fromY != y) {
            board.movePlayer(board.playerX, board.playerY, x, y);
        }
    }

    @Override
    public void reverse(BoardCustom board) {
        if (fromX != x || fromY != y) {
            board.movePlayer(x, y, fromX, fromY);
        }
    }
}
