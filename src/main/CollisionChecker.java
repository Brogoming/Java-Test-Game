package main;

import entity.Entity;

/**
 * Handles collision detection between entities and tiles, world objects, other entities, and the player.
 */
public class CollisionChecker {

	GamePanel gamePanel; // Reference to the main game panel for accessing world, tile, and object data

	/**
	 * Constructs a CollisionChecker bound to the given game panel.
	 *
	 * @param gamePanel the {@link GamePanel} used to access tiles, objects, and entities
	 */
	public CollisionChecker( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
	}

	/**
	 * Checks whether the entity's next movement step will collide with a solid tile,
	 * setting {@code entity.collisionOn} to true if so.
	 *
	 * @param entity the {@link Entity} to check tile collision for
	 */
	public void checkTileCollision( Entity entity ) {
		// Calculate the world pixel edges of the entity's solid area
		int entityLeftWorldX = entity.worldX + entity.solidArea.x;
		int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
		int entityTopWorldY = entity.worldY + entity.solidArea.y;
		int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

		// Convert world pixel edges to tile grid indices
		int entityLeftCol = entityLeftWorldX / gamePanel.tileSize;
		int entityRightCol = entityRightWorldX / gamePanel.tileSize;
		int entityTopRow = entityTopWorldY / gamePanel.tileSize;
		int entityBottomRow = entityBottomWorldY / gamePanel.tileSize;

		// Only 2 tiles need to be checked per direction (the two corners facing movement)
		int tileNum1 = 0, tileNum2 = 0;

		// Project the entity's position one step ahead in its direction, then sample the two corner tiles
		switch ( entity.direction ) {
			case "up":
				entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
				tileNum1 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];  // Top-left corner tile
				tileNum2 = gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow]; // Top-right corner tile
				break;
			case "down":
				entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
				tileNum1 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow];  // Bottom-left corner tile
				tileNum2 = gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow]; // Bottom-right corner tile
				break;
			case "left":
				entityLeftCol = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
				tileNum1 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow];    // Top-left corner tile
				tileNum2 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow]; // Bottom-left corner tile
				break;
			case "right":
				entityRightCol = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
				tileNum1 = gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow];    // Top-right corner tile
				tileNum2 = gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow]; // Bottom-right corner tile
				break;
		}

		// If either corner tile is solid, flag the entity as colliding
		if ( gamePanel.tileManager.tile[tileNum1].collision || gamePanel.tileManager.tile[tileNum2].collision )
			entity.collisionOn = true;
	}

	/**
	 * Checks whether the entity's next movement step will intersect any world object's solid area,
	 * returning the index of the hit object if the entity is the player, or 999 if no collision occurred.
	 *
	 * @param entity the {@link Entity} to check object collision for
	 * @param player true if the entity is the player, enabling the hit object's index to be returned
	 * @return the index of the collided object in {@code gamePanel.objs}, or 999 if none
	 */
	public int checkObjectCollision( Entity entity, boolean player ) {
		int index = 999; // Sentinel value indicating no object was hit

		for ( int i = 0; i < gamePanel.objs.length; i++ ) {
			if ( gamePanel.objs[i] != null ) {

				// Translate solid areas from local offsets to absolute world pixel positions
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				gamePanel.objs[i].solidArea.x = gamePanel.objs[i].worldX + gamePanel.objs[i].solidArea.x;
				gamePanel.objs[i].solidArea.y = gamePanel.objs[i].worldY + gamePanel.objs[i].solidArea.y;

				// Simulate the entity moving one step in its current direction to predict next position
				// TODO: Consider using temporary variables here instead of mutating solidArea directly
				switch ( entity.direction ) {
					case "up":
						entity.solidArea.y -= entity.speed;
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						break;
				}

				if ( entity.solidArea.intersects(gamePanel.objs[i].solidArea) ) {
					if ( gamePanel.objs[i].collision ) entity.collisionOn = true; // Block movement if object is solid
					if ( player ) index = i; // Record which object the player touched for interaction handling
				}

				// Reset both solid areas back to their default local offsets after the check
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				gamePanel.objs[i].solidArea.x = gamePanel.objs[i].solidAreaDefaultX;
				gamePanel.objs[i].solidArea.y = gamePanel.objs[i].solidAreaDefaultY;
			}
		}

		return index;
	}

	/**
	 * Checks whether the entity's next movement step will intersect any entity in the targets array,
	 * returning the index of the hit target or 999 if no collision occurred.
	 *
	 * @param entity  the {@link Entity} performing the movement
	 * @param targets the array of {@link Entity} targets to check against (e.g. NPCs or monsters)
	 * @return the index of the collided target in {@code targets}, or 999 if none
	 */
	public int checkEntityCollision( Entity entity, Entity[] targets ) {
		int index = 999; // Sentinel value indicating no target was hit

		for ( int i = 0; i < targets.length; i++ ) {
			if ( targets[i] != null ) {

				// Translate solid areas from local offsets to absolute world pixel positions
				entity.solidArea.x = entity.worldX + entity.solidArea.x;
				entity.solidArea.y = entity.worldY + entity.solidArea.y;
				targets[i].solidArea.x = targets[i].worldX + targets[i].solidArea.x;
				targets[i].solidArea.y = targets[i].worldY + targets[i].solidArea.y;

				// Simulate the entity moving one step in its current direction to predict next position
				// TODO: Consider using temporary variables here instead of mutating solidArea directly
				switch ( entity.direction ) {
					case "up":
						entity.solidArea.y -= entity.speed;
						break;
					case "down":
						entity.solidArea.y += entity.speed;
						break;
					case "left":
						entity.solidArea.x -= entity.speed;
						break;
					case "right":
						entity.solidArea.x += entity.speed;
						break;
				}

				if ( entity.solidArea.intersects(targets[i].solidArea) ) {
					entity.collisionOn = true;
					index = i; // Record which target was hit for interaction handling
				}

				// Reset both solid areas back to their default local offsets after the check
				entity.solidArea.x = entity.solidAreaDefaultX;
				entity.solidArea.y = entity.solidAreaDefaultY;
				targets[i].solidArea.x = targets[i].solidAreaDefaultX;
				targets[i].solidArea.y = targets[i].solidAreaDefaultY;
			}
		}

		return index;
	}

	/**
	 * Checks whether the entity's next movement step will intersect the player's solid area,
	 * setting {@code entity.collisionOn} to true if so.
	 *
	 * @param entity the {@link Entity} to check against the player
	 */
	public void checkPlayerCollision( Entity entity ) {
		// Translate solid areas from local offsets to absolute world pixel positions
		entity.solidArea.x = entity.worldX + entity.solidArea.x;
		entity.solidArea.y = entity.worldY + entity.solidArea.y;
		gamePanel.player.solidArea.x = gamePanel.player.worldX + gamePanel.player.solidArea.x;
		gamePanel.player.solidArea.y = gamePanel.player.worldY + gamePanel.player.solidArea.y;

		// Simulate the entity moving one step in its current direction to predict next position
		// TODO: Consider using temporary variables here instead of mutating solidArea directly
		switch ( entity.direction ) {
			case "up":
				entity.solidArea.y -= entity.speed;
				break;
			case "down":
				entity.solidArea.y += entity.speed;
				break;
			case "left":
				entity.solidArea.x -= entity.speed;
				break;
			case "right":
				entity.solidArea.x += entity.speed;
				break;
		}

		if ( entity.solidArea.intersects(gamePanel.player.solidArea) ) entity.collisionOn = true;

		// Reset both solid areas back to their default local offsets after the check
		entity.solidArea.x = entity.solidAreaDefaultX;
		entity.solidArea.y = entity.solidAreaDefaultY;
		gamePanel.player.solidArea.x = gamePanel.player.solidAreaDefaultX;
		gamePanel.player.solidArea.y = gamePanel.player.solidAreaDefaultY;
	}
}