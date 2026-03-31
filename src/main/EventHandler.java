package main;

import java.awt.*;

/**
 * Manages tile-based world events by detecting player collisions with event zones
 * and triggering the appropriate game effects (damage, healing, teleportation, etc.).
 */
public class EventHandler {

	// -------------------------------------------------------------------------
	// Dependencies
	// -------------------------------------------------------------------------
	GamePanel gamePanel; // Reference to the main game panel for accessing global state

	// -------------------------------------------------------------------------
	// Event Grid
	// -------------------------------------------------------------------------
	EventRect[][] eventRects; // 2D grid of event trigger zones, one per world tile
	int pixelSize = 2;        // Width and height (in pixels) of each event trigger hitbox

	// -------------------------------------------------------------------------
	// Event Tracking
	// -------------------------------------------------------------------------
	int previousEventX, previousEventY; // World position of the last triggered event
	boolean canTouchEvent = true;        // Guards against re-triggering an event while the player is still nearby

	/**
	 * Initializes the EventHandler and builds the event rect grid across the entire world map.
	 * Each tile gets a small centered trigger zone sized to {@code pixelSize}.
	 *
	 * @param gamePanel the main GamePanel providing world dimensions and tile size
	 */
	public EventHandler( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;

		eventRects = new EventRect[gamePanel.maxWorldCol][gamePanel.maxWorldRow];

		int col = 0;
		int row = 0;
		while ( col < gamePanel.maxWorldCol && row < gamePanel.maxWorldRow ) {

			eventRects[col][row] = new EventRect();

			// Center the tiny trigger hitbox within the tile
			eventRects[col][row].x = (gamePanel.tileSize - pixelSize) / 2;
			eventRects[col][row].y = (gamePanel.tileSize - pixelSize) / 2;
			eventRects[col][row].width = pixelSize;
			eventRects[col][row].height = pixelSize;

			// Store defaults so hit() can restore coordinates after intersection testing
			eventRects[col][row].eventRectDefaultX = eventRects[col][row].x;
			eventRects[col][row].eventRectDefaultY = eventRects[col][row].y;

			col++;
			if ( col == gamePanel.maxWorldCol ) { // Wrap to the next row when the column limit is reached
				col = 0;
				row++;
			}
		}
	}

	/**
	 * Evaluates all active world events each game tick, triggering effects when the
	 * player enters a defined event zone while facing the required direction.
	 * <p>
	 * TODO: Replace hardcoded event coordinates with an event map loaded from an external
	 *       file so events can be defined and edited without modifying this method.
	 */
	public void checkEvents() {
		// Allow re-triggering only after the player has moved at least one full tile away
		int xDistance = Math.abs(gamePanel.player.worldX - previousEventX);
		int yDistance = Math.abs(gamePanel.player.worldY - previousEventY);
		int distance = Math.max(xDistance, yDistance); // Chebyshev distance — diagonal counts as 1 tile

		if ( distance > gamePanel.tileSize ) canTouchEvent = true;

		if ( canTouchEvent ) {
			if ( hit(27, 16, "right") ) damagePit(27, 16, gamePanel.dialogueState);
			// if ( hit(27, 16, "right") ) teleport(gamePanel.dialogueState, 37, 10); // Preserved: alternate teleport trigger at same tile

			if ( hit(23, 12, "up") ) healingPool(23, 12, gamePanel.dialogueState);
		}
	}

	/**
	 * Tests whether the player's solid area intersects the event zone at the given tile,
	 * temporarily converting both rectangles to world-space coordinates for the check.
	 * <p>
	 * TODO: Extend this method to test all entities and objects, not just the player.
	 *
	 * @param col          the column index of the event tile to test
	 * @param row          the row index of the event tile to test
	 * @param reqDirection the player direction required to trigger the event, or "any" for any direction
	 * @return {@code true} if the player is touching the event zone and facing the required direction
	 */
	public boolean hit( int col, int row, String reqDirection ) {
		boolean hit = false;

		// Temporarily shift both rects into world space for accurate intersection testing
		gamePanel.player.solidArea.x = gamePanel.player.worldX + gamePanel.player.solidArea.x;
		gamePanel.player.solidArea.y = gamePanel.player.worldY + gamePanel.player.solidArea.y;
		eventRects[col][row].x = col * gamePanel.tileSize + eventRects[col][row].x;
		eventRects[col][row].y = row * gamePanel.tileSize + eventRects[col][row].y;

		if ( gamePanel.player.solidArea.intersects(eventRects[col][row]) && !eventRects[col][row].eventDone ) {
			if ( gamePanel.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any") ) {
				hit = true;
				previousEventX = gamePanel.player.worldX; // Record position to enforce the cooldown distance
				previousEventY = gamePanel.player.worldY;
			}
		}

		// Restore both rects to their local default coordinates after the test
		gamePanel.player.solidArea.x = gamePanel.player.solidAreaDefaultX;
		gamePanel.player.solidArea.y = gamePanel.player.solidAreaDefaultY;
		eventRects[col][row].x = eventRects[col][row].eventRectDefaultX;
		eventRects[col][row].y = eventRects[col][row].eventRectDefaultY;

		return hit;
	}

	/**
	 * Deals one point of damage to the player and displays a dialogue message.
	 * Marks the event as done so it cannot fire again.
	 *
	 * @param col       the column index of the damage pit tile
	 * @param row       the row index of the damage pit tile
	 * @param gameState the game state constant used to open the dialogue screen
	 */
	public void damagePit( int col, int row, int gameState ) {
		gamePanel.gameState = gameState;
		gamePanel.ui.currentDialogue = "You fell in a pit!";
		gamePanel.player.currentLife -= 1;
		eventRects[col][row].eventDone = true; // One-time event — prevents repeat damage on the same tile
		canTouchEvent = false;
	}

	/**
	 * Restores one point of life to the player when the interact key is pressed,
	 * and displays a healing dialogue message.
	 *
	 * @param col       the column index of the healing pool tile
	 * @param row       the row index of the healing pool tile
	 * @param gameState the game state constant used to open the dialogue screen
	 */
	public void healingPool( int col, int row, int gameState ) {
		if ( gamePanel.keyH.interactPressed ) {
			gamePanel.gameState = gameState;
			gamePanel.ui.currentDialogue = "You drink the water.\nYour life has been recovered!";
			gamePanel.player.currentLife += 1;
		}
	}

	/**
	 * Instantly moves the player to a new tile position and displays a dialogue message.
	 *
	 * @param gameState the game state constant used to open the dialogue screen
	 * @param newXTile  the destination column tile index
	 * @param newYTile  the destination row tile index
	 */
	public void teleport( int gameState, int newXTile, int newYTile ) {
		gamePanel.gameState = gameState;
		gamePanel.ui.currentDialogue = "Teleporting...";
		gamePanel.player.worldX = gamePanel.tileSize * newXTile; // Convert tile index to pixel world coordinate
		gamePanel.player.worldY = gamePanel.tileSize * newYTile;
	}
}