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
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageEditor;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.Drawable;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.KImage;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;

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

	private final String name;
	private final XMLFile data;
	//------------------------------------------------
	private BackgroundLayerSprite backgroundLayerSprite; //nullable
	private List<FieldMapLayerSprite> backlLayeres = new ArrayList<>();
	private FieldMapCharacter playerCharacter;
	private List<FieldMapCharacter> character = new ArrayList<>();
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

	public D2Idx getCurrentIdx() {
		return currentIdx;
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

	public List<FieldMapCharacter> getCharacter() {
		return character;
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

	public FieldMapCharacter getPlayerCharacter() {
		return playerCharacter;
	}

	public void setPlayerCharacter(FieldMapCharacter playerCharacter) {
		this.playerCharacter = playerCharacter;
	}

	public FieldMap build() throws FieldMapDataException {
		data.load();
		XMLElement root = data.getFirst();

		//事前に別設定されたテキストストレージの取得
		//テキストのないマップの場合、ロードしないことを許可する
		if (root.getAttributes().contains("textStorageName")) {
			String textStorageName = root.getAttributes().get("textStorageName").getValue();
			textStorage = TextStorageStorage.getInstance().get(textStorageName);
		}
		//事前に別設定されたフィールドイベントストレージの取得
		//イベントのないマップの場合、ロードしないことを許可する
		if (root.getAttributes().contains("eventStorageName")) {
			String fieldEventStorageName = root.getAttributes().get("eventStorageName").getValue();
			fieldEventStorage = FieldEventStorageStorage.getInstance().get(fieldEventStorageName);
		}
		// 表示倍率・・・ない場合は1倍とする
		float mg = root.getAttributes().contains("mg") ? root.getAttributes().get("mg").getFloatValue() : 1;

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
				backgroundLayerSprite = new BackgroundLayerSprite(FieldMapStorage.getScreenWidth(), FieldMapStorage.getScreenWidth());
				backgroundLayerSprite.build(mg, tc, images);
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
				nodeStorage.add(node);
			}

		}
		//出口専用ノード
		{
			for (XMLElement e : root.getElement("outNode")) {
				String name = e.getAttributes().get("name").getValue();
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				FourDirection outDir = FourDirection.valueOf(e.getAttributes().get("outDir").getValue());
				Node node = Node.ofOutNode(name, x, y, outDir);
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
				int w = (int) (data[0][0].getImage().getWidth() * mg);
				int h = (int) (data[0][0].getImage().getWidth() * mg);

				FieldMapLayerSprite layerSprite = new FieldMapLayerSprite(chipset, w, h, data, mg);
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
				int w = (int) (data[0][0].getImage().getWidth() * mg);
				int h = (int) (data[0][0].getImage().getWidth() * mg);

				FieldMapLayerSprite layerSprite = new FieldMapLayerSprite(chipset, w, h, data, mg);
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
				images = ImageEditor.resizeAll(images, mg);
				w *= mg;
				h *= mg;
				int locationX = (int) (x * (chipW));
				int locationY = (int) (y * (chipH));
				frontAnimation.add(new FieldAnimationSprite(new D2Idx(x, y), locationX, locationY, w, h, new Animation(tc, images)));
			}

		}
		//NPC
		{
			//テキストストレージが空の場合フォーマットエラー
			//NPCストレージの内容をキャラクタリストに全部入れること
		}
		//TODO

		//イベント
		//TODO
		data.dispose();

		// カメラ初期化
		camera = new FieldMapCamera(this);

		return this;
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
		if (debugMode) {
			backlLayeres.forEach(e -> e.debugDraw(g, currentIdx, getBaseLayer().getLocation(), chipW, chipH));
		}
		character.forEach(e -> e.draw(g));
		if (playerCharacter != null) {
			playerCharacter.draw(g);
		}
		frontlLayeres.forEach(e -> e.draw(g));
		if (debugMode) {
			frontlLayeres.forEach(e -> e.debugDraw(g, currentIdx, getBaseLayer().getLocation(), chipW, chipH));
		}
		frontAnimation.forEach(e -> e.draw(g));
		beforeLayerSprites.forEach(e -> e.draw(g));
		if (debugMode) {
			float centerX = FieldMapStorage.getScreenWidth() / 2;
			float centerY = FieldMapStorage.getScreenHeight() / 2;
			Point2D.Float p1 = new Point2D.Float(centerX - chipW, centerY - chipH);
			Point2D.Float p2 = new Point2D.Float(centerX + chipW, centerY + chipH);
			Point2D.Float p3 = new Point2D.Float(centerX - chipW, centerY + chipH);
			Point2D.Float p4 = new Point2D.Float(centerX + chipW, centerY - chipH);
			Graphics2D g2 = g.create();
			g2.setColor(Color.RED);
			g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
			g2.drawLine((int) p3.x, (int) p3.y, (int) p4.x, (int) p4.y);
			g2.dispose();
			System.out.print("IDX : " + currentIdx);
			System.out.println("  FM_LOCATION : " + getBaseLayer().getLocation());
		}
	}

	@Override
	public void dispose() {
		backgroundLayerSprite.dispose();
		backgroundLayerSprite = null;
		backlLayeres.clear();
		backlLayeres = null;
		character.clear();
		character = null;
		playerCharacter = null;
		frontlLayeres.clear();
		frontlLayeres = null;
		frontAnimation.clear();
		frontAnimation = null;
		beforeLayerSprites.clear();
		beforeLayerSprites = null;

		npcStorage.clear();
		npcStorage = null;
		if (fieldEventStorage != null) {
			fieldEventStorage.dispose();
			fieldEventStorage = null;
		}
		nodeStorage.clear();
		nodeStorage = null;
		if (textStorage != null) {
			textStorage.clear();
			textStorage = null;
		}
	}

	public void move() {
		camera.move();
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
		FieldEvent event = fieldEventStorage == null ? null : fieldEventStorage.get(idx);
		Node node = nodeStorage.get(idx);

		return new FieldMapTile(chip, npc, idx.equals(currentIdx) ? playerCharacter : null, event, node);
	}

	public FieldMapTile getCurrentCenterTile() {
		return getTile(currentIdx);
	}

	/**
	 * プレイヤーの座標を更新します。x,yが中心（プレイヤーロケーション）になるようにマップの表示座標を設定します。
	 * このメソッドでは、移動可能かどうかの判定は行いません。移動判定はgetTileから行ってください。
	 *
	 * @param idx マップデータのインデックス。
	 */
	public void setCurrentIdx(D2Idx idx) {
		this.currentIdx = idx.clone();
	}

	/**
	 * フィールドマップのキャラクタとビフォアレイヤー以外を描画した画像を生成します。
	 * バックグラウンド及びフロントアニメーションは、現在の状態が使用されます。
	 *
	 * @param scale 拡大率。1で等倍、0.5で50%のサイズ。
	 * @param animation フロントアニメーションを描画するかどうか。
	 * @param playerChara プレイヤーキャラクタ?を描画するかどうか。
	 * @param npc npcキャラクターを描画するかどうか。
	 * @return 指定の拡大率で描画された画像。
	 */
	public KImage createMiniMap(float scale, boolean npc, boolean playerChara, boolean animation) {

		float w = getBaseLayer().getDataWidth() * getBaseLayer().getChip(0, 0).getImage().getWidth() * getBaseLayer().getMg();
		float h = getBaseLayer().getDataHeight() * getBaseLayer().getChip(0, 0).getImage().getHeight() * getBaseLayer().getMg();

		BufferedImage image = ImageUtil.newImage((int) w, (int) h);
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.QUALITY);
		if (backgroundLayerSprite != null) {
			g.drawImage(backgroundLayerSprite.getAWTImage(), 0, 0, null);
		}
		for (FieldMapLayerSprite s : backlLayeres) {
			g.drawImage(s.getImage(), 0, 0, null);
		}
		if (npc) {
			for (FieldMapCharacter c : character) {
				g.drawImage(c.getAWTImage(), 0, 0, null);
			}
		}
		if (playerCharacter != null && playerChara) {
			g.drawImage(playerCharacter.getAWTImage(), 0, 0, null);
		}
		for (FieldMapLayerSprite s : frontlLayeres) {
			g.drawImage(s.getImage(), 0, 0, null);
		}
		if (animation) {
			for (FieldAnimationSprite a : frontAnimation) {
				float x = chipW * a.getIdx().x;
				float y = chipH * a.getIdx().y;
				g.drawImage(a.getAWTImage(), (int)x, (int)y, null);
			}
		}
		g.dispose();
		return new KImage(ImageEditor.resize(image, scale));
	}

}
