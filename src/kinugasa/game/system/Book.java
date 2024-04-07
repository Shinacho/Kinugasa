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

import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.I18N;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_19:23:33<br>
 * @author Shinacho<br>
 */
public class Book implements Nameable, Cloneable {

	private Action action;

	public Book(Action action) {
		if (action.getType() != ActionType.魔法) {
			throw new GameSystemException("this book action is not a magic : " + action);
		}
		this.action = action;
	}

	public String getID() {
		return action.getId();
	}

	public String getVisibleName() {
		return action.getVisibleName() + I18N.get(GameSystemI18NKeys.の魔術書);
	}

	@Deprecated
	@Override
	public String getName() {
		return action.getId();
	}

	public Action getAction() {
		return action;
	}

	public List<BookPage> getPages() {
		return action.getAllEvents().stream().map(p -> new BookPage(p)).collect(Collectors.toList());
	}

	@Override
	public String toString() {
		return "Book{" + "action=" + action + '}';
	}

	//自動計算された金額を返す
	public int getPrice() {
		var v = action.getAllEvents().stream().map(p -> new BookPage(p)).mapToInt(BookPage::getPrice).sum();
		if (action.getTgtType().is全員()) {
			v *= 3;
		} else if (action.getTgtType().isグループ()) {
			v *= 1.5f;
		}
		return v;
	}

	@Override
	protected Book clone() {
		try {
			return (Book) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
