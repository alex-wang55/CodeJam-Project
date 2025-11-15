import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

/**
 * Main class to run the Snake game.
 * It simply creates the GameFrame.
 */
public class SnakeGame {

    public static void main(String[] args) {
        // We use SwingUtilities.invokeLater to ensure that
        // the GUI creation code runs on the Event Dispatch Thread.
        SwingUtilities.invokeLater(() -> {
            new GameFrame();
        });
    }
}

/**
 * GameFrame class (the main window)
 * This class sets up the main window (JFrame) for the game.
 */
class GameFrame extends JFrame {

    GameFrame() {
        // Create an instance of the GamePanel
        GamePanel panel = new GamePanel();

        // Add the panel to the frame
        this.add(panel);

        // Set the window title
        this.setTitle("Snake");

        // Ensure the application exits when the window is closed
        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Prevent the window from being resized
        this.setResizable(false);

        // Pack the window to fit the preferred size of its components (GamePanel)
        this.pack();

        // Make the window visible
        this.setVisible(true);

        // Center the window on the screen
        this.setLocationRelativeTo(null);
    }
}

/**
 * GamePanel class (the game screen)
 * This class handles all the game logic, drawing, and user input.
 */

/**
 * GamePanel class (the game screen)
 * This class handles all the game logic, drawing, and user input.
 */
class GamePanel extends JPanel implements ActionListener {

    // --- Constants ---
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 60; // Size of each grid item (and snake body part)
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / (UNIT_SIZE * UNIT_SIZE);
    static final int DELAY = 75; // The delay for the timer (controls game speed)

    // --- Game State Arrays ---
    final int[] x = new int[GAME_UNITS];
    final int[] y = new int[GAME_UNITS];

    // --- Game State Variables ---
    int bodyParts = 6;
    int applesEaten = 0;
    int appleX;
    int appleY;
    char direction = 'R'; // 'U', 'D', 'L', 'R'
    boolean running = false;
    Timer timer;
    Random random;

    /**
     * Constructor for GamePanel.
     * Sets up the panel and starts the game.
     */
    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    /**
     * Initializes the game state.
     */
    public void startGame() {
        newApple();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    /**
     * Overrides paintComponent to handle all custom drawing.
     * @param g The Graphics object to draw with.
     */
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    /**
     * Helper method for drawing all game elements.
     * @param g The Graphics object.
     */
    public void draw(Graphics g) {
        if (running) {

            // --- 1. GRID LINES ADDED ---
            // Draw the grid lines
            g.setColor(new Color(40, 40, 40)); // Dark gray color for grid
            for (int i = 0; i < SCREEN_WIDTH / UNIT_SIZE; i++) {
                g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
            }
            for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
            }

            // Draw the apple
            g.setColor(Color.red);
            g.fillOval(appleX, appleY, UNIT_SIZE, UNIT_SIZE);

            // Draw the snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) { // The head
                    g.setColor(Color.green);
                } else { // The body
                    g.setColor(new Color(45, 180, 0));
                }
                g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
            }

            // Draw the score
            g.setColor(Color.red);
            g.setFont(new Font("Ink Free", Font.BOLD, 40));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());

        } else {
            // If the game is not running, show the "Game Over" screen
            gameOver(g);
        }
    }

    /**
     * Generates a new random position for the apple.
     */
    public void newApple() {
        appleX = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
        appleY = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
    }

    /**
     * Moves the snake by updating its coordinates.
     */
    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    /**
     * Checks if the snake's head has collided with the apple.
     */
    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;
            newApple();
        }
    }

    /**
     * Checks for all game-ending collisions.
     */
    public void checkCollisions() {
        // Check if head collides with its own body
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false;
            }
        }

        // Check if head touches left border
        if (x[0] < 0) {
            running = false;
        }
        // Check if head touches right border
        if (x[0] >= SCREEN_WIDTH) {
            running = false;
        }
        // Check if head touches top border
        if (y[0] < 0) {
            running = false;
        }
        // Check if head touches bottom border
        if (y[0] >= SCREEN_HEIGHT) {
            running = false;
        }

        if (!running) {
            timer.stop();
        }
    }

    /**
     * Displays the "Game Over" text and final score.
     * @param g The Graphics object.
     */
    public void gameOver(Graphics g) {
        // Display "Game Over" text
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game Over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);

        // Display final score
        g.setColor(Color.red);
        g.setFont(new Font("Ink Free", Font.BOLD, 40));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score: " + applesEaten)) / 2, g.getFont().getSize());
    }

    /**
     * This method is called by the Timer in each "tick" of the game loop.
     * @param e The ActionEvent.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkApple();
            checkCollisions();
        }
        repaint();
    }

    /**
     * Inner class to handle keyboard input.
     */
    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            // --- 2. WASD CONTROLS ADDED ---
            switch (e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                case KeyEvent.VK_A: // A for Left
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                case KeyEvent.VK_D: // D for Right
                    if (direction != 'L') {
                        direction = 'R';
                    }
                    break;
                case KeyEvent.VK_UP:
                case KeyEvent.VK_W: // W for Up
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                case KeyEvent.VK_S: // S for Down
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;
            }
        }
    }
}