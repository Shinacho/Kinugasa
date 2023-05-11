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
package kinugasa.game.system;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.Vehicle;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.ProgressBarSprite;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.AnimationSprite;
import kinugasa.object.Drawable;
import kinugasa.object.FourDirection;
import kinugasa.object.ImageSprite;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_20:49:18<br>
 * @author Shinacho<br>
 */
public class Enemy implements Nameable, Drawable, BattleCharacter {

	private String id;
	private Status status;
	private ArrayList<DropItem> dropItem;
	private ImageSprite sprite;
	private ProgressBarSprite progressBarSprite;
	private static String progressBarKey = BattleConfig.StatusKey.hp;
	private List<BattleCharacter> currentTgt;
	private Vehicle vehicle;
	private EnemyAI ai;

	public static String getProgressBarKey() {
		return progressBarKey;
	}

	public static void setProgressBarKey(String progressBarKey) {
		Enemy.progressBarKey = progressBarKey;

	}

	public EnemyAI getAI() {
		return ai;
	}

	public void setAI(EnemyAI ai) {
		this.ai = ai;
	}

	@Override
	public String getId() {
		return id;
	}

	Enemy(String id, Status status, ArrayList<DropItem> dropItem, ImageSprite sprite, Vehicle v, EnemyAI ai) {
		this.id = id;
		this.status = status;
		this.dropItem = dropItem;
		this.sprite = sprite;
		this.vehicle = v;
		if (status.getActions().isEmpty()) {
			throw new GameSystemException(status.getName() + " s action is empty");
		}
		this.ai = ai;
	}

	private Point2D.Float tgt;
	private boolean moving = false;
	private Point2D.Float prev;

	@Override
	public void setTargetLocation(Point2D.Float p, int area) {
		prev = getSprite().getCenter();
		tgt = (Point2D.Float) p.clone();
		distance = 0;
		this.area = area;
		dirTo(p);
		moving = true;
	}

	@Override
	public void unsetTarget() {
		prev = getSprite().getCenter();
		tgt = null;
		distance = 0;
		moving = false;
	}

	@Override
	public boolean isMoving() {
		return moving;
	}
	private int distance;
	private int area;
	boolean move = false;
	boolean leftOrRight;
	private int reverseTime = 16;
	private boolean reverse = false;

	public void setArea(int area) {
		this.area = area;
	}

	public int getArea() {
		return area;
	}

	@Override
	public void move() {
		moveToTgt();
	}

	public boolean targetInArea() {
		if (tgt == null) {
			return false;
		}
		return getSprite().getCenter().distance(tgt) < area;
	}

	@Override
	public void to(FourDirection dir) {
		throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
	}

