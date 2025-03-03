import static game.board.ext.BoardCompactExtended.fromBoardCompact;
import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

import AStarUtils.*;
import agents.ArtificialAgent;
import game.actions.EDirection;
import game.actions.compact.*;
import game.actions.slim.SAction;
import game.board.compact.BoardCompact;
import game.board.ext.BoardCompactExtended;
import game.board.slim.BoardSlim;

/**
 * The simplest Tree-DFS agent.
 * @author Jimmy
 */
public class MyAgent extends ArtificialAgent {
	protected BoardCompactExtended board;
	protected int searchedNodes;
	
	@Override
	protected List<EDirection> think(BoardCompact board) {
		this.board = fromBoardCompact(board);
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

		HeuristicProblem<BoardCompactExtended, CAction> sokoban = new SokobanProblem(this.board);
		Solution<BoardCompactExtended, CAction> solution = AStar.search(sokoban);

		//if no solution found
		if (solution == null) {
			return false;
		}

		// we now need to reconstruct the solution from actions to e directions
		List<CAction> actions = solution.actions;
		for(CAction action : actions) {
			result.add(action.getDirection());
		}

		return true;

	}

	private boolean useAStarSlim(List<EDirection> result) {
		// idk why slim is being much slower so I will not use it for now


		//System.out.println("Using a star");
		//Use A star to get a solution
		//Convert Solution to EDirections

		HeuristicProblem<BoardSlim, SAction> sokoban = new SokobanProblemSlim(this.board);
		Solution<BoardSlim, SAction> solution = AStar.search(sokoban);
		//if no solution found
		if (solution == null) {
			return false;
		}

		// we now need to reconstruct the solution from actions to e directions
		List<SAction> actions = solution.actions;
		for(SAction action : actions) {
			result.add(action.getDirection());
		}

		return true;

	}
}
