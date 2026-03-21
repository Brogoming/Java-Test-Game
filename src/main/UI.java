package main;

import java.awt.*;
import java.text.DecimalFormat;

/**
 * Handles all on-screen UI elements including the key counter, play timer, messages, and the game finished screen.
 */
public class UI {

	GamePanel gamePanel; // Reference to the main game panel for screen dimensions and player data
	Graphics2D g2;
	Font arial40; // Standard font used for HUD elements and general UI text
	Font arial80B; // Large bold font used for the game finished congratulations text
	public boolean messageOn = false; // Whether a temporary message is currently being displayed
	public String message = ""; // The current message text to display on screen
	int messageCounter = 0; // Tracks how many frames the current message has been displayed
	public boolean gameFinished = false; // Whether the player has completed the game
	double playTime; // Accumulated play time in seconds, incremented each frame
	DecimalFormat dFormat = new DecimalFormat("#0.00"); // Formats play time to always show 2 decimal places

	/**
	 * Constructs the UI, initializes fonts, and loads the key sprite image from an OBJ_Key instance.
	 *
	 * @param gamePanel the {@link GamePanel} used to access screen dimensions and player state
	 */
	public UI( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
		arial40 = new Font("Arial", Font.PLAIN, 40); // Standard UI font
		arial80B = new Font("Arial", Font.BOLD, 80);  // Bold font for end screen
	}

	/**
	 * Draws all active UI elements each frame, switching between the HUD and the game finished screen
	 * depending on the state of {@code gameFinished}.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics2D g2 ) {
		this.g2 = g2;
		g2.setFont(arial80B);
		g2.setColor(Color.white);

		if ( gamePanel.gameState == gamePanel.playState ) {
			// TODO do stuff later
		}
		if ( gamePanel.gameState == gamePanel.pauseState ) {
			drawPauseScreen();
		}
	}

	private void drawPauseScreen() {
		String pauseText = "PAUSED";
		int x = getCenterX(pauseText);
		int y = gamePanel.screenHeight / 2;

		g2.drawString(pauseText, x, y);
	}

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