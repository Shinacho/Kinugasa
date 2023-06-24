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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2022/11/10_20:35:58<br>
 * @author Shinacho<br>
 */
public class PlayerCharacterSprite extends AnimationSprite {

	private final D2Idx initialIdx;
	private FourDirection currentDir;
	private FourDirAnimation fAnimation;

	public PlayerCharacterSprite(float x, float y, float w, float h, D2Idx initialLocation, FourDirAnimation a, FourDirection initialDir) {
		super(x, y, w, h, a.get(initialDir));
		this.initialIdx = initialLocation;
		this.fAnimation = a;
		this.currentDir = initialDir;
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

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		super.draw(g);
		if (shadow) {
			Graphics2D g2 = g.create();
			g2.setColor(SHADOW);
			g2.fillOval((int) (getX() + getWidth() / 8), (int) (getY() + getHeight() - getHeight() / 16), (int) (getWidth() - getWidth() / 4), (int) (getHeight() / 8));
			g2.dispose();
		}
	}
	private static final Color SHADOW = new Color(0, 0, 0, 128);
	private int stage = 0;
	private int ly, lx;
	private D2Idx currentIdx, targetIdx;

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

	void setTargetIdx(D2Idx targetIdx) {
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

	void updatePartyMemberLocation(FieldMap map, D2Idx tgt) {
		update();
		switch (stage) {
			case 0:
				//初期化
				if (!tgt.equals(currentIdx)) {
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

}
