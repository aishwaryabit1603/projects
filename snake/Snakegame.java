package snake;
// awt - abstract window toolkit

// used to develope window based applications

import java.awt.*;
import java.awt.event.*;

// to store segments of snake body
import java.util.ArrayList;

// used to randomly place food for snake
import java.util.Random;

import javax.swing.*;

public class Snakegame extends JPanel implements ActionListener, KeyListener {
    private class Tile {
        int x;
        int y;
        // top left (0,0)
        // botton right (600,600)

        Tile(int x, int y) {
            this.x = x;
            this.y = y;
        }
    }

    int board_width;
    int board_height;

    // size of each cell on grid
    int tileSize = 25;

    // snake
    Tile snakehead;

    // snakeBody
    ArrayList<Tile> snakebody;

    // food
    Tile food;

    // obstacles
    Tile obstacles;

    Random random;

    // game logic
    Timer gameLoop;
    // to move snake
    int velocityX;
    int velocityY;
    boolean gameOver = false;

    JButton restartButton;
    JButton scoreButton;

    Snakegame(int bw, int hw) {
        this.board_height = hw;
        this.board_width = bw;
        setPreferredSize(new Dimension(this.board_width, this.board_height));
        setBackground(new Color(173, 216, 230));
        addKeyListener(this);
        setFocusable(true);

        snakehead = new Tile(5, 5);
        // 10 units right 10 units down
        food = new Tile(10, 10);
        snakebody = new ArrayList<>();
        obstacles = new Tile(20, 20);

        random = new Random();
        placeFood();
        if (snakebody.size() > 10) {
            placeobstacle();
        }

        velocityX = 0;
        velocityY = 0;

        // 100 ms
        gameLoop = new Timer(100, this);
        gameLoop.start();

        // create the restart button
        restartButton = new JButton("Restart");

        // button.setBounds(x,y,width,height)
        restartButton.setBounds(board_width / 2 - 50, board_height / 2 - 15, 100, 30);
        restartButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });
        add(restartButton);
        restartButton.setVisible(false);

        scoreButton = new JButton("Score : 0");
        scoreButton.setFont(new Font("Arial", Font.BOLD, 16));
        scoreButton.setBounds(10, 10, 150, 30);
        scoreButton.setBackground(Color.WHITE);
        scoreButton.setForeground(Color.BLACK);
        // make score button non click able
        scoreButton.setEnabled(false);
        add(scoreButton);
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
        if (gameOver) {
            restartButton.setVisible(true);
            scoreButton.setText("GAME OVER: " + snakebody.size());
        }
    }

    // draw method where custom graphics are drawn
    public void draw(Graphics g) {

        // grid
        // g.setColor(Color.white);
        // for (int i = 0; i < board_width / tileSize; i++) {
        // // vertical line
        // g.drawLine(i * tileSize, 0, i * tileSize, board_height);

        // // horizontal line
        // g.drawLine(0, i * tileSize, board_width, i * tileSize);
        // }

        // custom object - snakehead
        g.setColor(new Color(144, 238, 144));
        g.fillOval(snakehead.x * tileSize, snakehead.y * tileSize, tileSize, tileSize);
        g.setColor(Color.BLACK); // outline color
        g.drawOval(snakehead.x * tileSize, snakehead.y * tileSize, tileSize, tileSize);

        // creating new parts of snake body when collided with food
        for (int i = 0; i < snakebody.size(); i++) {
            g.setColor(new Color(60, 179, 113));
            Tile snakePart = snakebody.get(i);
            g.fillOval(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
            g.setColor(Color.BLACK); // outline color
            g.drawOval(snakePart.x * tileSize, snakePart.y * tileSize, tileSize, tileSize);
        }
        // custom object - food
        g.setColor(new Color(255, 182, 193));
        g.fill3DRect(food.x * tileSize, food.y * tileSize, tileSize, tileSize, true);

        // Obstacles
        g.setColor(new Color(210, 180, 140));
        g.fill3DRect(obstacles.x * tileSize, obstacles.y * tileSize, tileSize, 3 * tileSize, true);

    }

    public void placeFood() {
        // food will be btw 0 to 24 box - 25 tiles in total
        food.x = random.nextInt(board_width / tileSize);
        food.y = random.nextInt(board_height / tileSize);
    }

    public void placeobstacle() {
        while (true) {
            // obstacle shouldn't collide with food
            obstacles.x = random.nextInt(board_width / tileSize);
            obstacles.y = random.nextInt(board_height / tileSize);
            if (!collision(obstacles, food)) {
                break;
            }
        }
    }

    public boolean collision(Tile tile1, Tile tile2) {
        return tile1.x == tile2.x && tile1.y == tile2.y;
    }

    public void move() {

        if (gameOver)
            return;
        // to move body firstly last tile will be moved
        // then 2nd last and at last head

        // eat food

        if (collision(snakehead, food)) {
            snakebody.add(new Tile(food.x, food.y));
            placeFood();
            placeobstacle();
            scoreButton.setText("SCORE : " + snakebody.size());
        }

        // move snake body
        for (int i = snakebody.size() - 1; i >= 0; i--) {
            Tile snakepart = snakebody.get(i);
            // i = 0 means 1st part after head
            if (i == 0) {
                // move head;
                snakepart.x = snakehead.x;
                snakepart.y = snakehead.y;
            } else {
                Tile prev_snakepart = snakebody.get(i - 1);
                snakepart.x = prev_snakepart.x;
                snakepart.y = prev_snakepart.y;
            }
        }

        // snakehead;
        snakehead.x += velocityX;
        snakehead.y += velocityY;

        // gameover condition
        // 1 . snake collision with it's own body
        for (int i = 0; i < snakebody.size(); i++) {
            Tile snakePart = snakebody.get(i);
            if (collision(snakehead, snakePart)) {
                gameOver = true;
                scoreButton.setText("GAME OVER : " + snakebody.size());
                break;
            }
        }
        if (snakehead.x < 0 || snakehead.x > board_width / tileSize || snakehead.y < 0
                || snakehead.y > board_height / tileSize) {
            scoreButton.setText("GAME OVER : " + snakebody.size());
            gameOver = true;
        }

        // check collison with obstacle

        // vertical check - snakehead.y >= obstacles.y * tileSize && snakehead.y <
        // (obstacles.y+3)*tileSize

        if (snakehead.y >= obstacles.y && snakehead.y < (obstacles.y + 3)) {
            if (snakehead.x == obstacles.x || (snakehead.x + 1) == obstacles.x) {
                scoreButton.setText("GAME OVER : " + snakebody.size());
                gameOver = true;
            }
        }

        if (gameOver) {
            gameLoop.stop();
            restartButton.setVisible(true);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        move();
        repaint();
        // repaint will call draw again and again

        if (gameOver == true) {
            gameLoop.stop();
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        // now we need to make sure that it doesn't
        // go in opposite direction wrt to current direction
        // that is avoid running into its own body
        if (e.getKeyCode() == KeyEvent.VK_UP && velocityY != 1) {
            velocityX = 0;
            velocityY = -1;
        } else if (e.getKeyCode() == KeyEvent.VK_DOWN && velocityY != -1) {
            velocityX = 0;
            velocityY = 1;
        } else if (e.getKeyCode() == KeyEvent.VK_LEFT && velocityX != 1) {
            velocityX = -1;
            velocityY = 0;
        } else if (e.getKeyCode() == KeyEvent.VK_RIGHT && velocityX != -1) {
            velocityX = 1;
            velocityY = 0;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    public void restartGame() {
        snakehead = new Tile(5, 5);
        snakebody.clear();
        placeFood();
        placeobstacle();
        velocityX = 0;
        velocityY = 0;
        gameOver = false;
        gameLoop.start();
        restartButton.setVisible(false);
        scoreButton.setText("SCORE : " + 0);
        repaint();
    }
}
