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
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.game.rpgui;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import kinugasa.resource.Nameable;
import kinugasa.resource.TImage;
/**
 * フィールドマップを構成する1つのタイルです.
 * <br>
 * マップチップは名前、画像および属性を持ちます。
 * <br>
 * 名前には、通常はスプライトシートの切り出し位置が設定されます。<br>
 * この名前はマップデータファイルに記述するチップの配置で 使用します。<br>
 *
 * これらのマッピングはXMLファイルで行います。<br>
 * XMLからマップチップを作成するには、ChipSetStorageクラスを使用します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_23:02:47<br>
 * @author Dra0211<br>
 */
public final class MapChip implements Nameable, Serializable, Comparable<MapChip> {

	private static final long serialVersionUID = -6802142835353191416L;
	/**
	 * マップチップの一意的な名前です.
	 */
	private String name;
	/**
	 * このマップチップの画像です.
	 */
	private TImage image;
	/**
	 * このマップチップの属性です.
	 */
	private ChipAttribute attribute;

	/**
	 * 新しいマップチップを作成します.
	 *
	 * @param name 一意的な名前を指定します。 通常はChipSetおよびChipSetStorageがスプライトシートの切り出し位置をもとに 自動的に名前をつけます。この名前はマップデータファイルに記述するチップの配置で 使用します。<br>
	 * @param image このマップチップの画像を指定します。通常は スプライトシートから切り出します。<br>
	 * @param attribute このマップチップの属性を指定します。<br>
	 */
	public MapChip(String name, TImage image, ChipAttribute attribute) {
		this.name = name;
		this.image = image;
		this.attribute = attribute;
	}

	/**
	 * このチップが持つ属性を取得します.
	 *
	 * @return チップの属性を返します。<br>
	 */
	public ChipAttribute getAttribute() {
		return attribute;
	}

	/**
	 * このチップが持つ属性を設定します.
	 *
	 * @param attribute 新しい属性を指定します。<br>
	 */
	public void setAttribute(ChipAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * このチップが持つ画像を取得します.
	 *
	 * @return 画像を返します。<br>
	 */
	public TImage getSerializableImage() {
		return image;
	}

	/**
	 * このチップが持つ画像を取得します.
	 *
	 * @return 画像を返します。<br>
	 */
	public BufferedImage getImage() {
		return image.get();
	}

	/**
	 * このチップが持つ画像を設定します.
	 *
	 * @param image 画像を指定します。<br>
	 */
	public void setImage(TImage image) {
		this.image = image;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MapChip other = (MapChip) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MapChip{" + "name=" + name + ", image=" + image + ", attribute=" + attribute + '}';
	}

	@Override
	public int compareTo(MapChip o) {
		return this.name.compareTo(o.getName());
	}
}
