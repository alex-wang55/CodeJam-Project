
import java.awt.event.KeyEvent;

public class keyInput {
    private tile gameBoard;



    // Constructor takes a reference to the main game logic
    public void GameKeyListener(tile board) {
        this.gameBoard = board;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // Delegate the action to the GameBoard object
        switch (e.getKeyCode()) {
            case KeyEvent.VK_UP:
                gameBoard.moveUp();
                System.out.println("test");
                break;
            // ... cases for DOWN, LEFT, RIGHT
            case KeyEvent.VK_DOWN:
                gameBoard.moveDown();
                break;
            case KeyEvent.VK_LEFT:
                gameBoard.moveLeft();
                break;
            case KeyEvent.VK_RIGHT:
                gameBoard.moveRight();
                break;
        }
    }
}
