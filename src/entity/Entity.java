package entity;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Base class storing shared properties for all in-game entities
 * such as the player, monsters, and NPCs.
 */
public class Entity {

    // -------------------------------------------------------------------------
    // World Position & Movement
    // -------------------------------------------------------------------------
    public int worldX; // The entity's current X position in pixels within the game world.
    public int worldY; // The entity's current Y position in pixels within the game world.
    public int speed; // The number of pixels the entity moves per frame.

    // -------------------------------------------------------------------------
    // Sprite Images
    // -------------------------------------------------------------------------
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; //It describes an image with an accessible buffer of image data, use it to store image files

    // -------------------------------------------------------------------------
    // Direction & Animation State
    // -------------------------------------------------------------------------
    public String direction; // The current facing direction of the entity ("up", "down", "left", or "right").
    public int spriteCounter = 0; // Tracks how many frames have passed since the last sprite frame swap.
    public int spriteNum = 1; // The current active sprite frame index (1 or 2) used in the walk-cycle animation.

    // -------------------------------------------------------------------------
    // Hit Box
    // -------------------------------------------------------------------------
    public Rectangle solidArea; // Describes the area of the hit box
    public boolean collisionOn = false;
}