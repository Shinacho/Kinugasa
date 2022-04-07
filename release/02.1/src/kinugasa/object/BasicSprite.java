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

import java.awt.Shape;
import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.movemodel.BasicMoving;
import kinugasa.object.movemodel.CompositeMove;

/**
 * 基本的な移動機能を実装した、Spriteの拡張です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_17:03:44<br>
 * @author Dra0211<br>
 */
public abstract class BasicSprite extends Sprite implements Controllable {

	private TVector vector;
	//
	/** 移動アルゴリズム. */
	private MovingModel moving;
	//

	public BasicSprite() {
		super();
		vector = new TVector();
		moving = BasicMoving.getInstance();
	}

	public BasicSprite(float x, float y, float w, float h) {
		this(x, y, w, h, new TVector());
	}

	public BasicSprite(float x, float y, float w, float h, TVector vector) {
		super(x, y, w, h);
		this.vector = vector;
		this.moving = BasicMoving.getInstance();
	}

	public BasicSprite(float x, float y, float w, float h, TVector vector, MovingModel model) {
		super(x, y, w, h);
		this.vector = vector;
		this.moving = model;
	}

	public BasicSprite(float w, float h, TVector vector, MovingModel model) {
		super(0, 0, w, h);
		this.vector = vector;
		this.moving = model;
	}

	public float getAngle() {
		return vector.angle;
	}

	public void setAngle(float angle) {
		vector.angle = angle;
	}

	public float getSpeed() {
		return vector.speed;
	}

	public void setSpeed(float speed) {
		vector.speed = speed;
	}

	/**
	 * オブジェクトに設定されているパラメータおよびアルゴリズムを使用して移動します.
	 */
	public void move() {
		moving.move(this);
		updateCenter();
	}

	/**
	 * 指定のアルゴリズムを使用して移動します.
	 * 
	 * @param m 移動方法.<br>
	 */
	public void move(MovingModel m) {
		m.move(this);
		updateCenter();
	}

	@Override
	public boolean move(float xValue, float yValue, Shape s) {
		float x = getX() + xValue * vector.speed;
		float y = getY() - yValue * vector.speed;
		if (s == null) {
			setX(x);
			setY(y);
			updateCenter();
			return true;
		}
		if (s.contains(new Point2D.Float(x + getPersonalCenterX(), y + getPersonalCenterY()))) {
			setX(x);
			setY(y);
			updateCenter();
			return true;
		}
		return false;
	}

	@Override
	public boolean move(Point2D.Float p, Shape s) {
		return move(p.x, p.y, s);
	}

	/**
	 * スプライトを描画します.
	 * visibleまたはexistがfalseのとき、描画してはなりません.<br>
	 * 
	 * @param g グラフィックスコンテキスト.<br>
	 */
	@Override
	public abstract void draw(GraphicsContext g);

	/**
	 * このスプライトが現在の設定で次に移動した時の中心の座標を返します.
	 * <br>
	 * このメソッドは、移動モデルによる移動手段を考慮しません。<br>
	 * 
	 * @return 次の中心座標.<br>
	 */
	public Point2D.Float getNextCenter() {
		Point2D.Float p = (Point2D.Float) getCenter().clone();
		p.x += vector.getX();
		p.y += vector.getY();
		return p;
	}

	/**
	 * このスプライトが現在の設定で次に移動した時の左上の座標を返します.
	 * <br>
	 * このメソッドは、移動モデルによる移動手段を考慮しません。<br>
	 * 
	 * @return 次の座標.<br>
	 */
	public Point2D.Float getNextLocation() {
		Point2D.Float p = getLocation();
		p.x += vector.getX();
		p.y += vector.getY();
		return p;
	}

	public TVector getVector() {
		return vector;
	}

	public void setVector(TVector vector) {
		this.vector = vector;
	}

	/**
	 * 移動モデルを取得します.
	 * 
	 * @return 移動モデル.<br>
	 */
	public MovingModel getMovingModel() {
		return moving;
	}

	/**
	 * このスプライトの移動イベントのうち、指定したクラスのイベントを返します.
	 * このメソッドでは、このスプライトの移動イベントがMovingEventである場合には
	 * その内部を検索して移動イベントの実装を返します。<br>MovingEventを取得するには、
	 * 引数にMovingEventのクラスを指定します。<br>
	 *
	 * @param model 検索するモデルのクラス。<br>
	 *
	 * @return 指定したクラスのイベントが含まれている場合にそのインスタンスを返す。存在しない場合はnullを返す。<br>
	 */
	public MovingModel getMovingModel(Class<? extends MovingModel> model) {
		if (moving instanceof CompositeMove) {
			CompositeMove me = (CompositeMove) moving;
			for (int i = 0; i < me.getModels().length; i++) {
				if (model.isInstance(me.getModels()[i])) {
					return me.getModels()[i];
				}
			}
			return null;
		}
		return (model.isInstance(moving)) ? moving : null;
	}

	/**
	 * 移動モデルを設定します.
	 * 
	 * @param movingModel 移動モデル.<br>
	 */
	public void setMovingModel(MovingModel movingModel) {
		this.moving = movingModel;
	}

	/**
	 * このスプライトの複製を作成します.
	 * このメソッドでは、全てのフィールドをクローニングします.<br>
	 * このメソッドはサブクラスで適切にオーバーライドしてください.<br>
	 * 
	 * @return このスプライトと同じ設定の新しいインスタンス.<br>
	 */
	@Override
	public BasicSprite clone() {
		BasicSprite s = (BasicSprite) super.clone();
		s.vector = this.vector.clone();
		s.moving = this.moving.clone();
		return s;
	}

	/**
	 * スプライトの文字列表記を取得します.
	 * 文字列にはスプライトのフィールド情報が含まれています.これらの値はすべてアクセサを通して取得可能です.<br>
	 * 
	 * @return スプライトの情報.<br>
	 */
	@Override
	public String toString() {
		return "BasicSprite location=[" + getX() + "," + getY() + "] size=["
				+ getWidth() + "," + getHeight() + "] " + "center=["
				+ getCenterX() + "," + getCenterY() + "] personalCenter=["
				+ getPersonalCenterX() + "," + getPersonalCenterY() + "] visible=["
				+ isVisible() + "] exist=[" + isExist() + "] vector=[" + getVector() + "] "
				+ "z=[" + getZ() + "]";
	}
}
