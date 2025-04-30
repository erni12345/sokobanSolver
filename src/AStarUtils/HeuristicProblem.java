package AStarUtils;

public interface HeuristicProblem<S, A> extends Problem<S, A> {
    double estimate(S state);
    double updateEstimate(S statePrev, S stateNext, A action);
    boolean prune(S state);
}