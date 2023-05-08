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

import kinugasa.resource.FileNotFoundException;

/**
 * ���̃C���^�[�t�F�[�X�����������N���X�́A
 * XML�t�@�C������f�[�^�����[�h�ł��܂�.
 * <br>
 * �K�v�ł���΁AFreeable���������Ă��������B<br>
 * <br>
 * @version 1.0.0 - 2013/04/28_22:06:10<br>
 * @author Shinacho<br>
 */
public interface XMLFileSupport {

	/**
	 * �R���e���c��XML���烍�[�h���܂�.
	 * �قƂ�ǂ̎����ł́A�X�g���[�W�ɑ΂���f�[�^�̒ǉ����s���܂��B<br>
	 * @param filePath ���[�h����XML�t�@�C���̃p�X���w�肵�܂��B<br>
	 * @throws IllegalXMLFormatException XML�t�H�[�}�b�g��DTD�ɓK�����Ȃ��ꍇ�Ȃǂɓ����邱�Ƃ��ł��܂��B<br>
	 * @throws FileNotFoundException �w�肳�ꂽ�t�@�C�������݂��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws FileIOException �w�肳�ꂽ�t�@�C�������[�h�ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException,
			FileIOException;
}
