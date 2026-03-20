package main;

import object.OBJ_Key;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.text.DecimalFormat;

/**
 * Handles all on-screen UI elements including the key counter, play timer, messages, and the game finished screen.
 */
public class UI {

	GamePanel gamePanel; // Reference to the main game panel for screen dimensions and player data
	Font arial40; // Standard font used for HUD elements and general UI text
	Font arial80B; // Large bold font used for the game finished congratulations text
	BufferedImage keyImage; // Sprite image of the key, displayed in the HUD key counter
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

		// TODO: Load the key image directly from its resource path instead of instantiating a full OBJ_Key
		OBJ_Key key = new OBJ_Key();
		keyImage = key.image;
	}

	/**
	 * Draws all active UI elements each frame, switching between the HUD and the game finished screen
	 * depending on the state of {@code gameFinished}.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics2D g2 ) {
		g2.setFont(arial40);
		g2.setColor(Color.white);

		if ( gameFinished ) {
			// --- Game Finished Screen ---

			// "You found the treasure!" message, centered horizontally above middle
			String text = "You found the treasure!";
			int textWidth = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
			int x = gamePanel.screenWidth / 2 - textWidth / 2;
			int y = gamePanel.screenHeight / 2 - (gamePanel.tileSize * 3);
			g2.drawString(text, x, y);

			// Play time display, centered horizontally below middle
			text = "Your time is: " + dFormat.format(playTime) + "!";
			textWidth = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
			x = gamePanel.screenWidth / 2 - textWidth / 2;
			y = gamePanel.screenHeight / 2 + (gamePanel.tileSize * 4);
			g2.drawString(text, x, y);

			// "Congratulations!" in large bold yellow text, centered between the two messages above
			g2.setFont(arial80B);
			g2.setColor(Color.YELLOW);
			text = "Congratulations!";
			textWidth = (int) g2.getFontMetrics().getStringBounds(text, g2).getWidth();
			x = gamePanel.screenWidth / 2 - textWidth / 2;
			y = gamePanel.screenHeight / 2 + (gamePanel.tileSize * 2);
			g2.drawString(text, x, y);

			// Stop the game loop by nulling the thread once the end screen is shown
			gamePanel.gameThread = null;
		} else {
			// --- HUD ---

			// Draw the key icon and the player's current key count in the top-left corner
			g2.drawImage(keyImage, gamePanel.tileSize / 2, gamePanel.tileSize / 2, gamePanel.tileSize, gamePanel.tileSize, null);
			g2.drawString("x " + gamePanel.player.hasKey, 74, 65); // Y position is the baseline of the text

			// Increment play time by 1/60 each frame since draw() is called 60 times per second
			playTime += (double) 1 / 60;
			g2.drawString("Time: " + dFormat.format(playTime), gamePanel.tileSize * 11, 65);

			// Display a temporary message at the bottom of the screen for 2 seconds (120 frames)
			if ( messageOn ) {
				g2.setFont(g2.getFont().deriveFont(30F));
				g2.drawString(message, gamePanel.tileSize / 2, gamePanel.screenHeight - 40);
				messageCounter++;

				if ( messageCounter > 120 ) { // 120 frames = 2 seconds at 60 FPS
					messageCounter = 0;
					messageOn = false;
				}
			}
		}
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