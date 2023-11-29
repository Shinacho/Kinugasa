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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.D2Idx;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FourDirAnimation;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/10_20:35:58<br>
 * @author Shinacho<br>
 */
public class PCSprite extends AnimationSprite implements XMLFileSupport, Cloneable {

	private D2Idx initialIdx;
	private FourDirection currentDir;
	private FourDirAnimation fAnimation;
	private int order = 0;
	private Point2D.Float tgt;
	private boolean moving = false;
	private static final Color SHADOW = new Color(0, 0, 0, 128);
	private int stage = 0;
	private int ly, lx;
	private D2Idx currentIdx, targetIdx;

	@Deprecated
	public PCSprite(float x, float y, float size) {
		super(x, y, size, size);
	}

	public PCSprite(float x, float y, float w, float h, D2Idx initialLocation, FourDirAnimation a, FourDirection initialDir) {
		super(x, y, w, h, a.get(initialDir));
		this.initialIdx = initialLocation;
		this.fAnimation = a;
		this.currentDir = initialDir;
	}

	public PCSprite(XMLElement e) {
		parse(e);
	}

	public PCSprite(String f) {
		readFromXML(f);
	}

	public D2Idx getInitialIdx() {
		return initialIdx.clone();
	}

	public FourDirection getCurrentDir() {
		return currentDir;
	}

	public FourDirAnimation getFourDirAnimation() {
		return fAnimation;
	}

	public void to(FourDirection dir) {
		setAnimation(fAnimation.get(dir));
		currentDir = dir;
		getAnimation().update();
		setImage(getAnimation().getCurrentImage());
	}
	private boolean shadow = true;

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	public boolean isShadow() {
		return shadow;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (shadow) {
			Graphics2D g2 = g.create();
			g2.setColor(SHADOW);
			g2.fillOval(
					(int) (getX() + getWidth() / 4),
					(int) (getY() + getHeight() - 10),
					(int) (getWidth() / 2),
					(int) 12f);
			g2.dispose();
		}
		super.draw(g);
	}

	public void setCurrentIdx(D2Idx currentIdx) {
		assert currentIdx != null : "current is null";
		this.currentIdx = currentIdx;
	}

	public D2Idx getCurrentIdx() {
		return currentIdx;
	}

	public D2Idx getTargetIdx() {
		return targetIdx;
	}

	public void setTargetIdx(D2Idx targetIdx) {
		this.targetIdx = targetIdx;
	}

	@Override
	public void update() {
		if (!FieldMap.getPlayerCharacter().isEmpty() && FieldMap.getPlayerCharacter().get(0).equals(this)) {
			return;
		}
		if (getVector().getSpeed() != 0) {
			super.update();
		}
	}

	public void updateAnimation() {
		getAnimation().update();
		setImage(getAnimation().getCurrentImage());
	}

