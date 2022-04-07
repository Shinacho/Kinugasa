/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
 * FITNESS FOR MapLayerSprite PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.game.rpgui;

import java.awt.image.BufferedImage;
import java.util.Arrays;
import kinugasa.graphics.Animation;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Disposable;
import kinugasa.util.TimeCounter;

/**
 * map属性に対応し、フィールドマップとして表示されるアニメーションを 管理するスプライトです.
 * <br>
 * このスプライトの位置情報は変更しないでください。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/05/02_22:41:06<br>
 * @author Dra0211<br>
 */
public class MapLayerSprite extends AnimationSprite implements Disposable {

	private static final long serialVersionUID = -5233491178202270274L;
	private MapData[] mapData;

	public MapLayerSprite(float x, float y, float w, float h,
			TimeCounter tc, MapData... mapData) {
		super(x, y, w, h);
		this.mapData = mapData;
		BufferedImage[] images = new BufferedImage[mapData.length];
		for (int i = 0; i < mapData.length; i++) {
			images[i] = mapData[i].getImage();
		}
		setAnimation(new Animation(tc, images));
	}

	@Override
	public void dispose() {
		for (MapData data : mapData) {
			data.dispose();
		}
		setAnimation(null);
	}

	public MapData[] getMapData() {
		return mapData;
	}

	public MapData getCurrentMapData() {
		return mapData[getAnimation().getIndex().getIndex()];
	}

	public int mapNum() {
		return mapData.length;
	}

	public MapData getMapData(int index)
			throws ArrayIndexOutOfBoundsException {
		return mapData[index];
	}

	public MapChip[] getChip(int x, int y) throws ArrayIndexOutOfBoundsException {
		MapChip[] chips = new MapChip[mapData.length];
		for (int i = 0, size = mapNum(); i < size; i++) {
			chips[i] = mapData[i].getChips()[y][x];
		}
		return chips;
	}

	public ChipAttribute[] getAttribute(int x, int y) throws ArrayIndexOutOfBoundsException {
		ChipAttribute[] attributes = new ChipAttribute[mapData.length];
		for (int i = 0, size = mapNum(); i < size; i++) {
			attributes[i] = mapData[i].getChips()[y][x].getAttribute();
		}
		return attributes;
	}

	public boolean stepOn(int x, int y) {
		return stepOn(VehicleStorage.getInstance().getCurrentVehicle(), x, y);
	}

	public boolean stepOn(Vehicle vehicle, int x, int y) {
		if (vehicle == null) {
			return true;
		}
		for (ChipAttribute attribute : getAttribute(x, y)) {
			if (!vehicle.stepOn(attribute)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "FieldMapDataLayer{" + "mapData=" + Arrays.toString(mapData) + '}';
	}
}
