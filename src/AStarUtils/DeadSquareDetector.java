package AStarUtils;

import game.actions.EDirection;
import game.actions.custom.CustAction;
import game.board.compact.BoardCompact;
import game.board.custom.BoardCustom;
import game.board.custom.CustomTile;

import java.util.*;

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
                if (CustomTile.forAnyBox(board.tile(x, y))) {
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

        if (CustomTile.isWall(board.tile(playerX, playerY))) return false;

        // box is a wall
        if(CustomTile.isWall(board.tile(playerX+pushDirection.dX, playerY + pushDirection.dY))) return false;

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

    public static boolean[][] detect(BoardCompact board){
        BoardCustom boardCustom = BoardCustom.fromBoardCompact(board);
        return detect(boardCustom);
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
                if (CustomTile.isWall(board.tile(x, y))) {
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



    private static boolean isWithinBounds(BoardCustom board, int x, int y) {
        return x >= 0 && y >= 0 && x < board.width() && y < board.height();
    }




    public static boolean pushIntoDeadSquare(CustAction action, boolean[][] deadSquare, BoardCustom board){
        // we know action is of push type
        // move player coords in direction that is where the box is
        // check if dead square
        int playerX = board.playerX;
        int playerY = board.playerY;
        return deadSquare[playerX + action.getDirection().dX][playerY + action.getDirection().dY];
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
                System.out.print(CustomTile.isWall(bc.tile(x, y)) ? '#' : (dead[x][y] ? 'X' : '_'));
            }
            System.out.println();
        }
    }

}
