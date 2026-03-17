package entity;

import java.awt.image.BufferedImage;

/**
 * Store variables that will be used in player, monster, and NPC classes
 */
public class Entity {

    public int x, y;
    public int speed;
    public BufferedImage up1, up2, down1, down2, left1, left2, right1, right2; //It describes an image with an accessible buffer of image data, use it to store image files
    public String direction;

    public int spriteCounter = 0;
    public int spriteNum = 1;
}
