package AStarUtils;

import game.actions.EDirection;
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
//        System.out.println(initialBoard.getBoxPositions());
        this.deadSquares = DeadSquareDetector.detect(initialBoard);
        this.distances = DeadSquareDetector.computeManhattanDistanceMap(initialBoard);
    }

    public BoardCustom initialState(){
        return initialBoard;
    }


    public List<CustAction> actions(BoardCustom board) {




        List<CustAction> actions = new ArrayList<CustAction>(4);
        for (CustMove move : CustMove.getActions()) {
//            System.out.println("______");
//            System.out.println(move.toString());
//            System.out.println(board.getBoardString());
            if (move.isPossible(board)) {
                actions.add(move);
            }
        }
        for (CustPush push : CustPush.getActions()) {
            if (push.isPossible(board)) {
                actions.add(push);
            }
        }

//        System.out.println(actions.toString());
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
        Set<Coordinate> boxes = board.getBoxes();
//        System.out.println("Estimate called");
        double totalDistance = 0.0;

        for (Coordinate box : boxes) {
            totalDistance += distances[box.x][box.y]; // uses the distance to closest box that was precomputed
        }

        board.h = totalDistance;

        return totalDistance;
    }

    public double updateEstimate(BoardCustom prev, BoardCustom next, CustAction action) {
        if (action instanceof CustPush) {
            EDirection dir = action.getDirection();
            next.h = prev.h - distances[next.playerX][next.playerY] + distances[next.playerX+dir.dX][next.playerY+dir.dY];
        }
        else {
            next.h = prev.h; // TODO: is this the expected behaviour
        }
        return next.h;
    }



    @Override
    public boolean prune(BoardCustom state) {
        return DeadSquareDetector.isOnDeadSquare(state, deadSquares);
//                || DeadSquareDetector.isBoxClusterDeadlock(state);
    }
}
