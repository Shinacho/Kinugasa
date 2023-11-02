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
package kinugasa.game.field4;

import kinugasa.game.system.NPCSprite;
import kinugasa.game.system.PCSprite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.util.List;
import kinugasa.game.GraphicsContext;

/**
 *
 * @vesion 1.0.0 - 2022/12/15_11:55:51<br>
 * @author Shinacho<br>
 */
public class DebugLayerSprite {

	public static void debugDrawPC(GraphicsContext g, List<PCSprite> pcList, D2Idx idx, Point2D.Float base, int chipW, int chipH) {
		float drawX = base.x + (idx.x * chipW);
		float drawY = base.y + (idx.y * chipH);
		Graphics2D g2 = g.create();
		g2.setColor(Color.CYAN);
		g2.drawRect((int) drawX + 1, (int) drawY + 1, chipW - 2, chipH - 2);

		int i = 1;
		for (PCSprite c : pcList) {
			g2.setColor(Color.BLUE);
			D2Idx tgt = c.getTargetIdx();
			if (tgt != null) {
				drawX = base.x + (c.getTargetIdx().x * chipW);
				drawY = base.y + (c.getTargetIdx().y * chipH);
				g2.drawRect((int) drawX + 2, (int) drawY + 2, chipW - 4, chipH - 4);
				g2.drawString("PC" + i++, (int) drawX, (int) drawY);
			}
		}

		g2.dispose();
	}

	public static void debugDrawNPC(GraphicsContext g, List<NPCSprite> npc, Point2D.Float base, int chipW, int chipH) {
		Graphics2D g2 = g.create();
		for (NPCSprite n : npc) {
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
