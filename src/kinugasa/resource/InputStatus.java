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
package kinugasa.resource;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_9:52:23<br>
 * @author Shinacho<br>
 */
public enum InputStatus {
	NOT_LOADED,
	LOADED,;
	
	public boolean is(InputStatus s){
		return s == this;
	}
	
	public boolean loaded(){
		return this == LOADED;
	}
}
