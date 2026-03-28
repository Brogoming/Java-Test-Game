package main;

import entity.Entity;
import entity.Player;
import object.SuperObject;
import tile.TileManager;

import javax.swing.*;
import java.awt.*;

/**
 * The main game panel serving as the primary rendering surface, game loop controller,
 * and central hub for all core game systems.
 */
public class GamePanel extends JPanel implements Runnable {

	// -------------------------------------------------------------------------
	// Screen Settings
	// -------------------------------------------------------------------------
	final int originalTileSize = 16;                         // Base tile size in pixels before scaling (16x16)
	final int scale = 3;                                     // Scale multiplier applied to the original tile size
	public final int tileSize = originalTileSize * scale;    // Effective tile size after scaling (48x48 pixels)
	public final int maxScreenCol = 16;                      // Number of tile columns visible on screen at once
	public final int maxScreenRow = 12;                      // Number of tile rows visible on screen at once
	public final int screenWidth = tileSize * maxScreenCol;  // Total screen width in pixels (768)
	public final int screenHeight = tileSize * maxScreenRow; // Total screen height in pixels (576)

	// -------------------------------------------------------------------------
	// World Settings
	// -------------------------------------------------------------------------
	public final int maxWorldCol = 50; // Total number of tile columns in the game world
	public final int maxWorldRow = 50; // Total number of tile rows in the game world

	// -------------------------------------------------------------------------
	// Game Loop Settings
	// -------------------------------------------------------------------------
	int FPS = 60; // Target number of frames to render and update per second

	// -------------------------------------------------------------------------
	// Core System Components
	// -------------------------------------------------------------------------
	TileManager tileManager = new TileManager(this);  // Loads and renders all world tiles
	KeyHandler keyH = new KeyHandler(this);   // Handles keyboard input and tracks key states
	Sound soundEffect = new Sound();            // Used for short one-shot sound effects
	Sound music = new Sound();            // Used for looping background music
	public CollisionChecker cChecker = new CollisionChecker(this); // Handles tile and object collision detection
	public AssetSetter assetSetter = new AssetSetter(this);      // Places objects and NPCs into the world
	public UI ui = new UI(this);               // Manages all on-screen UI rendering
	Thread gameThread;                                          // The thread that runs the game loop via run()

	// -------------------------------------------------------------------------
	// Entity and Object
	// -------------------------------------------------------------------------
	public Player player = new Player(this, keyH); // The player entity controlled by keyboard input
	public SuperObject[] objs = new SuperObject[10];    // Holds up to 10 interactable world objects
	public Entity[] npcs = new Entity[10];         // Holds up to 10 active NPCs in the world

	// -------------------------------------------------------------------------
	// Game State
	// -------------------------------------------------------------------------
	// TODO: Consider replacing int state constants with an enum for clarity and type safety
	public int gameState;
	public final int titleState = 0;
	public final int playState = 1; // Game is actively running
	public final int pauseState = 2; // Game is paused
	public final int dialogueState = 3; // Player is in a dialogue interaction

	// -------------------------------------------------------------------------
	// Constructor
	// -------------------------------------------------------------------------

	/**
	 * Constructs and configures the GamePanel, setting up screen size, background,
	 * double buffering, and keyboard input handling.
	 */
	public GamePanel() {
		this.setPreferredSize(new Dimension(screenWidth, screenHeight));
		this.setBackground(Color.black);
		this.setDoubleBuffered(true); // Draw to offscreen buffer first to reduce flicker
		this.addKeyListener(keyH);    // Register key handler to capture keyboard events
		this.setFocusable(true);      // Allow this panel to receive keyboard focus
	}

	// -------------------------------------------------------------------------
	// Setup
	// -------------------------------------------------------------------------

	/**
	 * Initializes the game world by placing objects and NPCs, then sets the game state to playing.
	 */
	public void setupGame() {
		assetSetter.setObjects();
		assetSetter.setNpcs();
		gameState = titleState;
	}

	// -------------------------------------------------------------------------
	// Game Loop
	// -------------------------------------------------------------------------

	/**
	 * Creates and starts the game thread, triggering the run() game loop.
	 * Should be called once after the window is made visible.
	 */
	public void startGameThread() {
		gameThread = new Thread(this); // Pass this GamePanel as Runnable so the thread calls run()
		gameThread.start();
	}

	/**
	 * The core game loop using a delta-time accumulator to maintain a consistent
	 * frame rate regardless of system performance.
	 */
	@Override
	public void run() {
		double drawInterval = (double) 1000000000 / FPS; // Nanoseconds per frame (1s = 1,000,000,000ns)
		double delta = 0; // Accumulates elapsed frame fractions; a frame is processed when delta >= 1
		long lastTime = System.nanoTime();
		long currentTime;
		long timer = 0; // Accumulates nanoseconds to detect when one full second has passed
		int drawCount = 0; // Counts frames drawn in the current second for FPS logging

		while ( gameThread != null ) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval; // Fraction of a frame interval elapsed
			timer += (currentTime - lastTime);
			lastTime = currentTime;

			if ( delta >= 1 ) {
				update();  // Step 1: Update game state (positions, input, logic)
				repaint(); // Step 2: Schedule a paintComponent() call with the updated state
				delta--;
				drawCount++;
			}

			// Log actual FPS to console once per second
			if ( timer >= 1000000000 ) {
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
	 * Updates all active game entities each frame, gated by the current game state.
	 */
	public void update() {
		if ( gameState == playState ) {
			player.update();

			for ( Entity npc : npcs ) {
				if ( npc != null ) npc.update();
			}
		}

		if ( gameState == pauseState ) {
			// TODO: Add pause state logic (e.g. pause menu rendering)
		}
	}

	/**
	 * Renders all game layers in order each frame: tiles, objects, NPCs, player, then UI.
	 *
	 * @param g the {@link Graphics} context provided by the Swing painting system
	 */
	@Override
	public void paintComponent( Graphics g ) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g; // Cast to Graphics2D for enhanced rendering control

		// Debug: record draw start time if debug mode is active
		long drawStart = 0;
		if ( keyH.debugMode ) drawStart = System.nanoTime();

		//TITLE SCREEN
		if ( gameState == titleState ) {
			ui.draw(g2);
		} else {
			// Draw world layers in order (tiles first so player renders above them)
			tileManager.draw(g2);

			for ( SuperObject obj : objs ) {
				if ( obj != null ) obj.draw(g2, this);
			}

			for ( Entity npc : npcs ) {
				if ( npc != null ) npc.draw(g2);
			}

			player.draw(g2);
			ui.draw(g2); // Draw UI last so it always renders on top of all world elements
		}


		// Debug: print draw time to screen and console if debug mode is active
		if ( keyH.debugMode ) {
			long drawTime = System.nanoTime() - drawStart;
			g2.setColor(Color.white);
			g2.drawString("Draw time: " + drawTime, 10, 400);
			System.out.println("Draw time: " + drawTime);
		}

		g2.dispose(); // Release Graphics2D resources to free memory
	}

	// -------------------------------------------------------------------------
	// Audio
	// -------------------------------------------------------------------------

	/**
	 * Loads, plays, and loops the background music track.
	 */
	public void playMusic() {
		music.setFile(0);
		music.play();
		music.loop();
	}

	/**
	 * Stops the currently playing background music.
	 */
	public void stopMusic() {
		music.stop();
	}

	/**
	 * Loads and plays a one-shot sound effect at the given index.
	 *
	 * @param index the index of the sound effect in the Sound URL array
	 */
	public void playSoundEffect( int index ) {
		soundEffect.setFile(index);
		soundEffect.play();
	}
}