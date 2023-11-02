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
package kinugasa.game.field4;

import kinugasa.game.system.NPCSprite;
import kinugasa.game.system.PCSprite;
import java.awt.geom.Point2D;
import kinugasa.game.GameOption;
import kinugasa.game.system.GameSystem;
import kinugasa.object.KVector;
import kinugasa.object.BasicSprite;

/**
 * フィールドマップの視点をカプセル化するクラスです。 このクラスにより、フィールドマップの各レイヤーが移動されます。
 * ただし、ビフォアレイヤーは移動されません。
 *
 * @vesion 1.0.0 - 2022/11/10_16:43:45<br>
 * @author Shinacho<br>
 */
public class FieldMapCamera {

	private FieldMap map;
	private FieldMapCameraMode mode = FieldMapCameraMode.FOLLOW_TO_CENTER;
	private final D2Idx playerLocationBuf;
	private D2Idx currentCenter;
	private D2Idx targetIdx;

	public FieldMapCamera(FieldMap map) {
		this.map = map;
		// 画面サイズとチップサイズからキャラクタ表示インデックスを計算
		int chipW = map.getChipW();
		int chipH = map.getChipH();
		int screenW = (int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize());
		int screenH = (int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize());
		int x = (int) ((float) (screenW / 2 / chipW));
		int y = (int) ((float) (screenH / 2 / chipH));
		playerLocationBuf = new D2Idx(x, y);
		currentCenter = playerLocationBuf.clone();
		INSTANCE = this;
	}
	private static FieldMapCamera INSTANCE;

	static FieldMapCamera getInstance() {
		return INSTANCE;
	}

	D2Idx getCurrentCenter() {
		return currentCenter;
	}

	void setTargetIdx(D2Idx idx, float speed) {
		this.targetIdx = idx;
		//currentからtgtへの角度算出
		KVector v = new KVector(currentCenter.asPoint2D(), targetIdx.asPoint2D());
		v.setSpeed(speed);
		map.setVector(v);
		mode = FieldMapCameraMode.FREE;
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

	public static String cameraCantMoveDesc;

	public void move() {
		//追従モードで次のチップが乗れない場合移動しない
		if (mode == FieldMapCameraMode.FOLLOW_TO_CENTER) {
			//次フレームの座標取得
			int chipW = map.getChipW();
			int chipH = map.getChipH();
			BasicSprite base = map.getBaseLayer();
			float fieldMapX = ((-base.getX() + (chipW / 2) - (chipW / 4) + base.getVector().reverse().getLocation().x)) / chipW;
			float fieldMapY = ((-base.getY() + (chipH / 2) + (chipH / 4) + base.getVector().reverse().getLocation().y)) / chipH;
			int x = (int) (playerLocationBuf.x + fieldMapX);
			int y = (int) (playerLocationBuf.y + fieldMapY);

			//領域外の判定
			if (x < 1 || y < 1) {
				cameraCantMoveDesc = "[1]x < 1 || y < 1";
				return;
			}
			if (map.getBaseLayer().getDataWidth() <= x + 1 || map.getBaseLayer().getDataHeight() <= y + 1) {
				cameraCantMoveDesc = "[1]x > dataWidth || y > dataHeight";
				return;
			}

			//NPC衝突判定
			if (map.getNpcStorage().get(new D2Idx(x, y)) != null) {
				cameraCantMoveDesc = "[1]NPC hit[" + map.getNpcStorage().get(new D2Idx(x, y)) + "]";
				return;
			}

			//乗れるチップかの判定
			if (!VehicleStorage.getInstance().getCurrentVehicle().isStepOn(map.getTile(new D2Idx(x, y)).getChip())) {
				cameraCantMoveDesc = "[1]cant step[" + map.getTile(new D2Idx(x, y)).getChip() + "]";
				return;
			}
		}
		cameraCantMoveDesc = null;

		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().move();
		}
		map.getBacklLayeres().forEach(e -> e.move());
		if (mode != FieldMapCameraMode.FOLLOW_TO_CENTER) {
			//追従モードじゃない場合は同じベクトルで移動
			FieldMap.getPlayerCharacter().get(0).move();

		} else {
			if (FieldMap.getPlayerCharacter().size() > 1) {
				FieldMap.getPlayerCharacter().subList(1, FieldMap.getPlayerCharacter().size()).forEach(p -> p.move());
			}
		}

		map.getNpcStorage().forEach(e -> e.move());
		map.getFrontlLayeres().forEach(e -> e.move());
		map.getFrontAnimation().forEach(e -> e.move());
		//移動後の座標再計算
		//NPCの位置更新
		int chipW = map.getChipW();
		int chipH = map.getChipH();
//		for (NPCSprite n : map.getNpcStorage()) {
//			float nx = map.getBaseLayer().getX() + n.getCurrentIdx().x * chipW;
//			float ny = map.getBaseLayer().getY() + n.getCurrentIdx().y * chipH;
//			n.setLocation(nx, ny);
//		}
		BasicSprite base = map.getBaseLayer();
		float fieldMapX = ((-base.getX() + (chipW / 2) - (chipW / 4) + base.getVector().reverse().getLocation().x)) / chipW;
		float fieldMapY = ((-base.getY() + (chipH / 2) + (chipH / 4) + base.getVector().reverse().getLocation().y)) / chipH;
		int x = (int) (playerLocationBuf.x + fieldMapX);
		int y = (int) (playerLocationBuf.y + fieldMapY);
		currentCenter = new D2Idx(x, y);
		switch (mode) {
			case FOLLOW_TO_CENTER:
				//追従モードの場合、キャラクタの座標を再計算する
				//プレイヤーキャラクター（中心）IDX更新

				//領域外の判定
				if (x < 1 || y < 1) {
					cameraCantMoveDesc = "[2]x < 1 || y < 1";
					return;
				}
				if (map.getBaseLayer().getDataWidth() <= x + 1 || map.getBaseLayer().getDataHeight() <= y + 1) {
					cameraCantMoveDesc = "[2]x > dataWidth || y > dataHeight";
					return;
				}
				//NPC衝突判定
				if (map.getNpcStorage().get(new D2Idx(x, y)) != null) {
					cameraCantMoveDesc = "[2]NPC hit[" + map.getNpcStorage().get(new D2Idx(x, y)) + "]";
					return;
				}

				//乗れるチップかの判定
				if (!VehicleStorage.getInstance().getCurrentVehicle().isStepOn(map.getTile(new D2Idx(x, y)).getChip())) {
					cameraCantMoveDesc = "[2]cant step[" + map.getTile(new D2Idx(x, y)).getChip() + "]";
					return;
				}
				cameraCantMoveDesc = null;
				map.setCurrentIdx(new D2Idx(x, y));
				break;
			case FREE:
				// フリーモードの場合カメラのみをを動かし、何もしない
				if (currentCenter.equals(targetIdx)) {
					//オートムーブ終了
					targetIdx = null;
					map.setVector(new KVector(0, 0));
				}
				break;
		}
	}

