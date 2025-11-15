import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;

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
        setupAnimations();
    }
    
    private void initializeFrame() {
        setTitle("Game Main Menu");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 800);
        setLocationRelativeTo(null);
        setResizable(false);
        
        // Create layered pane for depth effects
        layeredPane = new JLayeredPane();
        layeredPane.setLayout(new OverlayLayout(layeredPane));
        setContentPane(layeredPane);
    }
    
    private void createBackground() {
        // Gradient background panel
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
        
        // Game Title
        JLabel titleLabel = createStyledLabel("GAME COLLECTION", 72, TEXT_COLOR, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));
        
        // Menu Buttons - UPDATED: First button is now "MINI GAMES"
        JButton miniGamesButton = createMenuButton("MINI GAMES");
        JButton optionsButton = createMenuButton("OPTIONS");
        JButton creditsButton = createMenuButton("CREDITS");
        JButton exitButton = createMenuButton("EXIT GAME");
        
        // Add action listeners
        miniGamesButton.addActionListener(e -> showGamesMenu());
        optionsButton.addActionListener(e -> showOptions());
        creditsButton.addActionListener(e -> showCredits());
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(miniGamesButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(optionsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(creditsButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(exitButton);
        
        // Center the main panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(mainPanel);
        
        cardPanel.add(centerPanel, "MAIN_MENU");
        
        // Add version label to background panel (not card panel)
        JLabel versionLabel = createStyledLabel("v1.0.0", 14, new Color(255, 255, 255, 150), false);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        backgroundPanel.add(versionLabel, BorderLayout.SOUTH);
        
        // Add some decorative elements
        addParticles();
    }
    
    private void createGamesMenu() {
        JPanel gamesPanel = new JPanel();
        gamesPanel.setOpaque(false);
        gamesPanel.setLayout(new BoxLayout(gamesPanel, BoxLayout.Y_AXIS));
        gamesPanel.setBorder(BorderFactory.createEmptyBorder(80, 0, 100, 0));
        
        // Games Menu Title
        JLabel titleLabel = createStyledLabel("SELECT A GAME", 60, TEXT_COLOR, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 50, 0));
        
        // Game Selection Buttons
        JButton snakeButton = createGameButton("SNAKE GAME", "A classic snake game. Eat apples and grow longer!", Color.GREEN);
        JButton game2048Button = createGameButton("2048 PUZZLE", "Slide tiles and combine them to reach 2048!", new Color(255, 165, 0));
        JButton backButton = createMenuButton("BACK TO MAIN MENU");
        
        // Add action listeners for games
        snakeButton.addActionListener(e -> launchSnakeGame());
        game2048Button.addActionListener(e -> launch2048Game());
        backButton.addActionListener(e -> showMainMenu());
        
        // Add components to games panel
        gamesPanel.add(titleLabel);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 30)));
        gamesPanel.add(snakeButton);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        gamesPanel.add(game2048Button);
        gamesPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        gamesPanel.add(backButton);
        
        // Center the games panel
        JPanel centerPanel = new JPanel(new GridBagLayout());
        centerPanel.setOpaque(false);
        centerPanel.add(gamesPanel);
        
        cardPanel.add(centerPanel, "GAMES_MENU");
    }
    
    private JButton createGameButton(String text, String description, Color accentColor) {
        JButton button = new JButton("<html><center><b>" + text + "</b><br><small>" + description + "</small></center></html>") {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Create rounded rectangle
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 25, 25);
                
                // Fill with gradient using accent color
                GradientPaint gradient = new GradientPaint(
                    0, 0, accentColor, 0, getHeight(), darkenColor(accentColor, 0.7f));
                g2.setPaint(gradient);
                g2.fill(roundedRectangle);
                
                // Add border
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
        
        // Hover effects
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
                
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 25, 25);
                
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
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
            
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(44, 62, 80), 
                getWidth(), getHeight(), new Color(52, 73, 94)
            );
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
    
    // Game launching methods (same as before)
    private void launchSnakeGame() {
        new Thread(() -> {
            try {
                System.out.println("Launching Snake Game...");
                ProcessBuilder pb = new ProcessBuilder("java", "SnakeGame");
                pb.directory(new File(System.getProperty("user.dir")));
                Process process = pb.start();
                
                int exitCode = process.waitFor();
                System.out.println("Snake game finished with exit code: " + exitCode);
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(GameMainMenu.this, 
                        "Failed to launch Snake Game: " + e.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                });
            }
        }).start();
    }
    
    private void launch2048Game() {
        new Thread(() -> {
            try {
                System.out.println("Launching 2048 Game...");
                ProcessBuilder pb = new ProcessBuilder("java", "Game2048_GUI");
                pb.directory(new File(System.getProperty("user.dir")));
                Process process = pb.start();
                
                int exitCode = process.waitFor();
                System.out.println("2048 game finished with exit code: " + exitCode);
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(GameMainMenu.this, 
                        "Failed to launch 2048 Game: " + e.getMessage(),
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    e.printStackTrace();
                });
            }
        }).start();
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
            "- 2048 Puzzle\n\n" +
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