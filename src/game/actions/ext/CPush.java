package game.actions.ext;

import game.actions.EDirection;
import game.actions.oop.EActionType;
import game.board.compact.BoardCompact;
import game.board.ext.BoardCompactExtended;
import game.board.compact.CTile;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * PUSH ONLY. If the player is not next to the box or there is nowhere to push the box, than the action is considered as not possible.
 * @author Jimmy
 */
public class CPush extends CAction {
	
	private static Map<EDirection, CPush> actions = new HashMap<EDirection, CPush>();
	
	static {
		actions.put(EDirection.DOWN, new CPush(EDirection.DOWN));
		actions.put(EDirection.UP, new CPush(EDirection.UP));
		actions.put(EDirection.LEFT, new CPush(EDirection.LEFT));
		actions.put(EDirection.RIGHT, new CPush(EDirection.RIGHT));
	}
	
	public static Collection<CPush> getActions() {
		return actions.values();
	}
	
	public static CPush getAction(EDirection direction) {
		return actions.get(direction);
	}
	
	private EDirection dir;
	
	private EDirection[] dirs;
	
	public CPush(EDirection dir) {
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
	public boolean isPossible(BoardCompactExtended board) {
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
	public static boolean isPushPossible(BoardCompactExtended board, int playerX, int playerY, EDirection pushDirection) {
		// PLAYER ON THE EDGE
		if (!CAction.isOnBoard(board, playerX, playerY, pushDirection)) return false;
		
		// TILE TO THE DIR IS NOT BOX
		if (!CTile.isSomeBox(board.tile(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;
		
		// BOX IS ON THE EDGE IN THE GIVEN DIR
		if (!CAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;
		
		// TILE TO THE DIR OF THE BOX IS NOT FREE
		if (!CTile.isFree(board.tile(playerX+pushDirection.dX+pushDirection.dX, playerY+pushDirection.dY+pushDirection.dY))) return false;
				
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
	public static boolean isPushPossibleIgnorePlayer(BoardCompactExtended board, int playerX, int playerY, EDirection pushDirection) {
		// PLAYER ON THE EDGE
		if (!CAction.isOnBoard(board, playerX, playerY, pushDirection)) return false;
		
		// TILE TO THE DIR IS NOT BOX
		if (!CTile.isSomeBox(board.tile(playerX+pushDirection.dX, playerY+pushDirection.dY))) return false;
		
		// BOX IS ON THE EDGE IN THE GIVEN DIR
		if (!CAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) return false;
		
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
	public void perform(BoardCompactExtended board) {
		// MOVE THE BOX
		board.moveBox(board.playerX + dir.dX, board.playerY + dir.dY, board.playerX + dir.dX + dir.dX, board.playerY + dir.dY + dir.dY);
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX + dir.dX, board.playerY + dir.dY);
	}
	
abstract class CAction {

	public abstract EActionType getType();

	/**
	 * Provides "the first direction"
	 * @return
	 */
	public abstract EDirection getDirection();

	/**
	 * Provides "all movements" in case of macro actions (e.g. {@link CWalk}, {@link CWalkPush}).
	 * @return
	 */
	public abstract EDirection[] getDirections();

	/**
	 * How many steps the action implements; may return -1 if unknown (i.e., custom teleports).
	 * @return
	 */
	public abstract int getSteps();

	public abstract boolean isPossible(BoardCompactExtended board);

	public abstract void perform(BoardCompactExtended board);

	public abstract void reverse(BoardCompactExtended board);

	/**
	 * If we move 1 step in given 'dir', will we still be at board?
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	protected boolean onBoard(BoardCompactExtended board, int tileX, int tileY, EDirection dir) {
		return isOnBoard(board, tileX, tileY, dir);
	}

	/**
	 * If we move 1 step in given 'dir', will we still be at board?
	 * @param tile
	 * @param dir
	 * @param steps
	 * @return
	 */
	public static boolean isOnBoard(BoardCompactExtended board, int tileX, int tileY, EDirection dir) {
		int targetX = tileX + dir.dX;
		if (targetX < 0 || targetX >= board.width()) return false;
		int targetY = tileY + dir.dY;
		if (targetY < 0 || targetY >= board.height()) return false;
		return true;
	}

}
	/**
	 * REVERSE THE ACTION PREVIOUSLY DONE BY {@link #perform(BoardCompact, EDirection)}, no validation.
	 * @param board
	 * @param dir
	 */
	@Override
	public void reverse(BoardCompactExtended board) {
		// MARK PLAYER POSITION
		int playerX = board.playerX;
		int playerY = board.playerY;
		// MOVE THE PLAYER
		board.movePlayer(board.playerX, board.playerY, board.playerX - dir.dX, board.playerY - dir.dY);
		// MOVE THE BOX
		board.moveBox(playerX + dir.dX, playerY + dir.dY, playerX, playerY);
	}
	
	@Override
	public String toString() {
		return "CPush[" + dir.toString() + "]";
	}

}
