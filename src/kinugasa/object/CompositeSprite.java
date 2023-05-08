/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
package kinugasa.object;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import kinugasa.game.GraphicsContext;

/**
 * �����̃X�v���C�g���܂Ƃ߂ĕ`�悵����A�\�[�g�ł���N���X�ł�.
 * <br>
 * ���̃N���X�̂��Ƃ�"�����X�v���C�g"�ƌĂт܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_20:45:45<br>
 * @author Shinacho<br>
 */
public class CompositeSprite extends BasicSprite {

	/** ���̕����X�v���C�g�����X�v���C�g�̃��X�g�ł�. */
	private ArrayList<Sprite> sprites;

	/**
	 * �V���������X�v���C�g���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A�X�v���C�g���X�g�̏����e�ʂ�0�ɂȂ�܂��B<br>
	 */
	public CompositeSprite() {
		this(Collections.<Sprite>emptyList());
	}

	/**
	 * �V���������X�v���C�g���쐬���܂�.
	 *
	 * @param spr �����X�v���C�g�ɒǉ�����X�v���C�g���w�肵�܂��B<br>
	 */
	public CompositeSprite(Sprite... spr) {
		this(Arrays.asList(spr));
	}

	/**
	 * �V���������X�v���C�g���쐬���܂�.
	 *
	 * @param spr �����X�v���C�g�ɒǉ�����X�v���C�g���w�肵�܂��B<br>
	 */
	public CompositeSprite(List<Sprite> spr) {
		this.sprites = new ArrayList<Sprite>(spr.size());
		addAll(spr.toArray(new Sprite[spr.size()]));
	}

	/**
	 * ���̕����X�v���C�g�ɐV�����X�v���C�g��ǉ����܂�.
	 * �V�����X�v���C�g�̓X�v���C�g���X�g�̍Ō���ɒǉ�����܂��B<br>
	 * Z�����W�͎�����������܂���B<br>
	 *
	 * @param spr �ǉ�����X�v���C�g���w�肵�܂��B<br>
	 *
	 * @throws IllegalArgumentException �ǉ�����X�v���C�g��this�̂Ƃ��A�܂��́A�ǉ�����X�v���C�g��CompositeSprite��
	 * ���̕����X�v���C�g���ێ�����X�v���C�g���X�g����this�܂��͐e�̃X�v���C�g������ꍇ�ɓ������܂��B<br>
	 * �����X�v���C�g�̐e�����̕����X�v���C�g�������ׂĂ̎q�̐e�Əz�Q�ƂɂȂ��Ă��邩�̃`�F�b�N��
	 * �V�����X�v���C�g��ǉ����邽�тɍċA�I�ɍs���܂��B<br>
	 */
	public void add(Sprite spr) throws IllegalArgumentException {
		if (spr instanceof CompositeSprite) {
			checkInstance(Arrays.asList(this), Arrays.asList(spr));
		}
		sprites.add(spr);
	}

	/**
	 * ���̕����X�v���C�g�ɐV�����X�v���C�g��ǉ����܂�.
	 * �V�����X�v���C�g�̓X�v���C�g���X�g�̍Ō���Ɏw�肳�ꂽ�����Œǉ�����܂��B<br>
	 * Z�����W�͎�����������܂���B<br>
	 *
	 * @param spr �ǉ�����X�v���C�g���w�肵�܂��B<br>
	 *
	 * @throws IllegalArgumentException �ǉ�����X�v���C�g��this���܂܂��Ƃ��A�܂��́A�ǉ�����X�v���C�g��CompositeSprite��
	 * ���̕����X�v���C�g���ێ�����X�v���C�g���X�g����this�܂��͐e�̃X�v���C�g������ꍇ�ɓ������܂��B<br>
	 * �����X�v���C�g�̐e�����̕����X�v���C�g�������ׂĂ̎q�̐e�Əz�Q�ƂɂȂ��Ă��邩�̃`�F�b�N��
	 * �V�����X�v���C�g��ǉ����邽�тɍċA�I�ɍs���܂��B<br>
	 */
	public void addAll(Sprite... spr) throws IllegalArgumentException {
		for (Sprite s : spr) {
			add(s);
		}
	}

