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
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.MapChipAttribute;
import kinugasa.game.field4.MapChipAttributeStorage;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.Sprite;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.Random;
import kinugasa.object.EmptySprite;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:32:23<br>
 * @author Shinacho<br>
 */
public class BattleFieldSystem implements XMLFileSupport {

	private final HashMap<MapChipAttribute, BufferedImage> fieldImage = new HashMap<>();
	private final HashMap<MapChipAttribute, Integer> obstacleMax = new HashMap<>();
	private final HashMap<MapChipAttribute, String[]> obstacleName = new HashMap<>();
	private MapChipAttribute currentChipAttr;
	private MapChipAttribute defaultChipAttr;
	private Rectangle battleArea;
	private Rectangle enemytArea;
	private Rectangle partyArea;
	private Rectangle battleAreaAndNoPartyArea;
	private EmptySprite battleFieldAllArea;

	{
		int screenW = (int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize());
		int screenH = (int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize());
		int statusPaneH = (int) BattleStatusWindows.h;
		int minX = 4;
		int screenCenterX = (int) (GameOption.getInstance().getWindowSize().getWidth() / 2 / GameOption.getInstance().getDrawSize());
		int partyAreaX = (int) (GameOption.getInstance().getWindowSize().getWidth() * 0.75 / GameOption.getInstance().getDrawSize());
		int areaH = (int) (screenH * 0.60);
		battleArea = new Rectangle(minX, statusPaneH + 4, screenW - minX * 2, areaH);
		enemytArea = new Rectangle(minX, statusPaneH + 4, screenCenterX, areaH);
		partyArea = new Rectangle(partyAreaX, statusPaneH + 4, screenW - partyAreaX - 4, areaH);
		battleAreaAndNoPartyArea = new Rectangle(minX, statusPaneH + 4, partyAreaX - 4, areaH);
		battleFieldAllArea = new EmptySprite(minX, statusPaneH + 4, screenW - 8, areaH);
	}
	private List<BattleFieldObstacle> obstacle = new ArrayList<>();

	private static final BattleFieldSystem INSTANCE = new BattleFieldSystem();

	public static BattleFieldSystem getInstance() {
		return INSTANCE;
	}

	public EmptySprite getBattleFieldAllArea() {
		return battleFieldAllArea;
	}

	private BattleFieldSystem() {
	}

