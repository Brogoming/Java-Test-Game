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
	public String name; // The name identifier for this object
	public EntityType type; // Player, Npc, Monster

	// World Position & Movement
	public int worldX;   // The entity's current X position in pixels within the game world
	public int worldY;   // The entity's current Y position in pixels within the game world
	public int speed;    // The number of pixels the entity moves per frame

	// Sprite Images - two walk-cycle frames per direction
	public BufferedImage up1, up2;       // Walk-cycle frames for upward movement
	public BufferedImage down1, down2;   // Walk-cycle frames for downward movement
	public BufferedImage left1, left2;   // Walk-cycle frames for leftward movement
	public BufferedImage right1, right2; // Walk-cycle frames for rightward movement

	// Direction & Animation State
	public String direction = "down"; // Current facing direction: "up", "down", "left", or "right"
	public int spriteCounter = 0;  // Tracks frames elapsed since the last sprite swap
	public int spriteNum = 1;      // Active sprite frame index (1 or 2) for the walk cycle

	// Collision
	public Rectangle solidArea;    // The hitbox used for collision detection
	public int solidAreaDefaultX;  // Default X offset of the solid area before any world translation
	public int solidAreaDefaultY;  // Default Y offset of the solid area before any world translation
	public boolean collisionOn = false; // True when the entity is currently blocked by a collision
	public boolean collision = false; // Whether this object blocks entity movement

	// Entity Interaction
	public BufferedImage image, image1, image2; // The sprite image displayed for this object
	public int actionCounter = 0;          // General-purpose counter used to pace NPC actions or behaviors
	public String[] dialogues = new String[20];   // Stores the sequential dialogue lines for this entity
	int dialogueIndex = 0;                 // Tracks which dialogue line will be shown on the next speak() call

	// Character Status
	public int maxLife;
	public int currentLife;
	public boolean invincible = false;
	public int invincibleCounter = 0;

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
			if ( spriteNum == 1 ) spriteNum = 2;
			else if ( spriteNum == 2 ) spriteNum = 1;
			spriteCounter = 0;
		}
	}

	/**
	 * Loads an image from the given resource path and scales it to the game's tile size.
	 *
	 * @param imagePath the classpath resource path of the image to load (e.g. "/player/boy_up_1.png")
	 * @return the loaded and scaled {@link BufferedImage}, or null if loading failed
	 */
	public BufferedImage setup( String imagePath ) {
		UtilityTool util = new UtilityTool();
		BufferedImage tempImage = null;
		try {
			tempImage = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(imagePath)));
			tempImage = util.scaleImage(tempImage, gamePanel.tileSize, gamePanel.tileSize);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
		return tempImage;
	}

	/**
	 * Draws the correct directional sprite frame at the entity's screen-relative position,
	 * only if the entity is within the visible screen boundary.
	 *
	 * @param g2 the {@link Graphics} context used for rendering
	 */
	public void draw( Graphics g2 ) {
		image = null;

		// Translate world position to screen position relative to the player
		int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
		int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

		// Only draw if within the visible screen boundary, plus a 1-tile buffer for smooth scrolling
		if ( worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX && worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX && worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY && worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY ) {

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

			g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
		}
	}
}