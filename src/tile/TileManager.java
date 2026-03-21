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
		tile = new Tile[50];  // Supports up to 50 unique tile types
		mapTileNum = new int[gamePanel.maxWorldCol][gamePanel.maxWorldRow]; // 50x50 world grid
		getTileImage();
		loadMap("/maps/worldV2.txt");
	}

	/**
	 * Loads each tile type's image from the /tiles/ resource folder and assigns it to the tile array.
	 * If any image fails to load, the stack trace is printed.
	 * TODO: Find a better way to load tiles rather than manually setting each one. Maybe a JSON
	 */
	public void getTileImage() {

		// Place holder tiles 0-10
		for ( int i = 0; i < 11; i++ ) {
			setup(i, "grass00", false);
		}

		// Grass
		setup(11, "grass01", false);

		// Waters
		setup(12, "water00", true);
		setup(13, "water01", true);
		setup(14, "water02", true);
		setup(15, "water03", true);
		setup(16, "water04", true);
		setup(17, "water05", true);
		setup(18, "water06", true);
		setup(19, "water07", true);
		setup(20, "water08", true);
		setup(21, "water09", true);
		setup(22, "water10", true);
		setup(23, "water11", true);
		setup(24, "water12", true);
		setup(25, "water13", true);

		// Roads
		setup(26, "road00", false);
		setup(27, "road01", false);
		setup(28, "road02", false);
		setup(29, "road03", false);
		setup(30, "road04", false);
		setup(31, "road05", false);
		setup(32, "road06", false);
		setup(33, "road07", false);
		setup(34, "road08", false);
		setup(35, "road09", false);
		setup(36, "road10", false);
		setup(37, "road11", false);
		setup(38, "road12", false);

		// Earth
		setup(39, "earth", false);

		// Walls
		setup(40, "wall", true);

		// Trees
		setup(41, "tree", true);

		// Table
		// Sand
		// Hut
		// Floor
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