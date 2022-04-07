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
 * @author Dra0211<br>
 */
public class SpeedChange extends MovingModel {

	/** ���̈ړ��ŉ��Z����l. */
	private float addSpeed;
	/** ���x�̍Œ�l�i������܂�). */
	private float minSpeed;
	/** ���x�̍ő�l(������܂�). */
	private float maxSpeed;

	/**
	 * �ړ����x���ύX�����ړ����f�����\�z���܂�.
	 * 
	 * @param addSpeed �P��̈ړ��ŉ��Z���鑬�x�̒l.�������w��ł���.<Br>
	 * @param min      �Œ�̑��x.���̒l���܂�.<br>
	 * @param max      �ő�̑��x.���̒l���܂�.<br>
	 *
	 * @throws IllegalArgumentException min>max�̂Ƃ��ɓ�������.<br>
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
	 * �ړ����x���ύX�����ړ����f�����\�z���܂�.
	 * �Œᑬ�x����эő呬�x���g�p���܂���.(�������ݒ�)<br>
	 * 
	 * @param addSpeed �P��̈ړ��ŉ��Z���鑬�x�̒l.�������w��ł���.<Br>
	 */
	public SpeedChange(float addSpeed) {
		this(addSpeed, Float.NEGATIVE_INFINITY, Float.POSITIVE_INFINITY);
	}

	/**
	 * �ړ��̓x�ɉ��Z����l���擾���܂�.
	 * 
	 * @return ���Z����l.<bR>
	 */
	public float getAddSpeed() {
		return addSpeed;
	}

	/**
	 * �ő�̑��x���擾���܂�.
	 * 
	 * @return �ő呬�x.<bR>
	 */
	public float getMaxSpeed() {
		return maxSpeed;
	}

	/**
	 * �Œ�̑��x���擾���܂�.
	 * 
	 * @return �Œ�̑��x.<bR>
	 */
	public float getMinSpeed() {
		return minSpeed;
	}

	/**
	 * �ړ��̓x�ɉ��Z����l��ݒ肵�܂�.
	 * 
	 * @param addSpeed ���Z����l.<bR>
	 */
	public void setAddSpeed(float addSpeed) {
		this.addSpeed = addSpeed;
	}

	/**
	 * �ő�̑��x��ݒ肵�܂�.
	 * 
	 * @param maxSpeed �ő呬�x.<bR>
	 */
	public void setMaxSpeed(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/**
	 * �Œ�̑��x��ݒ肵�܂�.
	 * 
	 * @param minSpeed �Œ�̑��x.<bR>
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
