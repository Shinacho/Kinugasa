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

import java.awt.geom.Point2D;
import kinugasa.object.KVector;
import kinugasa.object.BasicSprite;

/**
 * フィールドマップの視点をカプセル化するクラスです。 このクラスにより、フィールドマップの各レイヤーが移動されます。
 * ただし、ビフォアレイヤーは移動されません。
 *
 * @vesion 1.0.0 - 2022/11/10_16:43:45<br>
 * @author Dra211<br>
 */
public class FieldMapCamera {

	private FieldMap map;
	private FieldMapCameraMode mode = FieldMapCameraMode.FOLLOW_TO_CENTER;
	private final D2Idx playerLocation;

	public FieldMapCamera(FieldMap map) {
		this.map = map;
		// 画面サイズとチップサイズからキャラクタ表示インデックスを計算
		int chipW = map.getChipW();
		int chipH = map.getChipH();
		int screenW = FieldMapStorage.getScreenWidth();
		int screenH = FieldMapStorage.getScreenHeight();
		int x = (int) ((float) (screenW / 2 / chipW));
		int y = (int) ((float) (screenH / 2 / chipH));
		playerLocation = new D2Idx(x, y);
		System.out.println("FM_CAMERA, playerLocation is : " + playerLocation);
	}

	public FieldMapCameraMode getMode() {
		return mode;
	}

	public void setMode(FieldMapCameraMode mode) {
		this.mode = mode;
		if (mode == FieldMapCameraMode.FOLLOW_TO_CENTER) {
			updateToCenter();
		}
	}

