/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game.field;

import java.awt.Point;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.GameLog;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.TextStorage;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.BasicSprite;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/25_18:40:10<br>
 * @author Dra211<br>
 */
public class FieldMap extends BasicSprite implements Nameable, Disposable {

	private final String name;
	private final XMLFile dataFile;
	private final Sound bgm = null;
	private final List<FieldEvent> loadEvent = new ArrayList<>();
	private final List<FieldEvent> stepEvent = new ArrayList<>();
	private final List<FieldEvent> disposeEvent = new ArrayList<>();
	private final List<FieldEvent> otherEvent = new ArrayList<>();
	private BackgroundLayerSprite backSprite;
	private List<FieldMapLayer> layers = new ArrayList<>();
	private BasicSprite beforeSprite;
	private Map<Point, FieldMapNode> nodeMap = new HashMap<>();
	private static int charX, charY;
	private TooltipModel tlModel;
	private boolean loaded = false;
	private TextStorage text;
	private Point currentCharPoint = new Point(charX, charY);
	static boolean debug = false;
	private NPCLayer npcs;
	private Point2D.Float mwLocation;
	private MessageWindow mw;

	public List<FieldEvent> getStepEvent() {
		return stepEvent;
	}

	public FieldMap(String name, XMLFile dataFile) {
		this.name = name;
		this.dataFile = dataFile;
		this.tlModel = new SimpleToolTipModel();
	}

	@Override
	public String getName() {
		return name;
	}

