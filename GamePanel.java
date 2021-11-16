// The inner workings of the snake game.
// ------------------------------------------------------------------------------------------------
// Ideas for future implementation:
//      - put in a special piece of food randomly that is worth more but adds extra difficulty 
//        (e.g. obstacles, extra pieces of food, increase in speed or length etc.)
//      - if the snake hits a border, allow the snake to appear on the opposite border. 
//        (best for game scenarios where you include obstacles.)
//      - keep track of high scores outside of the program running
//      - different game modes (e.g. eating food adds a random amount of length to the snake, or
//        accelerates the snake!)
// ------------------------------------------------------------------------------------------------
// (based on a Bro Code tutorial https://www.youtube.com/watch?v=bI6e6qjJ8JQ

// import java.awt.Graphics;
import java.awt.*;
// import java.awt.event.ActionEvent;
// import java.awt.event.ActionListener;
// import java.awt.event.KeyAdapter;
// import java.awt.event.KeyEvent;
import java.awt.event.*;
// import javax.swing.JPanel;
import javax.swing.*;
import java.util.Random;



public class GamePanel extends JPanel implements ActionListener {

    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 15;
    static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;     // how many objects can fit on your screen
    static final int DELAY = 75;        // delay for the timer - how fast your game plays. The higher the number the slower the game
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int foodEaten;
    int foodX;
    int foodY;
    char direction = 'R';       // starting direction - one of "up" (U), "down" (D), "left" (L), or "right" (R)
    boolean running = false;
    Timer timer;
    Random random;

    GamePanel() {
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.black);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        startGame();
    }

    public void startGame() {
        newFood();
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
    }

    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        draw(g);
    }

    public void draw(Graphics g) {
        if (running) {
            /*
            // reference grid
            for(int i = 0; i < SCREEN_HEIGHT/UNIT_SIZE; i++) {
                g.drawLine(i*UNIT_SIZE, 0, i*UNIT_SIZE, SCREEN_HEIGHT);
                g.drawLine(0, i*UNIT_SIZE, SCREEN_WIDTH, i*UNIT_SIZE);
            }
            */
            // food
            g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
            g.fillRect(foodX + (UNIT_SIZE - UNIT_SIZE/3)/2, foodY + (UNIT_SIZE - UNIT_SIZE/3)/2, UNIT_SIZE/3, UNIT_SIZE/3);
            
            // snake
            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g.setColor(Color.green);
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                } else {
                    g.setColor(new Color(45,180,0));
                    g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
                }
            }

            // score
            g.setColor(Color.red);
            g.setFont(new Font("Consolas", Font.PLAIN, 20));
            FontMetrics metrics = getFontMetrics(g.getFont());
            g.drawString("score: " + foodEaten, (int)(SCREEN_WIDTH - 3*(metrics.stringWidth("score: " + foodEaten))/2), SCREEN_HEIGHT/20);
        } else {
            gameOver(g);
        }

    }

    public void newFood() {
        foodX = random.nextInt((int)(SCREEN_WIDTH/UNIT_SIZE))*UNIT_SIZE;
        foodY = random.nextInt((int)(SCREEN_HEIGHT/UNIT_SIZE))*UNIT_SIZE;
        for (int i = 0; i < bodyParts; i++) {
            if ((x[i] == foodX) && (y[i] == foodY)) {
                newFood();
            }
        }
    }

    public void move() {
        for(int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }

        switch(direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;

        }
    }

    public void checkFood() {
        if((x[0] == foodX) && (y[0] == foodY)) {
            bodyParts++;
            foodEaten++;
            newFood();
        }
    }

    public void checkCollisions() {
        // ends program if head collides with body
        for (int i = bodyParts; i > 0; i--) {
            if((x[0] == x[i]) && (y[0]) == y[i]) {
                running = false;
            }
        }

        // ends program if head collides with left border
        if (x[0] < 0) {
            running = false;
        }
        // ends program if head collides with right border
        if (x[0] > SCREEN_WIDTH) {
            running = false;
        }

        // ends program if head collides with upper border
        if (y[0] < 0) {
            running = false;
        }
        // ends program if head collides with lower border
        if (y[0] > SCREEN_HEIGHT) {
            running = false;
        }

        if (!running){
            timer.stop();
        }

    }

    public void gameOver(Graphics g) {
        // 'Game Over' text
        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD, 75));
        FontMetrics metricsOne = getFontMetrics(g.getFont());
        g.drawString("GAME OVER", (SCREEN_WIDTH - metricsOne.stringWidth("GAME OVER"))/2, SCREEN_HEIGHT/2);

        // final score text
        g.setColor(Color.red);
        g.setFont(new Font("Consolas", Font.BOLD, 30));
        FontMetrics metricsTwo = getFontMetrics(g.getFont());
        g.drawString("FINAL SCORE: " + foodEaten, (SCREEN_WIDTH - metricsTwo.stringWidth("FINAL SCORE: " + foodEaten))/2, 2*(SCREEN_HEIGHT/3));
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (running) {
            move();
            checkFood();
            checkCollisions();
        }
        repaint();
        
    }

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            switch(e.getKeyCode()) {
                case KeyEvent.VK_LEFT:
                    if (direction != 'R') {
                        direction = 'L';
                    }
                    break;
                case KeyEvent.VK_RIGHT:
                    if (direction != 'L') {
                        direction = 'R';
                    }
                break;
                case KeyEvent.VK_UP:
                    if (direction != 'D') {
                        direction = 'U';
                    }
                    break;
                case KeyEvent.VK_DOWN:
                    if (direction != 'U') {
                        direction = 'D';
                    }
                    break;        
            }
        }
    }
    
}
