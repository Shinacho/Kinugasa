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
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.ProgressBarSprite;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.object.Sprite;
import kinugasa.resource.text.XMLElement;

/**
 *
 * @vesion 1.0.0 - 2023/10/28_13:36:59<br>
 * @author Shinacho<br>
 */
public class EnemySprite extends PCSprite {

	private Enemy me;
	private Point2D.Float tgt;
	private boolean moving = false;
	private Point2D.Float prev;
	private int distance;
	private int area;
	boolean move = false;
	boolean leftOrRight;
	private int reverseTime = 16;
	private boolean reverse = false;
	private ProgressBarSprite progressBarSprite1;
	private ProgressBarSprite progressBarSprite2;

	public ProgressBarSprite getProgressBarSprite2() {
		return progressBarSprite2;
	}

	public ProgressBarSprite getProgressBarSprite1() {
		return progressBarSprite1;
	}

	public EnemySprite(XMLElement e) {
		super(e);
		setImage(getAnimation().getCurrentBImage());
	}

	public void setMe(Enemy me) {
		this.me = me;

		{
			int val = (int) me.getStatus().getEffectedStatus().get(StatusKey.体力).getValue();
			int max = (int) me.getStatus().getEffectedStatus().get(StatusKey.体力).getMax();
			progressBarSprite1 = new ProgressBarSprite(0, 0, me.getSprite().getWidth(), 4, val, val, max);
			progressBarSprite1.setLocation(me.getSprite().getLocation());
			progressBarSprite1.setY(progressBarSprite1.getY() - 4);
			progressBarSprite1.setVal((int) me.getStatus().getEffectedStatus().get(StatusKey.体力).getValue());
		}
		{
			int val = (int) me.getStatus().getEffectedStatus().get(StatusKey.正気度).getValue();
			int max = (int) me.getStatus().getEffectedStatus().get(StatusKey.正気度).getMax();
			progressBarSprite2 = new ProgressBarSprite(0, 0, me.getSprite().getWidth(), 4, val, val, max);
			progressBarSprite2.setLocation(me.getSprite().getLocation());
			progressBarSprite2.setY(progressBarSprite2.getY());
			progressBarSprite2.setVal((int) me.getStatus().getEffectedStatus().get(StatusKey.正気度).getValue());
		}
	}

	@Override
	public void readFromXML(String fileName) {
		super.readFromXML(fileName);

	}

