package entity;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Base class storing shared properties and common behavior for all in-game entities
 * such as the player, monsters, and NPCs.
 */
public class Entity {

	GamePanel gamePanel; // Reference to the main game panel for accessing world, collision, and UI systems
	public String name;      // The name identifier for this entity
	public EntityType type;  // Classification of this entity: Player, Npc, or Monster

	// -------------------------------------------------------------------------
	// World Position & Movement
	// -------------------------------------------------------------------------
	public int worldX;  // The entity's current X position in pixels within the game world
	public int worldY;  // The entity's current Y position in pixels within the game world
	public int speed;   // The number of pixels the entity moves per frame

	// -------------------------------------------------------------------------
	// Sprite Images
	// -------------------------------------------------------------------------
	public BufferedImage up1, up2;       // Walk-cycle frames for upward movement
	public BufferedImage down1, down2;   // Walk-cycle frames for downward movement
	public BufferedImage left1, left2;   // Walk-cycle frames for leftward movement
	public BufferedImage right1, right2; // Walk-cycle frames for rightward movement
	public BufferedImage attackUp1, attackUp2;       // Attack-cycle frames for upward attacks
	public BufferedImage attackDown1, attackDown2;   // Attack-cycle frames for downward attacks
	public BufferedImage attackLeft1, attackLeft2;   // Attack-cycle frames for leftward attacks
	public BufferedImage attackRight1, attackRight2; // Attack-cycle frames for rightward attacks

	// -------------------------------------------------------------------------
	// Direction & Animation State
	// -------------------------------------------------------------------------
	public String direction = "down"; // Current facing direction: "up", "down", "left", or "right"
	public int spriteCounter = 0;      // Tracks frames elapsed since the last sprite swap
	public int spriteNum = 1;      // Active sprite frame index (1 or 2) for the walk cycle

	// -------------------------------------------------------------------------
	// Collision
	// -------------------------------------------------------------------------
	public Rectangle solidArea;         // The hitbox used for collision detection
	public int solidAreaDefaultX;       // Default X offset of the solid area before any world translation
	public int solidAreaDefaultY;       // Default Y offset of the solid area before any world translation
	public boolean collisionOn = false; // True when the entity is currently blocked by a collision this frame
	public boolean collision = false; // Whether this entity blocks the movement of other entities
	public Rectangle attackArea = new Rectangle(0, 0, 0, 0); // Hitbox activated during an attack swing

	// -------------------------------------------------------------------------
	// Entity Interaction
	// -------------------------------------------------------------------------
	public BufferedImage image, image1, image2; // General-purpose sprite slots used by objects and HUD elements
	public int actionCounter = 0;               // General-purpose counter used to pace NPC actions or behaviors
	public String[] dialogues = new String[20]; // Stores the sequential dialogue lines for this entity
	int dialogueIndex = 0;                      // Tracks which dialogue line will be shown on the next speak() call

	// -------------------------------------------------------------------------
	// Character Status
	// -------------------------------------------------------------------------
	public int maxLife;              // The entity's maximum life total
	public int currentLife;          // The entity's current remaining life
	public boolean invincible = false; // True while the entity is in its post-hit invincibility window
	public int invincibleCounter = 0;     // Tracks frames elapsed during the invincibility window
	boolean attacking = false; // True while the entity is actively performing an attack
	public boolean alive = true;  // False once the entity has completed its dying animation
	public boolean dying = false; // True while the dying animation is playing
	int dyingCounter = 0;     // Tracks frames elapsed during the dying animation
	boolean hpBarOn = false; // True while the enemy HP bar should be rendered
	int hpBarCounter = 0;     // Tracks how long the HP bar has been visible; hides after 600 frames

	/**
	 * Constructs an Entity bound to the game panel and initializes its hitbox to one full tile.
	 *
	 * @param gamePanel the {@link GamePanel} used to access collision, UI, and world systems
	 */
	public Entity( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
		solidArea = new Rectangle(0, 0, gamePanel.tileSize, gamePanel.tileSize);
	}

	/**
	 * Defines the entity's per-frame behavior; intended to be overridden by subclasses such as NPCs.
	 */
	public void setAction() {}

	/**
	 * Defines the entity's reaction when struck; intended to be overridden by subclasses
	 * to implement knockback, retaliation, or other hit responses.
	 */
	public void damageReaction() {}

	/**
	 * Displays the next line of dialogue and turns the entity to face the player.
	 * Resets to the first dialogue line when all lines have been shown.
	 */
	public void speak() {
		if ( dialogues.length > 0 ) {
			// Reset to the first dialogue line if we've reached a null entry (end of defined dialogues)
			if ( dialogues[dialogueIndex] == null ) dialogueIndex = 0;

			gamePanel.ui.currentDialogue = dialogues[dialogueIndex];
			dialogueIndex++;

			// Turn the entity to face the player during conversation
			switch ( gamePanel.player.direction ) {
				case "up":
					direction = "down";
					break;
				case "down":
					direction = "up";
					break;
				case "left":
					direction = "right";
					break;
				case "right":
					direction = "left";
					break;
			}

			gamePanel.player.spriteNum = 1; // Reset player sprite to idle frame during dialogue
		}
	}

