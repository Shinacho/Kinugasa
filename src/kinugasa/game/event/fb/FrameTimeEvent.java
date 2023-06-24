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
package kinugasa.game.event.fb;

import java.util.function.Supplier;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_8:13:39<br>
 * @author Shinacho<br>
 */
public abstract class FrameTimeEvent<T>{

	private long frame;

	public FrameTimeEvent(long frame) {
		this.frame = frame;
	}

	public long getFrame() {
		return frame;
	}
	
	public abstract T exec();

	
	
}
