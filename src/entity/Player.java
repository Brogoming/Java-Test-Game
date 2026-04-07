package entity;

import entity.objects.OBJ_Normal_Sword;
import entity.objects.OBJ_Wood_Shield;
import main.GamePanel;
import main.GameState;
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
	public boolean attackCancel = false;

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
		screenX = gamePanel.screenWidth / 2 - (gamePanel.tileSize / 2);
		screenY = gamePanel.screenHeight / 2 - (gamePanel.tileSize / 2);

		// Hitbox is inset from the sprite edges to match the character's visible body
		solidArea = new Rectangle();
		solidArea.x = gamePanel.tileSize / 6;      // ~8px from the left edge
		solidArea.y = gamePanel.tileSize / 3;      // ~16px from the top edge
		solidArea.width = gamePanel.tileSize * 2 / 3;  // 32px wide (2/3 of tile)
		solidArea.height = gamePanel.tileSize * 2 / 3;  // 32px tall (2/3 of tile)
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;

		// Attack area is 3/4 of a tile — large enough to hit adjacent enemies without reaching two tiles
		attackArea.width = gamePanel.tileSize * 3 / 4;
		attackArea.height = gamePanel.tileSize * 3 / 4;

		type = EntityType.Player;
		setDefaultValues();
		getPlayerImages();
		getPlayerAttackImages();
	}

	/**
	 * Sets the player's starting world position (tile 23, 21), movement speed, and default direction.
	 */
	public void setDefaultValues() {
		worldX = gamePanel.tileSize * 23; // Starting tile column
		worldY = gamePanel.tileSize * 21; // Starting tile row
		speed = 4;                       // Movement speed in pixels per frame
		direction = "down";                  // Default facing direction on game start

		// Player Stats
		maxLife = 6; // Three hearts (2 life units per heart)
		currentLife = maxLife;
		level = 1;
		strength = 1; // More strength they have, the more damage they deal
		dexterity = 1; // More dex they have, the less damage they take
		exp = 0;
		nextLevelExp = 5;
		coins = 0;
		currentWeapon = new OBJ_Normal_Sword(gamePanel);
		currentShield = new OBJ_Wood_Shield(gamePanel);
		attack = getAttack(); // Total attack value
		defence = getDefence(); // Total defense value
	}

	/**
	 * Gets the total amount of damage a player can do
	 *
	 * @return Total amount of damage a player can deal
	 */
	private int getAttack() {
		return strength * currentWeapon.attackDamage;
	}

	/**
	 * Gets the total amount of defense the player can block against
	 *
	 * @return Total amount of damage the player can block
	 */
	private int getDefence() {
		return dexterity * currentShield.defenceValue;
	}

	/**
	 * Loads all directional walk-cycle sprite frames from the /player/ resource folder.
	 * <p>
	 * TODO: Streamline the process to make it easier to swap out the player sprite sheet.
	 */
	private void getPlayerImages() {
		up1 = setup("/player/boy_up_1.png", gamePanel.tileSize, gamePanel.tileSize);
		up2 = setup("/player/boy_up_2.png", gamePanel.tileSize, gamePanel.tileSize);
		down1 = setup("/player/boy_down_1.png", gamePanel.tileSize, gamePanel.tileSize);
		down2 = setup("/player/boy_down_2.png", gamePanel.tileSize, gamePanel.tileSize);
		left1 = setup("/player/boy_left_1.png", gamePanel.tileSize, gamePanel.tileSize);
		left2 = setup("/player/boy_left_2.png", gamePanel.tileSize, gamePanel.tileSize);
		right1 = setup("/player/boy_right_1.png", gamePanel.tileSize, gamePanel.tileSize);
		right2 = setup("/player/boy_right_2.png", gamePanel.tileSize, gamePanel.tileSize);
	}

	/**
	 * Loads all directional attack animation frames from the /player/ resource folder.
	 * Up and down attack sprites are twice the tile height to extend above or below the player;
	 * left and right attack sprites are twice the tile width to extend to either side.
	 */
	private void getPlayerAttackImages() {
		attackUp1 = setup("/player/boy_attack_up_1.png", gamePanel.tileSize, gamePanel.tileSize * 2);
		attackUp2 = setup("/player/boy_attack_up_2.png", gamePanel.tileSize, gamePanel.tileSize * 2);
		attackDown1 = setup("/player/boy_attack_down_1.png", gamePanel.tileSize, gamePanel.tileSize * 2);
		attackDown2 = setup("/player/boy_attack_down_2.png", gamePanel.tileSize, gamePanel.tileSize * 2);
		attackLeft1 = setup("/player/boy_attack_left_1.png", gamePanel.tileSize * 2, gamePanel.tileSize);
		attackLeft2 = setup("/player/boy_attack_left_2.png", gamePanel.tileSize * 2, gamePanel.tileSize);
		attackRight1 = setup("/player/boy_attack_right_1.png", gamePanel.tileSize * 2, gamePanel.tileSize);
		attackRight2 = setup("/player/boy_attack_right_2.png", gamePanel.tileSize * 2, gamePanel.tileSize);
	}

	/**
	 * Drives the two-phase attack animation and performs a single hitbox sweep during frames 6–25.
	 * The player's world position and solid area are temporarily shifted in the attack direction
	 * to project the attack hitbox one tile ahead, then restored immediately after the check.
	 */
	private void attackAction() {
		spriteCounter++;
		if ( spriteCounter <= 5 ) spriteNum = 1; // Phase 1: wind-up frame, first 5 ticks

		if ( spriteCounter > 5 && spriteCounter <= 25 ) { // Phase 2: swing frame, ticks 6–25
			spriteNum = 2;

			// Snapshot current position and hitbox so they can be restored after the sweep
			int currentWorldX = worldX;
			int currentWorldY = worldY;
			int solidAreaWidth = solidArea.width;
			int solidAreaHeight = solidArea.height;

			// Project the player's world position forward by the attack area's size
			switch ( direction ) {
				case "up":
					worldY -= attackArea.height;
					break;
				case "down":
					worldY += attackArea.height;
					break;
				case "left":
					worldX -= attackArea.width;
					break;
				case "right":
					worldX += attackArea.width;
					break;
			}

			// Temporarily replace the solid area with the attack area for collision detection
			solidArea.width = attackArea.width;
			solidArea.height = attackArea.height;

			int monsterIndex = gamePanel.cChecker.checkEntityCollision(this, gamePanel.enemies);
			damageEnemy(monsterIndex);

			// Restore position and solid area after the hit check
			worldX = currentWorldX;
			worldY = currentWorldY;
			solidArea.width = solidAreaWidth;
			solidArea.height = solidAreaHeight;
		}

		if ( spriteCounter > 25 ) { // Phase 3: reset attack state after full animation
			spriteNum = 1;
			spriteCounter = 0;
			attacking = false;
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
	 * If no NPC is present, the interact key instead initiates an attack swing.
	 *
	 * @param index the index of the touched NPC in {@code gamePanel.npcs}, or 999 if none
	 */
	private void interactNpc( int index ) {
		if ( keyH.interactPressed ) {
			if ( index != 999 ) {
				attackCancel = true;
				gamePanel.gameState = GameState.Dialogue;
				gamePanel.npcs[index].speak();
			}
		}
	}

	/**
	 * Deals one point of damage to the player on enemy contact and activates the invincibility window.
	 *
	 * @param index the index of the contacted enemy in {@code gamePanel.enemies}, or 999 if none
	 */
	private void touchEnemy( int index ) {
		if ( index != 999 ) {
			if ( !invincible ) {
				gamePanel.playSoundEffect(6); // Sound effect index 6 = received damage
				currentLife -= 1;
				invincible = true;
			}
		}
	}

	/**
	 * Applies one point of damage to the enemy at the given index if it is not already invincible,
	 * triggers its damage reaction, and marks it as dying when its life reaches zero.
	 *
	 * @param index the index of the hit enemy in {@code gamePanel.enemies}, or 999 if none
	 */
	private void damageEnemy( int index ) {
		if ( index != 999 ) {
			if ( !gamePanel.enemies[index].invincible ) {
				gamePanel.playSoundEffect(5); // Sound effect index 5 = hit monster
				gamePanel.enemies[index].currentLife -= 1;
				gamePanel.enemies[index].invincible = true;
				gamePanel.enemies[index].damageReaction();

				if ( gamePanel.enemies[index].currentLife <= 0 ) {
					gamePanel.enemies[index].dying = true;
				}
			}
		}
	}

	/**
	 * Reads keyboard input to move the player, runs collision checks, handles interactions,
	 * and advances the walk-cycle or idle animation accordingly.
	 * <p>
	 * TODO: Fix input handling so character movement is smoother.
	 */
	public void update() {

		if ( attacking ) {
			attackAction();
		} else if ( keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed || keyH.interactPressed ) {

			if ( keyH.upPressed ) direction = "up";
			if ( keyH.downPressed ) direction = "down";
			if ( keyH.leftPressed ) direction = "left";
			if ( keyH.rightPressed ) direction = "right";

			collisionOn = false;
			gamePanel.cChecker.checkTileCollision(this);

			int objIndex = gamePanel.cChecker.checkObjectCollision(this, true);
			objectInteraction(objIndex);

			int npcIndex = gamePanel.cChecker.checkEntityCollision(this, gamePanel.npcs);
			interactNpc(npcIndex);

			int enemyIndex = gamePanel.cChecker.checkEntityCollision(this, gamePanel.enemies);
			touchEnemy(enemyIndex);

			gamePanel.eHandler.checkEvents();

			// Only move if no collision was detected and the player is not interacting
			if ( !collisionOn && !keyH.interactPressed ) {
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

			if ( keyH.interactPressed && !attackCancel ) {
				gamePanel.playSoundEffect(7);
				attacking = true;
				spriteCounter = 0;
			}

			attackCancel = false;
			keyH.interactPressed = false; // Always reset interact key to prevent repeated triggers

			/*
			 * Alternate diagonal movement — direction-locking above must be removed to enable this.
			 * Disabled because the current system prevents diagonal movement for simpler grid navigation.
			 *
			 * if (keyH.upPressed)    { direction = "up";    if (!collisionOn) worldY -= speed; }
			 * if (keyH.downPressed)  { direction = "down";  if (!collisionOn) worldY += speed; }
			 * if (keyH.leftPressed)  { direction = "left";  if (!collisionOn) worldX -= speed; }
			 * if (keyH.rightPressed) { direction = "right"; if (!collisionOn) worldX += speed; }
			 */

			// Advance walk-cycle, toggling sprite frames every 12 ticks
			spriteCounter++;
			if ( spriteCounter > 12 ) {
				spriteNum = (spriteNum == 1) ? 2 : 1;
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

		// Invincibility counter must run every frame, not gated behind input checks
		if ( invincible ) {
			invincibleCounter++;
			if ( invincibleCounter > 60 ) {
				invincible = false;
				invincibleCounter = 0;
			}
		}
	}

	/**
	 * Selects the correct sprite frame based on direction and animation state,
	 * then draws it at the player's fixed center screen position scaled to tile size.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics2D g2 ) {
		BufferedImage tempImage = null;
		int tempScreenX = screenX;
		int tempScreenY = screenY;

		switch ( direction ) {
			case "up":
				if ( !attacking ) {
					tempImage = (spriteNum == 1) ? up1 : up2;
				} else {
					tempScreenY -= gamePanel.tileSize; // Shift draw origin up so the extended attack sprite aligns correctly
					tempImage = (spriteNum == 1) ? attackUp1 : attackUp2;
				}
				break;
			case "down":
				if ( !attacking ) {
					tempImage = (spriteNum == 1) ? down1 : down2;
				} else {
					tempImage = (spriteNum == 1) ? attackDown1 : attackDown2;
				}
				break;
			case "left":
				if ( !attacking ) {
					tempImage = (spriteNum == 1) ? left1 : left2;
				} else {
					tempScreenX -= gamePanel.tileSize; // Shift draw origin left so the extended attack sprite aligns correctly
					tempImage = (spriteNum == 1) ? attackLeft1 : attackLeft2;
				}
				break;
			case "right":
				if ( !attacking ) {
					tempImage = (spriteNum == 1) ? right1 : right2;
				} else {
					tempImage = (spriteNum == 1) ? attackRight1 : attackRight2;
				}
				break;
		}

		if ( invincible )
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f)); // Semi-transparent flash during invincibility window

		g2.drawImage(tempImage, tempScreenX, tempScreenY, null);

		g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Restore full opacity after any transparency effects

		// TODO: Disable collision box debug drawing before release
		g2.setColor(Color.RED);
		g2.drawRect(screenX + solidArea.x, screenY + solidArea.y, solidArea.width, solidArea.height);
	}
}