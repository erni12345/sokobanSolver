package AStarUtils;

public interface HeuristicProblem<S, A> extends Problem<S, A> {
    double estimate(S state);
    boolean prune(S state);
    default boolean prune2(S state, A action) {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}