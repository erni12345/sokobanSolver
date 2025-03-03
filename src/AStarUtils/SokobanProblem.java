package AStarUtils;

import game.actions.EDirection;
import game.actions.ext.CAction;
import game.actions.ext.CMove;
import game.actions.ext.CPush;
import game.actions.oop.EActionType;
import game.board.ext.BoardCompactExtended;
import game.board.compact.CTile;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SokobanProblem implements HeuristicProblem<BoardCompactExtended, CAction>{

    BoardCompactExtended initialBoard;
    boolean[][] deadSquares;
    int[][] distances;



    public SokobanProblem(BoardCompactExtended initialBoard){
        this.initialBoard = initialBoard;
        initialBoard.initializeBoxes();
        this.deadSquares = DeadSquareDetector.detect(initialBoard);
        this.distances = DeadSquareDetector.computeManhattanDistanceMap(initialBoard);
    }

    public BoardCompactExtended initialState(){
        return initialBoard;
    }


    public List<CAction> actions(BoardCompactExtended board) {



        List<CAction> actions = new ArrayList<CAction>(4);
        for (CMove move : CMove.getActions()) {
            if (move.isPossible(board)) {
                actions.add(move);
            }
        }
        for (CPush push : CPush.getActions()) {
            if (push.isPossible(board)) {
                actions.add(push);
            }
        }
        return actions;
    }


    public BoardCompactExtended result(BoardCompactExtended board, CAction action){
        BoardCompactExtended boardCopy = board.clone();
        action.perform(boardCopy);
        return boardCopy;
    }


    public boolean isGoal(BoardCompactExtended board){
        return board.isVictory();
    }



    public double cost(BoardCompactExtended state, CAction action){

        // We want to minimise the amount of moves, every move has the same cost of 1.
        return 1.0;
    }


    public double estimate(BoardCompactExtended board){
//        // Here we implemetn the heuristic of the board
//        // simple one -> Manhattan distance of all boxes to nearest point summed
//        //could be better
        Set<Coordinate> boxes = initialBoard.getBoxes();

        double totalDistance = 0.0;

        for (Coordinate box : boxes) {
            totalDistance += distances[box.x][box.y]; // uses the distance to closest destination that was precomputed
        }

        return totalDistance;
    }




    @Override
    public boolean prune(BoardCompactExtended state) {
        int X = state.playerX, Y = state.playerY;
        //can make into single if, but this is slightly more readable
        if (CTile.isSomeBox(state.tile(X+1, Y)) && deadSquares[X+1][Y])
            return true;
        else if (CTile.isSomeBox(state.tile(X, Y+1)) && deadSquares[X][Y+1])
            return true;
        else if (CTile.isSomeBox(state.tile(X-1, Y)) && deadSquares[X-1][Y])
            return true;
        else if (CTile.isSomeBox(state.tile(X, Y-1)) && deadSquares[X][Y-1])
            return true;
        return false;
//        return DeadSquareDetector.isOnDeadSquare(state, deadSquares) ||
//                DeadSquareDetector.isBoxClusterDeadlock(state);
    }

    @Override
    public boolean prune2(BoardCompactExtended state, CAction action) {
        EActionType actionType = action.getType();
        if (actionType == EActionType.WALK || actionType == EActionType.MOVE) {
            return false;
        }
        EDirection direction = action.getDirection();
        // effectively repeat the player's last action to find the position into which the box moved
        return deadSquares[state.playerX + direction.dX][state.playerY + direction.dY];
    }
}
