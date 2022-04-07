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
package kinugasa.resource.sound;

import java.io.File;
import java.io.Serializable;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.FileNotFoundException;

/**
 * �L���b�V�����T�E���h�̃C���X�^���X���쐬���邽�߂̃r���_�ł�.
 * <br>
 * WAVE�t�@�C�����g�p����ۂ́A�ł���ʓI�ȍ\�z�N���X�ƂȂ�܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:48:59<br>
 * @author Dra0211<br>
 */
public final class SoundBuilder implements Serializable, Nameable {

	/** �t�@�C���C���X�^���X. */
	private File file;
	/** ���[�v�ݒ�. */
	private LoopPoint loopPoint = LoopPoint.NO_USE;
	/** ���o�[�u�ݒ�. */
	private ReverbModel reverbModel = ReverbModel.NO_USE;
	/** �}�X�^�[�Q�C���̒l. */
	private float masterGain = 1f;
	/** �{�����[��. */
	private float volume = 1f;
	/** �p���̐ݒ�. */
	private float pan = 0f;
	/** �Đ����̃T���v�����[�g. */
	private float sampleRate = 0f;
	/** �V�K�C���X�^���X�ō쐬���邩. */
	private boolean newFile = false;
	private String name;

	/**
	 * �V����CachedSound���쐬���邽�߂̃r���_���\�z���܂�.
	 * 
	 * @param filePath �t�@�C���p�X.<br>
	 *
	 * @throws ContentsFileNotFoundException �t�@�C�������݂��Ȃ��ꍇ�ɓ�������.<br>
	 */
	public SoundBuilder(String filePath) throws FileNotFoundException {
		File soundFile = new File(filePath);
		if (!soundFile.exists()) {
			throw new FileNotFoundException("not found : filePath=[" + filePath + "]");
		}
		this.file = soundFile;
		this.name = file.getName();
	}

	/**
	 * ���[�v�ʒu��ݒ肵�܂�.
	 * 
	 * @param loopPoint ���[�v�ʒu.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setLoopPoint(LoopPoint loopPoint) {
		this.loopPoint = loopPoint;
		return this;
	}

	/**
	 * ���[�v�ʒu��ݒ肵�܂�.
	 * 
	 * @param from ���[�v�ʒu.<br>
	 * @param to   ���[�v�ʒu.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setLoopPoint(int from, int to) {
		this.loopPoint = new LoopPoint(from, to);
		return this;
	}

	/**
	 * �T�E���h�̃}�X�^�[�Q�C����ݒ肵�܂�.
	 * ����̓{�����[�����T�|�[�g����Ă��Ȃ����ŉ��ʂ�ݒ肷�邱�Ƃ��ł��܂�.<br>
	 * 
	 * @param masterGain �Q�C���̒l.0.0f�Ŗ����ɂȂ�.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setMasterGain(float masterGain) {
		this.masterGain = masterGain;
		return this;
	}

	/**
	 * �X�e���I�T�E���h�̃p���ʒu��ݒ肵�܂�.
	 * ���̋@�\�̓T�|�[�g����Ă��Ȃ��\��������܂�.
	 * 
	 * @param pan ���S��0.0�A���E��1.0�Ƃ����ꍇ�̃p���ʒu.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setPan(float pan) {
		this.pan = pan;
		return this;
	}

	/**
	 * �T�E���h�̉��ʂ�ݒ肵�܂�.
	 * ���̋@�\�̓T�|�[�g����Ă��Ȃ��\��������܂�.���ʂ̒��߂̓}�X�^�[�Q�C�����g�p���Ă�������.<br>
	 * 
	 * @param volume ����.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setVolume(float volume) {
		this.volume = volume;
		return this;
	}

	/**
	 * �T�E���h�̃��o�[�u��ݒ肵�܂�.
	 * ���̋@�\�̓T�|�[�g����Ă��Ȃ��\��������܂�.
	 * 
	 * @param reverbModel ���o�[�u�̐ݒ�.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setReverbModel(ReverbModel reverbModel) {
		this.reverbModel = reverbModel;
		return this;
	}

	/**
	 * �T�E���h�̍Đ����̃T���v�����[�g��ݒ肵�܂�.
	 * ���̋@�\�̓T�|�[�g����Ă��Ȃ��\��������܂�.
	 * 
	 * @param sampleRate �Đ����̃T���v�����[�g.<br>
	 *
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
		return this;
	}

	/**
	 * ���̃��\�b�h���Ăяo����CachedSound�̃L���b�V���f�[�^���g�p�����A
	 * �V�����T�E���h�C���X�^���X���쐬���܂�.<br>
	 * 
	 * @return ���̃r���_�̃C���X�^���X.<br>
	 */
	public SoundBuilder newFile() {
		newFile = true;
		return this;
	}

