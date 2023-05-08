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
