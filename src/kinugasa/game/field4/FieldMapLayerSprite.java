/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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
package kinugasa.game.field4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.object.BasicSprite;
import kinugasa.resource.Disposable;

/**
 * フィールドマップのメインレイヤーの役割を持ったスプライトです。 このクラスはフィールドマップのレイヤですが、単品で使うこともできます。
 * その場合、このレイヤーに使用するチップセットとその二次元配列データを用意する必要があります。データのインデックスは、[y][x]である点に注意してください。
 *
 * @vesion 1.0.0 - 2021/11/26_11:08:20<br>
 * @author Shinacho<br>
 */
public class FieldMapLayerSprite extends BasicSprite implements Disposable {

	private final MapChipSet chipSet;
	private MapChip[][] data;
	private BufferedImage fieldMapImage = null;
	private float mg;

	public FieldMapLayerSprite(MapChipSet chipSet, int w, int h, MapChip[][] data) {
		this(chipSet, w, h, 1, data);
	}

	public FieldMapLayerSprite(MapChipSet chipSet, int w, int h, float mg, MapChip[][] data) {
		super(0, 0, w, h);
		this.chipSet = chipSet;
		this.data = data;
		this.mg = mg;
		build();
	}

	private void build() {
		fieldMapImage = ImageUtil.newImage(
				(int) (data[0].length * data[0][0].getImage().getWidth() * mg),
				(int) (data.length * data[0][0].getImage().getHeight() * mg));

		Graphics2D g = ImageUtil.createGraphics2D(fieldMapImage, RenderingQuality.SPEED);
		for (int y = 0; y < data.length; y++) {
			for (int x = 0; x < data[y].length; x++) {
				BufferedImage cellImage = data[y][x].getImage().get();
				int lx = (int) (x * data[y][x].getImage().getWidth() * mg);
				int ly = (int) (y * data[y][x].getImage().getHeight() * mg);
				g.drawImage(cellImage, lx, ly, (int) (cellImage.getWidth() * mg), (int) (cellImage.getHeight() * mg), null);
			}
		}
		g.dispose();
	}

	public BufferedImage getImage() {
		return fieldMapImage;
	}

	public MapChip getChip(int x, int y) throws ArrayIndexOutOfBoundsException {
		return data[y][x];
	}

	public int getDataWidth() {
		return data[0].length;
	}

	public int getDataHeight() {
		return data.length;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		g.drawImage(fieldMapImage, (int) getX(), (int) getY());
	}

	@Override
	public void dispose() {
		data = null;
		fieldMapImage = null;
	}

	public boolean include(D2Idx idx) {
		if (idx.x < 0 || idx.y < 0) {
			return false;
		}
		return idx.x < getDataWidth() && idx.y < getDataHeight();
	}

}
