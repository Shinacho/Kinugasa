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

import kinugasa.game.GraphicsContext;
import kinugasa.object.Model;

/**
 *
 * @vesion 1.0.0 - 2022/11/11_12:29:09<br>
 * @author Shinacho<br>
 */
public abstract class MapNameModel extends Model{
	
	public abstract void drawMapName(FieldMap fm, GraphicsContext g);
	
	public abstract void reset();

	@Override
	public MapNameModel clone() {
		return (MapNameModel) super.clone(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
	}

	
}
