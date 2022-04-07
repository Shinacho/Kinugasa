/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.game.rpgui;

import java.awt.image.BufferedImage;
import java.awt.image.RasterFormatException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.object.ImagePainter;
import kinugasa.object.ImagePainterStorage;
import kinugasa.object.ImageSprite;
import kinugasa.object.Sprite;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.NotYetLoadedException;
import kinugasa.resource.ReflectionClassNotFoundException;
import kinugasa.resource.sound.SoundMap;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLAttributeStorage;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.StopWatch;
import kinugasa.util.StringUtil;
import kinugasa.util.TimeCounter;

/**
 * フィールドマップをXMLからロードするためのビルダです.
 * <br>
 * 作成されたFieldMapBuilderは自動的にFieldMapBuildetStorageに追加されます。<br>
 * <br>
 * 備忘：BackgroundSpriteの表示サイズは、FieldMapBuilderのXMLに記載された幅・高さに依存する。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/29_15:52:17<br>
 * @author Dra0211<br>
 */
public final class XMLFieldMapBuilder implements FieldMapBuilder {

	private String name;
	private SoundMap soundMap;
	private NodeMap nodeMap;
	private XMLFile dataFileReader;
	private boolean loaded = false;
	private int chipWidth;
	private int chipHeight;
	private FieldMap fieldMap;
	private TextStorage textStorage;
	private int x, y;
	private String textDataPath;

	public XMLFieldMapBuilder(String name, SoundMap soundMap,
			NodeMap nodeMap, XMLFile dataFileReader, String textDataPath, int x, int y) {
		this.name = name;
		this.soundMap = soundMap;
		this.nodeMap = nodeMap;
		this.dataFileReader = dataFileReader;
		this.x = x;
		this.y = y;
		this.textDataPath = textDataPath;
		addThis();
	}

