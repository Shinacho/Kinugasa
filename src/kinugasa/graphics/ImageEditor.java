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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RasterFormatException;
import java.util.Arrays;
import static kinugasa.graphics.ImageUtil.*;

/**
 * BufferedImage�ɑ΂��鍂�x�ȕҏW�@�\��񋟂��郆�[�e�B���e�B�N���X�ł�.
 * <br>
 * ���̃N���X�ł́A�u�\�[�X�摜�v�̃s�N�Z���f�[�^���A���ڕύX�����\���̂���@�\����`����Ă��܂��B<br>
 * ���̃N���X�ɒ�`���ꂽ���\�b�h�́A�Q�[�����ɂ����Ďg�p�����ׂ��ł͂���܂���B���\�[�X�Ƃ��ď��������ق����p�t�H�[�}���X�����サ�܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_2:18:18<br>
 * @author Dra0211<br>
 */
public final class ImageEditor {

	/**
	 * ���[�e�B���e�B�N���X�̂��߃C���X�^���X���ł��܂���.
	 */
	private ImageEditor() {
	}

	/**
	 * �FtgtARGB��newARGB�Œu���������V�����摜��dst�Ɋi�[���ĕԂ��܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param tgtARGB src���̒u���̑ΏۂƂȂ�F��ARGB�J���[�Ŏw�肵�܂��B<br>
	 * @param newARGB �u����̐F��ARGB�J���[�Ŏw�肵�܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src����tgtARGB�̃s�N�Z����newARGB�ɒu���������摜��Ԃ��܂��B<br>
	 */
	public static BufferedImage replaceColor(BufferedImage src, int tgtARGB, int newARGB, BufferedImage dst) {
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		for (int i = 0; i < pix.length; i++) {
			if (pix[i] == tgtARGB) {
				pix[i] = newARGB;
			}
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * src���O���C�X�P�[���ϊ������摜��dst�Ɋi�[���ĕԂ��܂�. ���̃��\�b�h�̓s�N�Z����RGB���ς����̂܂ܐݒ肵�܂��B NTSC�n���d���ϖ@�͎g�p���܂���B<br>
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src�̃s�N�Z���𖾈Â����ɒu�������摜��Ԃ��܂��B<br>
	 */
	public static BufferedImage grayScale(BufferedImage src, BufferedImage dst) {
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		for (int i = 0; i < pix.length; i++) {
			int average = ARGBColor.getRGBAverage(pix[i]);
			pix[i] = (pix[i] & ARGBColor.ARGB_ALPHA_MASK)
					| (average << 16)
					| (average << 8)
					| average;
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * NTSC���d���ϖ@��K�p�����O���C�X�P�[���ϊ����s���܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src�̃s�N�Z���𖾈Â����ɒu�������摜��Ԃ��܂��B<br>
	 */
	public static BufferedImage weightedGrayScale(BufferedImage src, BufferedImage dst) {
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		for (int i = 0; i < pix.length; i++) {
			int brightness = (int) (ARGBColor.getRed(pix[i]) * 0.298912f)
					+ (int) (ARGBColor.getGreen(pix[i]) * 0.586611f)
					+ (int) (ARGBColor.getBlue(pix[i]) * 0.114478f);
			pix[i] = pix[i] & ARGBColor.ARGB_ALPHA_MASK
					| (brightness << 16)
					| (brightness << 8)
					| brightness;
		}
		setPixel(dst, pix);

		return dst;
	}

	/**
	 * src�𔒍����m�N���[���ϊ������摜��dst�Ɋi�[���ĕԂ��܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param center ��ƂȂ閾�x��0����255�Ŏw�肵�܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src���̖��x��center�𒴂���s�N�Z���𔒂ɁA�����łȂ��s�N�Z�������ɒu���������摜��Ԃ��܂��B<br>
	 *
	 * @throws IllegalArgumentException center��0�����܂���255�𒴂���ꍇ�ɓ������܂��B<br>
	 */
	public static BufferedImage monochrome(BufferedImage src, int center, BufferedImage dst)
			throws IllegalArgumentException {
		if (center < 0 || center > 255) {
			throw new IllegalArgumentException("center is over color range : center=[" + center + "]");
		}
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		for (int i = 0; i < pix.length; i++) {
			pix[i] = (ARGBColor.getRGBAverage(pix[i]) > center)
					? (pix[i] & ARGBColor.ARGB_ALPHA_MASK)
					| 0x00FFFFFF
					: (pix[i] & ARGBColor.ARGB_ALPHA_MASK);
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * src���̃s�N�Z���̖��x��(right*100)%�ɕϊ������摜��dst�Ɋi�[���ĕԂ��܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param right ���x�̕ύX����銄�����w�肵�܂��B0�������w�肷�邱�Ƃ͂ł��܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src�̃s�N�Z���̖��x��(right*100)%�ɕϊ������摜��Ԃ��܂��B<br>
	 *
	 * @throws IllegalArgumentException right��0�����̂Ƃ��ɓ������܂��B<br>
	 */
	public static BufferedImage brightness(BufferedImage src, float right, BufferedImage dst)
			throws IllegalArgumentException {
		if (right == 1f) {
			return dst = copy(src);
		}
		if (right < 0) {
			throw new IllegalArgumentException("right < 0 : right=[" + right + "]");
		}
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		int a, r, g, b;
		for (int i = 0; i < pix.length; i++) {
			a = ARGBColor.getAlpha(pix[i]);
			r = (int) (ARGBColor.getRed(pix[i]) * right);
			if (r > 255) {
				r = 255;
			} else if (r < 0) {
				r = 0;
			}
			g = (int) (ARGBColor.getGreen(pix[i]) * right);
			if (g > 255) {
				g = 255;
			} else if (g < 0) {
				g = 0;
			}
			b = (int) (ARGBColor.getBlue(pix[i]) * right);
			if (b > 255) {
				b = 255;
			} else if (b < 0) {
				b = 0;
			}
			pix[i] = ARGBColor.toARGB(a, r, g, b);
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * src�̐F�𔽓]�����摜��dst�Ɋi�[���ĕԂ��܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src���̃s�N�Z���̐F���𔽓]�����摜��Ԃ��܂��B<br>
	 */
	public static BufferedImage reverseColor(BufferedImage src, BufferedImage dst) {
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		int a, r, g, b;
		for (int i = 0; i < pix.length; i++) {
			a = pix[i] & ARGBColor.ARGB_ALPHA_MASK;
			r = (255 - ARGBColor.getRed(pix[i])) << 16;
			g = (255 - ARGBColor.getGreen(pix[i])) << 8;
			b = ARGBColor.getBlue(pix[i]);
			pix[i] = ARGBColor.toARGB(a, r, g, b);
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * src�ɊȈՓI�ȃ��U�C�N�������{�����摜��dst�Ɋi�[���ĕԂ��܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param size ���U�C�N�̃^�C���̃T�C�Y���s�N�Z���P�ʂŎw�肵�܂��B1�������w�肷�邱�Ƃ͂ł��܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src��size�s�N�Z�����Ƃɋ�؂����̈�����̗̈�̂����Ƃ�����̃s�N�Z���̐F�œh��Ԃ����摜��Ԃ��܂��B<br>
	 *
	 * @throws IllegalArgumentException size��1�����̏ꍇ�ɓ������܂��B<br>
	 * @throws RasterFormatException size���摜�����傫���ꍇ�ɓ������܂��B<br>
	 */
	public static BufferedImage mosaic(BufferedImage src, int size, BufferedImage dst)
			throws IllegalArgumentException, RasterFormatException {
		if (size < 1) {
			throw new IllegalArgumentException("size < 1 : size=[" + size + "]");
		}
		if (size > src.getWidth() || size > src.getHeight()) {
			throw new RasterFormatException("size is over image bounds : size=[" + size + "]");
		}
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[][] pix = getPixel2D(src);
		for (int y = 0, imageHeight = src.getHeight(); y + size < imageHeight; y += size) {
			for (int x = 0, imageWidth = src.getWidth(); x + size < imageWidth; x += size) {
				int argb = pix[y][x];
				for (int mosaicY = y, i = 0; i < size; mosaicY++, i++) {
					for (int mosaicX = x, j = 0; j < size; mosaicX++, j++) {
						if (mosaicX <= imageWidth && mosaicY <= imageHeight) {
							pix[mosaicY][mosaicX] = argb;
						}
					}
				}
			}
		}
		setPixel2D(dst, pix);
		return dst;
	}

	/**
	 * src�����v����deg�x��]�����摜��dst�Ɋi�[���ĕԂ��܂�.
	 *
	 * @param src �F��u������\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���͕ύX����܂���B<br>
	 * @param deg ��]�p�x��x���@�Ŏw�肵�܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src����]�����摜��Ԃ��܂��Bdeg��1�̂Ƃ���src�̃R�s�[���Ԃ���܂��B��]���ɉ摜���̈�� �O�ɏo��ꍇ�A�󂢂��̈�͍�(Color.BLACK)�ƂȂ�܂��B<br>
	 */
	public static BufferedImage rotate(BufferedImage src, float deg, BufferedImage dst) {
		if (deg == 0) {
			return dst = copy(src);
		}
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		Graphics2D g = createGraphics2D(dst, RenderingQuality.QUALITY);
		g.setClip(0, 0, dst.getWidth(), dst.getHeight());
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, dst.getWidth(), dst.getHeight());
		g.rotate(Math.toRadians(deg), dst.getWidth() / 2, dst.getHeight() / 2);
		g.drawImage(src, 0, 0, null);
		g.dispose();
		return dst;
	}

	/**
	 * �\�[�X�摜�̑S�Ẵs�N�Z���̓��ߓx��tp�����Z�����摜��Ԃ��܂�. ���̃��\�b�h�ł̓s�N�Z���̓��ߓx��0��������255�𒴂���ꍇ�͗L���͈͓��ɐ؂�l�߂��܂��B<br>
	 * tp��0�̏ꍇ�A���ߓx��ύX����K�v���Ȃ����߁A�P����src�̃R�s�[��dst�Ɋi�[���ĕԂ��܂��B<br>
	 *
	 * @param src ���ߓx��ύX����\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���f�[�^�͑��삳��܂���B<br>
	 * @param tp ���Z���铧�ߓx���w�肵�܂��B���������e���܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src�̑S�Ẵs�N�Z���̓��ߓx��tp�����Z����dst�Ɋi�[���ĉ摜��Ԃ��܂��B<br>
	 */
	public static BufferedImage addTransparent(BufferedImage src, int tp, BufferedImage dst) {
		if (tp == 0) {
			return dst = copy(src);
		}
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		for (int i = 0; i < pix.length; i++) {
			int alpha = ARGBColor.getAlpha(pix[i]);
			alpha += tp;
			if (alpha < 0) {
				alpha = 0;
			} else if (alpha > 255) {
				alpha = 255;
			}
			pix[i] = (alpha << 24) & ARGBColor.ARGB_ALPHA_MASK | pix[i] & ARGBColor.CLEAR_WHITE;
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * �摜�̊e�s�N�Z�����w�肳�ꂽ�T�C�Y�����������ɃV�t�g���č\�z�����摜��Ԃ��܂��B<br>
	 *
	 * @param src �摜�B<br>
	 * @param dst null�łȂ��ꍇ���̃C���X�^���X�ɕҏW���ʂ��i�[�����B<br>
	 * @param shiftPixNum �c�̊e�s�N�Z���̃V�t�g��.src�̍����ɖ����Ȃ��ꍇ�͂���𖞂����܂ŌJ��Ԃ����.�������w��ł���<br>
	 * @param insertARGB �V�t�g��������,�󂢂��̈�ɑ}�������F��ARBG�`���Ŏw�肷��B<br>
	 *
	 * @return null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 */
	public static BufferedImage rasterScroll(BufferedImage src, BufferedImage dst, int[] shiftPixNum, int insertARGB) {
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] sPix = shiftPixNum;
		if (shiftPixNum.length != dst.getHeight()) {
			sPix = new int[dst.getHeight()];
			for (int i = 0, spi = 0; i < sPix.length; i++) {
				sPix[i] = shiftPixNum[spi];
				spi = (spi < shiftPixNum.length - 1) ? spi + 1 : 0;
			}
		}
		int[][] pix = getPixel2D(dst);
		for (int y = 0; y < pix.length; y++) {
			if (sPix[y] == 0) {
				continue;
			}
			final int[] ROW = new int[pix[y].length];
			System.arraycopy(pix[y], 0, ROW, 0, ROW.length);
			int x = 0, lineIdx = 0;
			if (sPix[y] > 0) {
				Arrays.fill(pix[y], 0, sPix[y], insertARGB);
				x += sPix[y];
			} else {
				lineIdx += Math.abs(sPix[y]);
			}
			System.arraycopy(ROW, lineIdx, pix[y], x, ROW.length - Math.abs(sPix[y]));
			x += ROW.length - Math.abs(sPix[y]);
			Arrays.fill(pix[y], x, pix[y].length, insertARGB);
		}
		setPixel2D(dst, pix);
		return dst;
	}

	/**
	 * ��������ѐ��������Ƀu���[�G�t�F�N�g�������摜��Ԃ��܂�.
	 *
	 * @param src ���ʂ�������\�[�X�摜�ł��B���̉摜�̃s�N�Z���f�[�^�͑��삳��܂���B<br>
	 * @param width ���������̃u���[�͈͂𑗐M���܂��B1�ȏ�̒l�𑗐M�ł��A�傫�Ȓl�قǕs�N���Ȍ��ʂɂȂ�܂��B<br>
	 * @param height ���������̃u���[�͈͂��w�肵�܂��B1�ȏ�̒l�𑗐M�ł��A�傫�Ȓl�قǕs�N���Ȍ��ʂɂȂ�܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return ��������ѐ��������Ƀu���[�G�t�F�N�g���������摜��dst�Ɋi�[���ĕԂ��܂��B<br>
	 */
	public static BufferedImage blur2D(BufferedImage src, int width, int height, BufferedImage dst) {
		return blur(blur(src, width, false, dst), height, true, null);
	}

	/**
	 * �摜�̐������͐��������Ƀu���[���ʂ������܂�.
	 *
	 * @param src ���ʂ�������\�[�X�摜�ł��B���̉摜�̃s�N�Z���f�[�^�͑��삳��܂���B<br>
	 * @param rad 1�̃s�N�Z���ɑ΂���A�u���[�̌��ʔ͈͂��w�肵�܂��B1�ȏ�̒l�𑗐M�ł��A�傫�Ȓl�قǕs�N���Ȍ��ʂɂȂ�܂��B<br>
	 * @param hrz true�̂Ƃ��u���[�̕����������ɂȂ�܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return �������͐��������Ƀu���[�G�t�F�N�g���������摜��dst�Ɋi�[���ĕԂ��܂��B<br>
	 *
	 * @throws IllegalArgumentException rad��1�����̏ꍇ�ɓ������܂��B<br>
	 */
	public static BufferedImage blur(BufferedImage src, int rad, boolean hrz, BufferedImage dst)
			throws IllegalArgumentException {
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		if (rad < 1) {
			throw new IllegalArgumentException("radius < 1 :  rad=[" + rad + "]");
		}

		int size = rad * 2 + 1;
		float[] data = new float[size];
		float sigma = rad * 3f;
		float twoSigmaSq = 2f * sigma * sigma;
		float sigmaRoot = (float) Math.sqrt(twoSigmaSq * Math.PI);
		float total = 0f;

		for (int i = -rad; i < rad; i++) {
			float dist = i * i;
			int index = i + rad;
			data[index] = (float) Math.exp(-dist / twoSigmaSq) / sigmaRoot;
			total += data[index];
		}
		for (int i = 0; i < data.length; i++) {
			data[i] /= total;
		}
		Kernel kernel = hrz ? new Kernel(size, 1, data) : new Kernel(1, size, data);
		ConvolveOp op = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
		return op.filter(src, dst);
	}

	/**
	 * �\�[�X�摜�̓��ߓx��ύX�����摜��Ԃ��܂�. ���̃��\�b�h�͉摜�̑S�Ẵs�N�Z���̃A���t�@�l���ψ��tp*100%�ɕύX���܂��B<br>
	 * �������\�[�X�摜�̊��S�ɓ����ȃs�N�Z���͂��̂܂܊��S�ɓ����ȃs�N�Z���Ƃ��ăR�s�[����܂��B<br>
	 *
	 * @param src ���ߓx��ύX����\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���f�[�^�͑��삳��܂���B<br>
	 * @param tp �ύX��̓��ߓx�̌W�����w�肵�܂��B0.0f����1.0f�̒l���w��ł��܂��B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 *
	 * @return src�̊��S�ɓ����łȂ��s�N�Z���̓��ߓx��tp*100%�ɕύX�����摜��dst�Ɋi�[���ĕԂ��܂��B<br>
	 *
	 * @throws IllegalArgumentException tp��1�𒴂���ꍇ����0�����̏ꍇ�ɓ������܂��B<br>
	 */
	public static BufferedImage transparent(
			BufferedImage src, float tp, BufferedImage dst)
			throws IllegalArgumentException {
		if (tp > 1f || tp < 0f) {
			throw new IllegalArgumentException("���ߒl�������ł� [tp > 1 || tp < 0]�ł���K�v������܂� tp=[" + tp + "]");
		}
		if (dst == null || dst == src) {
			dst = copy(src);
		}
		int[] pix = getPixel(src);
		int alpha = (int) (255 * tp) << 24;
		for (int i = 0; i < pix.length; i++) {
			pix[i] = (pix[i] & ARGBColor.ARGB_ALPHA_MASK) != 0x00000000
					? alpha | (pix[i] & ARGBColor.CLEAR_WHITE)
					: pix[i] & ARGBColor.CLEAR_WHITE;
		}
		setPixel(dst, pix);
		return dst;
	}

	/**
	 * �w�肳�ꂽ�摜���g��^�k�������V�����摜��Ԃ��܂��B<br>
	 *
	 * @param src �\�[�X�摜�B<br>
	 * @param scale �g��X�P�[��.1.0f���w�肵���ꍇ�́Asrc�̃R�s�[���Ԃ�B<br>
	 *
	 * @return �\�[�X�摜�̃X�P�[�����O���ʂ�Ԃ�.�X�P�[�����O�W����1.0f�̂Ƃ��̓\�[�X�摜�̃R�s�[��Ԃ��B<br>
	 */
	public static BufferedImage resize(BufferedImage src, float scale) {
		if (Float.compare(scale, 1.0f) == 0) {
			return copy(src);
		}
		int newWidth = (int) (src.getWidth() * scale);
		int newHeight = (int) (src.getHeight() * scale);
		BufferedImage dst = newImage(newWidth, newHeight);
		Graphics2D g2 = createGraphics2D(dst, RenderingQuality.SPEED);
		g2.drawImage(src, 0, 0, newWidth, newHeight, null);
		g2.dispose();
		return dst;
	}

	public static BufferedImage[] resizeAll(BufferedImage[] images, float scale) {
		BufferedImage[] result = new BufferedImage[images.length];
		for (int i = 0; i < result.length; i++) {
			if (images[i] == null) {
				continue;
			}
			result[i] = resize(images[i], scale);
		}
		return result;
	}
}
