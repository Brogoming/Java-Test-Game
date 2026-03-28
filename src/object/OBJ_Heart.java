package object;

import main.GamePanel;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class OBJ_Heart extends SuperObject {
	GamePanel gamePanel;

	/**
	 * Constructs a heart object, loading its sprite and enabling collision.
	 */
	public OBJ_Heart( GamePanel gamePanel ) {
		this.gamePanel = gamePanel;
		name = "Heart";

		try {
			image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/heart_full.png")));
			image1 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/heart_half.png")));
			image2 = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/heart_blank.png")));
			image = util.scaleImage(image, gamePanel.tileSize, gamePanel.tileSize);
			image1 = util.scaleImage(image1, gamePanel.tileSize, gamePanel.tileSize);
			image2 = util.scaleImage(image2, gamePanel.tileSize, gamePanel.tileSize);
		} catch ( IOException e ) {
			e.printStackTrace();
		}
	}
}
