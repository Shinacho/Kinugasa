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
package kinugasa.game.system;

import kinugasa.resource.Storage;

/**
 * マネーシステムは複数の種類の金銭を持てる、財布管理システムです。
 *
 * @vesion 1.0.0 - 2022/11/24_22:19:22<br>
 * @author Shinacho<br>
 */
public class MoneySystem extends Storage<Money> {

	private static final MoneySystem INSTANCE = new MoneySystem();

	private MoneySystem() {
	}

	static MoneySystem getInstance() {
		return INSTANCE;
	}

	public void addMoneyType(String name) {
		add(new Money(name));
	}

}
