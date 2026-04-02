package main;

import enemies.MON_GreenSlime;
import entity.NPC_OldMan;

/**
 * Responsible for placing all interactable objects into the game world at their designated positions.
 */
public class AssetSetter {

	GamePanel gamePanel; // Reference to the main game panel for accessing the object array and tile size

	/**
	 * Constructs an AssetSetter bound to the given game panel.
	 *
	 * @param gamePanel the {@link GamePanel} used to access the world object array and tile dimensions
	 */
	public AssetSetter( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
	}

	/**
	 * Instantiates and places all world objects into {@code gamePanel.objs} at their tile-based positions.
	 * <p>
	 * TODO: Find a better way to load objects rather than manually setting each one, perhaps via an object map file.
	 */
	public void setObjects() {
//		gamePanel.objs[0] = new OBJ_Door(gamePanel);
//		gamePanel.objs[0].worldX = gamePanel.tileSize * 21;
//		gamePanel.objs[0].worldY = gamePanel.tileSize * 22;
	}

	/**
	 * Instantiates and places all npcs into {@code gamePanel.npcs} at their tile-based positions.
	 * <p>
	 * TODO: Find a better way to load npcs rather than manually setting each one, perhaps via an npcs map file.
	 */
	public void setNpcs() {
		gamePanel.npcs[0] = new NPC_OldMan(gamePanel);
		gamePanel.npcs[0].worldX = gamePanel.tileSize * 9;
		gamePanel.npcs[0].worldY = gamePanel.tileSize * 10;
	}

	public void setEnemies() {
		gamePanel.enemies[0] = new MON_GreenSlime(gamePanel);
		gamePanel.enemies[0].worldX = gamePanel.tileSize * 11;
		gamePanel.enemies[0].worldY = gamePanel.tileSize * 10;

		gamePanel.enemies[1] = new MON_GreenSlime(gamePanel);
		gamePanel.enemies[1].worldX = gamePanel.tileSize * 11;
		gamePanel.enemies[1].worldY = gamePanel.tileSize * 11;
	}
}