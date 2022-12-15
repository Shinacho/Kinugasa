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
import java.awt.geom.Point2D;
import java.util.List;
import kinugasa.game.GraphicsContext;

/**
 *
 * @vesion 1.0.0 - 2022/12/15_11:55:51<br>
 * @author Dra211<br>
 */
public class DebugLayerSprite {

	public static void debugDrawPC(GraphicsContext g, List<PlayerCharacterSprite> pcList, D2Idx idx, D2Idx plDir, Point2D.Float base, int chipW, int chipH) {
		float drawX = base.x + (idx.x * chipW);
		float drawY = base.y + (idx.y * chipH);
		Graphics2D g2 = g.create();
		g2.setColor(Color.CYAN);
		g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
		g2.setColor(Color.LIGHT_GRAY);

		drawX = base.x + (plDir.x * chipW);
		drawY = base.y + (plDir.y * chipH);
		g2.setColor(Color.LIGHT_GRAY);
		g2.drawRect((int) drawX, (int) drawY, chipW, chipH);

		int i = 1;
		for (PlayerCharacterSprite c : pcList) {
			g2.setColor(Color.BLUE);
			D2Idx tgt = c.getTargetIdx();
			if (tgt != null) {
				drawX = base.x + (c.getTargetIdx().x * chipW);
				drawY = base.y + (c.getTargetIdx().y * chipH);
				g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
				g2.drawString("PC" + i++, (int) drawX, (int) drawY);
			}
		}

		g2.dispose();
	}

	public static void debugDrawNPC(GraphicsContext g, List<NPC> npc, Point2D.Float base, int chipW, int chipH) {
		Graphics2D g2 = g.create();
		for (NPC n : npc) {
			D2Idx idx = n.getCurrentIdx();
			float drawX = base.x + (idx.x * chipW);
			float drawY = base.y + (idx.y * chipH);
			g2.setColor(Color.WHITE);
			g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
			g2.drawString(n.getName(), (int) drawX, (int) drawY);

			g2.setColor(Color.GREEN);
			idx = n.getTargetIdx();
			if (idx == null) {
				g2.dispose();
				return;
			}
			drawX = base.x + (idx.x * chipW);
			drawY = base.y + (idx.y * chipH);
			g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
			g2.drawString(n.getName() + "_TGT_" + n.getNextMoveFrameTime().getTimeCount(), (int) drawX, (int) drawY);

			g2.setColor(Color.PINK);
			idx = n.getInitialIdx();
			if (idx == null) {
				g2.dispose();
				return;
			}
			drawX = base.x + (idx.x * chipW);
			drawY = base.y + (idx.y * chipH);
			g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
			g2.drawString(n.getName() + "_INI", (int) drawX, (int) drawY);

			idx = n.getMoveModel().getMin(n);
			drawX = base.x + (idx.x * chipW);
			drawY = base.y + (idx.y * chipH);
			D2Idx idx2 = idx.clone();
			float drawX2, drawY2;
			idx = n.getMoveModel().getMax(n);
			drawX2 = ((idx.x - idx2.x) * chipW) + chipW;
			drawY2 = ((idx.y - idx2.y) * chipH) + chipH;
			g2.drawRect((int) drawX, (int) drawY, (int) drawX2, (int) drawY2);
			g2.drawString(n.getName() + "_MAX", (int) drawX, (int) drawY);

		}
		g2.dispose();

	}

	public static void debugDrawCamera(GraphicsContext g, Point2D.Float base, int chipW, int chipH) {

		Graphics2D g2 = g.create();
		D2Idx idx = FieldMapCamera.getInstance().getCurrentCenter();
		if (idx == null) {
			return;
		}
		float drawX = base.x + (idx.x * chipW);
		float drawY = base.y + (idx.y * chipH);
		g2.setColor(Color.RED);
		g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
		g2.drawString("CC", drawX, drawY);

		g2.setColor(Color.ORANGE);
		idx = FieldMapCamera.getInstance().getTargetIdx();
		if (idx == null) {
			return;
		}
		drawX = base.x + (idx.x * chipW);
		drawY = base.y + (idx.y * chipH);
		g2.drawRect((int) drawX, (int) drawY, chipW, chipH);
		g2.drawString("TG", drawX, drawY);

		g2.dispose();
	}

}
