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
 * BufferedImageに対する高度な編集機能を提供するユーティリティクラスです.
 * <br>
 * このクラスでは、「ソース画像」のピクセルデータが、直接変更される可能性のある機能が定義されています。<br>
 * このクラスに定義されたメソッドは、ゲーム中において使用されるべきではありません。リソースとして準備したほうがパフォーマンスが向上します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_2:18:18<br>
 * @author Dra0211<br>
 */
public final class ImageEditor {

	/**
	 * ユーティリティクラスのためインスタンス化できません.
	 */
	private ImageEditor() {
	}

	/**
	 * 色tgtARGBをnewARGBで置き換えた新しい画像をdstに格納して返します.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param tgtARGB src中の置換の対象となる色をARGBカラーで指定します。<br>
	 * @param newARGB 置換後の色をARGBカラーで指定します。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return src中のtgtARGBのピクセルをnewARGBに置き換えた画像を返します。<br>
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
	 * srcをグレイスケール変換した画像をdstに格納して返します. このメソッドはピクセルのRGB平均をそのまま設定します。 NTSC系加重平均法は使用しません。<br>
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcのピクセルを明暗だけに置換した画像を返します。<br>
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
	 * NTSC加重平均法を適用したグレイスケール変換を行います.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcのピクセルを明暗だけに置換した画像を返します。<br>
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
	 * srcを白黒モノクローム変換した画像をdstに格納して返します.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param center 基準となる明度を0から255で指定します。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return src中の明度がcenterを超えるピクセルを白に、そうでないピクセルを黒に置き換えた画像を返します。<br>
	 *
	 * @throws IllegalArgumentException centerが0未満または255を超える場合に投げられます。<br>
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
	 * src中のピクセルの明度を(right*100)%に変換した画像をdstに格納して返します.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param right 明度の変更される割合を指定します。0未満を指定することはできません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcのピクセルの明度を(right*100)%に変換した画像を返します。<br>
	 *
	 * @throws IllegalArgumentException rightが0未満のときに投げられます。<br>
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
	 * srcの色を反転した画像をdstに格納して返します.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return src中のピクセルの色情報を反転した画像を返します。<br>
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
	 * srcに簡易的なモザイク処理を施した画像をdstに格納して返します.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param size モザイクのタイルのサイズをピクセル単位で指定します。1未満を指定することはできません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcをsizeピクセルごとに区切った領域をその領域のもっとも左上のピクセルの色で塗りつぶした画像を返します。<br>
	 *
	 * @throws IllegalArgumentException sizeが1未満の場合に投げられます。<br>
	 * @throws RasterFormatException sizeが画像よりも大きい場合に投げられます。<br>
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
	 * srcを時計回りにdeg度回転した画像をdstに格納して返します.
	 *
	 * @param src 色を置換するソース画像を指定します。この画像のピクセルは変更されません。<br>
	 * @param deg 回転角度を度数法で指定します。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcを回転した画像を返します。degが1のときはsrcのコピーが返されます。回転時に画像が領域の 外に出る場合、空いた領域は黒(Color.BLACK)となります。<br>
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
	 * ソース画像の全てのピクセルの透過度にtpを加算した画像を返します. このメソッドではピクセルの透過度が0未満又は255を超える場合は有効範囲内に切り詰められます。<br>
	 * tpが0の場合、透過度を変更する必要がないため、単純にsrcのコピーをdstに格納して返します。<br>
	 *
	 * @param src 透過度を変更するソース画像を指定します。この画像のピクセルデータは操作されません。<br>
	 * @param tp 加算する透過度を指定します。負数を許容します。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcの全てのピクセルの透過度にtpを加算したdstに格納して画像を返します。<br>
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
	 * 画像の各ピクセルを指定されたサイズだけ横方向にシフトして構築した画像を返します。<br>
	 *
	 * @param src 画像。<br>
	 * @param dst nullでない場合このインスタンスに編集結果が格納される。<br>
	 * @param shiftPixNum 縦の各ピクセルのシフト幅.srcの高さに満たない場合はそれを満たすまで繰り返される.負数を指定できる<br>
	 * @param insertARGB シフトした結果,空いた領域に挿入される色をARBG形式で指定する。<br>
	 *
	 * @return nullでない場合、この引数に結果が格納されます。<br>
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
	 * 水平および垂直方向にブラーエフェクトかけた画像を返します.
	 *
	 * @param src 効果をかけるソース画像です。この画像のピクセルデータは操作されません。<br>
	 * @param width 水平方向のブラー範囲を送信します。1以上の値を送信でき、大きな値ほど不鮮明な効果になります。<br>
	 * @param height 垂直方向のブラー範囲を指定します。1以上の値を送信でき、大きな値ほど不鮮明な効果になります。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return 水平および垂直方向にブラーエフェクトをかけた画像をdstに格納して返します。<br>
	 */
	public static BufferedImage blur2D(BufferedImage src, int width, int height, BufferedImage dst) {
		return blur(blur(src, width, false, dst), height, true, null);
	}

	/**
	 * 画像の水平又は垂直方向にブラー効果をかけます.
	 *
	 * @param src 効果をかけるソース画像です。この画像のピクセルデータは操作されません。<br>
	 * @param rad 1つのピクセルに対する、ブラーの効果範囲を指定します。1以上の値を送信でき、大きな値ほど不鮮明な効果になります。<br>
	 * @param hrz trueのときブラーの方向が水平になります。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return 水平又は垂直方向にブラーエフェクトをかけた画像をdstに格納して返します。<br>
	 *
	 * @throws IllegalArgumentException radが1未満の場合に投げられます。<br>
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
	 * ソース画像の透過度を変更した画像を返します. このメソッドは画像の全てのピクセルのアルファ値を均一にtp*100%に変更します。<br>
	 * ただしソース画像の完全に透明なピクセルはそのまま完全に透明なピクセルとしてコピーされます。<br>
	 *
	 * @param src 透過度を変更するソース画像を指定します。この画像のピクセルデータは操作されません。<br>
	 * @param tp 変更後の透過度の係数を指定します。0.0fから1.0fの値を指定できます。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 *
	 * @return srcの完全に透明でないピクセルの透過度をtp*100%に変更した画像をdstに格納して返します。<br>
	 *
	 * @throws IllegalArgumentException tpが1を超える場合又は0未満の場合に投げられます。<br>
	 */
	public static BufferedImage transparent(
			BufferedImage src, float tp, BufferedImage dst)
			throws IllegalArgumentException {
		if (tp > 1f || tp < 0f) {
			throw new IllegalArgumentException("透過値が無効です [tp > 1 || tp < 0]である必要があります tp=[" + tp + "]");
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
	 * 指定された画像を拡大／縮小した新しい画像を返します。<br>
	 *
	 * @param src ソース画像。<br>
	 * @param scale 拡大スケール.1.0fを指定した場合は、srcのコピーが返る。<br>
	 *
	 * @return ソース画像のスケーリング結果を返す.スケーリング係数が1.0fのときはソース画像のコピーを返す。<br>
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
