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
 * �t�B�[���h�}�b�v�̎��_���J�v�Z��������N���X�ł��B ���̃N���X�ɂ��A�t�B�[���h�}�b�v�̊e���C���[���ړ�����܂��B
 * �������A�r�t�H�A���C���[�͈ړ�����܂���B
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
		// ��ʃT�C�Y�ƃ`�b�v�T�C�Y����L�����N�^�\���C���f�b�N�X���v�Z
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
		//�Ǐ]���[�h�Ŏ��̃`�b�v�����Ȃ��ꍇ�ړ����Ȃ�
		if (mode == FieldMapCameraMode.FOLLOW_TO_CENTER) {
			//���t���[���̍��W�擾
			int chipW = map.getChipW();
			int chipH = map.getChipH();
			BasicSprite base = map.getBaseLayer();
			float fieldMapX = ((-base.getX() + (chipW / 2) - (chipW / 4) + base.getVector().reverse().getLocation().x)) / chipW;
			float fieldMapY = ((-base.getY() + (chipH / 2) + (chipH / 4) + base.getVector().reverse().getLocation().y)) / chipH;
			int x = (int) (playerLocation.x + fieldMapX);
			int y = (int) (playerLocation.y + fieldMapY);

			//�̈�O�̔���
			if (x < 1 || y < 1) {
				return;
			}
			if (map.getBaseLayer().getDataWidth() <= x + 1 || map.getBaseLayer().getDataHeight() <= y + 1) {
				return;
			}

			//NPC�Փ˔���
			if (map.getNpcStorage().get(new D2Idx(x, y)) != null) {
				return;
			}

			//����`�b�v���̔���
			if (!VehicleStorage.getInstance().getCurrentVehicle().isStepOn(map.getTile(new D2Idx(x, y)).getChip())) {
				return;
			}
		}

		if (map.getBackgroundLayerSprite() != null) {
			map.getBackgroundLayerSprite().move();
		}
		map.getBacklLayeres().forEach(e -> e.move());
		if (mode != FieldMapCameraMode.FOLLOW_TO_CENTER) {
			//�Ǐ]���[�h����Ȃ��ꍇ�͓����x�N�g���ňړ�
			FieldMap.getPlayerCharacter().get(0).move();
		}
		if (FieldMap.getPlayerCharacter().size() > 1) {
			FieldMap.getPlayerCharacter().subList(1, FieldMap.getPlayerCharacter().size()).forEach(v -> v.move());
		}
		map.getNpcStorage().forEach(e -> e.move());
		map.getFrontlLayeres().forEach(e -> e.move());
		map.getFrontAnimation().forEach(e -> e.move());
		//�ړ���̍��W�Čv�Z
		//NPC�̈ʒu�X�V
		int chipW = map.getChipW();
		int chipH = map.getChipH();
//		for (NPC n : map.getNpcStorage()) {
//			float nx = map.getBaseLayer().getX() + n.getCurrentIdx().x * chipW;
//			float ny = map.getBaseLayer().getY() + n.getCurrentIdx().y * chipH;
//			n.setLocation(nx, ny);
//		}
		switch (mode) {
			case FOLLOW_TO_CENTER:
				//�Ǐ]���[�h�̏ꍇ�A�L�����N�^�̍��W���Čv�Z����
				//�v���C���[�L�����N�^�[�i���S�jIDX�X�V
				BasicSprite base = map.getBaseLayer();
				float fieldMapX = ((-base.getX() + (chipW / 2) - (chipW / 4) + base.getVector().reverse().getLocation().x)) / chipW;
				float fieldMapY = ((-base.getY() + (chipH / 2) + (chipH / 4) + base.getVector().reverse().getLocation().y)) / chipH;
				int x = (int) (playerLocation.x + fieldMapX);
				int y = (int) (playerLocation.y + fieldMapY);
				//�̈�O�̔���
				if (x < 1 || y < 1) {
					return;
				}
				if (map.getBaseLayer().getDataWidth() <= x + 1 || map.getBaseLayer().getDataHeight() <= y + 1) {
					return;
				}
				//NPC�Փ˔���
				if (map.getNpcStorage().get(new D2Idx(x, y)) != null) {
					return;
				}

				//����`�b�v���̔���
				if (!VehicleStorage.getInstance().getCurrentVehicle().isStepOn(map.getTile(new D2Idx(x, y)).getChip())) {
					return;
				}
				map.setCurrentIdx(new D2Idx(x, y));
				break;
			case FREE:
				// �t���[���[�h�̏ꍇ�J�����݂̂��𓮂����A�������Ȃ�
				break;
		}
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
	 * �t�B�[���h�}�b�v�̃J�����gIDX�𒆐S�ɕ\������悤�A�J�����ʒu���X�V���܂��B
	 */
	public void updateToCenter() {
		D2Idx currentIdx = map.getCurrentIdx();
		int chipW = map.getChipW();
		int chipH = map.getChipH();
		int screenW = FieldMapStorage.getScreenWidth();
		int screenH = FieldMapStorage.getScreenHeight();
		//�\���ʒu�����S-��ʃT�C�Y
		int x = currentIdx.x * chipW - (screenW / 2);
		int y = currentIdx.y * chipH - (screenH / 2);
		x += chipW / 2;
		y += chipH / 2;
		setLocation(-x, -y);
		//�L�����N�^�̈ʒu�C��
		int charaW = FieldMap.getPlayerCharacter().get(0).getImageWidth();
		int charaH = FieldMap.getPlayerCharacter().get(0).getImageHeight();

		float cx = screenW / 2 - (charaW / 2);
		float cy = screenH / 2 - (charaH / 2);
		FieldMap.getPlayerCharacter().forEach(v -> v.setLocation(cx, cy));

		//NPC�̈ʒu�X�V
		for (NPC n : map.getNpcStorage()) {
			float nx = map.getBaseLayer().getX() + n.getCurrentIdx().x * chipW;
			float ny = map.getBaseLayer().getY() + n.getCurrentIdx().y * chipH;
			n.setLocation(nx, ny);
		}
		//PC�̈ʒu�X�V
		for (PlayerCharacterSprite c : FieldMap.getPlayerCharacter().subList(1, FieldMap.getPlayerCharacter().size())) {
			if (c.getCurrentIdx() != null) {
				float nx = map.getBaseLayer().getX() + c.getCurrentIdx().x * chipW;
				float ny = map.getBaseLayer().getY() + c.getCurrentIdx().y * chipH;
				c.setLocation(nx, ny);
			}
		}

	}

}
