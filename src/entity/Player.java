package entity;

import main.GamePanel;
import main.KeyHandler;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents the player-controlled character, handling movement input,
 * walk-cycle animation, and rendering at a fixed center screen position.
 */
public class Player extends Entity {

    // -------------------------------------------------------------------------
    // Core References
    // -------------------------------------------------------------------------
    GamePanel gamePanel; // Reference to the main game panel, used to access screen and tile dimensions.
    KeyHandler keyH; // Reference to the key handler, used to read directional input each frame.

    // -------------------------------------------------------------------------
    // Screen Position
    // -------------------------------------------------------------------------
    public final int screenX; // The fixed X screen coordinate where the player is always drawn, horizontally centered.
    public final int screenY; // The fixed Y screen coordinate where the player is always drawn, vertically centered.

    // -------------------------------------------------------------------------
    // Constructor
    // -------------------------------------------------------------------------

    /**
     * Constructs a Player, calculates its fixed center screen position,
     * and loads default values and sprite images.
     * @param gamePanel  the {@link GamePanel} this player belongs to
     * @param keyHandler the {@link KeyHandler} used to read keyboard input
     */
    public Player(GamePanel gamePanel, KeyHandler keyHandler) {
        this.gamePanel = gamePanel;
        this.keyH = keyHandler;

        // Center the player on screen, offsetting by half a tile since
        // draw coordinates refer to the top-left corner of the sprite
        screenX = gamePanel.screenWidth / 2 - (gamePanel.tileSize / 2);
        screenY = gamePanel.screenHeight / 2 - (gamePanel.tileSize / 2);

        // set player hit box
        int hitBoxX = gamePanel.tileSize / 6; // 1/6 from the left of entity = 8 pixels
        int hitBoxY = gamePanel.tileSize / 3; // 1/3 from the top of entity = 16 pixels
        int hitBoxWidth = gamePanel.tileSize * 3 / 4; // 3/4 of the players width = 32 pixels
        int hitBoxHeight = gamePanel.tileSize * 2 / 3; // 3/4 of the players height = 32 pixels

        solidArea = new Rectangle(hitBoxX, hitBoxY, hitBoxWidth, hitBoxHeight);

        setDefaultValues();
        getPlayerImage();
    }

    // -------------------------------------------------------------------------
    // Initialization
    // -------------------------------------------------------------------------

    /**
     * Sets the player's starting world position (tile 23, 21), movement speed, and default direction.
     */
    public void setDefaultValues() {
        // Place the player at tile (23, 21) in the game world
        worldX = gamePanel.tileSize * 23;
        worldY = gamePanel.tileSize * 21;

        // Movement speed in pixels per frame
        speed = 4;

        // Default facing direction on game start
        direction = "down";
    }

    /**
     * Loads all directional walk-cycle sprite frames from the /player/ resource folder.
     * If loading fails, the stack trace is printed.
     * <p>
     * TODO: Streamline the process to make it easier to swap out the player sprite sheet.
     */
    public void getPlayerImage() {
        try {
            up1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_up_1.png")));
            up2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_up_2.png")));
            down1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_down_1.png")));
            down2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_down_2.png")));
            left1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_left_1.png")));
            left2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_left_2.png")));
            right1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_right_1.png")));
            right2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/player/boy_right_2.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // -------------------------------------------------------------------------
    // Game Loop Methods
    // -------------------------------------------------------------------------

    /**
     * Reads keyboard input to move the player in the world and advances the walk-cycle animation.
     * Sprite animation only ticks while a movement key is held.
     */
    public void update() {
        // Only process movement and animation if at least one direction key is held
        if (keyH.upPressed || keyH.downPressed || keyH.leftPressed || keyH.rightPressed) {

            //TODO fix user input so character movement is smoother
            if (keyH.upPressed) direction = "up";
            else if (keyH.downPressed) direction = "down";
            else if (keyH.leftPressed) direction = "left";
            else if (keyH.rightPressed) direction = "right";
            // if you want diagonal movement don't use else if or just add it here

            // CHECK TILE COLLISION
            collisionOn = false;
            gamePanel.cChecker.checkCollision(this);

            //IF COLLISION IS FALSE, PLAYER CAN MOVE
            if (!collisionOn) {
                switch (direction) {
                    case "up":
                        worldY -= speed; // Move player up in the world (decrease Y)
                        break;
                    case "down":
                        worldY += speed; // Move player down in the world (increase Y)
                        break;
                    case "left":
                        worldX -= speed; // Move player left in the world (decrease X)
                        break;
                    case "right":
                        worldX += speed; // Move player right in the world (increase X)
                        break;
                }
            }

            spriteCounter++; // Advance the animation frame counter each update tick
            if (spriteCounter > 12) { // Toggle between sprite frames 1 and 2 every 12 frames to create a walk cycle
                if (spriteNum == 1) spriteNum = 2;
                else if (spriteNum == 2) spriteNum = 1;
                spriteCounter = 0; // Reset counter after toggling
            }
        }
    }

    /**
     * Selects the correct sprite frame based on direction and animation state,
     * then draws it at the player's fixed center screen position scaled to tile size.
     *
     * @param g2 the {@link Graphics2D} context used for rendering
     */
    public void draw(Graphics2D g2) {
        // Holds the sprite frame to draw this tick; determined by direction and animation frame
        BufferedImage image = null;

        // Select the correct sprite frame based on current direction and walk-cycle frame
        switch (direction) {
            case "up":
                if (spriteNum == 1) image = up1;
                if (spriteNum == 2) image = up2;
                break;
            case "down":
                if (spriteNum == 1) image = down1;
                if (spriteNum == 2) image = down2;
                break;
            case "left":
                if (spriteNum == 1) image = left1;
                if (spriteNum == 2) image = left2;
                break;
            case "right":
                if (spriteNum == 1) image = right1;
                if (spriteNum == 2) image = right2;
                break;
        }

        // Draw the selected sprite at the player's fixed screen position, scaled to tile size
        g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
    }
}