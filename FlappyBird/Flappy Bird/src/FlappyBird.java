import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.*;


public class FlappyBird extends JPanel implements ActionListener, KeyListener {
    int boardWidth = 360; // 360px 640px because thats the size of our background image
    int boardHeight = 640;

    // Images
    Image backgroundImage;
    Image birdImage;
    Image topPipeimage;
    Image bottomPipeImage;

    // Bird
    int birdX = boardWidth / 8;
    int birdY = boardHeight / 2;
    int birdWidth = 34;
    int birdHeight = 24;

    // class to make it easier to call the variables in the rest of the functions
    class Bird {
        int x = birdX;
        int y = birdY;
        int width = birdWidth;
        int height = birdHeight;
        Image img;

        Bird(Image img) {
            this.img = img;
        }
    }

    // Pipes
    int pipeX = boardWidth;
    int pipeY = 0;
    int pipeWidth = 64;    // scaled 1 / 6
    int pipeHeight = 512;  // scaled 1 / 6

    class Pipe {
        int x = pipeX;
        int y = pipeY;
        int width = pipeWidth;
        int height = pipeHeight;
        Image img;
        boolean passed = false;

        Pipe(Image img) {
            this.img = img; 
        }
    }

    // game logic
    Bird bird;
    int velocityX = -9; // simulates the bird moving to the right, but its the pipes moving left;
    int velocityY = 0; 
    int gravity = 1;

    ArrayList<Pipe> pipes;
    Random random = new Random();

    Timer placePiepesTimer;
    Timer gameLoop;
    boolean gameOver = false;
    double score = 0;

    FlappyBird() {
        setPreferredSize(new Dimension(boardWidth, boardHeight));
        // setBackground(Color.blue);
        setFocusable(true); // make sure our flappy bird class is the one that takes in our key events
        addKeyListener(this); // makes sure the 3 functions are checked (keyPressed...)

        // Load the images
        backgroundImage = new ImageIcon(getClass().getResource("./flappybirdbg.png")).getImage();
        birdImage = new ImageIcon(getClass().getResource("./flappybird.png")).getImage();
        topPipeimage = new ImageIcon(getClass().getResource("./toppipe.png")).getImage();
        bottomPipeImage = new ImageIcon(getClass().getResource("./bottompipe.png")).getImage();

        // Bird
        bird = new Bird(birdImage);
        pipes = new ArrayList<Pipe>();

        // Place pipes timer
        placePiepesTimer = new Timer(1500, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                placePipes();
            }
        });
        placePiepesTimer.start();

        // game timer
        gameLoop = new Timer(1000/60,this); // gameLoop continuosly draws the frames for our game (60 frames per second)
        gameLoop.start();
    }

    public void placePipes() {
        // Math.random() * (pipeHeight / 2) ==> will give a num between 0 - 256, basically we will shift the pipes up by a factor of 1/4 => 3/4 depending on Math.random()

        int randomPipeY = (int) (pipeY - pipeHeight / 4 - Math.random() * (pipeHeight / 2));
        int openingSpace = boardHeight/4;


        Pipe topPipe = new Pipe(topPipeimage);
        topPipe.y = randomPipeY;
        pipes.add(topPipe);

        Pipe bottomPipe = new Pipe(bottomPipeImage);
        bottomPipe.y = topPipe.y + pipeHeight + openingSpace;
        pipes.add(bottomPipe);
    }
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }
    public void draw(Graphics g) {
        // drawing the background
        g.drawImage(backgroundImage, 0, 0, boardWidth, boardHeight, null);
        // when drawing, alwasy start from top left corner (0,0) then specify width then height

        // drawing the bird
        g.drawImage(bird.img, bird.x, bird.y, bird.width, bird.height, null);

        // drawing pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            g.drawImage(pipe.img, pipe.x, pipe.y, pipe.width, pipe.height, null);
        }

        // Score
        g.setColor(Color.white);
        g.setFont(new Font("Arial", Font.PLAIN, 16));
        if (gameOver) {
            g.drawString("Game Over: "  + String.valueOf((int) score), 8, 22);
            g.drawString("Please press spacebar to restart!", 58, 320);
        } else {
            g.drawString(String.valueOf((int) score), 10, 35);
        }
    }
    public void move() {
        // Bird
        // update all the x and y positions of our objects (bird up down, pipes left)
        velocityY += gravity;
        bird.y += velocityY;
        bird.y = Math.max(bird.y, 0); // cap the y position at 0 so bird doesn't fly out of frame

        // Pipes
        for (int i = 0; i < pipes.size(); i++) {
            Pipe pipe = pipes.get(i);
            pipe.x += velocityX;

            if (!pipe.passed && bird.x > pipe.x + pipe.width) {
                pipe.passed = true;
                score += 0.5; // 0.5 because there are 2 pipes so 0.5 * 2 = 1; player gets 1 for each SET of pipes
            }

            if (collision(bird, pipe)) {
                gameOver = true;
            }
        }

        if (bird.y > boardHeight) {
            gameOver = true;
        }
    }
    public boolean collision(Bird b, Pipe p) {
        return b.x < p.x + b.width && b.x + b.width > p.x && b.y < p.y + p.height && b.y + b.height > p.y;
    }
    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        if (gameOver) {
            placePiepesTimer.stop();
            gameLoop.stop();
        }
    }
    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            velocityY = -9;
            if (gameOver) {
                // restart the game by resseting the conditions
                bird.y = birdY;
                velocityY = 0;
                pipes.clear();
                score = 0;
                gameOver = false;
                gameLoop.start();
                placePiepesTimer.start();
            }
        }
    }
    @Override
    public void keyTyped(KeyEvent e) {}
    @Override
    public void keyReleased(KeyEvent e) {}
}