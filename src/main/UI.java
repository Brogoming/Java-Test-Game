package main;

import object.OBJ_Heart;
import object.SuperObject;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Handles all on-screen UI elements including the HUD, pause screen, and dialogue windows.
 */
public class UI {

	// -------------------------------------------------------------------------
	// Core References
	// -------------------------------------------------------------------------
	GamePanel gamePanel;            // Reference to the main game panel for screen dimensions and game state
	Graphics2D g2;                  // Cached Graphics2D context updated each draw() call

	// -------------------------------------------------------------------------
	// Fonts
	// -------------------------------------------------------------------------
	Font arial30;                   // Used for dialogue text inside sub-windows
	Font arial40;                   // Used for standard HUD elements and general UI text
	Font arial80B;                  // Large bold font used for the pause screen and end screen

	// -------------------------------------------------------------------------
	// Message State
	// -------------------------------------------------------------------------
	public boolean messageOn = false;       // Whether a temporary message is currently being displayed
	public String message = "";             // The current message text to display on screen
	int messageCounter = 0;                 // Tracks how many frames the current message has been displayed
	public boolean gameFinished = false;    // Whether the player has completed the game
	public String currentDialogue = "";     // The current dialogue string to render in the dialogue window

	// -------------------------------------------------------------------------
	// Menu Options
	// -------------------------------------------------------------------------
	public int commandNumber = 0;
	public int titleScreenState = 0; // 0 = first screen, 1 = second screen

	// -------------------------------------------------------------------------
	// Player Hud
	// -------------------------------------------------------------------------
	BufferedImage heart_full, heart_half, heart_blank;

	/**
	 * Constructs the UI and initializes all fonts used across the different screen states.
	 *
	 * @param gamePanel the {@link GamePanel} used to access screen dimensions and game state
	 */
	public UI( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;

		// Set fonts
		// TODO get better fonts
		arial30 = new Font("Arial", Font.PLAIN, 30); // Dialogue text font
		arial40 = new Font("Arial", Font.PLAIN, 40); // Standard HUD font
		arial80B = new Font("Arial", Font.BOLD, 80);  // Bold font for pause and end screens

		// Create HUD Object
		SuperObject heart = new OBJ_Heart(gamePanel);
		heart_full = heart.image;
		heart_half = heart.image1;
		heart_blank = heart.image2;
	}

