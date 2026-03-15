package main;

import javax.swing.*;
import java.awt.*;

public class GamePanel extends JPanel implements Runnable {
    // Screen settings
    final int originalTileSize = 16; //16x16 tile
    final int scale = 3; //Screens now have higher pixel count
    final int tileSize = originalTileSize * scale; //48x48 tile
    final int maxScreenCol = 16;
    final int maxScreenRow = 12;
    final int screenWidth = tileSize * maxScreenCol; //768 pixels
    final int screenHeight = tileSize * maxScreenRow; //576 pixels

    //FPS
    int FPS = 60;

    KeyHandler keyH = new KeyHandler();
    Thread gameThread; //Something you can start and stop, useful for frames per second. This will automatically call the run method

    //Set players default pos.
    int playerX = 100;
    int playerY = 100;
    int playerSpeed = 4;

    /**
     * GamePanel
     */
    public GamePanel() {
        this.setPreferredSize(new Dimension(screenWidth, screenHeight)); //Set the size of the class
        this.setBackground(Color.black);
        this.setDoubleBuffered(true); // If true, all the drawing from this computer will be done in an offscreen painting buffer, helps with rendering
        this.addKeyListener(keyH);
        this.setFocusable(true); //The game panel can be focused to receive key input
    }

    public void startGameThread() {
        gameThread = new Thread(this); // Passing in this GamePanel class into the constructor
        gameThread.start(); // This will call the run method
    }

    @Override
    public void run() {
        //1,000,000,000 nanoseconds = 1 second

        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0;
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0;
        int drawCount = 0;

        while (gameThread != null) {
            currentTime = System.nanoTime(); //check the current time
            delta += (currentTime - lastTime) / drawInterval; //amount of time passed divided by the interval
            timer += currentTime - lastTime;
            lastTime = currentTime;

            if (delta >= 1) {
                // 1. Update information such as character pos.
                update();

                // 2. Draw the screen with teh updated information
                repaint(); // this is how we call the paintComponent method
                delta--; //reset delta
                drawCount++;
            }

            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    public void update() {
        if (keyH.upPressed) playerY -= playerSpeed;
        else if (keyH.downPressed) playerY += playerSpeed;
        else if (keyH.leftPressed) playerX -= playerSpeed;
        else if (keyH.rightPressed) playerX += playerSpeed;
    }

    public void paintComponent(Graphics g) { //Graphics is a class that provides functions to draw objects on screen
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g; // Extends the Graphics class to provide more sophisticated control over geometry, coordinate transformations, color management, and text layout

        g2.setColor(Color.white);

        g2.fillRect(playerX, playerY, tileSize, tileSize);
        g2.dispose(); //Dispose of this graphics to save memories
    }
}
