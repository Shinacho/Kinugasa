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
package kinugasa.graphics;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import javax.imageio.ImageIO;
import kinugasa.game.GameLog;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.util.StopWatch;

/**
 * �摜��IO��ȈՕҏW���s�����[�e�B���e�B�N���X�ł�.
 * <br>
 * ���̃N���X���烍�[�h�����摜�́A�ʏ�̕��@�Ń��[�h���ꂽ�摜���� �����ɕ`��ł���\��������܂��B
 * �܂��A���̃N���X�̃��[�h�@�\�́A�����t�@�C���p�X���w�肷��� �����摜�C���X�^���X��Ԃ��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_2:08:33<br>
 * @version 1.1.0 - 2013/04/28_23:16<br>
 * @author Shinacho<br>
 */
public final class ImageUtil {

	/**
	 * �f�t�H���g�̃E�C���h�E�V�X�e�����T�|�[�g����摜�̐����@�\���������A�O���t�B�b�N�X�̐ݒ�ł�.
	 */
	private static final GraphicsConfiguration gc
			= GraphicsEnvironment.getLocalGraphicsEnvironment().
					getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * ���C���X�N���[���̃f�o�C�X�ݒ���擾���܂��B<br>
	 *
	 * @return �f�o�C�X�̐ݒ�B���̃C���X�^���X����摜���쐬�ł��܂��B<br>
	 */
	public static GraphicsConfiguration getGraphicsConfiguration() {
		return gc;
	}

	/**
	 * ���[�e�B���e�B�N���X�̂��߃C���X�^���X���ł��܂���.
	 */
	private ImageUtil() {
	}

	//------------------------------------------------------------------------------------------------------------
	/**
	 * �V�������BufferedImage�𐶐����܂�. �쐬���ꂽ�摜�͑S�Ẵs�N�Z�������S�ɓ����ȍ�(0x00000000)�ł��B<br>
	 *
	 * @param width �摜�̕����s�N�Z���P�ʂŎw�肵�܂��B<br>
	 * @param height �摜�̍������s�N�Z���P�ʂŎw�肵�܂��B<br>
	 *
	 * @return BufferedImage�̐V�����C���X�^���X��Ԃ��܂��B<br>
	 */
	public static BufferedImage newImage(int width, int height) {
		return gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

	/**
	 * BufferedImage�̕�����V�����C���X�^���X�Ƃ��ĕԂ��܂�.
	 *
	 * @param src �R�s�[����摜�B<br>
	 *
	 * @return src�Ɠ����摜�̐V�����C���X�^���X��Ԃ��܂��B<br>
	 */
	public static BufferedImage copy(BufferedImage src) {
		return copy(src, (BufferedImage) null);
	}

	/**
	 * BufferedImage�̕������쐬���Adst�Ɋi�[���܂�.
	 *
	 * @param src �R�s�[����摜�B<br>
	 * @param dst null�łȂ��ꍇ���̃C���X�^���X�Ɍ��ʂ��i�[�����B<br>
	 *
	 * @return null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 */
	public static BufferedImage copy(BufferedImage src, BufferedImage dst) {
		if (dst == null || dst == src) {
			dst = newImage(src.getWidth(), src.getHeight());
		}
		Graphics2D g2 = dst.createGraphics();
		g2.setRenderingHints(RenderingQuality.QUALITY.getRenderingHints());
		g2.drawImage(src, 0, 0, null);
		g2.dispose();
		return dst;
	}

	/**
	 * BufferedImage���t�@�C������쐬���܂�.
	 * ���̃��\�b�h�͂��łɈ�x�v�����ꂽ�摜���ēx�v�������ꍇ�A�����C���X�^���X��Ԃ��܂��B<br>
	 * �m���ɕʂ̃C���X�^���X���擾����ꍇ�͂��̃��\�b�h�̖߂�l�ɑ΂��Ă��̃N���X��copy���\�b�h���g�p���Ă��������B<br>
	 *
	 * @param filePath �ǂݍ��ރt�@�C���p�X�B<br>
	 *
	 * @return �ǂݍ��܂ꂽ�摜.���łɈ�x�ǂݍ��܂�Ă���ꍇ�̓L���b�V���f�[�^�̓����摜�C���X�^���X��Ԃ��B<br>
	 *
	 * @throws ContentsFileNotFoundException �t�@�C�������݂��Ȃ��ꍇ�ɓ�������B<br>
	 * @throws ContentsIOException �t�@�C�������[�h�ł��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public static BufferedImage load(String filePath) throws FileNotFoundException, ContentsIOException {
		StopWatch watch = new StopWatch().start();
		//GC�����s����Ă��邩�L���b�V�����Ȃ���ΐV�������[�h���ăL���b�V���ɒǉ�����
		File file = new File(filePath);
		BufferedImage dst = null;
		try {
			dst = ImageIO.read(file);
		} catch (IOException ex) {
			watch.stop();
			GameLog.print(Level.WARNING, "cant load filePath=[" + filePath + "](" + watch.getTime() + " ms)");
			throw new ContentsIOException(ex);
		}
		if (dst == null) {
			watch.stop();
			GameLog.print(Level.WARNING, "image is null filePath=[" + filePath + "](" + watch.getTime() + " ms)");
			throw new ContentsIOException("image is null");
		}
		//�݊��摜�ɒu��
		dst = copy(dst, newImage(dst.getWidth(), dst.getHeight()));
		watch.stop();
		GameLog.printInfoIfUsing("ImageUtil loaded filePath=[" + filePath + "](" + watch.getTime() + " ms)");
		return dst;
	}

	/**
	 * BufferedImage���t�@�C���ɕۑ����܂�. �摜�`���͓���PNG�摜�ƂȂ�܂��B<br>
	 *
	 * @param filePath �������ރt�@�C���p�X.�㏑���͊m�F���ꂸ�A�g���q���C�ӁB<br>
	 * @param image �������މ摜�B<br>
	 *
	 * @throws ContentsIOException �t�@�C�����������߂Ȃ��ꍇ�ɓ�������B<br>
	 */
	public static void save(String filePath, BufferedImage image) throws ContentsIOException {
		save(new File(filePath), image);
	}

	public static void save(File f, BufferedImage image) throws ContentsIOException {
		try {
			ImageIO.write(image, "PNG", f);
		} catch (IOException ex) {
			throw new ContentsIOException(ex);
		}
	}

	/**
	 * BufferedImage�̃s�N�Z���f�[�^��z��Ƃ��Ď擾���܂�.
	 *
	 * @param image �s�N�Z���f�[�^���擾����摜�𑗐M���܂��B<br>
	 *
	 * @return �w�肳�ꂽ�摜�̃s�N�Z���f�[�^���ꎟ���z��Ƃ��ĕԂ��܂��B ���̔z��͉摜�ɐݒ肳��Ă���s�N�Z���̃N���[���ł��B<br>
	 */
	public static int[] getPixel(BufferedImage image) {
		return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	}

	/**
	 * BufferedImage�̃s�N�Z���f�[�^��񎟌��z��Ƃ��Ď擾���܂�.
	 *
	 * @param image �s�N�Z���f�[�^���擾����摜�𑗐M���܂��B<br>
	 *
	 * @return �w�肳�ꂽ�摜�̃s�N�Z���f�[�^��񎟌��z��Ƃ��ĕԂ��܂��B ���̔z��͉摜�ɐݒ肳��Ă���s�N�Z���̃N���[���ł��B<br>
	 */
	public static int[][] getPixel2D(BufferedImage image) {
		int[] pix = getPixel(image);
		int[][] pix2 = new int[image.getHeight()][image.getWidth()];
		for (int i = 0, row = 0, WIDTH = image.getWidth(); i < pix2.length; i++, row += WIDTH) {
			System.arraycopy(pix, row, pix2[i], 0, WIDTH);
		}
		return pix2;
	}

	/**
	 * BufferedImage�Ƀs�N�Z���f�[�^��ݒ肵�܂�.
	 * ���̃��\�b�h�̓s�N�Z�����Ɖ摜�̎��ۂ̃s�N�Z�������قȂ�ꍇ�̓���͒�`����Ă��܂���B<br>
	 *
	 * @param image �s�N�Z���f�[�^��ݒ肷��摜�B<br>
	 * @param pix �ݒ肷��s�N�Z���f�[�^�B<br>
	 */
	public static void setPixel(BufferedImage image, int[] pix) {
		image.setRGB(0, 0, image.getWidth(), image.getHeight(), pix, 0, image.getWidth());
	}

	/**
	 * BufferedImage�Ƀs�N�Z���f�[�^��ݒ肵�܂�.
	 * ���̃��\�b�h�̓s�N�Z�����Ɖ摜�̎��ۂ̃s�N�Z�������قȂ�ꍇ�̓���͒�`����Ă��܂���B<br>
	 *
	 * @param image �摜�B<br>
	 * @param pix �ݒ肷��s�N�Z���f�[�^�B<br>
	 */
	public static void setPixel2D(BufferedImage image, int[][] pix) {
		int[] newPix = new int[getPixel(image).length];
		for (int i = 0; i < pix.length; i++) {
			System.arraycopy(pix[i], 0, newPix, i * pix[0].length, pix[i].length);
		}
		image.setRGB(0, 0, image.getWidth(), image.getHeight(), newPix, 0, image.getWidth());
	}

	public static int getPixel(BufferedImage image, int x, int y) {
		return getPixel2D(image)[y][x];
	}

	/**
	 * �摜�ɏ������ނ��߂̃O���t�B�N�X�R���e�L�X�g���쐬���܂�.
	 *
	 * @param image �O���t�B�b�N�X�R���e�L�X�g���擾����摜���w�肵�܂��B <br>
	 * @param renderingPolicy null�łȂ��ꍇ�A���̃����_�����O�ݒ肪�O���t�B�b�N�X�R���e�L�X�g�ɓK�p����܂��B<br>
	 *
	 * @return �w�肵���摜�ɏ������ނ��߂̃O���t�B�b�N�X�R���e�L�X�g���쐬���ĕԂ��܂��B<br>
	 */
	public static Graphics2D createGraphics2D(BufferedImage image, RenderingQuality renderingPolicy) {
		Graphics2D g = image.createGraphics();
		if (renderingPolicy != null) {
			g.setRenderingHints(renderingPolicy.getRenderingHints());
		}
		g.setClip(0, 0, image.getWidth(), image.getHeight());
		return g;
	}

	/**
	 * BudderdImage��0, y ����w, h�̃T�C�Y�ŉ������ɉ摜�𕪊����A�z��Ƃ��ĕԂ��܂�.
	 *
	 * @param src �摜�B<br>
	 * @param y Y���W�B<br>
	 * @param w �؂�o�����B<br>
	 * @param h �؂�o�������B<br>
	 *
	 * @return src����������w�̕��Ő؂�o�����������̉摜�B<br>
	 *
	 * @throws RasterFormatException ���W�܂��̓T�C�Y���s���ȏꍇ�ɓ�������B<br>
	 */
	public static BufferedImage[] rows(BufferedImage src, int y, int w, int h) throws RasterFormatException {
		BufferedImage[] dst = new BufferedImage[src.getWidth() / w];
		for (int i = 0, x = 0; i < dst.length; i++, x += w) {
			dst[i] = src.getSubimage(x, y, w, h);
		}
		return dst;
	}

	/**
	 * BudderdImage��x, 0 ����w, h�̃T�C�Y�ŏc�����ɉ摜�𕪊����A�z��Ƃ��ĕԂ��܂�.
	 *
	 * @param src �摜�B<br>
	 * @param x X���W�B<br>
	 * @param w �؂�o�����B<br>
	 * @param h �؂�o�������B<br>
	 *
	 * @return src���c������h�̍����Ő؂�o�����������̉摜�B<br>
	 *
	 * @throws RasterFormatException ���W�܂��̓T�C�Y���s���ȏꍇ�ɓ�������B<br>
	 */
	public static BufferedImage[] columns(BufferedImage src, int x, int w, int h) throws RasterFormatException {
		BufferedImage[] dst = new BufferedImage[src.getHeight() / h];
		for (int i = 0, y = 0; i < dst.length; i++, y += h) {
			dst[i] = src.getSubimage(x, y, w, h);
		}
		return dst;
	}

	/**
	 * BufferedImage��0, 0,����w, h�̃T�C�Y�œ񎟌��ɉ摜�𕪊����A���X�g�Ƃ��ĕԂ��܂�.
	 *
	 * �Ԃ���郊�X�g��1�����ŁA�摜�̍��ォ��E�����֕��ׂ��܂��B<br>
	 *
	 * @param src �摜�B<br>
	 * @param w �؂�o�����B<br>
	 * @param h �؂�o�������B<br>
	 *
	 * @return src���w�肳�ꂽ�T�C�Y�Ő؂�o�����������̉摜�B<br>
	 *
	 * @throws RasterFormatException ���W�܂��̓T�C�Y���s���ȏꍇ�ɓ�������B<br>
	 */
	public static List<BufferedImage> splitAsList(BufferedImage src, int w, int h) throws RasterFormatException {
		int columns = src.getHeight() / h;
		List<BufferedImage> dst = new ArrayList<BufferedImage>(columns * (src.getWidth() / w));
		for (int i = 0; i < columns; i++) {
			dst.addAll(Arrays.asList(rows(src, i * h, w, h)));
		}
		return dst;
	}

	/**
	 * BufferedImage��0, 0,����w, h�̃T�C�Y�œ񎟌��ɉ摜�𕪊����A�z��Ƃ��ĕԂ��܂�.
	 *
	 * @param src �摜�B<br>
	 * @param w �؂�o�����B<br>
	 * @param h �؂�o�������B<br>
	 *
	 * @return src���w�肳�ꂽ�T�C�Y�Ő؂�o�����������̉摜�B<br>
	 *
	 * @throws RasterFormatException ���W�܂��̓T�C�Y���s���ȏꍇ�ɓ�������B<br>
	 */
	public static BufferedImage[][] splitAsArray(BufferedImage src, int w, int h) throws RasterFormatException {
		BufferedImage[][] dst = new BufferedImage[src.getHeight() / h][src.getWidth() / w];
		for (int i = 0, y = 0; i < dst.length; i++, y += h) {
			dst[i] = rows(src, y, w, h);
		}
		return dst;
	}

	/**
	 * BufferedImage��0, 0,����w, h�̃T�C�Y�œ񎟌��ɉ摜�𕪊����A�}�b�v�Ƃ��ĕԂ��܂�.
	 * �e�v�f�̖����K����0�x�[�X��[�c�̗v�f�ԍ�][���̗v�f�ԍ�]�̓񂯂��̐���������ƂȂ�܂��B<br>
	 * �������v�f��2���ɖ����Ȃ��ꍇ��0n�̂悤�ɐ��`����܂��B<br>
	 *
	 * @param src �摜�B<br>
	 * @param w �؂�o�����B<br>
	 * @param h �؂�o�������B<br>
	 *
	 * @return src���w�肳�ꂽ�T�C�Y�Ő؂�o�����������̉摜�B<br>
	 *
	 * @throws RasterFormatException ���W�܂��̓T�C�Y���s���ȏꍇ�ɓ�������B<br>
	 */
	public static Map<String, BufferedImage> splitAsMap(BufferedImage src, int w, int h) throws RasterFormatException {
		HashMap<String, BufferedImage> dst = new HashMap<String, BufferedImage>(src.getWidth() / w * src.
				getHeight() / h);
		BufferedImage[][] dist = splitAsArray(src, w, h);
		for (int i = 0; i < dist.length; i++) {
			for (int j = 0; j < dist[i].length; j++) {
				String y = Integer.toString(i);
				String x = Integer.toString(j);
				if (y.length() == 1) {
					y = "0" + y;
				}
				if (x.length() == 1) {
					x = "0" + x;
				}
				dst.put(y + x, dist[i][j]);
			}
		}
		return dst;
	}

	/**
	 * BufferedImage��0, 0,����w, h�̃T�C�Y�œ񎟌��ɉ摜�𕪊����A�}�b�v�Ƃ��ĕԂ��܂�.
	 * �e�v�f�̖����K����0�x�[�X��[�c�̗v�f�ԍ�][���̗v�f�ԍ�]��digit�̐���������ƂȂ�܂��B<br>
	 * �������v�f��2���ɖ����Ȃ��ꍇ��0n�̂悤�ɐ��`����܂��B<br>
	 *
	 * @param src �摜�B<br>
	 * @param w �؂�o�����B<br>
	 * @param h �؂�o�������B<br>
	 * @param digit �摜�������̃C���f�b�N�X�̌����B0���߂����B<br>
	 *
	 * @return src���w�肳�ꂽ�T�C�Y�Ő؂�o�����������̉摜�B<br>
	 *
	 * @throws RasterFormatException ���W�܂��̓T�C�Y���s���ȏꍇ�ɓ�������B<br>
	 */
	public static Map<String, BufferedImage> splitAsMapN(BufferedImage src, int w, int h, int digit) throws RasterFormatException {
		HashMap<String, BufferedImage> dst = new HashMap<String, BufferedImage>(src.getWidth() / w * src.getHeight() / h);
		BufferedImage[][] dist = splitAsArray(src, w, h);
		for (int i = 0; i < dist.length; i++) {
			for (int j = 0; j < dist[i].length; j++) {
				String y = Integer.toString(i);
				String x = Integer.toString(j);
				while (y.length() < digit) {
					y = "0" + y;
				}
				while (x.length() < digit) {
					x = "0" + x;
				}
				dst.put(y + x, dist[i][j]);
			}
		}
		return dst;
	}

	/**
	 * �w�肳�ꂽ�̈�̃L���v�`�����w�肳�ꂽ�t�@�C���ɕۑ����܂�. ���̃��\�b�h�͐V�����X���b�h[kgf screen
	 * shot]���N�����A���̃X���b�h���ŉ摜���쐬���ĕۑ����܂��B<br>
	 * �摜�̏㏑���m�F�͍s���܂���B�����I�ɏ㏑������܂��B<br>
	 *
	 * @param FILE_PATH �t�@�C���p�X���L�q���܂��B<br>
	 * @param BOUNDS �L���v�`������̈�.�f�o�C�X�̃O���[�o�����W�Ŏw�肵�܂��B<br>
	 *
	 * @throws ContentsIOException �摜���ۑ��ł��Ȃ��ꍇ����уX�N���[���V���b�g���擾�ł��Ȃ��ꍇ�� �������܂��B<br>
	 */
	public static void screenShot(final String FILE_PATH, final Rectangle BOUNDS) throws ContentsIOException {
		new Thread("kgf screen shot") {
			@Override
			public void run() {
				try {
					BufferedImage image = new Robot().createScreenCapture(BOUNDS);
					ImageUtil.save(FILE_PATH, image);
					GameLog.printInfoIfUsing("�X�N���[���V���b�g���B�e���܂��� FILE_PATH=[" + FILE_PATH + "]");
				} catch (AWTException ex) {
					throw new ContentsIOException(ex);
				}
			}
		}.start();
	}

	/**
	 * �\�[�X�摜���w�肳�ꂽ���������������ɕ��ׂ��摜���쐬���܂�.
	 *
	 * @param src �^�C�����O����\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���f�[�^�͑��삳��܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 * @param xNum X�����ɕ��ׂ鐔���w�肵�܂��B<br>
	 * @param yNum Y�����ɕ��ׂ鐔���w�肵�܂��B<br>
	 *
	 * @return �\�[�X�摜��2�����Ɍ��ԂȂ����ׂ��摜��dst�Ɋi�[���ĕԂ��܂��B<br>
	 */
	public static BufferedImage tiling(BufferedImage src, BufferedImage dst, int xNum, int yNum) {
		if (dst == null || dst == src) {
			dst = newImage(src.getWidth() * xNum, src.getHeight() * yNum);
		}
		Graphics2D g2 = dst.createGraphics();
		for (int y = 0, width = src.getWidth(), height = src.getHeight(); y < yNum; y++) {
			for (int x = 0; x < xNum; x++) {
				g2.drawImage(src, x * width, y * height, null);
			}
		}
		g2.dispose();
		return dst;
	}

	/**
	 * �\�[�X�摜���w�肳�ꂽ���������ׂ��摜���쐬���܂�.
	 *
	 * @param src �^�C�����O����\�[�X�摜���w�肵�܂��B���̉摜�̃s�N�Z���f�[�^�͑��삳��܂���B<br>
	 * @param dst null�łȂ��ꍇ�A���̈����Ɍ��ʂ��i�[����܂��B<br>
	 * @param xNum X�����ɕ��ׂ鐔���w�肵�܂��B<br>
	 * @param yNum Y�����ɕ��ׂ鐔���w�肵�܂��B<br>
	 * @param drawWidth �摜��`�悷��ۂ̃T�C�Y���w�肵�܂��B<br>
	 * @param drawHeight �摜��`�悷��ۂ̃T�C�Y���w�肵�܂��B<br>
	 * @return �\�[�X�摜��2�����Ɍ��ԂȂ����ׂ��摜��dst�Ɋi�[���ĕԂ��܂��B<br>
	 */
	public static BufferedImage tiling(BufferedImage src, BufferedImage dst, int xNum, int yNum,
			int drawWidth, int drawHeight) {
		if (dst == null || dst == src) {
			dst = newImage(xNum * drawWidth, yNum * drawHeight);
		}
		Graphics2D g2 = dst.createGraphics();
		for (int y = 0; y < yNum; y++) {
			for (int x = 0; x < xNum; x++) {
				g2.drawImage(src, x * drawWidth, y * drawHeight, drawWidth, drawHeight, null);
			}
		}
		g2.dispose();
		return dst;
	}

	/**
	 * �w�肵���̈�̉摜��V�����C���X�^���X�Ƃ��ĕԂ��܂��B<br>
	 *
	 * @param src
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return
	 * @throws RasterFormatException
	 */
	public static BufferedImage trimming(BufferedImage src, int x, int y, int width, int height)
			throws RasterFormatException {
		return src.getSubimage(x, y, width, height);
	}

	/**
	 * 1�̉摜�̓��ߓx��initialTp����decTp���ύX�����摜��z��Ƃ��ĕԂ��܂�.
	 * ���̃��\�b�h�ł́A�\�[�X�摜�̊��S�ɓ����ȃs�N�Z���͂��̂܂ܓ����ȃs�N�Z���Ƃ��ăR�s�[����܂��B<br>
	 *
	 * @param image ���ߓx��ύX����\�[�X�摜�B<br>
	 * @param initialTp ���ߓx�̏����l�ł��B
	 * @param addTp ���ߓx�ɉ��Z����l�ł��B�ʏ�͕������g�p���܂��B<br>
	 *
	 * @return �\�[�X�摜�́A���X�ɓ��ߓx���ς��摜��z��Ƃ��ĕԂ��܂��B<br>
	 *
	 * @throws IllegalArgumentException initailTp��0��������1�𒴂���Ƃ��ɓ������܂��B<br>
	 */
	public static BufferedImage[] transparentArray(BufferedImage image, float initialTp, float addTp)
			throws IllegalArgumentException {
		if (initialTp > 1f || initialTp < 0f) {
			throw new IllegalArgumentException("initialTp > 1 || initialTp < 0 : initialTp=[" + initialTp + "], addTp=[" + addTp + "]");
		}
		BufferedImage src = ImageUtil.copy(image);
		List<BufferedImage> result = new ArrayList<BufferedImage>((int) (Math.abs(initialTp) / Math.
				abs(addTp)));
		for (float tp = initialTp; tp > 0; tp += addTp) {
			result.add(ImageUtil.copy(src));
			src = ImageEditor.transparent(src, tp, null);
		}
		return result.toArray(new BufferedImage[result.size()]);
	}

	/**
	 * �摜�z��𐅕������ɕ��ׂ��V�����摜���쐬���ĕԂ��܂�.
	 *
	 * @param images �g�p����摜��1�ȏ㑗�M���܂��B<br>
	 *
	 * @return images�����̏��Ԃō����琅�������Ɍ��ԂȂ����ׂ��V�����摜��Ԃ��܂��B<br>
	 *
	 * @throws IllegalArgumentException images�̒�����0�̂Ƃ��ɓ������܂��B<br>
	 */
	public static BufferedImage lineUp(BufferedImage... images)
			throws IllegalArgumentException {
		if (images.length == 0) {
			throw new IllegalArgumentException("images is empty : images.length=[" + images.length + "]");
		}
		int maxHeight = images[0].getHeight();
		int width = 0;
		for (int i = 0; i < images.length; i++) {
			if (images[i].getHeight() > maxHeight) {
				maxHeight = images[i].getHeight();
			}
			width += images[i].getWidth();
		}
		BufferedImage result = newImage(width, maxHeight);
		Graphics2D g = result.createGraphics();
		g.setRenderingHints(RenderingQuality.QUALITY.getRenderingHints());
		for (int i = 0, widthSum = 0; i < images.length; i++) {
			g.drawImage(images[i], widthSum, 0, null);
			widthSum += images[i].getWidth();
		}
		return result;
	}

	public static Color averageColor(BufferedImage image) {
		int a, r, g, b;
		a = r = g = b = 0;
		int[] pix = getPixel(image);
		for (int val : pix) {
			a += ARGBColor.getAlpha(val);
			r += ARGBColor.getRed(val);
			g += ARGBColor.getGreen(val);
			b += ARGBColor.getBlue(val);
		}
		a /= pix.length;
		r /= pix.length;
		g /= pix.length;
		b /= pix.length;
		return new Color(r, g, b, a);
	}

	public static boolean hasClaerPixcel(BufferedImage image) {
		for (int p : getPixel(image)) {
			if (ARGBColor.getAlpha(p) == 0) {
				return true;
			}
		}
		return false;
	}
}
