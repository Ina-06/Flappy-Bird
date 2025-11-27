import javax.swing.*;

public class App {
    public static void main(String[] args) throws Exception {
        int boardWidth = 360; // 360px 640px because thats the size of our background image
        int boardHeight = 640;

        JFrame frame = new JFrame("Flappy Bird");
        // frame.setVisible(true);
        frame.setSize(boardWidth, boardHeight); // sets size of screen
        frame.setLocationRelativeTo(null); // makes window pop up in middle of screen
        frame.setResizable(false); // doesnt allow user to resize window
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // when user clicks on X on top right, the program will terminate

        FlappyBird flappyBird = new FlappyBird();
        frame.add(flappyBird);
        frame.pack(); // 360 by 640 not including the title bar, which is what frame.pack() does
        flappyBird.requestFocus();
        frame.setVisible(true);
    }
}
