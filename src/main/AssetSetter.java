package main;

import object.OBJ_Boots;
import object.OBJ_Chest;
import object.OBJ_Door;
import object.OBJ_Key;

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
	public void setObject() {
		// TODO temp for now
	}
}