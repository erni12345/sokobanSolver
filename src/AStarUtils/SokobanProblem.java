package AStarUtils;

import game.actions.EDirection;
import game.actions.compact.CAction;
import game.actions.compact.CMove;
import game.actions.compact.CPush;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.oop.EEntity;
import game.board.oop.EPlace;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class SokobanProblem implements HeuristicProblem<BoardCompact, CAction>{

    BoardCompact initialBoard;
    boolean[][] deadSquares;
    int[][] distances;



    public SokobanProblem(BoardCompact initialBoard){
        this.initialBoard = initialBoard;
        initialBoard.initializeBoxes();
        this.deadSquares = DeadSquareDetector.detect(initialBoard);
        this.distances = DeadSquareDetector.computeManhattanDistanceMap(initialBoard);
    }

    public BoardCompact initialState(){
        return initialBoard;
    }


    public List<CAction> actions(BoardCompact board) {



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


    public BoardCompact result(BoardCompact board, CAction action){
        BoardCompact boardCopy = board.clone();
        action.perform(boardCopy);
        return boardCopy;
    }


    public boolean isGoal(BoardCompact board){
        return board.isVictory();
    }



    public double cost(BoardCompact state, CAction action){

        // We want to minimise the amount of moves, every move has the same cost of 1.
        return 1.0;
    }


    public double estimate(BoardCompact board){
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
    public boolean prune(BoardCompact state) {
        return DeadSquareDetector.isOnDeadSquare(state, deadSquares) ||
                DeadSquareDetector.isBoxClusterDeadlock(state);
    }
}