	/**
	 * FieldMapBuidletStorageにthisインスタンスを追加します.
	 */
	private void addThis() {
		FieldMapBuilderStorage.getInstance().add(this);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public FieldMap getFieldMap() throws NotYetLoadedException {
		if (!isLoaded()) {
			GameLog.print(Level.WARNING, "FieldMapが要求されましたが、ロードされていません " + this);
			throw new NotYetLoadedException("fieldMapBuilder : " + this + " is not yet loaded.");
		}
		return fieldMap;
	}

	@Override
	public XMLFieldMapBuilder load()
			throws IllegalXMLFormatException, NumberFormatException, NameNotFoundException,
			RasterFormatException, ReflectionClassNotFoundException {

		StopWatch watch = new StopWatch().start();
		dataFileReader.load();

		//ルートノードの検査
		XMLElement root = dataFileReader.getFirst();
		if (!"kinugasaFieldMapData".equals(root.getName())) {
			GameLog.print(Level.WARNING, "ルートノードが不正です " + dataFileReader);
			throw new IllegalXMLFormatException("illegal root node name : " + root);
		}
		//1つのチップサイズの設定
		chipWidth = root.getAttributes().get("chipWidth").getIntValue();
		chipHeight = root.getAttributes().get("chipHeight").getIntValue();

		fieldMap = parseLayer(root.getElement("layer"), root.getElement("background"));
		fieldMap.setX(x);
		fieldMap.setY(y);
		fieldMap.setName(name);
		dataFileReader.dispose();
		if (textDataPath != null) {
			textStorage = new TextStorage();
			textStorage.readFromXML(textDataPath);
		}
		loaded = true;
		watch.stop();
		GameLog.printInfo("フィールドマップデータが構築されました name=[" + getName() + "] (" + watch.getTime() + " ms)");
		return this;
	}

	private FieldMap parseLayer(List<XMLElement> layerElements, List<XMLElement> backgroundElements)
			throws IllegalXMLFormatException, NameNotFoundException, ReflectionClassNotFoundException,
			RasterFormatException {

		List<FieldMapLayer> fieldMapLayer = new ArrayList<FieldMapLayer>(layerElements.size());

		for (int i = 0, layerElementSize = layerElements.size(); i < layerElementSize; i++) {
			XMLElement layerElement = layerElements.get(i);

			fieldMapLayer.add(new FieldMapLayer(
					layerElement.getAttributes().get("z").getFloatValue(),
					layerElement.getAttributes().get("speed").getFloatValue(),
					new ObjectLayerSprite(parseAnySprite(layerElement)),
					parseMap(layerElement)));
		}
		Collections.sort(fieldMapLayer);

		return new FieldMap(this, parseBackground(backgroundElements), fieldMapLayer);
	}

	private List<Sprite> parseAnySprite(XMLElement parent)
			throws IllegalXMLFormatException, RasterFormatException,
			ReflectionClassNotFoundException {
		List<Sprite> result = new ArrayList<Sprite>();

		result.addAll(parseSprite(parent.getElement("sprite")));
		result.addAll(parseImageSprite(parent.getElement("imageSprite")));
		result.addAll(parseAnimationSprite(parent.getElement("animationSprite")));
		return result;
	}

	//レイヤ内のspriteエレメントを解析
	private List<Sprite> parseSprite(List<XMLElement> spriteElement)
			throws NumberFormatException, ReflectionClassNotFoundException {
		if (spriteElement == null) {
			return Collections.<Sprite>emptyList();
		}
		List<Sprite> result = new ArrayList<Sprite>(spriteElement.size());
		for (int i = 0, size = spriteElement.size(); i < size; i++) {
			XMLElement element = spriteElement.get(i);
			XMLAttributeStorage attr = element.getAttributes();
			int x = chipWidth * attr.get("chipX").getIntValue();
			int y = chipHeight * attr.get("chipY").getIntValue();
			float width = attr.get("width").getFloatValue();
			float height = attr.get("height").getFloatValue();
			Sprite sprite;
			try {
				Class<?> type = Class.forName(attr.get("class").getValue());
				sprite = (Sprite) type.newInstance();
			} catch (ClassNotFoundException ex) {
				throw new ReflectionClassNotFoundException(ex);
			} catch (InstantiationException ex) {
				throw new ReflectionClassNotFoundException(ex);
			} catch (IllegalAccessException ex) {
				throw new ReflectionClassNotFoundException(ex);
			}
			if (sprite == null) {
				throw new ReflectionClassNotFoundException("reflection failed");
			}
			sprite.setBounds(x, y, width, height);
			sprite.setVisible(attr.contains("visible")
					? attr.get("visible").getBool()
					: true);

			sprite.setExist(attr.contains("exist")
					? attr.get("exist").getBool()
					: true);
			result.add(sprite);
		}
		return result;
	}

	private List<Sprite> parseImageSprite(List<XMLElement> spriteElement)
			throws NumberFormatException, RasterFormatException, NameNotFoundException {
		if (spriteElement == null) {
			return Collections.<Sprite>emptyList();
		}
		List<Sprite> result = new ArrayList<Sprite>(spriteElement.size());
		for (int i = 0, size = spriteElement.size(); i < size; i++) {
			XMLElement element = spriteElement.get(i);
			XMLAttributeStorage attr = element.getAttributes();
			int x = chipWidth * attr.get("chipX").getIntValue();
			int y = chipHeight * attr.get("chipY").getIntValue();
			float width = attr.get("width").getFloatValue();
			float height = attr.get("height").getFloatValue();
			BufferedImage image = parseImages(element)[0];
			ImagePainter painter = attr.contains("painterName")
					? ImagePainterStorage.getInstance().get(attr.get("painterName").getValue())
					: ImagePainterStorage.IMAGE_BOUNDS_XY;
			Sprite sprite = new ImageSprite(x, y, width, height,
					image, painter);
			sprite.setVisible(attr.contains("visible")
					? attr.get("visible").getBool()
					: true);
			sprite.setExist(attr.contains("exist")
					? attr.get("exist").getBool()
					: true);
			result.add(sprite);
		}
		return result;
	}

	private List<Sprite> parseAnimationSprite(List<XMLElement> spriteElement) {
		if (spriteElement == null) {
			return Collections.<Sprite>emptyList();
		}
		List<Sprite> result = new ArrayList<Sprite>(spriteElement.size());
		for (int i = 0, size = spriteElement.size(); i < size; i++) {
			XMLElement element = spriteElement.get(i);
			XMLAttributeStorage attr = element.getAttributes();
			int x = chipWidth * attr.get("chipX").getIntValue();
			int y = chipHeight * attr.get("chipY").getIntValue();
			float width = attr.get("width").getFloatValue();
			float height = attr.get("height").getFloatValue();
			TimeCounter timeCounter = new FrameTimeCounter(StringUtil.parseIntCSV(attr.get("frame").getValue()));
			BufferedImage[] image = parseImages(element);
			ImagePainter painter = attr.contains("painterName")
					? ImagePainterStorage.getInstance().get(attr.get("painterName").getValue())
					: ImagePainterStorage.IMAGE_BOUNDS_XY;
			AnimationSprite sprite = new AnimationSprite(x, y, width, height,
					new Animation(timeCounter, image), painter);
			sprite.setVisible(attr.contains("visible")
					? attr.get("visible").getBool()
					: true);
			sprite.setExist(attr.contains("exist")
					? attr.get("exist").getBool()
					: true);
			result.add(sprite);
		}
		return result;
	}

	//map属性を持つエレメントを解析する
	private MapLayerSprite parseMap(XMLElement parent)
			throws IllegalXMLFormatException, NameNotFoundException,
			RasterFormatException {
		List<XMLElement> mapElements = parent.getElement("map");
		CSVMapData[] mapData = new CSVMapData[mapElements.size()];
		int[] frame = new int[mapElements.size()];
		for (int i = 0, mapElementSize = mapElements.size(); i < mapElementSize; i++) {
			XMLElement mapElement = mapElements.get(i);
			XMLAttributeStorage mapElementAttr = mapElement.getAttributes();
			String values = mapElement.getValue();
			if (values == null) {
				values = "";
			}
			values = values.replaceAll(" ", "").replaceAll("\t", "").replaceAll("\n", "");
			mapData[i] = new CSVMapData(
					ChipSetStorage.getInstance().get(mapElementAttr.get("chipSet").getValue()),
					values.split(mapElementAttr.get("lineSeparator").getValue()),
					chipWidth, chipHeight);
			frame[i] = mapElementAttr.contains("frame")
					? mapElementAttr.get("frame").getIntValue() : 0;
		}
		return new MapLayerSprite(
				FieldMapBuilderStorage.getInstance().getX(),
				FieldMapBuilderStorage.getInstance().getY(),
				FieldMapBuilderStorage.getInstance().getWidth(),
				FieldMapBuilderStorage.getInstance().getHeight(),
				new FrameTimeCounter(frame), mapData);
	}

	/**
	 * XML要素を解析し、BackgroundLayerを構築します.
	 *
	 * @param elements ルートエレメントから取得視したbackground要素を送信します。<br>
	 * @return 構築されたBackgroundLayerを返します。<br>
	 * @throws IllegalXMLFormatException background要素が2つ以上ある場合や 要素が不正な場合に投げられます。<br>
	 * @throws NameNotFoundException 属性の値が整数に変換できない場合に投げられます。<br>
	 * @throws RasterFormatException SpriteSheetおよびImageの切り出しにおいて 不正な座標を参照した際に投げられます。<br>
	 */
	private BackgroundLayerSprite parseBackground(List<XMLElement> elements)
			throws IllegalXMLFormatException, NameNotFoundException,
			RasterFormatException {
		if (elements.size() > 1) {
			throw new IllegalXMLFormatException("illegal background element size=" + elements.size());
		}
		XMLElement backgroundElement = elements.get(0);
		BufferedImage[] images = parseImages(backgroundElement);
		if (images == null) {
			throw new IllegalXMLFormatException("illegal image element : " + backgroundElement);
		}
		XMLAttributeStorage backgroundElementAttr = backgroundElement.getAttributes();
		return new BackgroundLayerSprite(
				backgroundElementAttr.get("speed").getFloatValue(),
				FieldMapBuilderStorage.getInstance().getX(),
				FieldMapBuilderStorage.getInstance().getY(),
				FieldMapBuilderStorage.getInstance().getWidth(),
				FieldMapBuilderStorage.getInstance().getHeight(),
				chipWidth, chipHeight,
				new FrameTimeCounter(StringUtil.parseIntCSV(backgroundElementAttr.get("frame").getValue())),
				images);
	}

	/**
	 * imageおよびspriteSheetタグを解析し、画像配列を作成します.
	 *
	 * @param parent image又はspriteSheetを持つタグを送信します。<br>
	 * @return 送信されたタグに基づいて構築された画像配列を返します。 parentにimageまたはspriteSheet要素が存在しない場合はnullを返します。<br>
	 * @throws RasterFormatException 不正な座標を参照したときに投げられます。<br>
	 */
	private BufferedImage[] parseImages(XMLElement parent)
			throws RasterFormatException {
		BufferedImage[] images = null;
		if (parent.getElement("spriteSheet") != null) {
			XMLElement spriteSheetElement = parent.getElement("spriteSheet").get(0);
			XMLAttributeStorage spriteSheetAttr = spriteSheetElement.getAttributes();
			int cutX = spriteSheetAttr.get("x").getIntValue();
			int cutY = spriteSheetAttr.get("y").getIntValue();
			int cutWidth = spriteSheetAttr.get("width").getIntValue();
			int cutHeight = spriteSheetAttr.get("height").getIntValue();
			SpriteSheet spriteSheet = new SpriteSheet(spriteSheetAttr.get("src").getValue());
			String cutType = spriteSheetAttr.get("cutType").getValue();
			if ("SPLIT".equals(cutType)) {
				spriteSheet.split(cutWidth, cutHeight);
			} else if ("COLUMN".equals(cutType)) {
				spriteSheet.columns(cutX, cutWidth, cutHeight);
			} else {
				spriteSheet.rows(cutY, cutWidth, cutHeight);
			}
			images = spriteSheet.images();
		} else if (parent.getElement("image") != null) {
			List<XMLElement> imageElements = parent.getElement("image");
			images = new BufferedImage[imageElements.size()];
			for (int i = 0, imageElementsSize = imageElements.size(); i < imageElementsSize; i++) {
				XMLElement imageElement = imageElements.get(i);
				BufferedImage image = ImageUtil.load(imageElement.getAttributes().get("src").getValue());
				if (imageElement.getElement("cutter") != null) {
					XMLElement cutterElement = imageElement.getElement("cutter").get(0);
					XMLAttributeStorage cutterAttr = cutterElement.getAttributes();
					int cutX = cutterAttr.get("x").getIntValue();
					int cutY = cutterAttr.get("y").getIntValue();
					int cutWidth = cutterAttr.get("width").getIntValue();
					int cutHeight = cutterAttr.get("height").getIntValue();
					image = ImageUtil.trimming(image, cutX, cutY, cutWidth, cutHeight);
				}
				images[i] = image;
			}
		}
		return images;
	}

	@Override
	public XMLFieldMapBuilder free() {
		if (textStorage != null) {
			textStorage.dispose();
		}
		fieldMap = null;
		loaded = false;
		GameLog.printInfo("フィールドマップデータ、テキストデータが開放されました XMLFieldMapBuilder.name=[" + getName() + "]");
		return this;
	}

	@Override
	public XMLFieldMapBuilder freeSound() {
		soundMap.dispose();
		GameLog.printInfo("サウンドデータが開放されました XMLFieldMapBuilder.name=[" + getName() + "]");
		return this;
	}

	@Override
	public boolean isLoaded() {
		return loaded;
	}

	@Override
	public SoundMap getSoundMap() {
		return soundMap;
	}

	public void setSoundMap(SoundMap soundMap) {
		this.soundMap = soundMap;
	}

	@Override
	public NodeMap getNodeMap() {
		return nodeMap;
	}

	public void setNodeMap(NodeMap nodeMap) {
		this.nodeMap = nodeMap;
	}

	public XMLFile getDataFileReader() {
		return dataFileReader;
	}

	public void setDataFileReader(XMLFile dataFileReader) {
		this.dataFileReader = dataFileReader;
	}

	@Override
	public int getChipWidth() {
		return chipWidth;
	}

	@Override
	public int getChipHeight() {
		return chipHeight;
	}

	@Override
	public MessageWindowSprite getMessageWindowSprite() throws IllegalStateException {
		if (!loaded) {
			throw new IllegalStateException("MessageWindowSpriteが要求されましたが、ロードされていません");
		}
		if (textStorage == null) {
			return null;
		}
		return textStorage.getMessageWindowSprite();
	}

	@Override
	public TextStorage getTextStorage() {
		return textStorage;
	}

	@Override
	public String toString() {
		return "FieldMapBuilder{" + "name=" + name + ", soundMap=" + soundMap + ", nodeMap=" + nodeMap.size() + ", dataFileReader=" + dataFileReader + ", loaded=" + loaded + '}';
	}

	@Override
	public int compareTo(FieldMapBuilder o) {
		return this.name.compareTo(o.getName());
	}
}
