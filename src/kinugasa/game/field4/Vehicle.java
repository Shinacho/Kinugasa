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

import java.util.Arrays;
import java.util.List;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_7:10:34<br>
 * @author Shinacho<br>
 */
public class Vehicle implements Nameable {

	private String name;
	private float speed;
	private List<MapChipAttribute> stepOn;

	public Vehicle(String name, float speed, List<MapChipAttribute> stepOn) {
		this.name = name;
		this.speed = speed;
		this.stepOn = stepOn;
		stepOn.add(MapChipAttributeStorage.getInstance().get("VOID"));
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "Vehicle{" + "name=" + name + ", speed=" + speed + ", stepOn=" + stepOn + '}';
	}

	public float getSpeed() {
		return speed;
	}

	public void setSpeed(float speed) {
		this.speed = speed;
	}

	public void setStepOn(List<MapChipAttribute> stepOn) {
		this.stepOn = stepOn;
	}

	public List<MapChipAttribute> getStepOn() {
		return stepOn;
	}

	public boolean isStepOn(MapChipAttribute a) {
		return getStepOn().contains(a);
	}

	public boolean isStepOn(List<MapChip> c) {
		return c.stream().allMatch(a -> isStepOn(a.getAttr()));
	}

}
