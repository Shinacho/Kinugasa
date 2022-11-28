/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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

import kinugasa.util.*;
import kinugasa.util.Random;
import kinugasa.resource.Nameable;

/**
 * このクラスはマスタデータのため、設定されるエフェクトのONECEフラグはステータスが持つ必要があります。
 *
 * @vesion 1.0.0 - 2022/11/15_11:59:41<br>
 * @author Dra211<br>
 */
public class EffectMaster implements Nameable {

	private ConditionKey key;
	private EffectContinueType continueType;
	private EffectTargetType targetType;
	private EffectSetType setType;
	private String targetName;
	private float value;
	private float p;
	private int minTime, maxTime;

	public EffectMaster(ConditionKey key) {
		this(key, EffectContinueType.NONE, null, null, null, 0, 0, 0, 0);
	}

	public EffectMaster(ConditionKey key, EffectContinueType continueType, EffectTargetType targetType, float p, int min, int max) {
		this(key, continueType, targetType, null, null, 0, p, min, max);
	}

	public EffectMaster(ConditionKey key, EffectContinueType continueType, EffectTargetType targetType, int min, int max, float p) {
		this(key, continueType, targetType, null, null, 0, p, min, max);
	}

	public EffectMaster(ConditionKey key, EffectContinueType continueType, EffectTargetType targetType, String targetName, float p) {
		this(key, continueType, targetType, null, targetName, 0, p, 0, 0);
	}

	public EffectMaster(ConditionKey key, EffectContinueType continueType, EffectTargetType targetType, EffectSetType setType, String targetName, float value, float p, int minTime, int maxTime) {
		this.key = key;
		this.continueType = continueType;
		this.targetType = targetType;
		this.setType = setType;
		this.targetName = targetName;
		this.value = value;
		this.p = p;
		this.minTime = minTime;
		this.maxTime = maxTime;
	}

	// エフェクトが複数ある場合で、どれもが同じタイムで稼働する場合、最初の1回だけ実行する必要がある
	public TimeCounter createTimeCounter() {
		if (minTime == 0 || maxTime == 0) {
			throw new GameSystemException("effect time is 0 : " + this);
		}
		if (minTime == 1 && maxTime == 1) {
			return TimeCounter.oneCounter();
		}
		if (minTime >= Integer.MAX_VALUE || maxTime >= Integer.MAX_VALUE) {
			return TimeCounter.FALSE;
		}
		if (minTime == maxTime) {
			return new FrameTimeCounter(minTime);
		}
		int val = Random.randomAbsInt(minTime, maxTime);
		return new FrameTimeCounter(val);
	}

	@Override
	public String getName() {
		return key.getName();
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

	public ConditionKey getKey() {
		return key;
	}

	public int getMaxTime() {
		return maxTime;
	}

	public int getMinTime() {
		return minTime;
	}

	@Override
	public String toString() {
		return "EffectMaster{" + "key=" + key + ", value=" + value + ", minTime=" + minTime + ", maxTime=" + maxTime + '}';
	}

}
