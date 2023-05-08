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

	/** ���̈ړ��ŉ��Z����l. */
	private float addDir;
	/** �ŏ��̊p�x. */
	private float startDir = Float.NaN;
	/** �p�x�̕ύX�\�Ȓl. */
	private float spread = Float.POSITIVE_INFINITY;
	/** �ύX���ꂽ�p�x�̗݌v. */
	private float angSum = 0f;

	/**
	 * �V�����C�x���g���\�z���܂�.
	 * ���̃R���X�g���N�^�ł́Aspread�͐��̖�����ɂȂ�܂�.���������āA�������Ɋp�x�ύX�C�x���g���������܂�.<br>
	 * 
	 * @param add �ړ��̓x�ɒǉ�����x���@�̊p�x�̒l.�������w��ł���.<br>
	 */
	public AngleChange(float add) {
		this.addDir = add;
	}

	/**
	 * �V�����C�x���g���\�z���܂�.
	 * 
	 * @param add    �ړ��̓x�ɒǉ�����x���@�̊p�x�̒l.�������w��ł���.<br>
	 * @param spread �p�x�̕ύX�\�Ȓl.�݌v�̕ύX���ꂽ�p�x�̍������̒l�𒴂���ƁA����ȏ�ύX���s���Ȃ��Ȃ�.<br>
	 */
	public AngleChange(float add, float spread) {
		super();
		this.addDir = add;
		this.spread = spread;
	}

	/**
	 * �ړ��̓x�ɒǉ�����l���擾���܂�.
	 * 
	 * @return �p�x�ɒǉ�����l.<bR>
	 */
	public float getAddDir() {
		return addDir;
	}

	/**
	 * �ړ��̓x�ɒǉ�����l��ݒ肵�܂�.
	 * 
	 * @param addDir �p�x�ɒǉ�����l.<bR>
	 */
	public void setAddDir(float addDir) {
		this.addDir = addDir;
	}

	/**
	 * �ύX�\�Ȋp�x�̒l���擾���܂�.
	 * 
	 * @return �ύX�\�Ȋp�x�̏��.<br>
	 */
	public float getSpread() {
		return spread;
	}

	/**
	 * �ύX�\�Ȋp�x�̒l��ݒ肵�܂�.
	 * 
	 * @param spread �ύX�\�Ȋp�x�̏��.<br>
	 */
	public void setSpread(float spread) {
		this.spread = spread;
	}

	/**
	 * �C�x���g�ΏۃI�u�W�F�N�g�̍ŏ��ɐݒ肳��Ă��p�x���擾���܂�.
	 * 
	 * @return �C�x���g�ΏۃI�u�W�F�N�g�ɍŏ��ɐݒ肳��Ă����p�x�̓x���@�\�L.<br>
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