	public void move() {
		//追従モードで次のチップが乗れない場合移動しない
		if (mode == FieldMapCameraMode.FOLLOW_TO_CENTER) {
			//次フレームの座標取得
			int chipW = map.getChipW();
			int chipH = map.getChipH();
			BasicSprite base = map.getBaseLayer();
			float fieldMapX = ((-base.getX() + (chipW / 2) - (chipW / 4) + base.getVector().reverse().getLocation().x)) / chipW;
			float fieldMapY = ((-base.getY() + (chipH / 2) + base.getVector().reverse().getLocation().y)) / chipH;
			int x = (int) (playerLocation.x + fieldMapX);
			int y = (int) (playerLocation.y + fieldMapY);

			//領域外の判定
			if (x < 1 || y < 1) {
				return;
			}
			if (map.getBaseLayer().getDataWidth() <= x + 1 || map.getBaseLayer().getDataHeight() <= y + 1) {
				return;
			}

			//乗れるチップかの判定
			if (!VehicleStorage.getInstance().getCurrentVehicle().isStepOn(map.getTile(new D2Idx(x, y)).getChip())) {
				return;
			}
		}

		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().move();
		}
		map.getBacklLayeres().forEach(e -> e.move());
		if (mode != FieldMapCameraMode.FOLLOW_TO_CENTER) {
			//追従モードじゃない場合は同じベクトルで移動
			FieldMap.getPlayerCharacter().move();
		}
		map.getCharacter().forEach(e -> e.move());
		map.getFrontlLayeres().forEach(e -> e.move());
		map.getFrontAnimation().forEach(e -> e.move());
		//移動後の座標再計算
		switch (mode) {
			case FOLLOW_TO_CENTER:
				//追従モードの場合、キャラクタの座標を再計算する
				//プレイヤーキャラクター（中心）IDX更新
				int chipW = map.getChipW();
				int chipH = map.getChipH();
				BasicSprite base = map.getBaseLayer();
				float fieldMapX = ((-base.getX() + (chipW / 2) - (chipW / 4) + base.getVector().reverse().getLocation().x)) / chipW;
				float fieldMapY = ((-base.getY() + (chipH / 2) + base.getVector().reverse().getLocation().y)) / chipH;
				int x = (int) (playerLocation.x + fieldMapX);
				int y = (int) (playerLocation.y + fieldMapY);
				//領域外の判定
				if (x < 1 || y < 1) {
					return;
				}
				if (map.getBaseLayer().getDataWidth() <= x + 1 || map.getBaseLayer().getDataHeight() <= y + 1) {
					return;
				}
				//乗れるチップかの判定
				if (!VehicleStorage.getInstance().getCurrentVehicle().isStepOn(map.getTile(new D2Idx(x, y)).getChip())) {
					return;
				}
				map.setCurrentIdx(new D2Idx(x, y));
				break;
			case FREE:
				// フリーモードの場合カメラのみをを動かし、何もしない
				break;
		}
	}

	public void setSpeed(float speed) {
		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setSpeed(speed);
		}
		map.getBacklLayeres().forEach(e -> e.setSpeed(speed));
		map.getCharacter().forEach(e -> e.setSpeed(speed));
		FieldMap.getPlayerCharacter().setSpeed(speed);
		map.getFrontlLayeres().forEach(e -> e.setSpeed(speed));
		map.getFrontAnimation().forEach(e -> e.setSpeed(speed));
	}

	public void setAngle(float angle) {
		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setAngle(angle);
		}
		map.getBacklLayeres().forEach(e -> e.setAngle(angle));
		map.getCharacter().forEach(e -> e.setAngle(angle));
		FieldMap.getPlayerCharacter().setAngle(angle);
		map.getFrontlLayeres().forEach(e -> e.setAngle(angle));
		map.getFrontAnimation().forEach(e -> e.setAngle(angle));
	}

	public void setVector(KVector v) {
		setSpeed(v.getSpeed());
		setAngle(v.getAngle());
	}

	public void setLocation(float x, float y) {
		setLocation(new Point2D.Float(x, y));
	}

	public void setLocation(Point2D.Float p) {
		int chipW = map.getChipW();
		int chipH = map.getChipH();

		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setLocation(p);
		}
		map.getBacklLayeres().forEach(e -> e.setLocation(p));
		map.getCharacter().forEach(e -> e.setLocation(p));
		FieldMap.getPlayerCharacter().setLocation(p);
		map.getFrontlLayeres().forEach(e -> e.setLocation(p));
		float fieldMapX = map.getBaseLayer().getX();
		float fieldMapY = map.getBaseLayer().getY();
		for (FieldAnimationSprite s : map.getFrontAnimation()) {
			float xx = fieldMapX + (s.getIdx().x * chipW);
			float yy = fieldMapY + (s.getIdx().y * chipH);
			s.setLocation(xx, yy);
		}
	}

	public void setX(float x) {
		int chipW = map.getChipW();

		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setX(x);
		}
		map.getBacklLayeres().forEach(e -> e.setX(x));
		map.getCharacter().forEach(e -> e.setX(x));
		FieldMap.getPlayerCharacter().setX(x);
		map.getFrontlLayeres().forEach(e -> e.setX(x));
		float fieldMapX = map.getBaseLayer().getX();
		for (FieldAnimationSprite s : map.getFrontAnimation()) {
			float xx = fieldMapX + (s.getIdx().x * chipW);
			s.setX(xx);
		}
	}

	public void setY(float y) {
		int chipH = map.getChipH();

		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setY(y);
		}
		map.getBacklLayeres().forEach(e -> e.setY(y));
		map.getCharacter().forEach(e -> e.setY(y));
		FieldMap.getPlayerCharacter().setY(y);
		map.getFrontlLayeres().forEach(e -> e.setY(y));
		float fieldMapY = map.getBaseLayer().getY();
		for (FieldAnimationSprite s : map.getFrontAnimation()) {
			float yy = fieldMapY + (s.getIdx().y * chipH);
			s.setY(yy);
		}
	}

	/**
	 * フィールドマップのカレントIDXを中心に表示するよう、カメラ位置を更新します。
	 */
	public void updateToCenter() {
		D2Idx currentIdx = map.getCurrentIdx();
		int chipW = map.getChipW();
		int chipH = map.getChipH();
		int screenW = FieldMapStorage.getScreenWidth();
		int screenH = FieldMapStorage.getScreenHeight();
		//表示位置＝中心-画面サイズ
		int x = currentIdx.x * chipW - (screenW / 2);
		int y = currentIdx.y * chipH - (screenH / 2);
		x += chipW / 2;
		y += chipH / 2;
		setLocation(-x, -y);
		//キャラクタの位置修正
		int charaW = FieldMap.getPlayerCharacter().getImageWidth();
		int charaH =  FieldMap.getPlayerCharacter().getImageHeight();

		float cx = screenW / 2 - (charaW / 2);
		float cy = screenH / 2 - (charaH / 2);
		 FieldMap.getPlayerCharacter().setLocation(cx, cy);
	}

}
