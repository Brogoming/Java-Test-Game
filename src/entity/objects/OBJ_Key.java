package entity.objects;

import entity.Entity;
import main.GamePanel;

/**
 * Represents a key object in the game world that the player can pick up to unlock doors.
 */
public class OBJ_Key extends Entity {

	/**
	 * Constructs a Key object and loads its sprite image.
	 */
	public OBJ_Key( GamePanel gamePanel ) {
		super(gamePanel);
		name = "Key";
		down1 = setup("/objects/key.png");
	}
}