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

import kinugasa.object.FourDirection;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.LoopCall;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.EnemySetStorageStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.GameSystemException;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.graphics.ARGBColor;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageEditor;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.Drawable;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.KImage;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.ManualTimeCounter;
import kinugasa.util.Random;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_17:18:11<br>
 * @author Dra211<br>
 */
public class FieldMap implements Drawable, Nameable, Disposable {

	private static String enterOperation = "(A)";

	public static String getEnterOperation() {
		return enterOperation;
	}

	public static void setEnterOperation(String enterOperation) {
		FieldMap.enterOperation = enterOperation;
	}

	public FieldMap(String name, XMLFile data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public String getName() {
		return name;
	}

	public static void setDebugMode(boolean debugMode) {
		FieldMap.debugMode = debugMode;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	public static List<PlayerCharacterSprite> getPlayerCharacter() {
		return playerCharacter;
	}

	public static void setPlayerCharacter(List<PlayerCharacterSprite> playerCharacter) {
		FieldMap.playerCharacter = playerCharacter;
	}

	private final String name;
	private final XMLFile data;
	//------------------------------------------------
	private BackgroundLayerSprite backgroundLayerSprite; //nullable
	private List<FieldMapLayerSprite> backlLayeres = new ArrayList<>();
	private static List<PlayerCharacterSprite> playerCharacter = new ArrayList<>();
	private List<FieldMapLayerSprite> frontlLayeres = new ArrayList<>();
	private List<FieldAnimationSprite> frontAnimation = new ArrayList<>();
	private List<BeforeLayerSprite> beforeLayerSprites = new ArrayList<>();
	//------------------------------------------------
	private NPCStorage npcStorage = new NPCStorage();
	private static Map<String, List<String>> removedNPC = new HashMap<>();
	private static Map<String, List<NPC>> addedNPC = new HashMap<>();
	private FieldEventStorage fieldEventStorage;//nullable
	private NodeStorage nodeStorage = new NodeStorage();
	private TextStorage textStorage;//nullable
	//------------------------------------------------
	private FieldMapCamera camera;
	//------------------------------------------------
	private int chipW, chipH;//1�^�C���̃T�C�Y
	private D2Idx currentIdx;// ���L�����N�^�\���f�[�^���W
	//------------------------------------------------
	private static boolean debugMode = false;
	//
	private boolean visible = true;
	//
	private TooltipModel tooltipModel = new SimpleTooltipModel();
	private MapNameModel mapNameModel = new SimpleMapNameModel();

	private LinkedList<D2Idx> prevLocationList = new LinkedList<>();
	private String enemyStorageName;
	//

	public static Map<String, List<NPC>> getAddedNPC() {
		return addedNPC;
	}

	public static Map<String, List<String>> getRemovedNPC() {
		return removedNPC;
	}

	public D2Idx getCurrentIdx() {
		return currentIdx;
	}

	public TooltipModel getTooltipModel() {
		return tooltipModel;
	}

	public void setTooltipModel(TooltipModel tooltipModel) {
		this.tooltipModel = tooltipModel;
	}

	public BackgroundLayerSprite getBackgroundLayerSprite() {
		return backgroundLayerSprite;
	}

	public List<FieldMapLayerSprite> getBacklLayeres() {
		return backlLayeres;
	}

	public FieldMapLayerSprite getBaseLayer() {
		return backlLayeres.get(0);
	}

	public List<FieldMapLayerSprite> getFrontlLayeres() {
		return frontlLayeres;
	}

	public List<FieldAnimationSprite> getFrontAnimation() {
		return frontAnimation;
	}

	public List<BeforeLayerSprite> getBeforeLayerSprites() {
		return beforeLayerSprites;
	}

	public NPCStorage getNpcStorage() {
		return npcStorage;
	}

	public FieldEventStorage getFieldEventStorage() {
		return fieldEventStorage;
	}

	public LinkedList<D2Idx> getPrevLocationList() {
		return prevLocationList;
	}

	public NodeStorage getNodeStorage() {
		return nodeStorage;
	}

	public TextStorage getTextStorage() {
		return textStorage;
	}

	public int getChipW() {
		return chipW;
	}

	public int getChipH() {
		return chipH;
	}

	public FieldMapCamera getCamera() {
		return camera;
	}

	public MapNameModel getMapNameModel() {
		return mapNameModel;
	}

	public void setMapNameModel(MapNameModel mapNameModel) {
		this.mapNameModel = mapNameModel;
	}

	public String getEnemyStorageName() {
		return enemyStorageName;
	}

	public EncountInfo createEncountInfo() {
		EncountInfo ei = new EncountInfo(bgm, EnemySetStorageStorage.getInstance().get(enemyStorageName), getCurrentTile().get0Attr());
		return ei;
	}
	private static FieldMap currentInstance;

	public static FieldMap getCurrentInstance() {
		return currentInstance;
	}

	void setBeforeLayerSprites(List<BeforeLayerSprite> beforeLayerSprites) {
		this.beforeLayerSprites = beforeLayerSprites;
	}
	private float mg;

	float getMg() {
		return mg;
	}

	public FieldMap build() throws FieldMapDataException {
		data.load();
		XMLElement root = data.getFirst();
		int screenW = (int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize());
		int screenH = (int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize());

		//���O�ɕʐݒ肳�ꂽ�e�L�X�g�X�g���[�W�̎擾
		//�e�L�X�g�̂Ȃ��}�b�v�̏ꍇ�A���[�h���Ȃ����Ƃ�������
		if (root.getAttributes().contains("textStorageName")) {
			String textStorageName = root.getAttributes().get("textStorageName").getValue();
			textStorage = TextStorageStorage.getInstance().get(textStorageName).build();
		}
		//�t�B�[���h�C�x���g�X�g���[�W�̍쐬
		if (root.getAttributes().contains("eventStorageName")) {
			String fieldEventStorageName = root.getAttributes().get("eventStorageName").getValue();
			fieldEventStorage = FieldEventStorageStorage.getInstance().contains("fieldEventStorageName")
					? FieldEventStorageStorage.getInstance().get("fieldEventStorageName")
					: new FieldEventStorage(fieldEventStorageName);
		}

		//�G���J�E���g�}�b�v�̖��O
		enemyStorageName = root.getAttributes().contains("esName") ? root.getAttributes().get("esName").getValue() : null;

		//�G���J�E���g�J�E���^�[�̏���
		//��`����Ă��Ȃ��ꍇ�̓G���J�E���g���Ȃ�
		if (root.getAttributes().contains("encountCounterDefault")) {
			this.encountCounter = new ManualTimeCounter(root.getAttributes().get("encountCounterDefault").getIntValue());
			int r = encountCounter.getCurrentTime();
			if (r != 1) {
				r = Random.randomAbsInt(r - r / 2, r + r / 2);
			}
			encountCounter.setCurrentTime(r);
		} else {
			this.encountCounter = ManualTimeCounter.FALSE;
		}

		//FM�`��{�����擾
		mg = 1;
		if (root.hasAttribute("mg")) {
			mg = root.getAttributes().get("mg").getFloatValue();
		}

		// �o�b�N�O���E���h���C���[
		{
			if (root.getElement("background").size() >= 2) {
				throw new FieldMapDataException("background must be 0 or 1 : " + data);
			}
			if (root.hasElement("background")) {
				XMLElement backgroundElement = root.getElement("background").get(0);
				String imageName = backgroundElement.getAttributes().get("image").getValue();

				String[] frame = backgroundElement.getAttributes().get("frame").getValue().split(",");
				FrameTimeCounter tc = new FrameTimeCounter(Arrays.stream(frame).mapToInt(v -> Integer.parseInt(v)).toArray());

				int cutW = backgroundElement.getAttributes().get("w").getIntValue();
				int cutH = backgroundElement.getAttributes().get("h").getIntValue();

				BufferedImage[] images = new SpriteSheet(imageName).split(cutW, cutH).images();
				backgroundLayerSprite = new BackgroundLayerSprite(screenW, screenH, mg);
				backgroundLayerSprite.build(tc, images);
			}

		}

		//�o������m�[�h
		{
			for (XMLElement e : root.getElement("inOutNode")) {
				String name = e.getAttributes().get("name").getValue();
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String tgtMapName = e.getAttributes().get("tgtMap").getValue();
				String exitNodeName = e.getAttributes().get("exitNode").getValue();
				String tooltip = e.getAttributes().get("tooltip").getValue();
				NodeAccepter accepter = NodeAccepterStorage.getInstance().get(e.getAttributes().get("accepterName").getValue());
				FourDirection outDir = FourDirection.valueOf(e.getAttributes().get("outDir").getValue());
				Node node = Node.ofInOutNode(name, tgtMapName, exitNodeName, x, y, tooltip, accepter, outDir);
				if (e.getAttributes().contains("se")) {
					String soundMapName = e.getAttributes().get("se").getValue().split("/")[0];
					String soundName = e.getAttributes().get("se").getValue().split("/")[1];
					node.setSe(SoundStorage.getInstance().get(soundMapName).get(soundName));
				}
				nodeStorage.add(node);
			}
		}
		//�o����p�m�[�h
		{
			for (XMLElement e : root.getElement("outNode")) {
				String name = e.getAttributes().get("name").getValue();
				String outMapName = e.getAttributes().get("outMap").getValue();
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				FourDirection dir = e.getAttributes().get("outDir").of(FourDirection.class);
				Node node = Node.ofOutNode(name, outMapName, x, y, dir);
				nodeStorage.add(node);
			}

		}
		// �o�b�N���C���[
		{
			//	�o�b�N���C���[��1�ȏ�Ȃ��ꍇ�G���[
			if (root.getElement("backLayer").isEmpty()) {
				throw new FieldMapDataException("backLayer is need 1 or more");
			}
			for (XMLElement e : root.getElement("backLayer")) {
				MapChipSet chipset = MapChipSetStorage.getInstance().get(e.getAttributes().get("chipSet").getValue());
				String lineSep = e.getAttributes().get("lineSeparator").getValue();
				String[] lineVal = e.getValue().replaceAll("\n", "").replaceAll("\r\n", "").replaceAll(" ", "").replaceAll("\t", "").split(lineSep);
				MapChip[][] data = new MapChip[lineVal.length][];
				for (int y = 0; y < lineVal.length; y++) {
					String[] val = lineVal[y].split(",");
					data[y] = new MapChip[val.length];
					for (int x = 0; x < val.length; x++) {
						data[y][x] = chipset.get(val[x]);
					}
				}
				int w = (int) (data[0][0].getImage().getWidth());
				int h = (int) (data[0][0].getImage().getWidth());

				FieldMapLayerSprite layerSprite = new FieldMapLayerSprite(chipset, w, h, mg, data);
				backlLayeres.add(layerSprite);
			}

		}
		// �t�����g���C���[
		{
			for (XMLElement e : root.getElement("frontLayer")) {
				MapChipSet chipset = MapChipSetStorage.getInstance().get(e.getAttributes().get("chipSet").getValue());
				String lineSep = e.getAttributes().get("lineSeparator").getValue();
				String[] lineVal = e.getValue().replaceAll("\n", "").replaceAll("\r\n", "").replaceAll(" ", "").replaceAll("\t", "").split(lineSep);
				MapChip[][] data = new MapChip[lineVal.length][];
				for (int y = 0; y < lineVal.length; y++) {
					String[] val = lineVal[y].split(",");
					data[y] = new MapChip[val.length];
					for (int x = 0; x < val.length; x++) {
						data[y][x] = chipset.get(val[x]);
					}
				}
				int w = (int) (data[0][0].getImage().getWidth());
				int h = (int) (data[0][0].getImage().getWidth());

				FieldMapLayerSprite layerSprite = new FieldMapLayerSprite(chipset, w, h, mg, data);
				frontlLayeres.add(layerSprite);
			}

		}

		//�r�t�H�A���C���[
		{
			for (XMLElement e : root.getElement("before")) {
				String name = e.getAttributes().get("name").getValue();
				if (BeforeLayerSpriteStorage.getInstance().contains(name)) {
					beforeLayerSprites.add(BeforeLayerSpriteStorage.getInstance().get(name));
				} else {

					String imageName = e.getAttributes().get("image").getValue();
					float angle = e.getAttributes().contains("angle") ? e.getAttributes().get("angle").getFloatValue() : 0;
					float speed = e.getAttributes().contains("speed") ? e.getAttributes().get("speed").getFloatValue() : 0;
					float tp = e.getAttributes().contains("tp") ? e.getAttributes().get("tp").getFloatValue() : 1;
					float bmg = e.getAttributes().contains("mg") ? e.getAttributes().get("mg").getFloatValue() : 1;

					BeforeLayerSprite bls = new BeforeLayerSprite(name, ImageUtil.load(imageName), tp, bmg, new KVector(angle, speed));
					beforeLayerSprites.add(bls);
				}
			}

		}
		// �`�b�v�T�C�Y�̎擾
		chipW = (int) (getBaseLayer().getChip(0, 0).getImage().getWidth() * mg);
		chipH = (int) (getBaseLayer().getChip(0, 0).getImage().getHeight() * mg);

		//�A�j���[�V����
		{
			for (XMLElement e : root.getElement("animation")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String image = e.getAttributes().get("image").getValue();
				String[] frame = e.getAttributes().get("frame").getValue().split(",");
				FrameTimeCounter tc = new FrameTimeCounter(Arrays.stream(frame).mapToInt(v -> Integer.parseInt(v)).toArray());
				int w = e.getAttributes().get("w").getIntValue();
				int h = e.getAttributes().get("h").getIntValue();
				BufferedImage[] images = new SpriteSheet(image).split(w, h).images();
				int locationX = (int) (x * (chipW));
				int locationY = (int) (y * (chipH));
				frontAnimation.add(new FieldAnimationSprite(new D2Idx(x, y), locationX, locationY, w, h, new Animation(tc, images)));
			}

		}
		//�C�x���g
		{
			for (XMLElement e : root.getElement("event")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String name = e.getAttributes().get("name").getValue();
				String script = e.getAttributes().get("script").getValue();
				fieldEventStorage.addAll(new FieldEventParser(name, new D2Idx(x, y), new XMLFile(script)).parse());
			}
		}

		data.dispose();

		// �J����������
		camera = new FieldMapCamera(this);

		//NPC
		{
			for (XMLElement e : root.getElement("npc")) {
				String name = e.getAttributes().get("name").getValue();
				//�폜���ꂽNPC�̏ꍇ�͒ǉ����Ȃ�
				if (removedNPC.containsKey(this.getName())) {
					if (removedNPC.get(getName()).contains(name)) {
						continue;
					}
				}
				String initialIdxStr = e.getAttributes().get("initialIdx").getValue();
				D2Idx idx = new D2Idx(Integer.parseInt(initialIdxStr.split(",")[0]), Integer.parseInt(initialIdxStr.split(",")[1]));
				// �̈�̃`�F�b�N
				if (!getBaseLayer().include(idx)) {
					throw new FieldMapDataException("npc : " + name + " is out of bounds");
				}
				NPCMoveModel moveModel = NPCMoveModelStorage.getInstance().get(e.getAttributes().get("NPCMoveModel").getValue());

				Vehicle v = e.hasAttribute("vehicle") ? VehicleStorage.getInstance().get(e.getAttributes().get("vehicle").getValue()) : null;
				String textID = e.hasAttribute("textID") ? e.getAttributes().get("textID").getValue() : null;

				int frame = e.hasAttribute("frame") ? e.getAttributes().get("frame").getIntValue() : Integer.MAX_VALUE;

				BufferedImage image = ImageUtil.load(e.getAttributes().get("image").getValue());
				int sx, sw, sh;
				int ex, ew, eh;
				int wx, ww, wh;
				int nx, nw, nh;
				String[] cs = e.getAttributes().get("s").getValue().split(",");
				String[] ce = e.getAttributes().get("e").getValue().split(",");
				String[] cw = e.getAttributes().get("w").getValue().split(",");
				String[] cn = e.getAttributes().get("n").getValue().split(",");
				sx = Integer.parseInt(cs[0]);
				sw = Integer.parseInt(cs[1]);
				sh = Integer.parseInt(cs[2]);
				ex = Integer.parseInt(ce[0]);
				ew = Integer.parseInt(ce[1]);
				eh = Integer.parseInt(ce[2]);
				wx = Integer.parseInt(cw[0]);
				ww = Integer.parseInt(cw[1]);
				wh = Integer.parseInt(cw[2]);
				nx = Integer.parseInt(cn[0]);
				nw = Integer.parseInt(cn[1]);
				nh = Integer.parseInt(cn[2]);
				Animation south = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(sx, sw, sh).images());
				Animation west = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(wx, ww, wh).images());
				Animation east = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(ex, ew, eh).images());
				Animation north = new Animation(new FrameTimeCounter(frame), new SpriteSheet(image).rows(nx, nw, nh).images());
				FourDirAnimation anime = new FourDirAnimation(south, west, east, north);
				FourDirection initialDir = FourDirection.valueOf(e.getAttributes().get("initialDir").getValue());
				int w = sw;
				int h = sh;
				float x = getBaseLayer().getX() + idx.x * chipW;
				float y = getBaseLayer().getY() + idx.y * chipH;
				npcStorage.add(new NPC(name, currentIdx, moveModel, v, this, textID, x, y, w, h, idx, anime, initialDir));
			}
			//�ǉ�NPC��ݒ�
			if (addedNPC.containsKey(getName())) {
				npcStorage.addAll(addedNPC.get(getName()));
			}
		}

