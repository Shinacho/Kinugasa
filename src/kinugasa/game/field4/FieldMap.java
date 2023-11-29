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
import kinugasa.game.system.NPCSprite;
import kinugasa.game.system.PCSprite;
import kinugasa.object.FourDirection;
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
import kinugasa.game.LoopCall;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.EnemySetStorageStorage;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.GameSystemException;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.graphics.ARGBColor;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.Drawable;
import kinugasa.object.ImageSprite;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.KImage;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.resource.sound.SoundType;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.ManualTimeCounter;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_17:18:11<br>
 * @author Shinacho<br>
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

	public static List<PCSprite> getPlayerCharacter() {
		return playerCharacter;
	}

	public static void setPlayerCharacter(List<PCSprite> playerCharacter) {
		FieldMap.playerCharacter = playerCharacter;
	}

	private final String name;
	private final XMLFile data;
	//------------------------------------------------
	private BackgroundLayerSprite backgroundLayerSprite; //nullable
	private List<FieldMapLayerSprite> backlLayeres = new ArrayList<>();
	private static List<PCSprite> playerCharacter = new ArrayList<>();
	private List<FieldMapLayerSprite> frontlLayeres = new ArrayList<>();
	private List<FieldAnimationSprite> animationLayer = new ArrayList<>();
	private List<BeforeLayerSprite> beforeLayerSprites = new ArrayList<>();
	//------------------------------------------------
	private NPCStorage npcStorage = new NPCStorage();
	private static Map<String, List<String>> removedNPC = new HashMap<>();
	private static Map<String, List<NPCSprite>> addedNPC = new HashMap<>();
	private FieldEventStorage fieldEventStorage;//nullable
	private NodeStorage nodeStorage = new NodeStorage();
	private TextStorage textStorage;//nullable
	private List<MiniMapLabel> miniMapLabels;
	//------------------------------------------------
	private FieldMapCamera camera;
	//------------------------------------------------
	private int chipW, chipH;//1タイルのサイズ
	private D2Idx currentIdx;// 自キャラクタ表示データ座標
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

	public static Map<String, List<NPCSprite>> getAddedNPC() {
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
		return animationLayer;
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
		return new EncountInfo(bgm,
				EnemySetStorageStorage.getInstance().get(enemyStorageName),
				getCurrentTile().get0Attr());
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

		//事前に別設定されたテキストストレージの取得
		//テキストのないマップの場合、ロードしないことを許可する
		if (root.getAttributes().contains("textStorageName")) {
			String textStorageName = root.getAttributes().get("textStorageName").getValue();
			textStorage = TextStorageStorage.getInstance().get(textStorageName).build();
		}
		//フィールドイベントストレージの作成
		if (root.getAttributes().contains("eventStorageName")) {
			String fieldEventStorageName = root.getAttributes().get("eventStorageName").getValue();
			fieldEventStorage = FieldEventStorageStorage.getInstance().contains("fieldEventStorageName")
					? FieldEventStorageStorage.getInstance().get("fieldEventStorageName")
					: new FieldEventStorage(fieldEventStorageName);
		}

		//エンカウントマップの名前
		enemyStorageName = root.getAttributes().contains("ess")
				? root.getAttributes().get("ess").getValue() : null;

		//エンカウントカウンターの処理
		//定義されていない場合はエンカウントしない
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

		//FM描画倍率を取得
		mg = 1;
		if (root.hasAttribute("mg")) {
			mg = root.getAttributes().get("mg").getFloatValue();
		}
		//miniMapLabels
		{
			miniMapLabels = new ArrayList<>();
			for (XMLElement e : root.getElement("miniMapLabel")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String val = e.getAttributes().get("value").getValue();
				miniMapLabels.add(new MiniMapLabel(new D2Idx(x, y), val));
			}
		}

		// バックグラウンドレイヤー
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

		//出入り口ノード
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
					String soundName = e.getAttributes().get("se").getValue();
					node.setSe(SoundStorage.getInstance().get(soundName));
				}
				nodeStorage.add(node);
			}
		}
		//出口専用ノード
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
		// バックレイヤー
		{
			//	バックレイヤーが1つ以上ない場合エラー
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
		// フロントレイヤー
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

		//ビフォアレイヤー
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
		// チップサイズの取得
		chipW = (int) (getBaseLayer().getChip(0, 0).getImage().getWidth() * mg);
		chipH = (int) (getBaseLayer().getChip(0, 0).getImage().getHeight() * mg);

		//アニメーション
		{
			for (XMLElement e : root.getElement("animation")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String image = e.getAttributes().get("image").getValue();
				String[] frame = e.getAttributes().get("frame").getValue().split(",");
				FrameTimeCounter tc = new FrameTimeCounter(Arrays.stream(frame).mapToInt(v -> Integer.parseInt(v)).toArray());
				int w = (int) (e.getAttributes().get("w").getIntValue());
				int h = (int) (e.getAttributes().get("h").getIntValue());
				BufferedImage[] images = ImageUtil.resizeAll(new SpriteSheet(image).split(w, h).images(), mg);
				int locationX = (int) (x * (chipW));
				int locationY = (int) (y * (chipH));
				animationLayer.add(new FieldAnimationSprite(new D2Idx(x, y), locationX, locationY, (int)(w * mg), (int)(h * mg),
						new Animation(tc, images)));
			}

		}
		//イベント
		{
			for (XMLElement e : root.getElement("event")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String name = e.getAttributes().get("name").getValue();
				String script = e.getAttributes().get("script").getValue();
				fieldEventStorage.addAll(new FieldEventParser(name, new D2Idx(x, y), new XMLFile(script)).parse());
			}
		}

		// カメラ初期化
		camera = new FieldMapCamera(this);

		//NPC
		{
			for (XMLElement e : root.getElement("npc")) {
				String name = e.getAttributes().get("name").getValue();
				//削除されたNPCの場合は追加しない
				if (removedNPC.containsKey(this.getName())) {
					if (removedNPC.get(getName()).contains(name)) {
						continue;
					}
				}
				String initialIdxStr = e.getAttributes().get("initialIdx").getValue();
				D2Idx idx = new D2Idx(Integer.parseInt(initialIdxStr.split(",")[0]), Integer.parseInt(initialIdxStr.split(",")[1]));
				// 領域のチェック
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
				NPCSprite npc = new NPCSprite(name, currentIdx, moveModel, v, this, textID, x, y, w, h, idx, anime, initialDir);
				//タッチイベント
				if (e.hasAttribute("touchEvent")) {
					npc.setTouchEvent(e.getAttributes().get("touchEvent").getValue());
				}
				npcStorage.add(npc);
			}
			//追加NPCを設定
			if (addedNPC.containsKey(getName())) {
				npcStorage.addAll(addedNPC.get(getName()));
			}
		}

		//BGMの処理
		{
			if (root.getElement("bgm").size() >= 2) {
				throw new FieldMapDataException("bgm must be 0 or 1 : " + data);
			}
			if (root.hasElement("bgm")) {
				XMLElement e = root.getElement("bgm").get(0);
				BGMMode mode = BGMMode.valueOf(e.getAttributes().get("mode").getValue());
				String soundName = e.getAttributes().get("soundName").getValue();
				bgm = SoundStorage.getInstance().get(soundName);
				switch (mode) {
					case NOTHING:
						break;
					case PAUSE:
						bgm.pause();
						break;
					case STOP:
						SoundStorage.getInstance().stream().filter(p -> p.getType() == SoundType.BGM).forEach(p -> p.stop());
						SoundStorage.getInstance().stream().filter(p -> p.getType() == SoundType.BGM).forEach(p -> p.dispose());
						break;
					case STOP_AND_PLAY:
						SoundStorage.getInstance().stream().filter(p -> p.getType() == SoundType.BGM).forEach(p -> p.stop());
						SoundStorage.getInstance().stream().filter(p -> p.getType() == SoundType.BGM).forEach(p -> p.dispose());
						bgm.load().stopAndPlay();
						break;
					default:
						throw new AssertionError("FM undefined bgm mode");
				}

			}
		}
		mapNameModel.reset();

		currentInstance = this;

		//自動適用スクリプト
		if (root.getAttributes().contains("loadScript")) {
			String fileName = root.getAttributes().get("loadScript").getValue();
			FieldEventSystem.getInstance().setEvent(new LinkedList<>(FieldEventParser.parse(getName(), fileName)));
			while (true) {
				if (FieldEventSystem.getInstance().exec() == UserOperationRequire.END) {
					break;
				}
			}
		}
		data.dispose();

		return this;
	}
	private Sound bgm;

	public Sound getBgm() {
		return bgm;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!visible) {
			return;
		}
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.draw(g);
		}
		backlLayeres.forEach(e -> e.draw(g));

		animationLayer.forEach(e -> e.draw(g));
		if (playerCharacter != null) {
			List<PCSprite> list = new ArrayList<>(playerCharacter);
			Collections.reverse(list);
			list.forEach(v -> v.draw(g));
		}
		npcStorage.forEach(e -> e.draw(g));

		frontlLayeres.forEach(e -> e.draw(g));
		if (debugMode) {
			DebugLayerSprite.debugDrawPC(g, playerCharacter, currentIdx, getBaseLayer().getLocation(), chipW, chipH);
			DebugLayerSprite.debugDrawNPC(g, npcStorage.asList(), getBaseLayer().getLocation(), chipW, chipH);
			DebugLayerSprite.debugDrawCamera(g, getBaseLayer().getLocation(), chipW, chipH);
		}
		beforeLayerSprites.forEach(e -> e.draw(g));
		if (mapNameModel != null) {
			mapNameModel.drawMapName(this, g);
		}
		if (tooltipModel != null) {
			tooltipModel.drawTooltip(this, g);
		}

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
		animationLayer.clear();
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
		if (GameSystem.isDebugMode()) {
			System.err.println("FieldMap[" + getName() + "] is disposed");
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
		//スタックチェック
		if (GameSystem.isDebugMode()) {
			if (!getTile(currentIdx).canStep()) {
				throw new GameSystemException("FM : PC IS STUCK!!!!!!!!:" + getTile(currentIdx));
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
			//新しいチップに乗った場合、状態異常の更新
			GameSystem.getInstance().getParty().stream().forEach(p -> p.getStatus().updateCondition());
			//エンカウントカウンターの処理
			int x = 0;
			for (MapChip c : getCurrentTile().getChip()) {
				x += c.getAttr().getEncountBaseValue();
			}
			encountCounter.sub(x);
			if (debugMode) {
				kinugasa.game.GameLog.print("FM MOVE " + currentIdx + " /EC=" + encountCounter.getCurrentTime() + " /TILE=" + getCurrentTile());
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
			kinugasa.game.GameLog.print("ENCOUNT!");
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
		NPCSprite npc = npcStorage.get(idx);
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
	 * プレイヤーの座標を更新します。x,yが中心（プレイヤーロケーション）になるようにマップの表示座標を設定します。
	 * このメソッドでは、移動可能かどうかの判定は行いません。移動判定はgetTileから行ってください。
	 *
	 * @param idx マップデータのインデックス。
	 */
	public void setCurrentIdx(D2Idx idx) {
		if (!idx.equals(currentIdx)) {
			if (fieldEventStorage != null) {
				//イベントの実行
				List<FieldEvent> e = fieldEventStorage.get(idx);
				Collections.sort(e);
				//自動発動イベントの設定
				FieldEventSystem.getInstance().setEvent(new LinkedList<>(e));
			}
		}
		this.currentIdx = idx.clone();

	}

	/**
	 * フィールドマップのキャラクタとビフォアレイヤー以外を描画した画像を生成します。
	 * バックグラウンド及びフロントアニメーションは、現在の状態が使用されます。 NPC及びキャラクタは表示されません。
	 *
	 * @param w 幅ピクセル
	 * @param h 高さピクセル
	 * @param pcLocation PCの位置に点を打つか。
	 * @param label ミニマップラベルの描画。
	 * @return 指定の拡大率で描画された画像。
	 */
	public KImage createMiniMap(int w, int h, boolean pcLocation, boolean label) {
		//ベースレイヤーが全部同じタイルの場合は背景と判断して1を取る
		FieldMapLayerSprite tgt
				= getBaseLayer().allIs(getBaseLayer().getChip(0, 0)) && backlLayeres.size() >= 2
				? this.backlLayeres.get(1) : getBaseLayer();

		int imageW = (int) (tgt.getChip(0, 0).getImage().getWidth() * mg);
		int imageH = (int) (tgt.getChip(0, 0).getImage().getHeight() * mg);
		BufferedImage[][] baseImage = ImageUtil.splitAsArray(tgt.getImage(), imageW, imageH);

		BufferedImage image = ImageUtil.newImage(tgt.getDataWidth(), tgt.getDataHeight());
		int[] pix = new int[tgt.getDataWidth() * tgt.getDataHeight()];

		if (getBackgroundLayerSprite() != null) {
			int col = ARGBColor.toARGB(ImageUtil.averageColor(getBackgroundLayerSprite().getImage().get()));;
			for (int i = 0; i < pix.length; i++) {
				pix[i] = col;
			}
		}

		assert baseImage.length * baseImage[0].length == pix.length : "createMiniMap PIX LENGTH MISSMATCH : " + baseImage.length * baseImage[0].length + " / " + pix.length;

		for (int i = 0, y = 0; y < baseImage.length; y++) {
			for (int x = 0; x < baseImage[y].length; x++) {
				//ベースイメージyxに透明タイルがある場合はこのタイルをスキップする
				if (ImageUtil.hasClaerPixcel(baseImage[y][x])) {
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

		//スケール計算
		float ws = w / image.getWidth();
		float hs = h / image.getHeight();
		KImage result = new KImage(ImageUtil.resize(image, ws, hs));

		//ミニマップラベルの処理
		if (label) {
			Graphics2D g2 = ImageUtil.createGraphics2D(result.get(), RenderingQuality.SPEED);
			for (MiniMapLabel l : miniMapLabels) {
				TextLabelSprite s = l.getSprite();
				g2.setFont(s.getLabelModel().getFontConfig().getFont());
				g2.setColor(Color.BLACK);
				float x = l.getX() * ws;
				float y = l.getY() * hs + 8;
				s.setLocationByCenter(new Point2D.Float(x, y));
				g2.drawString(s.getText(), (int) s.getX() + 2, (int) s.getY() + 2);
				g2.setColor(Color.WHITE);
				g2.drawString(s.getText(), (int) s.getX(), (int) s.getY());
			}
			g2.dispose();
		}

		return result;

	}

	public boolean canTalk() {
		D2Idx idx = playerDirIdx();
		// 範囲外のチェック
		if (idx.x < 0 || idx.y < 0) {
			return false;
		}
		if (idx.x >= getBaseLayer().getDataWidth() || idx.y >= getBaseLayer().getDataHeight()) {
			return false;
		}

		//NPCがいるかどうかを返す
		return getTile(idx).getNpc() != null;
	}

	//プレイヤーの向いている方向のD2IDXを返します。
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

	private MessageWindow mw;

	/**
	 * プレイヤーキャラクタの向いている方向にいるNPCに対して、いい感じの位置にメッセ-ーウインドウを表示します。
	 * メッセージウインドウのモデルはSimpleMessageWindowModelが使用されます。
	 *
	 * @return
	 * メッセージウインドウ。NPCがいない場合はnoNPCMessageを表示します。ただし、これは例外処置的なもので、この制御は、ゲームマネージャ側で行うべきです。
	 */
	public MessageWindow talk() {
		D2Idx idx = playerDirIdx();
		NPCSprite n = getTile(idx).getNpc();
		if (n != null) {
			n.notMove();
		}
		//NPCが画面下半分にいる場合はメッセージウインドウを上に表示。上半分にいる場合は下に表示。
		float buffer = 24;
		float x = buffer;
		float y = n == null || n.getCenterY() < GameOption.getInstance().getWindowSize().width / 2
				? GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2 + buffer * 2
				: buffer;
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - (buffer * 2);
		float h = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3;
		Text t = n == null ? new Text("") : textStorage.get(n.getTextID());
		if (n != null) {
			n.to(playerCharacter.get(0).getCurrentDir().reverse());
		}
		t.reset();
		mw = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel(), textStorage, t);

		if (isDebugMode()) {
			kinugasa.game.GameLog.print("FM TALK: textID[" + t.getName() + "]");
		}

		return mw;
	}

	/**
	 * メッセージウインドウを閉じます。ついでにNPCの移動を解除します。
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
	 * NPCの移動を一括停止します。
	 */
	public void NPCMoveStop() {
		npcStorage.forEach(c -> c.notMove());
	}

	/**
	 * NPCの移動停止を一括解除します。
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
		Sound currentBGM = this.bgm;
		FieldMap fm = FieldMapStorage.getInstance().get(n.getExitFieldMapName()).build();
		if (fm.bgm == null) {
			fm.bgm = currentBGM;
		}
		if (n.getExitNodeName() == null) {
			fm.setCurrentIdx(n.getIdx());
		} else {
			if (!fm.getNodeStorage().contains(n.getExitNodeName())) {
				throw new GameSystemException("Node not found : " + fm.getName() + "." + n.getExitNodeName() + " from " + this.getName());
			}
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
			kinugasa.game.GameLog.print("CHANGE_MAP: " + n);
		}
		return fm;
	}

	@Override
	public String toString() {
		return "FieldMap{" + "name=" + name + '}';
	}

}
