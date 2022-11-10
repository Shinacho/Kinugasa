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
package kinugasa.game.field;

import java.util.Collection;
import kinugasa.resource.Storage;

/**
 * �S�Ă�NodeAccepter��ۊǂ���X�g���[�W�ł�.
 * <br>
 * �쐬���ꂽNodeAccepter�͂��̃X�g���[�W�Ɏ����ǉ�����܂��B<br>
 * <br>
 * ���̃X�g���[�W�ɂ́A�ȉ��̗v�f�������ǉ�����܂��B<br>
 * <li>"TRUE"</li>
 * ���̃��f����accept���\�b�h�͕K��true��Ԃ��܂��B<br>
 * <br>
 * <li>"FALSE"</li>
 * ���̃��f����accept���\�b�h�͕K��false��Ԃ��܂��B<br>
 * <br>
 * �����̃I�u�W�F�N�g�́A�폜��N���A��ɍĐݒ肳��܂��B<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_12:34:16<br>
 * @author Dra0211<br>
 */
public final class NodeAccepterStorage extends Storage<NodeAccepter> {

	/** �B��̃C���X�^���X�ł�. */
	private static final NodeAccepterStorage INSTANCE = new NodeAccepterStorage();

	/**
	 * �V���O���g���N���X�ł�.
	 * getInstance���g�p���Ă��������B<br>
	 */
	private NodeAccepterStorage() {
	}

	static {
		addDefaultObject();
	}

	/**
	 * "TRUE"�����"FALSE"��ǉ����܂�.
	 */
	private static void addDefaultObject() {
		new NodeAccepter("TRUE") {
			private static final long serialVersionUID = -7779908992040310156L;

			@Override
			public boolean accept() {
				return true;
			}
		};
		new NodeAccepter("FALSE") {
			private static final long serialVersionUID = -7779908992040310156L;

			@Override
			public boolean accept() {
				return false;
			}
		};
	}

	/**
	 * �C���X�^���X���擾���܂�.
	 * @return NodeAccepterStorage�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public static NodeAccepterStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void remove(NodeAccepter val) {
		super.remove(val);
		addDefaultObject();
	}

	@Override
	public void remove(String key) {
		super.remove(key);
		addDefaultObject();
	}

	@Override
	public void removeAll(Collection<? extends NodeAccepter> values) {
		super.removeAll(values);
		addDefaultObject();
	}

	@Override
	public void removeAll(NodeAccepter... values) {
		super.removeAll(values);
		addDefaultObject();
	}

	@Override
	public void removeAll(String... keys) {
		super.removeAll(keys);
		addDefaultObject();
	}

	@Override
	public void clear() {
		super.clear();
		addDefaultObject();
	}
}
