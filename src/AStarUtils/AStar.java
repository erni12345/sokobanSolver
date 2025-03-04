package AStarUtils;

import java.util.*;


public class AStar<S, A> {
    public static <S, A> Solution<S, A> search(HeuristicProblem<S, A> prob) {
        PriorityQueue<Node<S, A>> frontier = new PriorityQueue<>(Comparator.comparingDouble(n -> n.f));
        Map<S, Double> gScore = new HashMap<>();
        Map<S, Node<S, A>> cameFrom = new HashMap<>();

        S start = prob.initialState();
        Node<S, A> startNode = new Node<>(start, null, null, 0, prob.estimate(start));
        frontier.add(startNode);
        gScore.put(start, 0.0);


        while (!frontier.isEmpty()) {
            Node<S, A> current = frontier.poll();

            if (prob.isGoal(current.state)) {
                return reconstructSolution(current, cameFrom);
            }

            if (prob.prune(current.state)) {
                continue;
            }

//            System.out.println("ACTION TAKEN " + current.action);

            for (A action : prob.actions(current.state)) {
//                System.out.println("ACtion : " + action);
                S nextState = prob.result(current.state, action);
                double newCost = current.g + prob.cost(current.state, action);

//                 If we already found this state with a lower cost, skip it
                if (gScore.containsKey(nextState) && newCost >= gScore.get(nextState)) {
                    continue;
                }

                gScore.put(nextState, newCost);
                double fScore = newCost + prob.estimate(nextState);
                Node<S, A> nextNode = new Node<>(nextState, current, action, newCost, fScore);
                cameFrom.put(nextState, nextNode);
                frontier.add(nextNode);
            }


        }

        return null;  // No solution found
    }

    private static <S, A> Solution<S, A> reconstructSolution(Node<S, A> node, Map<S, Node<S, A>> cameFrom) {
        List<A> actions = new ArrayList<>();
        S goalState = node.state;
        double pathCost = node.g;

        while (node.parent != null) {
            actions.add(node.action);
            node = node.parent;
        }
        Collections.reverse(actions);
        return new Solution<>(actions, goalState, pathCost);
    }
}
