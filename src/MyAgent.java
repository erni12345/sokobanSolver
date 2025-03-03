import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

import AStarUtils.*;
import agents.ArtificialAgent;
import game.actions.EDirection;
import game.actions.compact.*;
import game.actions.custom.CustAction;
import game.actions.slim.SAction;
import game.board.compact.BoardCompact;
import game.board.custom.BoardCustom;
import game.board.oop.Board;
import game.board.slim.BoardSlim;

/**
 * The simplest Tree-DFS agent.
 * @author Jimmy
 */
public class MyAgent extends ArtificialAgent {
	protected BoardCustom board;
	protected int searchedNodes;
	
	@Override
	protected List<EDirection> think(BoardCompact board) {
		this.board = BoardCustom.fromBoardCompact(board);
		searchedNodes = 0;
		long searchStartMillis = System.currentTimeMillis();
		
		List<EDirection> result = new ArrayList<EDirection>();

		boolean found = useAStar(result);
		long searchTime = System.currentTimeMillis() - searchStartMillis;
        
        if (verbose) {
            out.println("Nodes visited: " + searchedNodes);
            out.printf("Performance: %.1f nodes/sec\n",
                        ((double)searchedNodes / (double)searchTime * 1000));
        }
		
		return result.isEmpty() ? null : result;
	}

	private boolean useAStar(List<EDirection> result) {

		//System.out.println("Using a star");
		//Use A star to get a solution
		//Convert Solution to EDirections

		HeuristicProblem<BoardCustom, CustAction> sokoban = new SokobanProblem(this.board);
		Solution<BoardCustom, CustAction> solution = AStar.search(sokoban);

		//if no solution found
		if (solution == null) {
			return false;
		}

		// we now need to reconstruct the solution from actions to e directions
		List<CustAction> actions = solution.actions;
		for(CustAction action : actions) {
			result.add(action.getDirection());
		}

		return true;

	}
}
