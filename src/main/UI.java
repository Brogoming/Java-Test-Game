package main;

import java.awt.*;
import java.text.DecimalFormat;

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

	/**
	 * Constructs the UI and initializes all fonts used across the different screen states.
	 *
	 * @param gamePanel the {@link GamePanel} used to access screen dimensions and game state
	 */
	public UI( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
		arial30 = new Font("Arial", Font.PLAIN, 30); // Dialogue text font
		arial40 = new Font("Arial", Font.PLAIN, 40); // Standard HUD font
		arial80B = new Font("Arial", Font.BOLD, 80);  // Bold font for pause and end screens
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

		if ( gamePanel.gameState == gamePanel.playState ) {
			// TODO: Add HUD rendering (key counter, timer, messages) here
		}

		if ( gamePanel.gameState == gamePanel.pauseState ) {
			drawPauseScreen();
		}

		if ( gamePanel.gameState == gamePanel.dialogueState ) {
			drawDialogueScreen();
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