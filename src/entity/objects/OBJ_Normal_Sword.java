package entity.objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Normal_Sword extends Entity {

	/**
	 * Constructs an Entity bound to the game panel and initializes its hitbox to one full tile.
	 *
	 * @param gamePanel the {@link GamePanel} used to access collision, UI, and world systems
	 */
	public OBJ_Normal_Sword( GamePanel gamePanel ) {
		super(gamePanel);

		name = "Normal Sword";
		down1 = setup("/objects/sword_normal.png", gamePanel.tileSize, gamePanel.tileSize);
		attackDamage = 1;
	}
}
