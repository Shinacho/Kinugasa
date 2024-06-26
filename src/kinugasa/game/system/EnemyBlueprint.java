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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_21:02:30<br>
 * @author Shinacho<br>
 */
public class EnemyBlueprint {

	private static Map<String, Character> enemyNo = new HashMap<>();

	public static String getEnemyNo(String id) {
		if (enemyNo.containsKey(id)) {
			char c = enemyNo.get(id);
			c++;
			if (c > 'Z') {
				c = 'A';
			}
			enemyNo.put(id, c);
			return c + "";
		} else {
			enemyNo.put(id, (char) ('A' - 1));
			return getEnemyNo(id);
		}
	}

	public static void initEnemyNoMap() {
		enemyNo.clear();
	}

	private String fileName;

	public EnemyBlueprint(String fileName) {
		this.fileName = fileName;
	}

	public Enemy create() {
		Enemy e = new Enemy(fileName);//newするとloadされる
		String no = getEnemyNo(e.getId());
		e.setId(e.getId() + no);
		e.setNameNo(no);
		return e;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 29 * hash + Objects.hashCode(this.fileName);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final EnemyBlueprint other = (EnemyBlueprint) obj;
		return Objects.equals(this.fileName, other.fileName);
	}

	@Override
	public String toString() {
		return "EnemyBlueprint{" + "fileName=" + fileName + '}';
	}

}
