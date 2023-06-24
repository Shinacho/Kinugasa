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

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.ui.TextStorageStorage;

/**
 * フィールドマップ関連の設定を正しい順番でロードするためのクラスです。
 *
 * @vesion 1.0.0 - 2022/11/16_18:59:45<br>
 * @author Shinacho<br>
 */
public class FieldMapXMLLoader {

	public FieldMapXMLLoader() {
	}
	private List<String> textStorage = new ArrayList<>();
	private List<String> mapChipAttrStorage = new ArrayList<>();
	private List<String> vehicleStorage = new ArrayList<>();
	private String initialVehicleName;
	private List<String> mapChipSetStorage = new ArrayList<>();
	private List<String> fieldMapStorage = new ArrayList<>();
	private String initialFieldMapName;
	private D2Idx initialIdx;

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
