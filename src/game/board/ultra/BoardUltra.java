package game.board.ultra;

import AStarUtils.Coordinate;
import game.board.compact.BoardCompact;
import game.board.compact.CTile;
import game.board.slim.STile;
import java.util.HashSet;
import java.util.Set;

/**
 * Uses minimal memory while maintaining all necessary Sokoban functionality.
 */
public class BoardUltra {

    private Integer hash = null;
    private byte[][] tiles;
    private byte playerX, playerY;
    private byte boxCount, boxInPlaceCount;
    private final Set<Coordinate> boxPositions = new HashSet<>();

    private BoardUltra() { }

    public BoardUltra(byte width, byte height) {
        tiles = new byte[width][height];
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                tiles[x][y] = STile.NONE_FLAG;
            }
        }
    }

    public void initializeBoxes() {
        boxPositions.clear();
        boxCount = 0;
        boxInPlaceCount = 0;
        for (byte x = 0; x < width(); x++) {
            for (byte y = 0; y < height(); y++) {
                if (STile.isBox(tiles[x][y])) {
                    boxPositions.add(new Coordinate(x, y));
                    boxCount++;
                    if (STile.forBox(tiles[x][y])) {
                        boxInPlaceCount++;
                    }
                }
            }
        }
    }

    @Override
    public BoardUltra clone() {
        BoardUltra result = new BoardUltra();
        result.tiles = new byte[width()][height()];
        for (int x = 0; x < width(); ++x) {
            System.arraycopy(this.tiles[x], 0, result.tiles[x], 0, height());
        }
        result.playerX = this.playerX;
        result.playerY = this.playerY;
        result.boxCount = this.boxCount;
        result.boxInPlaceCount = this.boxInPlaceCount;
        result.hash = this.hash;
        result.boxPositions.addAll(this.boxPositions);
        return result;
    }

    @Override
    public int hashCode() {
        if (hash == null) {
            hash = 0;
            for (byte x = 0; x < width(); ++x) {
                for (byte y = 0; y < height(); ++y) {
                    hash += (290317 * x + 97 * y) * tiles[x][y];
                }
            }
        }
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (!(obj instanceof BoardUltra)) return false;
        BoardUltra other = (BoardUltra) obj;

        if (width() != other.width() || height() != other.height()) return false;
        for (byte x = 0; x < width(); ++x) {
            for (byte y = 0; y < height(); ++y) {
                if (tiles[x][y] != other.tiles[x][y]) return false;
            }
        }
        return true;
    }

    public byte width() { return (byte) tiles.length; }
    public byte height() { return (byte) tiles[0].length; }

    public byte tile(byte x, byte y) { return tiles[x][y]; }
    public byte tile(int x, int y) { return tiles[x][y]; }

    public void movePlayer(byte srcX, byte srcY, byte tgtX, byte tgtY) {
        tiles[tgtX][tgtY] &= STile.NULLIFY_ENTITY_FLAG;
        tiles[tgtX][tgtY] |= (tiles[srcX][srcY] & STile.SOME_ENTITY_FLAG);
        tiles[srcX][srcY] &= STile.NULLIFY_ENTITY_FLAG;
        tiles[srcX][srcY] |= STile.NONE_FLAG;

        playerX = tgtX;
        playerY = tgtY;
    }

    public void moveBox(byte srcX, byte srcY, byte tgtX, byte tgtY) {
        if (STile.isBox(tiles[srcX][srcY])) {
            boxPositions.remove(new Coordinate(srcX, srcY));
            boxPositions.add(new Coordinate(tgtX, tgtY));
        }

        if (STile.forBox(tiles[tgtX][tgtY])) ++boxInPlaceCount;
        if (STile.forBox(tiles[srcX][srcY])) --boxInPlaceCount;

        tiles[tgtX][tgtY] &= STile.NULLIFY_ENTITY_FLAG;
        tiles[tgtX][tgtY] |= (tiles[srcX][srcY] & STile.SOME_ENTITY_FLAG);
        tiles[srcX][srcY] &= STile.NULLIFY_ENTITY_FLAG;
        tiles[srcX][srcY] |= STile.NONE_FLAG;
    }

    public boolean isVictory() {
        return boxCount == boxInPlaceCount;
    }

    public Set<Coordinate> getBoxes() {
        return boxPositions;
    }

    public void nullHash() {
        this.hash = null;
    }

    public void debugPrint() {
        for (int y = 0; y < height(); ++y) {
            for (int x = 0; x < width(); ++x) {
                if (STile.isBox(tiles[x][y])) System.out.print("$");
                else if (STile.forBox(tiles[x][y])) System.out.print(".");
                else if (STile.isPlayer(tiles[x][y])) System.out.print("@");
                else if (STile.isWall(tiles[x][y])) System.out.print("#");
                else System.out.print(" ");
            }
            System.out.println();
        }
    }

    public static BoardUltra fromBoardCompact(BoardCompact board) {
        BoardUltra result = new BoardUltra((byte) board.width(), (byte) board.height());

        result.boxCount = (byte) board.boxCount;
        result.boxInPlaceCount = (byte) board.boxInPlaceCount;
        result.playerX = (byte) board.playerX;
        result.playerY = (byte) board.playerY;

        for (int x = 0; x < board.width(); ++x) {
            for (int y = 0; y < board.height(); ++y) {
                byte slimTile = computeUltraTile(board.tile(x, y));
                result.tiles[x][y] = slimTile;

                // Track box positions for fast access
                if (STile.isBox(slimTile)) {
                    result.boxPositions.add(new Coordinate(x, y));
                }
            }
        }

        return result;
    }

    public static byte computeUltraTile(int compact) {
        byte result = 0;

        if (CTile.forSomeBox(compact)) result |= STile.PLACE_FLAG;
        if (CTile.isWall(compact)) return (byte) (result | STile.WALL_FLAG);
        if (CTile.isSomeBox(compact)) return (byte) (result | STile.BOX_FLAG);
        if (CTile.isPlayer(compact)) return (byte) (result | STile.PLAYER_FLAG);

        return result; // Default: Empty tile
    }




}
