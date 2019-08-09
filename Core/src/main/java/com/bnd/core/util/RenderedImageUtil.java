package com.bnd.core.util;

import java.awt.image.RenderedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class RenderedImageUtil {

	/**
	 * Stores image to the file.
	 */
	public static void saveImage(RenderedImage image, String fileName, String format) {
		File outputFile = new File(fileName);
		try {
			ImageIO.write(image, format, outputFile);
		} catch (IOException e) {
			throw new BndFileException("I/O problem ocurred on the attemp to write image to file " + fileName);
		} 		
	}

	public static String getImageAsString(RenderedImage image, String format) {
		final byte[] imageBytes = getImageAsBytes(image, format);
		return new String(imageBytes);
	}

	public static byte[] getImageAsBytes(RenderedImage image, String format) {
		byte[] imageInByte = null;
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			ImageIO.write(image, format, baos);
			baos.flush();
			imageInByte = baos.toByteArray();
			baos.close();
		} catch (IOException e) {
			throw new BndFileException("I/O problem ocurred on the attemp to write image to file.");
		}
		return imageInByte;
	}
}