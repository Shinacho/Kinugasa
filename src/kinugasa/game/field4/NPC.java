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

import java.awt.geom.Point2D;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.resource.Nameable;
import kinugasa.util.FrameTimeCounter;

/**
 *
 *
 * @vesion 1.0.0 - 2022/11/08_19:20:50<br>
 * @author Dra211<br>
 */
public class NPC extends FieldMapCharacter implements Nameable {

	private D2Idx targetLocationOnMap = null;
	private D2Idx currentIDXonMapData = null;
	private FrameTimeCounter nextMoveFrameTime;
	//
	private String name;
	private NPCMoveModel moveModel;
	private Vehicle vehicle;
	private FieldMap map;
	private String textId;
	//

	public NPC(String name, D2Idx initialLocationOnMap, NPCMoveModel moveModel, Vehicle vehicle, FieldMap map, String textId, float x, float y, float w, float h, D2Idx initialLocation, FourDirAnimation a, FourDirection initialDir) {
		super(x, y, w, h, initialLocation, a, initialDir);
		this.name = name;
		this.moveModel = moveModel;
		this.vehicle = vehicle;
		this.map = map;
		this.textId = textId;
		this.currentIDXonMapData = initialLocation;
		setSpeed(vehicle.getSpeed());
		to(initialDir);
	}

	public NPCMoveModel getMoveModel() {
		return moveModel;
	}

	public void setMoveModel(NPCMoveModel moveModel) {
		this.moveModel = moveModel;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public D2Idx getCurrentIDXonMapData() {
		return currentIDXonMapData;
	}

	@Override
	public String getName() {
		return name;
	}
	private int stage = 0;

	@Override
	public void update() {
		super.update();
		switch (stage) {
			case 0:
				//初期化
				nextMoveFrameTime = new FrameTimeCounter(moveModel.nextMoveFrameTime(this, map));
				targetLocationOnMap = moveModel.getNextTargetLocationOnMap(this, map);
				nextStage();
				break;
			case 1:
				//移動フレーム判定
				if (!nextMoveFrameTime.isReaching()) {
					return;
				}
				nextMoveFrameTime = new FrameTimeCounter(moveModel.nextMoveFrameTime(this, map));
				nextStage();
				break;
			case 2:
				//移動実行
				//目的地までの角度算出
				float tgtX = map.getBaseLayer().getX() + targetLocationOnMap.x * getWidth();
				float tgtY = map.getBaseLayer().getY() + targetLocationOnMap.y * getHeight();
				KVector v = new KVector();
				v.setAngle(getLocation(), new Point2D.Float(tgtX, tgtY));
				v.setSpeed(vehicle.getSpeed());
				move();

				//カレントインデックスの更新
				//目的地に近づいたら再設定
				if (currentIDXonMapData.equals(targetLocationOnMap)) {
					nextStage();
				}
				break;
			default:
				throw new AssertionError("undefined NPCs stage : " + this);
		}
	}

	private void nextStage() {
		if (FieldMap.isDebugMode()) {
			System.out.println("NPC " + getName() + " s stage : " + stage + " -> " + (stage + 1) + " " + this);
		}
		stage++;
		if (stage >= 3) {
			stage = 0;
		}
	}

	@Override
	public void move() {
		super.move();
	}

	public Text getText(TextStorage ts) {
		return ts.get(textId);
	}

	@Override
	public String toString() {
		return "NPC{" + "targetLocationOnMap=" + targetLocationOnMap + ", currentIDXonMapData=" + currentIDXonMapData + ", nextMoveFrameTime=" + nextMoveFrameTime + ", name=" + name + ", vehicle=" + vehicle + ", textId=" + textId + ", stage=" + stage + '}';
	}

}
