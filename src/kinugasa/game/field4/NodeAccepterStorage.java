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

import java.util.Collection;
import kinugasa.resource.Storage;

/**
 * 全てのNodeAccepterを保管するストレージです.
 * <br>
 * 作成されたNodeAccepterはこのストレージに自動追加されます。<br>
 * <br>
 * このストレージには、以下の要素が自動追加されます。<br>
 * <li>"TRUE"</li>
 * このモデルのacceptメソッドは必ずtrueを返します。<br>
 * <br>
 * <li>"FALSE"</li>
 * このモデルのacceptメソッドは必ずfalseを返します。<br>
 * <br>
 * これらのオブジェクトは、削除やクリア後に再設定されます。<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_12:34:16<br>
 * @author Shinacho<br>
 */
public final class NodeAccepterStorage extends Storage<NodeAccepter> {

	/** 唯一のインスタンスです. */
	private static final NodeAccepterStorage INSTANCE = new NodeAccepterStorage();

	/**
	 * シングルトンクラスです.
	 * getInstanceを使用してください。<br>
	 */
	private NodeAccepterStorage() {
	}

	static {
		addDefaultObject();
	}

	/**
	 * "TRUE"および"FALSE"を追加します.
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
	 * インスタンスを取得します.
	 * @return NodeAccepterStorageのインスタンスを返します。<br>
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
