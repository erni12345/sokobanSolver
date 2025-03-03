package AStarUtils;

import game.actions.EDirection;
import game.actions.compact.CPush;
import game.actions.custom.CustAction;
import game.board.compact.CTile;
import game.board.custom.BoardCustom;
import game.board.oop.EEntity;
import game.board.oop.EPlace;
import game.board.oop.ESpace;
import game.board.slim.BoardSlim;
import game.board.slim.STile;

import java.security.cert.CertificateEncodingException;
import java.util.*;

import static game.actions.compact.CPush.isPushPossible;
import static game.board.oop.EPlace.SOME_BOX_PLACE_FLAG;

public class DeadSquareDetector {

    BoardCustom board;
    static List<Coordinate> goalCoordinates;
    static Integer[][] moves = new Integer[4][2];

    public DeadSquareDetector(BoardCustom board) {
        this.board = board;
        goalCoordinates = getGoalCoordinates(board);

        moves[0][0] = 1;
        moves[0][1] = 0;

        moves[1][0] = -1;
        moves[1][1] = 0;

        moves[2][0] = 0;
        moves[2][1] = 1;

        moves[3][0] = 0;
        moves[3][1] = -1;
    }



    // Returns the x y coordinated of all places
    public static List<Coordinate> getGoalCoordinates(BoardCustom board) {
        List<Coordinate> goalCoordinates = new ArrayList<>();

        for (int x = 0; x < board.width(); x++) {
            for (int y = 0; y < board.height(); y++) {
                if (CTile.forSomeBox(board.tile(x, y))) {
                    goalCoordinates.add(new Coordinate(x, y));
                }
            }
        }

        return goalCoordinates;
    }

    public static boolean isPushPossible(BoardCustom board, int playerX, int playerY, EDirection pushDirection) {

        //Player out of map
        if (playerX < 0 || playerX >= board.width() || playerY < 0 || playerY >= board.height()) {
            return false;
        }


        // PLAYER ON THE EDGE
        if (!CustAction.isOnBoard(board, playerX, playerY, pushDirection)) {
            return false;
        }

        // BOX IS ON THE EDGE IN THE GIVEN DIR
        if (!CustAction.isOnBoard(board, playerX+pushDirection.dX, playerY+pushDirection.dY, pushDirection)) {
            return false;
        }

        if (CTile.isWall(board.tile(playerX, playerY))) return false;

        // box is a wall
        if(CTile.isWall(board.tile(playerX+pushDirection.dX, playerY + pushDirection.dY))) return false;

        // YEP, WE CAN PUSH
        return true;
    }

    public static boolean isPossible(BoardCustom board, int x, int y, int destX, int destY) {

        // player
        int playerX = 2 * x + destX;
        int playerY = 2 * y + destY;

        // check if move can happen
        if(x == 0 && y == 1){
            return isPushPossible(board, playerX, playerY, EDirection.UP);
        } else if (x == 0 && y == -1){
            return isPushPossible(board, playerX, playerY, EDirection.DOWN);
        } else if (x == 1 && y == 0){
            return isPushPossible(board, playerX, playerY, EDirection.LEFT);
        } else if (x == -1 && y == 0){
            return isPushPossible(board, playerX, playerY, EDirection.RIGHT);
        }

        return false;

    }

    public static boolean[][] detect(BoardCustom board) {

        goalCoordinates = getGoalCoordinates(board);

        moves[0][0] = 1;
        moves[0][1] = 0;

        moves[1][0] = -1;
        moves[1][1] = 0;

        moves[2][0] = 0;
        moves[2][1] = 1;

        moves[3][0] = 0;
        moves[3][1] = -1;

        boolean[][] seen = new boolean[board.width()][board.height()];
        Queue<Coordinate> queue = new LinkedList<>();

        for (Coordinate goal : getGoalCoordinates(board)) {
            queue.add(goal);
            seen[goal.x][goal.y] = true;
        }

        while (!queue.isEmpty()) {
            Coordinate current = queue.poll();

            for (int i = 0; i < 4; i++) {
                int x = moves[i][0];
                int y = moves[i][1];

                int newX = x + current.x;
                int newY = y + current.y;

                if (newX >= 0 && newX < seen.length && newY >= 0 && newY < seen[0].length) {
                    if (!seen[newX][newY] && isPossible(board, x, y, current.x, current.y)) {
                        queue.add(new Coordinate(newX, newY));
                        seen[newX][newY] = true;
                    }
                }
            }
        }

        return invertBooleanArray(seen);
    }