	public boolean hasTarget() {
		return targetIdx != null;
	}

	D2Idx getTargetIdx() {
		return targetIdx;
	}

	public void setSpeed(float speed) {
		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setSpeed(speed);
		}
		map.getBacklLayeres().forEach(e -> e.setSpeed(speed));
		map.getNpcStorage().forEach(e -> e.setSpeed(speed));
		FieldMap.getPlayerCharacter().forEach(v -> v.setSpeed(speed));
		map.getFrontlLayeres().forEach(e -> e.setSpeed(speed));
		map.getFrontAnimation().forEach(e -> e.setSpeed(speed));
	}

	public void setAngle(float angle) {
		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().setAngle(angle);
		}
		map.getBacklLayeres().forEach(e -> e.setAngle(angle));
		map.getNpcStorage().forEach(e -> e.setAngle(angle));
		FieldMap.getPlayerCharacter().forEach(v -> v.setAngle(angle));
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
		map.getNpcStorage().forEach(e -> e.setLocation(p));
		FieldMap.getPlayerCharacter().forEach(v -> v.setLocation(p));
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
		map.getNpcStorage().forEach(e -> e.setX(x));
		FieldMap.getPlayerCharacter().get(0).setX(x);
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
		map.getNpcStorage().forEach(e -> e.setY(y));
		FieldMap.getPlayerCharacter().get(0).setY(y);
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
		int screenW = (int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize());
		int screenH = (int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize());
		//表示位置＝中心-画面サイズ
		int x = currentIdx.x * chipW - (screenW / 2);
		int y = currentIdx.y * chipH - (screenH / 2);
		x += chipW / 2;
		y += chipH / 2;
		setLocation(-x, -y);
		//キャラクタの位置修正
		int charaW = FieldMap.getPlayerCharacter().get(0).getImageWidth();
		int charaH = FieldMap.getPlayerCharacter().get(0).getImageHeight();

		float cx = screenW / 2 - (charaW / 2);
		float cy = screenH / 2 - (charaH / 2);
		FieldMap.getPlayerCharacter().forEach(v -> v.setLocation(cx, cy));

		//NPCの位置更新
		for (NPCSprite n : map.getNpcStorage()) {
			float nx = map.getBaseLayer().getX() + n.getCurrentIdx().x * chipW;
			float ny = map.getBaseLayer().getY() + n.getCurrentIdx().y * chipH;
			n.setLocation(nx, ny);
		}
		//PCの位置更新
		for (PCSprite c : FieldMap.getPlayerCharacter().subList(1, FieldMap.getPlayerCharacter().size())) {
			if (c.getCurrentIdx() != null) {
				float nx = map.getBaseLayer().getX() + c.getCurrentIdx().x * chipW;
				float ny = map.getBaseLayer().getY() + c.getCurrentIdx().y * chipH;
				c.setLocation(nx, ny);
			}
		}
		currentCenter = map.getCurrentIdx();

	}

	//PCがいないマップはこちらを使用する
	public void updateToCenterNoPC() {
		D2Idx currentIdx = map.getCurrentIdx();
		int chipW = map.getChipW();
		int chipH = map.getChipH();
		int screenW = (int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize());
		int screenH = (int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize());
		//表示位置＝中心-画面サイズ
		int x = currentIdx.x * chipW - (screenW / 2);
		int y = currentIdx.y * chipH - (screenH / 2);
		x += chipW / 2;
		y += chipH / 2;
		setLocation(-x, -y);

		//NPCの位置更新
		for (NPCSprite n : map.getNpcStorage()) {
			float nx = map.getBaseLayer().getX() + n.getCurrentIdx().x * chipW;
			float ny = map.getBaseLayer().getY() + n.getCurrentIdx().y * chipH;
			n.setLocation(nx, ny);
		}
		currentCenter = map.getCurrentIdx();

	}

}
