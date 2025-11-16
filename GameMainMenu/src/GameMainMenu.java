import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.io.File;
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
        createLeaderboardPanel();
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
        JButton leaderboardButton = createMenuButton("LEADERBOARDS");
        JButton optionsButton = createMenuButton("OPTIONS");
        JButton creditsButton = createMenuButton("CREDITS");
        JButton exitButton = createMenuButton("EXIT GAME");
        
        miniGamesButton.addActionListener(e -> showGamesMenu());
        leaderboardButton.addActionListener(e -> showLeaderboard());
        optionsButton.addActionListener(e -> showOptions());
        creditsButton.addActionListener(e -> showCredits());
        exitButton.addActionListener(e -> System.exit(0));
        
        mainPanel.add(titleLabel);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        mainPanel.add(miniGamesButton);
        mainPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        mainPanel.add(leaderboardButton);
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
        
        // Add Flappy Bird button
        JButton snakeButton = createGameButton("SNAKE GAME", "A classic snake game. Eat apples and grow longer!", Color.GREEN);
        JButton game2048Button = createGameButton("2048 PUZZLE", "Slide tiles and combine them to reach 2048!", new Color(255, 165, 0));
        JButton flappyBirdButton = createGameButton("FLAPPY BIRD", "Click to flap and navigate through pipes!", new Color(135, 206, 250)); // Light blue
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
    
    private void createLeaderboardPanel() {
        JPanel leaderboardPanel = new JPanel(new BorderLayout(20, 20));
        leaderboardPanel.setOpaque(false);
        leaderboardPanel.setBorder(BorderFactory.createEmptyBorder(40, 60, 40, 60));
        
        JLabel titleLabel = createStyledLabel("LEADERBOARDS", 48, TEXT_COLOR, true);
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        
        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(new Color(255, 255, 255, 30));
        tabbedPane.setForeground(TEXT_COLOR);
        tabbedPane.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Add Flappy Bird leaderboard tab
        JPanel snakeLeaderboard = createGameLeaderboardPanel("Snake");
        JPanel game2048Leaderboard = createGameLeaderboardPanel("2048");
        JPanel flappyBirdLeaderboard = createGameLeaderboardPanel("Flappy Bird");
        
        tabbedPane.addTab("🐍 Snake", snakeLeaderboard);
        tabbedPane.addTab("🔢 2048", game2048Leaderboard);
        tabbedPane.addTab("🐦 Flappy Bird", flappyBirdLeaderboard);
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setOpaque(false);
        buttonPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 0));
        
        JButton refreshButton = createMenuButton("🔄 REFRESH");
        JButton backButton = createMenuButton("BACK TO MAIN MENU");
        
        refreshButton.addActionListener(e -> refreshLeaderboards());
        backButton.addActionListener(e -> showMainMenu());
        
        buttonPanel.add(refreshButton);
        buttonPanel.add(backButton);
        
        leaderboardPanel.add(titleLabel, BorderLayout.NORTH);
        leaderboardPanel.add(tabbedPane, BorderLayout.CENTER);
        leaderboardPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        cardPanel.add(leaderboardPanel, "LEADERBOARD");
    }
    private JPanel createGameLeaderboardPanel(String gameName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Get scores from leaderboard manager
        List<LeaderboardManager.ScoreEntry> scores = LeaderboardManager.getInstance().getTopScores(gameName, 10);
        
        String[] columnNames = {"Rank", "Player", "Score", "Date"};
        Object[][] data;
        
        if (scores.isEmpty()) {
            data = new Object[][]{
                {"1", "No scores yet!", "-", "-"},
                {"2", "Play the game to", "see your", "scores here!"},
                {"3", "", "", ""},
                {"4", "", "", ""},
                {"5", "", "", ""},
                {"6", "", "", ""},
                {"7", "", "", ""},
                {"8", "", "", ""},
                {"9", "", "", ""},
                {"10", "", "", ""}
            };
        } else {
            data = new Object[scores.size()][4];
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy");
            
            for (int i = 0; i < scores.size(); i++) {
                LeaderboardManager.ScoreEntry entry = scores.get(i);
                data[i][0] = i + 1;
                data[i][1] = entry.getPlayerName();
                data[i][2] = entry.getScore();
                data[i][3] = dateFormat.format(entry.getDate());
            }
        }
        
        DefaultTableModel model = new DefaultTableModel(data, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        
        // Style the table
        table.setBackground(new Color(255, 255, 255, 200));
        table.setForeground(Color.BLACK);
        table.setFont(new Font("Arial", Font.PLAIN, 14));
        table.setRowHeight(30);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Style table header
        table.getTableHeader().setFont(new Font("Arial", Font.BOLD, 16));
        table.getTableHeader().setBackground(PRIMARY_COLOR);
        table.getTableHeader().setForeground(TEXT_COLOR);
        table.getTableHeader().setReorderingAllowed(false);
        
        // Style table cells
        table.setSelectionBackground(SECONDARY_COLOR);
        table.setSelectionForeground(TEXT_COLOR);
        table.setGridColor(new Color(200, 200, 200, 100));
        table.setShowGrid(true);
        table.setIntercellSpacing(new Dimension(1, 1));
        
        // Center align rank and score columns
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(3).setCellRenderer(centerRenderer);
        
        // Left align player name column
        DefaultTableCellRenderer leftRenderer = new DefaultTableCellRenderer();
        leftRenderer.setHorizontalAlignment(SwingConstants.LEFT);
        table.getColumnModel().getColumn(1).setCellRenderer(leftRenderer);
        
        // Set column widths
        table.getColumnModel().getColumn(0).setPreferredWidth(60);  // Rank
        table.getColumnModel().getColumn(1).setPreferredWidth(150); // Player
        table.getColumnModel().getColumn(2).setPreferredWidth(80);  // Score
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Date
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(255, 255, 255, 50), 2));
        scrollPane.getViewport().setBackground(new Color(255, 255, 255, 150));
        
        // Add a title for the game
        JLabel gameTitle = createStyledLabel(gameName + " Leaderboard", 24, TEXT_COLOR, true);
        gameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        gameTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        panel.add(gameTitle, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }

    private void refreshLeaderboards() {
        // Recreate the leaderboard panel to refresh data
        createLeaderboardPanel();
        cardLayout.show(cardPanel, "LEADERBOARD");
        JOptionPane.showMessageDialog(this, "Leaderboard refreshed!", "Refresh", JOptionPane.INFORMATION_MESSAGE);
    }

    private void clearLeaderboards() {
        int choice = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to clear ALL leaderboard scores?\nThis action cannot be undone.",
            "Clear Leaderboards",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);
        
        if (choice == JOptionPane.YES_OPTION) {
            LeaderboardManager.getInstance().clearLeaderboard();
            refreshLeaderboards();
            JOptionPane.showMessageDialog(this, "All leaderboard scores have been cleared.", "Cleared", JOptionPane.INFORMATION_MESSAGE);
        }
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
    
    private void showLeaderboard() {
        createLeaderboardPanel();
        cardLayout.show(cardPanel, "LEADERBOARD");
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
        System.out.println("Launching Snake Game...");
        try {
            // Call SnakeGame's main method directly
            SnakeGame.main(new String[]{});
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to launch Snake Game: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void launch2048Game() {
        System.out.println("Launching 2048 Game...");
        try {
            // Create Game2048_GUI instance directly
            new Game2048_GUI();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "Failed to launch 2048 Game: " + e.getMessage(),
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
        
    private void launchFlappyBirdGame() {
        SwingUtilities.invokeLater(() -> {
            try {
                // Direct instantiation since it's in the same folder
                JFrame frame = new JFrame("Flappy Bird");
                FlappyBird gamePanel = new FlappyBird();
                frame.add(gamePanel);
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setResizable(false);
                frame.pack();
                frame.setLocationRelativeTo(null);
                frame.setVisible(true);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(GameMainMenu.this, 
                    "Failed to launch Flappy Bird Game: " + e.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
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