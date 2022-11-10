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
package kinugasa.object;

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.game.GraphicsContext;
import kinugasa.resource.Nameable;

/**
 * ゲームに表示される自機やキャラクタの基底クラスです.
 * <br>
 * スプライトのすべてのサブクラスでは、クローンを適切にオーバーライドする必要があります。<br>
 * <br>
 *
 * @version 3.0 : 12/01/20_18:30-<br>
 * @version 4.0 : 12/02/06_21:00-<br>
 * @version 5.0 : 12/02/18_18:55-21:45<br>
 * @version 6.0.0 - 2012/06/02_16:46:58.<br>
 * @version 6.14.0 - 2012/06/12_20:17.<br>
 * @version 6.18.0 - 2012/06/16_02:26.<br>
 * @version 6.20.0 - 2012/06/18_21:53.<br>
 * @version 6.23.0 - 2012/07/01_00:48.<br>
 * @version 6.28.0 - 2012/07/07_00:37.<br>
 * @version 6.40.0 - 2012/07/07_17:24.<br>
 * @version 6.40.2 - 2012/07/07_22:45.<br>
 * @version 7.0.0 - 2012/07/14_16:41:11.<br>
 * @version 7.0.1 - 2012/07/21_23:42.<br>
 * @version 7.1.1 - 2012/09/23_01:58.<br>
 * @version 7.1.3 - 2012/11/12_00:54.<br>
 * @version 7.5.0 - 2012/11/21_11:39.<br>
 * @version 8.0.0 - 2013/01/14_16:52:02<br>
 * @version 8.3.0 - 2013/02/10_02:42<br>
 * @version 8.4.0 - 2013/04/28_21:54<br>
 * @version 8.5.0 - 2015/01/05_21:14<br>
 * @version 8.6.0 - 2015/03/29_16:26<br>
 * @version 8.7.0 - 2022/11/10_19:08<br>
 * @author Dra0211<br>
 */
