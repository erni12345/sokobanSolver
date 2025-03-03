package game.board.custom;

import AStarUtils.Coordinate;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.minimal.StateMinimal;
import game.board.oop.EEntity;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BoardCustom implements Cloneable{

    private Integer hash = null;

    /**
     * Compact representation of tiles.
     */
    public int[][] tiles;

    public int playerX;
    public int playerY;

    public int boxCount;
    public int boxInPlaceCount;

    private Set<Coordinate> boxPositions = new HashSet<>();

    private BoardCustom(){
    }

    public BoardCustom(int width, int height) {
        tiles = new int[width][height];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                tiles[x][y] = 0;
            }
        }
    }

    public Set<Coordinate> getBoxPositions() {
        return boxPositions;
    }

    @Override
    public BoardCustom clone() {
        BoardCustom result = new BoardCustom();
        result.tiles = this.tiles;
        result.playerX = playerX;
        result.playerY = playerY;
        result.boxCount = boxCount;
        result.boxInPlaceCount = boxInPlaceCount;
        result.boxPositions = this.boxPositions;
        return result;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 31 * hash + playerX;
        hash = 31 * hash + playerY;
        for (Coordinate c : boxPositions) {
            hash = 31 * hash + (c != null ? c.hashCode() : 0);
        }
        return hash;
    }

    public void initializeBoxes() {
        boxPositions.clear();

        for (int x = 0; x < width(); x++) {
            for (int y = 0; y < height(); y++) {
                int tile = tiles[x][y];

                if (CTile.isSomeBox(tile)) {
                    boxPositions.add(new Coordinate(x, y));
                }
            }
        }
    }

    public Set<Coordinate> getBoxes() {
        return boxPositions;
    }


    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BoardCustom)) return false;
        BoardCustom other = (BoardCustom) obj;

        return playerX == other.playerX &&
                playerY == other.playerY &&
                boxPositions.equals(other.boxPositions);
    }

    public boolean equalsState(BoardCustom other) {
        if (other == null) return false;
        if (width() != other.width() || height() != other.height()) return false;
        for (int x = 0; x < width(); ++x) {
            for (int y = 0; y < height(); ++y) {
                if (tiles[x][y] != other.tiles[x][y]) return false;
            }
        }
        return true;
    }

    public int width() {
        return tiles.length;
    }

    public int height() {
        return tiles[0].length;
    }

    public int tile(int x, int y) {
        return tiles[x][y];
    }

    public void movePlayer(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {

        playerX = targetTileX;
        playerY = targetTileY;

        hash = null;
    }


    public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {

        if (CTile.forAnyBox(tiles[targetTileX][targetTileY])){
            ++boxInPlaceCount;
        }

        if (CTile.forAnyBox(tiles[sourceTileX][sourceTileY])){
            --boxInPlaceCount;
        }

        boolean removed = boxPositions.remove(new Coordinate(sourceTileX, sourceTileY));
        if (removed) {
            boxPositions.add(new Coordinate(targetTileX, targetTileY));
        } else {
            System.out.println("ISSUE WITH MOVE BOX");
        }
        hash = null;

        hash = null;
    }


    public boolean isVictory() {
        return boxCount == boxInPlaceCount;
    }

    public void setState(StateMinimal state) {
        playerX = state.getX(state.positions[0]);
        playerY = state.getY(state.positions[0]);
        boxInPlaceCount = 0;

        tiles[playerX][playerY] = (tiles[playerX][playerY] & EEntity.NULLIFY_ENTITY_FLAG) | EEntity.PLAYER.getFlag();

        for (int i = 1; i < state.positions.length; ++i) {
            int x = state.getX(state.positions[i]);
            int y = state.getY(state.positions[i]);
            tiles[x][y] = (tiles[x][y] & EEntity.NULLIFY_ENTITY_FLAG) | EEntity.BOX_1.getFlag();
            if (CTile.forSomeBox(tiles[x][y])) ++boxInPlaceCount;
        }
    }


    public static BoardCustom fromBoardCompact(BoardCompact board){
        BoardCustom result = new BoardCustom();
        result.tiles = board.tiles;
        result.playerX = board.playerX;
        result.playerY = board.playerY;
        result.boxCount = board.boxCount;
        result.boxInPlaceCount = board.boxInPlaceCount;

        result.initializeBoxes();

        return result;
    }



}
