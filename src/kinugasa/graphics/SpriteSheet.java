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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import kinugasa.game.GameLog;

/**
 * 1�̉摜���\�[�X��؂�o���āA�����̉摜�C���X�^���X���\�z���邽�߂̃r���_�ł�.
 * <br>
 * ����̃A���S���Y���ŕ����̃X�v���C�g�V�[�g���\�z����ꍇ��SpriteSheetCutter���g�p���Ă��������B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_13:00:09<br>
 * @version 1.1.0 - 2015/06/19_22:39<br>
 * @author Dra0211<br>
 */
public class SpriteSheet {

	/**
	 * �摜��؂�o���x�[�X�ƂȂ�摜�ł�. ���̉摜�͕ύX����܂���B
	 */
	private BufferedImage baseImage;
	/**
	 * �؂�o�����摜���ǉ�����郊�X�g�ł�.
	 */
	private ArrayList<BufferedImage> subImages;

	/**
	 * ��̃X�v���C�g�V�[�g���쐬���܂�.
	 */
	public SpriteSheet() {
		subImages = new ArrayList<BufferedImage>(32);
	}

	/**
	 * �V�����X�v���C�g�V�[�g���\�z���܂�.
	 *
	 * @param filePath ���[�h����摜�̃p�X���w�肵�܂��B ���̃R���X�g���N�^�ł́AImageUtil��load���\�b�h���g�p���ĉ摜�����[�h����܂��B<br>
	 */
	public SpriteSheet(String filePath) {
		baseImage = ImageUtil.load(filePath);
		subImages = new ArrayList<BufferedImage>(32);
	}

	/**
	 * �V�����X�v���C�g�V�[�g���\�z���܂�.
	 *
	 * @param baseImage �x�[�X�ƂȂ�摜���w�肵�܂��B<br>
	 */
	public SpriteSheet(BufferedImage baseImage) {
		this.baseImage = baseImage;
		subImages = new ArrayList<BufferedImage>(32);
	}

	/**
	 * �x�[�X�摜�̎w�肳�ꂽ�̈��؂�o���ĐV�����摜�Ƃ��܂�.
	 *
	 * @param x X���W.<br>
	 * @param y Y���W.<br>
	 * @param width ��.<br>
	 * @param height ����.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 *
	 * @throws RasterFormatException �摜�͈̔͊O�ɃA�N�Z�X�����Ƃ��ɓ�������.<br>
	 */
	public SpriteSheet cut(int x, int y, int width, int height) throws RasterFormatException {
		subImages.add(baseImage.getSubimage(x, y, width, height));
		return this;
	}

