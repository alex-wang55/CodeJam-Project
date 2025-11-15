import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

public class GameMainMenu extends JFrame {
    private JPanel mainPanel;
    private JLayeredPane layeredPane;
    private BackgroundPanel backgroundPanel;

    // Colors for modern theme
    private final Color PRIMARY_COLOR = new Color(41, 128, 185);
    private final Color SECONDARY_COLOR = new Color(52, 152, 219);
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
        JLabel titleLabel = createStyledLabel("EPIC QUEST", 72, TEXT_COLOR, true);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 60, 0));

        // Menu Buttons
        JButton startButton = createMenuButton("START GAME");
        JButton loadButton = createMenuButton("LOAD GAME");
        JButton optionsButton = createMenuButton("OPTIONS");
        JButton creditsButton = createMenuButton("CREDITS");
        JButton exitButton = createMenuButton("EXIT GAME");

        // Add action listeners
        startButton.addActionListener(e -> startGame());
        loadButton.addActionListener(e -> loadGame());
        optionsButton.addActionListener(e -> showOptions());
        creditsButton.addActionListener(e -> showCredits());
        exitButton.addActionListener(e -> System.exit(0));

        // Add components to main panel
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(startButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(loadButton);
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
                // This would be where you'd add more sophisticated animations
                // For now, we'll just update occasionally
                hue += 0.01f;
                if (hue > 1.0f) hue = 0.0f;
            }
        });
        timer.start();
    }

    private void addParticles() {
        // This would be where you add animated particles or other decorative elements
        // For simplicity, we'll just add some static decorative elements
        JPanel particlesPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                // Draw some simple particles/stars
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

    // Background panel with gradient and pattern
    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g;

            // Create gradient background
            GradientPaint gradient = new GradientPaint(
                    0, 0, new Color(44, 62, 80),
                    getWidth(), getHeight(), new Color(52, 73, 94)
            );
            g2.setPaint(gradient);
            g2.fillRect(0, 0, getWidth(), getHeight());

            // Add some subtle pattern
            g2.setColor(new Color(255, 255, 255, 5));
            for (int i = 0; i < getWidth(); i += 40) {
                for (int j = 0; j < getHeight(); j += 40) {
                    g2.fillRect(i, j, 1, 1);
                }
            }
        }
    }

    // Menu action methods
    private void startGame() {
        JOptionPane.showMessageDialog(this, "Starting New Game...");
        // Add your game start logic here
    }

    private void loadGame() {
        JOptionPane.showMessageDialog(this, "Loading Game...");
        // Add your load game logic here
    }

    private void showOptions() {
        JOptionPane.showMessageDialog(this, "Opening Options...");
        // Add your options dialog here
    }

    private void showCredits() {
        JOptionPane.showMessageDialog(this, "Game Developed by Your Team!\n\nProgrammers:\n- Developer 1\n- Developer 2\n\nArtists:\n- Artist 1\n- Artist 2");
    }

    public static void main(String[] args) {
        // Set system look and feel for better appearance - FIXED VERSION
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Create and show the menu
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new GameMainMenu().setVisible(true);
            }
        });
    }
}