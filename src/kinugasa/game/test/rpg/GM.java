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
package kinugasa.game.test.rpg;

import java.awt.Color;
import java.awt.Dimension;
import kinugasa.game.GameLogicStorage;
import kinugasa.game.GameManager;
import kinugasa.game.GameOption;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LockUtil;
import kinugasa.game.field4.FieldMapStorage;
import kinugasa.game.field4.MapChipAttributeStorage;
import kinugasa.game.field4.MapChipSetStorage;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.ui.FPSLabel;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.resource.sound.SoundLoader;

/**
 *
 * @vesion 1.0.0 - 2022/11/12_21:36:46<br>
 * @author Dra211<br>
 */
public class GM extends GameManager {

	public static void main(String[] args) {
		LockUtil.deleteAllLockFile();
		new GM().gameStart();
	}

	private GM() {
		super(GameOption.defaultOption().setUseGamePad(true).setBackColor(new Color(0, 32, 66)).setWindowSize(new Dimension(Const.Screen.WIDTH, Const.Screen.HEIGHT)).setCenterOfScreen());
	}
	private FPSLabel fps = new FPSLabel(0, 12);
	private GameLogicStorage gls;

	@Override
	protected void startUp() {
		SoundLoader.loadList("resource/bgm/BGM.csv");
		SoundLoader.loadList("resource/se/SE.csv");

		TextStorageStorage.getInstance().readFromXML("resource/field/data/text/000.xml");
		//--------------------------------------
		MapChipAttributeStorage.getInstance().readFromXML("resource/field/data/attr/ChipAttributes.xml");
		VehicleStorage.getInstance().readFromXML("resource/field/data/vehicle/01.xml");
		VehicleStorage.getInstance().setCurrentVehicle("WALK");
		MapChipSetStorage.getInstance().readFromXML("resource/field/data/chipSet/01.xml");
		MapChipSetStorage.getInstance().readFromXML("resource/field/data/chipSet/02.xml");
		FieldMapStorage.getInstance().readFromXML("resource/field/data/mapBuilder/builder.xml");
		//
		gls = GameLogicStorage.getInstance();
		gls.add(new TitleLogic(this));
		gls.add(new MusicRoomLogic(this));
		gls.add(new GamePadTestLogic(this));
		gls.add(new OPLogic(this));

		gls.changeTo(Const.LogicName.TITLE_LOGIC);
		//
	}

	@Override
	protected void dispose() {
		gls.getCurrent().dispose();
	}

	@Override
	protected void update(GameTimeManager gtm) {
		fps.setGtm(gtm);
		gls.getCurrent().update(gtm);
	}

	@Override
	protected void draw(GraphicsContext gc) {
		fps.draw(gc);
		gls.getCurrent().draw(gc);

	}

}