	public SoundBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * �쐬�����\��̃T�E���h�̃t�@�C�����擾���܂�.
	 * ���̃��\�b�h�̖߂�l�̃t�@�C���͑��݂��ۏ؂���܂�.<br>
	 * 
	 * @return �t�@�C���C���X�^���X.<br>
	 */
	public File getFile() {
		return file;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return ���[�v�ʒu.<br>
	 */
	public LoopPoint getLoopPoint() {
		return loopPoint;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return �Q�C���̒l.<br>
	 */
	public float getMasterGain() {
		return masterGain;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return �L���b�V�����g�p�����ɐV�����C���X�^���X���쐬����ꍇ��TRUE��Ԃ�.<br>
	 */
	public boolean isNewFile() {
		return newFile;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return �p���̐ݒ�.<br>
	 */
	public float getPan() {
		return pan;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return ���o�[�u�̐ݒ�.<br>
	 */
	public ReverbModel getReverbModel() {
		return reverbModel;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return �Đ����̃T���v�����[�g.<br>
	 */
	public float getSampleRate() {
		return sampleRate;
	}

	/**
	 * �ݒ肳��Ă���l��Ԃ��܂�.
	 * 
	 * @return ����.<br>
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * ���݂̐ݒ�ŐV����CachedSound���쐬���܂�.
	 * 
	 * @return AudioData�̎�����Ԃ�.<br>
	 */
	public CachedSound builde() {
		return CachedSound.create(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SoundBuilder other = (SoundBuilder) obj;
		if (this.file != other.file && (this.file == null || !this.file.equals(other.file))) {
			return false;
		}
		if (this.loopPoint != other.loopPoint && (this.loopPoint == null || !this.loopPoint.equals(other.loopPoint))) {
			return false;
		}
		if (this.reverbModel != other.reverbModel && (this.reverbModel == null || !this.reverbModel.equals(other.reverbModel))) {
			return false;
		}
		if (Float.floatToIntBits(this.masterGain) != Float.floatToIntBits(other.masterGain)) {
			return false;
		}
		if (Float.floatToIntBits(this.volume) != Float.floatToIntBits(other.volume)) {
			return false;
		}
		if (Float.floatToIntBits(this.pan) != Float.floatToIntBits(other.pan)) {
			return false;
		}
		if (Float.floatToIntBits(this.sampleRate) != Float.floatToIntBits(other.sampleRate)) {
			return false;
		}
		if (this.newFile != other.newFile) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.file != null ? this.file.hashCode() : 0);
		hash = 97 * hash + (this.loopPoint != null ? this.loopPoint.hashCode() : 0);
		hash = 97 * hash + (this.reverbModel != null ? this.reverbModel.hashCode() : 0);
		hash = 97 * hash + Float.floatToIntBits(this.masterGain);
		hash = 97 * hash + Float.floatToIntBits(this.volume);
		hash = 97 * hash + Float.floatToIntBits(this.pan);
		hash = 97 * hash + Float.floatToIntBits(this.sampleRate);
		hash = 97 * hash + (this.newFile ? 1 : 0);
		return hash;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SoundBuilder{" + "file=" + file + ", loopPoint=" + loopPoint + ", reverbModel=" + reverbModel + ", masterGain=" + masterGain + ", volume=" + volume + ", pan=" + pan + ", sampleRate=" + sampleRate + ", newFile=" + newFile + '}';
	}
}
