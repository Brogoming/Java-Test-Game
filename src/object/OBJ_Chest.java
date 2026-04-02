package object;

import entity.Entity;
import main.GamePanel;

/**
 * Represents a chest object in the game world that the player can interact with.
 */
public class OBJ_Chest extends Entity {

	/**
	 * Constructs a Chest object and loads its sprite image.
	 */
	public OBJ_Chest( GamePanel gamePanel ) {
		super(gamePanel);
		name = "Chest";
		down1 = setup("/objects/chest.png");
	}
}