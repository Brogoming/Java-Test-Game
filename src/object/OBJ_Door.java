package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a door object in the game world that blocks entity movement until opened.
 */
public class OBJ_Door extends SuperObject {

	GamePanel gamePanel;

	/**
	 * Constructs a Door object, loading its sprite and enabling collision.
	 */
	public OBJ_Door( GamePanel gamePanel ) {
		name = "Door";
		collision = true; // Doors block entity movement by default

		try {
			image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/door.png")));
			util.scaleImage(image, gamePanel.tileSize, gamePanel.tileSize);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}