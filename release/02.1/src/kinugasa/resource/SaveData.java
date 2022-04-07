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
package kinugasa.resource;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * �Q�[���̓r���o�߂�ۑ����邽�߂̃N���X�ł�.
 * <br>
 * ���̃N���X���g�����āA�K�v�ȃt�B�[���h���`���Ă��������B<br>
 * <br>
 * �Z�[�u�f�[�^�̎����^���Ƃ��ẮA���O�A�쐬�����A�X�V����������܂��B<br>
 * ���O�ƍX�V�����͎��R�Ɏg�p���邱�Ƃ��ł��܂��B���O��Nameable�̃L�[�Ƃ���
 * �g�p����܂��B���O�ƍ쐬�����́A�ύX�ł��܂���B<br>
 *
 * @version 1.0.0 - 2013/01/13_0:10:27<br>
 * @author Dra0211<br>
 */
public abstract class SaveData implements Nameable, Serializable {

	private static final long serialVersionUID = 5051478173983169607L;
	/** �Z�[�u�f�[�^�̖��O�ł�. */
	private String name;
	/** �Z�[�u�f�[�^���쐬���ꂽ�����ł�. */
	private long createTime;
	/** �ŏI�X�V�Ȃǂ�ۊǂ���Date�ł�. */
	private Date date;

	/**
	 * �V�����Z�[�u�f�[�^���쐬���܂�.
	 * ���̃R���X�g���N�^�ł́A���O�͋�̕�����ɐݒ肳��܂��B<br>
	 */
	public SaveData() {
		this("");
	}

	/**
	 * �V�����Z�[�u�f�[�^���쐬���܂�.
	 * �쐬��������эX�V�����͌��݂̎����ɐݒ肳��܂��B<br>
	 *
	 * @param name �Z�[�u�f�[�^�̖��O���w�肵�܂��B��ӓI�ł���K�v������܂��B<br>
	 */
	public SaveData(String name) {
		this.name = name;
		createTime = System.currentTimeMillis();
		date = new Date();
	}

	@Override
	public final String getName() {
		return name;
	}

	/**
	 * ���̃Z�[�u�f�[�^�̍X�V�������擾���܂�.
	 *
	 * @return ���̃Z�[�u�f�[�^�ɐݒ肳�ꂽ�X�V������Ԃ��܂��B<br>
	 */
	public final Date getDate() {
		return date;
	}

	/**
	 * ���̃Z�[�u�f�[�^�̍X�V������ݒ肵�܂�.
	 *
	 * @param date ���̃Z�[�u�f�[�^�ɐݒ肷��X�V�����𑗐M���܂��B<br>
	 */
	public final void setDate(Date date) {
		this.date = date;
	}

	/**
	 * ���̃Z�[�u�f�[�^���ŏ��ɍ쐬���ꂽ�������擾���܂�.
	 * ���̒l�́A�ʏ�͈�ӓI�ł��B�������A���̒l�̓~���b�P�ʂȂ̂ŁA
	 * �Z���Ԃɕ����̃Z�[�u�f�[�^�𐶐������ꍇ�́A�d������\��������܂��B<br>
	 *
	 * @return ���̃Z�[�u�f�[�^���쐬���ꂽ������Ԃ��܂��B<br>
	 */
	public final long getCreateTime() {
		return createTime;
	}

	/**
	 * �w�肳�ꂽ�t�@�C���ɁA���̃Z�[�u�f�[�^��ۑ����܂�.
	 * �ۑ������t�@�C����ContentsIO���g�p���ĕ����ł��܂��B<Br>
	 *
	 * @param file ���s����t�@�C�����w�肵�܂��B�㏑���̊m�F�͍s���܂���B<br>
	 *
	 * @throws ContentsFileNotFoundException �t�@�C���p�X���s���ȏꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException �R���e���c��IO�Ɏ��s�����ꍇ�ɓ������܂��B<br>
	 */
	public final void save(File file) throws FileNotFoundException, ContentsIOException {
		ContentsIO.save(this, file);
	}

	/**
	 * �w�肳�ꂽ�t�@�C������A�Z�[�u�f�[�^��ǂݍ��݂܂�.
	 * ���̃��\�b�h�́AContentsIO���g�p�������[�h�Ɠ�����������܂��B<br>
	 *
	 * @param file �ǂݍ��ރt�@�C�����w�肵�܂��B<br>
	 *
	 * @return �w�肳�ꂽ�t�@�C�����畜�����ꂽ�Z�[�u�f�[�^��Ԃ��܂��B<br>
	 *
	 * @throws ContentsFileNotFoundException �t�@�C���p�X���s���ȏꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException �R���e���c��IO�Ɏ��s�����ꍇ�ɓ������܂��B<br>
	 */
	public static SaveData load(File file) throws FileNotFoundException, ContentsIOException {
		return ContentsIO.load(SaveData.class, file);
	}

	/**
	 * �w�肳�ꂽ�p�X�ɂ��邷�ׂĂ̓ǂݍ��݉\�ȃt�@�C�����Z�[�u�f�[�^�Ƃ��ă��[�h���A���X�g�Ƃ��ĕԂ��܂�.
	 *
	 * @param dir �ǂݍ��ރf�B���N�g���̃��[�g�ƂȂ�f�B���N�g���̃p�X���w�肵�܂��B����q�ɂȂ����f�B���N�g����
	 * �ċA�I�ɏ�������܂��B<br>
	 *
	 * @return �w�肳�ꂽ�f�B���N�g���ȉ��ɂ���A�Z�[�u�f�[�^�̔��s�ς݃f�[�^�𕜌����A���X�g�Ƃ��ĕԂ��܂��B<br>
	 *
	 * @throws ContentsFileNotFoundException �t�@�C���p�X���s���ȏꍇ�ɓ������܂��B<br>
	 * @throws ContentsIOException �R���e���c��IO�Ɏ��s�����ꍇ�ɓ������܂��B<br>
	 * @throws IllegalArgumentException dir���f�B���N�g���łȂ��ꍇ�ɓ������܂��B<br>
	 */
	public static List<SaveData> loadAll(File dir) throws FileNotFoundException, ContentsIOException, IllegalArgumentException {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("dir is not directory");
		}
		File[] files = dir.listFiles();
		List<SaveData> result = new ArrayList<SaveData>(files.length);
		for (File file : files) {
			if (file.isDirectory()) {
				result.addAll(loadAll(file));
			} else {
				try {
					result.add(ContentsIO.load(SaveData.class, file));
				} catch (FileNotFoundException ex) {
					continue;
				} catch (ContentsIOException ex) {
					continue;
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "SaveData{" + "name=" + name + ", createTime=" + createTime + ", date=" + date + '}';
	}
}
