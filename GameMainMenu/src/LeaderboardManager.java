import java.io.*;
import java.util.*;

public class LeaderboardManager {
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        private static final long serialVersionUID = 1L;
        
        private String gameName;
        private int score;
        private Date date;
        
        public ScoreEntry(String gameName, int score) {
            this.gameName = gameName;
            this.score = score;
            this.date = new Date();
        }
        
        public String getGameName() { return gameName; }
        public int getScore() { return score; }
        public Date getDate() { return date; }
        
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score); // Higher scores first
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d points (%s)", gameName, score, date);
        }
    }
    
    private static final String LEADERBOARD_FILE = "highscores.dat";
    private static LeaderboardManager instance;
    private List<ScoreEntry> highscores;
    
    private LeaderboardManager() {
        highscores = loadHighscores();
    }
    
    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }
    
    /**
     * Submit a score - automatically tracks only the highest score per game
     */
    public void submitScore(String gameName, int score) {
        if (score > 0) {
            // Check if we already have a highscore for this game
            ScoreEntry currentHighscore = getHighscore(gameName);
            
            // Only update if this score is higher than current highscore
            if (currentHighscore == null || score > currentHighscore.getScore()) {
                // Remove old highscore if it exists
                if (currentHighscore != null) {
                    highscores.remove(currentHighscore);
                }
                
                // Add new highscore
                ScoreEntry newHighscore = new ScoreEntry(gameName, score);
                highscores.add(newHighscore);
                Collections.sort(highscores);
                saveHighscores();
                
                System.out.println("New highscore for " + gameName + ": " + score + " points!");
            } else {
                System.out.println("Score " + score + " for " + gameName + " (current highscore: " + currentHighscore.getScore() + ")");
            }
        }
    }
    
    /**
     * Get the current highscore for a specific game
     */
    public ScoreEntry getHighscore(String gameName) {
        for (ScoreEntry entry : highscores) {
            if (entry.getGameName().equals(gameName)) {
                return entry;
            }
        }
        return null; // No highscore yet for this game
    }
    
    /**
     * Get all highscores (one per game)
     */
    public List<ScoreEntry> getAllHighscores() {
        return new ArrayList<>(highscores);
    }
    
    /**
     * Get formatted highscore display for a specific game
     */
    public String getHighscoreDisplay(String gameName) {
        ScoreEntry highscore = getHighscore(gameName);
        if (highscore != null) {
            return highscore.getScore() + " points";
        } else {
            return "No highscore yet!";
        }
    }
    
    /**
     * Reset highscore for a specific game
     */
    public void resetHighscore(String gameName) {
        ScoreEntry highscore = getHighscore(gameName);
        if (highscore != null) {
            highscores.remove(highscore);
            saveHighscores();
            System.out.println("Highscore reset for " + gameName);
        }
    }
    
    /**
     * Reset all highscores
     */
    public void resetAllHighscores() {
        highscores.clear();
        saveHighscores();
        System.out.println("All highscores reset!");
    }
    
    @SuppressWarnings("unchecked")
    private List<ScoreEntry> loadHighscores() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load highscores, starting fresh: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void saveHighscores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(highscores);
        } catch (IOException e) {
            System.out.println("Error saving highscores: " + e.getMessage());
        }
    }
}