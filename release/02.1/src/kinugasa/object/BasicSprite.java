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

import java.awt.Shape;
import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.movemodel.BasicMoving;
import kinugasa.object.movemodel.CompositeMove;

/**
 * ��{�I�Ȉړ��@�\�����������ASprite�̊g���ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_17:03:44<br>
 * @author Dra0211<br>
 */
public abstract class BasicSprite extends Sprite implements Controllable {

	private TVector vector;
	//
	/** �ړ��A���S���Y��. */
	private MovingModel moving;
	//

	public BasicSprite() {
		super();
		vector = new TVector();
		moving = BasicMoving.getInstance();
	}

	public BasicSprite(float x, float y, float w, float h) {
		this(x, y, w, h, new TVector());
	}

	public BasicSprite(float x, float y, float w, float h, TVector vector) {
		super(x, y, w, h);
		this.vector = vector;
		this.moving = BasicMoving.getInstance();
	}

	public BasicSprite(float x, float y, float w, float h, TVector vector, MovingModel model) {
		super(x, y, w, h);
		this.vector = vector;
		this.moving = model;
	}

	public BasicSprite(float w, float h, TVector vector, MovingModel model) {
		super(0, 0, w, h);
		this.vector = vector;
		this.moving = model;
	}

	public float getAngle() {
		return vector.angle;
	}

	public void setAngle(float angle) {
		vector.angle = angle;
	}

	public float getSpeed() {
		return vector.speed;
	}

	public void setSpeed(float speed) {
		vector.speed = speed;
	}

	/**
	 * �I�u�W�F�N�g�ɐݒ肳��Ă���p�����[�^����уA���S���Y�����g�p���Ĉړ����܂�.
	 */
	public void move() {
		moving.move(this);
		updateCenter();
	}

	/**
	 * �w��̃A���S���Y�����g�p���Ĉړ����܂�.
	 * 
	 * @param m �ړ����@.<br>
	 */
	public void move(MovingModel m) {
		m.move(this);
		updateCenter();
	}

	@Override
	public boolean move(float xValue, float yValue, Shape s) {
		float x = getX() + xValue * vector.speed;
		float y = getY() - yValue * vector.speed;
		if (s == null) {
			setX(x);
			setY(y);
			updateCenter();
			return true;
		}
		if (s.contains(new Point2D.Float(x + getPersonalCenterX(), y + getPersonalCenterY()))) {
			setX(x);
			setY(y);
			updateCenter();
			return true;
		}
		return false;
	}

	@Override
	public boolean move(Point2D.Float p, Shape s) {
		return move(p.x, p.y, s);
	}

	/**
	 * �X�v���C�g��`�悵�܂�.
	 * visible�܂���exist��false�̂Ƃ��A�`�悵�Ă͂Ȃ�܂���.<br>
	 * 
	 * @param g �O���t�B�b�N�X�R���e�L�X�g.<br>
	 */
	@Override
	public abstract void draw(GraphicsContext g);

	/**
	 * ���̃X�v���C�g�����݂̐ݒ�Ŏ��Ɉړ��������̒��S�̍��W��Ԃ��܂�.
	 * <br>
	 * ���̃��\�b�h�́A�ړ����f���ɂ��ړ���i���l�����܂���B<br>
	 * 
	 * @return ���̒��S���W.<br>
	 */
	public Point2D.Float getNextCenter() {
		Point2D.Float p = (Point2D.Float) getCenter().clone();
		p.x += vector.getX();
		p.y += vector.getY();
		return p;
	}

	/**
	 * ���̃X�v���C�g�����݂̐ݒ�Ŏ��Ɉړ��������̍���̍��W��Ԃ��܂�.
	 * <br>
	 * ���̃��\�b�h�́A�ړ����f���ɂ��ړ���i���l�����܂���B<br>
	 * 
	 * @return ���̍��W.<br>
	 */
	public Point2D.Float getNextLocation() {
		Point2D.Float p = getLocation();
		p.x += vector.getX();
		p.y += vector.getY();
		return p;
	}

	public TVector getVector() {
		return vector;
	}

	public void setVector(TVector vector) {
		this.vector = vector;
	}

	/**
	 * �ړ����f�����擾���܂�.
	 * 
	 * @return �ړ����f��.<br>
	 */
	public MovingModel getMovingModel() {
		return moving;
	}

	/**
	 * ���̃X�v���C�g�̈ړ��C�x���g�̂����A�w�肵���N���X�̃C�x���g��Ԃ��܂�.
	 * ���̃��\�b�h�ł́A���̃X�v���C�g�̈ړ��C�x���g��MovingEvent�ł���ꍇ�ɂ�
	 * ���̓������������Ĉړ��C�x���g�̎�����Ԃ��܂��B<br>MovingEvent���擾����ɂ́A
	 * ������MovingEvent�̃N���X���w�肵�܂��B<br>
	 *
	 * @param model �������郂�f���̃N���X�B<br>
	 *
	 * @return �w�肵���N���X�̃C�x���g���܂܂�Ă���ꍇ�ɂ��̃C���X�^���X��Ԃ��B���݂��Ȃ��ꍇ��null��Ԃ��B<br>
	 */
	public MovingModel getMovingModel(Class<? extends MovingModel> model) {
		if (moving instanceof CompositeMove) {
			CompositeMove me = (CompositeMove) moving;
			for (int i = 0; i < me.getModels().length; i++) {
				if (model.isInstance(me.getModels()[i])) {
					return me.getModels()[i];
				}
			}
			return null;
		}
		return (model.isInstance(moving)) ? moving : null;
	}

	/**
	 * �ړ����f����ݒ肵�܂�.
	 * 
	 * @param movingModel �ړ����f��.<br>
	 */
	public void setMovingModel(MovingModel movingModel) {
		this.moving = movingModel;
	}

	/**
	 * ���̃X�v���C�g�̕������쐬���܂�.
	 * ���̃��\�b�h�ł́A�S�Ẵt�B�[���h���N���[�j���O���܂�.<br>
	 * ���̃��\�b�h�̓T�u�N���X�œK�؂ɃI�[�o�[���C�h���Ă�������.<br>
	 * 
	 * @return ���̃X�v���C�g�Ɠ����ݒ�̐V�����C���X�^���X.<br>
	 */
	@Override
	public BasicSprite clone() {
		BasicSprite s = (BasicSprite) super.clone();
		s.vector = this.vector.clone();
		s.moving = this.moving.clone();
		return s;
	}

	/**
	 * �X�v���C�g�̕�����\�L���擾���܂�.
	 * ������ɂ̓X�v���C�g�̃t�B�[���h��񂪊܂܂�Ă��܂�.�����̒l�͂��ׂăA�N�Z�T��ʂ��Ď擾�\�ł�.<br>
	 * 
	 * @return �X�v���C�g�̏��.<br>
	 */
	@Override
	public String toString() {
		return "BasicSprite location=[" + getX() + "," + getY() + "] size=["
				+ getWidth() + "," + getHeight() + "] " + "center=["
				+ getCenterX() + "," + getCenterY() + "] personalCenter=["
				+ getPersonalCenterX() + "," + getPersonalCenterY() + "] visible=["
				+ isVisible() + "] exist=[" + isExist() + "] vector=[" + getVector() + "] "
				+ "z=[" + getZ() + "]";
	}
}
