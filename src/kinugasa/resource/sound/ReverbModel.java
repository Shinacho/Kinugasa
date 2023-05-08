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
package kinugasa.resource.sound;

import java.io.Serializable;

/**
 * �T�E���h�̃��o�[�u�̐ݒ���J�v�Z�������܂�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:48:17<br>
 * @author Shinacho<br>
 */
public class ReverbModel implements Serializable {

	/**
	 * ���o�[�u���g�p���Ȃ��ݒ�ł�.
	 */
	public static ReverbModel NO_USE = new ReverbModel(false, 0, 0);
	//
	//
	/** ���o�[�u���g�p���邩�ǂ����̃t���O. */
	private boolean use;
	/** ���M�����o�[�u. */
	private float send;
	/** ��M�����o�[�u. */
	private float ret;

	public ReverbModel(boolean use, float send, float ret) {
		this.use = use;
		this.send = send;
		this.ret = ret;
	}

	/**
	 * ��M���̃��o�[�u���擾���܂�.
	 *
	 * @return ��M���̃��o�[�u�l��Ԃ��܂��B<br>
	 */
	public float getRet() {
		return ret;
	}

	/**
	 * ���M���̃��o�[�u���擾���܂�.
	 *
	 * @return ���M���̃��o�[�u�l��Ԃ��܂��B<br>
	 */
	public float getSend() {
		return send;
	}

	/**
	 * ���o�[�u���g�p���邩�ǂ��������肷��t���O���擾���܂�.
	 * @return ���o�[�u�̗L���t���O��Ԃ��܂��Btrue�̂Ƃ��g�p���܂��B<br>
	 */
	public boolean isUse() {
		return use;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ReverbModel other = (ReverbModel) obj;
		if (this.use != other.use) {
			return false;
		}
		if (Float.floatToIntBits(this.send) != Float.floatToIntBits(other.send)) {
			return false;
		}
		if (Float.floatToIntBits(this.ret) != Float.floatToIntBits(other.ret)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (this.use ? 1 : 0);
		hash = 29 * hash + Float.floatToIntBits(this.send);
		hash = 29 * hash + Float.floatToIntBits(this.ret);
		return hash;
	}
}
