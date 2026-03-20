package tile;

import main.GamePanel;
import main.UtilityTool;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;

/**
 * Manages loading, storing, and rendering all tiles that make up the game world map.
 */
public class TileManager {

	GamePanel gamePanel; // Reference to the main game panel for screen and world dimensions
	public Tile[] tile;         // Array holding each unique tile type and its image
	public int[][] mapTileNum;  // 2D array representing the world map, storing a tile index per cell

	/**
	 * Constructs the TileManager, initializes the tile and map arrays, then loads tile
	 * images and the world map file.
	 *
	 * @param gamePanel the {@link GamePanel} used to access world and screen dimensions
	 */
	public TileManager( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
		tile = new Tile[10];  // Supports up to 10 unique tile types
		mapTileNum = new int[gamePanel.maxWorldCol][gamePanel.maxWorldRow]; // 50x50 world grid
		getTileImage();
		loadMap("/maps/world01.txt");
	}

	/**
	 * Loads each tile type's image from the /tiles/ resource folder and assigns it to the tile array.
	 * If any image fails to load, the stack trace is printed.
	 * TODO: Find a better way to load tiles rather than manually setting each one. Maybe a JSON
	 */
	public void getTileImage() {
		setup(0, "grass", false);
		setup(1, "wall", true);
		setup(2, "water", true);
		setup(3, "earth", false);
		setup(4, "tree", true);
		setup(5, "sand", false);
	}

	public void setup( int index, String imageName, boolean collision ) {
		UtilityTool util = new UtilityTool();

		try {
			tile[index] = new Tile();
			tile[index].image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/tiles/" + imageName + ".png")));
			tile[index].image = util.scaleImage(tile[index].image, gamePanel.tileSize, gamePanel.tileSize);
			tile[index].collision = collision;
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Reads a map text file and populates {@code mapTileNum} with tile indices for each world cell.
	 * Each line in the file represents a row, with space-separated tile index numbers per column.
	 *
	 * @param mapPath the classpath resource path to the map text file (e.g. "/maps/world01.txt")
	 */
	public void loadMap( String mapPath ) {
		try {
			InputStream is = getClass().getResourceAsStream(mapPath);
			assert is != null;
			BufferedReader br = new BufferedReader(new InputStreamReader(is));

			int col = 0;
			int row = 0;

			while ( col < gamePanel.maxWorldCol && row < gamePanel.maxWorldRow ) {
				String line = br.readLine(); // Read one row of tile indices from the map file
				String[] numbers = line.split(" ");

				while ( col < gamePanel.maxWorldCol ) {
					int num = Integer.parseInt(numbers[col]);
					mapTileNum[col][row] = num; // Store the tile index at this world cell
					col++;
				}
				if ( col == gamePanel.maxWorldCol ) { // Move to the next row once all columns in the current row are filled
					col = 0;
					row++;
				}
			}
			br.close();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}

	/**
	 * Draws only the tiles visible within the player's screen bounds, offsetting each tile's
	 * position relative to the player to simulate camera movement through the world.
	 *
	 * @param g2 the {@link Graphics2D} context used for rendering
	 */
	public void draw( Graphics2D g2 ) {
		int worldCol = 0;
		int worldRow = 0;

		while ( worldCol < gamePanel.maxWorldCol && worldRow < gamePanel.maxWorldRow ) {
			int tileNum = mapTileNum[worldCol][worldRow];

			int worldX = worldCol * gamePanel.tileSize; // Tile's X position in world pixels
			int worldY = worldRow * gamePanel.tileSize; // Tile's Y position in world pixels

			// Translate world position to screen position relative to the player
			int screenX = worldX - gamePanel.player.worldX + gamePanel.player.screenX;
			int screenY = worldY - gamePanel.player.worldY + gamePanel.player.screenY;

			// Only draw tiles within the visible screen boundary, plus a 1-tile buffer for smooth scrolling
			if ( worldX + gamePanel.tileSize > gamePanel.player.worldX - gamePanel.player.screenX
					&& worldX - gamePanel.tileSize < gamePanel.player.worldX + gamePanel.player.screenX
					&& worldY + gamePanel.tileSize > gamePanel.player.worldY - gamePanel.player.screenY
					&& worldY - gamePanel.tileSize < gamePanel.player.worldY + gamePanel.player.screenY ) {
				g2.drawImage(tile[tileNum].image, screenX, screenY, null);
			}

			// Advance to the next tile, wrapping to the next row after the last column
			worldCol++;
			if ( worldCol == gamePanel.maxWorldCol ) {
				worldCol = 0;
				worldRow++;
			}
		}
	}
}