	@Override
	public void moveToTgt() {
		if (!moving) {
			if (GameSystem.isDebugMode()) {
				System.out.println("enemy " + getName() + " move is canceld : " + moving + " / " + tgt);
			}
			return;
		}
		Point2D.Float f = sprite.simulateMove();
		Rectangle2D.Float r = new Rectangle2D.Float(f.x, f.y, getSprite().getWidth(), getSprite().getHeight());
		if (!GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleArea().contains(r)) {
			getSprite().setVector(getSprite().getVector().reverse());
			reverse = true;
		}
		for (Sprite s : GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getObstacle()) {
			if (s.hit(r)) {
				leftOrRight = (s.getCenter().y > GameOption.getInstance().getWindowSize().height / 2);
				if (leftOrRight) {
					getSprite().getVector().addAngle(-95);
					move = true;
				} else {
					getSprite().getVector().addAngle(+95);
					move = true;
				}
			}
		}
		sprite.move();
		distance++;
		if (reverse && distance > reverseTime) {
			dirTo(tgt);
			reverse = false;
		}
		if (move) {
			KVector v = getSprite().getVector().clone();
			if (!leftOrRight) {
				v.addAngle(-95);
			} else {
				v.addAngle(95);
			}
			f = getSprite().simulateMove(v);
			r = new Rectangle2D.Float(f.x, f.y, getSprite().getWidth(), getSprite().getHeight());
			boolean hit = false;
			for (Sprite s : GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getObstacle()) {
				hit |= s.hit(r);
			}
			if (!hit) {
				if (!leftOrRight) {
					getSprite().getVector().addAngle(-95);
					move = false;
				} else {
					getSprite().getVector().addAngle(95);
					move = false;
				}
			}
		}
		//ターゲットとの距離がエリア内になったら移動を終了
		if (targetInArea()) {
			sprite.setSpeed(0);
			moving = false;
			distance = 0;
			return;
		}
		if (distance > getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue()) {
			sprite.setSpeed(0);
			moving = false;
			distance = 0;
			return;
		}
		if (prev.distance(getSprite().getCenter()) >= getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue()) {
			sprite.setSpeed(0);
			moving = false;
			distance = 0;
			return;
		}
		if (sprite.getCenter().distance(tgt) < VehicleStorage.getInstance().getCurrentVehicle().getSpeed()) {
			sprite.setLocationByCenter(tgt);
			sprite.setSpeed(0);
			moving = false;
			distance = 0;
		}
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	@Override
	public String getName() {
		return status.getName();
	}

	public void update() {
		if (progressBarKey != null) {
			if (progressBarSprite == null) {
				int val = (int) getStatus().getEffectedStatus().get(progressBarKey).getValue();
				int max = (int) getStatus().getEffectedStatus().get(progressBarKey).getMax();

				progressBarSprite = new ProgressBarSprite(0, 0, sprite.getWidth(), 4, val, val, max);
			}
			progressBarSprite.setLocation(sprite.getLocation());
			progressBarSprite.setVal((int) getStatus().getEffectedStatus().get(progressBarKey).getValue());
		}
	}

	public void dirTo(BattleCharacter c) {
		KVector v = new KVector();
		v.setAngle(sprite.getCenter(), c.getSprite().getCenter());
		v.setSpeed(vehicle.getSpeed());
		sprite.setVector(v);
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

	private static final Color SHADOW = new Color(0, 0, 0, 128);

	@Override
	public void draw(GraphicsContext g) {
		if (!sprite.isVisible() || !sprite.isExist()) {
			return;
		}
		sprite.draw(g);
		if (progressBarKey != null && progressBarSprite != null) {
			progressBarSprite.draw(g);
		}
		Graphics2D g2 = g.create();
		g2.setColor(Color.RED);
		g2.setFont(FontModel.DEFAULT.clone().setFontStyle(Font.PLAIN).setFontSize(12).getFont());
		g2.drawString(status.getName(), sprite.getX() - getName().length() * 3, sprite.getY() - 4);

		g2.setColor(SHADOW);
		g2.fillOval(
				(int) (getSprite().getX() + getSprite().getWidth() / 8),
				(int) (getSprite().getY() + getSprite().getHeight() - getSprite().getHeight() / 16),
				(int) (getSprite().getWidth() - getSprite().getWidth() / 4),
				(int) (getSprite().getHeight() / 8));
		if (GameSystem.isDebugMode()) {
			g2.setColor(Color.ORANGE);
			GraphicsUtil.drawRect(g2, getSprite().getBounds());
		}
		g2.dispose();
	}

	public void dirTo(Point2D.Float tgt) {
		KVector v = new KVector();
		v.setAngle(sprite.getCenter(), tgt);
		v.setSpeed(vehicle.getSpeed());
		sprite.setVector(v);
	}

	public ArrayList<DropItem> getDropItem() {
		return dropItem;
	}

	@Override
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public ImageSprite getSprite() {
		return sprite;
	}

	public void setCurrentTgt(List<BattleCharacter> currentTgt) {
		this.currentTgt = currentTgt;
	}

	public List<BattleCharacter> getCurrentTgt() {
		return currentTgt;
	}

	public void setSprite(AnimationSprite sprite) {
		this.sprite = sprite;
	}

	@Override
	public String toString() {
		return "Enemy{" + "status=" + status + '}';
	}

	@Override
	public boolean isPlayer() {
		return false;
	}

}
