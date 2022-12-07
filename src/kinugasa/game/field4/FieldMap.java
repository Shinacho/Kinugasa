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
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.system.EncountInfo;
import kinugasa.game.system.EnemySetStorageStorage;
import kinugasa.game.system.GameSystemException;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
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

/**
 *
 * @vesion 1.0.0 - 2022/11/08_17:18:11<br>
 * @author Dra211<br>
 */
public class FieldMap implements Drawable, Nameable, Disposable {

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
	private FieldEventStorage fieldEventStorage;//nullable
	private NodeStorage nodeStorage = new NodeStorage();
	private TextStorage textStorage;//nullable
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
		enemyStorageName = root.getAttributes().contains("esName") ? root.getAttributes().get("esName").getValue() : null;

		//エンカウントカウンターの処理
		//定義されていない場合はエンカウントしない
		if (root.getAttributes().contains("encountCounterDefault")) {
			this.encountCounter = new ManualTimeCounter(root.getAttributes().get("encountCounterDefault").getIntValue());
			int r = encountCounter.getCurrentTime();
			r = Random.randomAbsInt(r - r / 2, r + r / 2);
			encountCounter.setCurrentTime(r);
		} else {
			this.encountCounter = ManualTimeCounter.FALSE;
		}

		//FM描画倍率を取得
		float mg = 1;
		if (root.hasAttribute("mg")) {
			mg = root.getAttributes().get("mg").getFloatValue();
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
					String soundMapName = e.getAttributes().get("se").getValue().split("/")[0];
					String soundName = e.getAttributes().get("se").getValue().split("/")[1];
					node.setSe(SoundStorage.getInstance().get(soundMapName).get(soundName));
				}
				nodeStorage.add(node);
			}

		}
		//出口専用ノード
		{
			for (XMLElement e : root.getElement("outNode")) {
				String name = e.getAttributes().get("name").getValue();
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				Node node = Node.ofOutNode(name, x, y);
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
				String[] lineVal = e.getValue().replaceAll(" ", "").replaceAll("\t", "").split(lineSep);
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

		//ビフォアレイヤー
		{
			for (XMLElement e : root.getElement("before")) {
				String imageName = e.getAttributes().get("image").getValue();
				float angle = e.getAttributes().contains("angle") ? e.getAttributes().get("angle").getFloatValue() : 0;
				float speed = e.getAttributes().contains("speed") ? e.getAttributes().get("speed").getFloatValue() : 0;
				float tp = e.getAttributes().contains("tp") ? e.getAttributes().get("tp").getFloatValue() : 1;
				float bmg = e.getAttributes().contains("mg") ? e.getAttributes().get("mg").getFloatValue() : 1;

				BeforeLayerSprite bls = new BeforeLayerSprite(ImageUtil.load(imageName), tp, bmg, new KVector(angle, speed));
				beforeLayerSprites.add(bls);
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
				int w = e.getAttributes().get("w").getIntValue();
				int h = e.getAttributes().get("h").getIntValue();
				BufferedImage[] images = new SpriteSheet(image).split(w, h).images();
				int locationX = (int) (x * (chipW));
				int locationY = (int) (y * (chipH));
				frontAnimation.add(new FieldAnimationSprite(new D2Idx(x, y), locationX, locationY, w, h, new Animation(tc, images)));
			}

		}
		//イベント
		{
			for (XMLElement e : root.getElement("event")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String name = e.getAttributes().get("name").getValue();
				String script = e.getAttributes().get("script").getValue();
				fieldEventStorage.add(new FieldEventParser(name, new D2Idx(x, y), new XMLFile(script)).parse());
			}
		}

		data.dispose();

		// カメラ初期化
		camera = new FieldMapCamera(this);

		//NPC
		{
			if (!root.getElement("npc").isEmpty() && textStorage.isEmpty()) {
				throw new FieldMapDataException("npc is not empty, but text is empty");
			}
			for (XMLElement e : root.getElement("npc")) {
				String name = e.getAttributes().get("name").getValue();
				String initialIdxStr = e.getAttributes().get("initialIdx").getValue();
				D2Idx idx = new D2Idx(Integer.parseInt(initialIdxStr.split(",")[0]), Integer.parseInt(initialIdxStr.split(",")[1]));
				// 領域のチェック
				if (!getBaseLayer().include(idx)) {
					throw new FieldMapDataException("npc : " + name + " is out of bounds");
				}
				NPCMoveModel moveModel = NPCMoveModelStorage.getInstance().get(e.getAttributes().get("NPCMoveModel").getValue());
				Vehicle v = VehicleStorage.getInstance().get(e.getAttributes().get("vehicle").getValue());
				String textID = e.getAttributes().get("textID").getValue();
				int frame = e.getAttributes().get("frame").getIntValue();
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
		}

		//BGMの処理
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
						bgm = SoundStorage.getInstance().get(mapName).get(soundName).load();
						bgm.stopAndPlay();
					}
				}

			}
		}
		mapNameModel.reset();

		return this;
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
		if (debugMode) {
			backlLayeres.forEach(e -> e.debugDrawPC(g, playerCharacter.subList(1, playerCharacter.size()), currentIdx, playerDirIdx(), getBaseLayer().getLocation(), chipW, chipH));
			backlLayeres.forEach(e -> e.debugDrawNPC(g, npcStorage.asList(), getBaseLayer().getLocation(), chipW, chipH));
		}

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
			frontlLayeres.forEach(e -> e.debugDrawPC(g, playerCharacter.subList(1, playerCharacter.size()), currentIdx, playerDirIdx(), getBaseLayer().getLocation(), chipW, chipH));
			frontlLayeres.forEach(e -> e.debugDrawNPC(g, npcStorage.asList(), getBaseLayer().getLocation(), chipW, chipH));
		}
		frontAnimation.forEach(e -> e.draw(g));
		beforeLayerSprites.forEach(e -> e.draw(g));
		if (mapNameModel != null) {
			mapNameModel.drawMapName(this, g);
		}
		if (tooltipModel != null) {
			tooltipModel.drawTooltip(this, g);
		}
		if (debugMode) {
			float centerX = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 2;
			float centerY = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2;
			Point2D.Float p1 = new Point2D.Float(centerX - chipW, centerY - chipH);
			Point2D.Float p2 = new Point2D.Float(centerX + chipW, centerY + chipH);
			Point2D.Float p3 = new Point2D.Float(centerX - chipW, centerY + chipH);
			Point2D.Float p4 = new Point2D.Float(centerX + chipW, centerY - chipH);
			Graphics2D g2 = g.create();
			g2.setColor(Color.RED);
			g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
			g2.drawLine((int) p3.x, (int) p3.y, (int) p4.x, (int) p4.y);
			g2.dispose();
//			System.out.print("IDX : " + currentIdx);
//			System.out.println("  FM_LOCATION : " + getBaseLayer().getLocation());
		}
	}

	@Override
	public void dispose() {
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.dispose();
		}
		backlLayeres.forEach(e -> e.dispose());
		backlLayeres.clear();
		frontlLayeres.forEach(e -> e.dispose());
		frontlLayeres.clear();
		frontAnimation.clear();
		beforeLayerSprites.clear();
		bgm.dispose();
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
			playerCharacter.subList(1, playerCharacter.size()).forEach(v -> v.setCurrentIdx(currentIdx));
			playerCharacter.subList(1, playerCharacter.size()).forEach(v -> v.setTargetIdx(currentIdx));
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

	public void move() {
		D2Idx prevIdx = currentIdx.clone();
		camera.move();
		if (!prevIdx.equals(currentIdx)) {
			//新しいチップに乗った場合、エンカウントカウンターの処理
			int x = 0;
			for (MapChip c : getCurrentTile().getChip()) {
				x += c.getAttr().getEncountBaseValue();
			}
			encountCounter.sub(x);
			if (debugMode) {
				System.out.println("FM MOVE " + currentIdx + " / EC=" + encountCounter.getCurrentTime());
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
		r = Random.randomAbsInt(r - r / 2, r + r / 2);
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
			//イベントの実行
			List<FieldEvent> e = fieldEventStorage.get(idx);
			e.forEach(v -> v.exec(this));
		}
		this.currentIdx = idx.clone();

	}

	/**
	 * フィールドマップのキャラクタとビフォアレイヤー以外を描画した画像を生成します。
	 * バックグラウンド及びフロントアニメーションは、現在の状態が使用されます。 NPC及びキャラクタは表示されません。
	 *
	 * @param scale 拡大率。1で等倍、0.5で50%のサイズ。
	 * @param animation フロントアニメーションを描画するかどうか。
	 * @return 指定の拡大率で描画された画像。
	 */
	public KImage createMiniMap(float scale, boolean animation) {

		float w = getBaseLayer().getDataWidth() * getBaseLayer().getChip(0, 0).getImage().getWidth();
		float h = getBaseLayer().getDataHeight() * getBaseLayer().getChip(0, 0).getImage().getHeight();

		BufferedImage image = ImageUtil.newImage((int) w, (int) h);
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.QUALITY);
		if (backgroundLayerSprite != null) {
			g.drawImage(backgroundLayerSprite.getAWTImage(), 0, 0, null);
		}
		for (FieldMapLayerSprite s : backlLayeres) {
			g.drawImage(s.getImage(), 0, 0, null);
		}
		for (FieldMapLayerSprite s : frontlLayeres) {
			g.drawImage(s.getImage(), 0, 0, null);
		}
		if (animation) {
			for (FieldAnimationSprite a : frontAnimation) {
				float x = chipW * a.getIdx().x;
				float y = chipH * a.getIdx().y;
				g.drawImage(a.getAWTImage(), (int) x, (int) y, null);
			}
		}
		g.dispose();
		return new KImage(ImageEditor.resize(image, scale));
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
	 * プレイヤーキャラクタの向いている方向にいるNPCに対して、いい感じの位置にメッセ-ーウインドウを表示します。
	 * メッセージウインドウのモデルはSimpleMessageWindowModelが使用されます。
	 *
	 * @return
	 * メッセージウインドウ。NPCがいない場合はnoNPCMessageを表示します。ただし、これは例外処置的なもので、この制御は、ゲームマネージャ側で行うべきです。
	 */
	public MessageWindow talk() {
		D2Idx idx = playerDirIdx();
		NPC n = getTile(idx).getNpc();
		if (n != null) {
			n.notMove();
		}
		//NPCが画面下半分にいる場合はメッセージウインドウを上に表示。上半分にいる場合は下に表示。
		float buffer = 24;
		float x = buffer;
		float y = n == null || n.getCenterY() < GameOption.getInstance().getWindowSize().width / 2 ? GameOption.getInstance().getWindowSize().height / 2 + buffer : buffer;
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
	 * メッセージウインドウを閉じます。ついでにNPCの移動を解除します。
	 */
	public void closeMessagWindow() {
		mw.setVisible(false);
		if (mw != null) {
			mw = null;
		}
		npcStorage.forEach(c -> c.canMove());
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
		FieldMap fm = FieldMapStorage.getInstance().get(n.getExitFieldMapName()).build();
		fm.setCurrentIdx(fm.getNodeStorage().get(n.getExitNodeName()).getIdx());
		fm.getCamera().updateToCenter();
		fm.prevLocationList.clear();

		FieldMap.getPlayerCharacter().get(0).to(n.getOutDir());
		if (FieldMap.getPlayerCharacter().size() > 1) {
			FieldMap.getPlayerCharacter().subList(1, getPlayerCharacter().size()).forEach(v -> v.setCurrentIdx(getCurrentIdx()));
			FieldMap.getPlayerCharacter().subList(1, getPlayerCharacter().size()).forEach(v -> v.setLocation(playerCharacter.get(0).getLocation()));
		}
		n.getSe().load().play();
		System.out.println("CHANGE_MAP IN: " + n + " OUT:" + fm.getNodeStorage().get(n.getExitNodeName()));
		return fm;
	}

}
