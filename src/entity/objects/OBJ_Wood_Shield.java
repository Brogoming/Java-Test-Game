package entity.objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Wood_Shield extends Entity {
	/**
	 * Constructs an Entity bound to the game panel and initializes its hitbox to one full tile.
	 *
	 * @param gamePanel the {@link GamePanel} used to access collision, UI, and world systems
	 */
	public OBJ_Wood_Shield( GamePanel gamePanel ) {
		super(gamePanel);

		name = "Wood Shield";
		down1 = setup("/objects/shield_wood.png", gamePanel.tileSize, gamePanel.tileSize);
		defenceValue = 1;
	}
}
