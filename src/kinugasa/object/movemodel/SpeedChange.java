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
package kinugasa.object.movemodel;

import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;

/**
 * .
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_18:29:00<br>
 * @author Shinacho<br>
 */
public class SpeedChange extends MovingModel {

	/** 一回の移動で加算する値. */
	private float addSpeed;
	/** 速度の最低値（これを含む). */
	private float minSpeed;
	/** 速度の最大値(これを含む). */
	private float maxSpeed;

	/**
	 * 移動速度が変更される移動モデルを構築します.
	 * 
	 * @param addSpeed １回の移動で加算する速度の値.負数を指定できる.<Br>
	 * @param min      最低の速度.この値を含む.<br>
	 * @param max      最大の速度.この値を含む.<br>
	 *
	 * @throws IllegalArgumentException min>maxのときに投げられる.<br>
	 */
	public SpeedChange(float addSpeed, float min, float max) throws IllegalArgumentException {
		super();
		if (min > max) {
			throw new IllegalArgumentException("! > min > max ! min=[" + min + "] max=[" + max + "]");
		}
		this.addSpeed = addSpeed;
		this.minSpeed = min;
		this.maxSpeed = max;
	}

	/**
	 * 移動速度が変更される移動モデルを構築します.
	 * 最低速度および最大速度を使用しません.(無限大を設定)<br>
	 * 
	 * @param addSpeed １回の移動で加算する速度の値.負数を指定できる.<Br>
	 */
	public SpeedChange(float addSpeed) {
		this(addSpeed, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	/**
	 * 移動の度に加算する値を取得します.
	 * 
	 * @return 加算する値.<bR>
	 */
	public float getAddSpeed() {
		return addSpeed;
	}

	/**
	 * 最大の速度を取得します.
	 * 
	 * @return 最大速度.<bR>
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * 最低の速度を取得します.
	 * 
	 * @return 最低の速度.<bR>
	 */
	public float getMinSpeed() {
		return minSpeed;
	}

	/**
	 * 移動の度に加算する値を設定します.
	 * 
	 * @param addSpeed 加算する値.<bR>
	 */
	public void setAddSpeed(float addSpeed) {
		this.addSpeed = addSpeed;
	}

	/**
	 * 最大の速度を設定します.
	 * 
	 * @param maxSpeed 最大速度.<bR>
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * 最低の速度を設定します.
	 * 
	 * @param minSpeed 最低の速度.<bR>
	 */
	public void setMinSpeed(float minSpeed) {
		this.minSpeed = minSpeed;
	}

	@Override
	public void move(BasicSprite s) {
		s.setSpeed(s.getSpeed() + addSpeed);
		if (s.getSpeed() < minSpeed) {
			s.setSpeed(minSpeed);
		}
		if (s.getSpeed() > maxSpeed) {
			s.setSpeed(maxSpeed);
		}
	}

	@Override
	public SpeedChange clone() {
		return (SpeedChange) super.clone();
	}
}
