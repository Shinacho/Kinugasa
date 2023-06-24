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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.Arrays;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_12:01:37<br>
 * @author Shinacho<br>
 */
public class GraphicsUtil {

	public static Color createColor(List<String> rgba) throws TColorException {
		if (rgba.isEmpty() || rgba.size() <= 2 || rgba.size() >= 5) {
			throw new TColorException(rgba);
		}
		int r = Integer.parseInt(rgba.get(0));
		int g = Integer.parseInt(rgba.get(1));
		int b = Integer.parseInt(rgba.get(2));
		int a = rgba.size() <= 3 ? 255 : Integer.parseInt(rgba.get(3));
		return new Color(r, g, b, a);
	}

	public static Color createColor(String[] rgba) throws TColorException {
		return createColor(Arrays.asList(rgba));

	}

	public static Color randomColor() {
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

//	/**
//	 * Java2DのOpenGLパイプラインを有効化します. 環境によっては、描画パフォーマンスが向上する場合があります。<br>
//	 */
//	public static void useOpenGL() {
//		System.setProperty("sun.java2d.opengl", "true");
//		GameLog.print("> opengl state : [" + System.getProperty("sun.java2d.opengl") + "]");
//	}
//
//	/**
//	 * OpenGLパイプラインを使用しているかを検査します.
//	 *
//	 * @return OpenGLパイプラインを使用している場合は、trueを返します。<br>
//	 */
//	public static boolean isUseOpenGL() {
//		return System.getProperty("sun.java2d.opengl").equals("true");
//	}
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
	 * Rectangle2Dインスタンスを使用して、drawRectを実行します.
	 *
	 * @param g 書き込むグラフィックスコンテキストを指定します。<br>
	 * @param r 描画範囲となるRectangle2Dインスタンスを指定します。<br>
	 */
	public static void drawRect(Graphics2D g, Rectangle2D r) {
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

	public static Color transparent(Color c, int a) {
		return new Color(c.getRed(), c.getGreen(), c.getBlue(), a);
	}
}
