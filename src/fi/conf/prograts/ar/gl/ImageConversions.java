/* [LGPL] Copyright 2010, 2011 Gima

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/
package fi.conf.prograts.ar.gl;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBufferByte;
import java.awt.image.WritableRaster;


public class ImageConversions {
	
	/*
	 * BufferedImage -> convert -> byte[]
	 */

	public static byte[] convertBIToBytes(BufferedImage srcImage, ComponentColorModel colorModel, WritableRaster writableRaster) {
		BufferedImage convertedImage;

		convertedImage = ImageUtils.createBufferedImage(colorModel, writableRaster);

		Graphics2D convertedImageGraphics = convertedImage.createGraphics();
		convertedImageGraphics.drawImage(srcImage, null, null);
		convertedImageGraphics.dispose();

		return ((DataBufferByte) convertedImage.getRaster().getDataBuffer()).getData();
	}
	
	public static byte[] convertBIToBGRABytes(BufferedImage srcImage) {
		WritableRaster writableRaster = ImageUtils.createInterleavedRaster(
				srcImage.getWidth(),
				srcImage.getHeight(),
				4,
				ImageUtils.pixelOffsets_BGRA
				);

		return convertBIToBytes(srcImage, ImageUtils.colorModel_Alpha32BPP, writableRaster);
	}
	
	public static byte[] convertBIToBGRBytes(BufferedImage srcImage) {
		
		WritableRaster writableRaster = ImageUtils.createInterleavedRaster(
				srcImage.getWidth(),
				srcImage.getHeight(),
				3,
				ImageUtils.pixelOffsets_BGR
				);

		return convertBIToBytes(srcImage, ImageUtils.colorModel_24BPP, writableRaster);
	}
	
	public static byte[] convertBIToGrayBytes(BufferedImage srcImage) {
		
		WritableRaster writableRaster = ImageUtils.createInterleavedRaster(
				srcImage.getWidth(),
				srcImage.getHeight(),
				1,
				ImageUtils.pixelOffsets_GRAY
				);
		
		return convertBIToBytes(srcImage, ImageUtils.colorModel_8BPP, writableRaster);
	}
	
	/*
	 * byte[] -> BufferedImage
	 */
	
	public static BufferedImage convertGrayBytesToBI(byte[] srcBytes, int width, int height) {
		
		BufferedImage bufferedImage = ImageUtils.createBufferedImage(
				ImageUtils.colorModel_8BPP,
				ImageUtils.createInterleavedRaster(width, height, 1, ImageUtils.pixelOffsets_GRAY)
				);
		
		byte[] biData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
		
		for (int i=0; i<width*height; i++) {
			biData[i] = srcBytes[i];
		}
		
		/*for (int y=0; y<height; y++)
			for (int x=0; x<width; x++) {
				int byteIdx = y * width + x;
				biData[byteIdx] = srcBytes[byteIdx];
			}*/
		
		return bufferedImage;
	}
	
	public static BufferedImage convertBGRABytesToBI(byte[] srcBytes, int width, int height) {
		
		BufferedImage bufferedImage = ImageUtils.createBufferedImage(
				ImageUtils.colorModel_Alpha32BPP,
				ImageUtils.createInterleavedRaster(width, height, 4, ImageUtils.pixelOffsets_BGRA)
				);
		
		byte[] biData = ((DataBufferByte) bufferedImage.getRaster().getDataBuffer()).getData();
		
		for (int i=0; i<width*height*4; i++) {
			biData[i] = srcBytes[i];
		}
		
		return bufferedImage;
	}
	
}
