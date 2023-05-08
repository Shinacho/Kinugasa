/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.resource.sound.SoundLoader;

/**
 * フィールドマップ関連の設定を正しい順番でロードするためのクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_18:59:45<br>
 * @author Shinacho<br>
 */
public class FieldMapXMLLoader {

	public FieldMapXMLLoader() {
	}
	private List<String> soundLoader = new ArrayList<>();
	private List<String> textStorage = new ArrayList<>();
	private List<String> mapChipAttrStorage = new ArrayList<>();
	private List<String> vehicleStorage = new ArrayList<>();
	private String initialVehicleName;
	private List<String> mapChipSetStorage = new ArrayList<>();
	private List<String> fieldMapStorage = new ArrayList<>();
	private String initialFieldMapName;
	private D2Idx initialIdx;

	public FieldMapXMLLoader addSound(String fileName) {
		soundLoader.add(fileName);
		return this;
	}

	public FieldMapXMLLoader addMapChipAttr(String fileName) {
		mapChipAttrStorage.add(fileName);
		return this;
	}

	public FieldMapXMLLoader addTextStorage(String fileName) {
		textStorage.add(fileName);
		return this;
	}

	public FieldMapXMLLoader addVehicle(String fileName) {
		vehicleStorage.add(fileName);
		return this;
	}

	public FieldMapXMLLoader setInitialVehicleName(String name) {
		this.initialVehicleName = name;
		return this;
	}

	public FieldMapXMLLoader addMapChipSet(String fileName) {
		mapChipSetStorage.add(fileName);
		return this;
	}

	public FieldMapXMLLoader addFieldMapStorage(String fileName) {
		fieldMapStorage.add(fileName);
		return this;
	}

	public FieldMapXMLLoader setInitialFieldMapName(String name) {
		this.initialFieldMapName = name;
		return this;
	}

	public FieldMapXMLLoader setInitialLocation(D2Idx idx) {
		this.initialIdx = idx;
		return this;
	}

	public List<String> getSoundLoader() {
		return soundLoader;
	}

	public List<String> getMapChipAttrStorage() {
		return mapChipAttrStorage;
	}

	public List<String> getVehicleStorage() {
		return vehicleStorage;
	}

	public String getInitialVehicleName() {
		return initialVehicleName;
	}

	public List<String> getMapChipSetStorage() {
		return mapChipSetStorage;
	}

	public List<String> getFieldMapStorage() {
		return fieldMapStorage;
	}

	public String getInitialFieldMapName() {
		return initialFieldMapName;
	}

	public D2Idx getInitialIdx() {
		return initialIdx;
	}

	public List<String> getTextStorage() {
		return textStorage;
	}

	public FieldMap load() throws IllegalStateException {
		soundLoader.forEach(v -> SoundLoader.loadList(v));

		textStorage.forEach(v -> TextStorageStorage.getInstance().readFromXML(v));

		if (mapChipAttrStorage.isEmpty()) {
			throw new IllegalStateException("mapChipAttr is empty");
		}
		mapChipAttrStorage.forEach(v -> MapChipAttributeStorage.getInstance().readFromXML(v));

		if (vehicleStorage.isEmpty()) {
			throw new IllegalStateException("vehicle is empty");
		}
		vehicleStorage.forEach(v -> VehicleStorage.getInstance().readFromXML(v));

		if (initialVehicleName.isEmpty()) {
			throw new IllegalStateException("initial vehicle is empty");
		}
		VehicleStorage.getInstance().setCurrentVehicle(initialVehicleName);

		if (mapChipSetStorage.isEmpty()) {
			throw new IllegalStateException("mapChipSet is empty");
		}
		mapChipSetStorage.forEach(v -> MapChipSetStorage.getInstance().readFromXML(v));

		if (fieldMapStorage.isEmpty()) {
			throw new IllegalStateException("fieldMapStorage is empty");
		}
		fieldMapStorage.forEach(v -> FieldMapStorage.getInstance().readFromXML(v));

		if (initialFieldMapName.isEmpty()) {
			throw new IllegalStateException("initialFieldMapName is empty");
		}
		FieldMap fm = FieldMapStorage.getInstance().get(initialFieldMapName).build();

		if (initialIdx == null) {
			throw new IllegalStateException("initialIDX is null");
		}
		fm.setCurrentIdx(initialIdx);
		return fm;
	}

}
