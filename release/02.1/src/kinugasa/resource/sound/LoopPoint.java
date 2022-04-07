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
package kinugasa.resource.sound;

/**
 * �T�E���h�̃��[�v�ʒu�����肷�邽�߂̃t���[�������J�v�Z�������܂�.
 * <br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:47:52<br>
 * @author Dra0211<br>
 */
public class LoopPoint {

	public static final int EOF = -1;
	/**
	 * �T�E���h�̊J�n��\���萔�ł�. �����0�Ɠ��l�ł��B<br>
	 */
	public static final int START = 0;
	//
	/**
	 * �t�@�C���̏I�[�܂ōĐ�������A�ŏ��ɖ߂�ݒ�ł�.
	 */
	public static final LoopPoint END_TO_START = new LoopPoint(EOF, START);
	/**
	 * ���[�v���g�p���Ȃ��ݒ�ł�.
	 */
	public static final LoopPoint NO_USE = null;

	/**
	 * ����������Ƃ�LoopPoint�v�f�Ƃ��Ďg�p�ł���int�l��Ԃ��܂�.
	 *
	 * @param valueString "EOF"����"START"�܂���int�𑗐M���܂��B �啶���������͋�ʂ���܂���B<br>
	 * @return�@�Ή�����int�l��Ԃ��܂��B<br>
	 * @throws NumberFormatException ����ł��Ȃ�������𑗐M���ꂽ�ꍇ�ɓ������܂��B<br>
	 */
	public static int valueOf(String valueString)
			throws NumberFormatException {
		if ("EOF".equals(valueString.toUpperCase())) {
			return EOF;
		}
		if ("START".equals(valueString.toUpperCase())) {
			return START;
		}
		return Integer.parseInt(valueString);
	}

	/**
	 * ���Ԃ��t���[�����ɕϊ�����. �߂�l��int�Ɋۂ߂��܂�.<br>
	 *
	 * @param sec ���Ԃ�b�P�ʂŎw��.<br>
	 * @param freq ���g��.���̃N���X�̒萔���g�p�ł���.<br>
	 *
	 * @return �w�肳�ꂽ�b���̃t���[����.<br>
	 */
	public static int secToFrame(double sec, int freq) {
		return (int) (sec * freq);
	}
	//
	/**
	 * ���[�v�J�n�ʒu.
	 */
	private int from;
	/**
	 * ���[�v���ɖ߂�����̈ʒu.
	 */
	private int to;

	/**
	 * ���[�v�ʒu���쐬.
	 *
	 * @param from ���[�v�J�n�ʒu.<br>
	 * @param to ���[�v���ɖ߂�����̈ʒu.<br>
	 */
	public LoopPoint(int from, int to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * ���[�v�J�n�ʒu���擾.
	 *
	 * @return ���[�v�J�n�ʒu.<br>
	 */
	public int getFrom() {
		return from;
	}

	/**
	 * ���[�v���ɖ߂�ʒu���擾.
	 *
	 * @return ���[�v���ɖ߂�ʒu.<br>
	 */
	public int getTo() {
		return to;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final LoopPoint other = (LoopPoint) obj;
		if (this.from != other.from) {
			return false;
		}
		if (this.to != other.to) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 13 * hash + this.from;
		hash = 13 * hash + this.to;
		return hash;
	}

	@Override
	public String toString() {
		return "LoopPoint{" + "from=" + from + ", to=" + to + '}';
	}
}
