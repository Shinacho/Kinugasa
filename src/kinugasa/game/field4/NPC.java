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
import java.awt.geom.Rectangle2D;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.FourDirection;
import kinugasa.resource.Nameable;
import kinugasa.util.FrameTimeCounter;

/**
 *
 *
 * @vesion 1.0.0 - 2022/11/08_19:20:50<br>
 * @author Dra211<br>
 */
public class NPC extends PlayerCharacterSprite implements Nameable {

	private D2Idx targetIdx = null;
	private D2Idx currentIdx = null;
	private FrameTimeCounter nextMoveFrameTime;
	//
	private String name;
	private NPCMoveModel moveModel;
	private Vehicle vehicle;
	private FieldMap map;
	private String textId;
	//

	public NPC(String name, D2Idx initialLocationOnMap, NPCMoveModel moveModel, Vehicle vehicle, FieldMap map, String textId, float x, float y, float w, float h, D2Idx initialIdx, FourDirAnimation a, FourDirection initialDir) {
		super(x, y, w, h, initialIdx, a, initialDir);
		this.name = name;
		this.moveModel = moveModel;
		this.vehicle = vehicle;
		this.map = map;
		this.textId = textId;
		this.currentIdx = initialIdx.clone();
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

	public D2Idx getCurrentIdx() {
		return currentIdx;
	}

	public void setCurrentIdx(D2Idx currentIDXonMapData) {
		this.currentIdx = currentIDXonMapData;
	}

	public D2Idx getTargetIdx() {
		return targetIdx;
	}

	public FrameTimeCounter getNextMoveFrameTime() {
		return nextMoveFrameTime;
	}

	@Override
	public String getName() {
		return name;
	}
	private int stage = 0;
	private int lx, ly;

	@Override
	public void update() {
		super.update();
		switch (stage) {
			case 0:
				//初期化
				nextMoveFrameTime = new FrameTimeCounter(moveModel.nextMoveFrameTime(this, map));
				targetIdx = moveModel.getNextTargetIdx(this, map);
				nextStage();
				break;
			case 1:
				//移動フレーム判定
				if (!nextMoveFrameTime.isReaching()) {
					return;
				}
				nextStage();
				break;
			case 2:
				//移動実行
				if (getTargetIdx().equals(map.getCurrentIdx())) {
					stage = 0;
				}
				float speed = vehicle.getSpeed() / 2;
				Point2D.Float p = (Point2D.Float) getLocation().clone();
				if (getCurrentIdx().x > getTargetIdx().x) {
					p.x -= speed;
					to(FourDirection.WEST);
					lx--;
				} else if (getCurrentIdx().x < getTargetIdx().x) {
					p.x += speed;
					to(FourDirection.EAST);
					lx++;
				}
				if (getCurrentIdx().y > getTargetIdx().y) {
					p.y -= speed;
					to(FourDirection.NORTH);
					ly--;
				} else if (getCurrentIdx().y < getTargetIdx().y) {
					p.y += speed;
					to(FourDirection.SOUTH);
					ly++;
				}
				Point2D.Float prevLocation = (Point2D.Float) getLocation().clone();
				setLocation(p);
				D2Idx prev = currentIdx.clone();
				if (lx >= map.getChipW()) {
					lx = 0;
					currentIdx.x++;
				} else if (lx <= -map.getChipW()) {
					lx = 0;
					currentIdx.x--;
				}
				if (ly >= map.getChipH()) {
					ly = 0;
					currentIdx.y++;
				} else if (ly <= -map.getChipH()) {
					ly = 0;
					currentIdx.y--;
				}
				if (!prev.equals(currentIdx)) {
					if (map.getNpcStorage().get(currentIdx) != null && map.getNpcStorage().get(currentIdx) != this) {
						setLocation(prevLocation);
						return;
					}
				}
				if (currentIdx.equals(targetIdx)) {
					nextStage();
				}
				//目的地に近づいたら再設定
				if (new Rectangle2D.Float(targetIdx.x * getWidth(), targetIdx.y * getHeight(), map.getChipW(), map.getChipH()).contains(getLocation())) {
					nextStage();
				}
				break;
			case 3:
				//NPCの位置更新
				lx = ly = 0;
				float nx = map.getBaseLayer().getX() + getCurrentIdx().x * map.getChipW();
				float ny = map.getBaseLayer().getY() + getCurrentIdx().y * map.getChipH();
				setLocation(nx, ny);
				nextStage();
				break;
			case 4:
				//移動停止中の処理
				break;
			default:
				throw new AssertionError("undefined NPCs stage : " + this);
		}
	}

	public void setStage(int stage) {
		this.stage = stage;
	}

	private int prevStage;

	public void notMove() {
		prevStage = stage;
		setStage(4);
	}

	public void canMove() {
		stage = prevStage;
	}

	void nextStage() {
//		if (FieldMap.isDebugMode()) {
//			System.out.println("NPC " + getName() + " s stage : " + stage + " -> " + (stage + 1) + " " + this);
//		}
		stage++;
		if (stage >= 4) {
			stage = 0;
		}
	}

	@Override
	public void move() {
		super.move();
	}

	public String getTextID() {
		return textId;
	}

	@Override
	public String toString() {
		return "NPC{" + "targetIdx=" + targetIdx + ", currentIdx=" + currentIdx + ", nextMoveFrameTime=" + nextMoveFrameTime + ", name=" + name + ", vehicle=" + vehicle + ", textId=" + textId + ", stage=" + stage + ", lx=" + lx + ", ly=" + ly + '}';
	}

}
