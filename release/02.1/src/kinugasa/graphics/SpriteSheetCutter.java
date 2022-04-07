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
package kinugasa.graphics;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import kinugasa.object.Model;

/**
 * �X�v���C�g�V�[�g�̐؂�o���A���S���Y�����J�v�Z�������܂�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_13:00:58<br>
 * @author Dra0211<br>
 */
public abstract class SpriteSheetCutter extends Model {

	private static final long serialVersionUID = -1630361870659622865L;

	/**
	 * �摜�����̃A���S���Y���ɏ]���Đ؂�o���܂�.
	 *
	 * @param base ���̉摜�����ƂɁA�摜��؂�o���܂��B���̉摜�͕ύX����Ă͂Ȃ�܂���B<br>
	 *
	 * @return �؂�o���ꂽ�摜���A���X�g�`���Ƃ��ĕԂ��܂��B<br>
	 *
	 * @throws RasterFormatException �x�[�X�摜�̃T�C�Y���A���̃A���S���Y���ɓK�؂łȂ��ꍇ�ɓ����邱�Ƃ��ł��܂��B<br>
	 */
	public abstract BufferedImage[] cut(BufferedImage base) throws RasterFormatException;

	@Override
	public SpriteSheetCutter clone() {
		return (SpriteSheetCutter) super.clone();
	}

}