	/**
	 * Draws the appropriate UI layer each frame based on the current game state.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics2D g2 ) {
		this.g2 = g2;
		g2.setFont(arial80B);
		g2.setColor(Color.white);

		if ( gamePanel.gameState == gamePanel.titleState ) {
			drawTitleScreen();
		}

		if ( gamePanel.gameState == gamePanel.playState ) {
			drawPlayerLife();
		}

		if ( gamePanel.gameState == gamePanel.pauseState ) {
			drawPlayerLife();
			drawPauseScreen();
		}

		if ( gamePanel.gameState == gamePanel.dialogueState ) {
			drawPlayerLife();
			drawDialogueScreen();
		}
	}

	private void drawPlayerLife() {
		int x = gamePanel.tileSize / 2;
		int y = gamePanel.tileSize / 2;

		// Draw max life
		for ( int i = 0; i < gamePanel.player.maxLife / 2; i++ ) {
			g2.drawImage(heart_blank, x, y, null);
			x += gamePanel.tileSize + 10;
		}

		// reset values
		x = gamePanel.tileSize / 2;

		// Draw current life
		for ( int i = 0; i < gamePanel.player.currentLife; i++ ) {
			g2.drawImage(heart_half, x, y, null);
			i++;
			if ( i < gamePanel.player.currentLife ) g2.drawImage(heart_full, x, y, null);
			x += gamePanel.tileSize + 10;
		}
	}

	private void drawTitleScreen() {
		if ( titleScreenState == 0 ) {
			// Background
			g2.setColor(new Color(17, 66, 0));
			g2.fillRect(0, 0, gamePanel.screenWidth, gamePanel.screenHeight);

			// Title Name
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 96F));
			String title = "Java Test Game";
			int x = getCenterX(title);
			int y = gamePanel.tileSize * 3;

			// Shadow
			g2.setColor(Color.black);
			g2.drawString(title, x + 5, y + 5);

			// Main text
			g2.setColor(Color.white);
			g2.drawString(title, x, y);

			// Player Image
			x = (gamePanel.screenWidth / 2) - gamePanel.tileSize;
			y += gamePanel.tileSize * 2;
			g2.drawImage(gamePanel.player.down1, x, y, gamePanel.tileSize * 2, gamePanel.tileSize * 2, null);

			// Menu
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 48F));
			String menuText = "New Game";
			x = getCenterX(menuText);
			y += (int) (gamePanel.tileSize * 3.5);
			g2.drawString(menuText, x, y);
			if ( commandNumber == 0 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}

			menuText = "Load Game";
			x = getCenterX(menuText);
			y += gamePanel.tileSize;
			g2.drawString(menuText, x, y);
			if ( commandNumber == 1 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}

			menuText = "Quit";
			x = getCenterX(menuText);
			y += gamePanel.tileSize;
			g2.drawString(menuText, x, y);
			if ( commandNumber == 2 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}
		} else if ( titleScreenState == 1 ) {
			g2.setColor(Color.white);
			g2.setFont(g2.getFont().deriveFont(Font.BOLD, 42F));
			String text = "Select your class";
			int x = getCenterX(text);
			int y = gamePanel.tileSize * 3;
			g2.drawString(text, x, y);

			text = "Fighter";
			x = (gamePanel.screenWidth / 2) - (gamePanel.tileSize * 2);
			y += gamePanel.tileSize * 3;
			g2.drawString(text, x, y);
			if ( commandNumber == 0 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}

			text = "Wizard";
			y += gamePanel.tileSize;
			g2.drawString(text, x, y);
			if ( commandNumber == 1 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}

			text = "Ranger";
			y += gamePanel.tileSize;
			g2.drawString(text, x, y);
			if ( commandNumber == 2 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}

			text = "Back";
			y += gamePanel.tileSize * 2;
			g2.drawString(text, x, y);
			if ( commandNumber == 3 ) {
				g2.drawString(">", x - gamePanel.tileSize, y);
			}
		}
	}

	/**
	 * Draws the dialogue sub-window and renders the current dialogue text line by line.
	 */
	private void drawDialogueScreen() {
		// Calculate dialogue window dimensions relative to the screen
		int x = gamePanel.tileSize;
		int y = gamePanel.tileSize / 3;
		int width = gamePanel.screenWidth - gamePanel.tileSize * 2;
		int height = gamePanel.tileSize * 4;

		drawSubWindow(x, y, width, height);

		if ( currentDialogue != null ) {
			g2.setFont(arial30);
			x += gamePanel.tileSize / 2;
			y += gamePanel.tileSize;

			// Render each line separately to support multi-line dialogue using \n
			for ( String line : currentDialogue.split("\n") ) {
				g2.drawString(line, x, y);
				y += arial30.getSize() + 10; // Offset Y by font size + padding for each new line
			}
		}
	}

	/**
	 * Draws the pause screen with a centered "PAUSED" label.
	 */
	private void drawPauseScreen() {
		String pauseText = "PAUSED";
		int x = getCenterX(pauseText);
		int y = gamePanel.screenHeight / 2;

		g2.drawString(pauseText, x, y);
	}

	/**
	 * Draws a rounded rectangle sub-window with a semi-transparent black background and white border,
	 * used as the base panel for dialogue and other overlay screens.
	 *
	 * @param x      the X position of the window's top-left corner
	 * @param y      the Y position of the window's top-left corner
	 * @param width  the total width of the window
	 * @param height the total height of the window
	 */
	private void drawSubWindow( int x, int y, int width, int height ) {
		int arcWidth = 35;  // Horizontal radius of the rounded corner arc
		int arcHeight = 35;  // Vertical radius of the rounded corner arc

		Color backgroundColor = new Color(0, 0, 0, 200); // Semi-transparent black (200/255 opacity)
		Color borderColor = new Color(255, 255, 255); // Solid white border

		// Draw the filled background
		g2.setColor(backgroundColor);
		g2.fillRoundRect(x, y, width, height, arcWidth, arcHeight);

		// Draw the inset border on top of the background
		g2.setColor(borderColor);
		g2.setStroke(new BasicStroke(5)); // 5px border width
		g2.drawRoundRect(x + 5, y + 5, width - 10, height - 10, arcWidth - 10, arcHeight - 10);
	}

	/**
	 * Calculates the X coordinate needed to horizontally center a string on the screen.
	 *
	 * @param text the string to center
	 * @return the X coordinate that centers the string based on the current font metrics
	 */
	private int getCenterX( String text ) {
		int length = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
		return gamePanel.screenWidth / 2 - length / 2;
	}

	/**
	 * Triggers a temporary on-screen message to be displayed for 2 seconds.
	 *
	 * @param message the text to display on screen
	 */
	public void showMessage( String message ) {
		this.message = message;
		this.messageOn = true;
	}
}