	/**
	 * Loads an image from the given resource path and scales it to the specified dimensions.
	 *
	 * @param imagePath the classpath resource path of the image to load (e.g. "/player/boy_up_1.png")
	 * @param width     the desired width of the output image in pixels
	 * @param height    the desired height of the output image in pixels
	 * @return the loaded and scaled {@link BufferedImage}, or null if loading failed
	 */
	public BufferedImage setup( String imagePath, int width, int height ) {
		UtilityTool util = new UtilityTool();
		BufferedImage tempImage = null;
		try {
			tempImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
			tempImage = util.scaleImage(tempImage, width, height);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return tempImage;
	}

	/**
	 * Plays a blink animation by toggling the entity's opacity every 10 frames, then
	 * marks the entity as dead once the animation completes at 40 frames.
	 * <p>
	 * TODO: Replace the blink effect with dedicated death sprites for a more polished death sequence.
	 */
	private void dyingAnimation( Graphics2D g2 ) {
		dyingCounter++;
		if ( dyingCounter % 10 == 0 )
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0f)); // Fully invisible on even blink ticks
		else
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
		if ( dyingCounter > 40 ) {
			dying = false;
			alive = false;
		}
	}

	/**
	 * Updates the entity's position and animation each frame, running collision checks before
	 * applying movement in the current direction.
	 */
	public void update() {
		setAction();

		// Reset collision flag before running checks each frame
		collisionOn = false;
		gamePanel.cChecker.checkTileCollision(this);
		gamePanel.cChecker.checkObjectCollision(this, false);
		gamePanel.cChecker.checkEntityCollision(this, gamePanel.enemies);
		gamePanel.cChecker.checkEntityCollision(this, gamePanel.npcs);
		boolean contactPlayer = gamePanel.cChecker.checkPlayerCollision(this);

		if ( contactPlayer && this.type == EntityType.Enemy ) {
			if ( !gamePanel.player.invincible ) {
				gamePanel.playSoundEffect(6); // Sound effect index 6 = received damage
				gamePanel.player.currentLife -= 1;
				gamePanel.player.invincible = true;
			}
		}

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

		// Advance walk-cycle animation, toggling sprite frames every 12 ticks
		spriteCounter++;
		if ( spriteCounter > 12 ) {
			spriteNum = (spriteNum == 1) ? 2 : 1;
			spriteCounter = 0;
		}

		// Invincibility counter must run every frame, not gated behind input checks
		if ( invincible ) {
			invincibleCounter++;
			if ( invincibleCounter > 40 ) {
				invincible = false;
				invincibleCounter = 0;
			}
		}
	}

	/**
	 * Draws the correct directional sprite frame at the entity's screen-relative position,
	 * only if the entity is within the visible screen boundary.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics2D g2 ) {
		image = null;

		// Translate world position to screen position relative to the player
		int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
		int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

		// Only draw if within the visible screen boundary, plus a 1-tile buffer for smooth scrolling
		if ( worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX &&
				worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX &&
				worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY &&
				worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY ) {

			// Select the correct sprite frame based on current direction and walk-cycle frame
			switch ( direction ) {
				case "up":
					image = (spriteNum == 1) ? up1 : up2;
					break;
				case "down":
					image = (spriteNum == 1) ? down1 : down2;
					break;
				case "left":
					image = (spriteNum == 1) ? left1 : left2;
					break;
				case "right":
					image = (spriteNum == 1) ? right1 : right2;
					break;
			}

			// Enemy HP bar — visible for 600 frames (10 seconds) after taking damage
			if ( type == EntityType.Enemy && hpBarOn ) {
				double oneScale = (double) gamePanel.tileSize / maxLife;
				double currentHpBar = oneScale * currentLife;

				g2.setColor(new Color(35, 35, 35));
				g2.fillRect(screenX - 1, screenY - 16, gamePanel.tileSize + 2, 12); // Dark background track

				g2.setColor(new Color(255, 0, 30));
				g2.fillRect(screenX, screenY - 15, (int) currentHpBar, 10); // Red fill scaled to current life

				hpBarCounter++;
				if ( hpBarCounter > 600 ) {
					hpBarOn = false;
					hpBarCounter = 0;
				}
			}

			if ( invincible ) {
				hpBarOn = true;
				hpBarCounter = 0;
				g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f)); // Semi-transparent flash during invincibility window
			}
			if ( dying ) dyingAnimation(g2);

			g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f)); // Restore full opacity after any transparency effects
		}
	}
}