	public void moveToTgt() {
		if (!moving) {
			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print("enemy " + getName() + " move is canceld : " + moving + " / " + tgt);
			}
			return;
		}
		Point2D.Float f = simulateMove();
		Rectangle2D.Float r = new Rectangle2D.Float(f.x, f.y, getWidth(), getHeight());
		if (!GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleArea().contains(r)) {
			setVector(getVector().reverse());
			reverse = true;
		}
		for (Sprite s : GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getObstacle()) {
			if (s.hit(r)) {
				leftOrRight = (s.getCenter().y > GameOption.getInstance().getWindowSize().height / 2);
				if (leftOrRight) {
					getVector().addAngle(-95);
					move = true;
				} else {
					getVector().addAngle(+95);
					move = true;
				}
			}
		}
		move();
		distance++;
		if (reverse && distance > reverseTime) {
			dirTo(tgt);
			reverse = false;
		}
		if (move) {
			KVector v = getVector().clone();
			if (!leftOrRight) {
				v.addAngle(-95);
			} else {
				v.addAngle(95);
			}
			f = simulateMove(v);
			r = new Rectangle2D.Float(f.x, f.y, getWidth(), getHeight());
			boolean hit = false;
			for (Sprite s : GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getObstacle()) {
				hit |= s.hit(r);
			}
			if (!hit) {
				if (!leftOrRight) {
					getVector().addAngle(-95);
					move = false;
				} else {
					getVector().addAngle(95);
					move = false;
				}
			}
		}
		//ターゲットとの距離がエリア内になったら移動を終了
		if (targetInArea()) {
			setSpeed(0);
			moving = false;
			distance = 0;
			return;
		}
		if (distance > me.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue()) {
			setSpeed(0);
			moving = false;
			distance = 0;
			return;
		}
		if (prev.distance(getCenter()) >= me.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue()) {
			setSpeed(0);
			moving = false;
			distance = 0;
			return;
		}
		if (getCenter().distance(tgt) < VehicleStorage.getInstance().getCurrentVehicle().getSpeed()) {
			setLocationByCenter(tgt);
			setSpeed(0);
			moving = false;
			distance = 0;
		}
	}

	@Override
	public void update() {
		if (getAnimation() == null) {
			return;
		}
		getAnimation().update();
		if (!getAnimation().isRepeat() && getAnimation().isEnded()) {
			setVisible(false);
		}
		setImage(getAnimation().getCurrentImage());
		if (progressBarSprite1.isVisible()) {
			progressBarSprite1.setLocation(getLocation());
			progressBarSprite1.setY(progressBarSprite1.getY() - 4);
			progressBarSprite1.setVal((int) me.getStatus().getEffectedStatus().get(StatusKey.体力).getValue());
		}
		if (progressBarSprite2.isVisible()) {
			progressBarSprite2.setLocation(getLocation());
			progressBarSprite2.setY(progressBarSprite2.getY());
			progressBarSprite2.setVal((int) me.getStatus().getEffectedStatus().get(StatusKey.正気度).getValue());
		}
	}

	public void dirTo(Actor c) {
		KVector v = new KVector();
		v.setAngle(getCenter(), c.getSprite().getCenter());
		v.setSpeed(BattleConfig.BATTLE_WALK_SPEED);
		setVector(v);
	}

	@Override
	public void dirTo(Actor c, float speed) {
		KVector v = new KVector();
		v.setAngle(getCenter(), c.getSprite().getCenter());
		v.setSpeed(speed);
		setVector(v);
	}

	@Override
	public void dirTo(Actor c, String vehicleName) {
		KVector v = new KVector();
		v.setAngle(getCenter(), c.getSprite().getCenter());
		v.setSpeed(VehicleStorage.getInstance().get(vehicleName).getSpeed());
		setVector(v);
	}

	private static final Color SHADOW = new Color(0, 0, 0, 128);
	private boolean nameVisible = true;

	public void setNameVisible(boolean nameVisible) {
		this.nameVisible = nameVisible;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		super.draw(g);
		progressBarSprite1.draw(g);
		progressBarSprite2.draw(g);
		Graphics2D g2 = g.create();
		if (nameVisible) {
			g2.setColor(Color.RED);
			g2.setFont(FontModel.DEFAULT.clone().setFontStyle(Font.PLAIN).setFontSize(12).getFont());
			g2.drawString(me.getVisibleName(), getX() - me.getStatus().getVisibleName().length() * 3, getY() - 4);
		}
		/*
		g2.setColor(SHADOW);
		g2.fillOval(
				(int) (getX() + getWidth() / 8),
				(int) (getY() + getHeight() - getHeight() / 16),
				(int) (getWidth() - getWidth() / 4),
				(int) (getHeight() / 8));
		 */
		if (GameSystem.isDebugMode()) {
			g2.setColor(Color.ORANGE);
			GraphicsUtil.drawRect(g2, getBounds());
		}
		g2.dispose();
	}

	public void dirTo(Point2D.Float tgt) {
		KVector v = new KVector();
		v.setAngle(getCenter(), tgt);
		v.setSpeed(BattleConfig.BATTLE_WALK_SPEED);
		setVector(v);
	}

	@Override
	public void setTargetLocation(Point2D.Float p, int area) {
		prev = getCenter();
		tgt = (Point2D.Float) p.clone();
		distance = 0;
		this.area = area;
		dirTo(p);
		moving = true;
	}

	@Override
	public void unsetTarget() {
		prev = getCenter();
		tgt = null;
		distance = 0;
		moving = false;
	}

	@Override
	public boolean isMoving() {
		return moving;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public int getArea() {
		return area;
	}

	public boolean targetInArea() {
		if (tgt == null) {
			return false;
		}
		return getCenter().distance(tgt) < area;
	}

	@Override
	public String toString() {
		return "EnemySprite{" + "me=" + me + '}';
	}

}
