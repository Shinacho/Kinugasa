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
package kinugasa.object;

/**
 * �N���[�j���O�\�ȓ���̃A���S���Y�����Ԃ��J�v�Z�������邽�߂̒��ۃN���X�ł�.
 * <br>
 * �S�Ẵ��f���̎����́Aclone���\�b�h��K�؂ɃI�[�o�[���C�h����K�v������܂��B<br>
 * <br>
 * �قƂ�ǂ̃��f���ł́A���f�������v�f�ɂ���āA���̃C���X�^���X�Ƃ̔�r���o���邱�Ƃ����҂���܂��B<br>
 * queals�����hashCode��K�؂ɃI�[�o�[���C�h����K�v������܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2012/07/14_16:58:06.<br>
 * @version 2.0.0 - 2013/01/11_17:10:31.<br>
 * @author Dra0211.<br>
 */
public abstract class Model implements Cloneable {

	/**
	 * �V�������f�����쐬���܂�.
	 */
	public Model() {
	}

	/**
	 * ���̃��f���̃N���[����Ԃ��܂�.
	 * �N���[����Object�N���X�̋@�\���g���čs���܂��B<br>
	 * �S�Ẵ��f���̎����́Aclone���\�b�h��K�؂ɃI�[�o�[���C�h����K�v������܂��B<br>
	 *
	 * @return ���̃��f���Ɠ����N���X�̐V�����C���X�^���X��Ԃ��܂��B<br>
	 */
	@Override
	public Model clone() {
		try {
			return (Model) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}

	/**
	 * ���郂�f��obj�̃N���[����Ԃ��܂�.�����obj.clone()�Ɠ�������ł�.
	 *
	 * @param <T> �N���[������N���X�ł��B<br>
	 * @param obj �N���[�����郂�f�����w�肵�܂��B<br>
	 *
	 * @return obj�̃N���[����Ԃ��܂��B<br>
	 */
	public static <T extends Model> T clone(T obj) {
		return (T) obj.clone();
	}
}
