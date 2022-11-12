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

import java.awt.Point;
import java.awt.geom.Point2D;
import kinugasa.util.MathUtil;

/**
 * ベクトルと角度／速度をカプセル化します.
 * <br>
 *
 * <br>
 *
 * @version 4.20.0.<br>
 * @version 4.21.0-12/6/12_20:20.<br>
 * @version 4.26.0-12/7/07_17:27.<br>
 * @version 4.27.0-12/7/14_21:46.<br>
 * @version 4.27.5-12/7/19_15:51.<br>
 * @version 5.0.0 - 2013/01/14_16:23:07<br>
 * @author Dra0211<br>
 */
public class KVector implements Cloneable {

	/**
	 * 東を表す定数です.
	 */
	public static final float EAST = 90f;
	/**
	 * 南東を表す定数です.
	 */
	public static final float SOUTH_EAST = 135f;
	/**
	 * 南を表す定数です.
	 */
	public static final float SOUTH = 180f;
	/**
	 * 南西を表す定数です.
	 */
	public static final float SOUTH_WEST = 225f;
	/**
	 * 西を表す定数です.
	 */
	public static final float WEST = 270f;
	/**
	 * 北西を表す定数です.
	 */
	public static final float NORTH_WEST = 315f;
	/**
	 * 北を表す定数です.
	 */
	public static final float NORTH = 0f;
	/**
	 * 北東を表す定数です.
	 */
	public static final float NORTH_EAST = 45f;
	public float angle;
	public float speed;

	public KVector() {
		this(0, 0);
	}

	public KVector(float angle, float speed) {
		this.angle = angle;
		this.speed = speed;
	}

	public KVector(float speed) {
		this.angle = 0;
		this.speed = speed;
	}

	public KVector(Point2D.Float location) {
		this((float) (Math.toDegrees(Math.atan2(location.y, location.x))) + 90f,
				(float) Point2D.distance(0, 0, location.x, location.y));
	}

	public KVector(Point location) {
		this(new Point2D.Float(location.x, location.y));
	}

	public void clamp() {
		while (angle >= 360.0f) {
			angle -= 360.0f;
		}
		while (angle < 0.0f) {
			angle += 360.0f;
		}
	}

	public void add(KVector v) {
		setLocation(getX() + v.getX(), getY() + v.getY());
	}

	public static KVector add(KVector v1, KVector v2) {
		KVector result = new KVector();
		result.setLocation(v1.getX() + v2.getX(), v1.getY() + v2.getY());
		return result;
	}

	public void sub(KVector v) {
		setLocation(getX() - v.getX(), getY() - v.getY());
	}

	public static KVector sub(KVector v1, KVector v2) {
		KVector result = new KVector();
		result.setLocation(v1.getX() - v2.getX(), v1.getY() - v2.getY());
		return result;
	}

	public float getAngle() {
		return angle;
	}

	public float getAngleAsRad() {
		return (float) Math.toRadians(angle);
	}

	public float getSpeed() {
		return speed;
	}

