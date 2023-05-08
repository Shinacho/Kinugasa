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
package kinugasa.resource.text;

/**
 * XMF�t�@�C���̃t�H�[�}�b�g��DTD�ɉ����Ă��Ȃ��ꍇ�ɓ��������O�ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/03/14_15:11:06.<br>
 * @author Shinacho ( <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public class IllegalXMLFormatException extends FileFormatException {

	private static final long serialVersionUID = -8305393008312141380L;

	/**
	 * �V���� IllegalXMLFormatException �̃C���X�^���X���쐬.
	 *
	 * @param msg ���̗�O�̃��b�Z�[�W.<br>
	 */
	public IllegalXMLFormatException(String msg) {
		super(msg);
	}

	/**
	 * Throwable�����O���쐬���܂�.
	 * @param ex ������ꂽ��O�𑗐M���܂��B<br>
	 */
	public IllegalXMLFormatException(Exception ex) {
		super(ex);
	}
}
