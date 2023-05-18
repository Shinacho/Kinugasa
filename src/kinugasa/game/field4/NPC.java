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

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.FourDirection;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 *
 * @vesion 1.0.0 - 2022/11/08_19:20:50<br>
 * @author Shinacho<br>
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
		if (vehicle != null) {
			setSpeed(vehicle.getSpeed());
		}
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

	public void setTargetIdx(D2Idx targetIdx) {
		this.targetIdx = targetIdx;
		outerTarget = true;
		stage = 0;
		moveStop = false;
	}

	public FrameTimeCounter getNextMoveFrameTime() {
		return nextMoveFrameTime;
	}

	public boolean isMoveStop() {
		return moveStop;
	}

	public boolean isOuterTarget() {
		return outerTarget;
	}

	public void setMoveStop(boolean moveStop) {
		this.moveStop = moveStop;
	}

	@Override
	public String getName() {
		return name;
	}
	private int stage = 0;
	private int lx, ly;
	private boolean outerTarget = false;
	private boolean moveStop = false;

	@Override
	public void update() {
		super.update();
		if (moveStop) {
			return;
		}
		switch (stage) {
			case 0:
				if (outerTarget) {
					nextMoveFrameTime = new FrameTimeCounter(1);
					assert targetIdx != null : "NPC target is null " + getName();
					nextStage();
					break;
				}
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
					//移動不能のため戻る
					stage = 0;
				}
				float speed = vehicle == null ? 1f : vehicle.getSpeed() / 2;
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
				moveStop = outerTarget;
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

	public static NPC readFromXML(String filePath) {
		//	<npc name="N02" initialIdx="6,4" NPCMoveModel="ROUND_1" vehicle="WALK"
		//textID="010" image="resource/char/pipo-charachip007a.png"
		//frame="32" s="0,32,32" w="32,32,32" e="64,32,32" n="96,32,32" initialDir="EAST"/>

		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}

		XMLElement root = file.load().getFirst();

		XMLElement e = root.getElement("npc").get(0);

		String name = e.getAttributes().get("name").getValue();
		String initialIdxStr = e.getAttributes().get("initialIdx").getValue();
		D2Idx idx = new D2Idx(Integer.parseInt(initialIdxStr.split(",")[0]), Integer.parseInt(initialIdxStr.split(",")[1]));

		NPCMoveModel moveModel = NPCMoveModelStorage.getInstance().get(e.getAttributes().get("NPCMoveModel").getValue());
		Vehicle v = VehicleStorage.getInstance().get(e.getAttributes().get("vehicle").getValue());
		String textID = e.getAttributes().get("textID").getValue();
		int frame = e.getAttributes().get("frame").getIntValue();
		BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
		int sx, sw, sh;
		int ex, ew, eh;
		int wx, ww, wh;
		int nx, nw, nh;
		String[] cs = e.getAttributes().get("s").getValue().split(",");
		String[] ce = e.getAttributes().get("e").getValue().split(",");
		String[] cw = e.getAttributes().get("w").getValue().split(",");
		String[] cn = e.getAttributes().get("n").getValue().split(",");
		sx = Integer.parseInt(cs[0]);
		sw = Integer.parseInt(cs[1]);
		sh = Integer.parseInt(cs[2]);
		ex = Integer.parseInt(ce[0]);
		ew = Integer.parseInt(ce[1]);
		eh = Integer.parseInt(ce[2]);
		wx = Integer.parseInt(cw[0]);
		ww = Integer.parseInt(cw[1]);
		wh = Integer.parseInt(cw[2]);
		nx = Integer.parseInt(cn[0]);
		nw = Integer.parseInt(cn[1]);
		nh = Integer.parseInt(cn[2]);
		Animation south = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(sx, sw, sh).images());
		Animation west = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(wx, ww, wh).images());
		Animation east = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(ex, ew, eh).images());
		Animation north = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(nx, nw, nh).images());
		FourDirAnimation anime = new FourDirAnimation(south, west, east, north);
		FourDirection initialDir = FourDirection.valueOf(e.getAttributes().get("initialDir").getValue());
		int w = sw;
		int h = sh;
		FieldMapLayerSprite baseLayer = FieldMap.getCurrentInstance().getBaseLayer();
		int chipW = (int) (baseLayer.getChip(0, 0).getImage().getWidth() * FieldMap.getCurrentInstance().getMg());
		int chipH = (int) (baseLayer.getChip(0, 0).getImage().getHeight() * FieldMap.getCurrentInstance().getMg());
		float x = baseLayer.getX() + idx.x * chipW;
		float y = baseLayer.getY() + idx.y * chipH;
		NPC npc = new NPC(name, idx, moveModel, v,
				FieldMap.getCurrentInstance(), textID, x, y, w, h, idx, anime, initialDir);
		FieldMap.getCurrentInstance().getNpcStorage().add(npc);

		file.dispose();
		return npc;
	}
}
