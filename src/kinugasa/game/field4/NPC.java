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

import java.awt.Point;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.graphics.Animation;
import kinugasa.object.Sprite;
import kinugasa.resource.Nameable;

/**
 * NPCはBasicSpriteでない点に注意してください。
 *
 * @vesion 1.0.0 - 2022/11/08_19:20:50<br>
 * @author Dra211<br>
 */
public class NPC extends FieldMapCharacter implements Nameable {

	private D2Idx targetLocationOnMap;
	private D2Idx currentIDXonMapData;
	private int nextMoveFrameTime;
	//
	private String name;
	private D2Idx initialLocationOnMap;
	private NPCMoveModel moveModel;
	private Vehicle vehicle;
	private FieldMap map;
	private String textId;
	private Animation anime;
	//

	public NPC() {
		super(0, 0, 0, 0, null, null, null);
	}

	//TODO:
	public NPCMoveModel getMoveModel() {
		return moveModel;
	}

	public void setMoveModel(NPCMoveModel moveModel) {
		this.moveModel = moveModel;
	}

	public Vehicle getVehicle() {
		return vehicle;
	}

	public D2Idx getCurrentIDXonMapData() {
		return currentIDXonMapData;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void update() {

//		switch (currentMode) {
//			case mode_stop:
//				//停止状態では、次の移動フレームと移動位置を算出する
//				nextMoveFrameTime--;
//				if (nextMoveFrameTime < 0) {
//					nextMoveFrameTime = moveModel.nextMoveFrameTime(this, map);
//					if (targetLocationOnMap.equals(currentIDXonMapData)) {
//						targetLocationOnMap = moveModel.getNextTargetLocationOnMap(this, map);
//					}
//				}
//				break;
//			case mode_moving:
//				move();
//				break;
//
//		};
	}

	@Override
	public void move() {
		// X方向に移動する必要があるか
		//X軸のTGTとの差分

	}

	@Override
	public void draw(GraphicsContext g) {
	}

	public Text getText(TextStorage ts) {
		return ts.get(textId);
	}

}
