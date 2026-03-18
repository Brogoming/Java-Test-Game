package main;

import entity.Entity;

public class CollisionChecker {

    GamePanel gamePanel;

    public CollisionChecker(GamePanel gamePanel) {
        this.gamePanel = gamePanel;
    }

    public void checkCollision(Entity entity) {
        int entityLeftWorldX = entity.worldX + entity.solidArea.x;
        int entityRightWorldX = entity.worldX + entity.solidArea.x + entity.solidArea.width;
        int entityTopWorldY = entity.worldY + entity.solidArea.y;
        int entityBottomWorldY = entity.worldY + entity.solidArea.y + entity.solidArea.height;

        int entityLeftCol = entityLeftWorldX / gamePanel.tileSize;
        int entityRightCol = entityRightWorldX / gamePanel.tileSize;
        int entityTopRow = entityTopWorldY / gamePanel.tileSize;
        int entityBottomRow = entityBottomWorldY / gamePanel.tileSize;

        int tileNum1 = 0,
                tileNum2 = 0; // we only need to check 2 tiles at a time

        switch (entity.direction) {
            case "up":
                entityTopRow = (entityTopWorldY - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow]; // Gets the tile of the top left sides
                tileNum2 = gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow]; //Gets the tile of the top right sides
                break;
            case "down":
                entityBottomRow = (entityBottomWorldY + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow]; // Gets the tile of the bottom left sides
                tileNum2 = gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow]; //Gets the tile of the bottom right sides
                break;
            case "left":
                entityLeftCol = (entityLeftWorldX - entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityTopRow]; // Gets the tile of the top left sides
                tileNum2 = gamePanel.tileManager.mapTileNum[entityLeftCol][entityBottomRow]; //Gets the tile of the bottom left sides
                break;
            case "right":
                entityRightCol = (entityRightWorldX + entity.speed) / gamePanel.tileSize;
                tileNum1 = gamePanel.tileManager.mapTileNum[entityRightCol][entityTopRow]; // Gets the tile of the top right sides
                tileNum2 = gamePanel.tileManager.mapTileNum[entityRightCol][entityBottomRow]; //Gets the tile of the bottom right sides
                break;
        }

        if ((gamePanel.tileManager.tile[tileNum1].collision || gamePanel.tileManager.tile[tileNum2].collision))
            entity.collisionOn = true;
    }
}