	/**
	 * ���̕����X�v���C�g�ɐV�����X�v���C�g��ǉ����܂�.
	 * �V�����X�v���C�g�̓X�v���C�g���X�g�̍Ō���Ɏw�肳�ꂽ�����Œǉ�����܂��B<br>
	 * Z�����W�͎�����������܂���B<br>
	 *
	 * @param spr �ǉ�����X�v���C�g���w�肵�܂��B<br>
	 *
	 * @throws IllegalArgumentException �ǉ�����X�v���C�g��this���܂܂��Ƃ��A�܂��́A�ǉ�����X�v���C�g��CompositeSprite��
	 * ���̕����X�v���C�g���ێ�����X�v���C�g���X�g����this�܂��͐e�̃X�v���C�g������ꍇ�ɓ������܂��B<br>
	 * �����X�v���C�g�̐e�����̕����X�v���C�g�������ׂĂ̎q�̐e�Əz�Q�ƂɂȂ��Ă��邩�̃`�F�b�N��
	 * �V�����X�v���C�g��ǉ����邽�тɍċA�I�ɍs���܂��B<br>
	 */
	public void addAll(List<Sprite> spr) throws IllegalArgumentException {
		addAll(spr.toArray(new Sprite[spr.size()]));
	}

	/**
	 * �w�肵���X�v���C�g���A���̕����X�v���C�g�Ɋ܂܂�Ă���ꍇ�A���̃X�v���C�g�����̕����X�v���C�g����폜���܂�.
	 *
	 * @param spr �폜����X�v���C�g���w�肵�܂��B<br>
	 */
	public void remove(Sprite spr) {
		sprites.remove(spr);
	}

	/**
	 * �w�肵���X�v���C�g���A���̕����X�v���C�g�Ɋ܂܂�Ă���ꍇ�A���̃X�v���C�g�����̕����X�v���C�g����폜���܂�.
	 *
	 * @param spr �폜����X�v���C�g���w�肵�܂��B<br>
	 */
	public void removeAll(Sprite... spr) {
		sprites.removeAll(Arrays.asList(spr));
	}

	/**
	 * �w�肵���X�v���C�g���A���̕����X�v���C�g�Ɋ܂܂�Ă���ꍇ�A���̃X�v���C�g�����̕����X�v���C�g����폜���܂�.
	 *
	 * @param spr �폜����X�v���C�g���w�肵�܂��B<br>
	 */
	public void removeAll(List<Sprite> spr) {
		removeAll(spr.toArray(new Sprite[spr.size()]));
	}

	/**
	 * �w�肵���X�v���C�g�����̕����X�v���C�g�Ɋ܂܂�Ă��邩���������܂�.
	 *
	 * @param spr ��������X�v���C�g���w�肵�܂��B<br>
	 *
	 * @return spr�����̕����X�v���C�g�̃X�v���C�g���X�g�Ɋ܂܂�Ă���ꍇtrue�A�����łȂ��ꍇfalse��Ԃ��܂��B<br>
	 */
	public boolean contains(Sprite spr) {
		return sprites.contains(spr);
	}
	/*
	 * TODO:�ǉ����郁�\�b�h
	 * public boolean containsAll(Sprite... spr);
	 * public boolean containsAll(List<Sprite> spr);
	 * public boolean deepContains(Sprite spr );
	 * public boolean deepContainsAll(Sprite... spr);
	 * public boolean deepContainsAll(List<Sprite> spr);
	 */

