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
package kinugasa.game.input;

import java.awt.Point;
import java.awt.Shape;

/**
 * �}�E�X�ɂ����͏�Ԃ��i�[���܂�.
 * <br>
 * AnyInput���胁�\�b�h�ł́A�}�E�X�J�[�\���̈ʒu��0,0�łȂ��ꍇ�� ���͂���Ɣ��肳��܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:08:38<br>
 * @author Dra0211<br>
 */
public final class MouseState extends InputDeviceState {

	private static final long serialVersionUID = -7984070482396918667L;

	/**
	 * �}�E�X�̏�Ԃ��쐬���܂�.
	 *
	 * @param LEFT_BUTTON ���{�^���̓��͏�Ԃł��B<br>
	 * @param RIGHT_BUTTON �E�{�^���̓��͏�Ԃł��B<br>
	 * @param WHEEL_BUTTON �z�C�[���{�^���̓��͏�Ԃł��B<br>
	 * @param IN_WINDOW �E�C���h�E�̈���ɂ���ꍇ��true�𑗐M���܂��B<br>
	 * @param DRAG_NOW �h���b�O���̏ꍇ��true�𑗐M���܂��B<br>
	 * @param WHEEL_VALUE ���O�ɓ��͂��ꂽ�z�C�[���̉�]�ʂł��B<br>
	 * @param location �E�C���h�E��̃J�[�\���̈ʒu�ł��B<br>
	 * @param locationOnScreen �B<br>
	 */
	MouseState(
			boolean LEFT_BUTTON,
			boolean RIGHT_BUTTON,
			boolean WHEEL_BUTTON,
			boolean IN_WINDOW,
			boolean DRAG_NOW,
			int WHEEL_VALUE,
			Point location,
			Point locationOnScreen,
			Point draggedLocation) {
		this.LEFT_BUTTON = LEFT_BUTTON;
		this.RIGHT_BUTTON = RIGHT_BUTTON;
		this.WHEEL_BUTTON = WHEEL_BUTTON;
		this.IN_WINDOW = IN_WINDOW;
		this.DRAG_NOW = DRAG_NOW;
		this.WHEEL_VALUE = WHEEL_VALUE;
		this.location = (Point) location.clone();
		this.locationOnScreen = (Point) locationOnScreen.clone();
		this.draggedLocation = draggedLocation;

	}
	/**
	 * ���{�^���̓��͏�Ԃł�.
	 */
	public final boolean LEFT_BUTTON;
	/**
	 * �E�{�^���̓��͏�Ԃł�.
	 */
	public final boolean RIGHT_BUTTON;
	/**
	 * �z�C�[���{�^���̓��͏�Ԃł�.
	 */
	public final boolean WHEEL_BUTTON;
	/**
	 * �E�C���h�E�̈���ɂ���ꍇ��true�𑗐M���܂�.
	 */
	public final boolean IN_WINDOW;
	/**
	 * �h���b�O���̏ꍇ��true�𑗐M���܂�.
	 */
	public final boolean DRAG_NOW;
	/**
	 * ���O�ɓ��͂��ꂽ�z�C�[���̉�]�ʂł�.
	 */
	public final int WHEEL_VALUE;
	/**
	 * �E�C���h�E��̃J�[�\���̈ʒu�ł�.
	 */
	private Point location;
	/**
	 * �X�N���[����̃J�[�\���̈ʒu�ł�.
	 */
	private Point locationOnScreen;
	/**
	 * 0,0��\�����W�ł�.
	 */
	private static final Point ZERO_POINT = new Point(0, 0);
	private Point draggedLocation;

	@Override
	public boolean isAnyInput() {
		return LEFT_BUTTON
				|| RIGHT_BUTTON
				|| WHEEL_BUTTON
				|| WHEEL_VALUE != 0
				|| DRAG_NOW;
	}

	@Override
	public boolean isAnyButtonInput() {
		return LEFT_BUTTON || RIGHT_BUTTON || WHEEL_BUTTON;
	}

