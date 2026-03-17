package main;

import entity.Player;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

/**
 * The main game panel that serves as the primary rendering surface and game loop controller.
 * <p>
 * {@code GamePanel} extends {@link JPanel} to act as the game's canvas, and implements
 * {@link Runnable} to support running the game loop on a dedicated {@link Thread}.
 * It manages screen configuration, frame timing, player input, and delegates
 * rendering and logic updates to the appropriate game entities.
 * </p>
 */
public class GamePanel extends JPanel implements Runnable {
    // -------------------------------------------------------------------------
    // Screen Settings
    // -------------------------------------------------------------------------
    final int originalTileSize = 16; // The base tile size in pixels before scaling (16x16 pixels).
    final int scale = 3; // The scale multiplier applied to the original tile size. Increasing this value makes all tiles larger on higher-resolution screens.
    public final int tileSize = originalTileSize * scale; // The effective tile size after scaling (48x48 pixels).
    public final int maxScreenCol = 16; // The number of tile columns visible on screen at one time.
    public final int maxScreenRow = 12; // The number of tile rows visible on screen at one time.
    public final int screenWidth = tileSize * maxScreenCol; // The total screen width in pixels, derived from tile size and column count (768 pixels).
    public final int screenHeight = tileSize * maxScreenRow; // The total screen height in pixels, derived from tile size and row count (576 pixels).

    // -------------------------------------------------------------------------
    // Game Loop Settings
    // -------------------------------------------------------------------------

    int FPS = 60; // The target number of frames to render and update per second.

    // -------------------------------------------------------------------------
    // Core Components
    // -------------------------------------------------------------------------

    TileManager tileManager = new TileManager(this);
    KeyHandler keyH = new KeyHandler(); //Handles keyboard input and tracks which keys are currently pressed.
    Thread gameThread; // The thread that runs the game loop. When started, it automatically invokes the {@link #run()} method, which drives updates and rendering at the target {@link #FPS}.
    Player player = new Player(this, keyH); // The player entity, initialized with a reference to this panel and the key handler.

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs and configures the {@code GamePanel}.
     * <p>
     * Sets the preferred screen dimensions, background color, double buffering,
     * key input listener, and focus behavior required for gameplay.
     * </p>
     */
    public GamePanel() {
        // Set the canvas size to match the calculated screen dimensions
        this.setPreferredSize(new Dimension(screenWidth, screenHeight));
        this.setBackground(Color.black);

        // Enable double buffering: draws frames to an offscreen buffer first,
        // then flushes to screen — reduces flickering during rendering
        this.setDoubleBuffered(true);

        this.addKeyListener(keyH); // Register the key handler to listen for keyboard events on this panel
        this.setFocusable(true); // Allow this panel to receive focus so it can capture keyboard input
    }

    // -------------------------------------------------------------------------
    // Game Loop
    // -------------------------------------------------------------------------

    /**
     * Creates and starts the game thread, which triggers the {@link #run()} method.
     * <p>
     * Should be called once after the window has been made visible to begin gameplay.
     * </p>
     */
    public void startGameThread() {
        // Pass this GamePanel as the Runnable so the thread calls our run() method
        gameThread = new Thread(this);
        gameThread.start();
    }

    /**
     * The core game loop, executed on the {@link #gameThread}.
     * <p>
     * Uses a delta-time accumulator pattern to decouple game updates from
     * actual CPU speed, ensuring the game runs at a consistent {@link #FPS}
     * regardless of system performance. Also prints the actual FPS to the
     * console once per second for debugging.
     * </p>
     */
    @Override
    public void run() {
        // 1,000,000,000 nanoseconds = 1 second
        // Calculate how many nanoseconds should pass between each frame
        double drawInterval = (double) 1000000000 / FPS;
        double delta = 0; // Accumulates elapsed time; when >= 1, it's time for the next frame
        long lastTime = System.nanoTime();
        long currentTime;
        long timer = 0; // Accumulates time to track when one second has elapsed (for FPS logging)
        int drawCount = 0; // Counts how many frames were drawn in the current second

        while (gameThread != null) {
            currentTime = System.nanoTime();
            delta += (currentTime - lastTime) / drawInterval; // Add the fraction of a frame interval that has elapsed since last loop
            timer += currentTime - lastTime; // Accumulate total elapsed nanoseconds for FPS reporting
            lastTime = currentTime;

            if (delta >= 1) { // Only update and render once a full frame interval has accumulated
                update(); // Step 1: Update game state (e.g., player position, input handling)
                repaint(); // Step 2: Repaint the panel with the updated state, repaint() schedules a call to paintComponent()
                delta--; // Subtract 1 to account for the frame just processed
                drawCount++;
            }

            // Once one second has passed, log the FPS count and reset counters
            if (timer >= 1000000000) {
                System.out.println("FPS: " + drawCount);
                drawCount = 0;
                timer = 0;
            }
        }
    }

    // -------------------------------------------------------------------------
    // Update & Render
    // -------------------------------------------------------------------------

    /**
     * Updates the game state for the current frame.
     * <p>
     * Delegates to each game entity's own update logic. Currently updates
     * only the {@link Player}, but should be extended as more entities are added.
     * </p>
     */
    public void update() {
        player.update();
    }

    /**
     * Renders all game elements onto the panel for the current frame.
     * <p>
     * Overrides {@link JPanel#paintComponent(Graphics)} to perform custom drawing.
     * Upgrades the provided {@link Graphics} context to {@link Graphics2D} for
     * access to advanced rendering features, then delegates drawing to game entities.
     * </p>
     *
     * @param g the {@link Graphics} context provided by the Swing painting system
     */
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g); // Call the parent implementation to clear the panel before redrawing
        Graphics2D g2 = (Graphics2D) g; // Cast to Graphics2D for enhanced rendering control (transformations, shapes, etc.)

        //Layer the tiles to the play is above the tiles
        tileManager.draw(g2);
        player.draw(g2); // Draw the player onto the panel

        g2.dispose(); // Release the Graphics2D resources to free up memory
    }
}