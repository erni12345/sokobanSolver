package AStarUtils;

public interface HeuristicProblem<S, A> extends Problem<S, A> {
    double estimate(S state);
}