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
 * @version 1.0.0 - 2013/01/14_18:38:13<br>
 * @author Shinacho<br>
 */
public class AngleChange extends MovingModel {

	/** 一回の移動で加算する値. */
	private float addDir;
	/** 最初の角度. */
	private float startDir = Float.NaN;
	/** 角度の変更可能な値. */
	private float spread = Float.POSITIVE_INFINITY;
	/** 変更された角度の累計. */
	private float angSum = 0f;

	/**
	 * 新しいイベントを構築します.
	 * このコンストラクタでは、spreadは正の無限大になります.したがって、無制限に角度変更イベントが発生します.<br>
	 * 
	 * @param add 移動の度に追加する度数法の角度の値.負数を指定できる.<br>
	 */
	public AngleChange(float add) {
		this.addDir = add;
	}

	/**
	 * 新しいイベントを構築します.
	 * 
	 * @param add    移動の度に追加する度数法の角度の値.負数を指定できる.<br>
	 * @param spread 角度の変更可能な値.累計の変更された角度の差がこの値を超えると、それ以上変更が行われなくなる.<br>
	 */
	public AngleChange(float add, float spread) {
		super();
		this.addDir = add;
		this.spread = spread;
	}

	/**
	 * 移動の度に追加する値を取得します.
	 * 
	 * @return 角度に追加する値.<bR>
	 */
	public float getAddDir() {
		return addDir;
	}

	/**
	 * 移動の度に追加する値を設定します.
	 * 
	 * @param addDir 角度に追加する値.<bR>
	 */
	public void setAddDir(float addDir) {
		this.addDir = addDir;
	}

	/**
	 * 変更可能な角度の値を取得します.
	 * 
	 * @return 変更可能な角度の上限.<br>
	 */
	public float getSpread() {
		return spread;
	}

	/**
	 * 変更可能な角度の値を設定します.
	 * 
	 * @param spread 変更可能な角度の上限.<br>
	 */
	public void setSpread(float spread) {
		this.spread = spread;
	}

	/**
	 * イベント対象オブジェクトの最初に設定されてた角度を取得します.
	 * 
	 * @return イベント対象オブジェクトに最初に設定されていた角度の度数法表記.<br>
	 */
	public float getStartDir() {
		return startDir;
	}

	@Override
	public void move(BasicSprite s) {
		if (startDir == Float.NaN) {
			startDir = s.getVector().getAngle();
		}
		if (angSum < spread) {
			s.getVector().addAngle(addDir);
			angSum += Math.abs(addDir);
		}
	}

	@Override
	public AngleChange clone() {
		return (AngleChange) super.clone();
	}
}
