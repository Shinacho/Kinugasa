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

import java.io.Serializable;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.TImage;

/**
 * フィールドマップの1つのレイヤが使用する、マップチップのセットです.
 * <br>
 * チップセットは1つのXMLファイルに定義され、ChipSetStorageクラスからロードされます。<br>
 * 作成されたチップセットは、同じ名前がなければChipSetStorageクラスに自動追加されます。<br>
 * 同じ名前のチップセットが登録されている場合、例外を投げます。<Br>
 * <br>
 * チップセットには名前"VOID"のチップが自動追加されます。このチップは空の画像（透明）を持ち、 属性は"VOID"です。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_23:26:33<br>
 * @author Dra0211<br>
 */
public class ChipSet extends Storage<MapChip> implements Nameable, Serializable, Comparable<ChipSet> {

	private static final long serialVersionUID = 7240169495094793450L;
	/**
	 * このチップセットの名前です.
	 */
	private String name;
	/**
	 * チップセット画像の切り出しサイズの幅です.
	 */
	private int cutWidth;
	/**
	 * チップセット画像の切り出しサイズの高さです.
	 */
	private int cutHeight;

	/**
	 * 新しいチップセットを作成します. 作成されたチップセットは自動的にChipSetStorageに追加されます。<br>
	 *
	 * @param name チップセットの名前を指定します。<br>
	 * @param cutWidth チップセット画像の切り出しサイズの幅です。<br>
	 * @param cutHeight チップセット画像の切り出しサイズの高さです。<br>
	 * @throws DuplicateNameException 名前がすでに使用されているときに投げられます。<br>
	 */
	public ChipSet(String name, int cutWidth, int cutHeight) throws DuplicateNameException {
		this.name = name;
		this.cutWidth = cutWidth;
		this.cutHeight = cutHeight;
		addThis();
	}

	/**
	 * ChipSetStorageにthisインスタンスを追加します.
	 *
	 * @throws DuplicateNameException 名前がすでに使用されているときに投げられます。<br>
	 */
	private void addThis() throws DuplicateNameException {
		ChipSetStorage.getInstance().add(this);
		put(new MapChip("VOID", new TImage(ImageUtil.newImage(cutWidth, cutHeight)),
				ChipAttributeStorage.getInstance().get("VOID")));
	}

	@Override
	public String getName() {
		return name;
	}

	public int getCutWidth() {
		return cutWidth;
	}

	public int getCutHeight() {
		return cutHeight;
	}

	@Override
	public int compareTo(ChipSet o) {
		return this.name.compareTo(o.getName());
	}
}
