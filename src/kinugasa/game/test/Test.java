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
import kinugasa.game.field.FieldMapLoader;
import kinugasa.game.field.MapChipAttributeSotrage;
import kinugasa.game.field.MapChipSetStorage;
import kinugasa.game.field.VehicleStorage;
import kinugasa.game.input.GamePadStatusMonitor;
import kinugasa.game.input.InputState;
import kinugasa.game.ui.FPSLabel;

/**
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
		MapChipAttributeSotrage.getInstance().readFromXML("resource/field/data/attr/ChipAttributes.xml");
		VehicleStorage.getInstance().readFromXML("resource/field/data/vehicle/01.xml");
		VehicleStorage.getInstance().setCurrentVehicle("WALK");
		MapChipSetStorage.getInstance().readFromXML("resource/6map/ChipSet.xml");
		
		
		m = new GamePadStatusMonitor();
    }
	private GamePadStatusMonitor m;

    @Override
    protected void dispose() {
    }

    @Override
    protected void update(GameTimeManager gtm) {
		m.update(InputState.getInstance().getGamePadState());
    }

    @Override
    protected void draw(GraphicsContext gc) {
		m.draw(gc);
    }

}
