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
package coderats.ar.gl;

import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Hashtable;

public class ImageUtils {
	
	public static final int[] pixelOffsets_BGR;
	public static final int[] pixelOffsets_BGRA;
	public static final int[] pixelOffsets_GRAY;
	
	public static final int[] bpp_8;
	public static final int[] bpp_24;
	public static final int[] bpp_32;
	
	public static final ColorSpace colorSpace_sRGB;
	public static final ColorSpace colorSpace_GRAY;
	
	public static final ComponentColorModel colorModel_Alpha32BPP;
	public static final ComponentColorModel colorModel_24BPP;
	public static final ComponentColorModel colorModel_8BPP;
	
	static {
		pixelOffsets_BGR = new int[] { 2, 1, 0 };
		pixelOffsets_BGRA = new int[] { 2, 1, 0, 3 };
		pixelOffsets_GRAY = new int[] { 0 };
		
		bpp_8 = new int[] { 8 };
		bpp_24 = new int[] { 8, 8, 8 };
		bpp_32 = new int[] { 8, 8, 8, 8 };
		
		colorSpace_sRGB = ColorSpace.getInstance(ColorSpace.CS_sRGB);
		colorSpace_GRAY = ColorSpace.getInstance(ColorSpace.CS_GRAY);

		colorModel_Alpha32BPP = createComponentColorModel(
				colorSpace_sRGB,
				bpp_32,
				true,
				DataBuffer.TYPE_BYTE
				);
		
		colorModel_24BPP = createComponentColorModel(
				colorSpace_sRGB,
				bpp_24,
				false,
				DataBuffer.TYPE_BYTE
				);
		
		colorModel_8BPP = createComponentColorModel(
				colorSpace_GRAY,
				bpp_8,
				false,
				DataBuffer.TYPE_BYTE
				);
	}
	
	/*
	 * common functions
	 */
	
	public static ComponentColorModel createComponentColorModel(ColorSpace colosSpace, int[] bits, boolean useAlpha, int dataType) {
		return new ComponentColorModel(
				colosSpace,
				bits,
				useAlpha,
				useAlpha,
				useAlpha ? ComponentColorModel.TRANSLUCENT : ComponentColorModel.OPAQUE,
				dataType
				);
	}

	public static WritableRaster createInterleavedRaster(int width, int height, int bytesPerPixel, int[] pixelOffsets) {

		return Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE,
				width,
				height,
				width * bytesPerPixel,
				bytesPerPixel,
				pixelOffsets,
				null
				);
	}

	public static BufferedImage createBufferedImage(ColorModel colorModel, WritableRaster writableRaster) {
		return new BufferedImage(
				colorModel,
				writableRaster,
				false,
				new Hashtable<Object, Object>()
				);
	}

}
