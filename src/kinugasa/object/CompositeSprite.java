/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
package kinugasa.object;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import kinugasa.game.GraphicsContext;

/**
 * 複数のスプライトをまとめて描画したり、ソートできるクラスです.
 * <br>
 * このクラスのことを"複合スプライト"と呼びます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:45:45<br>
 * @author Shinacho<br>
 */
public class CompositeSprite extends BasicSprite {

	/** この複合スプライトが持つスプライトのリストです. */
	private ArrayList<Sprite> sprites;

	/**
	 * 新しい複合スプライトを作成します.
	 * このコンストラクタでは、スプライトリストの初期容量は0になります。<br>
	 */
	public CompositeSprite() {
		this(Collections.<Sprite>emptyList());
	}

	/**
	 * 新しい複合スプライトを作成します.
	 *
	 * @param spr 複合スプライトに追加するスプライトを指定します。<br>
	 */
	public CompositeSprite(Sprite... spr) {
		this(Arrays.asList(spr));
	}

	/**
	 * 新しい複合スプライトを作成します.
	 *
	 * @param spr 複合スプライトに追加するスプライトを指定します。<br>
	 */
	public CompositeSprite(List<Sprite> spr) {
		this.sprites = new ArrayList<Sprite>(spr.size());
		addAll(spr.toArray(new Sprite[spr.size()]));
	}

	/**
	 * この複合スプライトに新しいスプライトを追加します.
	 * 新しいスプライトはスプライトリストの最後尾に追加されます。<br>
	 * Z軸座標は自動調整されません。<br>
	 *
	 * @param spr 追加するスプライトを指定します。<br>
	 *
	 * @throws IllegalArgumentException 追加するスプライトがthisのとき、または、追加するスプライトがCompositeSpriteで
	 * その複合スプライトが保持するスプライトリスト内にthisまたは親のスプライトがある場合に投げられます。<br>
	 * 複合スプライトの親がその複合スプライトが持つすべての子の親と循環参照になっているかのチェックは
	 * 新しいスプライトを追加するたびに再帰的に行われます。<br>
	 */
	public void add(Sprite spr) throws IllegalArgumentException {
		if (spr instanceof CompositeSprite) {
			checkInstance(Arrays.asList(this), Arrays.asList(spr));
		}
		sprites.add(spr);
	}

	/**
	 * この複合スプライトに新しいスプライトを追加します.
	 * 新しいスプライトはスプライトリストの最後尾に指定された順序で追加されます。<br>
	 * Z軸座標は自動調整されません。<br>
	 *
	 * @param spr 追加するスプライトを指定します。<br>
	 *
	 * @throws IllegalArgumentException 追加するスプライトにthisが含まれるとき、または、追加するスプライトがCompositeSpriteで
	 * その複合スプライトが保持するスプライトリスト内にthisまたは親のスプライトがある場合に投げられます。<br>
	 * 複合スプライトの親がその複合スプライトが持つすべての子の親と循環参照になっているかのチェックは
	 * 新しいスプライトを追加するたびに再帰的に行われます。<br>
	 */
	public void addAll(Sprite... spr) throws IllegalArgumentException {
		for (Sprite s : spr) {
			add(s);
		}
	}

	/**
	 * この複合スプライトに新しいスプライトを追加します.
	 * 新しいスプライトはスプライトリストの最後尾に指定された順序で追加されます。<br>
	 * Z軸座標は自動調整されません。<br>
	 *
	 * @param spr 追加するスプライトを指定します。<br>
	 *
	 * @throws IllegalArgumentException 追加するスプライトにthisが含まれるとき、または、追加するスプライトがCompositeSpriteで
	 * その複合スプライトが保持するスプライトリスト内にthisまたは親のスプライトがある場合に投げられます。<br>
	 * 複合スプライトの親がその複合スプライトが持つすべての子の親と循環参照になっているかのチェックは
	 * 新しいスプライトを追加するたびに再帰的に行われます。<br>
	 */
	public void addAll(List<Sprite> spr) throws IllegalArgumentException {
		addAll(spr.toArray(new Sprite[spr.size()]));
	}

	/**
	 * 指定したスプライトが、この複合スプライトに含まれている場合、そのスプライトをこの複合スプライトから削除します.
	 *
	 * @param spr 削除するスプライトを指定します。<br>
	 */
	public void remove(Sprite spr) {
		sprites.remove(spr);
	}

	/**
	 * 指定したスプライトが、この複合スプライトに含まれている場合、そのスプライトをこの複合スプライトから削除します.
	 *
	 * @param spr 削除するスプライトを指定します。<br>
	 */
	public void removeAll(Sprite... spr) {
		sprites.removeAll(Arrays.asList(spr));
	}

	/**
	 * 指定したスプライトが、この複合スプライトに含まれている場合、そのスプライトをこの複合スプライトから削除します.
	 *
	 * @param spr 削除するスプライトを指定します。<br>
	 */
	public void removeAll(List<Sprite> spr) {
		removeAll(spr.toArray(new Sprite[spr.size()]));
	}

