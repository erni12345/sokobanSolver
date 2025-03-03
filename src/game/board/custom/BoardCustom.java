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

    private final Set<Coordinate> boxPositions = new HashSet<>();

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

    @Override
    public BoardCustom clone() {
        BoardCustom result = new BoardCustom();
        result.tiles = new int[width()][height()];
        for (int x = 0; x < width(); ++x) {
            for (int y = 0; y < height(); ++y) {
                result.tiles[x][y] = tiles[x][y];
            }
        }
        result.playerX = playerX;
        result.playerY = playerY;
        result.boxCount = boxCount;
        result.boxInPlaceCount = boxInPlaceCount;
        return result;
    }

    @Override
    public int hashCode() {
        if (hash == null) {
            hash = Arrays.deepHashCode(tiles) ^ (31 * playerX + 97 * playerY);
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
                boxPositions.equals(other.boxPositions) && // Use Set equality
                Arrays.deepEquals(this.tiles, other.tiles);
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
        int entity = tiles[sourceTileX][sourceTileY] & EEntity.SOME_ENTITY_FLAG;

        tiles[targetTileX][targetTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
        tiles[targetTileX][targetTileY] |= entity;

        tiles[sourceTileX][sourceTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
        tiles[sourceTileX][sourceTileY] |= EEntity.NONE.getFlag();

        playerX = targetTileX;
        playerY = targetTileY;

        hash = null;
    }


    public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {
        int entity = tiles[sourceTileX][sourceTileY] & EEntity.SOME_ENTITY_FLAG;
        int boxNum = CTile.getBoxNum(tiles[sourceTileX][sourceTileY]);

        if (CTile.forBox(boxNum, tiles[targetTileX][targetTileY]) || CTile.forAnyBox(tiles[targetTileX][targetTileY])) {
            ++boxInPlaceCount;
        }
        tiles[targetTileX][targetTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
        tiles[targetTileX][targetTileY] |= entity;

        if (CTile.forBox(boxNum, tiles[sourceTileX][sourceTileY]) || CTile.forAnyBox(tiles[sourceTileX][sourceTileY])) {
            --boxInPlaceCount;
        }
        tiles[sourceTileX][sourceTileY] &= EEntity.NULLIFY_ENTITY_FLAG;
        tiles[sourceTileX][sourceTileY] |= EEntity.NONE.getFlag();

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
