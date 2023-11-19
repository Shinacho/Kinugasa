/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.RasterFormatException;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
 * @version 1.2.0 - 2023/08/29_22:51<br>
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
	 * ロードした画像をキャッシュするためのマップです.
	 */
	private static final HashMap<String, SoftReference<BufferedImage>> IMAGE_CACHE
			= new HashMap<String, SoftReference<BufferedImage>>(32);

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
		SoftReference<BufferedImage> cacheRef = IMAGE_CACHE.get(filePath);
		//キャッシュあり&GC未実行
		if (cacheRef != null && cacheRef.get() != null) {
			GameLog.print("ImageUtil cached filePath=[" + filePath + "]");
			return cacheRef.get();
		}
		//GCが実行されているかキャッシュがなければ新しくロードしてキャッシュに追加する
		File file = new File(filePath);
		if (!file.exists()) {
			watch.stop();
			throw new FileNotFoundException("notfound : filePath=[" + filePath + "]");
		}
		BufferedImage dst = null;
		try {
			dst = ImageIO.read(file);
		} catch (IOException ex) {
			watch.stop();
			GameLog.print("cant load filePath=[" + filePath + "]");
			throw new ContentsIOException(ex);
		}
		if (dst == null) {
			watch.stop();
			GameLog.print("image is null filePath=[" + filePath + "]");
			throw new ContentsIOException("image is null");
		}
		//互換画像に置換
		dst = copy(dst, newImage(dst.getWidth(), dst.getHeight()));
		IMAGE_CACHE.put(filePath, new SoftReference<BufferedImage>(dst));
		watch.stop();
		GameLog.print("ImageUtil loaded filePath=[" + filePath + "](" + watch.getTime() + " ms)");
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
					GameLog.print("ScreenShot FILE_PATH=[" + FILE_PATH + "]");
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
			src = transparent(src, tp, null);
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
	 * srcをグレイスケール変換した画像をdstに格納して返します. このメソッドはピクセルのRGB平均をそのまま設定します。
	 * NTSC系加重平均法は使用しません。<br>
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
		for (int y = 0, imageHeight = src.getHeight(); y < imageHeight; y += size) {
			for (int x = 0, imageWidth = src.getWidth(); x < imageWidth; x += size) {
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
	 * @return srcを回転した画像を返します。degが1のときはsrcのコピーが返されます。回転時に画像が領域の
	 * 外に出る場合、空いた領域は黒(Color.BLACK)となります。<br>
	 */
	public static BufferedImage rotate(BufferedImage src, float deg, BufferedImage dst) {
		if (deg == 0) {
			return dst = copy(src);
		}
		if (dst == null || dst == src) {
			dst = newImage(src.getWidth(), src.getHeight());
		}
		Graphics2D g = createGraphics2D(dst, RenderingQuality.QUALITY);
		g.setClip(0, 0, dst.getWidth(), dst.getHeight());
		g.setColor(new Color(ARGBColor.CLEAR_BLACK, true));
		g.fillRect(0, 0, dst.getWidth(), dst.getHeight());
		g.rotate(Math.toRadians(deg), dst.getWidth() / 2, dst.getHeight() / 2);
		g.drawImage(src, 0, 0, null);
		g.dispose();
		return dst;
	}

	/**
	 * ソース画像の全てのピクセルの透過度にtpを加算した画像を返します.
	 * このメソッドではピクセルの透過度が0未満又は255を超える場合は有効範囲内に切り詰められます。<br>
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
		Graphics2D g2 = createGraphics2D(dst, RenderingQuality.QUALITY);
		g2.drawImage(src, 0, 0, newWidth, newHeight, null);
		g2.dispose();
		return dst;
	}

	public static BufferedImage resize(BufferedImage src, float wScale, float hScale) {
		if (wScale == 1f && hScale == 1f) {
			return copy(src);
		}
		if (wScale == 0 || hScale == 0) {
			throw new IllegalArgumentException("IU scale is 0. Check the parameter is not int.");
		}
		int newWidth = (int) (src.getWidth() * wScale);
		int newHeight = (int) (src.getHeight() * hScale);
		BufferedImage dst = newImage(newWidth, newHeight);
		Graphics2D g2 = createGraphics2D(dst, RenderingQuality.QUALITY);
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

	public static BufferedImage reverseColorByMaskedArea(BufferedImage src, BufferedImage mask) {
		BufferedImage res = src;
		float wScale = mask.getWidth() / res.getWidth();
		float hScale = mask.getHeight() / res.getHeight();
		BufferedImage maskImage = resize(mask, wScale, hScale);
		int[][] pix2 = ImageUtil.getPixel2D(maskImage);
		int[][] pix = ImageUtil.getPixel2D(res);

		for (int y = 0; y < pix.length; y++) {
			for (int x = 0; x < pix[y].length; x++) {
				if (ARGBColor.getAlpha(pix2[y][x]) != 0) {
					pix[y][x] = ARGBColor.reverse(pix[y][x]);
				}
			}
		}

		ImageUtil.setPixel2D(res, pix);
		return res;
	}
}
