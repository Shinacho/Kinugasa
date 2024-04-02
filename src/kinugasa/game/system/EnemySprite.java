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
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.ProgressBarSprite;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.EmptySprite;
import kinugasa.object.KVector;
import kinugasa.resource.text.XMLElement;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2023/10/28_13:36:59<br>
 * @author Shinacho<br>
 */
public class EnemySprite extends PCSprite {

	private Enemy me;
	private Point2D.Float tgt;
	private boolean moving = false;
	boolean move = false;
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
	boolean right = Random.randomBool();
	boolean changeDir = true;
	float angle = 1f;
	private Point2D.Float moveStartLocation = null;
	private int lp = 0;

	public void moveToTgt() {
		if (!moving) {
			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print("enemy " + getName() + " move is canceld : " + moving + " / " + tgt);
			}
			return;
		}
		if (moveStartLocation == null) {
			moveStartLocation = getCenter();
		}
		if (getVector().getSpeed() == 0f) {
			setVector(new KVector(getCenter(), tgt));
			getVector().setSpeed(VehicleStorage.getInstance().getCurrentVehicle().getSpeed());
		}
		if (me.getStatus().getBaseStatus().get(StatusKey.残行動力).isZero()) {
			setSpeed(0);
			moving = false;
			angle = 1f;
			moveStartLocation = null;
			changeDir = true;
			return;
		}
		//現在の角度でターゲット位置まで移動できるか検査
		boolean currentVectorIsNP = true;
		boolean ikisugi = false;
		EmptySprite sp = new EmptySprite(getLocation(), getSize());
		sp.setVector(getVector().clone());
		Point2D.Float prevCenter = null;
		lp=0;
		while (true) {
			prevCenter = (Point2D.Float) sp.getCenter().clone();
			sp.move();
			Point2D.Float p = sp.getCenter();

			if (!GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleFieldAllArea().contains(p)) {
				currentVectorIsNP = false;
				ikisugi = false;
				break;
			}
			if (GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().hitObstacle(p)) {
				currentVectorIsNP = false;
				ikisugi = false;
				break;
			}
			if (tgt.distance(p) <= VehicleStorage.getInstance().getCurrentVehicle().getSpeed()) {
				currentVectorIsNP = true;
				ikisugi = false;
				break;
			}

			//イキスギる場合かどうか・・・prevの方がtgtに近い場合
			if (prevCenter.distance(tgt) < p.distance(tgt)) {
				currentVectorIsNP = false;
				ikisugi = true;
				break;
			}
			lp++;
		}
		//直接移動モード
		if (currentVectorIsNP) {
			move();
			if (tgt.distance(getCenter()) < VehicleStorage.getInstance().getCurrentVehicle().getSpeed()) {
				setLocationByCenter(tgt);
				setSpeed(0);
				moving = false;
				angle = 1f;
				moveStartLocation = null;
				changeDir = true;
				return;
			}
			if (moveStartLocation.distance(getCenter()) > me.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue()) {
				setSpeed(0);
				moving = false;
				angle = 1f;
				moveStartLocation = null;
				changeDir = true;
				return;
			}
			moveStartLocation = null;
			return;
		}
		//イキスギモード
		if (ikisugi) {
			if (lp == 0) {
				//ターゲット再設定
				setTargetLocation(tgt2, (int) VehicleStorage.getInstance().getCurrentVehicle().getSpeed());
			} else {
				//prevの位置まで移動する
				setTargetLocation(prevCenter, (int) VehicleStorage.getInstance().getCurrentVehicle().getSpeed());
			}
			moveToTgt();
			return;
		}

		//移動角度変更モード
		KVector v = sp.getVector();
		if (right) {
			v.addAngle(angle);
		} else {
			v.addAngle(-angle);
		}
		changeDir = !changeDir;
		if (changeDir) {
			angle += 1f;
		}
		setVector(v);
		moveToTgt();

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

	private Point2D.Float tgt2;

	@Override
	public void setTargetLocation(Point2D.Float p, int area) {
		tgt = (Point2D.Float) p.clone();
		tgt2 = (Point2D.Float) p.clone();
		dirTo(p);
		moving = true;
	}

	@Override
	public void unsetTarget() {
		tgt = null;
		moving = false;
	}

	@Override
	public boolean isMoving() {
		return moving;
	}

	@Override
	public String toString() {
		return "EnemySprite{" + "me=" + me + '}';
	}

}