	/**
	 * �X�v���C�g���X�g���̂��ׂẴX�v���C�g���A���̏�����Z�����W�ɕ��ׂ܂�.
	 *
	 * @param minZ ���X�g��0�Ԗڂɐݒ肳���z���W���w�肵�܂��B<br>
	 * @param maxZ ���X�g�̍Ō�̗v�f�ɐݒ肳���z���W���w�肵�܂��B<br>
	 *
	 * @throws IllegalArgumentException minZ &gt; maxZ�̂Ƃ��ɓ������܂��B<br>
	 */
	public void sortZ(float minZ, float maxZ) throws IllegalArgumentException {
		if (minZ > maxZ) {
			throw new IllegalArgumentException("min > max : minZ=[" + minZ + "] maxZ=[" + maxZ + "]");
		}
		float z = minZ;
		float addZ = (maxZ - minZ) / sprites.size();
		for (int i = 0; i < sprites.size(); i++) {
			sprites.get(i).setZ(z + addZ * i);
		}
		sort();
	}

	/**
	 * �X�v���C�g���X�g���̂��ׂẴX�v���C�g���폜���܂�.
	 */
	public void clear() {
		sprites.clear();
	}

	/**
	 * ���̕����X�v���C�g�����X�v���C�g���X�g���擾���܂�.
	 * ���X�g�͎Q�Ƃ�ێ����܂��B���X�g�ɑ΂��鑀��͕����X�v���C�g�ɓK�p����܂��B<br>
	 *
	 * @return ���̕����X�v���C�g�̃X�v���C�g���X�g���Ԃ���܂��B<br>
	 */
	public List<Sprite> getSprites() {
		return sprites;
	}

	/**
	 * �X�v���C�g���X�g�̎w�肵���C���f�b�N�X�Ɋi�[����Ă���X�v���C�g���擾���܂�.
	 *
	 * @param idx �擾����X�v���C�g�̃C���f�b�N�X���w�肵�܂�.<Br>
	 *
	 * @return �w�肵���C���f�b�N�X�̃X�v���C�g���Ԃ���܂�.<br>
	 *
	 * @throws IndexOutOfBoundsException �s���ȃC���f�b�N�X�̏ꍇ�ɓ������܂��B<br>
	 */
	public Sprite getSprite(int idx) throws IndexOutOfBoundsException {
		return sprites.get(idx);
	}

	/**
	 * ���̕����X�v���C�g�����X�v���C�g�̐����擾���܂�.
	 *
	 * @return �X�v���C�g���X�g�̗v�f����Ԃ��܂��B<br>
	 */
	public int size() {
		return sprites.size();
	}

	/**
	 * �����X�v���C�g�ɒǉ�����Ă���X�v���C�g�̈ˑ��֌W�𒲍����܂�.
	 *
	 * ���̃��\�b�h�͍ċA�I�ɌĂяo����܂��B<br>
	 *
	 * @param parents ���o���ꂽ���ׂĂ̕����X�v���C�g���i�[����Ă��郊�X�g�ł��B<br>
	 * ���̃��X�g�ɂ�this���܂݂܂��B<br>
	 * @param sprites ���o���ꂽ���ׂẴX�v���C�g���i�[����Ă��郊�X�g�ł��B<br>
	 * ���̃��X�g�ɂ͕����X�v���C�g���܂܂�Ă��܂��B<br>
	 *
	 * @throws IllegalArgumentException �X�v���C�g�̈ˑ��֌W�ɏz�Q�Ƃ��������ꂽ�Ƃ��ɓ������܂��B<br>
	 */
	private void checkInstance(List<CompositeSprite> parents, List<Sprite> sprites)
			throws IllegalArgumentException {
		Sprite spr;
		for (int i = 0, size = sprites.size(); i < size; i++) {
			spr = sprites.get(i);
			if (spr instanceof CompositeSprite) {
				CompositeSprite newParent = (CompositeSprite) spr;
				for (int j = 0, parentSize = parents.size(); j < parentSize; j++) {
					if (spr == parents.get(j)) {
						throw new IllegalArgumentException(
								"found loop reference : sprite=[" + spr + "]");
					}
				}
				List<CompositeSprite> newParents = new ArrayList<CompositeSprite>(parents);
				newParents.add(newParent);
				checkInstance(newParents, newParent.sprites);
			}
		}
	}

