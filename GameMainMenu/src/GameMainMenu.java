import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.text.SimpleDateFormat;
import java.util.List;

public class GameMainMenu extends JFrame {
    private JPanel mainPanel;
    private JLayeredPane layeredPane;
    private BackgroundPanel backgroundPanel;
    private CardLayout cardLayout;
    private JPanel cardPanel;
    
    // Colors for modern theme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color TEXT_COLOR = new Color(236, 240, 241);
    private final Color GLOW_COLOR = new Color(52, 152, 219, 100);
    
    public GameMainMenu() {
        initializeFrame();
        createBackground();
        setupCardLayout();
        createMainMenu();
        createGamesMenu();
        createHighscoresPanel();
        setupAnimations();
    }
    
    private void initializeFrame() {
        setTitle("Game Collection");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        setContentPane(layeredPane);
    }
    
    private void createBackground() {
        backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        layeredPane.add(backgroundPanel, Integer.valueOf(JLayeredPane.DEFAULT_LAYER));
    }
    
    private void setupCardLayout() {
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        cardPanel.setOpaque(false);
        backgroundPanel.add(cardPanel, BorderLayout.CENTER);
    }
    
    private void createMainMenu() {
        mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
        
        JLabel titleLabel = createStyledLabel("GAME COLLECTION", 72, TEXT_COLOR, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));
        
        JButton miniGamesButton = createMenuButton("MINI GAMES");
        JButton highscoresButton = createMenuButton("HIGHSCORES");
        JButton optionsButton = createMenuButton("OPTIONS");
        JButton creditsButton = createMenuButton("CREDITS");
        JButton exitButton = createMenuButton("EXIT GAME");
        