	public BufferedImage createMiniMap() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (backSprite != null) {
			backSprite.draw(g);
		}
		layers.forEach((l) -> l.draw(g));
		npcs.draw(g);
		beforeSprite.draw(g);
		tlModel.draw(g, this);
	}

	public TooltipModel getTooltipModel() {
		return tlModel;
	}

	public void setTooltipModel(TooltipModel tlModel) {
		this.tlModel = tlModel;
	}

	public boolean canMapChange() {
		if (debug) {
			System.out.println("CHANGE_MAP:" + tlModel.accept(this) + " " + nodeMap.get(getCurrentCharPoint()));
		}
		return tlModel.accept(this);
	}

	public FieldMapNode getCurrentNode() {
		return nodeMap.get(getCurrentCharPoint());
	}

	public FieldMap load() {
		if (!dataFile.getFile().exists()) {
			throw new FileNotFoundException(dataFile + " is not found");
		}

		XMLElement root = dataFile.load().getFirst();
		int windowW = root.getAttributes().get("windowW").getIntValue();
		int windowH = root.getAttributes().get("windowH").getIntValue();

		// バックグラウンドレイヤー（海）のロード
		if (root.getElement("background") != null) {
			XMLElement back = root.getElement("background").get(0);
			int frame = back.getAttributes().get("frame").getIntValue();
			int w = back.getAttributes().get("w").getIntValue();
			int h = back.getAttributes().get("h").getIntValue();
			int ds = back.getAttributes().get("drawSize").getIntValue();
			String mode = back.getAttributes().get("dir").getValue();
			String[] modes = mode.contains(",") ? mode.split(",") : new String[]{mode};
			BufferedImage[] images = new SpriteSheet(ImageUtil.load(back.getAttributes().get("image").getValue())).rows(0, w, h).images();
			this.backSprite = new BackgroundLayerSprite(windowW, windowH);
			for (String m : modes) {
				switch (m.toLowerCase()) {
					case "n":
						backSprite.toNorth();
						break;
					case "s":
						backSprite.toSouth();
						break;
					case "e":
						backSprite.toEast();
						break;
					case "w":
						backSprite.toWest();
						break;
					default:
						throw new AssertionError();
				}
			}
			backSprite.build(ds, new FrameTimeCounter(frame), images);
		}

		// ビフォアレイヤー（雲）のロード
		{
			XMLElement back = root.getElement("before").get(0);
			float ang = back.getAttributes().get("angle").getFloatValue();
			float spd = back.getAttributes().get("speed").getFloatValue();
			float tp = back.getAttributes().get("transparent").getFloatValue();
			int ds = back.getAttributes().get("drawMag").getIntValue();

			BufferedImage image = ImageUtil.load(back.getAttributes().get("image").getValue());
			this.beforeSprite = new BeforeLayerSprite(image, windowW, windowH, tp, ds, new KVector(ang, spd));
		}
		// テキストのロード
		this.text = new TextStorage();
		if (root.getElement("text") != null) {
			XMLElement textElement = root.getElement("text").get(0);
			String src = textElement.getAttributes().get("data").getValue();
			this.text = new TextStorage();
			text.readFromXML(src);
		}

		//ロード時イベントのロード
		if (root.getElement("load").get(0).getElement("event") != null) {
			root.getElement("load").get(0).getElement("event").forEach(e -> {
				loadEvent.add(createEvent(e));
			});
		}

		// 破棄時イベントのロード
		if (root.getElement("dispose").get(0).getElement("event") != null) {
			root.getElement("dispose").get(0).getElement("event").forEach(e -> {
				loadEvent.add(createEvent(e));
			});
		}

		// マスイベントのロード
		if (root.getElement("stepon").get(0).getElement("event") != null) {
			root.getElement("stepon").get(0).getElement("event").forEach(e -> {
				loadEvent.add(createEvent(e));
			});
		}

		// その他イベントのロード
		if (root.getElement("other").get(0).getElement("event") != null) {
			root.getElement("other").get(0).getElement("event").forEach(e -> {
				loadEvent.add(createEvent(e));
			});
		}

		//ノード
		{
			for (XMLElement e : root.getElement("node")) {
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				String name = e.getAttributes().get("name").getValue();
				if (e.getAttributes().contains("tgtMap")) {
					String tgtMapName = e.getAttributes().get("tgtMap").getValue();
					String exitNodeName = e.getAttributes().get("exitNode").getValue();
					String tooltip = e.getAttributes().get("tooltip").getValue();
					NodeAccepter na = NodeAccepterStorage.getInstance().get(e.getAttributes().get("accepterName").getValue());
					nodeMap.put(new Point(x, y), FieldMapNode.ofInOutNode(name, tgtMapName, exitNodeName, x, y, tooltip, na));
					if (e.getAttributes().contains("seMap")) {
						String seMap = e.getAttributes().get("seMap").getValue();
						String seName = e.getAttributes().get("seName").getValue();
						nodeMap.get(new Point(x, y)).setSe(SoundStorage.getInstance().get(seMap).get(seName));
					}
				} else {
					nodeMap.put(new Point(x, y), FieldMapNode.ofOutNode(name, x, y));
				}

			}
		}
		//layer
		root.getElement("layer").forEach(l -> {
			MapChipSet chipSet = MapChipSetStorage.getInstance().get(l.getAttributes().get("chipSet").getValue());
			int drawMG = l.getAttributes().get("mg").getIntValue();
			String lineSep = l.getAttributes().get("lineSeparator").getValue();
			float z = l.getAttributes().get("z").getFloatValue();

			String[] data2 = l.getValue().trim().split(lineSep);
			MapChip[][] data = new MapChip[data2.length][];

			for (int y = 0; y < data2.length; y++) {
				String[] d = data2[y].split(",");
				data[y] = new MapChip[d.length];
				for (int x = 0; x < d.length; x++) {
					data[y][x] = chipSet.get(d[x].trim());
				}
			}
			FieldMapLayer layer = new FieldMapLayer(chipSet, windowW, windowH, data, drawMG);
			layer.setZ(z);
			layers.add(layer);
		});
		Collections.sort(layers);

		// npc
//		npcs = new NPCLayer(
//				getX(),
//				getY(),
//				getWidth(),
//				getHeight(),
//				layers.get(0).getDataHeight(),
//				layers.get(0).getDataWidth()
//		);
//
//		if (root.getElement("npc") != null) {
//			root.getElement("npc").forEach(l -> {
//				int x = l.getAttributes().get("x").getIntValue();
//				int y = l.getAttributes().get("y").getIntValue();
//				String image = l.getAttributes().get("image").getValue();
//				String textId = l.getAttributes().get("tid").getValue();
//
//				float layerSize = layers.get(0).getChip(0, 0).getImage().getWidth() * layers.get(0).getMg();
//				float buf = layers.get(0).getChip(0, 0).getImage().getWidth() * layers.get(0).getMg() * 3;
//				NPC n = new NPC(name, x, y, buf, (int) layerSize, FourDirection.random(), this.text.get(textId), image);
//				npcs.add(n);
//			});
//		}

		backSprite.setLocation((Point2D.Float) layers.get(0).getLocation().clone());
		//ロード時イベントの処理
		loadEvent.forEach(FieldEvent::exec);

		// ロード状態更新
		loaded = true;
		setVisible(true);
		setExist(true);
		setSize(windowW, windowH);
		GameLog.printIfUsing(java.util.logging.Level.ALL, "FieldMap is loaded : " + getName());

		return this;
	}

	public Map<Point, FieldMapNode> getNodeMap() {
		return nodeMap;
	}

	private FieldEvent createEvent(XMLElement event) {
		String type = event.getAttributes().get("type").getValue();

		switch (type) {
			case "SOUND":
				String map = event.getAttributes().get("map").getValue();
				String name2 = event.getAttributes().get("name").getValue();
				String mode = event.getAttributes().get("mode").getValue();
				switch (mode) {
					case "STOP":
						return () -> SoundStorage.getInstance().get(map).get(name2).stop();
					case "STOP_AND_PLAY":
						return () -> SoundStorage.getInstance().get(map).get(name2).load().stopAndPlay();
					case "PLAY":
						return () -> SoundStorage.getInstance().get(map).get(name2).load().play();
					default:
						throw new AssertionError();
				}

			default:
				throw new AssertionError();
		}

	}

	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public void dispose() {
		disposeEvent.forEach(FieldEvent::exec);
		loadEvent.clear();
		disposeEvent.clear();
		otherEvent.clear();
		stepEvent.clear();
		backSprite = null;
		beforeSprite = null;
		nodeMap.clear();
		layers.forEach(v -> v.dispose());
		layers.clear();
		loaded = false;
		text.clear();
		text = null;
		npcs = null;
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible); //To change body of generated methods, choose Tools | Templates.
		beforeSprite.setVisible(visible);
		if (backSprite != null) {
			backSprite.setVisible(visible);
		}
		layers.forEach((s) -> s.setVisible(visible));
	}

	@Override
	public void setExist(boolean exist) {
		super.setExist(exist); //To change body of generated methods, choose Tools | Templates.
		beforeSprite.setExist(exist);
		if (backSprite != null) {
			backSprite.setExist(exist);
		}
		layers.forEach((s) -> s.setExist(exist));
	}

	@Override
	public void move() {
		super.move();
		backSprite.move();
		layers.forEach((s) -> s.move());
		// フィールドマップの移動によるNPCSの移動
		npcs.setVector(getVector().clone());
		npcs.move();
		// メッセージウインドウ消去
		if (mwLocation != null && getLocation().distance(mwLocation) > layers.get(0).getMg() * layers.get(0).getChip(0, 0).getImage().getWidth() * 2) {
			mw.setVisible(false);
		}

	}

	@Override
	public void setVector(KVector vector) {
		super.setVector(vector);
		backSprite.setVector(vector);
		layers.forEach((s) -> s.setVector(vector));
		npcs.setVector(vector);
	}

	@Override
	public void setSpeed(float speed) {
		super.setSpeed(speed); //To change body of generated methods, choose Tools | Templates.
		beforeSprite.setSpeed(speed);
		backSprite.setSpeed(speed);
		layers.forEach((s) -> s.setSpeed(speed));
		npcs.setSpeed(speed);
	}

	public List<FieldMapLayer> getLayers() {
		return layers;
	}

	// キャラクタの表示座標のバフ
	public static void setPlayerLocationBuf(int x, int y) {
		FieldMap.charX = x;
		FieldMap.charY = y;
	}

	public List<MapChip> getCurrentChip() {
		return getChip(charX, charY);
	}

	public List<MapChip> getChip(int x, int y) {
		List<MapChip> result = new ArrayList<>();
		for (FieldMapLayer l : layers) {
			result.add(l.getChip(x, y));
		}
		return result;
	}

	@Override
	public void setLocation(Point2D.Float location) {
		this.setLocation(location.x, location.y);
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		layers.forEach((l) -> l.setLocation(x, y));
		backSprite.setLocation(x, y);
	}

	public boolean canStep(BasicSprite c) {
		// チップの画像サイズ（pix）
		final float chipSizeW = layers.get(0).getChip(0, 0).getImage().getWidth() * layers.get(0).getMg();
		final float chipSizeH = layers.get(0).getChip(0, 0).getImage().getHeight() * layers.get(0).getMg();
		// 次ターンの左上のチップ座標を計算
		final float fieldMapX = ((-getX() + getVector().reverse().getLocation().x) / chipSizeW);
		final float fieldMapY = ((-getY() + getVector().reverse().getLocation().y) / chipSizeH);
		// プレイヤーの座標と左上のチップ位置を合成
		//       　　　↓画面上のキャラクタの座標
		final int x = (int) (FieldMap.charX + fieldMapX);
		final int y = (int) (FieldMap.charY + fieldMapY);
		currentCharPoint = new Point(x, y);

		if (debug) {
			System.out.print("next:" + x + "," + y);
		}
		// 領域外の判定
		if (x <= 0 || y <= 0) {
			if (debug) {
				System.out.println(" is under 0");
			}
			return false;
		}
		if (x >= layers.get(0).getDataWidth() - 1 || y >= layers.get(0).getDataHeight() - 1) {
			if (debug) {
				System.out.println(" is over size");
			}
			return false;
		}
		// NPCの衝突判定
		if (npcs.get(x, y) != null) {
			return false;
		}

		// 乗れないチップの判定
		List<MapChip> nextChip = getChip(x, y);
		Vehicle vh = VehicleStorage.getInstance().getCurrentVehicle();

		boolean result = nextChip.stream().allMatch((chip) -> vh.getStepOn().contains(chip.getAttr()));
		if (debug) {
			System.out.println(" is " + result + "(" + nextChip.get(0).getAttr() + ")");
		}
		// チップに乗ったイベントの実行
		if (result) {
			stepEvent.forEach(v -> v.exec());
		}
		return result;
	}

	public Point getCurrentCharPoint() {
		return (Point) currentCharPoint.clone();
	}

	public void pointUpdate(int x, int y) {
		float lx = x -= charX;
		float ly = y -= charY;
		// チップの画像サイズ（pix）
		float chipSizeW = layers.get(0).getChip(0, 0).getImage().getWidth() * layers.get(0).getMg();
		float chipSizeH = layers.get(0).getChip(0, 0).getImage().getHeight() * layers.get(0).getMg();

		setLocation((lx) * -(chipSizeW), (ly) * -(chipSizeH));
	}

	public FieldMapNode getNode(String name) {
		return nodeMap.values().stream().filter((v) -> v.getName().equals(name)).findFirst().get();
	}

	public FieldMapNode getNode(Point p) {
		return nodeMap.get(p);
	}

	public TextStorage getText() {
		return text;
	}

	public List<FieldEvent> getOtherEvent() {
		return otherEvent;
	}

	@Override
	public void update() {
		super.update(); //To change body of generated methods, choose Tools | Templates.
		layers.forEach(l -> l.update());
		backSprite.update();
		beforeSprite.update();
		npcs.update();
	}

	public NPCLayer getNPCLayer() {
		return npcs;
	}

	public NPC getNPC() {
		return npcs.get(currentCharPoint.x, currentCharPoint.y);
	}

	public MessageWindow createWindow() {
		if (getNPC() == null) {
			return null;
		}
		mwLocation = (Point2D.Float) getLocation().clone();
		return mw = this.text.createWindow(
				32,
				currentCharPoint.y < layers.get(0).getDataHeight() / 2 ? 32 : getHeight() - 200,
				getWidth() - 32 * 2,
				176,
				getNPC().getText()
		);
	}

	public MessageWindow talk() {
		if (getNPC() == null) {
			return null;
		}

		return createWindow();
	}

}
