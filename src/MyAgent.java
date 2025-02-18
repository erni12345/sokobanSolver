import static java.lang.System.out;
import java.util.ArrayList;
import java.util.List;

import AStarUtils.AStar;
import AStarUtils.HeuristicProblem;
import AStarUtils.SokobanProblem;
import AStarUtils.Solution;
import agents.ArtificialAgent;
import game.actions.EDirection;
import game.actions.compact.*;
import game.board.compact.BoardCompact;
import game.board.oop.Board;

/**
 * The simplest Tree-DFS agent.
 * @author Jimmy
 */
public class MyAgent extends ArtificialAgent {
	protected BoardCompact board;
	protected int searchedNodes;
	
	@Override
	protected List<EDirection> think(BoardCompact board) {
		this.board = board;
		searchedNodes = 0;
		long searchStartMillis = System.currentTimeMillis();
		
		List<EDirection> result = new ArrayList<EDirection>();
//		dfs(5, result); // the number marks how deep we will search (the longest plan we will consider)

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

		HeuristicProblem<BoardCompact, CAction> sokoban = new SokobanProblem(this.board);
		Solution<BoardCompact, CAction> solution = AStar.search(sokoban);

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
}
