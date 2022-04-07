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
package kinugasa.game.rpgui;

import java.awt.Point;
import kinugasa.object.FourDirection;
import kinugasa.resource.Nameable;

/**
 * �t�B�[���h�}�b�v�Ԃ��ړ�����A�}�b�v�J�ڂ��s�����߂̏o������ł�.
 * <br>
 * �m�[�h�̓t�B�[���h�}�b�v�̃}�b�v�J�ڃC�x���g�w�̂���ʒu�ɐݒ肳��܂��B
 * �L�����N�^���m�[�h�̏�Ɉړ�����ƁA�m�[�h�ɐݒ肳�ꂽ
 * �h�o���h�Ɉړ����܂��B�m�[�h�����m���Ă��玩���I�Ɉړ����邩�ǂ�����
 * �Q�[���f�U�C���ɂ���ĈقȂ�܂��B<br>
 * <br>
 * �v���C���[�����̃m�[�h���g�p�\�ł��邩�́ANodeAccepter���g�p���Č�������܂��B<br>
 * �������ANodeAccepter��null�������܂��Bnull�̏ꍇ�͕K���g�p�\�ƂȂ�܂��B<br>
 * <br>
 * �m�[�h���g�p�����}�b�v�J�ڂ̓t�@�C�����[�h���������邽�߁A
 * �}�b�v�J�ڒ��͉�ʂ��Ó]����Ȃǂ̃G�t�F�N�g���g�p���܂��B<br>
 * <br>
 * <br>
 * �m�[�h��ʂ��Ď��̃}�b�v�ɑJ�ڂ���ɂ́A�ړ���}�b�v�Ƃ��̃}�b�v����
 * �m�[�h���w�肵�܂��B<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_12:26:43<br>
 * @author Dra0211<br>
 */
public class Node implements Nameable {

	/** �m�[�h�̈�ӓI�Ȗ��O�ł�.
	 * ���O�̈�Ӑ��͓���t�B�[���h�}�b�v���ł��B<br>
	 */
	private final String name;
	/** ���̃m�[�h���ݒu�����ʒu�ł�.
	 * �ʒu�̓}�b�v�`�b�v���x�[�X�ł��B<br>
	 */
	private final Point location;
	/** ���̃m�[�h�ɂ���đJ�ڂ����̃}�b�v�̖��O�ł�. */
	private final String exitMapName;
	/** ���̃m�[�h�ɂ���đJ�ڂ����ɂ̃m�[�h�̖��O�ł�. */
	private final String exitNodeName;
	/** �m�[�h����o���Ƃ��ɁA�L�����N�^�������Ă�������ł�. */
	private final FourDirection face;
	/** ���̃m�[�h�̐����ł�.
	 * null�������܂��B<br>
	 */
	private String tooltip;
	/** ���̃m�[�h��NodeAccepter�ł�. */
	private NodeAccepter accepter;

	/**
	 * �V�����m�[�h���쐬���܂�.
	 * @param name �}�b�v���ň�ӓI�Ȗ��O���w�肵�܂��B<br>
	 * @param location ���̃m�[�h�̐ݒu�ʒu�ł��B<br>
	 * @param exitMapName �o���̂���}�b�v�����w�肵�܂��B<br>
	 * @param exitNodeName �o���̃m�[�h�����w�肵�܂��B<br>
	 * @param face �o���ł̃L�����N�^�̌������w�肵�܂��B<br>
	 */
	public Node(String name, Point location, String exitMapName, String exitNodeName, FourDirection face) {
		this(name, location, exitMapName, exitNodeName, "", face);
	}

	/**
	 * �V�����m�[�h���쐬���܂�.
	 * @param name �}�b�v���ň�ӓI�Ȗ��O���w�肵�܂��B<br>
	 * @param location ���̃m�[�h�̐ݒu�ʒu�ł��B<br>
	 * @param exitMapName �o���̂���}�b�v�����w�肵�܂��B<br>
	 * @param exitNodeName �o���̃m�[�h�����w�肵�܂��B<br>
	 * @param tooltip �m�[�h�̐������ł��B<br>
	 * @param face �o���ł̃L�����N�^�̌������w�肵�܂��B<br>
	 */
	public Node(String name, Point location, String exitMapName, String exitNodeName, String tooltip, FourDirection face) {
		this(name, location, exitMapName, exitNodeName, tooltip, face, null);
	}

