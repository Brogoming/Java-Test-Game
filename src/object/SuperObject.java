package object;

import main.GamePanel;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Base class for all interactable objects in the game world,
 * storing shared properties and handling screen-relative rendering.
 */
public class SuperObject {

    public BufferedImage image; // The sprite image displayed for this object
    public String name; // The name identifier for this object
    public boolean collision = false; // Whether this object blocks entity movement
    public int worldX, worldY; // The object's position in world pixels
    public Rectangle solidArea = new Rectangle(0, 0, 48, 48); // The collision hitbox area for this object
    public int solidAreaDefaultX = 0; // Default X origin of the solid area
    public int solidAreaDefaultY = 0; // Default Y origin of the solid area

    /**
     * Draws the object onto the screen at its world position translated relative to the player,
     * only if it falls within the visible screen boundary.
     *
     * @param g2 the {@link Graphics} context used for rendering
     * @param gamePanel the {@link GamePanel} used to access player position and screen dimensions
     */
    public void draw(Graphics g2, GamePanel gamePanel) {
        // Translate world position to screen position relative to the player
        int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
        int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

        // Only draw object within the visible screen boundary, plus a 1-tile buffer for smooth scrolling
        if (worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX
                && worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX
                && worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY
                && worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY) {
            g2.drawImage(image, screenX, screenY, gamePanel.tileSize, gamePanel.tileSize, null);
        }
    }
}