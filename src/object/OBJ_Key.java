package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a key object in the game world that the player can pick up to unlock doors.
 */
public class OBJ_Key extends SuperObject {

	GamePanel gamePanel;

	/**
	 * Constructs a Key object and loads its sprite image.
	 */
	public OBJ_Key( GamePanel gamePanel ) {
		name = "Key";
		// collision is false by default, allowing the player to walk over and pick up the key

		try {
			image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/key.png")));
			util.scaleImage(image, gamePanel.tileSize, gamePanel.tileSize);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}