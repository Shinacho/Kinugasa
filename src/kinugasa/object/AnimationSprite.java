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

import kinugasa.game.GraphicsContext;
import kinugasa.graphics.Animation;
import kinugasa.resource.Nameable;

/**
 * 画像配列をアニメーションとして表示するための ImageSpriteの拡張です.
 * <br>
 * imageUpdateフラグがONのとき、drawメソッド内で自動的にアニメーションを更新します。<Br>
 * この操作が不要な場合は、imageUpdateをfalseに設定し、AnimationSpriteのupdateメソッドを
 * コールすることで表示される画像を更新できます。<br>
 * imageUpdateはデフォルトではtrueに設定されています。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:49:20<br>
 * @author Shinacho<br>
 */
public class AnimationSprite extends ImageSprite{

	private String id;
	private Animation animation;
	private boolean imageUpdate = true;

	/**
	 * 新しいアニメーションスプライトを作成します.
	 * このコンストラクタでは、画像がnullに、描画モデルがIMAGE_BOUNDS_XYに設定されます。<br>
	 */
	public AnimationSprite() {
		super();
		//image is null
	}

	public AnimationSprite(Animation animation) {
		this.animation = animation;
	}

	/**
	 * 新しいアニメーションスプライトを作成します. このコンストラクタでは、画像がnullに、
	 * 描画モデルがIMAGE_BOUNDS_XYに設定されます。<br>
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 */
	public AnimationSprite(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	/**
	 * 新しいアニメーションスプライトを作成します. このコンストラクタでは、 描画モデルはIMAGE_BOUNDS_XYに設定されます。<br>
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param animation 表示するアニメーションを指定します。<br>
	 */
	public AnimationSprite(float x, float y, float w, float h, Animation animation) {
		super(x, y, w, h, animation.getCurrentImage());
		this.animation = animation;
	}

	/**
	 * 新しいアニメーションスプライトを作成します.
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param animation 表示する画像を指定します。<br>
	 * @param model 描画方法を指定します。<br>
	 */
	public AnimationSprite(float x, float y, float w, float h, Animation animation, ImagePainter model) {
		super(x, y, w, h, animation.getCurrentImage(), model);
		this.animation = animation;
	}

	/**
	 * 新しいアニメーションスプライトを作成します.
	 *
	 * @param x スプライトのX座標を指定します。<br>
	 * @param y スプライトのY座標を指定します。<br>
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param vector
	 * @param animation
	 * @param mm 移動モデルを指定します。<br>
	 * @param dm 描画モデルを指定します。<br>
	 */
	public AnimationSprite(float x, float y, float w, float h, KVector vector,
			MovingModel mm, Animation animation, ImagePainter dm) {
		super(x, y, w, h, vector, mm, animation.getCurrentImage(), dm);
		this.animation = animation;
	}

	/**
	 * 新しいアニメーションスプライトを作成します. このコンストラクタはクローニング用のマスタデータを作成する場合に有用です。<br>
	 *
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param vector
	 * @param animation
	 * @param mm 移動モデルを指定します。<br>
	 * @param dm 描画モデルを指定します。<br>
	 */
	public AnimationSprite(float w, float h, KVector vector,
			MovingModel mm, Animation animation, ImagePainter dm) {
		super(w, h, vector, mm, animation.getCurrentImage(), dm);
		this.animation = animation;
	}

	/**
	 * 新しいアニメーションスプライトを作成します. このコンストラクタはクローニング用のマスタデータを作成する場合に有用です。<br>
	 * 特にメッセージウインドウのアイコンなど座標を後から設定し、その後移動することのない スプライトに効果的です。<br>
	 *
	 * @param w スプライトの幅を指定します。<br>
	 * @param h スプライトの高さを指定します。<br>
	 * @param animation
	 * @param dm 描画モデルを指定します。<br>
	 */
	public AnimationSprite(float w, float h, Animation animation, ImagePainter dm) {
		super(0, 0, w, h, animation.getCurrentImage(), dm);
		this.animation = animation;
	}

	public Animation getAnimation() {
		return animation;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setImageUpdate(boolean imageUpdate) {
		this.imageUpdate = imageUpdate;
	}

	public boolean isImageUpdate() {
		return imageUpdate;
	}

	/**
	 * アニメーションを更新し、最新の画像を適用します. このメソッドは、drawメソッドによる描画の完了後、自動的にコールされます。<br>
	 * 設定されているアニメーションがnullの場合は何も行いません。<br>
	 */
	@Override
	public void update() {
		if (animation == null) {
			return;
		}
		animation.update();
		if (!animation.isRepeat() && animation.isEnded()) {
			setVisible(false);
		}
		setImage(animation.getCurrentImage());
	}

	@Override
	public void draw(GraphicsContext g) {
		super.draw(g);
		if (imageUpdate & isVisible() & isExist()) {
			update();
		}
	}

	@Override
	public AnimationSprite clone() {
		AnimationSprite result = (AnimationSprite) super.clone();
		if (this.animation != null) {
			result.animation = this.animation.clone();
		}
		return result;
	}

	@Override
	public String toString() {
		return "ImageSprite location=[" + getX() + "," + getY() + "] size=["
				+ getWidth() + "," + getHeight() + "] " + "center=["
				+ getCenterX() + "," + getCenterY() + "] personalCenter=["
				+ getPersonalCenterX() + "," + getPersonalCenterY() + "] visible=["
				+ isVisible() + "] exist=[" + isExist() + "] speed=[" + getSpeed() + "] vector=["
				+ getVector() + "] z=[" + getZ() + "] image=[" + getImage() + "] drawingModel=["
				+ getPainter().getName() + "] animationImage=[" + animation + "]";
	}
}
