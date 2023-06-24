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
package kinugasa.object;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.resource.Storage;
import kinugasa.resource.KImage;

/**
 * .
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:32:04<br>
 * @author Shinacho<br>
 */
public final class ImagePainterStorage extends Storage<ImagePainter> {

	/**
	 * このモデルは何も描画しません.
	 */
	public static final ImagePainter NOT_DRAW = new ImagePainter("NOT_DRAW") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			return;
		}
	};
	/**
	 * このモデルはスプライトの座標に画像を画像のサイズで描画します. したがって、画像の位置はスプライトの領域の”左上”に固定されます。<br>
	 * このモデルはもっとも高速に動作するため、スプライトと画像のサイズが一致する場合に有用です。<br>
	 */
	public static final ImagePainter IMAGE_BOUNDS_XY = new ImagePainter("IMAGE_BOUNDS_XY") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.drawImage(spr.getImage(), (int) spr.getX(), (int) spr.getY());
		}
	};
	/**
	 * このモデルはスプライトの中心と画像の中心が重なる位置に画像のサイズで描画します.
	 */
	public static final ImagePainter IMAGE_BOUNDS_CENTER = new ImagePainter("IMAGE_BOUNDS_CENTER") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.drawImage(spr.getImage(),
					(int) (spr.getCenterX() - spr.getImageWidth() / 2),
					(int) (spr.getCenterY() - spr.getImageHeight() / 2));
		}
	};
	/**
	 * このモデルは画像のサイズをスプライトのサイズに拡大し、スプライトの領域を埋めるように描画します.
	 */
	public static final ImagePainter SPRITE_BOUNDS = new ImagePainter("SPRITE_BOUNDS") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.drawImage(spr.getImage(), (int) spr.getX(), (int) spr.getY(),
					(int) spr.getWidth(), (int) spr.getHeight());
		}
	};
	/**
	 * このモデルはスプライトの移動角度に沿って画像を回転してから、IMAGE_BOUNDS_XYで描画します.
	 */
	public static final ImagePainter IMAGE_BOUNDS_XY_ROTATE = new ImagePainter("IMAGE_BOUNDS_XY_ROTATE") {
		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			Graphics2D g2 = g.create();
			g2.setClip(spr.getBounds());
			g2.rotate(spr.getVector().getAngleAsRad(), spr.getCenterX(), spr.getCenterY());
			g2.drawImage(spr.getImage().asImage(), (int) spr.getX(), (int) spr.getY(), null);
			g2.dispose();
		}
	};
	/**
	 * このモデルはスプライトの移動角度に沿って画像を回転してから、IMAGE_BOUNDS_CENTERで描画します.
	 */
	public static final ImagePainter IMAGE_BOUNDS_CENTER_ROTATE = new ImagePainter("IMAGE_BOUNDS_CENTER_ROTATE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			Graphics2D g2 = g.create();
			g2.setClip(spr.getBounds());
			g2.rotate(spr.getVector().getAngleAsRad(), spr.getCenterX(), spr.getCenterY());
			g2.drawImage(spr.getImage().asImage(),
					(int) (spr.getCenterX() - spr.getImageWidth() / 2),
					(int) (spr.getCenterY() - spr.getImageHeight() / 2), null);
			g2.dispose();
		}
	};
	/**
	 * このモデルはスプライトの移動角度に沿って画像を回転してから、SPRITE_BOUNDSで描画します.
	 */
	public static final ImagePainter SPRITE_BOUNDS_ROTATE = new ImagePainter("SPRITE_BOUNDS_ROTATE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			Graphics2D g2 = g.create();
			g2.setClip(spr.getBounds());
			g2.rotate(spr.getVector().getAngleAsRad(), spr.getCenterX(), spr.getCenterY());
			g2.drawImage(spr.getImage().asImage(), (int) spr.getX(), (int) spr.getY(),
					(int) spr.getWidth(), (int) spr.getHeight(), null);
			g2.dispose();

		}
	};
	/**
	 * このモデルは、スプライトの領域と当たり判定領域をそれぞれ矩形で描画し、可視化します.
	 * 領域の色はColor.GREENが、当たり判定の領域はColor.REDが使用されます。<br>
	 */
	public static final ImagePainter DEBUG_SPRITE_BOUNDS = new ImagePainter("DEBUG_SPRITE_BOUNDS") {

		@Override
		public void draw(GraphicsContext g, ImageSprite spr) {
			g.setColor(Color.GREEN);
			g.draw((List<Drawable>) spr.getBounds());
			g.setColor(Color.RED);
			g.draw((List<Drawable>) spr.getHitBounds());
		}
	};
	/**
	 * グラフィックスコンテキストのクリッピング領域に 等倍の画像を二次元に隙間なく並べて描画します. このモデルでの描画は非常に効率が悪いため、
	 * タイリングした画像をあらかじめ構築しておき、別のモデルによって 描画することを推奨します。<br>
	 */
	public static final ImagePainter TITLING_CPLI_AREA = new ImagePainter("TILING_IMAGE_SIZE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite sprite) {
			float minX = sprite.getX();
			float minY = sprite.getY();
			for (; minX >= 0; minX -= sprite.getImageWidth());
			for (; minY >= 0; minY -= sprite.getImageHeight());
			KImage image = sprite.getImage();
			float totalHeight = -minY + g.getClipBounds().height;
			float totalWidth = -minX + g.getClipBounds().width;
			for (float y = minY; y < totalHeight; y += image.getHeight()) {
				for (float x = minX; x < totalWidth; x += image.getWidth()) {
					g.drawImage(image.get(), (int) x, (int) y);
				}
			}
		}
	};
	/**
	 * スプライトの領域に等倍の画像を二次元に隙間なく並べて描画します. このモデルでの描画は非常に効率が悪いため、
	 * タイリングした画像をあらかじめ構築しておき、別のモデルによって 描画することを推奨します。<br>
	 */
	public static final ImagePainter TILING_SPRITE_SIZE = new ImagePainter("TILING_SPRITE_SIZE") {

		@Override
		public void draw(GraphicsContext g, ImageSprite sprite) {
			float minX = sprite.getX();
			float minY = sprite.getY();
			for (; minX >= 0; minX -= sprite.getImageWidth());
			for (; minY >= 0; minY -= sprite.getImageHeight());
			KImage image = sprite.getImage();
			float totalHeight = -minY + sprite.getHeight();
			float totalWidth = -minX + sprite.getWidth();
			for (float y = minY; y < totalHeight; y += image.getHeight()) {
				for (float x = minX; x < totalWidth; x += image.getWidth()) {
					g.drawImage(image.get(), (int) x, (int) y);
				}
			}
		}
	};
	private static final long serialVersionUID = 2147787454213377482L;

	/**
	 * インスタンスを取得します.
	 *
	 * @return このクラスの唯一のインスタンスを返します。<br>
	 */
	public static ImagePainterStorage getInstance() {
		return INSTANCE;
	}

	/**
	 * シングルトンクラスです.
	 */
	private ImagePainterStorage() {
		super();
		add(NOT_DRAW);
		add(IMAGE_BOUNDS_XY);
		add(IMAGE_BOUNDS_XY_ROTATE);
		add(IMAGE_BOUNDS_CENTER);
		add(IMAGE_BOUNDS_CENTER_ROTATE);
		add(SPRITE_BOUNDS);
		add(SPRITE_BOUNDS_ROTATE);
		add(DEBUG_SPRITE_BOUNDS);
		add(TILING_SPRITE_SIZE);
		add(TITLING_CPLI_AREA);
	}
	/**
	 * このクラスの唯一のインスタンスです.
	 */
	private static final ImagePainterStorage INSTANCE = new ImagePainterStorage();
}
