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
import java.util.List;
import kinugasa.game.ui.Text;
import kinugasa.graphics.Animation;
import kinugasa.object.ImageSprite;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/30_8:55:59<br>
 * @author Dra211<br>
 */
public class NPC extends ImageSprite {

	private Text text;
	//
	private Point initialP;
	private Point currentP;
	private Point targetP;
	//
	private TimeCounter movingCounter;
	//
	private List<Animation> anime;
	private FourDirection dir = FourDirection.NORTH;
	private int currentAnimationIndex = dir.getIdx();
	//
	private Vehicle vehicle;

	
	
	
	
	
	public Text getText() {
		return text;
	}

	public void setText(Text text) {
		this.text = text;
	}

	public Point getInitialP() {
		return initialP;
	}

	public void setInitialP(Point initialP) {
		this.initialP = initialP;
	}

	public Point getCurrentP() {
		return currentP;
	}

	public void setCurrentP(Point currentP) {
		this.currentP = currentP;
	}

	public Point getTargetP() {
		return targetP;
	}

	public void setTargetP(Point targetP) {
		this.targetP = targetP;
	}

	public TimeCounter getMovingCounter() {
		return movingCounter;
	}

	public void setMovingCounter(TimeCounter movingCounter) {
		this.movingCounter = movingCounter;
	}

	public List<Animation> getAnime() {
		return anime;
	}

	public void setAnime(List<Animation> anime) {
		this.anime = anime;
	}

	public FourDirection getDir() {
		return dir;
	}

	public void setDir(FourDirection dir) {
		this.dir = dir;
	}

	public int getCurrentAnimationIndex() {
		return currentAnimationIndex;
	}

	public void setCurrentAnimationIndex(int currentAnimationIndex) {
		this.currentAnimationIndex = currentAnimationIndex;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public void setVehicle(Vehicle vehicle) {
		this.vehicle = vehicle;
	}

}
