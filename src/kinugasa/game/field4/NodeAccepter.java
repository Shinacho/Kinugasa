/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.game.field4;

import kinugasa.object.Model;
import kinugasa.resource.Nameable;


/**
 * ノードが有効であるかを判定するクラスです.
 * <br>
 * このクラスは、”アイテムを所持していないと通過できない”などの
 * ノードの判定機能を提供します。<br>
 * 作成したNodeAccepterは自動的にNodeAccepterStorageに追加されます。<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_12:30:52<br>
 * @author Shinacho<br>
 */
public abstract class NodeAccepter extends Model implements Nameable {

	private static final long serialVersionUID = -4859183748031650028L;
	/** 一意的な名前です. */
	private String name;

	/**
	 * 新しいNodeAccepterを作成します.
	 * @param name 一意的な名前を指定します。<br>
	 */
	public NodeAccepter(String name) {
		this.name = name;
		putThis();
	}

	/**
	 * NodeAccepterStorageにthisインスタンスを追加します.
	 */
	private void putThis() {
		NodeAccepterStorage.getInstance().put(this);
	}

	@Override
	public final String getName() {
		return name;
	}

	/**
	 * このNodeAccepterを持つノードが使用可能であるかを判定します.
	 * @return プレイヤーがこのノードを使用できるときにtrueを返します。<br>
	 */
	public abstract boolean accept();

	@Override
	public NodeAccepter clone() {
		return (NodeAccepter) super.clone();
	}

	@Override
	public String toString() {
		return "NodeAccepter{" + "name=" + name + '}';
	}

	@Override
	public final int hashCode() {
		int hash = 5;
		hash = 67 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public final boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final NodeAccepter other = (NodeAccepter) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}
}