	public void updatePartyMemberLocation(FieldMap map, D2Idx tgt) {
		update();
		switch (stage) {
			case 0:
				//初期化
				if (!tgt.equals(currentIdx)) {
					getAnimation().setStop(false);
					targetIdx = tgt.clone();
					nextStage();
				}
				break;
			case 1:
				//移動実行
				float speed = getSpeed();
				if (speed == 0) {
					setSpeed(VehicleStorage.getInstance().getCurrentVehicle().getSpeed());
				}
				float nx = map.getBaseLayer().getX() + targetIdx.x * map.getChipW();
				float ny = map.getBaseLayer().getY() + targetIdx.y * map.getChipH();
				Point2D.Float tgtL = new Point2D.Float(nx, ny);

				KVector v = new KVector();
				v.setAngle(getLocation(), tgtL);
				setAngle(v.angle);
				to(getVector().round());
				move();
				if (tgtL.distance(getLocation()) < getSpeed() || tgtL.equals(getLocation())) {
					setLocation(nx, ny);
					nextStage();
				}
				break;
			case 2:
				//PCの位置更新
				lx = ly = 0;
				setSpeed(0);
				setAngle(0);
				currentIdx = targetIdx.clone();
				getAnimation().setStop(true);
				nextStage();
				break;
			case 3:
				//移動停止中の処理
				break;
			default:
				throw new AssertionError("undefined PCs stage : " + this);
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
		if (stage >= 3) {
			stage = 0;
		}
	}

	public int getStage() {
		return stage;
	}

	void setOrder(int order) {
		this.order = order;
	}

	int getOrder() {
		return order;
	}

	public void dirTo(Actor c, float speed) {
		KVector v = new KVector();
		v.setAngle(getCenter(), c.getSprite().getCenter());
		v.setSpeed(speed);
		setVector(v);
	}

	public void dirTo(Actor c, String vehicleName) {
		KVector v = new KVector();
		v.setAngle(getCenter(), c.getSprite().getCenter());
		v.setSpeed(VehicleStorage.getInstance().get(vehicleName).getSpeed());
		setVector(v);
	}

	public void dirTo(Point2D.Float tgt, String vehicleName) {
		KVector v = new KVector();
		v.setAngle(getCenter(), tgt);
		v.setSpeed(VehicleStorage.getInstance().get(vehicleName).getSpeed());
		setVector(v);
	}

	public void dirTo(Point2D.Float tgt, float speed) {
		KVector v = new KVector();
		v.setAngle(getCenter(), tgt);
		v.setSpeed(speed);
		setVector(v);
	}

	public void setTargetLocation(Point2D.Float p, int area) {
		tgt = (Point2D.Float) p.clone();
		dirTo(p, VehicleStorage.getInstance().getCurrentVehicle().getSpeed());
		moving = true;
	}

	public void unsetTarget() {
		tgt = null;
		moving = false;
		setVector(new KVector(0, 0));
	}

	public boolean isMoving() {
		return moving;
	}

	protected boolean animationUpdate = true;

	public void setAnimationUpdate(boolean animationUpdate) {
		this.animationUpdate = animationUpdate;
	}

	@Override
	public void move() {
		super.move();
		if (animationUpdate) {
			getAnimation().update();
			setImage(getAnimation().getCurrentImage());
		}
	}

	public void moveToTgt() {
		if (!moving) {
			return;
		}
		move();
		if (getCenter().distance(tgt) < VehicleStorage.getInstance().getCurrentVehicle().getSpeed()) {
			setLocationByCenter(tgt);
			setSpeed(0);
			moving = false;
			tgt = null;
		}
	}

	public boolean isPlayer() {
		return true;
	}

	private void parse(XMLElement e) {
		if (e.getAttributes().contains("tc")) {
			//アニメーション
			int w = e.getAttributes().get("w").getIntValue();
			int h = e.getAttributes().get("h").getIntValue();
			int[] tc = e.getAttributes().get("tc").safeParseInt(",");
			int ny = e.getAttributes().get("ny").getIntValue();
			int sy = e.getAttributes().get("sy").getIntValue();
			int ey = e.getAttributes().get("ey").getIntValue();
			int wy = e.getAttributes().get("wy").getIntValue();
			setSize(w, h);
			String fileName = e.getAttributes().get("image").getValue();

			this.fAnimation = new FourDirAnimation(
					new Animation(new FrameTimeCounter(tc), new SpriteSheet(fileName).rows(sy, w, h).images()),
					new Animation(new FrameTimeCounter(tc), new SpriteSheet(fileName).rows(wy, w, h).images()),
					new Animation(new FrameTimeCounter(tc), new SpriteSheet(fileName).rows(ey, w, h).images()),
					new Animation(new FrameTimeCounter(tc), new SpriteSheet(fileName).rows(ny, w, h).images()));

		} else {
			//イメージ
			int w = e.getAttributes().get("w").getIntValue();
			int h = e.getAttributes().get("h").getIntValue();
			setSize(w, h);
			String fileName = e.getAttributes().get("image").getValue();

			this.fAnimation = new FourDirAnimation(
					new Animation(TimeCounter.FALSE, new SpriteSheet(fileName).rows(0, w, h).images()),
					new Animation(TimeCounter.FALSE, new SpriteSheet(fileName).rows(0, w, h).images()),
					new Animation(TimeCounter.FALSE, new SpriteSheet(fileName).rows(0, w, h).images()),
					new Animation(TimeCounter.FALSE, new SpriteSheet(fileName).rows(0, w, h).images()));
		}
		if (e.hasAttribute("idx")) {
			int[] idx = e.getAttributes().get("idx").safeParseInt(",");
			this.initialIdx = new D2Idx(idx[0], idx[1]);
		}
		if (e.hasAttribute("dir")) {
			this.currentDir = e.getAttributes().get("dir").of(FourDirection.class);
		} else {
			currentDir = FourDirection.SOUTH;
		}
		setAnimation(this.fAnimation.get(currentDir));
	}

	@Override
	public void readFromXML(String fileName) {
		XMLFile f = new XMLFile(fileName);
		if (!f.exists()) {
			throw new FileNotFoundException(f.getFile());
		}
		XMLElement root = f.load().getFirst();
		parse(root.getElement("sprite").get(0));
		f.dispose();
	}

	@Override
	public PCSprite clone() {
		PCSprite r = (PCSprite) super.clone();
		if (this.initialIdx != null) {
			r.initialIdx = this.initialIdx.clone();
		}
		r.fAnimation = this.fAnimation.clone();
		if (this.tgt != null) {
			r.tgt = (Point2D.Float) this.tgt.clone();
		}
		if (this.currentIdx != null) {
			r.currentIdx = this.currentIdx.clone();
		}
		if (this.targetIdx != null) {
			r.targetIdx = this.targetIdx.clone();
		}
		return r;
	}

}
