package entity.objects;

import entity.Entity;
import main.GamePanel;

public class OBJ_Heart extends Entity {
	/**
	 * Constructs a heart object, loading its sprite and enabling collision.
	 */
	public OBJ_Heart( GamePanel gamePanel ) {
		super(gamePanel);
		name = "Heart";
		image = setup("/objects/heart_full.png", gamePanel.tileSize, gamePanel.tileSize);
		image1 = setup("/objects/heart_half.png", gamePanel.tileSize, gamePanel.tileSize);
		image2 = setup("/objects/heart_blank.png", gamePanel.tileSize, gamePanel.tileSize);
	}
}
