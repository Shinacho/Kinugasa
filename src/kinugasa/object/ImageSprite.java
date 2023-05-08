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
package kinugasa.object;

import java.awt.Image;
import java.awt.image.BufferedImage;
import kinugasa.game.GraphicsContext;
import kinugasa.resource.KImage;

/**
 * 単一の画像を表示する基本スプライトの実装です.
 * <br>
 * 画像スプライトに設定される画像は、SerializableImageにラップされます。<br>
 * この画像型はスプライトごとシリアライズできます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:21:03<br>
 * @version 1.4.0 - 2013/05/05_19:25<br>
 * @author Shinacho<br>
 */
public class ImageSprite extends BasicSprite {

	private KImage image;
	private ImagePainter painter;

	/**
	 * 新しい画像スプライトを作成します. このコンストラクタでは、画像がnullに、 描画モデルがIMAGE_BOUNDS_XYに設定されます。<br>
	 */
	public ImageSprite() {
		super();
		this.image = null;
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * 新しい画像スプライトを作成します. このコンストラクタでは、画像がnullに、 描画モデルがIMAGE_BOUNDS_XYに設定されます。<br>
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h) {
		super(x, y, w, h);
		this.image = null;
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * 新しい画像スプライトを作成します. このコンストラクタでは、 描画モデルはIMAGE_BOUNDS_XYに設定されます。<br>
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KImage image) {
		super(x, y, w, h);
		this.image = image;
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * 新しい画像スプライトを作成します.
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 * @param model 描画方法を指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KImage image, ImagePainter model) {
		super(x, y, w, h);
		this.image = image;
		this.painter = model;
	}

	/**
	 * 新しい画像スプライトを作成します.
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param vector 移動ベクトルを指定します。<br>
	 * @param mm 移動モデルを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 * @param dm 描画モデルを指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KVector vector,
			MovingModel mm, KImage image, ImagePainter dm) {
		super(x, y, w, h, vector, mm);
		this.image = image;
		this.painter = dm;
	}

	/**
	 * 新しい画像スプライトを作成します. このコンストラクタはクローニング用のマスタデータを作成する場合に有用です。<br>
	 *
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param vector 移動ベクトルを指定します。<br>
	 * @param mm 移動モデルを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 * @param dm 描画モデルを指定します。<br>
	 */
	public ImageSprite(float w, float h, KVector vector,
			MovingModel mm, KImage image, ImagePainter dm) {
		super(w, h, vector, mm);
		this.image = image;
		this.painter = dm;
	}

	/**
	 * 新しい画像スプライトを作成します. このコンストラクタでは、 描画モデルはIMAGE_BOUNDS_XYに設定されます。<br>
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h, BufferedImage image) {
		super(x, y, w, h);
		this.image = new KImage(image);
		this.painter = ImagePainterStorage.IMAGE_BOUNDS_XY;
	}

	/**
	 * 新しい画像スプライトを作成します.
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 * @param model 描画方法を指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h, BufferedImage image, ImagePainter model) {
		super(x, y, w, h);
		this.image = new KImage(image);
		this.painter = model;
	}

	/**
	 * 新しい画像スプライトを作成します.
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param vector 移動ベクトルを指定します。<br>
	 * @param mm 移動モデルを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 * @param dm 描画モデルを指定します。<br>
	 */
	public ImageSprite(float x, float y, float w, float h, KVector vector,
			MovingModel mm, BufferedImage image, ImagePainter dm) {
		super(x, y, w, h, vector, mm);
		this.image = new KImage(image);
		this.painter = dm;
	}

	public ImageSprite(float w, float h, BufferedImage image) {
		this(0, 0, w, h, image);
	}

	/**
	 * 新しい画像スプライトを作成します. このコンストラクタはクローニング用のマスタデータを作成する場合に有用です。<br>
	 *
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param vector 移動ベクトルを指定します。<br>
	 * @param mm 移動モデルを指定します。<br>
	 * @param image 表示する画像を指定します。<br>
	 * @param dm 描画モデルを指定します。<br>
	 */
	public ImageSprite(float w, float h, KVector vector,
			MovingModel mm, BufferedImage image, ImagePainter dm) {
		super(w, h, vector, mm);
		this.image = new KImage(image);
		this.painter = dm;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (image == null) {
			return;
		}
		painter.draw(g, this);
	}

	public KImage getImage() {
		return image;
	}

	public Image getAWTImage() {
		return image.get();
	}

	/**
	 * 画像の幅を取得します. 画像のサイズは画像インスタンスに設定されている、ピクセル単位のサイズです。<br>
	 * この値は描画モデルによっては無視され、実際のスプライトのサイズとは違う場合があります。<br>
	 *
	 * @return 画像の幅を返します。<br>
	 */
	public int getImageWidth() {
		return image.getWidth();
	}

	/**
	 * 画像の高さを取得します. 画像のサイズは画像インスタンスに設定されている、ピクセル単位のサイズです。<br>
	 * この値は描画モデルによっては無視され、実際のスプライトのサイズとは違う場合があります。<br>
	 *
	 * @return 画像の高さを返します。<br>
	 */
	public int getImageHeight() {
		return image.getHeight();
	}

	/**
	 * スプライトに表示する画像を設定します.
	 *
	 * @param image
	 */
	public void setImage(BufferedImage image) {
		this.image = new KImage(image);
	}

	public void setImage(KImage image) {
		this.image = image;
	}

	/**
	 * 描画モデルを取得します.
	 *
	 * @return 設定中の描画モデルを返します。<br>
	 */
	public ImagePainter getPainter() {
		return painter;
	}

	/**
	 * 描画モデルを設定します.
	 *
	 * @param painter 設定する描画モデル。<br>
	 */
	public void setPainter(ImagePainter painter) {
		this.painter = painter;
	}

	//Painterのクローンはしなくてもよい
	@Override
	public ImageSprite clone() {
		ImageSprite sprite = (ImageSprite) super.clone();
		//sprite.painter = this.painter.clone();
		return sprite;
	}

	@Override
	public String toString() {
		return "ImageSprite location=[" + getX() + "," + getY() + "] size=["
				+ getWidth() + "," + getHeight() + "] " + "center=["
				+ getCenterX() + "," + getCenterY() + "] personalCenter=["
				+ getPersonalCenterX() + "," + getPersonalCenterY() + "] visible=["
				+ isVisible() + "] exist=[" + isExist() + "] speed=[" + getSpeed() + "] vector=["
				+ getVector() + "] z=[" + getZ() + "] image=[" + getImage() + "] painter=["
				+ getPainter().getName() + "]";
	}
}
