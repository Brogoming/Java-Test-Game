package object;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.util.Objects;

public class OBJ_Boots extends SuperObject{
    /**
     * Constructs a Boots object, loading its sprite.
     */
    public OBJ_Boots() {
        name = "Boots";

        try {
            image = ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/objects/boots.png")));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
