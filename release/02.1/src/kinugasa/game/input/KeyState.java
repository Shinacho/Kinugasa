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
package kinugasa.game.input;

import java.util.BitSet;

/**
 * �L�[���͂̏�Ԃ��i�[���܂�.
 * <br>
 * �L�[�̏�Ԃ�BitSet�N���X���g�p���Ċi�[����܂��B<br>
 * ����L�[�̏�ԂɃA�N�Z�X���邽�߂̃C���f�b�N�X��AWT�L�[�R�[�h�܂��� ��������b�v����Keys�񋓂��g�p���܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_13:16:01<br>
 * @author Dra0211<br>
 */
public class KeyState extends InputDeviceState {

	/**
	 * �L�[�̏�Ԃ��i�[����z��.
	 */
	private BitSet keySet;

	/**
	 * �p�b�P�[�W�A�N�Z�X.
	 *
	 * @param state �L�[�̏��.�f�B�[�v�R�s�[���쐬����.<br>
	 */
	KeyState(BitSet state) {
		this.keySet = (BitSet) state.clone();
	}

	/**
	 * �L�[�̏�Ԃ��擾���܂�. 1�񂾂��̔���́u�O��̌�����ԁv��ێ����Ă������ƂŎ����\�ł�.<br>
	 *
	 * @param k �L�[.<br>
	 *
	 * @return ������Ă���Ƃ�TRUE��Ԃ�.<br>
	 */
	public boolean isPressed(Keys k) {
		return keySet.get(k.getKeyCode());
	}

	/**
	 * �L�[�̏�Ԃ��擾���܂�. ���̃��\�b�h�́AisPressed(Keys)���������ɓ��삷�邱�Ƃ�����܂�.<br>
	 *
	 * @param index �L�[�̃C���f�b�N�X�BKeyEvent�N���X�̒萔���g�p���Ă��������B<br>
	 *
	 * @return ������Ă���Ƃ���TRUE��Ԃ�.<br>
	 */
	public boolean isPressed(int index) {
		return keySet.size() <= index ? false : keySet.get(index);
	}

	@Override
	public String toString() {
		return "KeyState{" + "keySet=" + keySet + '}';
	}

	@Override
	public KeyState clone() {
		KeyState result = (KeyState) super.clone();
		result.keySet = (BitSet) this.keySet.clone();
		return result;
	}

	@Override
	public boolean isAnyInput() {
		return !keySet.isEmpty();
	}

	@Override
	public boolean isAnyButtonInput() {
		return !keySet.isEmpty();
	}

	@Override
	public boolean isEmptyInput() {
		return keySet.isEmpty();
	}

}
