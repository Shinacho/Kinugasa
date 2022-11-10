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

import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.game.GraphicsContext;
import kinugasa.resource.Nameable;

/**
 * �Q�[���ɕ\������鎩�@��L�����N�^�̊��N���X�ł�.
 * <br>
 * �X�v���C�g�̂��ׂẴT�u�N���X�ł́A�N���[����K�؂ɃI�[�o�[���C�h����K�v������܂��B<br>
 * <br>
 *
 * @version 3.0 : 12/01/20_18:30-<br>
 * @version 4.0 : 12/02/06_21:00-<br>
 * @version 5.0 : 12/02/18_18:55-21:45<br>
 * @version 6.0.0 - 2012/06/02_16:46:58.<br>
 * @version 6.14.0 - 2012/06/12_20:17.<br>
 * @version 6.18.0 - 2012/06/16_02:26.<br>
 * @version 6.20.0 - 2012/06/18_21:53.<br>
 * @version 6.23.0 - 2012/07/01_00:48.<br>
 * @version 6.28.0 - 2012/07/07_00:37.<br>
 * @version 6.40.0 - 2012/07/07_17:24.<br>
 * @version 6.40.2 - 2012/07/07_22:45.<br>
 * @version 7.0.0 - 2012/07/14_16:41:11.<br>
 * @version 7.0.1 - 2012/07/21_23:42.<br>
 * @version 7.1.1 - 2012/09/23_01:58.<br>
 * @version 7.1.3 - 2012/11/12_00:54.<br>
 * @version 7.5.0 - 2012/11/21_11:39.<br>
 * @version 8.0.0 - 2013/01/14_16:52:02<br>
 * @version 8.3.0 - 2013/02/10_02:42<br>
 * @version 8.4.0 - 2013/04/28_21:54<br>
 * @version 8.5.0 - 2015/01/05_21:14<br>
 * @version 8.6.0 - 2015/03/29_16:26<br>
 * @version 8.7.0 - 2022/11/10_19:08<br>
 * @author Dra0211<br>
 */
