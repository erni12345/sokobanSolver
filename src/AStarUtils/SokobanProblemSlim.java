package AStarUtils;

import game.actions.slim.SAction;
import game.actions.slim.SMove;
import game.actions.slim.SPush;
import game.board.compact.BoardCompact;
import game.board.slim.BoardSlim;
import game.board.slim.STile;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SokobanProblemSlim implements HeuristicProblem<BoardSlim, SAction>{

    BoardSlim initialBoard;
    boolean[][] deadSquares;
    int[][] distances;

    public SokobanProblemSlim(BoardCompact board){
        this.initialBoard = board.makeBoardSlim();
        initialBoard.initializeBoxesAndPlaces();
        this.deadSquares = DeadSquareDetector.detect(board);
        this.distances = DeadSquareDetector.computeManhattanDistanceMap(board);
    }

    public BoardSlim initialState(){
        return initialBoard;
    }


    public List<SAction> actions(BoardSlim board) {

        List<SAction> actions = new ArrayList<SAction>(4);
        for (SMove move : SMove.getActions()) {
            if (move.isPossible(board)) {
                actions.add(move);
            }
        }
        for (SPush push : SPush.getActions()) {
            if (push.isPossible(board)) {
                actions.add(push);
            }
        }
        return actions;
    }


    public BoardSlim result(BoardSlim board, SAction action){
        BoardSlim boardCopy = board.clone();
        action.perform(boardCopy);
        return boardCopy;
    }


    public boolean isGoal(BoardSlim board){
        return board.isVictory();
    }


    public double cost(BoardSlim state, SAction action){
        // We want to minimise the amount of moves, every move has the same cost of 1.
        return 1.0;
    }


    public double estimate(BoardSlim board){
//        // Here we implemetn the heuristic of the board
//        // simple one -> Manhattan distance of all boxes to nearest point summed
//        //could be better

        //O(N) thanks to pre computation.

        Set<Coordinate> boxes = initialBoard.getBoxes();

        double totalDistance = 0.0;

        for (Coordinate box : boxes) {
            totalDistance += distances[box.x][box.y]; // uses the distance to closest box that was precomputed
        }

        return totalDistance;
    }

    @Override
    public boolean prune(BoardSlim state) {
        return DeadSquareDetector.isOnDeadSquareSlim(state, deadSquares);
    }
}