	/**
	 * 指定したスプライトがこの複合スプライトに含まれているかを検査します.
	 *
	 * @param spr 検査するスプライトを指定します。<br>
	 *
	 * @return sprがこの複合スプライトのスプライトリストに含まれている場合true、そうでない場合falseを返します。<br>
	 */
	public boolean contains(Sprite spr) {
		return sprites.contains(spr);
	}
	/*
	 * TODO:追加するメソッド
	 * public boolean containsAll(Sprite... spr);
	 * public boolean containsAll(List<Sprite> spr);
	 * public boolean deepContains(Sprite spr );
	 * public boolean deepContainsAll(Sprite... spr);
	 * public boolean deepContainsAll(List<Sprite> spr);
	 */

	/**
	 * スプライトリスト内のすべてのスプライトを、その順序でZ軸座標に並べます.
	 *
	 * @param minZ リストの0番目に設定されるz座標を指定します。<br>
	 * @param maxZ リストの最後の要素に設定されるz座標を指定します。<br>
	 *
	 * @throws IllegalArgumentException minZ &gt; maxZのときに投げられます。<br>
	 */
	public void sortZ(float minZ, float maxZ) throws IllegalArgumentException {
		if (minZ > maxZ) {
			throw new IllegalArgumentException("min > max : minZ=[" + minZ + "] maxZ=[" + maxZ + "]");
		}
		float z = minZ;
		float addZ = (maxZ - minZ) / sprites.size();
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).setZ(z + addZ * i);
		}
		sort();
	}

	/**
	 * スプライトリスト内のすべてのスプライトを削除します.
	 */
	public void clear() {
		sprites.clear();
	}

	/**
	 * この複合スプライトが持つスプライトリストを取得します.
	 * リストは参照を保持します。リストに対する操作は複合スプライトに適用されます。<br>
	 *
	 * @return この複合スプライトのスプライトリストが返されます。<br>
	 */
	public List<Sprite> getSprites() {
		return sprites;
	}

	/**
	 * スプライトリストの指定したインデックスに格納されているスプライトを取得します.
	 *
	 * @param idx 取得するスプライトのインデックスを指定します.<Br>
	 *
	 * @return 指定したインデックスのスプライトが返されます.<br>
	 *
	 * @throws IndexOutOfBoundsException 不正なインデックスの場合に投げられます。<br>
	 */
	public Sprite getSprite(int idx) throws IndexOutOfBoundsException {
		return sprites.get(idx);
	}

	/**
	 * この複合スプライトが持つスプライトの数を取得します.
	 *
	 * @return スプライトリストの要素数を返します。<br>
	 */
	public int size() {
		return sprites.size();
	}

	/**
	 * 複合スプライトに追加されているスプライトの依存関係を調査します.
	 *
	 * このメソッドは再帰的に呼び出されます。<br>
	 *
	 * @param parents 検出されたすべての複合スプライトが格納されているリストです。<br>
	 * このリストにはthisを含みます。<br>
	 * @param sprites 検出されたすべてのスプライトが格納されているリストです。<br>
	 * このリストには複合スプライトも含まれています。<br>
	 *
	 * @throws IllegalArgumentException スプライトの依存関係に循環参照が発見されたときに投げられます。<br>
	 */
	private void checkInstance(List<CompositeSprite> parents, List<Sprite> sprites)
			throws IllegalArgumentException {
		Sprite spr;
		for (int i = 0, size = sprites.size(); i < size; i++) {
			spr = sprites.get(i);
			if (spr instanceof CompositeSprite) {
				CompositeSprite newParent = (CompositeSprite) spr;
				for (int j = 0, parentSize = parents.size(); j < parentSize; j++) {
					if (spr == parents.get(j)) {
						throw new IllegalArgumentException(
								"found loop reference : sprite=[" + spr + "]");
					}
				}
				List<CompositeSprite> newParents = new ArrayList<CompositeSprite>(parents);
				newParents.add(newParent);
				checkInstance(newParents, newParent.sprites);
			}
		}
	}

	/**
	 * 追加されているすべてのスプライトをその順序で描画します.
	 * このメソッドでは、この複合スプライトの可視状態と生存状態が考慮されます。<br>
	 * 各スプライトは、それぞれの可視状態と生存状態および座標が使用されます。<br>
	 *
	 * @param g 書き込むグラフィックスコンテキストを送信します。<br>
	 */
	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).draw(g);
		}
	}

	/**
	 * 追加されているすべてのスプライトをソートしてから描画します.
	 * このメソッドでは、この複合スプライトの可視状態と生存状態が考慮されます。<br>
	 * 各スプライトは、それぞれの可視状態と生存状態および座標が使用されます。<br>
	 * このメソッドでは、スプライトリスト内に複合スプライトが存在する場合は、
	 * その複合スプライトを再帰的にソートします。<br>
	 *
	 * @param g 書き込むグラフィックスコンテキストを送信します。<br>
	 * @param sortMode ソート方法を指定します。SpriteSortModeを指定できます。<br>
	 */
	public void draw(GraphicsContext g, Comparator<Sprite> sortMode) {
		sort(sortMode);
		draw(g);
	}

	/**
	 * スプライトリストをソートします.
	 * このメソッドでは、スプライトリスト内に複合スプライトが存在する場合は、
	 * その複合スプライトを再帰的にソートします。<br>
	 *
	 * @param sortMode ソート方法を指定します。SpriteSortModeを指定できます。<br>
	 */
	public void sort(Comparator<Sprite> sortMode) {
		deepSort(sprites, sortMode);
	}

	/**
	 * スプライトリストをソートします.
	 * このメソッドでは、SpriteSortModeのBACK_TO_FRONTが使用されます。<br>
	 * このメソッドでは、スプライトリスト内に複合スプライトが存在する場合は、
	 * その複合スプライトを再帰的にソートします。<br>
	 */
	public void sort() {
		sort(SpriteSortMode.BACK_TO_FRONT);
	}

	/**
	 * スプライトリストを再帰的にソートします.
	 * このメソッドでは、スプライトリスト内に複合スプライトが存在する場合は、
	 * その複合スプライトを再帰的にソートします。<br>
	 * このメソッドは再帰的に処理されます。<br>
	 *
	 * @param sprs ソートするスプライトリストを指定します。<br>
	 * @param sortMode ソート方法を指定します。SpriteSortModeを指定できます。<br>
	 */
	private void deepSort(List<Sprite> sprs, Comparator<Sprite> sortMode) {
		Sprite spr;
		for (int i = 0, size = sprs.size(); i < size; i++) {
			spr = sprs.get(i);
			if (spr instanceof CompositeSprite) {
				deepSort(((CompositeSprite) spr).sprites, sortMode);
			}
		}
		Collections.sort(sprs, sortMode);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setVisible(visible);
		}
	}

	@Override
	public void setExist(boolean exist) {
		super.setExist(exist);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setExist(exist);
		}
	}

	//この複合スプライト自体のZを設定する
	@Override
	public void setZ(float z) {
		super.setZ(z);
	}

	/**
	 * スプライトリストの要素idx番目のスプライトのZ軸座標を設定します.
	 *
	 * @param z 設定する座標を指定します。<br>
	 * @param idx Z座標を設定するスプライトのインデックスを指定します。<br>
	 *
	 * @throws IndexOutOfBoundsException 不正なインデックスの場合に投げられます。<br>
	 */
	public void setZ(float z, int idx) throws IndexOutOfBoundsException {
		sprites.get(idx).setZ(z);
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setX(x);
		}
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setY(y);
		}
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setWidth(width);
		}
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setHeight(height);
		}
	}

	@Override
	public void setLocation(Point2D.Float location) {
		super.setLocation(location);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setLocation(location);
		}
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setLocation(x, y);
		}
	}

	@Override
	public void setSize(float w, float h) {
		super.setSize(w, h);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setSize(w, h);
		}
	}

	@Override
	public void setSize(Dimension size) {
		super.setSize(size);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			sprites.get(i).setSize(size);
		}
	}

	@Override
	public void setBounds(Rectangle2D.Float bounds) {
		super.setBounds(bounds);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			sprites.get(i).setBounds(bounds);
		}
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setAngle(angle);
			}
		}
	}

	@Override
	public void setMovingModel(MovingModel movingModel) {
		super.setMovingModel(movingModel);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setMovingModel(movingModel);
			}
		}
	}

	@Override
	public void setVector(KVector vector) {
		super.setVector(vector);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setVector(vector);
			}
		}
	}

	@Override
	public void setSpeed(float speed) {
		super.setSpeed(speed);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setSpeed(speed);
			}
		}
	}

	/**
	 * スプライトの実装でフィルタリングし、サブリストを作成します.
	 * @param <T> スプライトの拡張クラスを指定します。<br>
	 * @param type 検索する型を指定します。<br>
	 * @return 指定した型の要素を新しいリストに格納して返します。順番は
	 * この複合スプライトに格納されている”発見された順番”です。<br>
	 */
	public <T extends Sprite> List<Sprite> subList(Class<T> type) {
		List<Sprite> result = new ArrayList<Sprite>();
		for (int i = 0, size = size(); i < size; i++) {
			Sprite sprite = getSprite(i);
			if (type.equals(sprite.getClass())) {
				result.add(getSprite(i));
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CompositeSprite clone() {
		CompositeSprite sprite = (CompositeSprite) super.clone();
		sprite.sprites = (ArrayList<Sprite>) this.sprites.clone();
		return sprite;
	}

	@Override
	public String toString() {
		return "CompositeSprite{" + "sprites=" + sprites + '}';
	}
}
