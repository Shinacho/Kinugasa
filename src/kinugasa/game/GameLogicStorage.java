/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game;

import kinugasa.resource.DynamicStorage;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_4:35:48<br>
 * @author Dra211<br>
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

	public void changeTo(String name) {
		changeTo(name, true);
	}

	public void changeTo(String name, boolean load) {
		System.out.println("kinugasa.game.GameLogicStorage.changeTo():" + name);
		if (!contains(name)) {
			throw new NameNotFoundException("logic " + name + " is not found");
		}
		if (current != null) {
			current.dispose();
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
