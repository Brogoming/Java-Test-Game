package main;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Provides shared image processing utilities used across the game for asset preparation.
 */
public class UtilityTool {

	/**
	 * Returns a new image scaled to the given dimensions using a Graphics2D draw pass.
	 *
	 * @param original the source image to scale
	 * @param width    the desired width of the output image in pixels
	 * @param height   the desired height of the output image in pixels
	 * @return a new {@link BufferedImage} rendered at the specified dimensions
	 */
	public BufferedImage scaleImage( BufferedImage original, int width, int height ) {
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2 = scaledImage.createGraphics();
		g2.drawImage(original, 0, 0, width, height, null);
		g2.dispose(); // Release graphics resources immediately — this context is not reused
		return scaledImage;
	}
}