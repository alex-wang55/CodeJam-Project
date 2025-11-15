import javax.swing.*;
import java.util.Scanner;

public class Main extends JPanel {

    public static void main(String[] args) {
        Scanner console = new Scanner(System.in);
        tile start = new tile();
        tile[][] board = new tile[4][4];

        for(int i = 0; i<4; i++){
            for(int j = 0; j<4; j++){
                board[i][j] = new tile();
                board[i][j].setValue(0);
            }
        }

        board[0][0].setValue(2);
        board[0][1].setValue(2);

        // Create panel instance
        Main panel = new Main();
        panel.addKeyListener(new keyInput(start));
        panel.setFocusable(true);

        // Create window
        JFrame frame = new JFrame("2048");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.add(panel);
        frame.setSize(400, 400);
        frame.setVisible(true);

        while(true){
            // game loop (later you should replace this with a timer)
        }
    }
}
