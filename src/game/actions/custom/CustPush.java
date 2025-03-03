package game.actions.custom;

import AStarUtils.Coordinate;
import game.actions.EDirection;
import game.actions.compact.CAction;
import game.actions.compact.CPush;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.custom.BoardCustom;

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

        // TILE TO THE DIR IS NOT BOX
        if (!board.getBoxPositions().contains(new Coordinate(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;

        // BOX IS ON THE EDGE IN THE GIVEN DIR
        if (!CustAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;

        // TILE TO THE DIR OF THE BOX IS NOT FREE (DOES NOT CHECK IF IT IS BOX)
        if (!CTile.isFree(board.tile(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;

        // TILE TO THE DIR OF THE BOX IS A BOX
        if (board.getBoxPositions().contains(new Coordinate(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;

        // YEP, WE CAN PUSH
        return true;
    }

    /**
     * Is it possible to push the box from [playerX, playerY] in 'pushDirection' ?
     *
     * This deem the box pushable even if there is a player in that direction.
     *
     * @param board
     * @param playerX
     * @param playerY
     * @param pushDirection
     * @return
     */
    public static boolean isPushPossibleIgnorePlayer(BoardCustom board, int playerX, int playerY, EDirection pushDirection) {
        // PLAYER ON THE EDGE
        if (!CustAction.isOnBoard(board, playerX, playerY, pushDirection)) return false;

        // TILE TO THE DIR IS NOT BOX
        if (!CTile.isSomeBox(board.tile(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;

        // BOX IS ON THE EDGE IN THE GIVEN DIR
        if (!CustAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;

        // TILE TO THE DIR OF THE BOX IS NOT FREE
        if (!CTile.isWalkable(board.tile(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;

        // YEP, WE CAN PUSH
        return true;
    }

    /**
     * PERFORM THE PUSH, no validation, call {@link #isPossible(BoardCompact, EDirection)} first!
     * @param board
     * @param dir
     */
    @Override
    public void perform(BoardCustom board) {
        // MOVE THE BOX
        board.moveBox(board.playerX + dir.dX, board.playerY + dir.dY, board.playerX + dir.dX + dir.dX, board.playerY + dir.dY + dir.dY);
        // MOVE THE PLAYER
        board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
    }

    /**
     * REVERSE THE ACTION PREVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
     * @param board
     * @param dir
     */
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
