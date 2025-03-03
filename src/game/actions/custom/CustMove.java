package game.actions.custom;

import game.actions.EDirection;
import game.actions.compact.CAction;
import game.actions.compact.CMove;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.custom.BoardCustom;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustMove extends CustAction {


    private static Map<EDirection, CustMove> actions = new HashMap<EDirection, CustMove>();

    static {
        actions.put(EDirection.DOWN, new CustMove(EDirection.DOWN));
        actions.put(EDirection.UP, new CustMove(EDirection.UP));
        actions.put(EDirection.LEFT, new CustMove(EDirection.LEFT));
        actions.put(EDirection.RIGHT, new CustMove(EDirection.RIGHT));
    }

    public static Collection<CustMove> getActions() {
        return actions.values();
    }

    public static CustMove getAction(EDirection direction) {
        return actions.get(direction);
    }

    private EDirection dir;

    private EDirection[] dirs;


    public CustMove(EDirection dir) {
        this.dir = dir;
        this.dirs = new EDirection[]{ dir };
    }

    @Override
    public EActionType getType() {
        return EActionType.MOVE;
    }

    @Override
    public EDirection getDirection() {
        return dir;
    }

    @Override
    public EDirection[] getDirections() {
        return dirs;
    }

    @Override
    public int getSteps() {
        return 1;
    }

    @Override
    public boolean isPossible(BoardCustom board) {
        // PLAYER ON THE EDGE
        if (!onBoard(board, board.playerX, board.playerY, dir)) return false;

        // TILE TO THE DIR IS FREE
        if (CTile.isFree(board.tile(board.playerX+dir.dX, board.playerY+dir.dY))) return true;

        // TILE WE WISH TO MOVE TO IS NOT FREE
        return false;
    }

    @Override
    public void perform(BoardCustom board) {
        // MOVE THE PLAYER
        board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
    }

    @Override
    public void reverse(BoardCustom board) {
        // REVERSE THE PLAYER
        board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
    }
}
