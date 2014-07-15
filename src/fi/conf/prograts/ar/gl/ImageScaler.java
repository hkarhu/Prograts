/* [LGPL] Copyright 2011 Irah, Gima

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
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;

public class ImageScaler {
	
	public static BufferedImage scaleImage(BufferedImage sourceImage, int targetWidth, int targetHeight, ScaleParams scaleParameters, QualityParams qualityParameters) {
		
		if(sourceImage.getWidth() < targetWidth && sourceImage.getHeight() < targetHeight) return sourceImage;
		
		boolean fill = false;
		
		BufferedImage targetImage = new BufferedImage(targetWidth, targetHeight, sourceImage.getType());
		
		Graphics2D graphics2D = targetImage.createGraphics();
		
		switch (qualityParameters) {
			case FAST:
				graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR);
				graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				break;
				
			case GOOD:
				graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
				graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_SPEED);
				break;
				
			case BEST:
				graphics2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
				graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				break;
			
			case DEFAULT:
			default:
				graphics2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_DEFAULT);
			
		}

		switch (scaleParameters) {
			case STRETCH:
				graphics2D.drawImage(sourceImage, 0, 0, targetWidth, targetHeight, null);
				break;
	
			case CROP:
				fill = true;
			
			case FIT:
				
				if((sourceImage.getWidth() > sourceImage.getHeight())^!fill){
					float nw = ((float)targetHeight/(float)sourceImage.getHeight())*sourceImage.getWidth();
					graphics2D.drawImage(sourceImage, (int)(-0.5f*(nw-targetWidth)), 0, (int) nw, targetHeight, null);
				} else {
					float nh = ((float)targetWidth/(float)sourceImage.getWidth())*sourceImage.getHeight();
					graphics2D.drawImage(sourceImage, 0, (int)(-0.5f*(nh-targetHeight)), targetWidth, (int) nh,	null);			
				}
				break;
		}
		
		graphics2D.dispose();
		return targetImage;
	}
	
	public static enum QualityParams {
		DEFAULT, FAST, GOOD, BEST
	}
	
	public static enum ScaleParams {
		STRETCH, FIT, CROP
	}
	
}
