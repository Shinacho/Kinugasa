/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Storage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:31:55<br>
 * @author Shinacho<br>
 */
public class StatusDamageCalcModelStorage extends Storage<StatusDamageCalcModel> {

	private static final StatusDamageCalcModelStorage INSTANCE = new StatusDamageCalcModelStorage();

	private StatusDamageCalcModelStorage() {
		add(new StatusDamageCalcModel("DUMMY") {
			@Override
			public ActionEventResult exec(BattleCharacter user, ActionEvent ba, BattleCharacter tgt) {
				System.out.println("ダメージ計算が実行された");
				//装備アイテムの属性、ダメージ計算に使うステータスキーを参照しなければならない。
				return new ActionEventResult(ActionResultType.SUCCESS, new AnimationSprite(new Animation(new FrameTimeCounter(20), new SpriteSheet("resource/field/image/fieldChip16.png").rows(0, 16, 16).images())));
			}
		});
		//TODO DCT=DIRECTかつATTR=noneAttrKeyの場合耐性を無視してダメージを与える
		setCurrent("DUMMY");
	}

	public static StatusDamageCalcModelStorage getInstance() {
		return INSTANCE;
	}
	private StatusDamageCalcModel current;

	public void setCurrent(String name) {
		this.current = get(name);
	}

	public StatusDamageCalcModel getCurrent() {
		return current;
	}

	private static String noneAttrKey = "NONE";

	public static String getNoneAttrKey() {
		return noneAttrKey;
	}

	public static void setNoneAttrKey(String noneAttrKey) {
		StatusDamageCalcModelStorage.noneAttrKey = noneAttrKey;
	}

}