	/**
	 * �x�[�X�摜�̎w�肳�ꂽ�̈��؂�o���ĐV�����摜�Ƃ��܂�.
	 *
	 * @param rectangle �̈�.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 *
	 * @throws RasterFormatException �摜�͈̔͊O�ɃA�N�Z�X�����Ƃ��ɓ�������.<br>
	 */
	public SpriteSheet cut(Rectangle rectangle) throws RasterFormatException {
		return cut(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	public SpriteSheet resizeAll(float scale) {
		BufferedImage[] result = ImageEditor.resizeAll(subImages.<BufferedImage>toArray(new BufferedImage[subImages.size()]), scale);
		subImages.clear();
		addAll(result);
		return this;
	}

	/**
	 * �؂�o���A���S���Y���Ɋ�Â��āA���̃V�[�g��؂�o���܂�.
	 *
	 * @param cutter ����̐؂�o���ݒ�A���S���Y��.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 *
	 * @throws RasterFormatException �摜�͈̔͊O�ɃA�N�Z�X�����Ƃ��ɓ�������.<br>
	 */
	public SpriteSheet cut(SpriteSheetCutter cutter) throws RasterFormatException {
		addAll(cutter.cut(baseImage));
		return this;
	}

	/**
	 * ���W0,0����width,height�̃T�C�Y�œ񎟌��ɉ\�Ȑ������������A�S�Ă̕����摜�����X�g�ɒǉ����܂�.
	 *
	 * <br>
	 *
	 * @param width ��.<br>
	 * @param height ����.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 *
	 * @throws RasterFormatException �摜�͈̔͊O�ɃA�N�Z�X�����Ƃ��ɓ�������.<br>
	 */
	public SpriteSheet split(int width, int height) throws RasterFormatException {
		BufferedImage[][] images = ImageUtil.splitAsArray(baseImage, width, height);
		for (BufferedImage[] line : images) {
			subImages.addAll(Arrays.asList(line));
		}
		return this;
	}

	/**
	 * ���W0,y����width,height�̃T�C�Y��X�����ɉ\�Ȑ������摜�𕪊����A�S�Ă̕����摜�����X�g�ɒǉ����܂�.
	 *
	 * @param y Y���W.<br>
	 * @param width ��.<br>
	 * @param height ����.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 *
	 * @throws RasterFormatException �摜�͈̔͊O�ɃA�N�Z�X�����Ƃ��ɓ�������.<br>
	 */
	public SpriteSheet rows(int y, int width, int height) throws RasterFormatException {
		subImages.addAll(Arrays.asList(ImageUtil.rows(baseImage, y, width, height)));
		return this;
	}

	/**
	 * ���Wx,0����width,height�̃T�C�Y��Y�����ɉ\�Ȑ������摜�𕪊����A�S�Ă̕����摜�����X�g�ɒǉ����܂�.
	 *
	 * @param x X���W.<br>
	 * @param width ��.<br>
	 * @param height ����.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 *
	 * @throws RasterFormatException �摜�͈̔͊O�ɃA�N�Z�X�����Ƃ��ɓ�������.<br>
	 */
	public SpriteSheet columns(int x, int width, int height) throws RasterFormatException {
		subImages.addAll(Arrays.asList(ImageUtil.columns(baseImage, x, width, height)));
		return this;
	}

	/**
	 * �w�肳�ꂽ�摜��ǉ����܂�.
	 *
	 * @param image �摜.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 */
	public SpriteSheet add(BufferedImage image) {
		subImages.add(image);
		return this;
	}

	/**
	 * �w�肳�ꂽ0�ȏ�̉摜��S�Ă��̏����Ń��X�g�ɒǉ����܂�.
	 *
	 * @param images �摜.<br>
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 */
	public SpriteSheet addAll(BufferedImage... images) {
		subImages.addAll(Arrays.asList(images));
		return this;
	}

	/**
	 * �x�[�X�摜�{�̂����X�g�ɒǉ����܂�.
	 *
	 * @return ���̃C���X�^���X���̂��Ԃ�.<br>
	 */
	public SpriteSheet baseImage() {
		subImages.add(baseImage);
		return this;
	}

	/**
	 * ������m�肵�A�ǉ�����Ă���S�Ẳ摜��ǉ����ꂽ�����̔z��Ƃ��Ď擾���܂�. ���̑���ł́Anull�C���X�^���X��subImage�͐؂�̂Ă��܂��B<br>
	 *
	 * @return �ǉ�����Ă���摜�̔z��.<br>
	 */
	public BufferedImage[] images() {
		GameLog.printInfo("SpriteSheet���؂�o����܂��� : size=[" + subImages.size() + "]");
		subImages.trimToSize();
		int nullIdx = 0;
		for (int size = subImages.size(); nullIdx < size && subImages.get(nullIdx) != null; nullIdx++);
		BufferedImage[] result = new BufferedImage[nullIdx];
		System.arraycopy(subImages.<BufferedImage>toArray(new BufferedImage[subImages.size()]), 0, result, 0, nullIdx);

		return result;
	}

	/**
	 * ������m�肵�A�ǉ�����Ă���S�Ẳ摜��ǉ����ꂽ�����̘A�Ԃ��L�[�Ƃ����}�b�v�Ƃ��Ď擾���܂�.
	 *
	 * @return �ǉ�����Ă���摜�̃}�b�v�B<br>
	 */
	public Map<String, BufferedImage> toMap() {
		Map<String, BufferedImage> result = new HashMap<String, BufferedImage>(subImages.size());
		for (int i = 0, size = subImages.size(); i < size; i++) {
			result.put(Integer.toString(i), subImages.get(i));
		}
		return result;
	}

	@Override
	public String toString() {
		return "SpriteSheet{" + "baseImage=" + baseImage + ", subImages=" + subImages + '}';
	}
}
