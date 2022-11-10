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

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
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
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/08_17:18:11<br>
 * @author Dra211<br>
 */
public class FieldMap extends BasicSprite implements Nameable, Disposable {

	private final String name;
	private final XMLFile data;

	public FieldMap(String name, XMLFile data) {
		this.name = name;
		this.data = data;
	}

	@Override
	public String getName() {
		return name;
	}

	/**
	 * ��ʂɑ΂���v���C���[�̕\���ʒu�����S�̃^�C���̍��W�̉���.
	 */
	private static int playerLocationX, playerLocationY;
	/**
	 * �v���C���[�i�����S�j�̌��݃C���f�b�N�X.
	 */
	private D2Idx currentIdx;

	public static int getPlayerLocationX() {
		return playerLocationX;
	}

	public static int getPlayerLocationY() {
		return playerLocationY;
	}

	public static void setPlayerLocationX(int playerLocationX) {
		FieldMap.playerLocationX = playerLocationX;

	}

	public static void setPlayerLocationY(int playerLocationY) {
		FieldMap.playerLocationY = playerLocationY;
	}

	public static void setPlayerLocation(int x, int y) {
		setPlayerLocationX(x);
		setPlayerLocationY(y);
	}

	private BackgroundLayerSprite backgroundLayerSprite; //nullable
	private List<FieldMapLayerSprite> backlLayeres = new ArrayList<>();
	private List<BasicSprite> character = new ArrayList<>();
	private List<FieldMapLayerSprite> frontlLayeres = new ArrayList<>();
	private List<FieldAnimationSprite> frontAnimation = new ArrayList<>();
	private List<BeforeLayerSprite> beforeLayerSprites = new ArrayList<>();
	//------------------------------------------------
	private NPCStorage npcStorage = new NPCStorage();
	private FieldEventStorage fieldEventStorage;//nullable
	private NodeStorage nodeStorage = new NodeStorage();
	private TextStorage textStorage;//nullable

