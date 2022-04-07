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
package kinugasa.object;

import kinugasa.game.GraphicsContext;
import kinugasa.graphics.Animation;

/**
 * �摜�z����A�j���[�V�����Ƃ��ĕ\�����邽�߂�
 * ImageSprite�̊g���ł�.
 * <br>
 * imageUpdate�t���O��ON�̂Ƃ��Adraw���\�b�h���Ŏ����I�ɃA�j���[�V�������X�V���܂��B<Br>
 * ���̑��삪�s�v�ȏꍇ�́AimageUpdate��false�ɐݒ肵�AAnimationSprite��update���\�b�h��
 * �R�[�����邱�Ƃŕ\�������摜���X�V�ł��܂��B<br>
 * imageUpdate�̓f�t�H���g�ł�true�ɐݒ肳��Ă��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:49:20<br>
 * @author Dra0211<br>
 */
public class AnimationSprite extends ImageSprite {

	private Animation animation;
	private boolean imageUpdate = true;

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A�摜��null�ɁA�`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 */
	public AnimationSprite() {
		super();
		//image is null
	}

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A�摜��null�ɁA
	 * �`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 */
	public AnimationSprite(float x, float y, float w, float h) {
		super(x, y, w, h);
	}

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A
	 * �`�惂�f����IMAGE_BOUNDS_XY�ɐݒ肳��܂��B<br>
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param animation �\������A�j���[�V�������w�肵�܂��B<br>
	 */
	public AnimationSprite(float x, float y, float w, float h, Animation animation) {
		super(x, y, w, h, animation.getCurrentImage());
		this.animation = animation;
	}

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param animation �\������摜���w�肵�܂��B<br>
	 * @param model �`����@���w�肵�܂��B<br>
	 */
	public AnimationSprite(float x, float y, float w, float h, Animation animation, ImagePainter model) {
		super(x, y, w, h, animation.getCurrentImage(), model);
		this.animation = animation;
	}

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 *
	 * @param x �X�v���C�g��X���W���w�肵�܂��B<br>
	 * @param y �X�v���C�g��Y���W���w�肵�܂��B<br>
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param vector
	 * @param animation
	 * @param mm �ړ����f�����w�肵�܂��B<br>
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
	 */
	public AnimationSprite(float x, float y, float w, float h, KVector vector,
			MovingModel mm, Animation animation, ImagePainter dm) {
		super(x, y, w, h, vector, mm, animation.getCurrentImage(), dm);
		this.animation = animation;
	}

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 * ���̃R���X�g���N�^�̓N���[�j���O�p�̃}�X�^�f�[�^���쐬����ꍇ�ɗL�p�ł��B<br>
	 *
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param vector
	 * @param animation
	 * @param mm �ړ����f�����w�肵�܂��B<br>
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
	 */
	public AnimationSprite(float w, float h, KVector vector,
			MovingModel mm, Animation animation, ImagePainter dm) {
		super(w, h, vector, mm, animation.getCurrentImage(), dm);
		this.animation = animation;
	}

	/**
	 * �V�����A�j���[�V�����X�v���C�g���쐬���܂�.
	 * ���̃R���X�g���N�^�̓N���[�j���O�p�̃}�X�^�f�[�^���쐬����ꍇ�ɗL�p�ł��B<br>
	 * ���Ƀ��b�Z�[�W�E�C���h�E�̃A�C�R���ȂǍ��W���ォ��ݒ肵�A���̌�ړ����邱�Ƃ̂Ȃ�
	 * �X�v���C�g�Ɍ��ʓI�ł��B<br>
	 *
	 * @param w �X�v���C�g�̕����w�肵�܂��B<br>
	 * @param h �X�v���C�g�̍������w�肵�܂��B<br>
	 * @param animation
	 * @param dm �`�惂�f�����w�肵�܂��B<br>
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
	 * �A�j���[�V�������X�V���A�ŐV�̉摜��K�p���܂�.
	 * ���̃��\�b�h�́Adraw���\�b�h�ɂ��`��̊�����A�����I�ɃR�[������܂��B<br>
	 * �ݒ肳��Ă���A�j���[�V������null�̏ꍇ�͉����s���܂���B<br>
	 */
	@Override
	public void update() {
		if (animation == null) {
			return;
		}
		animation.update();
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
