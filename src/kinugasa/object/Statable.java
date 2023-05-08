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
package kinugasa.object;


/**
 * ���̃C���^�[�t�F�[�X�����������I�u�W�F�N�g�ɁA�u�J�n���Ă���v�u�I�������v�Ȃǂ̏�Ԃ𒲂ׂ�@�\��񋟂��܂�.
 * <br>
 * ��ɁA�G�t�F�N�g�̊J�n�^�j���𔻒肷�邽�߂Ɏg�p����܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_19:11:08<br>
 * @author Shinacho<br>
 */
public interface Statable {

	/**
	 * ���̃I�u�W�F�N�g���u�J�n���Ă���v��Ԃł��邩���������܂�.
	 * ���̃I�u�W�F�N�g�̏�Ԃ����Z�b�g�ł���ꍇ�A���Z�b�g����u�J�n����Ă���v���ǂ�����
	 * �����ɂ���ĈقȂ�܂��B<br>
	 * @return �J�n���Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isRunning();

	/**
	 * ���̃I�u�W�F�N�g���u�I�������v��Ԃł��邩���������܂�.
	 * @return �I�����Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isEnded();
}