	/**
	 * �ǉ�����Ă��邷�ׂẴX�v���C�g�����̏����ŕ`�悵�܂�.
	 * ���̃��\�b�h�ł́A���̕����X�v���C�g�̉���ԂƐ�����Ԃ��l������܂��B<br>
	 * �e�X�v���C�g�́A���ꂼ��̉���ԂƐ�����Ԃ���э��W���g�p����܂��B<br>
	 *
	 * @param g �������ރO���t�B�b�N�X�R���e�L�X�g�𑗐M���܂��B<br>
	 */
	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).draw(g);
		}
	}

	/**
	 * �ǉ�����Ă��邷�ׂẴX�v���C�g���\�[�g���Ă���`�悵�܂�.
	 * ���̃��\�b�h�ł́A���̕����X�v���C�g�̉���ԂƐ�����Ԃ��l������܂��B<br>
	 * �e�X�v���C�g�́A���ꂼ��̉���ԂƐ�����Ԃ���э��W���g�p����܂��B<br>
	 * ���̃��\�b�h�ł́A�X�v���C�g���X�g���ɕ����X�v���C�g�����݂���ꍇ�́A
	 * ���̕����X�v���C�g���ċA�I�Ƀ\�[�g���܂��B<br>
	 *
	 * @param g �������ރO���t�B�b�N�X�R���e�L�X�g�𑗐M���܂��B<br>
	 * @param sortMode �\�[�g���@���w�肵�܂��BSpriteSortMode���w��ł��܂��B<br>
	 */
	public void draw(GraphicsContext g, Comparator<Sprite> sortMode) {
		sort(sortMode);
		draw(g);
	}

	/**
	 * �X�v���C�g���X�g���\�[�g���܂�.
	 * ���̃��\�b�h�ł́A�X�v���C�g���X�g���ɕ����X�v���C�g�����݂���ꍇ�́A
	 * ���̕����X�v���C�g���ċA�I�Ƀ\�[�g���܂��B<br>
	 *
	 * @param sortMode �\�[�g���@���w�肵�܂��BSpriteSortMode���w��ł��܂��B<br>
	 */
	public void sort(Comparator<Sprite> sortMode) {
		deepSort(sprites, sortMode);
	}

	/**
	 * �X�v���C�g���X�g���\�[�g���܂�.
	 * ���̃��\�b�h�ł́ASpriteSortMode��BACK_TO_FRONT���g�p����܂��B<br>
	 * ���̃��\�b�h�ł́A�X�v���C�g���X�g���ɕ����X�v���C�g�����݂���ꍇ�́A
	 * ���̕����X�v���C�g���ċA�I�Ƀ\�[�g���܂��B<br>
	 */
	public void sort() {
		sort(SpriteSortMode.BACK_TO_FRONT);
	}

	/**
	 * �X�v���C�g���X�g���ċA�I�Ƀ\�[�g���܂�.
	 * ���̃��\�b�h�ł́A�X�v���C�g���X�g���ɕ����X�v���C�g�����݂���ꍇ�́A
	 * ���̕����X�v���C�g���ċA�I�Ƀ\�[�g���܂��B<br>
	 * ���̃��\�b�h�͍ċA�I�ɏ�������܂��B<br>
	 *
	 * @param sprs �\�[�g����X�v���C�g���X�g���w�肵�܂��B<br>
	 * @param sortMode �\�[�g���@���w�肵�܂��BSpriteSortMode���w��ł��܂��B<br>
	 */
	private void deepSort(List<Sprite> sprs, Comparator<Sprite> sortMode) {
		Sprite spr;
		for (int i = 0, size = sprs.size(); i < size; i++) {
			spr = sprs.get(i);
			if (spr instanceof CompositeSprite) {
				deepSort(((CompositeSprite) spr).sprites, sortMode);
			}
		}
		Collections.sort(sprs, sortMode);
	}

	@Override
	public void setVisible(boolean visible) {
		super.setVisible(visible);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setVisible(visible);
		}
	}

	@Override
	public void setExist(boolean exist) {
		super.setExist(exist);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setExist(exist);
		}
	}

	//���̕����X�v���C�g���̂�Z��ݒ肷��
	@Override
	public void setZ(float z) {
		super.setZ(z);
	}

	/**
	 * �X�v���C�g���X�g�̗v�fidx�Ԗڂ̃X�v���C�g��Z�����W��ݒ肵�܂�.
	 *
	 * @param z �ݒ肷����W���w�肵�܂��B<br>
	 * @param idx Z���W��ݒ肷��X�v���C�g�̃C���f�b�N�X���w�肵�܂��B<br>
	 *
	 * @throws IndexOutOfBoundsException �s���ȃC���f�b�N�X�̏ꍇ�ɓ������܂��B<br>
	 */
	public void setZ(float z, int idx) throws IndexOutOfBoundsException {
		sprites.get(idx).setZ(z);
	}

	@Override
	public void setX(float x) {
		super.setX(x);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setX(x);
		}
	}

	@Override
	public void setY(float y) {
		super.setY(y);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setY(y);
		}
	}

	@Override
	public void setWidth(float width) {
		super.setWidth(width);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setWidth(width);
		}
	}

	@Override
	public void setHeight(float height) {
		super.setHeight(height);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setHeight(height);
		}
	}

	@Override
	public void setLocation(Point2D.Float location) {
		super.setLocation(location);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setLocation(location);
		}
	}

	@Override
	public void setLocation(float x, float y) {
		super.setLocation(x, y);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setLocation(x, y);
		}
	}

	@Override
	public void setSize(float w, float h) {
		super.setSize(w, h);
		for (int i = 0, size = sprites.size(); i < size; i++) {
			sprites.get(i).setSize(w, h);
		}
	}

	@Override
	public void setSize(Dimension size) {
		super.setSize(size);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			sprites.get(i).setSize(size);
		}
	}

	@Override
	public void setBounds(Rectangle2D.Float bounds) {
		super.setBounds(bounds);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			sprites.get(i).setBounds(bounds);
		}
	}

	@Override
	public void setAngle(float angle) {
		super.setAngle(angle);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setAngle(angle);
			}
		}
	}

	@Override
	public void setMovingModel(MovingModel movingModel) {
		super.setMovingModel(movingModel);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setMovingModel(movingModel);
			}
		}
	}

	@Override
	public void setVector(KVector vector) {
		super.setVector(vector);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setVector(vector);
			}
		}
	}

	@Override
	public void setSpeed(float speed) {
		super.setSpeed(speed);
		for (int i = 0, sprSize = sprites.size(); i < sprSize; i++) {
			if (sprites.get(i) instanceof BasicSprite) {
				((BasicSprite) sprites.get(i)).setSpeed(speed);
			}
		}
	}

	/**
	 * �X�v���C�g�̎����Ńt�B���^�����O���A�T�u���X�g���쐬���܂�.
	 * @param <T> �X�v���C�g�̊g���N���X���w�肵�܂��B<br>
	 * @param type ��������^���w�肵�܂��B<br>
	 * @return �w�肵���^�̗v�f��V�������X�g�Ɋi�[���ĕԂ��܂��B���Ԃ�
	 * ���̕����X�v���C�g�Ɋi�[����Ă���h�������ꂽ���ԁh�ł��B<br>
	 */
	public <T extends Sprite> List<Sprite> subList(Class<T> type) {
		List<Sprite> result = new ArrayList<Sprite>();
		for (int i = 0, size = size(); i < size; i++) {
			Sprite sprite = getSprite(i);
			if (type.equals(sprite.getClass())) {
				result.add(getSprite(i));
			}
		}
		return result;
	}

	@Override
	@SuppressWarnings("unchecked")
	public CompositeSprite clone() {
		CompositeSprite sprite = (CompositeSprite) super.clone();
		sprite.sprites = (ArrayList<Sprite>) this.sprites.clone();
		return sprite;
	}

	@Override
	public String toString() {
		return "CompositeSprite{" + "sprites=" + sprites + '}';
	}
}
