package game.board.slim;

import AStarUtils.Coordinate;
import game.board.compact.BoardCompact;
import game.board.oop.EEntity;
import game.board.oop.EPlace;
import game.board.oop.ESpace;

import java.util.HashSet;
import java.util.Set;

/**
 * Even more compact board than {@link BoardCompact}. Ignores colors of boxes and places.
 * Can be used for levels with only one type of boxes and places.
 * <br/>
 * Roughly 50% memory-wiser representation then {@link BoardCompact}.
 */
public class BoardSlim {
	
	private Integer hash = null;
	
	public byte[][] tiles;
	
	public byte playerX;
	public byte playerY;
	
	public byte boxCount;
	public byte boxInPlaceCount;

	private final Set<Coordinate> boxPositions = new HashSet<>();
	private final Set<Coordinate> placePositions = new HashSet<>();

	private BoardSlim() {
	}
	
	public BoardSlim(byte width, byte height) {
		tiles = new byte[width][height];
		for (int x = 0; x < width; ++x) {
			for (int y = 0; y < height; ++y) {
				tiles[x][y] = 0;
			}			
		}
	}

	public void initializeBoxesAndPlaces() {
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
				else if (STile.forBox(tiles[x][y])) {
					placePositions.add(new Coordinate(x, y));
				}
			}
		}
	}


	@Override
	public BoardSlim clone() {
		BoardSlim result = new BoardSlim();
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
		if (obj == null) return false;
		if (this == obj) return true;
		if (obj.hashCode() != hashCode()) return false;
		if (!(obj instanceof BoardSlim)) return false;
		BoardSlim other = (BoardSlim) obj;
		if (width() != other.width() || height() != other.height()) return false;
		for (byte x = 0; x < width(); ++x) {
			for (byte y = 0; y < height(); ++y) {
				if (tiles[x][y] != other.tiles[x][y]) return false;
			}			
		}
		return true;
	}
	
	public byte width() {
		return (byte)tiles.length;		
	}
	
	public byte height() {
		return (byte)tiles[0].length;
	}
	
	public byte tile(byte x, byte y) {
		return tiles[x][y];
	}
	
	public byte tile(int x, int y) {
		return tiles[x][y];
	}

	public void movePlayer(byte sourceTileX, byte sourceTileY, byte targetTileX, byte targetTileY) {
		byte entity = (byte) (tiles[sourceTileX][sourceTileY] & STile.SOME_ENTITY_FLAG);

		tiles[targetTileX][targetTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;

		tiles[sourceTileX][sourceTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= STile.NONE_FLAG;

		playerX = targetTileX;
		playerY = targetTileY;
	}
	
	public void moveBox(byte sourceTileX, byte sourceTileY, byte targetTileX, byte targetTileY) {
		byte entity = (byte)(tiles[sourceTileX][sourceTileY] & STile.SOME_ENTITY_FLAG);
		
		if ((tiles[targetTileX][targetTileY] & STile.PLACE_FLAG) > 0) {
			++boxInPlaceCount;
		}
		tiles[targetTileX][targetTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[targetTileX][targetTileY] |= entity;
		
		if ((tiles[sourceTileX][sourceTileY] & STile.PLACE_FLAG) > 0) {
			--boxInPlaceCount;
		}
		tiles[sourceTileX][sourceTileY] &= STile.NULLIFY_ENTITY_FLAG;
		tiles[sourceTileX][sourceTileY] |= STile.NONE_FLAG;
	}
	
	/**
	 * Whether the board is in WIN-STATE == all boxes are in correct places.
	 * 
	 * @return
	 */
	public boolean isVictory() {
		return boxCount == boxInPlaceCount;
	}

	public Set<Coordinate> getBoxes() {
		return boxPositions;
	}

	public Set<Coordinate> getPlaces() {
		return placePositions;
	}

	public void nullHash(){
		this.hash = null;
	}
	
	public void debugPrint() {
		for (int y = 0; y < height(); ++y) {
			for (int x = 0; x < width(); ++x) {
				EEntity entity = EEntity.fromSlimFlag(tiles[x][y]);
				EPlace place = EPlace.fromSlimFlag(tiles[x][y]);
				ESpace space = ESpace.fromSlimFlag(tiles[x][y]);
				
				if (entity != null && entity != EEntity.NONE) {
					System.out.print(entity.getSymbol());
				} else
				if (place != null && place != EPlace.NONE) {
					System.out.print(place.getSymbol());
				} else
				if (space != null) {
					System.out.print(space.getSymbol());
				} else {
					System.out.print("?");
				}
			}
			System.out.println();
		}
	}
	
}

