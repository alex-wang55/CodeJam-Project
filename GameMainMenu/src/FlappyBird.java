import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;

/**
 * A simple Flappy Bird clone created in a single Java file using Swing.
 */
public class FlappyBird extends JPanel implements ActionListener, MouseListener {

    // --- 1. Game Constants ---
    private static final int SCREEN_WIDTH = 800;
    private static final int SCREEN_HEIGHT = 600;

    // These constants will define the hitbox and the drawn size
    private static final int BIRD_WIDTH = 34;
    private static final int BIRD_HEIGHT = 24;
    private static final int BIRD_START_X = SCREEN_WIDTH / 3;
    private static final int BIRD_START_Y = SCREEN_HEIGHT / 2;

    private static final int PIPE_WIDTH = 100;
    private static final int PIPE_GAP = 200; // Vertical gap between pipes
    private static final int PIPE_SPEED = 4; // Horizontal speed of pipes
    private static final int PIPE_SPAWN_FREQUENCY = 90; // Spawn new pipe every 90 ticks

    private static final int GRAVITY = 1;
    private static final int JUMP_STRENGTH = -15; // Negative Y value is "up"

    // --- 2. Game State Variables ---
    private Rectangle bird;
    private int birdVelocityY;

    private List<Rectangle> topPipes;
    private List<Rectangle> bottomPipes;

    private Timer gameLoop;
    private boolean gameStarted;
    private boolean gameOver;
    private int score;
    private int ticks; // Used for pipe spawn timing

    private Random random;

    // *** NEW: Variable to hold the bird image ***
    private Image birdImage;

    /**
     * Constructor: Sets up the game panel and initializes game state.
     */
    public FlappyBird() {
        setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        setBackground(Color.CYAN); // Sky blue
        setFocusable(true);
        addMouseListener(this);

        random = new Random();
        gameLoop = new Timer(16, this); // 16ms delay = ~60 FPS

        // *** SIMPLIFIED: Load the bird image from parent directory ***
        birdImage = loadBirdImage();

        // Initialize game state
        resetGame();
    }

    /**
     * Try to load the bird image from the parent directory
     */
    private Image loadBirdImage() {
        try {
            // Since FlappyBird.java is in GameMainMenu/src/, go up one level to find the image
            File imageFile = new File("image_83f9c7.png");
            if (imageFile.exists()) {
                System.out.println("Found bird image at: " + imageFile.getAbsolutePath());
                return ImageIO.read(imageFile);
            }
            
            // Also try current directory in case image is copied there
            imageFile = new File("image_83f9c7.png");
            if (imageFile.exists()) {
                System.out.println("Found bird image in current directory");
                return ImageIO.read(imageFile);
            }
            
        } catch (IOException e) {
            System.err.println("Error loading bird image: " + e.getMessage());
        }

        System.err.println("Could not load bird image. Using fallback yellow rectangle.");
        return null;
    }

    /**
     * Resets the game to its initial state.
     */
    private void resetGame() {
        // Place bird in starting position
        bird = new Rectangle(BIRD_START_X, BIRD_START_Y, BIRD_WIDTH, BIRD_HEIGHT);
        birdVelocityY = 0;

        // Clear all pipes
        topPipes = new ArrayList<>();
        bottomPipes = new ArrayList<>();

        // Reset game flags and score
        gameStarted = false;
        gameOver = false;
        score = 0;
        ticks = 0;

        gameLoop.start();
    }

    /**
     * The main game loop, triggered by the Timer.
     * This method calls the update function and repaints the screen.
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameStarted && !gameOver) {
            updateGame();
        }

        // Repaint always runs to show start/end screens
        repaint();
    }

    /**
     * Updates the game state (bird position, pipes, collisions).
     */
    private void updateGame() {
        // --- 1. Update Bird ---
        birdVelocityY += GRAVITY;
        bird.y += birdVelocityY;

        // --- 2. Update Pipes ---
        ticks++;
        if (ticks % PIPE_SPAWN_FREQUENCY == 0) {
            addPipes();
        }

        // Move existing pipes to the left
        for (int i = 0; i < topPipes.size(); i++) {
            // Get both pipes
            Rectangle topPipe = topPipes.get(i);
            Rectangle bottomPipe = bottomPipes.get(i);

            // Move them
            topPipe.x -= PIPE_SPEED;
            bottomPipe.x -= PIPE_SPEED;

            // Check for scoring
            // If pipe's right edge has just passed the bird's center
            if (topPipe.x + PIPE_WIDTH < bird.x && topPipe.width > 0) {
                score++;

                // Set width to 0 to mark as "scored" AND make them disappear
                topPipe.width = 0;
                bottomPipe.width = 0; // This makes the bottom pipe disappear too
            }
        }

        // Remove off-screen pipes
        topPipes.removeIf(p -> p.x + PIPE_WIDTH < 0);
        bottomPipes.removeIf(p -> p.x + PIPE_WIDTH < 0);

        // --- 3. Check Collisions ---
        // Check for ground collision
        if (bird.y + bird.height > SCREEN_HEIGHT) {
            gameOver = true;
            submitScore();
        }

        // Check for pipe collisions
        for (Rectangle pipe : topPipes) {
            if (bird.intersects(pipe)) {
                gameOver = true;
                submitScore();
            }
        }
        for (Rectangle pipe : bottomPipes) {
            if (bird.intersects(pipe)) {
                gameOver = true;
                submitScore();
            }
        }

        // Stop the game loop if game over
        if (gameOver) {
            gameLoop.stop();
        }
    }