		//BGM�̏���
		{
			if (root.getElement("bgm").size() >= 2) {
				throw new FieldMapDataException("bgm must be 0 or 1 : " + data);
			}
			if (root.hasElement("bgm")) {
				XMLElement e = root.getElement("bgm").get(0);
				BGMMode mode = BGMMode.valueOf(e.getAttributes().get("mode").getValue());
				if (mode != BGMMode.NOTHING) {
					String mapName = e.getAttributes().get("mapName").getValue();
					String soundName = e.getAttributes().get("soundName").getValue();
					if (mode == BGMMode.STOP || mode == BGMMode.STOP || mode == BGMMode.PAUSE) {
						SoundStorage.getInstance().get(mapName).stopAll();
					}
					if (mode == BGMMode.STOP_AND_PLAY) {
						bgm = SoundStorage.getInstance().get(mapName).get(soundName);
						if (!bgm.isPlaying()) {
							bgm.load().stopAndPlay();
						}
					}
				}

			}
		}
		mapNameModel.reset();

		return currentInstance = this;
	}
	private Sound bgm;

	public Sound getBgm() {
		return bgm;
	}

	private static final Comparator<PlayerCharacterSprite> Y_COMPARATOR = new Comparator<>() {
		@Override
		public int compare(PlayerCharacterSprite o1, PlayerCharacterSprite o2) {
			return (int) o1.getY() - (int) o2.getY();
		}

	};

	@Override
	public void draw(GraphicsContext g) {
		if (!visible) {
			return;
		}
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.draw(g);
		}
		backlLayeres.forEach(e -> e.draw(g));

		if (playerCharacter != null) {
			List<PlayerCharacterSprite> list = npcStorage.asList().stream().map(c -> c).collect(Collectors.toList());
			list.addAll(playerCharacter);
			Collections.sort(list, Y_COMPARATOR);
			list.forEach(v -> v.draw(g));
		} else {
			npcStorage.forEach(e -> e.draw(g));
		}

		frontlLayeres.forEach(e -> e.draw(g));
		if (debugMode) {
			DebugLayerSprite.debugDrawPC(g, playerCharacter, currentIdx, getBaseLayer().getLocation(), chipW, chipH);
			DebugLayerSprite.debugDrawNPC(g, npcStorage.asList(), getBaseLayer().getLocation(), chipW, chipH);
			DebugLayerSprite.debugDrawCamera(g, getBaseLayer().getLocation(), chipW, chipH);
		}
		frontAnimation.forEach(e -> e.draw(g));
		beforeLayerSprites.forEach(e -> e.draw(g));
		if (mapNameModel != null) {
			mapNameModel.drawMapName(this, g);
		}
		if (tooltipModel != null) {
			tooltipModel.drawTooltip(this, g);
		}
