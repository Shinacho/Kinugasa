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
public class CreateEvent<T> extends FrameTimeEvent {

	private Supplier<T> supplier;

	public CreateEvent(long frame, Supplier<T> supplier) {
		super(frame);
		this.supplier = supplier;
	}

	@Override
	public T exec() {
		return supplier.get();
	}

	@Override
	public String toString() {
		return "CreateEvent{" + "supplier=" + supplier + '}';
	}

}
