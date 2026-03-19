package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a key object in the game world that the player can pick up to unlock doors.
 */
public class OBJ_Key extends SuperObject {

    /**
     * Constructs a Key object and loads its sprite image.
     */
    public OBJ_Key() {
        name = "Key";
        // collision is false by default, allowing the player to walk over and pick up the key

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/key.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}