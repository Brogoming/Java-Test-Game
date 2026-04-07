package entity.objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Boots extends Entity {

	/**
	 * Constructs a Boots object, loading its sprite.
	 */
	public OBJ_Boots( GamePanel gamePanel ) {
		super(gamePanel);
		name = "Boots";
		down1 = setup("/objects/boots.png", gamePanel.tileSize, gamePanel.tileSize);
	}
}
