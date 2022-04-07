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
package kinugasa.game.field;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.sound.SoundMap;
import kinugasa.resource.sound.SoundStorage;

/**
 *
 * @vesion 1.0.0 - 2021/11/29_7:26:08<br>
 * @author Dra211<br>
 */
public class FieldMapLoader {

	private String chipAttr;
	private String vehicle;
	private String initialVehicle;
	private List<String> chipSet = new ArrayList<>();
	private String fieldMapBuilder;
	private Map<String, String> soundMap = new HashMap<>();
	private String soundMapName;
	private String soundName;
	private int x, y;

	public FieldMapLoader mapChipAttr(String fileName) {
		this.chipAttr = fileName;
		return this;
	}

	public FieldMapLoader vehicle(String fileName) {
		this.vehicle = fileName;
		return this;
	}

	public FieldMapLoader mapChipSet(String fileName) {
		this.chipSet.add(fileName);
		return this;
	}

	public FieldMapLoader fieldMapBuilder(String fileName) {
		this.fieldMapBuilder = fileName;
		return this;
	}

	public FieldMapLoader charPoint(int x, int y) {
		this.x = x;
		this.y = y;
		return this;
	}

	public FieldMapLoader sound(String mapName, String path) {
		soundMap.put(mapName, path);
		return this;
	}

	public FieldMapLoader initialVehicle(String name) {
		this.initialVehicle = name;
		return this;
	}

	public FieldMapLoader initialPlaySound(String key, String name) {
		this.soundMapName = key;
		this.soundName = name;
		return this;
	}

	public FieldMapLoader debugMode() {
		FieldMap.debug = true;
		return this;
	}

	public FieldMapLoader() {
	}

	public void load() {
		MapChipAttributeSotrage.getInstance().readFromXML(chipAttr);
		VehicleStorage.getInstance().readFromXML(vehicle);
		VehicleStorage.getInstance().setCurrentVehicle(initialVehicle);
		chipSet.forEach(v -> MapChipSetStorage.getInstance().readFromXML(v));
		FieldMapStorage.getInstance().readFromXML(fieldMapBuilder);
		soundMap.keySet().stream().map(key -> {
			if (!SoundStorage.getInstance().contains(key)) {
				SoundStorage.getInstance().add(new SoundMap(key));
			}
			return key;
		}).forEachOrdered(key -> {
			SoundStorage.getInstance().get(key).add(new SoundBuilder(soundMap.get(key)).builde());
		});
		if (soundMapName != null && soundName != null) {
			SoundStorage.getInstance().get(soundMapName).get(soundName).load().stopAndPlay();
		}
		FieldMap.setPlayerLocationBuf(x, y);
	}

}
