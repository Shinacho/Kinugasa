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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FourDirection;
import kinugasa.game.system.*;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2022/11/10_20:35:58<br>
 * @author Dra211<br>
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
	}

	@Override
	public void draw(GraphicsContext g) {
		super.draw(g);
		Graphics2D g2 = g.create();
		g2.setColor(SHADOW);
		g2.fillOval((int) (getX() + getWidth() / 8), (int) (getY() + getHeight() - getHeight() / 16), (int) (getWidth() - getWidth() / 4), (int) (getHeight() / 8));
		g2.dispose();
	}
	private static final Color SHADOW = new Color(0, 0, 0, 128);
	private int stage = 0;
	private int lx, ly;
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

	public void setTargetIdx(D2Idx targetIdx) {
		this.targetIdx = targetIdx;
	}

	void updatePartyMemberLocation(FieldMap map, D2Idx tgt) {
		super.update();
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
				Point2D.Float tgtL = new Point2D.Float(nx,ny);
				
				KVector v = new KVector();
				v.setAngle(getLocation(), tgtL);
				setAngle(v.angle);
				to(getVector().round());
				move();
				if(tgtL.distance(getLocation()) < getSpeed() || tgtL.equals(getLocation())){
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
