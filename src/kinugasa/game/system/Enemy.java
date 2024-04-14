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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_20:49:18<br>
 * @author Shinacho<br>
 */
public final class Enemy extends Actor {

	private ArrayList<DropItem> dropItem = new ArrayList<>();
	private EnemyAI ai;

	public static Enemy cloneOf(Actor a) {
		Enemy e = new Enemy(a);
		e.dropItem = new ArrayList<>();
		return e;
	}

	private Enemy(Actor a) {
		super(a.getId() + "_CLONE", a);
	}

	Enemy(String filePath) {
		super(filePath);
		getSprite().setMe(this);
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		super.readFromXML(filePath);
		XMLFile f = new XMLFile(filePath);
		if (!f.exists()) {
			throw new FileNotFoundException(f);
		}
		try {
			XMLElement root = f.load().getFirst();
			//ドロップアイテム
			for (XMLElement e : root.getElement("dropItem")) {
				String id = e.getAttributes().get("id").getValue();
				int n = e.getAttributes().get("n").getIntValue();
				float p = e.getAttributes().get("p").getFloatValue();
				DropItem i = DropItem.itemOf(ActionStorage.getInstance().itemOf(id), n, p);
				dropItem.add(i);
			}
			for (XMLElement e : root.getElement("dropMaterial")) {
				String id = e.getAttributes().get("id").getValue();
				int n = e.getAttributes().get("n").getIntValue();
				float p = e.getAttributes().get("p").getFloatValue();
				DropItem i = DropItem.materialOf(MaterialStorage.getInstance().get(id), n, p);
				dropItem.add(i);
			}
			//AI
			if (!root.hasElement("ai")) {
				throw new IllegalXMLFormatException("enemy, but ai is undefined. " + this);
			}
			if (root.hasElement("hardAi") && GameSystem.getDifficulty().is敵AI2を使用()) {
				ai = EnemyAIImpl.valueOf(
						root.getElement("hardAi").get(0).getAttributes().get("name").getValue()
				);
			} else {
				ai = EnemyAIImpl.valueOf(
						root.getElement("ai").get(0).getAttributes().get("name").getValue()
				);
			}
			f.dispose();
		} catch (Exception ex) {
			throw new IllegalXMLFormatException(ex);
		}

	}

	void update() {
		getSprite().update();
		if (getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊, ConditionKey.気絶, ConditionKey.逃走した)) {
			getSprite().getProgressBarSprite1().setVisible(false);
			getSprite().getProgressBarSprite2().setVisible(false);
			getSprite().setNameVisible(false);
		}
	}

	@Override
	public EnemySprite getSprite() {
		return (EnemySprite) super.getSprite();
	}

	//移動が返される可能性がある
	//逃走や防御、回避の可能性もある。状態はない。
	public ActionTarget getActionTgt() {
		ActionTarget res = ai.getNextAction(this);
		if (GameSystem.isDebugMode()) {
			GameLog.print(" selected action : " + getVisibleName() + " -> " + res);
		}
		return res;
	}

	public void setMoveTgtLocation() {
		Action a = getActionTgt().getAction();
		getSprite().setTargetLocation(
				ai.targetLocation(this, a),
				getStatus().getEffectedArea(a));
	}

	public void setAI(EnemyAI ai) {
		this.ai = ai;
	}

	public ArrayList<DropItem> getDropItem() {
		return dropItem;
	}

	private String nameNo;

	public void setNameNo(String nameNo) {
		this.nameNo = nameNo;
	}

	public String getNameNo() {
		return nameNo;
	}

	public String getVisibleNameNoNumber() {
		return super.getVisibleName();
	}

	@Override
	public String getVisibleName() {
		return super.getVisibleName() + nameNo;
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

	@Override
	public String toString() {
		return "Enemy{" + getVisibleName() + '}';
	}

}