	public void setAngle(float angle) {
		this.angle = angle;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setX(float x) {
		setLocation(x, getY());
	}

	public void setY(float y) {
		setLocation(getX(), y);
	}

	public float getX() {
		return speed * MathUtil.sin(angle);
	}

	//座標系が、Y軸反転だから速度を-にする
	public float getY() {
		return -speed * MathUtil.cos(angle);
	}

	//このメソッドは、純粋な位置を返す。Y軸を反転したほうがいいだろう
	public Point2D.Float getLocation() {
		return new Point2D.Float(getX(), getY());
	}

	public Point2D.Float getLocationOnScreen() {
		Point2D.Float result = getLocation();
		result.y = -result.y;
		return result;
	}

	public void setLocation(float x, float y) {
		angle = (float) (Math.toDegrees(Math.atan2(y, x))) + 90f;
		speed = (float) Point2D.distance(0, 0, x, y);
	}

	@Override
	public KVector clone() {
		try {
			return (KVector) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}

	public boolean equalsInt(KVector obj) {
		return (int) obj.getAngle() == (int) getAngle()
				&& (int) obj.getSpeed() == (int) getSpeed();
	}

	public boolean equalsValue(KVector obj) {
		return obj.getSpeed() == getSpeed()
				&& obj.getAngle() == getAngle();
	}

	/**
	 * このインスタンスの表す角度がcenterDeg+-openDeg/2の範囲内かを調べる.
	 *
	 * @param centerDeg 傾向の中央.<br>
	 * @param openDeg 分布域.<br>
	 *
	 * @return 指定した範囲内にある場合はtrueを返す.<br>
	 */
	public boolean checkRange(float centerDeg, float openDeg) {
		float ang = clamp(angle);
		float min = centerDeg - openDeg / 2;
		float max = centerDeg + openDeg / 2;
		return ang <= max && ang >= min;
	}

	/**
	 * このインスタンスの表す角度がcenterDeg+-openDeg/2の範囲内かを調べる.
	 *
	 * @param centerDeg 傾向の中央.<br>
	 * @param openDeg 分布域.<br>
	 *
	 * @return 指定した範囲内にある場合はtrueを返す.<br>
	 */
	public boolean checkRange(KVector centerDeg, float openDeg) {
		return checkRange(centerDeg.getAngle(), openDeg);
	}

	@Override
	public String toString() {
		return "VectorF{" + "angle=" + angle + ", speed=" + speed + '}';
	}

	public KVector reverse() {
		return new KVector(this.angle - 180, speed);
	}

	/**
	 * この角度のサインを取得.
	 *
	 * @return サイン.<br>
	 */
	public float sin() {
		return MathUtil.sin(clamp(angle));
	}

	/**
	 * この角度のコサインを取得.<br>
	 *
	 * @return コサイン.<br>
	 */
	public float cos() {
		return MathUtil.cos(clamp(angle));
	}

	/**
	 * この角度に角度を加算.<br>
	 *
	 * @param a 加算する値.<br>
	 */
	public void addAngle(float a) {
		angle += a;
	}

	/**
	 * この角度に角度を加算.<br>
	 *
	 * @param v 加算する角度.<br>
	 */
	public void addAngle(KVector v) {
		addAngle(v.angle);
	}

	/**
	 * この角度をwidth/2の角度だけランダムに補正する.
	 *
	 * @param width 補正角度.<br>
	 */
	public void spreadAngle(float width) {
		angle -= width / 2;
		angle += (float) Math.random() * width;
	}

	/**
	 * 方向を設定. 座標p1からp2への角度を設定します.<br>
	 *
	 * @param p1 現在位置.<br>
	 * @param p2 目的地.<br>
	 */
	public void setAngle(Point2D.Float p1, Point2D.Float p2) {
		angle = (float) Math.toDegrees((Math.atan2(p2.y - p1.y, p2.x - p1.x))
				+ Math.toRadians(90));
	}

	/**
	 * 度数法の角度を0度から359度までに整形します.
	 *
	 * @param deg 度数法の角度.<br>
	 *
	 * @return 0から359度に整形された度数法の角度.<br>
	 */
	public static float clamp(float deg) {
		while (deg >= 360.0f) {
			deg -= 360.0f;
		}
		while (deg < 0.0f) {
			deg += 360.0f;
		}
		return deg;
	}

	public FourDirection round() {
		if (checkRange(FourDirection.NORTH.getAngle(), 90)) {
			return FourDirection.NORTH;
		}
		if (checkRange(FourDirection.SOUTH.getAngle(), 90)) {
			return FourDirection.SOUTH;
		}
		if (checkRange(FourDirection.EAST.getAngle(), 90)) {
			return FourDirection.EAST;
		}
		return FourDirection.WEST;

	}

}
