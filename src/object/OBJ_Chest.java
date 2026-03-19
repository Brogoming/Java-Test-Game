package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

/**
 * Represents a chest object in the game world that the player can interact with.
 */
public class OBJ_Chest extends SuperObject {

    /**
     * Constructs a Chest object and loads its sprite image.
     */
    public OBJ_Chest() {
        name = "Chest";
        // collision is false by default, allowing the player to walk over and interact with the chest

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/chest.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}