    public static boolean isOnDeadSquare(BoardCustom board, boolean[][] isDeadSquare){

        //TODO:
        // a way of making this much faster is on move of box have a flag if it gets moved int a dead square
        // make our own board representation would be best here

        for (int x = 0; x < board.width(); x++) {
            for (int y = 0; y < board.height(); y++) {
                if(isDeadSquare[x][y] && CTile.isSomeBox(board.tile(x, y))) {
                    // box is at a dead square
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isOnDeadSquareSlim(BoardSlim board, boolean[][] isDeadSquare){

        //TODO:
        // a way of making this much faster is on move of box have a flag if it gets moved int a dead square
        // make our own board representation would be best here

        for (int x = 0; x < board.width(); x++) {
            for (int y = 0; y < board.height(); y++) {
                if(isDeadSquare[x][y] && STile.isBox(board.tile(x, y))) {
                    // box is at a dead square
                    return true;
                }
            }
        }

        return false;
    }


    public static int[][] computeManhattanDistanceMap(BoardCustom board) {
        int width = board.width();
        int height = board.height();
        int[][] distanceMap = new int[width][height];

        for (int[] row : distanceMap) {
            Arrays.fill(row, Integer.MAX_VALUE);
        }

        List<Coordinate> goals = getGoalCoordinates(board);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                if (CTile.isWall(board.tile(x, y))) {
                    continue;
                }

                int minDistance = Integer.MAX_VALUE;
                for (Coordinate goal : goals) {
                    int manhattanDist = Math.abs(goal.x - x) + Math.abs(goal.y - y);
                    minDistance = Math.min(minDistance, manhattanDist);
                }
                distanceMap[x][y] = minDistance;
            }
        }

        return distanceMap;
    }

    private static boolean canMoveBox(BoardCustom board, int bX, int bY) {
        for (EDirection dir : EDirection.values()) {
            int playerX = bX - dir.dX;
            int playerY = bY - dir.dY;

            // If a push is possible from this position, the box can be moved
            if (isPushPossible(board, playerX, playerY, dir)) {
                return true;
            }
        }
        return false; // No valid pushes found, so the box is stuck
    }


    public static boolean isBoxTrapped(BoardCustom board, int boxX, int boxY) {
        // Check if a box is stuck against another box
        for (EDirection dir : EDirection.values()) {
            int nx = boxX + dir.dX;
            int ny = boxY + dir.dY;

            if (board.getBoxes().contains(new Coordinate(nx, ny))) {
                // Check if this box is immovable
                if (!canMoveBox(board, nx, ny)) {
                    return true;
                }
            }
        }
        return false;
    }

    private static boolean isWithinBounds(BoardCustom board, int x, int y) {
        return x >= 0 && y >= 0 && x < board.width() && y < board.height();
    }




    private static boolean isCorridorDeadlock(BoardCustom board, int boxX, int boxY, EDirection pushDir) {

        boolean isHorizontalCorridor = CTile.isWall(board.tile(boxX, boxY - 1)) && CTile.isWall(board.tile(boxX, boxY + 1));
        boolean isVerticalCorridor = CTile.isWall(board.tile(boxX - 1, boxY)) && CTile.isWall(board.tile(boxX + 1, boxY));

        if (!isHorizontalCorridor && !isVerticalCorridor) return false;

        if (canMoveBox(board, boxX, boxY)) return false;

        int nx = boxX + pushDir.dX;
        int ny = boxY + pushDir.dY;

        while (isWithinBounds(board, nx, ny) && !CTile.isWall(board.tile(nx, ny))) {
            if (CTile.forAnyBox(board.tile(nx, ny))) {
                return false;
            }

            if (CTile.isSomeBox(board.tile(nx, ny)) && canMoveBox(board, nx, ny)) {
                return false;
            }

            nx += pushDir.dX;
            ny += pushDir.dY;
        }

        return true;
    }




    public static boolean isBoxClusterDeadlock(BoardCustom state) {
        for (Coordinate box : state.getBoxes()) {
            int stuckBoxes = 0;

            if (isBoxTrapped(state, box.x, box.y)) {
                return true;
            }

            for (EDirection dir : EDirection.values()) {
                int nx = box.x + dir.dX;
                int ny = box.y + dir.dY;
                if (state.getBoxes().contains(new Coordinate(nx, ny))) {
                    stuckBoxes++;
                }

                if (isCorridorDeadlock(state, box.x, box.y, dir)) return true;
            }

            if (stuckBoxes >= 3) return true; // Box is trapped by 3+ boxes
        }
        return false;
    }




    public static boolean[][] invertBooleanArray(boolean[][] array) {
        int rows = array.length;
        int cols = array[0].length;
        boolean[][] inverted = new boolean[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                inverted[i][j] = !array[i][j];
            }
        }
        return inverted;
    }

    public static void printBoard(BoardCustom bc, boolean[][] dead) {
        for (int y = 0; y < bc.height(); ++y) {
            for (int x = 0; x < bc.width(); ++x) {
                System.out.print(CTile.isWall(bc.tile(x, y)) ? '#' : (dead[x][y] ? 'X' : '_'));
            }
            System.out.println();
        }
    }

    public static void printBoard(BoardSlim bc, boolean[][] dead) {
        for (int y = 0; y < bc.height(); ++y) {
            for (int x = 0; x < bc.width(); ++x) {
                System.out.print(STile.isWall(bc.tile(x, y)) ? '#' : (dead[x][y] ? 'X' : '_'));
            }
            System.out.println();
        }
    }

}
