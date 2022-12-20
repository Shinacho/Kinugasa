/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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

import java.util.List;

/**
 * マップ上の1タイルを透過的に扱うクラスです.
 *
 * @vesion 1.0.0 - 2022/11/08_20:37:29<br>
 * @author Dra211<br>
 */
public class FieldMapTile {

	private List<MapChip> chip;
	private NPC npc;
	private PlayerCharacterSprite playerCharacter;
	private List<FieldEvent> event;
	private Node node;

	public FieldMapTile(List<MapChip> chip, NPC npc, PlayerCharacterSprite playerCharacter, List<FieldEvent> event, Node node) {
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

	public NPC getNpc() {
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

	public PlayerCharacterSprite getPlayerCharacter() {
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