public abstract class Sprite
		implements Drawable, Shapeable, Cloneable, Comparable<Sprite> {

	/**
	 * 領域.
	 */
	private Rectangle2D.Float bounds;
	/**
	 * 中心座標のキャッシュ.
	 */
	private Point2D.Float center = null;
	/**
	 * 相対中心座標.
	 */
	private Point2D.Float personalCenter = null;
	/**
	 * Z軸深度.
	 */
	private float z = 0f;
	/**
	 * 可視状態.
	 */
	private boolean visible = true;
	/**
	 * 生存状態.
	 */
	private boolean exist = true;

	/**
	 * 中心座標のキャッシュを作成します.
	 */
	private void setCenter() {
		center = new Point2D.Float(bounds.x + bounds.width / 2,
				bounds.y + bounds.height / 2);
		personalCenter = new Point2D.Float(bounds.width / 2, bounds.height / 2);
	}

	/**
	 * 中心座標のキャッシュを更新します.
	 */
	protected final void updateCenter() {
		center.x = bounds.x + personalCenter.x;
		center.y = bounds.y + personalCenter.y;
	}

	/**
	 * 中心座標のキャッシュを更新します.
	 */
	protected final void updatePersonalCenter() {
		personalCenter.x = bounds.width / 2;
		personalCenter.y = bounds.height / 2;
	}

	/**
	 * 新しいスプライトを作成します. 全てのフィールドが初期化されます.<br>
	 */
	public Sprite() {
		bounds = new Rectangle2D.Float();
		setCenter();
	}

	/**
	 * 新しいスプライトを作成します. このコンストラクタでは、ディープコピーに近い、参照を利用したインスタンスの作成を行うことができます.<br>
	 *
	 * @param bounds このスプライトの領域.<br>
	 */
	private Sprite(Rectangle2D.Float bounds) {
		this.bounds = bounds;
		z = 0;
		setCenter();
	}

	/**
	 * 位置およびサイズを指定してスプライトを作成します.
	 *
	 * @param x X座標.<br>
	 * @param y Y座標.<br>
	 * @param w 幅.<br>
	 * @param h 高さ.<br>
	 */
	public Sprite(float x, float y, float w, float h) {
		this(x, y, w, h, 0);
	}

	/**
	 * 位置およびサイズを指定してスプライトを作成します.
	 *
	 * @param x X座標.<br>
	 * @param y Y座標.<br>
	 * @param w 幅.<br>
	 * @param h 高さ.<br>
	 * @param z
	 */
	public Sprite(float x, float y, float w, float h, float z) {
		this.bounds = new Rectangle2D.Float(x, y, w, h);
		this.z = z;
		setCenter();
	}

	/**
	 * スプライトを描画します. visibleまたはexistがfalseのとき、描画してはなりません.<br>
	 *
	 * @param g グラフィックスコンテキスト.<br>
	 */
	@Override
	public abstract void draw(GraphicsContext g);

	/**
	 * スプライトの様々なプロパティを更新します. このメソッドは、オーバーライドしない限り、何も行いません。<br>
	 * 移動とは別に、状態を更新する必要がある場合、オーバーライドすることで 処理を定義できます。<br>
	 */
	public void update() {
	}

	/**
	 * このスプライトの領域を取得します. このメソッドではクローンが返されます.<br>
	 *
	 * @return スプライトの領域.<br>
	 */
	public Rectangle2D.Float getBounds() {
		return (Rectangle2D.Float) bounds.clone();
	}

	/**
	 * このスプライトの”当たり判定”の領域を返します. このメソッドはスプライトが他のスプライトを衝突しているか検査する場合に
	 * スプライトを包含する矩形と論理的な衝突状態を区別するために設けられています。<br>
	 * このメソッドはデフォルトでは、getBounds()と同じ値を返します。<br>
	 * このメソッドを使用する場合は適切にオーバーライドしてください。<br>
	 *
	 * @return スプライトの”当たり判定”の領域を返します。オーバーライドしない場合はgetBounds()を返します。<br>
	 */
	public Rectangle2D.Float getHitBounds() {
		return (Rectangle2D.Float) bounds.clone();
	}

	/**
	 * このスプライトの領域を設定します.
	 *
	 * @param bounds スプライトの領域.<br>
	 */
	public void setBounds(Rectangle2D.Float bounds) {
		this.bounds = bounds;
		updatePersonalCenter();
		updateCenter();
	}

	/**
	 * このスプライトの領域を設定します.
	 *
	 * @param location 位置を指定します。<br>
	 * @param width 幅です。<br>
	 * @param height 高さです。<br>
	 */
	public void setBounds(Point2D.Float location, float width, float height) {
		setBounds(new Rectangle2D.Float(location.x, location.y, width, height));
	}

	/**
	 * このスプライトの領域を設定します.
	 *
	 * @param x X位置です。<br>
	 * @param y Y位置です。<br>
	 * @param width 幅です。<br>
	 * @param height 高さです。<br>
	 */
	public void setBounds(float x, float y, float width, float height) {
		setBounds(new Rectangle2D.Float(x, y, width, height));
	}

	//必要であれば、hitBoundsによる判定にオーバーライドできる
	@Override
	public boolean contains(Point2D point) {
		return bounds.contains(point);
	}

	/**
	 * スプライトの左上の位置を取得します. このメソッドは新しいインスタンスを返します.<br>
	 *
	 * @return 左上の位置.<br>
	 */
	public Point2D.Float getLocation() {
		return new Point2D.Float(bounds.x, bounds.y);
	}

	/**
	 * スプライトの左上の位置を設定します.
	 *
	 * @param location 左上の位置.<br>
	 */
	public void setLocation(Point2D.Float location) {
		setLocation(location.x, location.y);
	}

	/**
	 * スプライトの左上の位置を設定します.
	 *
	 * @param x X座標.<br>
	 * @param y Y座標.<br>
	 */
	public void setLocation(float x, float y) {
		setX(x);
		setY(y);
	}

	/**
	 * スプライトの中心の座標を取得します. このメソッドではクローンが返されます.<br>
	 *
	 * @return スプライトの中心の座標.ウインドウ上での絶対座標.<Br>
	 */
	public Point2D.Float getCenter() {
		return (Point2D.Float) center.clone();
	}

	/**
	 * スプライトの中心のX座標を取得します.
	 *
	 * @return スプライトの中心のX座標。ウインドウ上での座標を返します。<br>
	 */
	public float getCenterX() {
		return center.x;
	}

	/**
	 * スプライトの中心のY座標を取得します.
	 *
	 * @return スプライトの中心のY座標。ウインドウ上での座標を返します。<br>
	 */
	public float getCenterY() {
		return center.y;
	}

	/**
	 * スプライトの中心の相対的なX座標を取得します.
	 *
	 * @return スプライトの中心のX座標。スプライトのサイズに対する中心の座標を返します。<br>
	 */
	public float getPersonalCenterX() {
		return personalCenter.x;
	}

	/**
	 * スプライトの中心の相対的なY座標を取得します.
	 *
	 * @return スプライトの中心のY座標。スプライトのサイズに対する中心の座標を返します。<br>
	 */
	public float getPersonalCenterY() {
		return personalCenter.y;
	}

	/**
	 * スプライトの中心の相対座標を取得します. 相対中心座標とはスプライトの領域の左上からの中心までの距離です.<br>
	 * このメソッドではクローンが返されます.<br>
	 *
	 * @return 中心の相対座標.<br>
	 */
	public Point2D.Float getPersonalCenter() {
		return (Point2D.Float) personalCenter.clone();
	}

	/**
	 * スプライトのサイズを取得します. サイズはint精度に丸められます.<br>
	 * このメソッドは新しいインスタンスを返します.<br>
	 *
	 * @return スプライトのサイズ.<br>
	 */
	public Dimension getSize() {
		return new Dimension((int) bounds.width, (int) bounds.height);
	}

	/**
	 * スプライトのサイズを取得します. サイズはint精度に丸められます.<br>
	 *
	 * @param size スプライトのサイズ.<br>
	 */
	public void setSize(Dimension size) {
		setSize(size.width, size.height);
	}

	/**
	 * スプライトのサイズを取得します.
	 *
	 * @param w スプライトの幅.<br>
	 * @param h スプライトの高さ.<br>
	 */
	public void setSize(float w, float h) {
		bounds.width = w;
		bounds.height = h;
		updatePersonalCenter();
		updateCenter();
	}

	/**
	 * スプライトの生存状態を取得します.
	 *
	 * @return 生存中の場合はtrueを返す.<Br>
	 */
	public boolean isExist() {
		return exist;
	}

	/**
	 * スプライトの生存状態を設定します.
	 *
	 * @param exist 生存状態.<br>
	 */
	public void setExist(boolean exist) {
		this.exist = exist;
	}

	/**
	 * スプライトの可視状態を取得します.
	 *
	 * @return スプライトの可視状態.<br>
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * スプライトの可視状態を設定します.
	 *
	 * @param visible スプライトの可視状態.<br>
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * このスプライトの左上のX座標を取得します.<br>
	 *
	 * @return X座標.<br>
	 */
	public float getX() {
		return bounds.x;
	}

	/**
	 * このスプライトの左上のX座標を設定します.<br>
	 *
	 * @param x X座標.<br>
	 */
	public void setX(float x) {
		bounds.x = x;
		center.x = bounds.x + personalCenter.x;
	}

	/**
	 * このスプライトの左上のY座標を取得します.<br>
	 *
	 * @return Y座標.<br>
	 */
	public float getY() {
		return bounds.y;
	}

	/**
	 * このスプライトの左上のY座標を設定します.<br>
	 *
	 * @param y Y座標.<br>
	 */
	public void setY(float y) {
		bounds.y = y;
		center.y = bounds.y + personalCenter.y;
	}

	/**
	 * このスプライトの幅を取得します.<br>
	 *
	 * @return 幅.<br>
	 */
	public float getWidth() {
		return bounds.width;
	}

	/**
	 * このスプライトの幅を設定します.<br>
	 *
	 * @param width 幅.<br>
	 */
	public void setWidth(float width) {
		bounds.width = width;
		personalCenter.x = bounds.width / 2;
		center.x = bounds.x + personalCenter.x;
	}

	/**
	 * このスプライトの高さを取得します.<br>
	 *
	 * @return 高さ.<br>
	 */
	public float getHeight() {
		return bounds.height;
	}

	/**
	 * このスプライトの高さを設定します.<br>
	 *
	 * @param height 高さ.<br>
	 */
	public void setHeight(float height) {
		bounds.height = height;
		personalCenter.y = bounds.height / 2;
		center.y = bounds.y + personalCenter.y;
	}

	/**
	 * このスプライトのZ深度を取得します.
	 *
	 * @return 深度.<br>
	 */
	public float getZ() {
		return z;
	}

	/**
	 * このスプライトのZ深度を設定します.
	 *
	 * @param z 深度.<br>
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * このスプライトの複製を作成します. このメソッドでは、全てのフィールドをクローニングします.<br>
	 * このメソッドはサブクラスで適切にオーバーライドしてください.<br>
	 *
	 * @return このスプライトと同じ設定の新しいインスタンス.<br>
	 */
	@Override
	public Sprite clone() {
		try {
			Sprite s = (Sprite) super.clone();
			s.bounds = (Rectangle2D.Float) this.bounds.clone();
			s.center = (Point2D.Float) this.center.clone();
			s.personalCenter = (Point2D.Float) this.personalCenter.clone();
			return s;
		} catch (CloneNotSupportedException ex) {
			Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * スプライトの深度を比較し、z軸座標の昇順に並び替える機能を提供します.
	 *
	 * @param spr 比較するスプライト.<br>
	 *
	 * @return Comparableの実装に基づく値.<br>
	 */
	@Override
	public final int compareTo(Sprite spr) {
		return java.lang.Float.compare(z, spr.z);
	}

	@Override
	public String toString() {
		return "Sprite{" + "bounds=" + bounds + ", center=" + center + ", personalCenter="
				+ personalCenter + ", z=" + z + ", visible=" + visible + ", exist=" + exist + '}';
	}
}
