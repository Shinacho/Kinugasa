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
 * フィールドマップ間を移動する、マップ遷移を行うための出入り口です.
 * <br>
 * ノードはフィールドマップのマップ遷移イベント層のある位置に設定されます。
 * キャラクタがノードの上に移動すると、ノードに設定された
 * ”出口”に移動します。ノードを検知してから自動的に移動するかどうかは
 * ゲームデザインによって異なります。<br>
 * <br>
 * プレイヤーがこのノードが使用可能であるかは、NodeAccepterを使用して検査されます。<br>
 * ただし、NodeAccepterはnullを許可します。nullの場合は必ず使用可能となります。<br>
 * <br>
 * ノードを使用したマップ遷移はファイルロードが発生するため、
 * マップ遷移中は画面を暗転するなどのエフェクトを使用します。<br>
 * <br>
 * <br>
 * ノードを通って次のマップに遷移するには、移動先マップとそのマップ内の
 * ノードを指定します。<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_12:26:43<br>
 * @author Dra0211<br>
 */
public class Node implements Nameable {

	/** ノードの一意的な名前です.
	 * 名前の一意性は同一フィールドマップ内です。<br>
	 */
	private final String name;
	/** このノードが設置される位置です.
	 * 位置はマップチップ数ベースです。<br>
	 */
	private final Point location;
	/** このノードによって遷移する先のマップの名前です. */
	private final String exitMapName;
	/** このノードによって遷移する先にのノードの名前です. */
	private final String exitNodeName;
	/** ノードから出たときに、キャラクタが向いている方向です. */
	private final FourDirection face;
	/** このノードの説明です.
	 * nullを許可します。<br>
	 */
	private String tooltip;
	/** このノードのNodeAccepterです. */
	private NodeAccepter accepter;

	/**
	 * 新しいノードを作成します.
	 * @param name マップ内で一意的な名前を指定します。<br>
	 * @param location このノードの設置位置です。<br>
	 * @param exitMapName 出口のあるマップ名を指定します。<br>
	 * @param exitNodeName 出口のノード名を指定します。<br>
	 * @param face 出口でのキャラクタの向きを指定します。<br>
	 */
	public Node(String name, Point location, String exitMapName, String exitNodeName, FourDirection face) {
		this(name, location, exitMapName, exitNodeName, "", face);
	}

	/**
	 * 新しいノードを作成します.
	 * @param name マップ内で一意的な名前を指定します。<br>
	 * @param location このノードの設置位置です。<br>
	 * @param exitMapName 出口のあるマップ名を指定します。<br>
	 * @param exitNodeName 出口のノード名を指定します。<br>
	 * @param tooltip ノードの説明文です。<br>
	 * @param face 出口でのキャラクタの向きを指定します。<br>
	 */
	public Node(String name, Point location, String exitMapName, String exitNodeName, String tooltip, FourDirection face) {
		this(name, location, exitMapName, exitNodeName, tooltip, face, null);
	}

	/**
	 * 新しいノードを作成します.
	 * @param name マップ内で一意的な名前を指定します。<br>
	 * @param location このノードの設置位置です。<br>
	 * @param exitMapName 出口のあるマップ名を指定します。<br>
	 * @param exitNodeName 出口のノード名を指定します。<br>
	 * @param tooltip ノードの説明文です。<br>
	 * @param face 出口でのキャラクタの向きを指定します。<br>
	 * @param accepter NodeAccepterを指定します。<br>
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
	 * ノードの説明を取得します.
	 * @param tooltip ノードに設定される説明文です。<br>
	 */
	public void setTooltip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * このノードが設置された座標を返します.
	 * @return X座標を返します。位置はマップチップ数ベースです。<br>
	 */
	public int getX() {
		return location.x;
	}

	/**
	 * このノードが設置された座標を返します.
	 * @return Y座標を返します。位置はマップチップ数ベースです。<br>
	 */
	public int getY() {
		return location.y;
	}

	/**
	 * NodeAccepterを設定します.
	 * @param accepter 新しいNodeAccepterを送信します。nullを許可します。<br>
	 */
	public void setAccepter(NodeAccepter accepter) {
		this.accepter = accepter;
	}

	/**
	 * 出口となるマップの名前を返します.
	 * @return 出口のマップ名です。<br>
	 */
	public String getExitMapName() {
		return exitMapName;
	}

	/**
	 * 出口となるノードの名前を返します.
	 * @return 出口のノード名です。<br>
	 */
	public String getExitNodeName() {
		return exitNodeName;
	}

	/**
	 * ノードの説明を取得します.
	 * @return ノードに設定された説明文を返します。<br>
	 */
	public String getTooltip() {
		return tooltip;
	}

	/**
	 * 出口でのキャラクタの向きを返します.
	 * @return 出口でのキャラクタの向きです。<br>
	 */
	public FourDirection getFace() {
		return face;
	}

	/**
	 * このノードに設定されたNodeAccepterを返します.
	 * @return NodeAccepterを返します。<br>
	 */
	public NodeAccepter getAccepter() {
		return accepter;
	}

	/**
	 * このノードがNodeAccepterを持っているかを調べます.
	 * @return getAccepter() != nullを返します。<br>
	 */
	public boolean hasAccepter() {
		return accepter != null;
	}

	/**
	 * このノードが有効かを検査します.
	 * @return ノードにNodeAccepterが設定されている場合は、accepter.accept()を返します。<Br>
	 * NodeAccepterがnullの場合は必ずtrueを返します。<br>
	 */
	public boolean accept() {
		return hasAccepter() ? accepter.accept() : true;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * このノードの位置を取得します.
	 * 参照が返されます。<Br>
	 * @return ノードを位置を返します。<br>
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
