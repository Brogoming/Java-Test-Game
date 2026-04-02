package entity;

import main.GamePanel;
import main.KeyHandler;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Represents the player-controlled character, handling movement input,
 * walk-cycle animation, and rendering at a fixed center screen position.
 */
public class Player extends Entity {

	// -------------------------------------------------------------------------
	// Core References
	// -------------------------------------------------------------------------
	KeyHandler keyH; // Reference to the key handler, used to read directional input each frame

	// -------------------------------------------------------------------------
	// Screen Position
	// -------------------------------------------------------------------------
	public final int screenX; // Fixed X screen coordinate where the player is always drawn, horizontally centered
	public final int screenY; // Fixed Y screen coordinate where the player is always drawn, vertically centered

	// -------------------------------------------------------------------------
	// Animation
	// -------------------------------------------------------------------------
	int standCounter = 0; // Tracks frames elapsed while idle before snapping back to the standing sprite

	/**
	 * Constructs a Player, calculates its fixed center screen position, configures the hitbox,
	 * and loads default values and sprite images.
	 *
	 * @param gamePanel  the {@link GamePanel} this player belongs to
	 * @param keyHandler the {@link KeyHandler} used to read keyboard input
	 */
	public Player( GamePanel gamePanel, KeyHandler keyHandler ) {
		super(gamePanel);
		this.keyH = keyHandler;

		// Center the player on screen, offsetting by half a tile since
		// draw coordinates refer to the top-left corner of the sprite
		screenX = gamePanel.screenWidth / 2 - ( gamePanel.tileSize / 2 );
		screenY = gamePanel.screenHeight / 2 - ( gamePanel.tileSize / 2 );

		// Define the player's hitbox as a smaller rectangle inset from the sprite edges
		solidArea = new Rectangle();
		solidArea.x = gamePanel.tileSize / 6;      // 8px from the left edge of the sprite
		solidArea.y = gamePanel.tileSize / 3;      // 16px from the top edge of the sprite
		solidArea.width = gamePanel.tileSize * 2 / 3;  // 32px wide (2/3 of tile)
		solidArea.height = gamePanel.tileSize * 2 / 3;  // 32px tall (2/3 of tile)
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;

		type = EntityType.Player;
		setDefaultValues();
		getPlayerImage();
	}

	/**
	 * Sets the player's starting world position (tile 23, 21), movement speed, and default direction.
	 */
	public void setDefaultValues() {
		worldX = gamePanel.tileSize * 23; // Starting tile column
		worldY = gamePanel.tileSize * 21; // Starting tile row
		speed = 4;                       // Movement speed in pixels per frame
		direction = "down";                  // Default facing direction on game start

		// Player Status
		maxLife = 6; // Three hearts
		currentLife = maxLife;
	}

	/**
	 * Loads all directional walk-cycle sprite frames from the /player/ resource folder.
	 * <p>
	 * TODO: Streamline the process to make it easier to swap out the player sprite sheet.
	 */
	private void getPlayerImage() {
		up1 = setup("/player/boy_up_1.png");
		up2 = setup("/player/boy_up_2.png");
		down1 = setup("/player/boy_down_1.png");
		down2 = setup("/player/boy_down_2.png");
		left1 = setup("/player/boy_left_1.png");
		left2 = setup("/player/boy_left_2.png");
		right1 = setup("/player/boy_right_1.png");
		right2 = setup("/player/boy_right_2.png");
	}

