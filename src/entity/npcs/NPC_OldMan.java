package entity.npcs;

import entity.Entity;
import entity.EntityType;
import main.GamePanel;

import java.util.Random;

/**
 * Represents the Old Man NPC, an entity that wanders randomly and cycles through dialogue when spoken to.
 */
public class NPC_OldMan extends Entity {
	GamePanel gamePanel;

	/**
	 * Constructs the Old Man NPC, setting its default direction, speed, sprites, and dialogue.
	 *
	 * @param gamePanel the {@link GamePanel} used to access world and collision systems
	 */
	public NPC_OldMan( GamePanel gamePanel ) {
		super(gamePanel);
		this.gamePanel = gamePanel;

		// Set movement
		direction = "down";
		speed = 1;
		type = EntityType.NPC;

		// Init body and dialogue
		getNpcImage();
		setDialogue();

		// Set hit box
		solidArea.x = gamePanel.tileSize / 6;      // 8px from the left edge of the sprite
		solidArea.y = gamePanel.tileSize / 3;      // 16px from the top edge of the sprite
		solidArea.width = gamePanel.tileSize * 2 / 3;  // 32px wide (2/3 of tile)
		solidArea.height = gamePanel.tileSize * 2 / 3;  // 32px tall (2/3 of tile)
		solidAreaDefaultX = solidArea.x;
		solidAreaDefaultY = solidArea.y;
	}

	/**
	 * Loads all directional walk-cycle sprite frames from the /npc/ resource folder.
	 * If loading fails, the stack trace is printed.
	 * <p>
	 * TODO: Streamline the process to make it easier to swap out the NPC sprites.
	 */
	private void getNpcImage() {
		up1 = setup("/npc/oldman_up_1.png", gamePanel.tileSize, gamePanel.tileSize);
		up2 = setup("/npc/oldman_up_2.png", gamePanel.tileSize, gamePanel.tileSize);
		down1 = setup("/npc/oldman_down_1.png", gamePanel.tileSize, gamePanel.tileSize);
		down2 = setup("/npc/oldman_down_2.png", gamePanel.tileSize, gamePanel.tileSize);
		left1 = setup("/npc/oldman_left_1.png", gamePanel.tileSize, gamePanel.tileSize);
		left2 = setup("/npc/oldman_left_2.png", gamePanel.tileSize, gamePanel.tileSize);
		right1 = setup("/npc/oldman_right_1.png", gamePanel.tileSize, gamePanel.tileSize);
		right2 = setup("/npc/oldman_right_2.png", gamePanel.tileSize, gamePanel.tileSize);
	}

	/**
	 * Populates the dialogue array with the Old Man's lines, shown sequentially each time the player interacts.
	 * <p>
	 * TODO: Streamline dialogue loading to make it easier to swap out NPC dialogue.
	 */
	private void setDialogue() {
		// Use \n within a string to break it onto a second line if the text is too long
		dialogues[0] = "Hello my boy!";
		dialogues[1] = "Do you know where the treasure is? I've been \nlooking for it for so long I don't think it exists!";
		dialogues[2] = "I am old...";
		dialogues[3] = "I think All Star Superman is overrated, not bad \njust overrated.";
	}

	/**
	 * Picks a new random direction every 120 frames (2 seconds) to simulate idle wandering.
	 */
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