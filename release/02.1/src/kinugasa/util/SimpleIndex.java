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
package kinugasa.util;


/**
 * �z��̗v�f�ɁA���ԂɃA�N�Z�X�����{�I�ȃC���f�b�N�X�̃J�E���^�ł�.
 * <br>
 * index���\�b�h���Ăяo����邽�тɁA�C���f�b�N�X�̒l���J�E���g����܂��B
 * �C���f�b�N�X�̒l���ő�l�ɒB�����Ƃ��i�z��̗v�f��-1�j���̌Ăяo���ł�
 * �C���f�b�N�X�̒l��0�ɖ߂���܂��B<br>
 * <br>
 * ���̎����́A�����ŃJ�E���^�̒l��ێ����邽�߁A1�̔z��v�f�ɑ΂��āA1��
 * �C���f�b�N�X���f�����K�v�ł��B<br>
 *
 * @version 1.0.0 - 2013/01/12_17:12:36<br>
 * @author Dra0211<br>
 */
public class SimpleIndex extends ArrayIndexModel {

	private static final long serialVersionUID = -5792984578642711312L;
	/** ���݂̃C���f�b�N�X�ł�. */
	private int index;

	/**
	 * 0����X�^�[�g���A�E�����փ��[�v����C���f�b�N�X���f�����쐬���܂�.
	 */
	public SimpleIndex() {
		this(0);
	}

	/**
	 * �w�肳�ꂽ�C���f�b�N�X����X�^�[�g���A�E�����փ��[�v����C���f�b�N�X���f�����쐬���܂�.
	 *
	 * @param index �C���f�b�N�X�̏����l���w�肵�܂��B<br>
	 */
	public SimpleIndex(int index) {
		this.index = index;
	}

	@Override
	public int index(int arrayLength) {
		index++;
		if (index >= arrayLength) {
			index = 0;
		}
		return index;
	}

	/**
	 * �C���f�b�N�X�̌��ݒl��ݒ肵�܂�.
	 *
	 * @param index �J�E���^�̒l���w�肵�܂��B<br>
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public ArrayIndexModel clone() {
		return super.clone();
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 71 * hash + this.index;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SimpleIndex other = (SimpleIndex) obj;
		if (this.index != other.index) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "SimpleIndex{" + "index=" + index + '}';
	}
}