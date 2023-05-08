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
package kinugasa.util;


/**
 * �C���f�b�N�X�̒l��I���ł���C���f�b�N�X�̃J�E���^�ł�.
 * <br>
 * ���̃��f���ł́A�v�����ꂽ�C���f�b�N�X���A�z��̒����𒴂���ꍇ��
 * �z��̍Ō�̗v�f���Q�Ƃ���C���f�b�N�X��Ԃ��܂��B<br>
 * 0�����̏ꍇ��0��Ԃ��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/02/12_12:43:13<br>
 * @author Shinacho<br>
 * <br>
 *
 */
public class SelectableIndex extends ArrayIndexModel {

	private static final long serialVersionUID = -2030495902937694525L;
	/** �C���f�b�N�X�̌��݂̒l�ł�. */
	private int currentIndex;

	/**
	 * �C���f�b�N�X��0�̃��f�����쐬���܂�.
	 */
	public SelectableIndex() {
		this(0);
	}

	/**
	 * �w�肳�ꂽ�C���f�b�N�X�̃��f�����쐬���܂�.
	 *
	 * @param currentIndex �C���f�b�N�X�̏����l���w�肵�܂��B<br>
	 */
	public SelectableIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	@Override
	public int index(int arrayLength) {
		return currentIndex < 0 ? 0 : currentIndex >= arrayLength ? arrayLength - 1 : currentIndex;
	}

	/**
	 * �C���f�b�N�X���C���N�������g���܂�.
	 */
	public void next() {
		currentIndex++;
	}

	/**
	 * �C���f�b�N�X���f�N�������g���܂�.
	 */
	public void prev() {
		currentIndex--;
	}

	@Override
	public int getIndex() {
		return currentIndex;
	}

	/**
	 * �C���f�b�N�X��ݒ肵�܂�.
	 *
	 * @param currentIndex �C���f�b�N�X���w�肵�܂��B<br>
	 */
	public void setIndex(int currentIndex) {
		this.currentIndex = currentIndex;
	}

	@Override
	public SelectableIndex clone() {
		return (SelectableIndex) super.clone();
	}

	@Override
	public String toString() {
		return "SelectableIndex{" + "currentIndex=" + currentIndex + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SelectableIndex other = (SelectableIndex) obj;
		if (this.currentIndex != other.currentIndex) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 59 * hash + this.currentIndex;
		return hash;
	}
}