    /**
     * SUBMIT SCORE TO HIGHSCORE SYSTEM
     */
    private void submitScore() {
        if (score > 0) {
            // Automatically submit score without asking for name
            LeaderboardManager.getInstance().submitScore("Flappy Bird", score);
        }
    }

    /**
     * Adds a new pair of top and bottom pipes to the game.
     */
    private void addPipes() {
        // The top pipe's height is random
        int topPipeHeight = 50 + random.nextInt(SCREEN_HEIGHT - PIPE_GAP - 100);
        int bottomPipeY = topPipeHeight + PIPE_GAP;
        int bottomPipeHeight = SCREEN_HEIGHT - bottomPipeY;

        topPipes.add(new Rectangle(SCREEN_WIDTH, 0, PIPE_WIDTH, topPipeHeight));
        bottomPipes.add(new Rectangle(SCREEN_WIDTH, bottomPipeY, PIPE_WIDTH, bottomPipeHeight));
    }

    /**
     * The main drawing method, called by repaint().
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g); // Clears the screen and draws background

        // --- 1. Draw Pipes ---
        g.setColor(Color.GREEN.darker());
        for (Rectangle pipe : topPipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }
        for (Rectangle pipe : bottomPipes) {
            g.fillRect(pipe.x, pipe.y, pipe.width, pipe.height);
        }

        // --- 2. Draw Bird (MODIFIED) ---
        if (birdImage != null) {
            // Draw the loaded image
            // It will be scaled to fit the BIRD_WIDTH and BIRD_HEIGHT constants
            g.drawImage(birdImage, bird.x, bird.y, bird.width, bird.height, this);
        } else {
            // Fallback: Draw the yellow rectangle if image loading failed
            g.setColor(Color.YELLOW);
            g.fillRect(bird.x, bird.y, bird.width, bird.height);
        }

        // --- 3. Draw Score & Game State Messages ---
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 48));

        if (gameOver) {
            g.drawString("Game Over!", SCREEN_WIDTH / 2 - 130, SCREEN_HEIGHT / 2 - 50);
            g.drawString("Score: " + score, SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 20);
            g.drawString("Click to Restart", SCREEN_WIDTH / 2 - 170, SCREEN_HEIGHT / 2 + 90);
        } else if (!gameStarted) {
            g.drawString("Click to Start", SCREEN_WIDTH / 2 - 150, SCREEN_HEIGHT / 2 - 50);
        } else {
            // Draw score during gameplay
            g.drawString(String.valueOf(score), SCREEN_WIDTH / 2 - 20, 100);
        }
    }

    /**
     * Handles mouse click input.
     */
    @Override
    public void mousePressed(MouseEvent e) {
        if (gameOver) {
            // Restart the game
            resetGame();
        } else if (!gameStarted) {
            // Start the game on the first click
            gameStarted = true;
        } else {
            // "Flap" the bird
            birdVelocityY = JUMP_STRENGTH;
        }
    }

    // Unused mouse listener methods
    @Override public void mouseClicked(MouseEvent e) {}
    @Override public void mouseReleased(MouseEvent e) {}
    @Override public void mouseEntered(MouseEvent e) {}
    @Override public void mouseExited(MouseEvent e) {}

    /**
     * Main method to create the window and run the game.
     */
    public static void main(String[] args) {
        // Create the main window
        JFrame frame = new JFrame("Flappy Bird");

        // Create an instance of the game panel
        FlappyBird gamePanel = new FlappyBird();

        // Add the panel to the window
        frame.add(gamePanel);

        // Standard window setup
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setResizable(false);
        frame.pack(); // Sizes the window to fit the preferred size of the panel
        frame.setLocationRelativeTo(null); // Centers the window
        frame.setVisible(true);
    }
}