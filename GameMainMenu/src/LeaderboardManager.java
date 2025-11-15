import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

public class LeaderboardManager {
    public static class ScoreEntry implements Serializable, Comparable<ScoreEntry> {
        private static final long serialVersionUID = 1L;
        
        private String playerName;
        private String gameName;
        private int score;
        private Date date;
        
        public ScoreEntry(String playerName, String gameName, int score) {
            this.playerName = playerName;
            this.gameName = gameName;
            this.score = score;
            this.date = new Date();
        }
        
        public String getPlayerName() { return playerName; }
        public String getGameName() { return gameName; }
        public int getScore() { return score; }
        public Date getDate() { return date; }
        
        @Override
        public int compareTo(ScoreEntry other) {
            // Higher scores first - compare other to this for descending order
            return Integer.compare(other.score, this.score);
        }
        
        @Override
        public String toString() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, yyyy HH:mm");
            return String.format("%s: %d points (%s)", playerName, score, dateFormat.format(date));
        }
    }
    
    private static final String LEADERBOARD_FILE = "leaderboard.dat";
    private static LeaderboardManager instance;
    private List<ScoreEntry> scores;
    
    private LeaderboardManager() {
        scores = loadScores();
    }
    
    public static LeaderboardManager getInstance() {
        if (instance == null) {
            instance = new LeaderboardManager();
        }
        return instance;
    }
    
    public void addScore(String playerName, String gameName, int score) {
        if (playerName != null && !playerName.trim().isEmpty() && score > 0) {
            ScoreEntry entry = new ScoreEntry(playerName.trim(), gameName, score);
            scores.add(entry);
            Collections.sort(scores);
            saveScores();
            System.out.println("Score added to leaderboard: " + entry);
        } else {
            System.out.println("Invalid score entry - name: '" + playerName + "', score: " + score);
        }
    }
    
    public List<ScoreEntry> getTopScores(String gameName, int limit) {
        List<ScoreEntry> gameScores = new ArrayList<>();
        for (ScoreEntry entry : scores) {
            if (entry.getGameName().equals(gameName)) {
                gameScores.add(entry);
            }
        }
        Collections.sort(gameScores);
        return gameScores.size() > limit ? gameScores.subList(0, limit) : gameScores;
    }
    
    public List<ScoreEntry> getTopScores(String gameName) {
        return getTopScores(gameName, 10);
    }
    
    public List<ScoreEntry> getAllScores() {
        return new ArrayList<>(scores);
    }
    
    @SuppressWarnings("unchecked")
    private List<ScoreEntry> loadScores() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            System.out.println("Leaderboard file not found, creating new one.");
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            Object loaded = ois.readObject();
            if (loaded instanceof List) {
                List<ScoreEntry> loadedScores = (List<ScoreEntry>) loaded;
                System.out.println("Leaderboard loaded successfully with " + loadedScores.size() + " entries");
                return loadedScores;
            } else {
                System.out.println("Invalid leaderboard file format");
                return new ArrayList<>();
            }
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load leaderboard, starting fresh: " + e.getMessage());
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(scores);
            System.out.println("Leaderboard saved with " + scores.size() + " entries");
        } catch (IOException e) {
            System.out.println("Error saving leaderboard: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    public void clearLeaderboard() {
        scores.clear();
        saveScores();
        System.out.println("Leaderboard cleared");
    }
    
    public void clearGameLeaderboard(String gameName) {
        scores.removeIf(entry -> entry.getGameName().equals(gameName));
        saveScores();
        System.out.println("Leaderboard cleared for game: " + gameName);
    }
    
    // For debugging
    public void printAllScores() {
        System.out.println("=== ALL SCORES IN LEADERBOARD ===");
        if (scores.isEmpty()) {
            System.out.println("No scores yet!");
        } else {
            for (ScoreEntry entry : scores) {
                System.out.println(entry);
            }
        }
        System.out.println("=================================");
    }
    
    // For debugging specific game
    public void printGameScores(String gameName) {
        System.out.println("=== SCORES FOR " + gameName.toUpperCase() + " ===");
        List<ScoreEntry> gameScores = getTopScores(gameName);
        if (gameScores.isEmpty()) {
            System.out.println("No scores yet for " + gameName);
        } else {
            for (ScoreEntry entry : gameScores) {
                System.out.println(entry);
            }
        }
        System.out.println("=================================");
    }
}