package entity.enemies;

import entity.Entity;
import entity.EntityType;
import main.GamePanel;

import java.util.Random;

/**
 * A basic enemy that wanders randomly, changing direction every 2 seconds,
 * and retreats in the player's direction when struck.
 */
public class MON_GreenSlime extends Entity {

	GamePanel gamePanel; // Re-declared locally for direct access; Entity stores the same reference via super

	/**
	 * Constructs a GreenSlime, sets its base stats, loads its sprites, and configures
	 * its collision hitbox to sit low on the tile to match the slime's visual footprint.
	 *
	 * @param gamePanel the {@link GamePanel} used to access collision, UI, and world systems
	 */
	public MON_GreenSlime( GamePanel gamePanel ) {
		super(gamePanel);
		this.gamePanel = gamePanel;

		type = EntityType.Enemy;
		name = "Green Slime";
		speed = 1;
		maxLife = 4;
		currentLife = maxLife;

		getMonImage();

		// Hitbox sits near the bottom of the sprite to match the slime's grounded body
		solidArea.x = gamePanel.tileSize / 16; // ~3px inset from the left edge
		solidArea.y = 18;                       // 18px down from the top — clears the slime's upper body
		solidArea.width = gamePanel.tileSize * 15 / 16; // ~42px wide, near full tile width
		solidArea.height = 30;                       // 30px tall, covers only the lower body mass
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
	}

	/**
	 * Loads the slime's sprite frames. All four directional slots intentionally share the
	 * same two down-facing sprites since the slime has no directional animation variants.
	 */
	public void getMonImage() {
		up1 = setup("/enemies/greenslime_down_1.png", gamePanel.tileSize, gamePanel.tileSize);
		up2 = setup("/enemies/greenslime_down_2.png", gamePanel.tileSize, gamePanel.tileSize);
		down1 = setup("/enemies/greenslime_down_1.png", gamePanel.tileSize, gamePanel.tileSize);
		down2 = setup("/enemies/greenslime_down_2.png", gamePanel.tileSize, gamePanel.tileSize);
		left1 = setup("/enemies/greenslime_down_1.png", gamePanel.tileSize, gamePanel.tileSize);
		left2 = setup("/enemies/greenslime_down_2.png", gamePanel.tileSize, gamePanel.tileSize);
		right1 = setup("/enemies/greenslime_down_1.png", gamePanel.tileSize, gamePanel.tileSize);
		right2 = setup("/enemies/greenslime_down_2.png", gamePanel.tileSize, gamePanel.tileSize);
	}

	/**
	 * Picks a new random movement direction every 2 seconds (120 frames at 60 FPS).
	 */
	@Override
	public void setAction() {
		actionCounter++;

		if ( actionCounter == 120 ) {
			Random rand = new Random();
			int randDirection = rand.nextInt(4); // 0–3 mapped to up/down/left/right

			if ( randDirection == 0 ) direction = "up";
			if ( randDirection == 1 ) direction = "down";
			if ( randDirection == 2 ) direction = "left";
			if ( randDirection == 3 ) direction = "right";

			actionCounter = 0;
		}
	}

	/**
	 * Reacts to being hit by nudging the slime in the player's current movement direction,
	 * which pushes it away since the player is moving toward it on contact.
	 */
	@Override
	public void damageReaction() {
		actionCounter++;
		direction = gamePanel.player.direction; // Player moves toward the slime, so matching direction sends it away
	}
}