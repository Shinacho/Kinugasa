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

import kinugasa.graphics.Animation;

/**
 *
 * @vesion 1.0.0 - May 30, 2023_11:00:40 AM<br>
 * @author Shinacho<br>
 */
public abstract class CustomActionEvent extends ActionEvent {

	public CustomActionEvent(String id) {
		super(id);
	}

	/**
	 * アクションのサウンド再生は自動で行われますが、P判定を含めた処理は実施する必要があります。
	 *
	 * @param tgt アクションのターゲット情報
	 * @return アクションの成否。
	 */
	@Override
	public abstract ActionEventResult exec(ActionTarget tgt);

}
