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
package kinugasa.game.system;

import java.awt.geom.Point2D;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_11:12:33<br>
 * @author Dra211<br>
 */
public class PlayerCharacter implements BattleCharacter {

	private Status status;
	private PlayerCharacterSprite sprite;
	private int order = 0;

	public PlayerCharacter(Status status, PlayerCharacterSprite sprite) {
		this.status = status;
		this.sprite = sprite;
	}

	void setOrder(int order) {
		this.order = order;
	}

	int getOrder() {
		return order;
	}

	@Override
	public String getId() {
		return status.getName();
	}

	@Override
	public void to(FourDirection dir) {
		sprite.to(dir);
	}

	@Override
	public PlayerCharacterSprite getSprite() {
		return sprite;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	public void dirTo(BattleCharacter c, float speed) {
		KVector v = new KVector();
		v.setAngle(sprite.getCenter(), c.getSprite().getCenter());
		v.setSpeed(speed);
		sprite.setVector(v);
	}

	public void dirTo(BattleCharacter c, String vehicleName) {
		KVector v = new KVector();
		v.setAngle(sprite.getCenter(), c.getSprite().getCenter());
		v.setSpeed(VehicleStorage.getInstance().get(vehicleName).getSpeed());
		sprite.setVector(v);
	}

	public void dirTo(Point2D.Float tgt, String vehicleName) {
		KVector v = new KVector();
		v.setAngle(sprite.getCenter(), tgt);
		v.setSpeed(VehicleStorage.getInstance().get(vehicleName).getSpeed());
		sprite.setVector(v);
	}

	public void dirTo(Point2D.Float tgt, float speed) {
		KVector v = new KVector();
		v.setAngle(sprite.getCenter(), tgt);
		v.setSpeed(speed);
		sprite.setVector(v);
	}

	private Point2D.Float tgt;
	private boolean moving = false;

	@Override
	public void setTargetLocation(Point2D.Float p, int area) {
		tgt = (Point2D.Float) p.clone();
		dirTo(p, VehicleStorage.getInstance().getCurrentVehicle().getSpeed());
		moving = true;
	}

	@Override
	public void unsetTarget() {
		tgt = null;
		moving = false;
		sprite.setVector(new KVector(0, 0));
	}

	@Override
	public boolean isMoving() {
		return moving;
	}

	@Override
	public void move() {
		sprite.move();
	}

	@Override
	public void moveToTgt() {
		if (!moving) {
			return;
		}
		sprite.move();
		if (sprite.getCenter().distance(tgt) < VehicleStorage.getInstance().getCurrentVehicle().getSpeed()) {
			sprite.setLocationByCenter(tgt);
			sprite.setSpeed(0);
			moving = false;
			tgt = null;
		}
	}

	@Override
	public String toString() {
		return "PlayerCharacter{" + "status=" + status + '}';
	}

	@Override
	public boolean isPlayer() {
		return true;
	}

}
