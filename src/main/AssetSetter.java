package main;

import object.OBJ_Chest;
import object.OBJ_Door;
import object.OBJ_Key;

/**
 * Responsible for placing all interactable objects into the game world at their designated positions.
 */
public class AssetSetter {

    GamePanel gamePanel; // Reference to the main game panel for accessing the object array and tile size

    /**
     * Constructs an AssetSetter bound to the given game panel.
     *
     * @param gamePanel the {@link GamePanel} used to access the world object array and tile dimensions
     */
    public AssetSetter(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    /**
     * Instantiates and places all world objects into {@code gamePanel.objs} at their tile-based positions.
     *
     * TODO: Find a better way to load objects rather than manually setting each one, perhaps via an object map file.
     */
    public void setObject() {
        // Keys
        gamePanel.objs[0] = new OBJ_Key();
        gamePanel.objs[0].worldX = 23 * gamePanel.tileSize;
        gamePanel.objs[0].worldY = 7  * gamePanel.tileSize;

        gamePanel.objs[1] = new OBJ_Key();
        gamePanel.objs[1].worldX = 23 * gamePanel.tileSize;
        gamePanel.objs[1].worldY = 40 * gamePanel.tileSize;

        gamePanel.objs[2] = new OBJ_Key();
        gamePanel.objs[2].worldX = 37 * gamePanel.tileSize;
        gamePanel.objs[2].worldY = 7  * gamePanel.tileSize;

        // Doors
        gamePanel.objs[3] = new OBJ_Door();
        gamePanel.objs[3].worldX = 10 * gamePanel.tileSize;
        gamePanel.objs[3].worldY = 11 * gamePanel.tileSize;

        gamePanel.objs[4] = new OBJ_Door();
        gamePanel.objs[4].worldX = 8  * gamePanel.tileSize;
        gamePanel.objs[4].worldY = 28 * gamePanel.tileSize;

        gamePanel.objs[5] = new OBJ_Door();
        gamePanel.objs[5].worldX = 12 * gamePanel.tileSize;
        gamePanel.objs[5].worldY = 22 * gamePanel.tileSize;

        // Chest
        gamePanel.objs[6] = new OBJ_Chest();
        gamePanel.objs[6].worldX = 10 * gamePanel.tileSize;
        gamePanel.objs[6].worldY = 7  * gamePanel.tileSize;
    }
}