	public void init(MapChipAttribute attr) {
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("BF : init, attr=" + attr + ", obMax:" + obstacleMax.get(attr));
		}
		if (fieldImage.containsKey(attr)) {
			currentChipAttr = attr;
		} else {
			currentChipAttr = defaultChipAttr;
		}
		//障害物の設定
		obstacle.clear();
		int max = obstacleMax.containsKey(attr) ? obstacleMax.get(attr) : 0;
		if (max <= 0) {
			return;
		}
		max = Random.randomAbsInt(max);
		String[] name = obstacleName.get(attr);
		obstacle.addAll(BattleFieldObstacleStorage.getInstance().createN(max, name));
		//障害物の配置、障害物同士が重ならないようにする
		List<Sprite> checkList = new ArrayList<>();
		for (Sprite s : obstacle) {
			float w = s.getWidth();
			float h = s.getHeight();
			L2:
			do {
				s.setLocation(Random.randomLocation(battleAreaAndNoPartyArea, w, h));
				boolean hit = false;
				for (Sprite os : checkList) {
					hit |= s.hit(os);
				}
				if (!hit) {
					break L2;
				}
			} while (true);
			checkList.add(s);
		}
		Collections.sort(obstacle, (BattleFieldObstacle o1, BattleFieldObstacle o2) -> (int) (o1.getY() - o2.getY()));
	}

	public List<BattleFieldObstacle> getObstacle() {
		return obstacle;
	}

	public boolean hitObstacle(Sprite s) {
		for (Sprite o : obstacle) {
			if (o.hit(s)) {
				return true;
			}
		}
		return false;
	}

	public boolean hitObstacle(Rectangle2D.Float r) {
		for (Sprite o : obstacle) {
			if (o.hit(r)) {
				return true;
			}
		}
		return false;
	}

	public boolean hitObstacle(Point2D.Float p) {
		for (Sprite o : obstacle) {
			if (o.contains(p)) {
				return true;
			}
		}
		return false;
	}

	public boolean inArea(Rectangle2D.Float r) {
		if (battleArea.intersects(r)) {
			return true;
		}
		if (partyArea.intersects(r)) {
			return true;
		}
		return false;
	}

	public boolean inArea(Point2D.Float p) {
		if (battleArea.contains(p)) {
			return true;
		}
		if (partyArea.contains(p)) {
			return true;
		}
		return false;
	}

	public HashMap<MapChipAttribute, BufferedImage> getFieldImage() {
		return fieldImage;
	}

	public void setEnemytArea(Rectangle enemytArea) {
		this.enemytArea = enemytArea;
	}

	public Rectangle getEnemytArea() {
		return enemytArea;
	}

	public void setPartyArea(Rectangle partyArea) {
		this.partyArea = partyArea;
	}

	public Rectangle getPartyArea() {
		return partyArea;
	}

	public Rectangle getBattleArea() {
		return battleArea;
	}

	public HashMap<MapChipAttribute, String[]> getObstacleName() {
		return obstacleName;
	}

	public HashMap<MapChipAttribute, Integer> getObstacleMax() {
		return obstacleMax;
	}

	public MapChipAttribute getDefaultChipAttr() {
		return defaultChipAttr;
	}

	public MapChipAttribute getCurrentChipAttr() {
		return currentChipAttr;
	}

	public Rectangle getBattleAreaAndNoPartyArea() {
		return battleAreaAndNoPartyArea;
	}

	public void draw(GraphicsContext g) {
		g.drawImage(fieldImage.get(currentChipAttr), 0, 0);
		obstacle.forEach(v -> v.draw(g));

		if (GameSystem.isDebugMode()) {
			Graphics2D g2 = g.create();
			g2.setColor(Color.ORANGE);
			GraphicsUtil.drawRect(g2, enemytArea);
			g2.setColor(Color.BLUE);
			GraphicsUtil.drawRect(g2, partyArea);
			g2.setColor(Color.LIGHT_GRAY);
			GraphicsUtil.drawRect(g2, battleArea);
			g2.setColor(Color.GRAY);
			GraphicsUtil.drawRect(g, battleAreaAndNoPartyArea);
			g2.dispose();
		}
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}
		XMLElement root = file.load().getFirst();

		//障害物のパース
		for (XMLElement e : root.getElement("obstacle")) {
			String name = e.getAttributes().get("name").getValue();
			BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
			float mg = e.getAttributes().get("mg").getFloatValue();
			image = ImageUtil.resize(image, mg);
			int w = (int) (e.getAttributes().get("w").getIntValue() * mg);
			int h = (int) (e.getAttributes().get("h").getIntValue() * mg);
			BattleFieldObstacleStorage.getInstance().add(new BattleFieldObstacle(name, w, h, image));
		}

		//バトルフィールド定義のパース
		for (XMLElement e : root.getElement("bf")) {
			String chipName = e.getAttributes().get("chipAttrName").getValue();
			var c = MapChipAttributeStorage.getInstance().get(chipName);
			BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
			boolean d = e.getAttributes().contains("default");
			int obMax = e.getAttributes().get("obstacleMax").getIntValue();
			String[] obName = e.getAttributes().get("obstacleName").safeSplit(",");
			getInstance().fieldImage.put(c, image);
			if (d) {
				defaultChipAttr = c;
			}
			getInstance().obstacleMax.put(c, obMax);
			getInstance().obstacleName.put(c, obName);
		}
		//TODO:フィールドコンディションのアニメーションのパースここ

		file.dispose();
	}

}