        miniGamesButton.addActionListener(e -> showGamesMenu());
        highscoresButton.addActionListener(e -> showHighscores());
        optionsButton.addActionListener(e -> showOptions());
        creditsButton.addActionListener(e -> showCredits());
        exitButton.addActionListener(e -> System.exit(0));
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(miniGamesButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(highscoresButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(optionsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(creditsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(exitButton);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(mainPanel);
        
        cardPanel.add(centerPanel, "MAIN_MENU");
        
        JLabel versionLabel = createStyledLabel("v1.0.0", 14, new Color(255, 255, 255, 150), false);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        backgroundPanel.add(versionLabel, BorderLayout.SOUTH);
        
        addParticles();
    }
    
    private void createGamesMenu() {
        JPanel gamesPanel = new JPanel();
        gamesPanel.setOpaque(false);
        gamesPanel.setLayout(new BoxLayout(gamesPanel, BoxLayout.Y_AXIS));
        gamesPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 100, 0));
        
        JLabel titleLabel = createStyledLabel("SELECT A GAME", 60, TEXT_COLOR, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        
        JButton snakeButton = createGameButton("SNAKE GAME", "A classic snake game. Eat apples and grow longer!", Color.GREEN);
        JButton game2048Button = createGameButton("2048 PUZZLE", "Slide tiles and combine them to reach 2048!", new Color(255, 165, 0));
        JButton flappyBirdButton = createGameButton("FLAPPY BIRD", "Click to flap and navigate through pipes!", new Color(135, 206, 250));
        JButton backButton = createMenuButton("BACK TO MAIN MENU");
        
        snakeButton.addActionListener(e -> launchSnakeGame());
        game2048Button.addActionListener(e -> launch2048Game());
        flappyBirdButton.addActionListener(e -> launchFlappyBirdGame());
        backButton.addActionListener(e -> showMainMenu());
        
        gamesPanel.add(titleLabel);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        gamesPanel.add(snakeButton);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        gamesPanel.add(game2048Button);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        gamesPanel.add(flappyBirdButton);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        gamesPanel.add(backButton);
        
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(gamesPanel);
        
        cardPanel.add(centerPanel, "GAMES_MENU");
    }
    
    private void createHighscoresPanel() {
        JPanel highscoresPanel = new JPanel(new BorderLayout(20, 20));
        highscoresPanel.setOpaque(false);
        highscoresPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        JLabel titleLabel = createStyledLabel("HIGHSCORES", 48, TEXT_COLOR, true);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        // Highscores display panel
        JPanel scoresPanel = new JPanel();
        scoresPanel.setOpaque(false);
        scoresPanel.setLayout(new BoxLayout(scoresPanel, BoxLayout.Y_AXIS));
        scoresPanel.setBorder(BorderFactory.createEmptyBorder(20, 50, 20, 50));
        
        // Create highscore displays for each game
        JPanel snakeHighscore = createGameHighscorePanel("🐍 Snake", "Snake");
        JPanel game2048Highscore = createGameHighscorePanel("🔢 2048", "2048");
        JPanel flappyBirdHighscore = createGameHighscorePanel("🐦 Flappy Bird", "Flappy Bird");
        
        scoresPanel.add(snakeHighscore);
        scoresPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        scoresPanel.add(game2048Highscore);
        scoresPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        scoresPanel.add(flappyBirdHighscore);
        
        JScrollPane scrollPane = new JScrollPane(scoresPanel);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        scrollPane.setPreferredSize(new Dimension(500, 300));
        
        // Buttons panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        JButton refreshButton = createMenuButton("🔄 REFRESH");
        JButton resetButton = createMenuButton("🔄 RESET ALL");
        JButton backButton = createMenuButton("BACK TO MAIN MENU");
        
        refreshButton.addActionListener(e -> refreshHighscores());
        resetButton.addActionListener(e -> resetAllHighscores());
        backButton.addActionListener(e -> showMainMenu());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(resetButton);
        buttonPanel.add(backButton);
        
        highscoresPanel.add(titleLabel, BorderLayout.NORTH);
        highscoresPanel.add(scrollPane, BorderLayout.CENTER);
        highscoresPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        cardPanel.add(highscoresPanel, "HIGHSCORES");
    }
    
    private JPanel createGameHighscorePanel(String gameTitle, String gameName) {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(255, 255, 255, 100), 2),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        JLabel titleLabel = new JLabel(gameTitle);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(TEXT_COLOR);
        
        String highscoreText = LeaderboardManager.getInstance().getHighscoreDisplay(gameName);
        JLabel scoreLabel = new JLabel(highscoreText);
        scoreLabel.setFont(new Font("Arial", Font.BOLD, 28));
        scoreLabel.setForeground(new Color(255, 215, 0)); // Gold color for highscore
        
        JButton resetButton = new JButton("Reset");
        resetButton.setFont(new Font("Arial", Font.PLAIN, 14));
        resetButton.setBackground(ACCENT_COLOR);
        resetButton.setForeground(TEXT_COLOR);
        resetButton.setFocusPainted(false);
        resetButton.addActionListener(e -> resetGameHighscore(gameName));
        
        panel.add(titleLabel, BorderLayout.WEST);
        panel.add(scoreLabel, BorderLayout.CENTER);
        panel.add(resetButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JButton createGameButton(String text, String description, Color accentColor) {
        JButton button = new JButton("<html><center><b>" + text + "</b><br><small>" + description + "</small></center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25);
                
                GradientPaint gradient = new GradientPaint(0, 0, accentColor, 0, getHeight(), darkenColor(accentColor, 0.7f));
                g2.setPaint(gradient);
                g2.fill(roundedRectangle);
                
                g2.setColor(new Color(255, 255, 255, 80));
                g2.setStroke(new BasicStroke(3));
                g2.draw(roundedRectangle);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 20));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(400, 100));
        button.setMaximumSize(new Dimension(400, 100));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setHorizontalTextPosition(SwingConstants.CENTER);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setPreferredSize(new Dimension(420, 105));
                button.setMaximumSize(new Dimension(420, 105));
                button.revalidate();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setPreferredSize(new Dimension(400, 100));
                button.setMaximumSize(new Dimension(400, 100));
                button.revalidate();
            }
        });
        
        return button;
    }
    
    private Color darkenColor(Color color, float factor) {
        return new Color(
            Math.max((int)(color.getRed() * factor), 0),
            Math.max((int)(color.getGreen() * factor), 0),
            Math.max((int)(color.getBlue() * factor), 0)
        );
    }
    
    private void showMainMenu() {
        cardLayout.show(cardPanel, "MAIN_MENU");
    }
    
    private void showGamesMenu() {
        cardLayout.show(cardPanel, "GAMES_MENU");
    }
    
    private void showHighscores() {
        createHighscoresPanel();
        cardLayout.show(cardPanel, "HIGHSCORES");
    }
    
    private void refreshHighscores() {
        showHighscores();
    }
    
    private void resetGameHighscore(String gameName) {
        int choice = JOptionPane.showConfirmDialog(this,
            "Reset highscore for " + gameName + "?",
            "Reset Highscore",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            LeaderboardManager.getInstance().resetHighscore(gameName);
            showHighscores(); // Refresh display
        }
    }
    
    private void resetAllHighscores() {
        int choice = JOptionPane.showConfirmDialog(this,
            "Reset ALL highscores? This cannot be undone!",
            "Reset All Highscores",
            JOptionPane.YES_NO_OPTION);
        
        if (choice == JOptionPane.YES_OPTION) {
            LeaderboardManager.getInstance().resetAllHighscores();
            showHighscores(); // Refresh display
        }
    }
    
    private JLabel createStyledLabel(String text, int fontSize, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        if (bold) {
            label.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createEmptyBorder(2, 2, 0, 0),
                BorderFactory.createMatteBorder(0, 0, 0, 0, new Color(0, 0, 0, 100))
            ));
        }
        
        return label;
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(0, 0, getWidth(), getHeight(), 25, 25);
                
                GradientPaint gradient = new GradientPaint(0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fill(roundedRectangle);
                
                g2.setColor(new Color(255, 255, 255, 50));
                g2.setStroke(new BasicStroke(2));
                g2.draw(roundedRectangle);
                
                g2.dispose();
                super.paintComponent(g);
            }
        };
        
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(TEXT_COLOR);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setPreferredSize(new Dimension(300, 60));
        button.setMaximumSize(new Dimension(300, 60));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                button.setPreferredSize(new Dimension(320, 65));
                button.setMaximumSize(new Dimension(320, 65));
                button.revalidate();
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                button.setPreferredSize(new Dimension(300, 60));
                button.setMaximumSize(new Dimension(300, 60));
                button.revalidate();
            }
        });
        
        return button;
    }
    
    private void setupAnimations() {
        Timer timer = new Timer(50, new ActionListener() {
            float hue = 0;
            
            @Override
            public void actionPerformed(ActionEvent e) {
                hue += 0.01f;
                if (hue > 1.0f) hue = 0.0f;
            }
        });
        timer.start();
    }
    
    private void addParticles() {
        JPanel particlesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                g2.setColor(new Color(255, 255, 255, 100));
                for (int i = 0; i < 20; i++) {
                    int x = (int) (Math.random() * getWidth());
                    int y = (int) (Math.random() * getHeight());
                    int size = (int) (Math.random() * 3) + 1;
                    g2.fillOval(x, y, size, size);
                }
            }
        };
        particlesPanel.setOpaque(false);
        particlesPanel.setPreferredSize(new Dimension(1200, 800));
        layeredPane.add(particlesPanel, Integer.valueOf(JLayeredPane.PALETTE_LAYER));
    }
    
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;
            
            GradientPaint gradient = new GradientPaint(0, 0, new Color(44, 62, 80), getWidth(), getHeight(), new Color(52, 73, 94));
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());
            
            g2.setColor(new Color(255, 255, 255, 5));
            for (int i = 0; i < getWidth(); i += 40) {
                for (int j = 0; j < getHeight(); j += 40) {
                    g2.fillRect(i, j, 1, 1);
                }
            }
        }
    }
    
    private void launchSnakeGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Launching Snake Game...");
                
                // Delete any existing score file
                File scoreFile = new File("snake_score.tmp");
                if (scoreFile.exists()) {
                    scoreFile.delete();
                }
                
                // Run SnakeGame as a separate process
                String javaHome = System.getProperty("java.home");
                String javaBin = javaHome + File.separator + "bin" + File.separator + "java";
                String classpath = System.getProperty("java.class.path");
                String className = "SnakeGame";
                
                ProcessBuilder builder = new ProcessBuilder(
                    javaBin, "-cp", classpath, className
                );
                Process process = builder.start();
                
                // Monitor the process and read the score when it finishes
                new Thread(() -> {
                    try {
                        int exitCode = process.waitFor();
                        System.out.println("Snake game process finished with exit code: " + exitCode);
                        
                        // Wait a bit for file to be written
                        Thread.sleep(500);
                        
                        // Read the score from file
                        if (scoreFile.exists()) {
                            try (BufferedReader reader = new BufferedReader(new FileReader(scoreFile))) {
                                String scoreLine = reader.readLine();
                                if (scoreLine != null && !scoreLine.trim().isEmpty()) {
                                    int score = Integer.parseInt(scoreLine.trim());
                                    System.out.println("Retrieved score from Snake game: " + score);
                                    
                                    // Submit the score to leaderboard
                                    SwingUtilities.invokeLater(() -> {
                                        if (score > 0) {
                                            LeaderboardManager.getInstance().submitScore("Snake", score);
                                            JOptionPane.showMessageDialog(GameMainMenu.this,
                                                "Game Over! Your score: " + score + "\nHighscore updated!",
                                                "Snake Game Result",
                                                JOptionPane.INFORMATION_MESSAGE);
                                        }
                                    });
                                }
                            }
                            // Clean up the temp file
                            scoreFile.delete();
                        }
                    } catch (Exception e) {
                        System.err.println("Error monitoring Snake game process: " + e.getMessage());
                    }
                }).start();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GameMainMenu.this, 
                    "Failed to launch Snake Game: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private void launch2048Game() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Launching 2048 Game...");
                // Create 2048 game directly
                Game2048_GUI game2048 = new Game2048_GUI();
                game2048.setVisible(true);
                
                // Bring to front
                game2048.toFront();
                game2048.requestFocus();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GameMainMenu.this, 
                    "Failed to launch 2048 Game: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }

    private void launchFlappyBirdGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                System.out.println("Launching Flappy Bird Game...");
                // Create Flappy Bird directly
                JFrame flappyFrame = new JFrame("Flappy Bird");
                FlappyBird flappyPanel = new FlappyBird();
                flappyFrame.add(flappyPanel);
                flappyFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                flappyFrame.setResizable(false);
                flappyFrame.pack();
                flappyFrame.setLocationRelativeTo(null);
                flappyFrame.setVisible(true);
                
                // Bring to front
                flappyFrame.toFront();
                flappyFrame.requestFocus();
                
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GameMainMenu.this, 
                    "Failed to launch Flappy Bird Game: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                e.printStackTrace();
            }
        });
    }
    
    private void showOptions() {
        JOptionPane.showMessageDialog(this, 
            "Game Options:\n\n" +
            "- Sound: On\n" +
            "- Difficulty: Normal\n" + 
            "- Controls: Arrow Keys\n" +
            "- Fullscreen: Off", 
            "Options", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showCredits() {
        JOptionPane.showMessageDialog(this, 
            "Game Collection v1.0\n\n" +
            "Developed by: Your Team\n\n" +
            "Games Included:\n" +
            "- Snake Game\n" +
            "- 2048 Puzzle\n" +
            "- Flappy Bird\n\n" +
            "Thanks for playing!", 
            "Credits", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameMainMenu().setVisible(true);
            }
        });
    }
}