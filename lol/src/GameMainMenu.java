import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.*;

public class GameMainMenu extends JFrame {
    private JPanel mainPanel;
    private JLayeredPane layeredPane;
    private BackgroundPanel backgroundPanel;
    
    // Colors for modern theme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
    private final Color ACCENT_COLOR = new Color(231, 76, 60);
    private final Color TEXT_COLOR = new Color(236, 240, 241);
    private final Color GLOW_COLOR = new Color(52, 152, 219, 100);
    
    public GameMainMenu() {
        initializeFrame();
        createBackground();
        createMainMenu();
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
    
    private void createMainMenu() {
        mainPanel = new JPanel();
        mainPanel.setOpaque(false);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(100, 0, 100, 0));
        
        // Game Title
        JLabel titleLabel = createStyledLabel("GAME COLLECTION", 72, TEXT_COLOR, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));
        
        // Menu Buttons
        JButton snakeButton = createMenuButton("PLAY SNAKE");
        JButton game2048Button = createMenuButton("PLAY 2048");
        JButton optionsButton = createMenuButton("OPTIONS");
        JButton creditsButton = createMenuButton("CREDITS");
        JButton exitButton = createMenuButton("EXIT GAME");
        
        // Add action listeners for games
        snakeButton.addActionListener(e -> launchSnakeGame());
        game2048Button.addActionListener(e -> launch2048Game());
        optionsButton.addActionListener(e -> showOptions());
        creditsButton.addActionListener(e -> showCredits());
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(snakeButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(game2048Button);
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
        
        backgroundPanel.add(centerPanel, BorderLayout.CENTER);
        
        // Add version label
        JLabel versionLabel = createStyledLabel("v1.0.0", 14, new Color(255, 255, 255, 150), false);
        versionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 20));
        backgroundPanel.add(versionLabel, BorderLayout.SOUTH);
        
        // Add some decorative elements
        addParticles();
    }
    
    private JLabel createStyledLabel(String text, int fontSize, Color color, boolean bold) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Arial", bold ? Font.BOLD : Font.PLAIN, fontSize));
        label.setForeground(color);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        if (bold) {
            // Add text shadow effect
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
                
                // Create rounded rectangle
                RoundRectangle2D roundedRectangle = new RoundRectangle2D.Float(
                    0, 0, getWidth(), getHeight(), 25, 25);
                
                // Fill with gradient
                GradientPaint gradient = new GradientPaint(
                    0, 0, PRIMARY_COLOR, 0, getHeight(), SECONDARY_COLOR);
                g2.setPaint(gradient);
                g2.fill(roundedRectangle);
                
                // Add border
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
        
        // Hover effects
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setCursor(new Cursor(Cursor.HAND_CURSOR));
                // Scale effect
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
        // Simple title animation
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
    
    // UPDATED GAME LAUNCHING METHODS USING PROCESSBUILDER
    private void launchSnakeGame() {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", ".", "SnakeGame");
                pb.directory(new File(System.getProperty("user.dir")));
                pb.inheritIO(); // This shows the game output in the same terminal
                Process process = pb.start();
                
                // Wait for the process to complete (optional)
                int exitCode = process.waitFor();
                System.out.println("Snake game exited with code: " + exitCode);
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> showGameError("Snake", e));
            }
        }).start();
    }
    
    private void launch2048Game() {
        new Thread(() -> {
            try {
                ProcessBuilder pb = new ProcessBuilder("java", "-cp", ".", "Game2048_GUI");
                pb.directory(new File(System.getProperty("user.dir")));
                pb.inheritIO(); // This shows the game output in the same terminal
                Process process = pb.start();
                
                // Wait for the process to complete (optional)
                int exitCode = process.waitFor();
                System.out.println("2048 game exited with code: " + exitCode);
                
            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> showGameError("2048", e));
            }
        }).start();
    }
    
    private void showGameError(String gameName, Exception e) {
        JOptionPane.showMessageDialog(this, 
            "Error launching " + gameName + " Game!\n\n" +
            "Please make sure:\n" +
            "1. " + gameName + ".java is compiled to " + gameName + ".class\n" +
            "2. All files are in the same directory\n" +
            "3. Java is in your PATH\n\n" +
            "Error: " + e.getMessage(), 
            "Launch Error", 
            JOptionPane.ERROR_MESSAGE);
        e.printStackTrace();
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