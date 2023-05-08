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

import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FourDirAnimation;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_11:12:33<br>
 * @author Shinacho<br>
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

	public static PlayerCharacter readFromXML(String fileName) {
		XMLFile f = new XMLFile(fileName);
		if (!f.exists()) {
			throw new FileNotFoundException(f.getFile());
		}

		XMLElement root = f.load().getFirst();

		String name = root.getAttributes().get("name").getValue();
		Race race = RaceStorage.getInstance().get(root.getAttributes().get("race").getValue());

		XMLElement e = root.getElement("spritesheet").get(0);
		BufferedImage base = ImageUtil.load(e.getAttributes().get("image").getValue());
		int tc = e.getAttributes().get("tc").getIntValue();

		int w = e.getAttributes().get("w").getIntValue();
		int h = e.getAttributes().get("h").getIntValue();

		int ny = e.getAttributes().get("ny").getIntValue();
		int sy = e.getAttributes().get("sy").getIntValue();
		int ey = e.getAttributes().get("ey").getIntValue();
		int wy = e.getAttributes().get("wy").getIntValue();

		Animation na = new Animation(new FrameTimeCounter(tc), new SpriteSheet(base).rows(ny, w, h).images());
		Animation sa = new Animation(new FrameTimeCounter(tc), new SpriteSheet(base).rows(sy, w, h).images());
		Animation ea = new Animation(new FrameTimeCounter(tc), new SpriteSheet(base).rows(ey, w, h).images());
		Animation wa = new Animation(new FrameTimeCounter(tc), new SpriteSheet(base).rows(wy, w, h).images());
		FourDirAnimation ani = new FourDirAnimation(sa, wa, ea, na);
		Point2D.Float pcl = FieldMap.getPlayerCharacter().get(0).getLocation();
		PlayerCharacterSprite sprite = new PlayerCharacterSprite(pcl.x, pcl.y, w, h, FieldMap.getCurrentInstance().getCurrentIdx(), ani, FourDirection.EAST);

		int order = FieldMap.getPlayerCharacter().size();

		Status status = new Status(name, race);
		for (XMLElement ee : root.getElement("status")) {
			String key = ee.getAttributes().get("key").getValue();
			float max = ee.getAttributes().get("max").getFloatValue();
			float min = ee.getAttributes().get("min").getFloatValue();
			float value = ee.getAttributes().get("value").getFloatValue();
			status.getBaseStatus().get(key).setMax(max);
			status.getBaseStatus().get(key).setMin(min);
			status.getBaseStatus().get(key).setValue(value);
		}
		for (XMLElement ee : root.getElement("attrIn")) {
			String key = ee.getAttributes().get("key").getValue();
			float value = ee.getAttributes().get("value").getFloatValue();
			status.getBaseAttrIn().get(key).set(value);
		}
		for (XMLElement ee : root.getElement("eqip")) {
			Item i = ItemStorage.getInstance().get(ee.getAttributes().get("name").getValue());
			if (!status.getItemBag().contains(i)) {
				status.getItemBag().add(i);
			}
			status.addEqip(i);
		}
		for (XMLElement ee : root.getElement("item")) {
			Item i = ItemStorage.getInstance().get(ee.getAttributes().get("name").getValue());
			status.getItemBag().add(i);
		}
		for (XMLElement ee : root.getElement("book")) {
			Book b = BookStorage.getInstance().get(ee.getAttributes().get("name").getValue());
			status.getBookBag().add(b);
		}

		f.dispose();

		PlayerCharacter pc = new PlayerCharacter(status, sprite);
		pc.setOrder(order);
		return pc;
	}
}
