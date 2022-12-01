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
package kinugasa.game.system;

import java.util.List;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.Status;
import kinugasa.resource.Storage;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:31:55<br>
 * @author Dra211<br>
 */
public class StatusDamageCalcModelStorage extends Storage<StatusDamageCalcModel> {

	private static final StatusDamageCalcModelStorage INSTANCE = new StatusDamageCalcModelStorage();

	private StatusDamageCalcModelStorage() {
		add(new StatusDamageCalcModel("DUMMY") {
			@Override
			public BattleActionResult exec(GameSystem gs, BattleCharacter user, BattleAction ba, BattleActionEvent e, StatusDamageCalcType calcType, AttributeKey atkAttr, String tgtStatusKey, List<BattleCharacter> target) {
				System.out.println("�_���[�W�v�Z�����s���ꂽ");
				return BattleActionResult.SUCCESS;
			}

		});
		//TODO DCT=DIRECT����ATTR=noneAttrKey�̏ꍇ�ϐ��𖳎����ă_���[�W��^����
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