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
package kinugasa.game.test.rpg;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.field.FieldMap;
import kinugasa.game.field.FieldMapLoader;
import kinugasa.game.field.FieldMapNode;
import kinugasa.game.field.FieldMapNode.Mode;
import kinugasa.game.field.FieldMapStorage;
import kinugasa.game.field.PlayerCharacter;
import kinugasa.game.field.VehicleStorage;
import kinugasa.game.input.GamePadState;
import kinugasa.game.input.InputState;
import kinugasa.game.ui.FPSLabel;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.MessageWindow;
import kinugasa.graphics.FadeCounter;
import kinugasa.object.BasicSprite;
import kinugasa.object.KVector;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_17:23:22<br>
 * @author Dra211<br>
 */
public class FieldLogic extends GameLogic {

	public FieldLogic(String name, GameManager gm) {
		super(name, gm);
	}

	@Override
	public void load() {
		new FieldMapLoader()
				.mapChipAttr("resource/field/data/attr/ChipAttributes.xml")
				.vehicle("resource/field/data/vehicle/01.xml")
				.initialVehicle("WALK")
				.mapChipSet("resource/field/data/chipSet/01.xml")
				.mapChipSet("resource/field/data/chipSet/02.xml")
				.fieldMapBuilder("resource/field/data/mapBuilder/builder.xml")
				.sound("01", "resource/フィールド.wav")
				.sound("SE", "resource/sound/effect/door1.wav")
				.charPoint(11, 8)
				.debugMode()
				.load();
		fm = FieldMapStorage.getInstance().get("03").load();
		fm.pointUpdate(8, 8);
		cr = new PlayerCharacter(720 / 2 - 16 - 8, 480 / 2 - 16 + 6, 32, 32, "resource/char/pipo-charachip007a.png");
		cr.setVector(new KVector(KVector.SOUTH, 0f));
		cr.update();

		fps = new FPSLabel(678, 14);

		tooltip = new TooltipLabel(580, 28);
		tooltip.setText("ズシ");
	}

	@Override
	public void dispose() {
	}

	private MessageWindow mw;
	private PlayerCharacter cr;
	private FieldMap fm;
	private FPSLabel fps;
	private TooltipLabel tooltip;

	@Override
	public void update(GameTimeManager gtm) {
		GamePadState gs = InputState.getInstance().getGamePadState();
		KVector input = new KVector(gs.sticks.LEFT.getLocation(VehicleStorage.getInstance().getCurrentVehicle().getSpeed()));
		fm.setVector(input.reverse());
		cr.setVector(input);
		fm.update();
		if (fm.canStep(cr)) {
			fm.move();
			if (!gs.sticks.LEFT.isEmptyInput()) {
				cr.update();
			}
		}

		if (fm.canMapChange() && gs.buttons.A) {
			FieldMapNode node = fm.getCurrentNode();
			if (node.getMode() == Mode.INOUT) {
				fm.dispose();
				fm = FieldMapStorage.getInstance().get(node.getExitFieldMapName()).load();
				fm.pointUpdate(
						fm.getNode(node.getExitNodeName()).getX(),
						fm.getNode(node.getExitNodeName()).getY());
				tooltip.setText(node.getTooltip());
				tooltip.reset();
			}
		}
		if (mw != null && mw.isVisible()) {
			mw.update();
			if (mw.isAllVisible() && gs.buttons.isAnyButtonInput()) {
				if (mw.hasNext()) {
					mw.next();
					mw.reset();
				} else {
					mw = null;
				}
			}
		}
		if (fm.getNPC() != null && gs.buttons.A) {
			mw = fm.createWindow();
			mw.reset();
		}
		fps.setGtm(gtm);
	}

	@Override
	public void draw(GraphicsContext g) {
		fm.draw(g);
		cr.draw(g);
		if (mw != null) {
			mw.draw(g);
		}
		fps.draw(g);
		if (tooltip != null) {
			tooltip.draw(g);
		}
	}

}

class TooltipLabel extends BasicSprite {

	public TooltipLabel(int x, int y) {
		super(x, y, 256, 20);
	}

	private FadeCounter fc = new FadeCounter(255, -1);
	private String text;

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(new Color(0, 0, 0, fc.getValue()));
		g2.fillRect((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
		g2.setFont(FontModel.DEFAULT.clone().getFont());
		g2.setColor(new Color(255, 255, 255, fc.getValue()));
		g2.drawString(text, (int) getX() + 3, (int) getY() + getHeight() - 3);
		g2.dispose();
		fc.update();
		if (fc.isEnded()) {
			setVisible(false);
		}
	}

	public void reset() {
		fc = new FadeCounter(255, -1);
		setVisible(true);
	}

}