	/**
	 * �V�����m�[�h���쐬���܂�.
	 * @param name �}�b�v���ň�ӓI�Ȗ��O���w�肵�܂��B<br>
	 * @param location ���̃m�[�h�̐ݒu�ʒu�ł��B<br>
	 * @param exitMapName �o���̂���}�b�v�����w�肵�܂��B<br>
	 * @param exitNodeName �o���̃m�[�h�����w�肵�܂��B<br>
	 * @param tooltip �m�[�h�̐������ł��B<br>
	 * @param face �o���ł̃L�����N�^�̌������w�肵�܂��B<br>
	 * @param accepter NodeAccepter���w�肵�܂��B<br>
	 */
	public Node(String name, Point location, String exitMapName, String exitNodeName, String tooltip, FourDirection face, NodeAccepter accepter) {
		this.name = name;
		this.location = location;
		this.exitMapName = exitMapName;
		this.exitNodeName = exitNodeName;
		this.tooltip = tooltip;
		this.face = face;
		this.accepter = accepter;
	}

	/**
	 * �m�[�h�̐������擾���܂�.
	 * @param tooltip �m�[�h�ɐݒ肳���������ł��B<br>
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * ���̃m�[�h���ݒu���ꂽ���W��Ԃ��܂�.
	 * @return X���W��Ԃ��܂��B�ʒu�̓}�b�v�`�b�v���x�[�X�ł��B<br>
	 */
	public int getX() {
		return location.x;
	}

	/**
	 * ���̃m�[�h���ݒu���ꂽ���W��Ԃ��܂�.
	 * @return Y���W��Ԃ��܂��B�ʒu�̓}�b�v�`�b�v���x�[�X�ł��B<br>
	 */
	public int getY() {
		return location.y;
	}

	/**
	 * NodeAccepter��ݒ肵�܂�.
	 * @param accepter �V����NodeAccepter�𑗐M���܂��Bnull�������܂��B<br>
	 */
	public void setAccepter(NodeAccepter accepter) {
		this.accepter = accepter;
	}

	/**
	 * �o���ƂȂ�}�b�v�̖��O��Ԃ��܂�.
	 * @return �o���̃}�b�v���ł��B<br>
	 */
	public String getExitMapName() {
		return exitMapName;
	}

	/**
	 * �o���ƂȂ�m�[�h�̖��O��Ԃ��܂�.
	 * @return �o���̃m�[�h���ł��B<br>
	 */
	public String getExitNodeName() {
		return exitNodeName;
	}

	/**
	 * �m�[�h�̐������擾���܂�.
	 * @return �m�[�h�ɐݒ肳�ꂽ��������Ԃ��܂��B<br>
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * �o���ł̃L�����N�^�̌�����Ԃ��܂�.
	 * @return �o���ł̃L�����N�^�̌����ł��B<br>
	 */
	public FourDirection getFace() {
		return face;
	}

	/**
	 * ���̃m�[�h�ɐݒ肳�ꂽNodeAccepter��Ԃ��܂�.
	 * @return NodeAccepter��Ԃ��܂��B<br>
	 */
	public NodeAccepter getAccepter() {
		return accepter;
	}

	/**
	 * ���̃m�[�h��NodeAccepter�������Ă��邩�𒲂ׂ܂�.
	 * @return getAccepter() != null��Ԃ��܂��B<br>
	 */
	public boolean hasAccepter() {
		return accepter != null;
	}

	/**
	 * ���̃m�[�h���L�������������܂�.
	 * @return �m�[�h��NodeAccepter���ݒ肳��Ă���ꍇ�́Aaccepter.accept()��Ԃ��܂��B<Br>
	 * NodeAccepter��null�̏ꍇ�͕K��true��Ԃ��܂��B<br>
	 */
	public boolean accept() {
		return hasAccepter() ? accepter.accept() : true;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * ���̃m�[�h�̈ʒu���擾���܂�.
	 * �Q�Ƃ��Ԃ���܂��B<Br>
	 * @return �m�[�h���ʒu��Ԃ��܂��B<br>
	 */
	public Point getLocation() {
		return location;
	}

	@Override
	public String toString() {
		return "Node{" + "name=" + name + ", location=" + location + ", exitMapName="
				+ exitMapName + ", exitNodeName=" + exitNodeName + ", face=" + face
				+ ", tooltip=" + tooltip + ", accepter=" + accepter + '}';
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 79 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Node other = (Node) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
