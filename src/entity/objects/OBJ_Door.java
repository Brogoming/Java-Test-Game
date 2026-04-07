package entity.objects;

import entity.Entity;
import main.GamePanel;

/**
 * Represents a door object in the game world that blocks entity movement until opened.
 */
public class OBJ_Door extends Entity {

	/**
	 * Constructs a Door object, loading its sprite and enabling collision.
	 */
	public OBJ_Door( GamePanel gamePanel ) {
		super(gamePanel);
		name = "Door";
		down1 = setup("/objects/door.png", gamePanel.tileSize, gamePanel.tileSize);
		collision = true;

		solidArea.x = 0;
		solidArea.y = gamePanel.tileSize / 3;
		solidArea.width = gamePanel.tileSize;
		solidArea.height = gamePanel.tileSize * 2 / 3;
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
	}
}