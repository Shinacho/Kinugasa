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
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.TVector;
import kinugasa.resource.Disposable;

/**
 * フィールドマップに表示される1つのレイヤで、様々なスプライトを表示する階層と マップデータをアニメーション表示する階層を持ちます.
 * <br>
 * <br>
 *
 * @version 1.0.0 - 2013/05/04_23:04:38<br>
 * @author Dra0211<br>
 */
public class FieldMapLayer extends BasicSprite implements Disposable {

	private static final long serialVersionUID = -8112646736891449853L;
	private ObjectLayerSprite objectLayerSprite;
	private MapLayerSprite mapLayerSprite;

	public FieldMapLayer(float z, float speed,
			ObjectLayerSprite objectLayerSprite,
			MapLayerSprite mapLayerSprite) {
		setZ(z);
		setVector(new TVector(speed));
		this.objectLayerSprite = objectLayerSprite;
		this.mapLayerSprite = mapLayerSprite;
	}

	@Override
	public void dispose() {
		objectLayerSprite.dispose();
		mapLayerSprite.dispose();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		//この時点でbackgroundLayerSpriteはLayerListによって描画されており、
		//LayerListはList<FieldMapLayer>をループで順に描画している
		mapLayerSprite.draw(g);
		objectLayerSprite.draw(g);
	}

	public ObjectLayerSprite getObjectLayerSprite() {
		return objectLayerSprite;
	}

	public MapLayerSprite getMapLayerSprite() {
		return mapLayerSprite;
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
		return "FieldMapLayer{" + "objectLayerSprite=" + objectLayerSprite
				+ ", mapLayerSprite=" + mapLayerSprite + '}';
	}
}
