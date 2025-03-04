package game.board.custom;

import AStarUtils.Coordinate;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.compact.CustomEntity;
import game.board.minimal.StateMinimal;
import game.board.oop.EEntity;
import game.board.oop.EPlace;
import game.board.oop.ESpace;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BoardCustom implements Cloneable{

    private Integer hash = null;

    /**
     * Compact representation of tiles.
     */
    public CustomEntity[][] tiles;

    public int playerX;
    public int playerY;

    public int boxCount;
    public int boxInPlaceCount;

    private Set<Coordinate> boxPositions = new HashSet<>();

    public double h;

    private BoardCustom(){
    }

    public BoardCustom(int width, int height) {
        tiles = new CustomEntity[width][height];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                tiles[x][y] = CustomEntity.EMPTY;
            }
        }
        h = 0.0; // TODO: check this
    }

    private BoardCustom(CustomEntity[][] tiles, int px, int py, int bc, int bipc) {
        this.tiles = tiles;
        playerX = px;
        playerY = py;
        boxCount = bc;
        boxInPlaceCount = bipc;
    }

    public Set<Coordinate> getBoxPositions() {
        return boxPositions;
    }

    @Override
    public BoardCustom clone() {
        BoardCustom result = new BoardCustom(tiles, playerX, playerY, boxCount, boxInPlaceCount);
        Set<Coordinate> clonedSet = new HashSet<>(boxPositions);
        //            clonedSet.add(c.clone());
//        clonedSet.addAll(boxPositions);
        result.boxPositions = clonedSet;
        return result;
    }

    @Override
    public int hashCode() {

        if (hash != null) return hash;
        int hash = 7;
        hash = 31 * hash + playerX;
        hash = 31 * hash + playerY;
        for (Coordinate c : boxPositions) {
            hash = 31 * hash + (c != null ? c.hashCode() : 0);
        }
        return hash;
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

    public CustomEntity tile(int x, int y) {
        return tiles[x][y];
    }

    public void movePlayer(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {

        playerX = targetTileX;
        playerY = targetTileY;

        hash = null;
    }


    public void moveBox(int sourceTileX, int sourceTileY, int targetTileX, int targetTileY) {

//        System.out.println(tiles[targetTileX][targetTileY] + " type +++++++++++++" );
//        System.out.println("KSNFPSDNFSDN:JKDSNFLKDJSNFLDJSNFLDSJKNFLKJSDNFLDSKJNFLJNF");

        if (CustomTile.forAnyBox(tiles[targetTileX][targetTileY])){
            ++boxInPlaceCount;
        }

        if (CustomTile.forAnyBox(tiles[sourceTileX][sourceTileY])){
            --boxInPlaceCount;
        }

        boolean removed = boxPositions.remove(new Coordinate(sourceTileX, sourceTileY));
        if (removed) {
            boxPositions.add(new Coordinate(targetTileX, targetTileY));
        } else {
            System.out.println("ISSUE WITH MOVE BOX");
        }
        hash = null;

    }


    public boolean isVictory() {
//        System.out.println(boxCount + " " + boxInPlaceCount);
        return boxCount == boxInPlaceCount;
    }


    public static BoardCustom fromBoardCompact(BoardCompact board){
        BoardCustom result = new BoardCustom();
        result.tiles = new CustomEntity[board.tiles.length][board.tiles[0].length];
        HashSet<Coordinate> boxPositions = new HashSet<>();
        for (int x = 0; x < board.tiles.length; ++x) {
            for (int y = 0; y < board.tiles[0].length; ++y) {
                if (CTile.isSomeBox(board.tiles[x][y])) {
                    result.tiles[x][y] = CustomEntity.EMPTY;
                    boxPositions.add(new Coordinate(x, y));
                }
                if (CTile.isPlayer(board.tiles[x][y])) {
                    result.tiles[x][y] = CustomEntity.EMPTY;
                }

                if (CTile.isWall(board.tiles[x][y])) {
                    result.tiles[x][y] = CustomEntity.WALL;
                } else if (CTile.forBox(1, board.tiles[x][y])){
                    result.tiles[x][y] = CustomEntity.DESTINATION;
                } else {
                    result.tiles[x][y] = CustomEntity.EMPTY;
                }
            }
        }
        result.playerX = board.playerX;
        result.playerY = board.playerY;
        result.boxCount = board.boxCount;
        result.boxInPlaceCount = board.boxInPlaceCount;
        result.boxPositions = boxPositions;
        return result;
    }

    public String getBoardString() {
        StringBuffer sb = new StringBuffer();

        for (int y = 0; y < height(); ++y) {
            if (y != 0) sb.append("\n");
            for (int x = 0; x < width(); ++x) {

                if (x == playerX && y == playerY) {
                    sb.append("@");
                } else
                if (boxPositions.contains(new Coordinate(x, y))) {
                    sb.append("$");
                } else
                if (CustomTile.isWall(tiles[x][y])){
                    sb.append("#");
                } else if (CustomTile.forAnyBox(tiles[x][y])){
                    sb.append(".");
                } else if (CustomTile.isFree(tiles[x][y])){
                    sb.append(" ");
                }
                else {
                    sb.append("?");
                }
            }
        }

        return sb.toString();
    }




}
