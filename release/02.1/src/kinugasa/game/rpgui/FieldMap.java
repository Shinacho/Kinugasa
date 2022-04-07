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

import java.awt.Shape;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.TVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.sound.SoundMap;

/**
 * フィールドマップの本体で、表示できる全てのデータを持ちます.
 * <br>
 * フィールドマップは複合スプライトのような構造になっています。 フィールドマップに対する座標や移動速度の設定は全ての子スプライトに 反映されます。子スプライトは背景と、フィールドマップレイヤがあります。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/05/02_22:41:14<br>
 * @author Dra0211<br>
 */
public class FieldMap extends BasicSprite implements Iterable<FieldMapLayer>, Disposable {

	private static final long serialVersionUID = -6273256850976425790L;
	private BackgroundLayerSprite backgroundLayerSprite;
	private List<FieldMapLayer> fieldMapLayers;
	private FieldMapBuilder fieldMapBuilder;

	public FieldMap(
			FieldMapBuilder fieldMapBuilder,
			BackgroundLayerSprite backgroundLayerSprite,
			List<FieldMapLayer> fieldMapLayers) {
		this.fieldMapBuilder = fieldMapBuilder;
		this.backgroundLayerSprite = backgroundLayerSprite;
		this.fieldMapLayers = fieldMapLayers;
	}

	public FieldMap(
			FieldMapBuilder fieldMapBuilder,
			BackgroundLayerSprite backgroundLayerSprite,
			FieldMapLayer... fieldMapLayers) {
		this(fieldMapBuilder, backgroundLayerSprite, Arrays.asList(fieldMapLayers));
	}

	//順番は適当
	public List<MapChip> getChip(int x, int y) throws ArrayIndexOutOfBoundsException {
		List<MapChip> result = new ArrayList<MapChip>();
		for (int i = 0, size = fieldMapLayers.size(); i < size; i++) {
			MapChip[] chips = fieldMapLayers.get(i).getMapLayerSprite().getChip(x, y);
			result.addAll(Arrays.asList(chips));
		}
		return result;
	}

	public List<ChipAttribute> getAttribute(int x, int y) throws ArrayIndexOutOfBoundsException {
		List<MapChip> mapChipList = getChip(x, y);
		List<ChipAttribute> result = new ArrayList<ChipAttribute>(mapChipList.size());
		for (int i = 0, size = fieldMapLayers.size(); i < size; i++) {
			result.add(mapChipList.get(i).getAttribute());
		}
		return result;
	}

	public boolean stepOn(int x, int y) {
		return stepOn(VehicleStorage.getInstance().getCurrentVehicle(), x, y);
	}

	public boolean stepOn(Vehicle vehicle, int x, int y) {
		if (vehicle == null) {
			return true;
		}
		for (ChipAttribute attribute : getAttribute(x, y)) {
			if (!vehicle.stepOn(attribute)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void dispose() {
		backgroundLayerSprite.dispose();
		for (int i = 0, size = fieldMapLayers.size(); i < size; i++) {
			fieldMapLayers.get(i).dispose();
		}
		GameLog.printInfo("フィールドマップが破棄されました name=[" + getName() + "]");
		fieldMapBuilder.free();
	}

	@Override
	public void draw(GraphicsContext g2) {
		if (!isVisible() || !isExist()) {
			return;
		}
		backgroundLayerSprite.draw(g2);
		for (int i = 0, size = fieldMapLayers.size(); i < size; i++) {
			fieldMapLayers.get(i).draw(g2);
		}

		/*/
		 g2.setColor(Color.RED);
		 g2.fillOval((int) (getX() - 2), (int) (getY() - 2), 4, 4);
		 //*/
	}

	public BackgroundLayerSprite getBackgroundLayerSprite() {
		return backgroundLayerSprite;
	}

	public List<FieldMapLayer> getFieldMapLayers() {
		return fieldMapLayers;
	}

	public FieldMapBuilder getFieldMapBuilder() {
		return fieldMapBuilder;
	}

	public SoundMap getSoundMap() {
		return fieldMapBuilder.getSoundMap();
	}

	public NodeMap getNodeMap() {
		return fieldMapBuilder.getNodeMap();
	}

	public int getChipWidth() {
		return fieldMapBuilder.getChipWidth();
	}

	public int getChipHeight() {
		return fieldMapBuilder.getChipHeight();
	}

//----------------------------------------------------------------------------------------------------------------------
	@Override
	public void move() {
		super.move(); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void move(MovingModel m) {
		super.move(m); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean move(Point2D.Float p, Shape s) {
		return super.move(p, s); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean move(float xValue, float yValue, Shape s) {
		return super.move(xValue, yValue, s); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setLocation(Point2D.Float location) {
		super.setLocation(location); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setX(float x) {
		super.setX(x); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setY(float y) {
		super.setY(y); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setSpeed(float speed) {
		super.setSpeed(speed); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setVector(TVector vector) {
		super.setVector(vector); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setBounds(Rectangle2D.Float bounds) {
		super.setBounds(bounds); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setBounds(Point2D.Float location, float width, float height) {
		super.setBounds(location, width, height); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setBounds(float x, float y, float width, float height) {
		super.setBounds(x, y, width, height); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setMovingModel(MovingModel movingModel) {
		super.setMovingModel(movingModel); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible); //To change body of generated methods, choose Tools | Templates.
	}

//----------------------------------------------------------------------------------------------------------------------
	@Override
	public String toString() {
		return "FieldMapLayerList{" + "backgroundLayerSprite=" + backgroundLayerSprite
				+ ", fieldMapLayers=" + fieldMapLayers + '}';
	}

	@Override
	public Iterator<FieldMapLayer> iterator() {
		return fieldMapLayers.iterator();
	}
}
