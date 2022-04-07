/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.game.rpgui;

import java.awt.image.BufferedImage;
import java.io.Serializable;
import kinugasa.resource.Nameable;
import kinugasa.resource.TImage;
/**
 * �t�B�[���h�}�b�v���\������1�̃^�C���ł�.
 * <br>
 * �}�b�v�`�b�v�͖��O�A�摜����ё����������܂��B
 * <br>
 * ���O�ɂ́A�ʏ�̓X�v���C�g�V�[�g�̐؂�o���ʒu���ݒ肳��܂��B<br>
 * ���̖��O�̓}�b�v�f�[�^�t�@�C���ɋL�q����`�b�v�̔z�u�� �g�p���܂��B<br>
 *
 * �����̃}�b�s���O��XML�t�@�C���ōs���܂��B<br>
 * XML����}�b�v�`�b�v���쐬����ɂ́AChipSetStorage�N���X���g�p���܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_23:02:47<br>
 * @author Dra0211<br>
 */
public final class MapChip implements Nameable, Serializable, Comparable<MapChip> {

	private static final long serialVersionUID = -6802142835353191416L;
	/**
	 * �}�b�v�`�b�v�̈�ӓI�Ȗ��O�ł�.
	 */
	private String name;
	/**
	 * ���̃}�b�v�`�b�v�̉摜�ł�.
	 */
	private TImage image;
	/**
	 * ���̃}�b�v�`�b�v�̑����ł�.
	 */
	private ChipAttribute attribute;

	/**
	 * �V�����}�b�v�`�b�v���쐬���܂�.
	 *
	 * @param name ��ӓI�Ȗ��O���w�肵�܂��B �ʏ��ChipSet�����ChipSetStorage���X�v���C�g�V�[�g�̐؂�o���ʒu�����Ƃ� �����I�ɖ��O�����܂��B���̖��O�̓}�b�v�f�[�^�t�@�C���ɋL�q����`�b�v�̔z�u�� �g�p���܂��B<br>
	 * @param image ���̃}�b�v�`�b�v�̉摜���w�肵�܂��B�ʏ�� �X�v���C�g�V�[�g����؂�o���܂��B<br>
	 * @param attribute ���̃}�b�v�`�b�v�̑������w�肵�܂��B<br>
	 */
	public MapChip(String name, TImage image, ChipAttribute attribute) {
		this.name = name;
		this.image = image;
		this.attribute = attribute;
	}

	/**
	 * ���̃`�b�v�����������擾���܂�.
	 *
	 * @return �`�b�v�̑�����Ԃ��܂��B<br>
	 */
	public ChipAttribute getAttribute() {
		return attribute;
	}

	/**
	 * ���̃`�b�v����������ݒ肵�܂�.
	 *
	 * @param attribute �V�����������w�肵�܂��B<br>
	 */
	public void setAttribute(ChipAttribute attribute) {
		this.attribute = attribute;
	}

	/**
	 * ���̃`�b�v�����摜���擾���܂�.
	 *
	 * @return �摜��Ԃ��܂��B<br>
	 */
	public TImage getSerializableImage() {
		return image;
	}

	/**
	 * ���̃`�b�v�����摜���擾���܂�.
	 *
	 * @return �摜��Ԃ��܂��B<br>
	 */
	public BufferedImage getImage() {
		return image.get();
	}

	/**
	 * ���̃`�b�v�����摜��ݒ肵�܂�.
	 *
	 * @param image �摜���w�肵�܂��B<br>
	 */
	public void setImage(TImage image) {
		this.image = image;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 83 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final MapChip other = (MapChip) obj;
		if ((this.name == null) ? (other.name != null) : !this.name.equals(other.name)) {
			return false;
		}
		return true;
	}

	@Override
	public String toString() {
		return "MapChip{" + "name=" + name + ", image=" + image + ", attribute=" + attribute + '}';
	}

	@Override
	public int compareTo(MapChip o) {
		return this.name.compareTo(o.getName());
	}
}
