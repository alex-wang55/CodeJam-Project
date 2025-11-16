import java.io.*;
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
            return Integer.compare(other.score, this.score);
        }
        
        @Override
        public String toString() {
            return String.format("%s: %d points (%s)", playerName, score, date);
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
    
    public List<ScoreEntry> getPlayerScores(String playerName) {
        List<ScoreEntry> playerScores = new ArrayList<>();
        for (ScoreEntry entry : scores) {
            if (entry.getPlayerName().equals(playerName)) {
                playerScores.add(entry);
            }
        }
        Collections.sort(playerScores);
        return playerScores;
    }
    
    @SuppressWarnings("unchecked")
    private List<ScoreEntry> loadScores() {
        File file = new File(LEADERBOARD_FILE);
        if (!file.exists()) {
            return new ArrayList<>();
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            return (List<ScoreEntry>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Could not load leaderboard, starting fresh: " + e.getMessage());
            return new ArrayList<>();
        }
    }
    
    private void saveScores() {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(LEADERBOARD_FILE))) {
            oos.writeObject(scores);
        } catch (IOException e) {
            System.out.println("Error saving leaderboard: " + e.getMessage());
        }
    }
    
    public void clearLeaderboard() {
        scores.clear();
        saveScores();
    }
}