//		if (debugMode) {
//			float centerX = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 2;
//			float centerY = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2;
//			Point2D.Float p1 = new Point2D.Float(centerX - chipW, centerY - chipH);
//			Point2D.Float p2 = new Point2D.Float(centerX + chipW, centerY + chipH);
//			Point2D.Float p3 = new Point2D.Float(centerX - chipW, centerY + chipH);
//			Point2D.Float p4 = new Point2D.Float(centerX + chipW, centerY - chipH);
//			Graphics2D g2 = g.create();
//			g2.setColor(Color.RED);
//			g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
//			g2.drawLine((int) p3.x, (int) p3.y, (int) p4.x, (int) p4.y);
//			g2.dispose();
////			System.out.print("IDX : " + currentIdx);
////			System.out.println("  FM_LOCATION : " + getBaseLayer().getLocation());
//		}
	}

	@Override
	public void dispose() {
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.dispose();
			backgroundLayerSprite = null;
		}
		backlLayeres.forEach(e -> e.dispose());
		backlLayeres.clear();
		frontlLayeres.forEach(e -> e.dispose());
		frontlLayeres.clear();
		frontAnimation.clear();
		beforeLayerSprites.clear();
		npcStorage.clear();
		if (fieldEventStorage != null) {
			fieldEventStorage.dispose();
			fieldEventStorage = null;
		}
		nodeStorage.clear();
		if (textStorage != null) {
			textStorage.clear();
		}
	}

	public void update() {
		if (prevLocationList.isEmpty()) {
			for (int i = 0; i < playerCharacter.size() - 1; i++) {
				prevLocationList.add(currentIdx);
			}
			if (playerCharacter.size() > 1) {
				playerCharacter.subList(1, playerCharacter.size()).forEach(v -> v.setCurrentIdx(currentIdx));
				playerCharacter.subList(1, playerCharacter.size()).forEach(v -> v.setTargetIdx(currentIdx));
			}
		}
		npcStorage.forEach(c -> c.update());
		if (mw != null) {
			mw.update();
		}
		if (playerCharacter.size() > 1) {
			for (int i = 1, j = 0; i < playerCharacter.size(); i++) {
				playerCharacter.get(i).updatePartyMemberLocation(this, prevLocationList.get(j));
				if (j < prevLocationList.size() - 1) {
					j++;
				}
			}
		}
	}

	private ManualTimeCounter encountCounter;

	public ManualTimeCounter getEncountCounter() {
		return encountCounter;
	}

	public void setEncountCounter(ManualTimeCounter encountCounter) {
		this.encountCounter = encountCounter;
	}

	@LoopCall
	public void move() {
		if (getBaseLayer().getVector().getSpeed() == 0) {
			return;
		}
		D2Idx prevIdx = currentIdx.clone();
		camera.move();
		if (!prevIdx.equals(currentIdx)) {
			//�V�����`�b�v�ɏ�����ꍇ�A�G���J�E���g�J�E���^�[�̏���
			int x = 0;
			for (MapChip c : getCurrentTile().getChip()) {
				x += c.getAttr().getEncountBaseValue();
			}
			encountCounter.sub(x);
			if (debugMode) {
				System.out.print("FM MOVE " + currentIdx + " / EC=" + encountCounter.getCurrentTime());
				System.out.println(" / " + getCurrentTile());
			}
			prevLocationList.addFirst(prevIdx);
			if (playerCharacter.size() <= prevLocationList.size()) {
				prevLocationList.removeLast();
			}
			for (int i = 1, j = 0; i < playerCharacter.size(); i++) {
				playerCharacter.get(i).updatePartyMemberLocation(this, prevLocationList.get(j));
				if (j < prevLocationList.size() - 1) {
					j++;
				}
			}
		}
	}

	public boolean isEncount() {
		if (encountCounter.equals(ManualTimeCounter.FALSE)) {
			return false;
		}
		boolean r = encountCounter.isReaching();
		if (debugMode && r) {
			System.out.println("ENCOUNT!");
		}
		if (r) {
			resetEncountCounter();
		}
		if (enemyStorageName == null) {
			throw new GameSystemException("encount, but this maps enemy set name is null");
		}
		return r;
	}

	public void resetEncountCounter() {
		int r = encountCounter.getInitialTime();
		if (r != 1) {
			r = Random.randomAbsInt(r - r / 2, r + r / 2);
		}
		encountCounter.setCurrentTime(r);
	}

	public void setVector(KVector vector) {
		camera.setVector(vector.reverse());
	}

	public void setSpeed(float speed) {
		camera.setSpeed(speed);
	}

	public void setAngle(float angle) {
		camera.setAngle(angle);
	}

	public void setLocation(Point2D.Float location) {
		camera.setLocation(location);
	}

	public void setLocation(float x, float y) {
		camera.setLocation(x, y);
	}

	public void setX(float x) {
		camera.setX(x);
	}

	public void setY(float y) {
		camera.setY(y);
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public FieldMapTile getTile(D2Idx idx) throws ArrayIndexOutOfBoundsException {
		List<MapChip> chip = new ArrayList<>();
		for (FieldMapLayerSprite s : backlLayeres) {
			chip.add(s.getChip(idx.x, idx.y));
		}
		for (FieldMapLayerSprite s : frontlLayeres) {
			chip.add(s.getChip(idx.x, idx.y));
		}
		NPC npc = npcStorage.get(idx);
		List<FieldEvent> event = fieldEventStorage == null ? null : fieldEventStorage.get(idx);
		Node node = nodeStorage.get(idx);

		if (playerCharacter.isEmpty()) {
			return new FieldMapTile(chip, npc, null, event, node);
		}

		return new FieldMapTile(chip, npc, idx.equals(currentIdx) ? playerCharacter.get(0) : null, event, node);
	}

	public FieldMapTile getCurrentTile() {
		return getTile(currentIdx);
	}

	/**
	 * �v���C���[�̍��W���X�V���܂��Bx,y�����S�i�v���C���[���P�[�V�����j�ɂȂ�悤�Ƀ}�b�v�̕\�����W��ݒ肵�܂��B
	 * ���̃��\�b�h�ł́A�ړ��\���ǂ����̔���͍s���܂���B�ړ������getTile����s���Ă��������B
	 *
	 * @param idx �}�b�v�f�[�^�̃C���f�b�N�X�B
	 */
	public void setCurrentIdx(D2Idx idx) {
		if (!idx.equals(currentIdx)) {
			if (fieldEventStorage != null) {
				//�C�x���g�̎��s
				List<FieldEvent> e = fieldEventStorage.get(idx);
				Collections.sort(e);
				//���������C�x���g�̐ݒ�
				FieldEventSystem.getInstance().setEvent(new LinkedList<>(e));
			}
		}
		this.currentIdx = idx.clone();

	}

	/**
	 * �t�B�[���h�}�b�v�̃L�����N�^�ƃr�t�H�A���C���[�ȊO��`�悵���摜�𐶐����܂��B
	 * �o�b�N�O���E���h�y�уt�����g�A�j���[�V�����́A���݂̏�Ԃ��g�p����܂��B NPC�y�уL�����N�^�͕\������܂���B
	 *
	 * @param w ���s�N�Z��
	 * @param h �����s�N�Z��
	 * @param pcLocation PC�̈ʒu�ɓ_��ł��B
	 * @return �w��̊g�嗦�ŕ`�悳�ꂽ�摜�B
	 */
	public KImage createMiniMap(int w, int h, boolean pcLocation) {

		int imageW = (int) (getBaseLayer().getChip(0, 0).getImage().getWidth() * mg);
		int imageH = (int) (getBaseLayer().getChip(0, 0).getImage().getHeight() * mg);
		BufferedImage[][] baseImage = ImageUtil.splitAsArray(getBaseLayer().getImage(), imageW, imageH);

		BufferedImage image = ImageUtil.newImage(getBaseLayer().getDataWidth(), getBaseLayer().getDataHeight());
		int[] pix = new int[getBaseLayer().getDataWidth() * getBaseLayer().getDataHeight()];

		if (getBackgroundLayerSprite() != null) {
			int col = ARGBColor.toARGB(ImageUtil.averageColor(getBackgroundLayerSprite().getImage().get()));;
			for (int i = 0; i < pix.length; i++) {
				pix[i] = col;
			}
		}

		assert baseImage.length * baseImage[0].length == pix.length : "createMiniMap PIX LENGTH MISSMATCH : " + baseImage.length * baseImage[0].length + " / " + pix.length;

		for (int i = 0, y = 0; y < baseImage.length; y++) {
			for (int x = 0; x < baseImage[y].length; x++) {
				//�x�[�X�C���[�Wyx�ɓ����^�C��������ꍇ�͂��̃^�C�����X�L�b�v����
				if(ImageUtil.hasClaerPixcel(baseImage[y][x])){
					i++;
					continue;
				}
				int col = ARGBColor.toARGB(ImageUtil.averageColor(baseImage[y][x]));
				if (ARGBColor.getAlpha(col) == 0) {
					i++;
					continue;
				}
				if (pcLocation && currentIdx.x == x && currentIdx.y == y) {
					pix[i] = ARGBColor.RED;
					i++;
					continue;
				}
				pix[i] = col;
				i++;
			}
		}
		ImageUtil.setPixel(image, pix);
		baseImage = null;

		//�X�P�[���v�Z
		float ws = w / image.getWidth();
		float hs = h / image.getHeight();

		return new KImage(ImageEditor.resize(image, ws, hs));

	}

	public boolean canTalk() {
		D2Idx idx = playerDirIdx();
		// �͈͊O�̃`�F�b�N
		if (idx.x < 0 || idx.y < 0) {
			return false;
		}
		if (idx.x >= getBaseLayer().getDataWidth() || idx.y >= getBaseLayer().getDataHeight()) {
			return false;
		}

		//NPC�����邩�ǂ�����Ԃ�
		return getTile(idx).getNpc() != null;
	}

	//�v���C���[�̌����Ă��������D2IDX��Ԃ��܂��B
	public D2Idx playerDirIdx() {
		D2Idx idx = this.currentIdx.clone();
		FourDirection currentDir = playerCharacter.get(0).getCurrentDir();

		switch (currentDir) {
			case EAST:
				idx.x += 1;
				break;
			case WEST:
				idx.x -= 1;
				break;
			case NORTH:
				idx.y -= 1;
				break;
			case SOUTH:
				idx.y += 1;
				break;
		}
		return idx;
	}

	private static String noNPCMessage;

	static {
		noNPCMessage = I18N.translate("NO_NPC");
	}

	public static String getNoNPCMessage() {
		return noNPCMessage;
	}

	public static void setNoNPCMessage(String noNPCMessage) {
		FieldMap.noNPCMessage = noNPCMessage;
	}
	private MessageWindow mw;

	/**
	 * �v���C���[�L�����N�^�̌����Ă�������ɂ���NPC�ɑ΂��āA���������̈ʒu�Ƀ��b�Z-�[�E�C���h�E��\�����܂��B
	 * ���b�Z�[�W�E�C���h�E�̃��f����SimpleMessageWindowModel���g�p����܂��B
	 *
	 * @return
	 * ���b�Z�[�W�E�C���h�E�BNPC�����Ȃ��ꍇ��noNPCMessage��\�����܂��B�������A����͗�O���u�I�Ȃ��̂ŁA���̐���́A�Q�[���}�l�[�W�����ōs���ׂ��ł��B
	 */
	public MessageWindow talk() {
		D2Idx idx = playerDirIdx();
		NPC n = getTile(idx).getNpc();
		if (n != null) {
			n.notMove();
		}
		//NPC����ʉ������ɂ���ꍇ�̓��b�Z�[�W�E�C���h�E����ɕ\���B�㔼���ɂ���ꍇ�͉��ɕ\���B
		float buffer = 24;
		float x = buffer;
		float y = n == null || n.getCenterY() < GameOption.getInstance().getWindowSize().width / 2 ? GameOption.getInstance().getWindowSize().height / 2 + buffer * 2 : buffer;
		float w = GameOption.getInstance().getWindowSize().width - (buffer * 2);
		float h = GameOption.getInstance().getWindowSize().height / 3;
		Text t = n == null ? new Text(noNPCMessage) : textStorage.get(n.getTextID());
		if (n != null) {
			n.to(playerCharacter.get(0).getCurrentDir().reverse());
		}
		t.reset();

		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), textStorage, t);

		return mw;
	}

	/**
	 * ���b�Z�[�W�E�C���h�E����܂��B���ł�NPC�̈ړ����������܂��B
	 */
	public void closeMessagWindow() {
		if (mw == null) {
			return;
		}
		mw.setVisible(false);
		if (mw != null) {
			mw = null;
		}
		npcStorage.forEach(c -> c.canMove());
	}

	MessageWindow getMessageWindow() {
		return mw;
	}

	void setMW(MessageWindow mw) {
		this.mw = mw;
	}

	/**
	 * NPC�̈ړ����ꊇ��~���܂��B
	 */
	public void NPCMoveStop() {
		npcStorage.forEach(c -> c.notMove());
	}

	/**
	 * NPC�̈ړ���~���ꊇ�������܂��B
	 */
	public void NPCMoveStart() {
		npcStorage.forEach(c -> c.canMove());
	}

	public FieldMap changeMap() {
		return changeMap(getCurrentTile().getNode());
	}

	public FieldMap changeMap(Node n) {
		for (Node o : getNodeStorage()) {
			if (o.getSe() != null) {
				o.getSe().dispose();
			}
		}
		dispose();
		if (n.getSe() != null) {
			n.getSe().load().stopAndPlay();
		}
		FieldMap fm = FieldMapStorage.getInstance().get(n.getExitFieldMapName()).build();
		if (n.getExitNodeName() == null) {
			fm.setCurrentIdx(n.getIdx());
		} else {
			fm.setCurrentIdx(fm.getNodeStorage().get(n.getExitNodeName()).getIdx());
		}
		fm.getCamera().updateToCenter();
		fm.prevLocationList.clear();
		if (n.getExitNodeName() != null
				&& fm.getNodeStorage() != null
				&& fm.getNodeStorage().get(n.getExitNodeName()) != null
				&& fm.getNodeStorage().get(n.getExitNodeName()).getOutDir() != null) {
			FieldMap.getPlayerCharacter().get(0).to(n.getOutDir());
		}
		if (FieldMap.getPlayerCharacter().size() > 1) {
			FieldMap.getPlayerCharacter().subList(1, getPlayerCharacter().size()).forEach(v -> v.setCurrentIdx(getCurrentIdx()));
			FieldMap.getPlayerCharacter().subList(1, getPlayerCharacter().size()).forEach(v -> v.setLocation(playerCharacter.get(0).getLocation()));
		}
		if (GameSystem.isDebugMode()) {
			System.out.println("CHANGE_MAP: " + n);
		}
		return fm;
	}

	@Override
	public String toString() {
		return "FieldMap{" + "name=" + name + ", chipW=" + chipW + ", chipH=" + chipH + '}';
	}

}
