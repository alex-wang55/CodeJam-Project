# Game Collection

A Java Swing arcade built at CodeJam 15 (November 2025) with [Zenderland](https://github.com/Zenderland). One animated main menu launches Snake, 2048 and Flappy Bird. Every game reports its score back to the menu, which keeps your best score for each game between sessions.

## What's inside

All the code is in `GameMainMenu/src/`.

- **`GameMainMenu`** is the launcher. A `CardLayout` switches between the main menu, the game picker and the highscores screen, over an animated background with custom-painted gradient buttons.
- **`SnakeGame`** (arrow keys or WASD) runs as its own JVM process. When it ends it writes the score to a temp file, which the menu reads once the process exits.
- **`Game2048_GUI`** (arrow keys) slides and merges tiles until you reach 2048 or run out of moves.
- **`FlappyBird`** (click to flap, click again to restart) runs on a roughly 60 FPS Swing `Timer` game loop.
- **`LeaderboardManager`** is a singleton that keeps one highscore per game, sorts them with `Comparable`, and saves them to `highscores.dat` with Java serialization. Scores can be reset one game at a time or all at once.

## Running it

You need a JDK. From the repo root:

```bash
javac -d build GameMainMenu/src/*.java
java -cp build GameMainMenu
```

Run it from the repo root so Flappy Bird can find its sprite (`image_83f9c7.png`); without it the bird is drawn as a yellow rectangle. Highscores are saved to `highscores.dat` in whatever folder you run it from.
