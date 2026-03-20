package main;

import java.awt.*;
import java.awt.image.BufferedImage;

/**
 * Utility tool class for all general utility functions
 */
public class UtilityTool {

	/**
	 * Scales any image passed in
	 *
	 * @param original
	 * @param width
	 * @param height
	 * @return
	 */
	public BufferedImage scaleImage( BufferedImage original, int width, int height ) {
		BufferedImage scaledImage = new BufferedImage(width, height, original.getType());
		Graphics2D g2 = scaledImage.createGraphics();
		g2.drawImage(original, 0, 0, width, height, null);
		g2.dispose();
		return scaledImage;
	}
}
