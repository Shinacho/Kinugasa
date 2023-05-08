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
 * 画像のIOや簡易編集を行うユーティリティクラスです.
 * <br>
 * このクラスからロードした画像は、通常の方法でロードされた画像よりも 高速に描画できる可能性があります。
 * また、このクラスのロード機能は、同じファイルパスを指定すると 同じ画像インスタンスを返します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_2:08:33<br>
 * @version 1.1.0 - 2013/04/28_23:16<br>
 * @author Shinacho<br>
 */
public final class ImageUtil {

	/**
	 * デフォルトのウインドウシステムがサポートする画像の生成機能を持った、グラフィックスの設定です.
	 */
	private static final GraphicsConfiguration gc
			= GraphicsEnvironment.getLocalGraphicsEnvironment().
					getDefaultScreenDevice().getDefaultConfiguration();

	/**
	 * メインスクリーンのデバイス設定を取得します。<br>
	 *
	 * @return デバイスの設定。このインスタンスから画像を作成できます。<br>
	 */
	public static GraphicsConfiguration getGraphicsConfiguration() {
		return gc;
	}

	/**
	 * ユーティリティクラスのためインスタンス化できません.
	 */
	private ImageUtil() {
	}

	//------------------------------------------------------------------------------------------------------------
	/**
	 * 新しい空のBufferedImageを生成します. 作成された画像は全てのピクセルが完全に透明な黒(0x00000000)です。<br>
	 *
	 * @param width 画像の幅をピクセル単位で指定します。<br>
	 * @param height 画像の高さをピクセル単位で指定します。<br>
	 *
	 * @return BufferedImageの新しいインスタンスを返します。<br>
	 */
	public static BufferedImage newImage(int width, int height) {
		return gc.createCompatibleImage(width, height, Transparency.TRANSLUCENT);
	}

	/**
	 * BufferedImageの複製を新しいインスタンスとして返します.
	 *
	 * @param src コピーする画像。<br>
	 *
	 * @return srcと同じ画像の新しいインスタンスを返します。<br>
	 */
	public static BufferedImage copy(BufferedImage src) {
		return copy(src, (BufferedImage) null);
	}

	/**
	 * BufferedImageの複製を作成し、dstに格納します.
	 *
	 * @param src コピーする画像。<br>
	 * @param dst nullでない場合このインスタンスに結果が格納される。<br>
	 *
	 * @return nullでない場合、この引数に結果が格納されます。<br>
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
	 * BufferedImageをファイルから作成します.
	 * このメソッドはすでに一度要求された画像を再度要求した場合、同じインスタンスを返します。<br>
	 * 確実に別のインスタンスを取得する場合はこのメソッドの戻り値に対してこのクラスのcopyメソッドを使用してください。<br>
	 *
	 * @param filePath 読み込むファイルパス。<br>
	 *
	 * @return 読み込まれた画像.すでに一度読み込まれている場合はキャッシュデータの同じ画像インスタンスを返す。<br>
	 *
	 * @throws ContentsFileNotFoundException ファイルが存在しない場合に投げられる。<br>
	 * @throws ContentsIOException ファイルがロードできない場合に投げられます。<br>
	 */
	public static BufferedImage load(String filePath) throws FileNotFoundException, ContentsIOException {
		StopWatch watch = new StopWatch().start();
		//GCが実行されているかキャッシュがなければ新しくロードしてキャッシュに追加する
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
		//互換画像に置換
		dst = copy(dst, newImage(dst.getWidth(), dst.getHeight()));
		watch.stop();
		GameLog.printInfoIfUsing("ImageUtil loaded filePath=[" + filePath + "](" + watch.getTime() + " ms)");
		return dst;
	}

	/**
	 * BufferedImageをファイルに保存します. 画像形式は透過PNG画像となります。<br>
	 *
	 * @param filePath 書き込むファイルパス.上書きは確認されず、拡張子も任意。<br>
	 * @param image 書き込む画像。<br>
	 *
	 * @throws ContentsIOException ファイルが書き込めない場合に投げられる。<br>
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
	 * BufferedImageのピクセルデータを配列として取得します.
	 *
	 * @param image ピクセルデータを取得する画像を送信します。<br>
	 *
	 * @return 指定された画像のピクセルデータを一次元配列として返します。 この配列は画像に設定されているピクセルのクローンです。<br>
	 */
	public static int[] getPixel(BufferedImage image) {
		return image.getRGB(0, 0, image.getWidth(), image.getHeight(), null, 0, image.getWidth());
	}

