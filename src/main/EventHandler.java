package main;

import java.awt.*;

public class EventHandler {
	GamePanel gamePanel;
	Rectangle eventRect;
	int eventRectDefaultX, eventRectDefaultY;
	int pixelSize = 2;

	public EventHandler( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;

		// Event box
		eventRect = new Rectangle();
		// Sets trigger in the middle of a tile
		eventRect.x = (gamePanel.tileSize - pixelSize) / 2;
		eventRect.y = (gamePanel.tileSize - pixelSize) / 2;
		eventRect.width = pixelSize;
		eventRect.height = pixelSize;
		eventRectDefaultX = eventRect.x;
		eventRectDefaultY = eventRect.y;
	}

	public void checkEvents() {
		// TODO make a event map to read from so we don't have to manually set this
//		if ( hit(27, 16, "right") ) damagePit(gamePanel.dialogueState); // keep for future purposes
		if ( hit(27, 16, "right") ) teleport(gamePanel.dialogueState, 37, 10);

		if ( hit(23, 12, "up") ) healingPool(gamePanel.dialogueState);
	}

	public boolean hit( int eventCol, int eventRow, String reqDirection ) {
		// TODO Maybe make it so it checks all entities and objects not just players
		boolean hit = false;

		gamePanel.player.solidArea.x = gamePanel.player.worldX + gamePanel.player.solidArea.x;
		gamePanel.player.solidArea.y = gamePanel.player.worldY + gamePanel.player.solidArea.y;
		eventRect.x = eventCol * gamePanel.tileSize + eventRect.x;
		eventRect.y = eventRow * gamePanel.tileSize + eventRect.y;

		if ( gamePanel.player.solidArea.intersects(eventRect) ) {
			if ( gamePanel.player.direction.contentEquals(reqDirection) || reqDirection.contentEquals("any") ) {
				// event happens if the player interacts with event area and either facing a certain direction or happens regardless
				hit = true;
			}
		}

		gamePanel.player.solidArea.x = gamePanel.player.solidAreaDefaultX;
		gamePanel.player.solidArea.y = gamePanel.player.solidAreaDefaultY;
		eventRect.x = eventRectDefaultX;
		eventRect.y = eventRectDefaultY;

		return hit;
	}

	// Damage player event
	public void damagePit( int gameState ) {
		gamePanel.gameState = gameState;
		gamePanel.ui.currentDialogue = "You fell in a pit!";
		gamePanel.player.currentLife -= 1;
	}

	// Heal player event
	public void healingPool( int gameState ) {
		if ( gamePanel.keyH.interactPressed ) {
			gamePanel.gameState = gameState;
			gamePanel.ui.currentDialogue = "You drink the water.\nYour life has been recovered!";
			gamePanel.player.currentLife += 1;
		}
	}

	// Teleport player
	public void teleport( int gameState, int newXTile, int newYTile ) {
		gamePanel.gameState = gameState;
		gamePanel.ui.currentDialogue = "Teleporting...";
		gamePanel.player.worldX = gamePanel.tileSize * newXTile;
		gamePanel.player.worldY = gamePanel.tileSize * newYTile;
	}
}
