/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.game.GraphicsContext;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_12:01:37<br>
 * @author Dra211<br>
 */
public class GraphicsUtil {

	public static Color createColor(List<String> rgba) throws TColorException {
		if (rgba.isEmpty() || rgba.size() <= 2 || rgba.size() >= 5) {
			throw new TColorException(rgba);
		}
		int r = Integer.parseInt(rgba.get(0));
		int g = Integer.parseInt(rgba.get(1));
		int b = Integer.parseInt(rgba.get(2));
		int a = rgba.size() <=3 ? 255 : Integer.parseInt(rgba.get(3));
		return new Color(r, g, b, a);
	}

	public static Color createColor(String[] rgba) throws TColorException {
		return createColor(Arrays.asList(rgba));

	}
	
	public static Color randomColor(){
		int r = Random.randomAbsInt(256);
		int g = Random.randomAbsInt(256);
		int b = Random.randomAbsInt(256);
		int a = 255;
		return new Color(r, g, b, a);
	}

	/**
	 * インスタンス化できません.
	 */
	private GraphicsUtil() {
	}

	/**
	 * Java2DのOpenGLパイプラインを有効化します.
	 * 環境によっては、描画パフォーマンスが向上する場合があります。<br>
	 */
	public static void useOpenGL() {
		System.setProperty("sun.java2d.opengl", "true");
		GameLog.printInfoIfUsing( "> opengl state : [" + System.getProperty("sun.java2d.opengl") + "]");
	}

	/**
	 * OpenGLパイプラインを使用しているかを検査します.
	 *
	 * @return OpenGLパイプラインを使用している場合は、trueを返します。<br>
	 */
	public static boolean isUseOpenGL() {
		return System.getProperty("sun.java2d.opengl").equals("true");
	}

	/**
	 * Rectangle2Dインスタンスを使用して、clearRectを実行します.
	 *
	 * @param g 書き込むグラフィックスコンテキストを指定します。<br>
	 * @param r 描画範囲となるRectangle2Dインスタンスを指定します。<br>
	 */
	public static void clearRect(GraphicsContext g, Rectangle2D r) {
		g.clearRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Rectangle2Dインスタンスを使用して、drawRectを実行します.
	 *
	 * @param g 書き込むグラフィックスコンテキストを指定します。<br>
	 * @param r 描画範囲となるRectangle2Dインスタンスを指定します。<br>
	 */
	public static void drawRect(GraphicsContext g, Rectangle2D r) {
		g.drawRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Rectangle2Dインスタンスを使用して、fillRectを実行します.
	 *
	 * @param g 書き込むグラフィックスコンテキストを指定します。<br>
	 * @param r 描画範囲となるRectangle2Dインスタンスを指定します。<br>
	 */
	public static void fillRect(GraphicsContext g, Rectangle2D r) {
		g.fillRect((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Rectangle2Dインスタンスを使用して、drawOvalを実行します.
	 *
	 * @param g 書き込むグラフィックスコンテキストを指定します。<br>
	 * @param r 描画範囲となるRectangle2Dインスタンスを指定します。<br>
	 */
	public static void drawOval(GraphicsContext g, Rectangle2D r) {
		g.drawOval((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}

	/**
	 * Rectangle2Dインスタンスを使用して、fillOvalを実行します.
	 *
	 * @param g 書き込むグラフィックスコンテキストを指定します。<br>
	 * @param r 描画範囲となるRectangle2Dインスタンスを指定します。<br>
	 */
	public static void fillOval(GraphicsContext g, Rectangle2D r) {
		g.fillOval((int) r.getX(), (int) r.getY(), (int) r.getWidth(), (int) r.getHeight());
	}
}