	@Override
	public boolean isEmptyInput() {
		return location.equals(ZERO_POINT)
				&& locationOnScreen.equals(ZERO_POINT)
				&& !LEFT_BUTTON
				&& !RIGHT_BUTTON
				&& !WHEEL_BUTTON
				&& WHEEL_VALUE == 0
				&& !DRAG_NOW;
	}

	/**
	 * �}�E�X�̃E�C���h�E��̈ʒu���擾���܂�.
	 * ���̍��W�̓^�C�g���o�[���܂߂����W�ƂȂ�܂��̂ŁA���ۂɂ͂��̕␳���K�v�ł��B<br>
	 * �␳�́AGame.gerWindow����s���܂��B<br>
	 *
	 * @return �E�C���h�E��̈ʒu�̃N���[���ł��B<br>
	 */
	public Point getLocation() {
		return (Point) location.clone();
	}

	/**
	 * �E�C���h�E��̍��W���擾���܂�.
	 *
	 * @return �E�C���h�E�̈ʒu�����X�ʒu��Ԃ��܂��B
	 */
	public int getX() {
		return location.x;
	}

	/**
	 * �E�C���h�E��̍��W���擾���܂�.
	 *
	 * @return �E�C���h�E�̈ʒu�����Y�u��Ԃ��܂��B
	 */
	public int getY() {
		return location.y;
	}

	/**
	 * �}�E�X�̃X�N���[����̈ʒu���擾���܂�.
	 *
	 * @return �X�N���[����̈ʒu�̃N���[���ł��B<br>
	 */
	public Point getLocationOnScreen() {
		return (Point) locationOnScreen.clone();
	}

	/**
	 * �X�N���[����̈ʒu���擾���܂�.
	 *
	 * @return �X�N���[���̍��ォ���X�ʒu��Ԃ��܂��B
	 */
	public int getXOnScreen() {
		return locationOnScreen.x;
	}

	/**
	 * �X�N���[����̈ʒu���擾���܂�.
	 *
	 * @return �X�N���[���̍��ォ���Y�ʒu��Ԃ��܂��B
	 */
	public int getYOnScreen() {
		return locationOnScreen.y;
	}

	/**
	 * �E�C���h�E��̃}�E�X�̈ʒu���Ashape���ɂ��邩�𒲂ׂ܂�.
	 *
	 * @param shape ���肷��̈�𑗐M���܂��B<br>
	 * @return �E�C���h�E��̃J�[�\���ʒu��shape���ɂ���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean contains(Shape shape) {
		return shape == null ? false : LEFT_BUTTON & shape.contains(location);
	}

	/**
	 * �}�E�X�{�^����������Ă��邩���������܂�.
	 *
	 * @param button ��������{�^�����w�肵�܂��B<br>
	 * @return �w�肳�ꂽ�{�^����������Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public boolean isPressed(MouseButtons button) {
		switch (button) {
			case LEFT_BUTTON: {
				return LEFT_BUTTON;
			}
			case RIGHT_BUTTON: {
				return RIGHT_BUTTON;
			}
			case WHEEL_BUTTON: {
				return WHEEL_BUTTON;
			}
			default:
				throw new AssertionError();
		}
	}

	public Point getDraggedLocation() {
		return (Point) draggedLocation.clone();
	}

	@Override
	public String toString() {
		return "MouseState{" + "LEFT_BUTTON=" + LEFT_BUTTON + ", RIGHT_BUTTON=" + RIGHT_BUTTON
				+ ", WHEEL_BUTTON=" + WHEEL_BUTTON + ", IN_WINDOW=" + IN_WINDOW + ", DRAG_NOW="
				+ DRAG_NOW + ", WHEEL_VALUE=" + WHEEL_VALUE + ", location=["
				+ location.x + ", " + location.y + "], locationOnScreen=["
				+ locationOnScreen.x + ", " + locationOnScreen.y + "]}";
	}

	@Override
	public MouseState clone() {
		MouseState result = (MouseState) super.clone();
		result.location = (Point) this.location.clone();
		result.locationOnScreen = (Point) this.locationOnScreen.clone();
		return result;
	}
}