public abstract class Sprite
		implements Drawable, Shapeable, Cloneable, Comparable<Sprite> {

	/**
	 * �̈�.
	 */
	private Rectangle2D.Float bounds;
	/**
	 * ���S���W�̃L���b�V��.
	 */
	private Point2D.Float center = null;
	/**
	 * ���Β��S���W.
	 */
	private Point2D.Float personalCenter = null;
	/**
	 * Z���[�x.
	 */
	private float z = 0f;
	/**
	 * �����.
	 */
	private boolean visible = true;
	/**
	 * �������.
	 */
	private boolean exist = true;

	/**
	 * ���S���W�̃L���b�V�����쐬���܂�.
	 */
	private void setCenter() {
		center = new Point2D.Float(bounds.x + bounds.width / 2,
				bounds.y + bounds.height / 2);
		personalCenter = new Point2D.Float(bounds.width / 2, bounds.height / 2);
	}

	/**
	 * ���S���W�̃L���b�V�����X�V���܂�.
	 */
	protected final void updateCenter() {
		center.x = bounds.x + personalCenter.x;
		center.y = bounds.y + personalCenter.y;
	}

	/**
	 * ���S���W�̃L���b�V�����X�V���܂�.
	 */
	protected final void updatePersonalCenter() {
		personalCenter.x = bounds.width / 2;
		personalCenter.y = bounds.height / 2;
	}

	/**
	 * �V�����X�v���C�g���쐬���܂�. �S�Ẵt�B�[���h������������܂�.<br>
	 */
	public Sprite() {
		bounds = new Rectangle2D.Float();
		setCenter();
	}

	/**
	 * �V�����X�v���C�g���쐬���܂�. ���̃R���X�g���N�^�ł́A�f�B�[�v�R�s�[�ɋ߂��A�Q�Ƃ𗘗p�����C���X�^���X�̍쐬���s�����Ƃ��ł��܂�.<br>
	 *
	 * @param bounds ���̃X�v���C�g�̗̈�.<br>
	 */
	private Sprite(Rectangle2D.Float bounds) {
		this.bounds = bounds;
		z = 0;
		setCenter();
	}

	/**
	 * �ʒu����уT�C�Y���w�肵�ăX�v���C�g���쐬���܂�.
	 *
	 * @param x X���W.<br>
	 * @param y Y���W.<br>
	 * @param w ��.<br>
	 * @param h ����.<br>
	 */
	public Sprite(float x, float y, float w, float h) {
		this(x, y, w, h, 0);
	}

	/**
	 * �ʒu����уT�C�Y���w�肵�ăX�v���C�g���쐬���܂�.
	 *
	 * @param x X���W.<br>
	 * @param y Y���W.<br>
	 * @param w ��.<br>
	 * @param h ����.<br>
	 * @param z
	 */
	public Sprite(float x, float y, float w, float h, float z) {
		this.bounds = new Rectangle2D.Float(x, y, w, h);
		this.z = z;
		setCenter();
	}

	/**
	 * �X�v���C�g��`�悵�܂�. visible�܂���exist��false�̂Ƃ��A�`�悵�Ă͂Ȃ�܂���.<br>
	 *
	 * @param g �O���t�B�b�N�X�R���e�L�X�g.<br>
	 */
	@Override
	public abstract void draw(GraphicsContext g);

	/**
	 * �X�v���C�g�̗l�X�ȃv���p�e�B���X�V���܂�. ���̃��\�b�h�́A�I�[�o�[���C�h���Ȃ�����A�����s���܂���B<br>
	 * �ړ��Ƃ͕ʂɁA��Ԃ��X�V����K�v������ꍇ�A�I�[�o�[���C�h���邱�Ƃ� �������`�ł��܂��B<br>
	 */
	public void update() {
	}

	/**
	 * ���̃X�v���C�g�̗̈���擾���܂�. ���̃��\�b�h�ł̓N���[�����Ԃ���܂�.<br>
	 *
	 * @return �X�v���C�g�̗̈�.<br>
	 */
	public Rectangle2D.Float getBounds() {
		return (Rectangle2D.Float) bounds.clone();
	}

	/**
	 * ���̃X�v���C�g�́h�����蔻��h�̗̈��Ԃ��܂�. ���̃��\�b�h�̓X�v���C�g�����̃X�v���C�g���Փ˂��Ă��邩��������ꍇ��
	 * �X�v���C�g���܂����`�Ƙ_���I�ȏՓˏ�Ԃ���ʂ��邽�߂ɐ݂����Ă��܂��B<br>
	 * ���̃��\�b�h�̓f�t�H���g�ł́AgetBounds()�Ɠ����l��Ԃ��܂��B<br>
	 * ���̃��\�b�h���g�p����ꍇ�͓K�؂ɃI�[�o�[���C�h���Ă��������B<br>
	 *
	 * @return �X�v���C�g�́h�����蔻��h�̗̈��Ԃ��܂��B�I�[�o�[���C�h���Ȃ��ꍇ��getBounds()��Ԃ��܂��B<br>
	 */
	public Rectangle2D.Float getHitBounds() {
		return (Rectangle2D.Float) bounds.clone();
	}

	/**
	 * ���̃X�v���C�g�̗̈��ݒ肵�܂�.
	 *
	 * @param bounds �X�v���C�g�̗̈�.<br>
	 */
	public void setBounds(Rectangle2D.Float bounds) {
		this.bounds = bounds;
		updatePersonalCenter();
		updateCenter();
	}

	/**
	 * ���̃X�v���C�g�̗̈��ݒ肵�܂�.
	 *
	 * @param location �ʒu���w�肵�܂��B<br>
	 * @param width ���ł��B<br>
	 * @param height �����ł��B<br>
	 */
	public void setBounds(Point2D.Float location, float width, float height) {
		setBounds(new Rectangle2D.Float(location.x, location.y, width, height));
	}

	/**
	 * ���̃X�v���C�g�̗̈��ݒ肵�܂�.
	 *
	 * @param x X�ʒu�ł��B<br>
	 * @param y Y�ʒu�ł��B<br>
	 * @param width ���ł��B<br>
	 * @param height �����ł��B<br>
	 */
	public void setBounds(float x, float y, float width, float height) {
		setBounds(new Rectangle2D.Float(x, y, width, height));
	}

	//�K�v�ł���΁AhitBounds�ɂ�锻��ɃI�[�o�[���C�h�ł���
	@Override
	public boolean contains(Point2D point) {
		return bounds.contains(point);
	}

	/**
	 * �X�v���C�g�̍���̈ʒu���擾���܂�. ���̃��\�b�h�͐V�����C���X�^���X��Ԃ��܂�.<br>
	 *
	 * @return ����̈ʒu.<br>
	 */
	public Point2D.Float getLocation() {
		return new Point2D.Float(bounds.x, bounds.y);
	}

	/**
	 * �X�v���C�g�̍���̈ʒu��ݒ肵�܂�.
	 *
	 * @param location ����̈ʒu.<br>
	 */
	public void setLocation(Point2D.Float location) {
		setLocation(location.x, location.y);
	}

	/**
	 * �X�v���C�g�̍���̈ʒu��ݒ肵�܂�.
	 *
	 * @param x X���W.<br>
	 * @param y Y���W.<br>
	 */
	public void setLocation(float x, float y) {
		setX(x);
		setY(y);
	}

	/**
	 * �X�v���C�g�̒��S�̍��W���擾���܂�. ���̃��\�b�h�ł̓N���[�����Ԃ���܂�.<br>
	 *
	 * @return �X�v���C�g�̒��S�̍��W.�E�C���h�E��ł̐�΍��W.<Br>
	 */
	public Point2D.Float getCenter() {
		return (Point2D.Float) center.clone();
	}

	/**
	 * �X�v���C�g�̒��S��X���W���擾���܂�.
	 *
	 * @return �X�v���C�g�̒��S��X���W�B�E�C���h�E��ł̍��W��Ԃ��܂��B<br>
	 */
	public float getCenterX() {
		return center.x;
	}

	/**
	 * �X�v���C�g�̒��S��Y���W���擾���܂�.
	 *
	 * @return �X�v���C�g�̒��S��Y���W�B�E�C���h�E��ł̍��W��Ԃ��܂��B<br>
	 */
	public float getCenterY() {
		return center.y;
	}

	/**
	 * �X�v���C�g�̒��S�̑��ΓI��X���W���擾���܂�.
	 *
	 * @return �X�v���C�g�̒��S��X���W�B�X�v���C�g�̃T�C�Y�ɑ΂��钆�S�̍��W��Ԃ��܂��B<br>
	 */
	public float getPersonalCenterX() {
		return personalCenter.x;
	}

	/**
	 * �X�v���C�g�̒��S�̑��ΓI��Y���W���擾���܂�.
	 *
	 * @return �X�v���C�g�̒��S��Y���W�B�X�v���C�g�̃T�C�Y�ɑ΂��钆�S�̍��W��Ԃ��܂��B<br>
	 */
	public float getPersonalCenterY() {
		return personalCenter.y;
	}

	/**
	 * �X�v���C�g�̒��S�̑��΍��W���擾���܂�. ���Β��S���W�Ƃ̓X�v���C�g�̗̈�̍��ォ��̒��S�܂ł̋����ł�.<br>
	 * ���̃��\�b�h�ł̓N���[�����Ԃ���܂�.<br>
	 *
	 * @return ���S�̑��΍��W.<br>
	 */
	public Point2D.Float getPersonalCenter() {
		return (Point2D.Float) personalCenter.clone();
	}

	/**
	 * �X�v���C�g�̃T�C�Y���擾���܂�. �T�C�Y��int���x�Ɋۂ߂��܂�.<br>
	 * ���̃��\�b�h�͐V�����C���X�^���X��Ԃ��܂�.<br>
	 *
	 * @return �X�v���C�g�̃T�C�Y.<br>
	 */
	public Dimension getSize() {
		return new Dimension((int) bounds.width, (int) bounds.height);
	}

	/**
	 * �X�v���C�g�̃T�C�Y���擾���܂�. �T�C�Y��int���x�Ɋۂ߂��܂�.<br>
	 *
	 * @param size �X�v���C�g�̃T�C�Y.<br>
	 */
	public void setSize(Dimension size) {
		setSize(size.width, size.height);
	}

	/**
	 * �X�v���C�g�̃T�C�Y���擾���܂�.
	 *
	 * @param w �X�v���C�g�̕�.<br>
	 * @param h �X�v���C�g�̍���.<br>
	 */
	public void setSize(float w, float h) {
		bounds.width = w;
		bounds.height = h;
		updatePersonalCenter();
		updateCenter();
	}

	/**
	 * �X�v���C�g�̐�����Ԃ��擾���܂�.
	 *
	 * @return �������̏ꍇ��true��Ԃ�.<Br>
	 */
	public boolean isExist() {
		return exist;
	}

	/**
	 * �X�v���C�g�̐�����Ԃ�ݒ肵�܂�.
	 *
	 * @param exist �������.<br>
	 */
	public void setExist(boolean exist) {
		this.exist = exist;
	}

	/**
	 * �X�v���C�g�̉���Ԃ��擾���܂�.
	 *
	 * @return �X�v���C�g�̉����.<br>
	 */
	public boolean isVisible() {
		return visible;
	}

	/**
	 * �X�v���C�g�̉���Ԃ�ݒ肵�܂�.
	 *
	 * @param visible �X�v���C�g�̉����.<br>
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	/**
	 * ���̃X�v���C�g�̍����X���W���擾���܂�.<br>
	 *
	 * @return X���W.<br>
	 */
	public float getX() {
		return bounds.x;
	}

	/**
	 * ���̃X�v���C�g�̍����X���W��ݒ肵�܂�.<br>
	 *
	 * @param x X���W.<br>
	 */
	public void setX(float x) {
		bounds.x = x;
		center.x = bounds.x + personalCenter.x;
	}

	/**
	 * ���̃X�v���C�g�̍����Y���W���擾���܂�.<br>
	 *
	 * @return Y���W.<br>
	 */
	public float getY() {
		return bounds.y;
	}

	/**
	 * ���̃X�v���C�g�̍����Y���W��ݒ肵�܂�.<br>
	 *
	 * @param y Y���W.<br>
	 */
	public void setY(float y) {
		bounds.y = y;
		center.y = bounds.y + personalCenter.y;
	}

	/**
	 * ���̃X�v���C�g�̕����擾���܂�.<br>
	 *
	 * @return ��.<br>
	 */
	public float getWidth() {
		return bounds.width;
	}

	/**
	 * ���̃X�v���C�g�̕���ݒ肵�܂�.<br>
	 *
	 * @param width ��.<br>
	 */
	public void setWidth(float width) {
		bounds.width = width;
		personalCenter.x = bounds.width / 2;
		center.x = bounds.x + personalCenter.x;
	}

	/**
	 * ���̃X�v���C�g�̍������擾���܂�.<br>
	 *
	 * @return ����.<br>
	 */
	public float getHeight() {
		return bounds.height;
	}

	/**
	 * ���̃X�v���C�g�̍�����ݒ肵�܂�.<br>
	 *
	 * @param height ����.<br>
	 */
	public void setHeight(float height) {
		bounds.height = height;
		personalCenter.y = bounds.height / 2;
		center.y = bounds.y + personalCenter.y;
	}

	/**
	 * ���̃X�v���C�g��Z�[�x���擾���܂�.
	 *
	 * @return �[�x.<br>
	 */
	public float getZ() {
		return z;
	}

	/**
	 * ���̃X�v���C�g��Z�[�x��ݒ肵�܂�.
	 *
	 * @param z �[�x.<br>
	 */
	public void setZ(float z) {
		this.z = z;
	}

	/**
	 * ���̃X�v���C�g�̕������쐬���܂�. ���̃��\�b�h�ł́A�S�Ẵt�B�[���h���N���[�j���O���܂�.<br>
	 * ���̃��\�b�h�̓T�u�N���X�œK�؂ɃI�[�o�[���C�h���Ă�������.<br>
	 *
	 * @return ���̃X�v���C�g�Ɠ����ݒ�̐V�����C���X�^���X.<br>
	 */
	@Override
	public Sprite clone() {
		try {
			Sprite s = (Sprite) super.clone();
			s.bounds = (Rectangle2D.Float) this.bounds.clone();
			s.center = (Point2D.Float) this.center.clone();
			s.personalCenter = (Point2D.Float) this.personalCenter.clone();
			return s;
		} catch (CloneNotSupportedException ex) {
			Logger.getLogger(Sprite.class.getName()).log(Level.SEVERE, null, ex);
		}
		return null;
	}

	/**
	 * �X�v���C�g�̐[�x���r���Az�����W�̏����ɕ��ёւ���@�\��񋟂��܂�.
	 *
	 * @param spr ��r����X�v���C�g.<br>
	 *
	 * @return Comparable�̎����Ɋ�Â��l.<br>
	 */
	@Override
	public final int compareTo(Sprite spr) {
		return java.lang.Float.compare(z, spr.z);
	}

	@Override
	public String toString() {
		return "Sprite{" + "bounds=" + bounds + ", center=" + center + ", personalCenter="
				+ personalCenter + ", z=" + z + ", visible=" + visible + ", exist=" + exist + '}';
	}
}
