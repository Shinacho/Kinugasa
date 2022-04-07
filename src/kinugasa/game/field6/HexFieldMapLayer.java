/*
 * The MIT License
 *
 * Copyright 2021 hiroki.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.game.field6;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field.MapChip;
import kinugasa.game.field.MapChipSet;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.object.BasicSprite;
import kinugasa.object.Statable;

/**
 *
 * @author hiroki
 */
public class HexFieldMapLayer extends BasicSprite{

	private MapChipSet chipSet;
	private MapChip[][] data;
	private BufferedImage image;

	public HexFieldMapLayer(int w, int h, MapChipSet chipSet, List<String[]> data) {
		super(0, 0, w, h);
		this.chipSet = chipSet;
		for( int y = 0 ; y < data.size(); y ++ ){
			
		}
	}
	
	public void load(){
		image = ImageUtil.newImage((int) getWidth(), (int) getHeight());
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.QUALITY);

		int x = 0, y = 0;

		g.dispose();
		
	}

	@Override
	public void draw(GraphicsContext g) {

	}

}
