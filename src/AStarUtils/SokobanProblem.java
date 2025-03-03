package AStarUtils;

import game.actions.compact.CAction;
import game.actions.compact.CMove;
import game.actions.compact.CPush;
import game.actions.custom.CustAction;
import game.actions.custom.CustMove;
import game.actions.custom.CustPush;
import game.board.compact.CTile;
import game.board.custom.BoardCustom;
import game.board.oop.Board;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SokobanProblem implements HeuristicProblem<BoardCustom, CustAction>{

    BoardCustom initialBoard;
    boolean[][] deadSquares;
    int[][] distances;



    public SokobanProblem(BoardCustom initialBoard){
        this.initialBoard = initialBoard;
        initialBoard.initializeBoxes();
        this.deadSquares = DeadSquareDetector.detect(initialBoard);
        this.distances = DeadSquareDetector.computeManhattanDistanceMap(initialBoard);
    }

    public BoardCustom initialState(){
        return initialBoard;
    }


    public List<CustAction> actions(BoardCustom board) {



        List<CustAction> actions = new ArrayList<CustAction>(4);
        for (CustMove move : CustMove.getActions()) {
            if (move.isPossible(board)) {
                actions.add(move);
            }
        }
        for (CustPush push : CustPush.getActions()) {
            if (push.isPossible(board)) {
                actions.add(push);
            }
        }
        return actions;
    }


    public BoardCustom result(BoardCustom board, CustAction action){
        BoardCustom boardCopy = board.clone();
        action.perform(boardCopy);
        return boardCopy;
    }


    public boolean isGoal(BoardCustom board){
        return board.isVictory();
    }



    public double cost(BoardCustom state, CustAction action){

        // We want to minimise the amount of moves, every move has the same cost of 1.
        return action.getSteps();
    }


    public double estimate(BoardCustom board){
//        // Here we implemetn the heuristic of the board
//        // simple one -> Manhattan distance of all boxes to nearest point summed
//        //could be better
        Set<Coordinate> boxes = initialBoard.getBoxes();

        double totalDistance = 0.0;

        for (Coordinate box : boxes) {
            totalDistance += distances[box.x][box.y]; // uses the distance to closest box that was precomputed
        }

        return totalDistance;
    }




    @Override
    public boolean prune(BoardCustom state) {
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
}
