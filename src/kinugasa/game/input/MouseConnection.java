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
package kinugasa.game.input;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;

/**
 * �}�E�X�J�[�\���̈ʒu����͏�Ԃ��������邽�߂̃}�E�X�A�_�v�^�̎����ł�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:09:50<br>
 * @author Shinacho<br>
 */
public class MouseConnection extends MouseAdapter {

	/**
	 * �B��̃C���X�^���X�ł�.
	 */
	private static final MouseConnection INSTANCE = new MouseConnection();
	/**
	 * �������͂���Ă��Ȃ���Ԃ�MouseState�ł�.
	 */
	private static final MouseState CLEAR_STATE = new MouseState(false, false, false, false, false, 0, new Point(), new Point(), null);

	static MouseConnection getInstance() {
		return INSTANCE;
	}

	/**
	 * �R���|�[�l���g�ɂ��̃��X�i��ǉ����܂�.
	 *
	 * @param component ���X�i��o�^����R���|�[�l���g�𑗐M���܂��B<br>
	 */
	public static void setListener(Component component) {
		component.addMouseListener(INSTANCE);
		component.addMouseMotionListener(INSTANCE);
		component.addMouseWheelListener(INSTANCE);
	}

	/**
	 * ��̃}�E�X�X�e�[�g�𔭍s���܂�. ���̃��\�b�h�͏�ɓ����C���X�^���X��Ԃ��܂��B<br>
	 *
	 * @return ��̃}�E�X�X�e�[�g��Ԃ��܂��B����������Ă��Ȃ���Ԃł��B���W��0,0�ł��B<br>
	 */
	public static MouseState createClearState() {
		return CLEAR_STATE;
	}

	/**
	 * �V���O���g���N���X�ł�.
	 */
	private MouseConnection() {
		location = new Point();
		locationOnScreen = new Point();
	}

	/**
	 * ���݂̃}�E�X�̏�Ԃ��擾���܂�.
	 *
	 * @return �}�E�X�̓��͏�Ԃ�Ԃ��܂��B<br>
	 */
	public static MouseState getState() {
		return new MouseState(
				INSTANCE.buttonState[MouseEvent.BUTTON1 - 1],
				INSTANCE.buttonState[MouseEvent.BUTTON3 - 1],
				INSTANCE.buttonState[MouseEvent.BUTTON2 - 1],
				INSTANCE.entry,
				INSTANCE.drag,
				INSTANCE.wheelValue,
				INSTANCE.location,
				INSTANCE.locationOnScreen,
				INSTANCE.draggedLocation);
	}
	/**
	 * �}�E�X�̃{�^���̓��͏�Ԃł�.
	 */
	private boolean[] buttonState = {false, false, false, false, false, false};
	/**
	 * �}�E�X���E�C���h�E���ɂ��邩�𔻒肵�܂�.
	 */
	private boolean entry;
	/**
	 * �h���b�O���ł��邩�𔻒肵�܂�.
	 */
	private boolean drag;
	/**
	 * �E�C���h�E���ɂ�����}�E�X�J�[�\���̈ʒu�ł�.
	 */
	private Point location;
	/**
	 * �X�N���[����ɂ�����}�E�X�J�[�\���̈ʒu�ł�.
	 */
	private Point locationOnScreen;
	/**
	 * �z�C�[���̉�]�ʂł�. ��ɉ񂷂ƕ����ƂȂ�܂��B
	 */
	private int wheelValue;
	private Point draggedLocation;
	private Point prevDragLocation;

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		drag = true;
		draggedLocation = e.getPoint();
	}

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		location = e.getPoint();
		locationOnScreen = e.getLocationOnScreen();
	}

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		buttonState[e.getButton() - 1] = true;
	}

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		buttonState[e.getButton() - 1] = false;
		drag = false;
		draggedLocation = null;
	}

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		entry = true;
	}

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		entry = false;
	}

	/**
	 * �}�E�X�A�_�v�^�̎����ł�.
	 *
	 * @param e �C�x���g�����M����܂��B<br>
	 */
	@Override
	public void mouseWheelMoved(MouseWheelEvent e) {
		wheelValue = e.getUnitsToScroll();
	}
}
