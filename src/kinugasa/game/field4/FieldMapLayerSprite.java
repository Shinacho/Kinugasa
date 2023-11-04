/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
	
	public boolean allIs(MapChip c){
		boolean res = true;
		for(MapChip[] l : data){
			for(MapChip m : l ){
				res &= c.equals(m);
			}
		}
		return res;
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
