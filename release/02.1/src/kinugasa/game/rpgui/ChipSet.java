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

import java.io.Serializable;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.Nameable;
import kinugasa.resource.Storage;
import kinugasa.resource.TImage;

/**
 * �t�B�[���h�}�b�v��1�̃��C�����g�p����A�}�b�v�`�b�v�̃Z�b�g�ł�.
 * <br>
 * �`�b�v�Z�b�g��1��XML�t�@�C���ɒ�`����AChipSetStorage�N���X���烍�[�h����܂��B<br>
 * �쐬���ꂽ�`�b�v�Z�b�g�́A�������O���Ȃ����ChipSetStorage�N���X�Ɏ����ǉ�����܂��B<br>
 * �������O�̃`�b�v�Z�b�g���o�^����Ă���ꍇ�A��O�𓊂��܂��B<Br>
 * <br>
 * �`�b�v�Z�b�g�ɂ͖��O"VOID"�̃`�b�v�������ǉ�����܂��B���̃`�b�v�͋�̉摜�i�����j�������A ������"VOID"�ł��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/04/28_23:26:33<br>
 * @author Dra0211<br>
 */
public class ChipSet extends Storage<MapChip> implements Nameable, Serializable, Comparable<ChipSet> {

	private static final long serialVersionUID = 7240169495094793450L;
	/**
	 * ���̃`�b�v�Z�b�g�̖��O�ł�.
	 */
	private String name;
	/**
	 * �`�b�v�Z�b�g�摜�̐؂�o���T�C�Y�̕��ł�.
	 */
	private int cutWidth;
	/**
	 * �`�b�v�Z�b�g�摜�̐؂�o���T�C�Y�̍����ł�.
	 */
	private int cutHeight;

	/**
	 * �V�����`�b�v�Z�b�g���쐬���܂�. �쐬���ꂽ�`�b�v�Z�b�g�͎����I��ChipSetStorage�ɒǉ�����܂��B<br>
	 *
	 * @param name �`�b�v�Z�b�g�̖��O���w�肵�܂��B<br>
	 * @param cutWidth �`�b�v�Z�b�g�摜�̐؂�o���T�C�Y�̕��ł��B<br>
	 * @param cutHeight �`�b�v�Z�b�g�摜�̐؂�o���T�C�Y�̍����ł��B<br>
	 * @throws DuplicateNameException ���O�����łɎg�p����Ă���Ƃ��ɓ������܂��B<br>
	 */
	public ChipSet(String name, int cutWidth, int cutHeight) throws DuplicateNameException {
		this.name = name;
		this.cutWidth = cutWidth;
		this.cutHeight = cutHeight;
		addThis();
	}

	/**
	 * ChipSetStorage��this�C���X�^���X��ǉ����܂�.
	 *
	 * @throws DuplicateNameException ���O�����łɎg�p����Ă���Ƃ��ɓ������܂��B<br>
	 */
	private void addThis() throws DuplicateNameException {
		ChipSetStorage.getInstance().add(this);
		put(new MapChip("VOID", new TImage(ImageUtil.newImage(cutWidth, cutHeight)),
				ChipAttributeStorage.getInstance().get("VOID")));
	}

	@Override
	public String getName() {
		return name;
	}

	public int getCutWidth() {
		return cutWidth;
	}

	public int getCutHeight() {
		return cutHeight;
	}

	@Override
	public int compareTo(ChipSet o) {
		return this.name.compareTo(o.getName());
	}
}
