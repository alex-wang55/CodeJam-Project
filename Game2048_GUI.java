import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.swing.*;

/**
 * A complete, self-contained 2048 game with a Java Swing GUI.
 *
 * How to run:
 * 1. Save this file as Game2048_GUI.java
 * 2. Compile: javac Game2048_GUI.java
 * 3. Run:     java Game2048_GUI
 *
 * Use the arrow keys to play.
 */
public class Game2048_GUI extends JFrame {

    // --- Game Logic Fields ---
    private int[][] board;
    private int score;
    private boolean gameWon;
    private boolean gameOver;
    private final Random rand = new Random();

    private static final int SIZE = 4;
    private static final int WIN_VALUE = 2048;

    // --- GUI Fields ---
    private BoardPanel gamePanel;
    private JLabel scoreLabel;
    private JLabel statusLabel;

    /**
     * Constructor: Sets up the main window (JFrame).
     */
    public Game2048_GUI() {
        setTitle("2048");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(false);

        // Center the window on the screen
        setSize(400, 500); // Window size
        setLocationRelativeTo(null);

        // --- Setup UI Components ---

        // Top panel for score and status
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(scoreLabel, BorderLayout.WEST);

        statusLabel = new JLabel("Use arrow keys to play!");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(statusLabel, BorderLayout.EAST);

        // Game board panel
        gamePanel = new BoardPanel();

        // Add components to the frame
        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        // --- Setup Key Listener (Controller) ---
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        // --- Finalize Window and Start Game ---
        setFocusable(true); // Important for key listener to work
        setVisible(true);
        initializeGame();
    }

    /**
     * Sets the game to its initial state.
     */
    private void initializeGame() {
        board = new int[SIZE][SIZE];
        score = 0;
        gameWon = false;
        gameOver = false;

        addRandomTile();
        addRandomTile();

        updateUI();
    }

    /**
     * Handles a key press and updates the game state.
     * @param keyCode The key code from the KeyEvent.
     */
    private void handleKeyPress(int keyCode) {
        if (gameOver || gameWon) {
            // If the game is over, don't process moves
            // (The restart logic is handled by the popup dialog)
            return;
        }

        boolean boardChanged = false;

        switch (keyCode) {
            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_A:
                boardChanged = move('a');
                break;
            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_D:
                boardChanged = move('d');
                break;
            case KeyEvent.VK_UP:
            case KeyEvent.VK_W:
                boardChanged = move('w');
                break;
            case KeyEvent.VK_DOWN:
            case KeyEvent.VK_S:
                boardChanged = move('s');
                break;
        }

        // --- After a move ---
        if (boardChanged) {
            addRandomTile();
            checkGameOver();
            updateUI();

            // Check for game over or win state to show popup
            if (gameOver || gameWon) {
                showEndGameDialog();
            }
        }
    }

    /**
     * Shows the final "Game Over" or "You Win" dialog with a restart option.
     */
    private void showEndGameDialog() {
        String message = gameOver ? "Game Over!" : "You Win!";
        String title = gameOver ? "Game Over" : "Congratulations!";

        int choice = JOptionPane.showConfirmDialog(
                this,
                message + "\nYour final score is: " + score + "\n\nPlay Again?",
                title,
                JOptionPane.YES_NO_OPTION
        );

        if (choice == JOptionPane.YES_OPTION) {
            initializeGame(); // Restart the game
        } else {
            // User wants to quit, or just close the dialog
            // You could also call System.exit(0) if you want the app to close
        }
    }

    /**
     * Refreshes the score and status labels, and repaints the board.
     */
    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        if (gameWon) {
            statusLabel.setText("You Win!");
        } else if (gameOver) {
            statusLabel.setText("Game Over!");
        } else {
            statusLabel.setText("Use arrow keys!");
        }

