import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public class Game2048 {

    private int[][] board;
    private int score;
    private boolean gameWon;
    private boolean gameOver;
    private Random rand;

    // Defines the size of the grid (4x4)
    private static final int SIZE = 4;
    // The tile value to win the game
    private static final int WIN_VALUE = 2048;

    /**
     * Constructor to initialize the game.
     */
    public Game2048() {
        board = new int[SIZE][SIZE];
        score = 0;
        gameWon = false;
        gameOver = false;
        rand = new Random();

        // Start the game with two random tiles
        addRandomTile();
        addRandomTile();
    }

    /**
     * Main game loop.
     */
    public void runGame() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            printBoard();

            if (gameOver) {
                System.out.println("Game Over! No more moves.");
                System.out.println("Final Score: " + score);
                break;
            }
            if (gameWon) {
                System.out.println("You Win! You reached 2048!");
                System.out.println("Final Score: " + score);
                break;
            }

            System.out.println("Enter move (w=up, a=left, s=down, d=right, q=quit):");
            String input = scanner.next().toLowerCase();

            if (input.equals("q")) {
                System.out.println("Quitting game. Final Score: " + score);
                break;
            }

            // Make a move and check if the board changed
            boolean boardChanged = move(input.charAt(0));

            if (boardChanged) {
                // Only add a new tile if a valid move was made
                addRandomTile();

                // Check for game over conditions after adding a new tile
                checkGameOver();
            } else {
                System.out.println("Invalid move. Try again.");
            }
        }
        scanner.close();
    }

    /**
     * Prints the current state of the game board and score.
     */
    private void printBoard() {
        // Clear console (simple way, works in many terminals)
        // System.out.print("\033[H\033[2J");
        // System.out.flush();

        System.out.println("============================");
        System.out.println("Score: " + score);
        System.out.println("============================");
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    System.out.printf("%6s", ".");
                } else {
                    // %6d formats the number to take up 6 spaces for alignment
                    System.out.printf("%6d", board[i][j]);
                }
            }
            System.out.println("\n"); // Extra newline for spacing
        }
        System.out.println("============================");
    }

    /**
     * Adds a new tile (90% chance of '2', 10% chance of '4')
     * to a random empty cell on the board.
     */
    private void addRandomTile() {
        List<int[]> emptyCells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    emptyCells.add(new int[]{i, j});
                }
            }
        }

        if (!emptyCells.isEmpty()) {
            // Pick a random empty cell
            int[] cell = emptyCells.get(rand.nextInt(emptyCells.size()));
            int row = cell[0];
            int col = cell[1];

            // 90% chance for a 2, 10% chance for a 4
            int value = (rand.nextInt(10) == 0) ? 4 : 2;
            board[row][col] = value;
        }
    }

    /**
     * Main move handler.
     * Uses rotation to simplify move logic.
     * All moves (up, down, right) are transformed into a 'left' move,
     * processed, and then rotated back.
     *
     * @param direction 'w', 'a', 's', or 'd'
     * @return true if the board changed, false otherwise
     */
    private boolean move(char direction) {
        // Create a copy of the board to check for changes later
        int[][] originalBoard = new int[SIZE][];
        for (int i = 0; i < SIZE; i++) {
            originalBoard[i] = Arrays.copyOf(board[i], SIZE);
        }

        switch (direction) {
            case 'a': // Left
                moveLeft();
                break;
            case 'd': // Right
                rotateBoard(2); // Rotate 180 degrees
                moveLeft();
                rotateBoard(2); // Rotate back
                break;
            case 'w': // Up
                rotateBoard(3); // Rotate 270 degrees (or -90)
                moveLeft();
                rotateBoard(1); // Rotate 90 degrees to restore
                break;
            case 's': // Down
                rotateBoard(1); // Rotate 90 degrees
                moveLeft();
                rotateBoard(3); // Rotate 270 degrees (or -90) to restore
                break;
            default:
                return false; // Invalid key
        }

        // Check if the board is different from its original state
        return !Arrays.deepEquals(originalBoard, board);
    }

    /**
     * Rotates the board 90 degrees clockwise, 'times' number of times.
     */
    private void rotateBoard(int times) {
        for (int t = 0; t < times; t++) {
            int[][] newBoard = new int[SIZE][SIZE];
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    newBoard[j][SIZE - 1 - i] = board[i][j];
                }
            }
            board = newBoard;
        }
    }

    /**
     * Core logic: processes all rows for a 'left' move.
     * It slides tiles, merges them, and slides again.
     */
    private void moveLeft() {
        for (int i = 0; i < SIZE; i++) {
            // 1. Get the current row
            int[] row = board[i];

            // 2. Process the row (slide, merge, slide)
            int[] processedRow = processRow(row);

            // 3. Update the board with the new row
            board[i] = processedRow;
        }
    }

    /**
     * Processes a single row.
     * 1. Slides all non-zero tiles to the left.
     * 2. Merges adjacent identical tiles.
     * 3. Slides again to fill gaps from merges.
     *
     * @param row The row to process
     * @return The new, processed row
     */
    private int[] processRow(int[] row) {
        // 1. Slide: Filter out all zeros
        List<Integer> nonZeroTiles = new ArrayList<>();
        for (int value : row) {
            if (value != 0) {
                nonZeroTiles.add(value);
            }
        }

        // 2. Merge
        List<Integer> mergedTiles = new ArrayList<>();
        for (int i = 0; i < nonZeroTiles.size(); i++) {
            if (i < nonZeroTiles.size() - 1 &&
                    nonZeroTiles.get(i).equals(nonZeroTiles.get(i + 1))) {

                // Merge pair
                int mergedValue = nonZeroTiles.get(i) * 2;
                mergedTiles.add(mergedValue);

                // Update score and check for win
                score += mergedValue;
                if (mergedValue == WIN_VALUE) {
                    gameWon = true;
                }

                i++; // Skip the next tile since it was merged
            } else {
                // No merge, just add the tile
                mergedTiles.add(nonZeroTiles.get(i));
            }
        }

        // 3. Create final row and slide again
        int[] newRow = new int[SIZE];
        for (int i = 0; i < mergedTiles.size(); i++) {
            newRow[i] = mergedTiles.get(i);
        }
        // The rest of newRow is already 0 by default, completing the slide.

        return newRow;
    }

    /**
     * Checks if the game is over (either board is full and no moves, or board is not full).
     */
    private void checkGameOver() {
        if (gameWon) {
            return; // Game is already won
        }

        if (!isBoardFull()) {
            return; // Not over, still has empty spots
        }

        if (!canMove()) {
            gameOver = true; // Board is full AND no moves are possible
        }
    }

    /**
     * Checks if the board has any empty (0) cells.
     */
    private boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) {
                    return false; // Found an empty cell
                }
            }
        }
        return true; // No empty cells
    }

    /**
     * Checks if any adjacent (horizontal or vertical) tiles are identical.
     * This is used to determine if a move is possible when the board is full.
     */
    private boolean canMove() {
        // Check for horizontal merges
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (board[i][j] == board[i][j + 1]) {
                    return true;
                }
            }
        }

        // Check for vertical merges
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE - 1; i++) {
                if (board[i][j] == board[i + 1][j]) {
                    return true;
                }
            }
        }

        return false; // No possible merges
    }


    /**
     * Main method to create and run the game.
     */
    public static void main(String[] args) {
        Game2048 game = new Game2048();
        game.runGame();
    }
}