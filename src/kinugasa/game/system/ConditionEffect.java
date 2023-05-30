/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
package kinugasa.game.system;

import java.util.Objects;
import kinugasa.util.*;
import kinugasa.util.Random;
import kinugasa.resource.Nameable;
import kinugasa.resource.db.DBRecord;

/**
 * このクラスはマスタデータのため、設定されるエフェクトのONECEフラグはステータスが持つ必要があります。
 *
 * @vesion 1.0.0 - 2022/11/15_11:59:41<br>
 * @author Shinacho<br>
 */
@DBRecord
public class ConditionEffect implements Nameable {

	private String id;
	private EffectContinueType continueType;
	private EffectTargetType targetType;
	private EffectSetType setType;
	private String targetName;
	private float value;
	private float p;
	private int time;

	public ConditionEffect(String id, EffectContinueType continueType, EffectTargetType targetType, EffectSetType setType, String targetName, float value, float p, int time) {
		this.id = id;
		this.continueType = continueType;
		this.targetType = targetType;
		this.setType = setType;
		this.targetName = targetName;
		this.value = value;
		this.p = p;
		this.time = time;
	}

	// エフェクトが複数ある場合で、どれもが同じタイムで稼働する場合、最初の1回だけ実行する必要がある
	public TimeCounter createTimeCounter() {
		if (time == 0) {
			throw new GameSystemException("effect time is 0 : " + this);
		}

		if (time == 1) {
			return TimeCounter.oneCounter();
		}

		if (time == 9999) {
			return TimeCounter.FALSE;
		}
		int val = Random.randomAbsInt((int) (time * 0.5), (int) (time * 2));
		return new FrameTimeCounter(val);
	}

	public void exec(Status s) {
		//この状態異常効果をSに発生させる。対象はADD＿CONDITION、ATTRIN、STATUS。
		switch (targetType) {
			case ADD_CONDITION:
				s.addCondition(targetName);
				break;
			case ATTR:
				switch (setType) {
					case ADD_PERCENT_OF_MAX:
						float val = s.getBaseAttrIn().get(targetName).getMax() * value;
						s.getBaseAttrIn().get(targetName).set(value);
						break;
					case ADD_VALUE:
						s.getBaseAttrIn().get(targetName).add(value);
						break;
					case TO:
						s.getBaseAttrIn().get(targetName).set(value);
						break;
				}
			case STATUS:
				switch (setType) {
					case ADD_PERCENT_OF_MAX:
						float val = s.getBaseStatus().get(targetName).getMax() * value;
						s.getBaseStatus().get(targetName).set(val);
						break;
					case ADD_VALUE:
						s.getBaseStatus().get(targetName).add(value);
						break;
					case TO:
						s.getBaseStatus().get(targetName).set(value);
						break;
				}
			case CONFU:
			case STOP:
				//CONFUとSTOPはStatusからやるので、操作なし
				break;
			default:
				throw new AssertionError();
		}
	}

	@Override
	public String getName() {
		return id;
	}

	public EffectContinueType getContinueType() {
		return continueType;
	}

	public void setContinueType(EffectContinueType continueType) {
		this.continueType = continueType;
	}

	public EffectTargetType getTargetType() {
		return targetType;
	}

	public void setTargetType(EffectTargetType targetType) {
		this.targetType = targetType;
	}

	public EffectSetType getSetType() {
		return setType;
	}

	public void setSetType(EffectSetType setType) {
		this.setType = setType;
	}

	public String getTargetName() {
		return targetName;
	}

	public void setTargetName(String targetName) {
		this.targetName = targetName;
	}

	public float getValue() {
		return value;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public float getP() {
		return p;
	}

	public void setP(float p) {
		this.p = p;
	}

	public int getTime() {
		return time;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 53 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ConditionEffect other = (ConditionEffect) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString() {
		return "ConditionEffect{" + "id=" + id + ", continueType=" + continueType + ", targetType=" + targetType + ", setType=" + setType + ", targetName=" + targetName + ", value=" + value + ", p=" + p + ", time=" + time + '}';
	}

}
