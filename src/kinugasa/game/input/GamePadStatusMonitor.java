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
package kinugasa.game.input;

import java.awt.Color;
import java.awt.Graphics2D;
import javax.swing.ImageIcon;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.object.BasicSprite;

/**
 * ゲームパッドの状態を表示するスプライトです。
 *
 * @vesion 1.0.0 - 2021/11/23_8:44:45<br>
 * @author Shinacho<br>
 */
public class GamePadStatusMonitor extends BasicSprite {

	private ImageIcon image = new ImageIcon(getClass().getResource("gamepad.png"));

	public GamePadStatusMonitor() {

	}

	private GamePadState gs;

	public void update(GamePadState gs) {
		this.gs = gs;
		A.setExist(gs.buttons.A);
		B.setExist(gs.buttons.B);
		X.setExist(gs.buttons.X);
		Y.setExist(gs.buttons.Y);

		RB.setExist(gs.buttons.RB);
		LB.setExist(gs.buttons.LB);

		RS.setExist(gs.buttons.RIGHT_STICK);
		LS.setExist(gs.buttons.LEFT_STICK);

		L.setExist(gs.buttons.POV_LEFT);
		R.setExist(gs.buttons.POV_RIGHT);
		U.setExist(gs.buttons.POV_UP);
		D.setExist(gs.buttons.POV_DOWN);

		BACK.setExist(gs.buttons.BACK);
		START.setExist(gs.buttons.START);

		LT.setWidth(gs.triggeres.LEFT.value * 100);
		RT.setWidth(gs.triggeres.RIGHT.value * 100);

		LST.setX(164 + gs.sticks.LEFT.x * 33);
		LST.setY(160 + gs.sticks.LEFT.y * 33);

		RST.setX(380 + gs.sticks.RIGHT.x * 33);
		RST.setY(260 + gs.sticks.RIGHT.y * 33);

	}
	private ButtonSprite A = new ButtonSprite("A", 464, 210, 20);
	private ButtonSprite B = new ButtonSprite("B", 484, 180, 20);
	private ButtonSprite X = new ButtonSprite("X", 444, 180, 20);
	private ButtonSprite Y = new ButtonSprite("Y", 464, 150, 20);

	private ButtonSprite RB = new ButtonSprite("RB", 464, 110, 36);
	private ButtonSprite LB = new ButtonSprite("LB", 124, 110, 36);

	private ButtonSprite RS = new ButtonSprite("LS", 164 - 9, 160, 36);
	private ButtonSprite LS = new ButtonSprite("RS", 380 - 9, 260, 36);

	private ButtonSprite L = new ButtonSprite("←", 194, 270, 20);
	private ButtonSprite R = new ButtonSprite("→", 244, 270, 20);
	private ButtonSprite U = new ButtonSprite("↑", 218, 250, 20);
	private ButtonSprite D = new ButtonSprite("↓", 218, 290, 20);

	private ButtonSprite BACK = new ButtonSprite("Ba", 238, 160, 36);
	private ButtonSprite START = new ButtonSprite("St", 368, 160, 36);

	private TrrigerSprite LT = new TrrigerSprite("LT", 85, 75);
	private TrrigerSprite RT = new TrrigerSprite("RT", 430, 75);

	private StickSprite LST = new StickSprite("L", 164, 160, 20);
	private StickSprite RST = new StickSprite("R", 380, 260, 20);

	@Override
	public void draw(GraphicsContext g) {
		g.drawImage(image.getImage(), 0, 0);

		A.draw(g);
		B.draw(g);
		X.draw(g);
		Y.draw(g);
		RB.draw(g);
		LB.draw(g);

		RS.draw(g);
		LS.draw(g);

		R.draw(g);
		L.draw(g);
		U.draw(g);
		D.draw(g);

		BACK.draw(g);
		START.draw(g);

		LT.draw(g);
		RT.draw(g);

		LST.draw(g);
		RST.draw(g);

	}

}

class ButtonSprite extends TextLabelSprite {

	private static final TextLabelModel MODEL = new SimpleTextLabelModel("DEFAULT", FontModel.DEFAULT);

	public ButtonSprite(String msg, float x, float y, int w) {
		super(msg, MODEL, x, y, w, 18);
	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(Color.BLACK);
		if (super.isExist()) {
			g2.fillRect((int) getX() - 4, (int) getY(), (int) getWidth(), (int) getHeight() + 4);
		} else {
			g2.drawRect((int) getX() - 4, (int) getY(), (int) getWidth(), (int) getHeight() + 4);
		}
		super.draw(g); //To change body of generated methods, choose Tools | Templates.
		g2.dispose();
	}

}

class TrrigerSprite extends TextLabelSprite {

	private static final TextLabelModel MODEL = new SimpleTextLabelModel("DEFAULT", FontModel.DEFAULT);

	public TrrigerSprite(String msg, float x, float y) {
		super(msg, MODEL, x, y, 0, 18);
	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(Color.BLACK);
		g2.drawRect((int) getX() - 4, (int) getY(), 100, (int) getHeight() + 4);
		if (super.isExist()) {
			g2.fillRect((int) getX() - 4, (int) getY(), (int) getWidth(), (int) getHeight() + 4);
		} else {
			g2.drawRect((int) getX() - 4, (int) getY(), (int) getWidth(), (int) getHeight() + 4);
		}
		super.draw(g); //To change body of generated methods, choose Tools | Templates.
		g2.dispose();
	}

}

class StickSprite extends TextLabelSprite {

	private static final TextLabelModel MODEL = new SimpleTextLabelModel("DEFAULT", FontModel.DEFAULT);

	public StickSprite(String msg, float x, float y, int w) {
		super(msg, MODEL, x, y, w, 18);
	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(Color.BLACK);
		if (super.isExist()) {
			g2.fillRect((int) getX() - 4, (int) getY(), (int) getWidth(), (int) getHeight() + 4);
		} else {
			g2.drawRect((int) getX() - 4, (int) getY(), (int) getWidth(), (int) getHeight() + 4);
		}
		super.draw(g); //To change body of generated methods, choose Tools | Templates.
		g2.dispose();
	}

}
