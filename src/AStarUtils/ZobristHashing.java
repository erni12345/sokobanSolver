package AStarUtils;

import java.util.Random;

public class ZobristHashing {
    private static final long[][] playerHashes;
    private static final long[][] boxHashes;
    private static final int MAX_WIDTH = 20;  // Adjust based on your board size
    private static final int MAX_HEIGHT = 20; // Adjust based on your board size

    static {
        Random random = new Random(42069);  // Fixed seed for consistency
        playerHashes = new long[MAX_WIDTH][MAX_HEIGHT];
        boxHashes = new long[MAX_WIDTH][MAX_HEIGHT];

        for (int x = 0; x < MAX_WIDTH; x++) {
            for (int y = 0; y < MAX_HEIGHT; y++) {
                playerHashes[x][y] = random.nextLong();
                boxHashes[x][y] = random.nextLong();
            }
        }
    }

    public static long getPlayerHash(int x, int y) {
        return playerHashes[x][y];
    }

    public static long getBoxHash(int x, int y) {
        return boxHashes[x][y];
    }
}