	/**
	 * BufferedImageのピクセルデータを二次元配列として取得します.
	 *
	 * @param image ピクセルデータを取得する画像を送信します。<br>
	 *
	 * @return 指定された画像のピクセルデータを二次元配列として返します。 この配列は画像に設定されているピクセルのクローンです。<br>
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
	 * BufferedImageにピクセルデータを設定します.
	 * このメソッドはピクセル数と画像の実際のピクセル数が異なる場合の動作は定義されていません。<br>
	 *
	 * @param image ピクセルデータを設定する画像。<br>
	 * @param pix 設定するピクセルデータ。<br>
	 */
	public static void setPixel(BufferedImage image, int[] pix) {
		image.setRGB(0, 0, image.getWidth(), image.getHeight(), pix, 0, image.getWidth());
	}

	/**
	 * BufferedImageにピクセルデータを設定します.
	 * このメソッドはピクセル数と画像の実際のピクセル数が異なる場合の動作は定義されていません。<br>
	 *
	 * @param image 画像。<br>
	 * @param pix 設定するピクセルデータ。<br>
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
	 * 画像に書き込むためのグラフィクスコンテキストを作成します.
	 *
	 * @param image グラフィックスコンテキストを取得する画像を指定します。 <br>
	 * @param renderingPolicy nullでない場合、このレンダリング設定がグラフィックスコンテキストに適用されます。<br>
	 *
	 * @return 指定した画像に書き込むためのグラフィックスコンテキストを作成して返します。<br>
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
	 * BudderdImageの0, y からw, hのサイズで横方向に画像を分割し、配列として返します.
	 *
	 * @param src 画像。<br>
	 * @param y Y座標。<br>
	 * @param w 切り出す幅。<br>
	 * @param h 切り出す高さ。<br>
	 *
	 * @return srcを横方向にwの幅で切り出した複数枚の画像。<br>
	 *
	 * @throws RasterFormatException 座標またはサイズが不正な場合に投げられる。<br>
	 */
	public static BufferedImage[] rows(BufferedImage src, int y, int w, int h) throws RasterFormatException {
		BufferedImage[] dst = new BufferedImage[src.getWidth() / w];
		for (int i = 0, x = 0; i < dst.length; i++, x += w) {
			dst[i] = src.getSubimage(x, y, w, h);
		}
		return dst;
	}

	/**
	 * BudderdImageのx, 0 からw, hのサイズで縦方向に画像を分割し、配列として返します.
	 *
	 * @param src 画像。<br>
	 * @param x X座標。<br>
	 * @param w 切り出す幅。<br>
	 * @param h 切り出す高さ。<br>
	 *
	 * @return srcを縦方向にhの高さで切り出した複数枚の画像。<br>
	 *
	 * @throws RasterFormatException 座標またはサイズが不正な場合に投げられる。<br>
	 */
	public static BufferedImage[] columns(BufferedImage src, int x, int w, int h) throws RasterFormatException {
		BufferedImage[] dst = new BufferedImage[src.getHeight() / h];
		for (int i = 0, y = 0; i < dst.length; i++, y += h) {
			dst[i] = src.getSubimage(x, y, w, h);
		}
		return dst;
	}

	/**
	 * BufferedImageの0, 0,からw, hのサイズで二次元に画像を分割し、リストとして返します.
	 *
	 * 返されるリストは1次元で、画像の左上から右方向へ並べられます。<br>
	 *
	 * @param src 画像。<br>
	 * @param w 切り出す幅。<br>
	 * @param h 切り出す高さ。<br>
	 *
	 * @return srcを指定されたサイズで切り出した複数枚の画像。<br>
	 *
	 * @throws RasterFormatException 座標またはサイズが不正な場合に投げられる。<br>
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
	 * BufferedImageの0, 0,からw, hのサイズで二次元に画像を分割し、配列として返します.
	 *
	 * @param src 画像。<br>
	 * @param w 切り出す幅。<br>
	 * @param h 切り出す高さ。<br>
	 *
	 * @return srcを指定されたサイズで切り出した複数枚の画像。<br>
	 *
	 * @throws RasterFormatException 座標またはサイズが不正な場合に投げられる。<br>
	 */
	public static BufferedImage[][] splitAsArray(BufferedImage src, int w, int h) throws RasterFormatException {
		BufferedImage[][] dst = new BufferedImage[src.getHeight() / h][src.getWidth() / w];
		for (int i = 0, y = 0; i < dst.length; i++, y += h) {
			dst[i] = rows(src, y, w, h);
		}
		return dst;
	}

