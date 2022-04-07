/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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
package hexwarsim.fieldmap;

import hexwarsim.game.GameConstants;
import kinugasa.fieldObject.ChipAttribute;
import kinugasa.game.GameLog;
import kinugasa.fieldObject.ChipAttributeStorage;
import kinugasa.fieldObject.ChipSet;
import kinugasa.fieldObject.ChipSetStorage;
import kinugasa.fieldObject.MapChip;
import kinugasa.util.StopWatch;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/10/04<br>
 * @author Dra<br>
 * <br>
 */
public final class HexFieldMapInitializer {

	private HexFieldMapInitializer() {
	}
	private static final HexFieldMapInitializer INSTANCE = new HexFieldMapInitializer();

	public static final HexFieldMapInitializer getInstance() {
		return INSTANCE;
	}
	private boolean init = false;

	public void init(String dirPath) {
		if (init) {
			HexFieldMapStorage.getInstance().clear();
			HexFieldMapStorage.getInstance().load();
			System.out.println("読み込まれたマップファイル：");
			HexFieldMapStorage.getInstance().printAll(System.out, true);
			return;
		}
		GameLog.printInfo("FieldMapリストの初期ロードを開始・・・");
		StopWatch watch = new StopWatch().start();

		ChipAttributeStorage.getInstance().readFromXML(dirPath + "ChipAttribute.xml");
		if (!ChipAttributeStorage.getInstance().contains(GameConstants.CHIP_ATTR_HQ)) {
			ChipAttributeStorage.getInstance().add(new ChipAttribute(GameConstants.CHIP_ATTR_HQ));
		}
		ChipSetStorage.getInstance().readFromXML(dirPath + "ChipSet.xml");
		boolean result = false;
		for (ChipSet chipSet : ChipSetStorage.getInstance()) {
			for (MapChip chip : chipSet) {
				if (GameConstants.CHIP_ATTR_HQ.equals(chip.getAttribute().getName())) {
					result = true;
					break;
				}
			}
		}
		if (!result) {
			throw new RuntimeException("チップセットに属性が「" + GameConstants.CHIP_ATTR_HQ + "」のチップが含まれて居ません");
		}

		HexFieldMapStorage.getInstance().load();
		System.out.println("読み込まれたマップファイル：");
		HexFieldMapStorage.getInstance().printAll(System.out, true);

		watch.stop();
		GameLog.printInfo("FieldMapリストのロードが完了 (" + watch.getTime() + "ms)");
		init = true;
	}

}