	public FieldMap build() throws FieldMapDataException {
		data.load();
		XMLElement root = data.getFirst();

		//���O�ɕʐݒ肳�ꂽ�e�L�X�g�X�g���[�W�̎擾
		//�e�L�X�g�̂Ȃ��}�b�v�̏ꍇ�A���[�h���Ȃ����Ƃ�������
		if (root.getAttributes().contains("textStorageName")) {
			String textStorageName = root.getAttributes().get("textStorageName").getValue();
			textStorage = TextStorageStorage.getInstance().get(textStorageName);
		}
		//���O�ɕʐݒ肳�ꂽ�t�B�[���h�C�x���g�X�g���[�W�̎擾
		//�C�x���g�̂Ȃ��}�b�v�̏ꍇ�A���[�h���Ȃ����Ƃ�������
		if (root.getAttributes().contains("eventStorageName")) {
			String fieldEventStorageName = root.getAttributes().get("eventStorageName").getValue();
			fieldEventStorage = FieldEventStorageStorage.getInstance().get(fieldEventStorageName);
		}
		// �\���{���E�E�E�Ȃ��ꍇ��1�{�Ƃ���
		float mg = root.getAttributes().contains("mg") ? root.getAttributes().get("mg").getFloatValue() : 1;

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
				backgroundLayerSprite = new BackgroundLayerSprite(FieldMapStorage.getScreenWidth(), FieldMapStorage.getScreenWidth());
				backgroundLayerSprite.build(mg, tc, images);
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
				Node node = Node.ofInOutNode(name, tgtMapName, exitNodeName, x, y, tooltip, accepter);
				nodeStorage.add(node);
			}

		}
		//�o����p�m�[�h
		{
			for (XMLElement e : root.getElement("outNode")) {
				String name = e.getAttributes().get("name").getValue();
				int x = e.getAttributes().get("x").getIntValue();
				int y = e.getAttributes().get("y").getIntValue();
				Node node = Node.ofOutNode(name, x, y);
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
				int w = (int) (data[0][0].getImage().getWidth() * mg);
				int h = (int) (data[0][0].getImage().getWidth() * mg);

				FieldMapLayerSprite layerSprite = new FieldMapLayerSprite(chipset, w, h, data, mg);
				backlLayeres.add(layerSprite);
			}

		}
		// �t�����g���C���[
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

		//�r�t�H�A���C���[
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
		// �`�b�v�T�C�Y�̎擾
		chipW = (int) (backlLayeres.get(0).getChip(0, 0).getImage().getWidth() * mg);
		chipH = (int) (backlLayeres.get(0).getChip(0, 0).getImage().getHeight() * mg);

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
				images = ImageEditor.resizeAll(images, mg);
				w *= mg;
				h *= mg;
				int locationX = (int) (x * (chipW * mg));
				int locationY = (int) (y * (chipH * mg));
				frontAnimation.add(new FieldAnimationSprite(new D2Idx(x, y), locationX, locationY, w, h, new Animation(tc, images)));
			}

		}
		//NPC
		{
			//�e�L�X�g�X�g���[�W����̏ꍇ�t�H�[�}�b�g�G���[

		}
		//TODO

		//�C�x���g
		//TODO
		data.dispose();

		// �v���C���[���P�[�V�����̍X�V
		currentIdx = new D2Idx(playerLocationX, playerLocationY);

		return this;
	}
	//1�^�C���̃T�C�Y
	private int chipW, chipH;

	@Override
	public void update() {

	}

	@Override
	public void draw(GraphicsContext g) {
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.draw(g);
		}
		backlLayeres.forEach(e -> e.draw(g));
		character.forEach(e -> e.draw(g));
		frontlLayeres.forEach(e -> e.draw(g));
		frontAnimation.forEach(e -> e.draw(g));
		beforeLayerSprites.forEach(e -> e.draw(g));
		if (debugMode) {
			Graphics2D g2 = g.create();
			g2.setColor(Color.WHITE);
			g2.fillOval(FieldMapStorage.getScreenWidth() / 2 - 2, FieldMapStorage.getScreenHeight() / 2 - 2, 4, 4);
			g2.setColor(Color.RED);
			g2.fillOval(FieldMapStorage.getScreenWidth() / 2 - 1, FieldMapStorage.getScreenHeight() / 2 - 1, 2, 2);
			g2.dispose();
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

	@Override
	public void move() {
		super.move(); //To change body of generated methods, choose Tools | Templates.
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.move();
		}
		backlLayeres.forEach(e -> e.move());
		character.forEach(e -> e.move());
		frontlLayeres.forEach(e -> e.move());
		frontAnimation.forEach(e -> e.move());
		//�v���C���[�L�����N�^�[�i���S�jIDX�X�V
		final float fieldMapX = ((-getX() + getVector().reverse().getLocation().x)) / chipW;
		final float fieldMapY = ((-getY() + getVector().reverse().getLocation().y)) / chipH;
		// �v���C���[�̍��W�ƍ���̃`�b�v�ʒu������
		//       �@�@�@����ʏ�̃L�����N�^�̍��W
		final int x = (int) (playerLocationX + fieldMapX);
		final int y = (int) (playerLocationY + fieldMapY);
		currentIdx.x = x;
		currentIdx.y = y;
		if (debugMode) {
			System.out.println("IDX : " + currentIdx);
			System.out.println("FM_LOCATION : " + getLocation());
		}
	}
	private static boolean debugMode = false;

	public static void setDebugMode(boolean debugMode) {
		FieldMap.debugMode = debugMode;
	}

	public static boolean isDebugMode() {
		return debugMode;
	}

	@Override
	public void setVector(KVector vector
	) {
		super.setVector(vector); //To change body of generated methods, choose Tools | Templates.
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.setVector(vector);
		}
		backlLayeres.forEach(e -> e.setVector(vector));
		character.forEach(e -> e.setVector(vector));
		frontlLayeres.forEach(e -> e.setVector(vector));
		frontAnimation.forEach(e -> e.setVector(vector));
	}

	@Override
	public void setLocation(Point2D.Float location) {
		super.setLocation(location); //To change body of generated methods, choose Tools | Templates.
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.setLocation(location);
		}
		backlLayeres.forEach(e -> e.setLocation(location));
		frontlLayeres.forEach(e -> e.setLocation(location));
		character.forEach(e -> e.setLocation(location));
		float fieldMapX = getX();
		float fieldMapY = getY();
		for (FieldAnimationSprite s : frontAnimation) {
			float xx = fieldMapX + (s.getIdx().x * chipW);
			float yy = fieldMapY + (s.getIdx().y * chipH);
			s.setLocation(xx, yy);
		}
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y); //To change body of generated methods, choose Tools | Templates.
		if (backgroundLayerSprite != null) {
			backgroundLayerSprite.setLocation(x, y);
		}
		backlLayeres.forEach(e -> e.setLocation(x, y));
		frontlLayeres.forEach(e -> e.setLocation(x, y));

		character.forEach(e -> e.setLocation(x, y));
		float fieldMapX = getX();
		float fieldMapY = getY();
		for (FieldAnimationSprite s : frontAnimation) {
			float xx = fieldMapX + (s.getIdx().x * chipW);
			float yy = fieldMapY + (s.getIdx().y * chipH);
			s.setLocation(xx, yy);
		}
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
		FieldEvent event = fieldEventStorage == null ? null : fieldEventStorage.get(idx).get();
		Node node = nodeStorage.get(idx);

		return new FieldMapTile(chip, npc, event, node);
	}

	public FieldMapTile getCurrentCenterTile() {
		return getTile(currentIdx);
	}

	/**
	 * �v���C���[�̍��W���X�V���܂��Bx,y�����S�i�v���C���[���P�[�V�����j�ɂȂ�悤�Ƀ}�b�v�̕\�����W��ݒ肵�܂��B
	 * ���̃��\�b�h�ł́A�ړ��\���ǂ����̔���͍s���܂���B
	 *
	 * @param idx �}�b�v�f�[�^�̃C���f�b�N�X�B
	 */
	public void setCurrentIdx(D2Idx idx) {
		if (backlLayeres.get(0).getDataHeight() <= idx.y || backlLayeres.get(0).getDataWidth() <= idx.x) {
			throw new IllegalArgumentException("idx is over the data : " + idx);
		}
		this.currentIdx = idx;
		currentIdx.x -= playerLocationX;
		currentIdx.y -= playerLocationY;

		//�\���ʒu�̌v�Z
		float x = -idx.x * chipW;
		float y = -idx.y * chipH;

		setLocation(x, y);
	}

}
