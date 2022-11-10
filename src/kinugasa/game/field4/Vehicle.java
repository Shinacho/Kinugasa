/*
 * The MIT License
 *
 * Copyright 2021 Dra.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package kinugasa.game.field4;

import java.util.Arrays;
import java.util.List;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_7:10:34<br>
 * @author Dra211<br>
 */
public class Vehicle implements Nameable {

	private String name;
	private float speed;
	private List<MapChipAttribute> stepOn;

	public Vehicle(String name, float speed, List<MapChipAttribute> stepOn) {
		this.name = name;
		this.speed = speed;
		this.stepOn = stepOn;
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
