package AStarUtils;

import game.board.custom.BoardCustom;

public class Coordinate implements Cloneable{
    public final int x, y;
    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public Coordinate clone() {
        return new Coordinate(this.x, this.y);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + x;
        result = prime * result + y;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof Coordinate)) return false;
        Coordinate other = (Coordinate) obj;
        if (x != other.x) return false;
        if (y != other.y) return false;
        return true;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ")";
    }

}
