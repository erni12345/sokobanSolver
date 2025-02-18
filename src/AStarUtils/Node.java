package AStarUtils;

public class Node<S, A> implements Comparable<Node<S, A>> {
    S state;
    Node<S, A> parent;
    A action;
    double g; // Cost from start node
    double f; // Estimated total cost (g + heuristic)

    Node(S state, Node<S, A> parent, A action, double g, double f) {
        this.state = state;
        this.parent = parent;
        this.action = action;
        this.g = g;
        this.f = f;
    }

    @Override
    public int compareTo(Node<S, A> other) {
        return Double.compare(this.f, other.f);
    }
}
