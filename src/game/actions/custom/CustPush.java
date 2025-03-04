package game.actions.custom;

import AStarUtils.Coordinate;
import game.actions.EDirection;
import game.actions.compact.CAction;
import game.actions.compact.CPush;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.custom.BoardCustom;
import game.board.custom.CustomTile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class CustPush extends CustAction {

    private static Map<EDirection, CustPush> actions = new HashMap<EDirection, CustPush>();

    static {
        actions.put(EDirection.DOWN, new CustPush(EDirection.DOWN));
        actions.put(EDirection.UP, new CustPush(EDirection.UP));
        actions.put(EDirection.LEFT, new CustPush(EDirection.LEFT));
        actions.put(EDirection.RIGHT, new CustPush(EDirection.RIGHT));
    }

    public static Collection<CustPush> getActions() {
        return actions.values();
    }

    public static CustPush getAction(EDirection direction) {
        return actions.get(direction);
    }

    private EDirection dir;

    private EDirection[] dirs;

    public CustPush(EDirection dir) {
        this.dir = dir;
        this.dirs = new EDirection[]{ dir };
    }

    @Override
    public EActionType getType() {
        return EActionType.PUSH;
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
        return isPushPossible(board, board.playerX, board.playerY, dir);
    }

    /**
     * Is it possible to push the box from [playerX, playerY] in 'pushDirection' ?
     * @param board
     * @param playerX
     * @param playerY
     * @param pushDirection
     * @return
     */
    public static boolean isPushPossible(BoardCustom board, int playerX, int playerY, EDirection pushDirection) {
        // PLAYER ON THE EDGE

        if (!CustAction.isOnBoard(board, playerX, playerY, pushDirection)) return false;

//        System.out.println(board.getBoxPositions());
//        System.out.println(new Coordinate(playerX+pushDirection.dX, playerY+pushDirection.dY));

        // TILE TO THE DIR IS NOT BOX
        if (!board.getBoxPositions().contains(new Coordinate(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;

//        System.out.println("Player is pushing into box");

        // BOX IS ON THE EDGE IN THE GIVEN DIR
        if (!CustAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;

//        System.out.println("Box is not being pushed out of the edge");

        // TILE TO THE DIR OF THE BOX IS NOT FREE (DOES NOT CHECK IF IT IS BOX)
        if (!CustomTile.isFree(board.tile(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;

//        System.out.println("new box position is free (maybe a box)");

        // TILE TO THE DIR OF THE BOX IS A BOX
        if (board.getBoxPositions().contains(new Coordinate(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;

//        System.out.println("new box position is free (not a box)");

        // YEP, WE CAN PUSH
        return true;
    }



    @Override
    public void perform(BoardCustom board) {
        // MOVE THE BOX
        board.moveBox(board.playerX + dir.dX, board.playerY + dir.dY, board.playerX + dir.dX + dir.dX, board.playerY + dir.dY + dir.dY);
        // MOVE THE PLAYER
        board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
    }


    @Override
    public void reverse(BoardCustom board) {
        // MARK PLAYER POSITION
        int playerX = board.playerX;
        int playerY = board.playerY;
        // MOVE THE PLAYER
        board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
        // MOVE THE BOX
        board.moveBox(playerX + dir.dX, playerY + dir.dY, playerX, playerY);
    }
}
