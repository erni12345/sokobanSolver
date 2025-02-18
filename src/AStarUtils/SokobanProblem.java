package AStarUtils;

import game.actions.EDirection;
import game.actions.compact.CAction;
import game.actions.compact.CMove;
import game.actions.compact.CPush;
import game.board.compact.BoardCompact;

import java.util.ArrayList;
import java.util.List;

public class SokobanProblem implements HeuristicProblem<BoardCompact, CAction>{

    BoardCompact initialBoard;

    public SokobanProblem(BoardCompact initialBoard){
        this.initialBoard = initialBoard;
    }

    public BoardCompact initialState(){
        return initialBoard;
    }


    public List<CAction> actions(BoardCompact board) {

        // HERE WE NEED TO PRUNE MOVES THAT ARE NOT ALLOWED

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

    public double estimate(BoardCompact state){

        // Here we implemetn the heuristic of the board
        // simple one -> Manhattan distance of all boxes to nearest point summed
        // maybe to be smart 2 sorts x and y then match (O(nlog(n)) instead of O(N^2))

        return 0.0;
    }


}
