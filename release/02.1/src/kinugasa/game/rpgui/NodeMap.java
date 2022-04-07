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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import kinugasa.game.GameLog;

/**
 * �t�B�[���h�}�b�v�����m�[�h�̊i�[�̈�ł�.
 * <br>
 * �m�[�h�͍��W�C���X�^���X���L�[�ƂȂ���Map�Ɋi�[����܂��B<br>
 * ���������āA������W�ɂ�1�̃m�[�h�����ݒ�ł��܂���B<br>
 * 1�̃m�[�h�ŕ����̏o������������K�v������ꍇ�A NodeAccepter���g���ėL���^������؂�ւ��邩�A Node�N���X���p�����ďo���̃}�b�v��m�[�h�̖��O�� �؂�ւ���N���X���쐬�ł��܂��B<br>
 * <br>
 * ���̃}�b�v��null�L�[�����e���܂���B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/29_15:37:08<br>
 * @author Dra0211<br>
 */
public class NodeMap extends HashMap<Point, Node> {

	private static final long serialVersionUID = 590842202485110966L;

	public NodeMap() {
	}

	@Override
	public Node put(Point k, Node v)
			throws IllegalArgumentException {
		if (k == null) {
			throw new IllegalArgumentException("key is null");
		}
		return super.put(k, v);
	}

	@Override
	public void putAll(Map<? extends Point, ? extends Node> map)
			throws IllegalArgumentException {
		if (map.containsKey(null)) {
			throw new IllegalArgumentException("key is null");
		}
		super.putAll(map);
	}

	public Node getByName(String name) {
		List<Node> nodeList = new ArrayList<Node>(values());
		for (int i = 0, size = nodeList.size(); i < size; i++) {
			if (nodeList.get(i).getName().equals(name)) {
				return nodeList.get(i);
			}
		}
		GameLog.print(Level.WARNING, "�v�����ꂽ�m�[�h�����݂��܂��� name=[" + name + "]");
		return null;
	}
}
