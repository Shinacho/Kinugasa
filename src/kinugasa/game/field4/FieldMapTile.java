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

import kinugasa.game.system.NPCSprite;
import kinugasa.game.system.PCSprite;
import java.util.List;

/**
 * マップ上の1タイルを透過的に扱うクラスです.
 *
 * @vesion 1.0.0 - 2022/11/08_20:37:29<br>
 * @author Shinacho<br>
 */
public class FieldMapTile {

	private List<MapChip> chip;
	private NPCSprite npc;
	private PCSprite playerCharacter;
	private List<FieldEvent> event;
	private Node node;

	public FieldMapTile(List<MapChip> chip, NPCSprite npc, PCSprite playerCharacter, List<FieldEvent> event, Node node) {
		this.chip = chip;
		this.npc = npc;
		this.playerCharacter = playerCharacter;
		this.event = event;
		this.node = node;
	}

	public List<FieldEvent> getEvent() {
		return event;
	}

	public Node getNode() {
		return node;
	}

	public NPCSprite getNpc() {
		return npc;
	}

	public List<MapChip> getChip() {
		return chip;
	}

	public boolean canStep(Vehicle v) {
		return v.isStepOn(chip);
	}

	public boolean canStep() {
		return VehicleStorage.getInstance().getCurrentVehicle().isStepOn(chip);
	}

	public PCSprite getPlayerCharacter() {
		return playerCharacter;
	}

	public boolean hasPC() {
		return playerCharacter != null;
	}

	public boolean hasNode() {
		return node != null;
	}

	public boolean hasInNode() {
		return node != null && node.getMode() == Node.Mode.INOUT;
	}

	public MapChipAttribute get0Attr() {
		return chip.get(0).getAttr();
	}

	@Override
	public String toString() {
		return "FieldMapTile{" + "chip=" + chip + ", npc=" + npc + ", playerCharacter=" + playerCharacter + ", event=" + event + ", node=" + node + '}';
	}

}