	/**
	 * BufferedImageの0, 0,からw, hのサイズで二次元に画像を分割し、マップとして返します.
	 * 各要素の命名規則は0ベースで[縦の要素番号][横の要素番号]の二けたの数字文字列となります。<br>
	 * ただし要素が2桁に満たない場合は0nのように整形されます。<br>
	 *
	 * @param src 画像。<br>
	 * @param w 切り出す幅。<br>
	 * @param h 切り出す高さ。<br>
	 *
	 * @return srcを指定されたサイズで切り出した複数枚の画像。<br>
	 *
	 * @throws RasterFormatException 座標またはサイズが不正な場合に投げられる。<br>
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
	 * BufferedImageの0, 0,からw, hのサイズで二次元に画像を分割し、マップとして返します.
	 * 各要素の命名規則は0ベースで[縦の要素番号][横の要素番号]のdigitの数字文字列となります。<br>
	 * ただし要素が2桁に満たない場合は0nのように整形されます。<br>
	 *
	 * @param src 画像。<br>
	 * @param w 切り出す幅。<br>
	 * @param h 切り出す高さ。<br>
	 * @param digit 画像命名時のインデックスの桁数。0埋めされる。<br>
	 *
	 * @return srcを指定されたサイズで切り出した複数枚の画像。<br>
	 *
	 * @throws RasterFormatException 座標またはサイズが不正な場合に投げられる。<br>
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
	 * 指定された領域のキャプチャを指定されたファイルに保存します. このメソッドは新しいスレッド[kgf screen
	 * shot]を起動し、そのスレッド内で画像を作成して保存します。<br>
	 * 画像の上書き確認は行われません。強制的に上書きされます。<br>
	 *
	 * @param FILE_PATH ファイルパスを記述します。<br>
	 * @param BOUNDS キャプチャする領域.デバイスのグローバル座標で指定します。<br>
	 *
	 * @throws ContentsIOException 画像が保存できない場合およびスクリーンショットが取得できない場合に 投げられます。<br>
	 */
	public static void screenShot(final String FILE_PATH, final Rectangle BOUNDS) throws ContentsIOException {
		new Thread("kgf screen shot") {
			@Override
			public void run() {
				try {
					BufferedImage image = new Robot().createScreenCapture(BOUNDS);
					ImageUtil.save(FILE_PATH, image);
					GameLog.printInfoIfUsing("スクリーンショットを撮影しました FILE_PATH=[" + FILE_PATH + "]");
				} catch (AWTException ex) {
					throw new ContentsIOException(ex);
				}
			}
		}.start();
	}

	/**
	 * ソース画像を指定された数だけ水平方向に並べた画像を作成します.
	 *
	 * @param src タイリングするソース画像を指定します。この画像のピクセルデータは操作されません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 * @param xNum X方向に並べる数を指定します。<br>
	 * @param yNum Y方向に並べる数を指定します。<br>
	 *
	 * @return ソース画像を2次元に隙間なく並べた画像をdstに格納して返します。<br>
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
	 * ソース画像を指定された数だけ並べた画像を作成します.
	 *
	 * @param src タイリングするソース画像を指定します。この画像のピクセルデータは操作されません。<br>
	 * @param dst nullでない場合、この引数に結果が格納されます。<br>
	 * @param xNum X方向に並べる数を指定します。<br>
	 * @param yNum Y方向に並べる数を指定します。<br>
	 * @param drawWidth 画像を描画する際のサイズを指定します。<br>
	 * @param drawHeight 画像を描画する際のサイズを指定します。<br>
	 * @return ソース画像を2次元に隙間なく並べた画像をdstに格納して返します。<br>
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
	 * 指定した領域の画像を新しいインスタンスとして返します。<br>
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
	 * 1つの画像の透過度をinitialTpからdecTpずつ変更した画像を配列として返します.
	 * このメソッドでは、ソース画像の完全に透明なピクセルはそのまま透明なピクセルとしてコピーされます。<br>
	 *
	 * @param image 透過度を変更するソース画像。<br>
	 * @param initialTp 透過度の初期値です。
	 * @param addTp 透過度に加算する値です。通常は負数を使用します。<br>
	 *
	 * @return ソース画像の、徐々に透過度が変わる画像を配列として返します。<br>
	 *
	 * @throws IllegalArgumentException initailTpが0未満又は1を超えるときに投げられます。<br>
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
	 * 画像配列を水平方向に並べた新しい画像を作成して返します.
	 *
	 * @param images 使用する画像を1つ以上送信します。<br>
	 *
	 * @return imagesをその順番で左から水平方向に隙間なく並べた新しい画像を返します。<br>
	 *
	 * @throws IllegalArgumentException imagesの長さが0のときに投げられます。<br>
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
