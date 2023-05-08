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
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field4.MapChipAttribute;
import kinugasa.game.field4.MapChipAttributeStorage;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.graphics.ImageEditor;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.Sprite;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import kinugasa.object.EmptySprite;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:32:23<br>
 * @author Shinacho<br>
 */
public class BattleFieldSystem implements XMLFileSupport {

	private FieldConditionValueSet condition = new FieldConditionValueSet();
	private final HashMap<ConditionKey, TimeCounter> conditionTimes = new HashMap<>();
	private final HashMap<ConditionKey, AnimationSprite> conditionAnime = new HashMap<>();
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
		if (fieldImage.containsKey(attr)) {
			currentChipAttr = attr;
		} else {
			currentChipAttr = defaultChipAttr;
		}
		//��Q���̐ݒ�
		obstacle.clear();
		int max = obstacleMax.containsKey(attr) ? obstacleMax.get(attr) : 0;
		if (max <= 0) {
			return;
		}
		max = Random.randomAbsInt(max);
		String[] name = obstacleName.get(attr);
		obstacle.addAll(BattleFieldObstacleStorage.getInstance().createN(max, name));
		//��Q���̔z�u�A��Q�����m���d�Ȃ�Ȃ��悤�ɂ���
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

	public FieldConditionValueSet getCondition() {
		return condition;
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

	public HashMap<ConditionKey, TimeCounter> getConditionTimes() {
		return conditionTimes;
	}

	public HashMap<ConditionKey, AnimationSprite> getConditionAnime() {
		return conditionAnime;
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

	// ���ׂĂ̏�Ԉُ����菜���܂�
	public void clearCondition() {
		condition.clear();
		conditionTimes.clear();
	}

	//��Ԉُ��ǉ����܂�
	public void addCondition(ConditionKey k) {
		addCondition(k.getName());
	}

	//��Ԉُ��ǉ����܂�
	public void addCondition(String name) {
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		// ���łɔ������Ă�����ʂ̏ꍇ�A�������Ȃ�
		if (condition.contains(name)) {
			assert conditionTimes.containsKey(v.getKey()) : "condition��effectTimes�̓��������Ă��܂���";
			return;
		}
		//�D��x�v�Z
		//�D��x������̏�Ԉُ킪����ꍇ�A�㏟���ō폜
		int pri = v.getKey().getPriority();
		if (!condition.asList().stream().filter(s -> s.getKey().getPriority() == pri).collect(Collectors.toList()).isEmpty()) {
			condition.remove(name);
			conditionTimes.remove(new ConditionKey(name, "", 0));
		}
		List<EffectMaster> effects = v.getEffects();
		//�^�C���Z�o
		List<EffectMaster> continueEffect = effects.stream().filter(a -> a.getContinueType() == EffectContinueType.CONTINUE).collect(Collectors.toList());
		TimeCounter tc = continueEffect.isEmpty() ? TimeCounter.oneCounter() : continueEffect.get(0).createTimeCounter();
		//�������̌��ʂƃG�t�F�N�g���ʎ��Ԃɒǉ�
		condition.add(v);
		conditionTimes.put(v.getKey(), tc);
	}

	// ��Ԉُ�������I�Ɏ�菜���܂�
	public void removeCondition(String name) {
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		condition.remove(v);
		conditionTimes.remove(v.getKey());
	}

	// ��Ԉُ�̌��ʎ��Ԃ��㏑�����܂��B��Ԉُ킪�t�^����Ă��Ȃ��ꍇ�̓Z�b�g���܂��B
	public void setConditionTime(String name, int time) {
		ConditionKey key = ConditionValueStorage.getInstance().get(name).getKey();
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		if (condition.contains(v)) {
			removeCondition(name);
		}
		condition.put(v);
		conditionTimes.put(key, new FrameTimeCounter(time));
	}

	// ��Ԉُ�̌��ʎ��Ԃ�ǉ����܂��B��Ԉُ킪�t�^����Ă��Ȃ��ꍇ�̓Z�b�g���܂��B
	public void addConditionTime(String name, int time) {
		ConditionKey key = ConditionValueStorage.getInstance().get(name).getKey();
		ConditionValue v = ConditionValueStorage.getInstance().get(name);
		if (condition.contains(v)) {
			removeCondition(name);
		}
		time += conditionTimes.get(key).getCurrentTime();
		condition.put(v);
		conditionTimes.put(key, new FrameTimeCounter(time));
	}

	// �R���f�B�V�����ɂ��R���f�B�V����������ݒ肷��
	//P�̔�����s���Ă���̂ŁA����Ⴄ���ʂɂȂ�\��������B
	// ���łɔ������Ă����Ԉُ�͕t�^���Ȃ��B���ʎ��Ԃ̃��Z�b�g�͕ʓr�쐬���邱��
	public void updateCondition() {
		List<ConditionValue> addList = new ArrayList<>();
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.ADD_CONDITION) {
					if (Random.percent(e.getP())) {
						if (!condition.contains(e.getTargetName())) {
							addList.add(ConditionValueStorage.getInstance().get(e.getTargetName()));
							conditionTimes.put(e.getKey(), e.createTimeCounter());
						}
					}
				}
			}
		}
		condition.addAll(addList);
	}

	public void update() {
		for (ConditionValue v : condition) {
			conditionAnime.get(v.getKey()).update();
		}
		List<ConditionKey> deleteList = new ArrayList<>();
		for (ConditionKey key : conditionTimes.keySet()) {
			if (conditionTimes.get(key).isReaching()) {
				deleteList.add(key);
			}
		}
		for (ConditionKey k : deleteList) {
			conditionTimes.remove(k);
			condition.remove(k.getName());
		}

	}

	// �������̌��ʂɊ�Â��āA���̃^�[���s���ł��邩�𔻒肵�܂�
	public boolean canMoveThisTurn() {
		if (condition.isEmpty()) {
			assert conditionTimes.isEmpty() : "condition��effectTimes�̓��������Ă��܂���";
			return true;
		}
		for (ConditionValue v : condition) {
			for (EffectMaster e : v.getEffects()) {
				if (e.getTargetType() == EffectTargetType.STOP) {
					if (Random.percent(e.getP())) {
						return false;
					}
				}
			}
		}
		return true;
	}

	public void draw(GraphicsContext g) {
		g.drawImage(fieldImage.get(currentChipAttr), 0, 0);
		obstacle.forEach(v -> v.draw(g));

		for (ConditionValue v : condition) {
			conditionAnime.get(v.getKey()).draw(g);
		}
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

		//��Q���̃p�[�X
		for (XMLElement e : root.getElement("obstacle")) {
			String name = e.getAttributes().get("name").getValue();
			BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
			float mg = e.getAttributes().get("mg").getFloatValue();
			image = ImageEditor.resize(image, mg);
			int w = (int) (e.getAttributes().get("w").getIntValue() * mg);
			int h = (int) (e.getAttributes().get("h").getIntValue() * mg);
			BattleFieldObstacleStorage.getInstance().add(new BattleFieldObstacle(name, w, h, image));
		}

		//�o�g���t�B�[���h��`�̃p�[�X
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
		//TODO:�t�B�[���h�R���f�B�V�����̃A�j���[�V�����̃p�[�X����

		file.dispose();
	}

}
