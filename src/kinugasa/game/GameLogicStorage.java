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
package kinugasa.game;

import kinugasa.game.system.GameSystem;
import kinugasa.resource.InputObjectStorage;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_4:35:48<br>
 * @author Shinacho<br>
 */
public class GameLogicStorage extends Storage<GameLogic> {

	private static final GameLogicStorage INSTANCE = new GameLogicStorage();

	public static GameLogicStorage getInstance() {
		return INSTANCE;
	}

	private GameLogicStorage() {
	}

	private GameLogic current;
	private GameLogic prev;

	/**
	 * startUpで実行する場合はウインドウがまだ初期化されていない点に注意してください。
	 *
	 * @param name
	 */
	public void changeTo(String name) {
		changeTo(name, true);
	}

	/**
	 * startUpで実行する場合はウインドウがまだ初期化されていない点に注意してください。
	 *
	 * @param name
	 * @param load
	 */

	public void changeTo(String name, boolean load) {
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("kinugasa.game.GameLogicStorage.changeTo():" + name);
		}
		if (!contains(name)) {
			throw new NameNotFoundException("logic " + name + " is not found");
		}
		if (current != null) {
			current.dispose();
			System.gc();
		}
		prev = current;
		this.current = get(name);
		if (load) {
			current.load();
		}
	}

	public GameLogic getCurrent() {
		return current;
	}

	public GameLogic getPrev() {
		return prev;
	}

}
