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
package kinugasa.graphics;

import java.util.Arrays;
import java.util.List;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_12:03:01<br>
 * @author Shinacho<br>
 */
public class ColorException extends RuntimeException{

	public ColorException(Exception ex) {
		super(ex);
	}
	
	public ColorException(String... msg) {
		super(Arrays.toString(msg) + " is illegal color value");
	}
	
	public ColorException(List<String> msg) {
		super(msg + " is illegal color value");
	}

	
}
