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
 * フィールドマップが持つノードの格納領域です.
 * <br>
 * ノードは座標インスタンスがキーとなってMapに格納されます。<br>
 * したがって、同一座標には1つのノードしか設定できません。<br>
 * 1つのノードで複数の出口を実現する必要がある場合、 NodeAccepterを使って有効／無効を切り替えるか、 Nodeクラスを継承して出口のマップやノードの名前を 切り替えるクラスを作成できます。<br>
 * <br>
 * このマップはnullキーを許容しません。<br>
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
		GameLog.print(Level.WARNING, "要求されたノードが存在しません name=[" + name + "]");
		return null;
	}
}
