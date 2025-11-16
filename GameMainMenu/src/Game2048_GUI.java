import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

public class Game2048_GUI extends JFrame {
    private int[][] board;
    private int score;
    private boolean gameWon;
    private boolean gameOver;
    private final Random rand = new Random();
    private static final int SIZE = 4;
    private static final int WIN_VALUE = 2048;
    private BoardPanel gamePanel;
    private JLabel scoreLabel;
    private JLabel statusLabel;

    public Game2048_GUI() {
        setTitle("2048 Game");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(false);
        setSize(400, 500);
        setLocationRelativeTo(null);

        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        scoreLabel = new JLabel("Score: 0");
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 16));
        topPanel.add(scoreLabel, BorderLayout.WEST);

        statusLabel = new JLabel("Use arrow keys to play!");
        statusLabel.setFont(new Font("Arial", Font.PLAIN, 16));
        topPanel.add(statusLabel, BorderLayout.EAST);

        gamePanel = new BoardPanel();
        add(topPanel, BorderLayout.NORTH);
        add(gamePanel, BorderLayout.CENTER);

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                handleKeyPress(e.getKeyCode());
            }
        });

        setFocusable(true);
        setVisible(true);
        initializeGame();
    }

    private void initializeGame() {
        board = new int[SIZE][SIZE];
        score = 0;
        gameWon = false;
        gameOver = false;
        addRandomTile();
        addRandomTile();
        updateUI();
    }

    private void handleKeyPress(int keyCode) {
        if (gameOver || gameWon) return;

        boolean boardChanged = false;
        switch (keyCode) {
            case KeyEvent.VK_LEFT: boardChanged = move('a'); break;
            case KeyEvent.VK_RIGHT: boardChanged = move('d'); break;
            case KeyEvent.VK_UP: boardChanged = move('w'); break;
            case KeyEvent.VK_DOWN: boardChanged = move('s'); break;
        }

        if (boardChanged) {
            addRandomTile();
            checkGameOver();
            updateUI();
            if (gameOver || gameWon) showEndGameDialog();
        }
    }

    private void showEndGameDialog() {
        String message = gameOver ? "Game Over!" : "You Win!";
        String title = gameOver ? "Game Over" : "Congratulations!";
        
        // Automatically submit score without asking for name
        if (score > 0) {
            LeaderboardManager.getInstance().submitScore("2048", score);
        }
        
        int choice = JOptionPane.showConfirmDialog(this, 
            message + "\nYour score: " + score + "\n\nPlay Again?", 
            title, JOptionPane.YES_NO_OPTION);
        if (choice == JOptionPane.YES_OPTION) initializeGame();
    }

    private void updateUI() {
        scoreLabel.setText("Score: " + score);
        if (gameWon) statusLabel.setText("You Win!");
        else if (gameOver) statusLabel.setText("Game Over!");
        else statusLabel.setText("Use arrow keys!");
        gamePanel.repaint();
    }

    private boolean move(char direction) {
        int[][] originalBoard = new int[SIZE][];
        for (int i = 0; i < SIZE; i++) {
            originalBoard[i] = Arrays.copyOf(board[i], SIZE);
        }

        switch (direction) {
            case 'a': moveLeft(); break;
            case 'd': rotateBoard(2); moveLeft(); rotateBoard(2); break;
            case 'w': rotateBoard(3); moveLeft(); rotateBoard(1); break;
            case 's': rotateBoard(1); moveLeft(); rotateBoard(3); break;
            default: return false;
        }
        return !Arrays.deepEquals(originalBoard, board);
    }

    private void moveLeft() {
        for (int i = 0; i < SIZE; i++) {
            board[i] = processRow(board[i]);
        }
    }

    private int[] processRow(int[] row) {
        java.util.List<Integer> nonZeroTiles = new java.util.ArrayList<>();
        for (int value : row) {
            if (value != 0) nonZeroTiles.add(value);
        }

        java.util.List<Integer> mergedTiles = new java.util.ArrayList<>();
        for (int i = 0; i < nonZeroTiles.size(); i++) {
            if (i < nonZeroTiles.size() - 1 && nonZeroTiles.get(i).equals(nonZeroTiles.get(i + 1))) {
                int mergedValue = nonZeroTiles.get(i) * 2;
                mergedTiles.add(mergedValue);
                score += mergedValue;
                if (mergedValue == WIN_VALUE) gameWon = true;
                i++;
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

    private void addRandomTile() {
        java.util.List<int[]> emptyCells = new java.util.ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (board[i][j] == 0) emptyCells.add(new int[]{i, j});
            }
        }
        if (!emptyCells.isEmpty()) {
            int[] cell = emptyCells.get(rand.nextInt(emptyCells.size()));
            board[cell[0]][cell[1]] = (rand.nextInt(10) == 0) ? 4 : 2;
        }
    }

    private void checkGameOver() {
        if (gameWon || !isBoardFull()) return;
        if (!canMove()) gameOver = true;
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
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                if (board[i][j] == board[i][j + 1]) return true;
            }
        }
        for (int j = 0; j < SIZE; j++) {
            for (int i = 0; i < SIZE - 1; i++) {
                if (board[i][j] == board[i + 1][j]) return true;
            }
        }
        return false;
    }

    private class BoardPanel extends JPanel {
        private static final int TILE_MARGIN = 15;

        private Color getTileColor(int value) {
            switch (value) {
                case 0: return new Color(0xCDC1B4);
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
                default: return new Color(0x3C3A32);
            }
        }

        private Color getTextColor(int value) {
            return (value < 16) ? new Color(0x776E65) : new Color(0xF9F6F2);
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2d.setColor(new Color(0xBBADA0));
            g2d.fillRect(0, 0, getWidth(), getHeight());

            int panelSize = Math.min(getWidth(), getHeight());
            int tileSize = (panelSize - TILE_MARGIN * (SIZE + 1)) / SIZE;
            int xOffset = (getWidth() - (tileSize * SIZE + TILE_MARGIN * (SIZE + 1))) / 2;
            int yOffset = (getHeight() - (tileSize * SIZE + TILE_MARGIN * (SIZE + 1))) / 2;

            for (int i = 0; i < SIZE; i++) {
                for (int j = 0; j < SIZE; j++) {
                    int value = board[i][j];
                    int x = xOffset + TILE_MARGIN + (j * (tileSize + TILE_MARGIN));
                    int y = yOffset + TILE_MARGIN + (i * (tileSize + TILE_MARGIN));

                    g2d.setColor(getTileColor(value));
                    g2d.fillRoundRect(x, y, tileSize, tileSize, 10, 10);

                    if (value != 0) {
                        g2d.setColor(getTextColor(value));
                        int fontSize = value < 128 ? 36 : value < 1024 ? 32 : 28;
                        Font font = new Font("Arial", Font.BOLD, fontSize);
                        g2d.setFont(font);
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
}