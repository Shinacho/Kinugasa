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
package kinugasa.object.movemodel;

import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;

/**
 * 複数のMovingModelを実行できるMovingModelの拡張です.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_18:24:13<br>
 * @author Shinacho<br>
 */
public class CompositeMove extends MovingModel {

	private MovingModel[] models;

	public CompositeMove(MovingModel... models) {
		this.models = models;
	}

	public MovingModel[] getModels() {
		return models;
	}

	public void setModels(MovingModel[] models) {
		this.models = models;
	}

	@Override
	public void move(BasicSprite s) {
		for (MovingModel model : models) {
			model.move(s);
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public CompositeMove clone() {
		CompositeMove e = (CompositeMove) super.clone();
		e.models = this.models.clone();
		return e;
	}
}
