package snake;

import javax.swing.*;

public class app {
    public static void main(String[] args) throws Exception {
        int board_width = 600;
        int board_height = 600;

        JFrame frame = new JFrame("Snake");
        frame.setVisible(true);
        frame.setSize(board_width, board_height);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        Snakegame game = new Snakegame(board_width, board_height);
        frame.add(game);
        frame.pack();
        game.requestFocus();
    }
}
