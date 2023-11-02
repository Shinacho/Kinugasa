/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
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
public class DamageAnimationSprite extends AnimationSprite {

	public DamageAnimationSprite(float x, float y, int damage, Color c) {
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
		super.update();
		move();
		if (getAnimation().isEnded()) {
			if (visibleTime.isReaching()) {
				setVisible(false);
			}
		}
	}

}
