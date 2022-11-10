/*
 * The MIT License
 *
 * Copyright 2021 shin211.
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
package kinugasa.game.test;

import kinugasa.game.GameManager;
import kinugasa.game.GameOption;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LockUtil;
import kinugasa.game.field4.D2Idx;
import kinugasa.game.field4.FieldEventStorage;
import kinugasa.game.field4.FieldEventStorageStorage;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FieldMapStorage;
import kinugasa.game.field4.MapChipAttributeStorage;
import kinugasa.game.field4.MapChipSetStorage;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.input.Keys;
import static kinugasa.game.input.Keys.ENTER;
import kinugasa.game.ui.FPSLabel;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.object.KVector;
import kinugasa.resource.sound.SoundLoader;

/**
 * ゲームのテスト実装です.
 *
 * @author shin211
 */
public class Test extends GameManager {

	public static void main(String... args) {
		LockUtil.deleteAllLockFile();
		new Test().gameStart(args);
	}

	private Test() {
		super(GameOption.defaultOption().setUseGamePad(true).setCenterOfScreen());
	}

	@Override
	protected void startUp() {
		SoundLoader.fileOf("resource/bgm/bgmMap.csv");

		TextStorageStorage.getInstance().readFromXML("resource/field/data/text/000.xml");
		ts = TextStorageStorage.getInstance().get("001").build();

		mw = new MessageWindow(0, 300, 600, 150, new SimpleMessageWindowModel(), ts, ts.get("001"));
		mw.setVisible(true);
		//--------------------------------------
		FieldEventStorageStorage.getInstance().add(new FieldEventStorage("001"));//仮
		//--------------------------------------
		MapChipAttributeStorage.getInstance().readFromXML("resource/field/data/attr/ChipAttributes.xml");
		VehicleStorage.getInstance().readFromXML("resource/field/data/vehicle/01.xml");
		VehicleStorage.getInstance().setCurrentVehicle("WALK");
		MapChipSetStorage.getInstance().readFromXML("resource/field/data/chipSet/01.xml");
		MapChipSetStorage.getInstance().readFromXML("resource/field/data/chipSet/02.xml");
		FieldMapStorage.getInstance().readFromXML("resource/field/data/mapBuilder/builder.xml");
		int w = (int) ((float) (720 / 32 / 2) - 1);
		int h = (int) ((float) (480 / 32 / 2) + 1);
		FieldMap.setPlayerLocation(w, h);
		fm = FieldMapStorage.getInstance().get("01").build();
//		fm.setCurrentIdx(new D2Idx(20, 22));
		fm.setCurrentIdx(new D2Idx(0, 0));

		FieldMap.setDebugMode(true);

	}
	FieldMap fm;
	TextStorage ts;
	MessageWindow mw;
	FPSLabel fps = new FPSLabel(0, 12);

	@Override
	protected void dispose() {
	}

	@Override
	protected void update(GameTimeManager gtm) {
		mw.update();
		fps.setGtm(gtm);
		InputState is = InputState.getInstance();
		if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
			if (mw.isVisible()) {
				if (!mw.isAllVisible()) {
					mw.allText();
				} else if (mw.isChoice()) {
					mw.choicesNext();
				} else if (mw.hasNext()) {
					mw.next();
				} else {
					mw.setVisible(false);
				}
			} else {
				mw.setTextFromId("001");
				mw.reset();
				mw.getTextStorage().resetAll();
				mw.setVisible(true);
			}
		}
		if (mw.isChoice()) {
			if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
				mw.nextSelect();
			}
			if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
				mw.prevSelect();
			}
		}

		float speed = VehicleStorage.getInstance().getCurrentVehicle().getSpeed();
		fm.setVector(new KVector(is.getGamePadState().sticks.LEFT.getLocation(speed)).reverse());
		fm.move();
	}

	@Override
	protected void draw(GraphicsContext gc) {
		fm.draw(gc);
		mw.draw(gc);
		fps.draw(gc);
	}

}
