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
import kinugasa.game.GraphicsContext;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FourDirection;

/**
 *
 * @vesion 1.0.0 - 2022/11/10_20:35:58<br>
 * @author Dra211<br>
 */
public class FieldMapCharacter extends AnimationSprite {

	private final D2Idx initialIdx;
	private FourDirection currentDir;
	private FourDirAnimation fAnimation;

	public FieldMapCharacter(float x, float y, float w, float h, D2Idx initialLocation, FourDirAnimation a, FourDirection initialDir) {
		super(x, y, w, h, a.get(initialDir));
		this.initialIdx = initialLocation;
		this.fAnimation = a;
		this.currentDir = initialDir;
	}

	public D2Idx getInitialIdx() {
		return initialIdx;
	}

	public FourDirection getCurrentDir() {
		return currentDir;
	}

	public FourDirAnimation getFourDirAnimation() {
		return fAnimation;
	}

	public void to(FourDirection dir) {
		setAnimation(fAnimation.get(dir));
	}

	@Override
	public void draw(GraphicsContext g) {
		super.draw(g); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
		Graphics2D g2 = g.create();
		g2.setColor(SHADOW);
		g2.fillOval((int) (getX() + getWidth() / 8), (int) (getY() + getHeight() - getHeight() / 16), (int) (getWidth() - getWidth() / 4), (int) (getHeight() / 8));
		g2.dispose();
	}
	private static final Color SHADOW = new Color(0, 0, 0, 128);

}