        // Tell the custom panel to redraw itself
        gamePanel.repaint();
    }


    // =================================================================
    //  CORE 2048 GAME LOGIC (Adapted from console version)
    // =================================================================

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
     * Core logic: processes all rows for a 'left' move.
     * It slides tiles, merges them, and slides again.
     */
    private void moveLeft() {
        for (int i = 0; i < SIZE; i++) {
            int[] row = board[i];
            int[] processedRow = processRow(row);
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
        List<Integer> nonZeroTiles = new ArrayList<>();
        for (int value : row) {
            if (value != 0) nonZeroTiles.add(value);
        }

        List<Integer> mergedTiles = new ArrayList<>();
        for (int i = 0; i < nonZeroTiles.size(); i++) {
            if (i < nonZeroTiles.size() - 1 &&
                    nonZeroTiles.get(i).equals(nonZeroTiles.get(i + 1))) {

                int mergedValue = nonZeroTiles.get(i) * 2;
                mergedTiles.add(mergedValue);
                score += mergedValue;
                if (mergedValue == WIN_VALUE) gameWon = true;
                i++; // Skip the next tile
            } else {
                mergedTiles.add(nonZeroTiles.get(i));
            }
        }

        int[] newRow = new int[SIZE];
        for (int i = 0; i < mergedTiles.size(); i++) {
            newRow[i] = mergedTiles.get(i);
        }
        return newRow;
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
            int[] cell = emptyCells.get(rand.nextInt(emptyCells.size()));
            int row = cell[0];
            int col = cell[1];
            board[row][col] = (rand.nextInt(10) == 0) ? 4 : 2;
        }
    }

    /**
     * Checks if the game is over.
     */
    private void checkGameOver() {
        if (gameWon || !isBoardFull()) {
            return;
        }
        if (!canMove()) {
            gameOver = true;
        }
    }

    private boolean isBoardFull() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) return false;
            }
        }
        return true;
    }

    private boolean canMove() {
        // Check horizontal
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (board[i][j] == board[i][j + 1]) return true;
            }
        }
        // Check vertical
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE - 1; i++) {
                if (board[i][j] == board[i + 1][j]) return true;
            }
        }
        return false;
    }


    // =================================================================
    //  Inner Class for the GUI Board (The View)
    // =================================================================

    /**
     * The custom JPanel that handles all the drawing of the game board.
     */
    private class BoardPanel extends JPanel {

        private static final int TILE_MARGIN = 15;

        // Colors for the tiles
        private Color getTileColor(int value) {
            switch (value) {
                case 0: return new Color(0xCDC1B4); // Empty
                case 2: return new Color(0xEEE4DA);
                case 4: return new Color(0xEDE0C8);
                case 8: return new Color(0xF2B179);
                case 16: return new Color(0xF59563);
                case 32: return new Color(0xF67C5F);
                case 64: return new Color(0xF65E3B);
                case 128: return new Color(0xEDCF72);
                case 256: return new Color(0xEDCC61);
                case 512: return new Color(0xEDC850);
                case 1024: return new Color(0xEDC53F);
                case 2048: return new Color(0xEDC22E);
                default: return new Color(0x3C3A32); // Other high numbers
            }
        }

        // Colors for the text on the tiles
        private Color getTextColor(int value) {
            return (value < 16) ? new Color(0x776E65) : new Color(0xF9F6F2);
        }

        /**
         * This is the main drawing method.
         */
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            // Cast to Graphics2D for better rendering (antialiasing)
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                    RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING,
                    RenderingHints.VALUE_RENDER_QUALITY);

            // Draw the background of the board
            g2d.setColor(new Color(0xBBADA0));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            // Get the size of each tile based on the panel size
            int panelSize = Math.min(getWidth(), getHeight());
            int tileSize = (panelSize - TILE_MARGIN * (SIZE + 1)) / SIZE;

            // Calculate offsets to center the board
            int xOffset = (getWidth() - (tileSize * SIZE + TILE_MARGIN * (SIZE + 1))) / 2;
            int yOffset = (getHeight() - (tileSize * SIZE + TILE_MARGIN * (SIZE + 1))) / 2;


            // Loop through the board and draw each tile
            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];

                    // Calculate the top-left corner of the current tile
                    int x = xOffset + TILE_MARGIN + (j * (tileSize + TILE_MARGIN));
                    int y = yOffset + TILE_MARGIN + (i * (tileSize + TILE_MARGIN));

                    // Draw the tile
                    g2d.setColor(getTileColor(value));
                    g2d.fillRoundRect(x, y, tileSize, tileSize, 10, 10); // Rounded corners

                    // Draw the number on the tile (if value is not 0)
                    if (value != 0) {
                        g2d.setColor(getTextColor(value));

                        // Adjust font size based on number of digits
                        int fontSize;
                        if (value < 128) fontSize = 36;
                        else if (value < 1024) fontSize = 32;
                        else fontSize = 28;

                        Font font = new Font("Arial", Font.BOLD, fontSize);
                        g2d.setFont(font);

                        // Center the text in the tile
                        String s = String.valueOf(value);
                        FontMetrics fm = g2d.getFontMetrics(font);
                        int textX = x + (tileSize - fm.stringWidth(s)) / 2;
                        int textY = y + (tileSize - fm.getHeight()) / 2 + fm.getAscent();

                        g2d.drawString(s, textX, textY);
                    }
                }
            }
        }
    }


    // =================================================================
    //  Main Method
    // =================================================================

    /**
     * Main method to run the game.
     */
    public static void main(String[] args) {
        // Run the GUI creation on the Event Dispatch Thread (EDT)
        // This is the standard, safe way to start a Swing application.
        SwingUtilities.invokeLater(() -> new Game2048_GUI());
    }
}