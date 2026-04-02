package enemies;

import entity.Entity;
import main.GamePanel;

import java.util.Random;

public class MON_GreenSlime extends Entity {
	/**
	 * Constructs an Entity bound to the game panel and initializes its hitbox to one full tile.
	 *
	 * @param gamePanel the {@link GamePanel} used to access collision, UI, and world systems
	 */
	public MON_GreenSlime( GamePanel gamePanel ) {
		super(gamePanel);

		name = "Green Slime";
		speed = 1;
		maxLife = 4;
		currentLife = maxLife;

		getMonImage();

		// Set hit box
		solidArea.x = gamePanel.tileSize / 16;      // 3px from the left edge of the sprite
		solidArea.y = 18;      // 18px from the top edge of the sprite
		solidArea.width = gamePanel.tileSize * 15 / 16;   // 42px wide
		solidArea.height = 30;  // 30px tall
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
	}

	public void getMonImage() {
		up1 = setup("/enemies/greenslime_down_1.png");
		up2 = setup("/enemies/greenslime_down_2.png");
		down1 = setup("/enemies/greenslime_down_1.png");
		down2 = setup("/enemies/greenslime_down_2.png");
		left1 = setup("/enemies/greenslime_down_1.png");
		left2 = setup("/enemies/greenslime_down_2.png");
		right1 = setup("/enemies/greenslime_down_1.png");
		right2 = setup("/enemies/greenslime_down_2.png");
	}

	@Override
	public void setAction() {
		actionCounter++;

		if ( actionCounter == 120 ) { // 120 frames = 2 seconds at 60 FPS
			Random rand = new Random();
			int randDirection = rand.nextInt(4); // Random number 0–3 mapped to a direction

			if ( randDirection == 0 ) direction = "up";
			if ( randDirection == 1 ) direction = "down";
			if ( randDirection == 2 ) direction = "left";
			if ( randDirection == 3 ) direction = "right";

			actionCounter = 0; // Reset counter after picking a new direction
		}
	}
}
