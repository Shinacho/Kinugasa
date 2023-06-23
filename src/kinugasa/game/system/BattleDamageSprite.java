/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
package kinugasa.game.system;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import kinugasa.game.ui.FontModel;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.object.AnimationSprite;
import kinugasa.object.FourDirection;
import kinugasa.object.ImagePainterStorage;
import kinugasa.object.KVector;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2023/06/15_18:23:45<br>
 * @author Shinacho<br>
 */
public class BattleDamageSprite extends AnimationSprite {

	public BattleDamageSprite(float x, float y, int damage, Color c) {
		super(x, y, 48, 16);
		init(damage, c);
	}

	private void init(int d, Color c) {
		String val = d + "";
		BufferedImage[] images = new BufferedImage[val.length()];

		for (int i = 0; i < images.length; i++) {
			images[i] = ImageUtil.newImage(64, 16);
			Graphics2D g = ImageUtil.createGraphics2D(images[i], RenderingQuality.SPEED);
			String v = StringUtil.toRight(val.substring(val.length() - i - 1), val.length());
			g.setFont(FontModel.DEFAULT.clone().setFontSize(12).getFont());
			g.setColor(Color.BLACK);
			g.drawString(v, 1, 13);
			g.setColor(c);
			g.drawString(v, 0, 12);
			g.dispose();
		}

		Animation a = new Animation(new FrameTimeCounter(4), images);
		a.setRepeat(false);
		setAnimation(a);

		setPainter(ImagePainterStorage.IMAGE_BOUNDS_CENTER);

		setVector(new KVector(FourDirection.NORTH.getAngle(), 0.25f));
	}

	private FrameTimeCounter visibleTime = new FrameTimeCounter(55);

	@Override
	public void update() {
		super.update(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
		move();
		if (getAnimation().isEnded()) {
			if (visibleTime.isReaching()) {
				setVisible(false);
			}
		}
	}

}