	/**
	 * Reads keyboard input to move the player, runs collision checks, handles interactions,
	 * and advances the walk-cycle or idle animation accordingly.
	 * <p>
	 * TODO: Fix input handling so character movement is smoother.
	 */
	public void update() {
		if ( keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed ) {

			// Update direction based on which key is held
			if ( keyH.upPressed ) direction = "up";
			if ( keyH.downPressed ) direction = "down";
			if ( keyH.leftPressed ) direction = "left";
			if ( keyH.rightPressed ) direction = "right";

			// Tile Collision
			collisionOn = false;
			gamePanel.cChecker.checkTileCollision(this);

			// Object Collision
			int objIndex = gamePanel.cChecker.checkObjectCollision(this, true);
			objectInteraction(objIndex);

			// NPC Collision
			int npcIndex = gamePanel.cChecker.checkEntityCollision(this, gamePanel.npcs);
			interactNpc(npcIndex);

			// Enemy Collision
			int enemyIndex = gamePanel.cChecker.checkEntityCollision(this, gamePanel.enemies);
			touchEnemy(enemyIndex);

			// Check Events
			gamePanel.eHandler.checkEvents();

			keyH.interactPressed = false; // Always reset interact key to prevent repeated triggers

			// Only move if no collision was detected
			if ( !collisionOn ) {
				switch ( direction ) {
					case "up":
						worldY -= speed;
						break; // Move up (decrease Y)
					case "down":
						worldY += speed;
						break; // Move down (increase Y)
					case "left":
						worldX -= speed;
						break; // Move left (decrease X)
					case "right":
						worldX += speed;
						break; // Move right (increase X)
				}
			}

            /*
             // Diagonal movement (uncomment and remove direction-locking above to enable)
             if (keyH.upPressed)    { direction = "up";    if (!collisionOn) worldY -= speed; }
             if (keyH.downPressed)  { direction = "down";  if (!collisionOn) worldY += speed; }
             if (keyH.leftPressed)  { direction = "left";  if (!collisionOn) worldX -= speed; }
             if (keyH.rightPressed) { direction = "right"; if (!collisionOn) worldX += speed; }
             */

			// Advance walk-cycle, toggling sprite frames every 12 ticks
			spriteCounter++;
			if ( spriteCounter > 12 ) {
				if ( spriteNum == 1 ) spriteNum = 2;
				else if ( spriteNum == 2 ) spriteNum = 1;
				spriteCounter = 0;
			}
		} else {
			// Snap back to standing sprite after ~20 frames of no input (1/3 of a second)
			standCounter++;
			if ( standCounter == 20 ) {
				spriteNum = 1;
				standCounter = 0;
			}
		}

		// Needs to be outside of key if statement
		if ( invincible ) {
			invincibleCounter++;
			if ( invincibleCounter > 60 ) {
				invincible = false;
				invincibleCounter = 0;
			}
		}
	}

	/**
	 * Handles interaction logic for the object the player is currently touching.
	 *
	 * @param index the index of the touched object in {@code gamePanel.objs}, or 999 if none
	 */
	private void objectInteraction( int index ) {
		if ( index != 999 ) {
			// TODO: Implement object interaction logic per object type
		}
	}

	/**
	 * Triggers dialogue with an NPC if the player is touching one and presses the interact key.
	 *
	 * @param index the index of the touched NPC in {@code gamePanel.npcs}, or 999 if none
	 */
	private void interactNpc( int index ) {
		if ( index != 999 ) {
			if ( keyH.interactPressed ) {
				gamePanel.gameState = gamePanel.dialogueState;
				gamePanel.npcs[index].speak();
			}
		}
	}

	private void touchEnemy( int index ) {
		if ( index != 999 ) {
			if ( !invincible ) {
				currentLife -= 1;
				invincible = true;
			}
		}
	}

	/**
	 * Selects the correct sprite frame based on direction and animation state,
	 * then draws it at the player's fixed center screen position scaled to tile size.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics g2 ) {
		Graphics2D g2d = (Graphics2D) g2;
		BufferedImage image = null;

		// Select the correct sprite frame based on current direction and walk-cycle frame
		switch ( direction ) {
			case "up":
				if ( spriteNum == 1 ) image = up1;
				if ( spriteNum == 2 ) image = up2;
				break;
			case "down":
				if ( spriteNum == 1 ) image = down1;
				if ( spriteNum == 2 ) image = down2;
				break;
			case "left":
				if ( spriteNum == 1 ) image = left1;
				if ( spriteNum == 2 ) image = left2;
				break;
			case "right":
				if ( spriteNum == 1 ) image = right1;
				if ( spriteNum == 2 ) image = right2;
				break;
		}

		if ( invincible ) g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); //make invisible

		g2d.drawImage(image, screenX, screenY, null);

		// reset alpha
		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

		// TODO: Disable collision box debug drawing before release
		g2d.setColor(Color.RED);
		g2d.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
	}
}