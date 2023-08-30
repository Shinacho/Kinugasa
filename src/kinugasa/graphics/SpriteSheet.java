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

import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 1つの画像リソースを切り出して、複数の画像インスタンスを構築するためのビルダです.
 * <br>
 * 同一のアルゴリズムで複数のスプライトシートを構築する場合はSpriteSheetCutterを使用してください。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_13:00:09<br>
 * @version 1.1.0 - 2015/06/19_22:39<br>
 * @author Shinacho<br>
 */
public class SpriteSheet {

	/**
	 * 画像を切り出すベースとなる画像です. この画像は変更されません。
	 */
	private BufferedImage baseImage;
	/**
	 * 切り出した画像が追加されるリストです.
	 */
	private ArrayList<BufferedImage> subImages;

	/**
	 * 空のスプライトシートを作成します.
	 */
	public SpriteSheet() {
		subImages = new ArrayList<BufferedImage>(32);
	}

	/**
	 * 新しいスプライトシートを構築します.
	 *
	 * @param filePath ロードする画像のパスを指定します。
	 * このコンストラクタでは、ImageUtilのloadメソッドを使用して画像がロードされます。<br>
	 */
	public SpriteSheet(String filePath) {
		baseImage = ImageUtil.load(filePath);
		subImages = new ArrayList<BufferedImage>(32);
	}

	/**
	 * 新しいスプライトシートを構築します.
	 *
	 * @param baseImage ベースとなる画像を指定します。<br>
	 */
	public SpriteSheet(BufferedImage baseImage) {
		this.baseImage = baseImage;
		subImages = new ArrayList<BufferedImage>(32);
	}

	/**
	 * ベース画像の指定された領域を切り出して新しい画像とします.
	 *
	 * @param x X座標.<br>
	 * @param y Y座標.<br>
	 * @param width 幅.<br>
	 * @param height 高さ.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 *
	 * @throws RasterFormatException 画像の範囲外にアクセスしたときに投げられる.<br>
	 */
	public SpriteSheet cut(int x, int y, int width, int height) throws RasterFormatException {
		subImages.add(baseImage.getSubimage(x, y, width, height));
		return this;
	}

	/**
	 * ベース画像の指定された領域を切り出して新しい画像とします.
	 *
	 * @param rectangle 領域.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 *
	 * @throws RasterFormatException 画像の範囲外にアクセスしたときに投げられる.<br>
	 */
	public SpriteSheet cut(Rectangle rectangle) throws RasterFormatException {
		return cut(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}

	public SpriteSheet resizeAll(float scale) {
		BufferedImage[] result = ImageUtil.resizeAll(subImages.<BufferedImage>toArray(new BufferedImage[subImages.size()]), scale);
		subImages.clear();
		addAll(result);
		return this;
	}

	/**
	 * 切り出しアルゴリズムに基づいて、このシートを切り出します.
	 *
	 * @param cutter 特定の切り出し設定アルゴリズム.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 *
	 * @throws RasterFormatException 画像の範囲外にアクセスしたときに投げられる.<br>
	 */
	public SpriteSheet cut(SpriteSheetCutter cutter) throws RasterFormatException {
		addAll(cutter.cut(baseImage));
		return this;
	}

	/**
	 * 座標0,0からwidth,heightのサイズで二次元に可能な数だけ分割し、全ての部分画像をリストに追加します.
	 *
	 * <br>
	 *
	 * @param width 幅.<br>
	 * @param height 高さ.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 *
	 * @throws RasterFormatException 画像の範囲外にアクセスしたときに投げられる.<br>
	 */
	public SpriteSheet split(int width, int height) throws RasterFormatException {
		BufferedImage[][] images = ImageUtil.splitAsArray(baseImage, width, height);
		for (BufferedImage[] line : images) {
			subImages.addAll(Arrays.asList(line));
		}
		return this;
	}

	/**
	 * 座標0,yからwidth,heightのサイズでX方向に可能な数だけ画像を分割し、全ての部分画像をリストに追加します.
	 *
	 * @param y Y座標.<br>
	 * @param width 幅.<br>
	 * @param height 高さ.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 *
	 * @throws RasterFormatException 画像の範囲外にアクセスしたときに投げられる.<br>
	 */
	public SpriteSheet rows(int y, int width, int height) throws RasterFormatException {
		subImages.addAll(Arrays.asList(ImageUtil.rows(baseImage, y, width, height)));
		return this;
	}

	/**
	 * 座標x,0からwidth,heightのサイズでY方向に可能な数だけ画像を分割し、全ての部分画像をリストに追加します.
	 *
	 * @param x X座標.<br>
	 * @param width 幅.<br>
	 * @param height 高さ.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 *
	 * @throws RasterFormatException 画像の範囲外にアクセスしたときに投げられる.<br>
	 */
	public SpriteSheet columns(int x, int width, int height) throws RasterFormatException {
		subImages.addAll(Arrays.asList(ImageUtil.columns(baseImage, x, width, height)));
		return this;
	}

	/**
	 * 指定された画像を追加します.
	 *
	 * @param image 画像.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 */
	public SpriteSheet add(BufferedImage image) {
		subImages.add(image);
		return this;
	}

	/**
	 * 指定された0個以上の画像を全てその順序でリストに追加します.
	 *
	 * @param images 画像.<br>
	 *
	 * @return このインスタンス自体が返る.<br>
	 */
	public SpriteSheet addAll(BufferedImage... images) {
		subImages.addAll(Arrays.asList(images));
		return this;
	}

	/**
	 * ベース画像本体をリストに追加します.
	 *
	 * @return このインスタンス自体が返る.<br>
	 */
	public SpriteSheet baseImage() {
		subImages.add(baseImage);
		return this;
	}

	/**
	 * 操作を確定し、追加されている全ての画像を追加された順序の配列として取得します.
	 * この操作では、nullインスタンスのsubImageは切り捨てられます。<br>
	 *
	 * @return 追加されている画像の配列.<br>
	 */
	public BufferedImage[] images() {
		subImages.trimToSize();
		int nullIdx = 0;
		for (int size = subImages.size(); nullIdx < size && subImages.get(nullIdx) != null; nullIdx++);
		BufferedImage[] result = new BufferedImage[nullIdx];
		System.arraycopy(subImages.<BufferedImage>toArray(new BufferedImage[subImages.size()]), 0, result, 0, nullIdx);

		return result;
	}

	/**
	 * 操作を確定し、追加されている全ての画像を追加された順序の連番をキーとしたマップとして取得します.
	 *
	 * @return 追加されている画像のマップ。<br>
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
