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
package kinugasa.game.event;

import java.io.Serializable;

/**
 * ���n��Ŕ�������C�x���g�̏����ƑΏۃI�u�W�F�N�g���i�[����N���X�ł�.
 * <br>
 * �C�x���g��1�́u�I�u�W�F�N�g�v�Ɓu���s�����v�������܂��B<br>
 * �ʏ�A���s�����̓Q�[���̍X�V�񐔂Ɠ������܂��B���̒l�̓C�x���g����������
 * EventManager�̍X�V�񐔂����Ƃɔ��肵�܂��B<br>
 * �I�u�W�F�N�g�̓C�x���g�̎��s�ɂ���ĕԂ����A�C�e���ł��B<br>
 * �I�u�W�F�N�g�͎��s�O�Ɂi�o�^���Ɂj���̃C���X�^���X�����݂��Ă��Ȃ���΂Ȃ�܂���B<br>
 * ���s���ɂ́A�I�u�W�F�N�g�ɑ΂�����ʂȑ�����s������A�����߂����ƂŁA
 * �Q�[�����ŗ��p�ł���悤�ɂ��܂��B<br>
 * <br>
 * �C�x���g�����s�ł���^�C�~���O�́A�C�x���g�ɐݒ肳�ꂽEntryModel�ɂ���Ĕ��f����܂��B<br>
 * <br>
 * <br>
 * �C�x���g������ł��邩�ǂ�����ID�݂̂��]������܂��B<br>
 *
 * @param <T> ���̃C�x���g�������I�u�W�F�N�g�̌^���w�肵�܂��B<br>
 *
 * @version 1.0.0 - 2012/10/19_18:14:25.<br>
 * @author Dra0211<br>
 * <br>
 */
public abstract class TimeEvent<T> implements Comparable<TimeEvent<?>>, Serializable {

	/** �C�x���g�̎����ɂ��̒l���w�肷��ƁA���̃C�x���g�͍ŏ��̃^�[���Ŏ��s����܂�. */
	public static final long TIME_INITIAL = 0;
	/** �C�x���g�̎����ɂ��̒l���w�肷��ƁA���̃C�x���g�͎��s����܂���.
	 * ���̒l��-1L���g�p����Ă���̂ŁA�C�x���g�̎�����-1L���g�p���邱�Ƃ͂ł��܂���B */
	public static final long TIME_NOT_EXECUTE = -1L;
	/** �C�x���g�̍��v���̃J�E���^�ł�. */
	private static int idCounter = 0;
	/** ���̃C�x���g�ŗL��ID�ł�.
	 * ���̒l�͌����ďd�������A�C�x���g���쐬���ꂽ���ɘA�Ԃ��~���܂��B */
	private int id = ++idCounter;
	/** ���̃C�x���g�̎��s�����ł�. */
	private long executeTime;
	/** ���̃C�x���g�̎��s���f���ł�. */
	private EntryModel entryModel;
	/** ���̃C�x���g���g�p����I�u�W�F�N�g�ł�.
	 * �T�u�N���X����g�p�ł��܂��B<br> */
	protected final T object;
	/** ���̃C�x���g�������^���ł�. */
	private Class<T> type;

	/**
	 * �V�����C�x���g���쐬���܂�.
	 * �C�x���g�������I�u�W�F�N�g�́A�����̃C�x���g�Ԃŋ��ʂ̃C���X�^���X���g�p���邱�Ƃ��ł��܂��B<br>
	 * �C�x���g�̋N��������-1(TIME_NOT_EXECUTE)���w�肵���ꍇ�A���̃C�x���g�͎��s�ł��Ȃ��Ȃ邱�Ƃɒ��ӂ��Ă��������B<br>
	 * �ŏ��ɋN������K�v�̂���C�x���g��0���w�肵�܂��B<br>
	 *
	 * @param executeTime �C�x���g�̋N���������w�肵�܂��B���̒l�͒ʏ�A�}�l�[�W���̍X�V�񐔂ɂ���Ĕ��肳��܂��B<br>
	 *                       ���Ƃ��΁AFPS��60�ł́A�J�n���炨�悻1�b��Ɏ���60�ƂȂ�܂��B<br>
	 * @param entryModel  ���̃C�x���g�̎��s����@�\���`���܂��B���̃��f�����g�p���āA�u�t�B�[���h�ɓG������ꍇ�͏o�����Ȃ��v
	 *                       ���̏����𔻒肷�邱�Ƃ��ł��܂��B<br>
	 * @param obj         �C�x���g���g�p����I�u�W�F�N�g���w�肵�܂��B�I�u�W�F�N�g�͉���\�ȏꍇ�́A���[�h�����ɑ��M���邱�Ƃ��ł��܂��B<br>
	 *
	 * @throws IllegalArgumentException �N��������TIME_NOT_EXECUTE(-1)��菬�����ꍇ�ɓ������܂��B<br>
	 * @throws NullPointerException     �g�p����I�u�W�F�N�g��null�̏ꍇ�ɓ������܂��B<br>
	 */
	@SuppressWarnings("unchecked")
	public TimeEvent(long executeTime, EntryModel entryModel, T obj) throws IllegalArgumentException, NullPointerException {
		if (executeTime < TIME_NOT_EXECUTE) {
			throw new IllegalArgumentException("executeTime < NOT_EXECUTE :  id=[" + id + "], time=[" + executeTime + "]");
		}
		if (obj == null) {
			throw new NullPointerException("event obj == null id=" + id + "]");
		}
		this.executeTime = executeTime;
		this.entryModel = entryModel;
		this.object = obj;
		this.type = (Class<T>) obj.getClass();
	}

	/**
	 * ���̃C�x���g��ID���擾���܂�.
	 * ID�̓C�x���g�̍쐬���ɘA�ԂƂ��Ċ��蓖�Ă��܂��B<br>
	 * ���̃}�l�[�W���̃C�x���g�ł����Ă��d�����邱�Ƃ͂���܂���B<br>
	 *
	 * @return ���̃C�x���g��ID��Ԃ��B<br>
	 */
	public final int getId() {
		return id;
	}

	/**
	 * �쐬���ꂽ�C�x���g�̍��v�����擾���܂�.
	 *
	 * @return �쐬�ς݂̃C�x���g�̐�.<Br>
	 */
	public static int getEventsNum() {
		return idCounter;
	}

	/**
	 * ���̃C�x���g�̃G���g�����f����ݒ肵�܂�.
	 *
	 * @param entryModel �V�����G���g�����f���B<br>
	 */
	public final void setEntryModel(EntryModel entryModel) {
		this.entryModel = entryModel;
	}

	/**
	 * ���̃C�x���g�ɐݒ肳��Ă���G���g�����f�����擾���܂�.
	 *
	 * @return �G���g�����f���B<br>
	 */
	public final EntryModel getEntryModel() {
		return entryModel;
	}

	/**
	 * ���̃C�x���g�������_�Ŏ��s�\�ł��邩���G���g�����f���ɂ���ĕ]�����܂�.
	 *
	 * @return ���̃C�x���g�������_�Ŏ��s�ł���ꍇ��true�A�����łȂ��ꍇ��false��Ԃ��B<br>
	 */
	public final boolean isReaching() {
		return executeTime == TIME_NOT_EXECUTE ? false : entryModel.isReaching(this);
	}

	/**
	 * ���̃C�x���g�̎��s�������擾���܂�.
	 *
	 * @return ���̃C�x���g�̎��s�����B<br>
	 */
	public final long getExecuteTime() {
		return executeTime;
	}

	/**
	 * �C�x���g�������̏����Ƀ\�[�g���邽�߂̔�r�@�\�ł�.
	 * ���̃��\�b�h�͕ʂ̌^�̃C�x���g�ł����Ă���r�ł��܂��B<br>
	 *
	 * @param o ��r����C�x���g�B<br>
	 *
	 * @return Compareble�̎����Ɋ�Â��l��Ԃ��B<br>
	 */
	@Override
	public final int compareTo(TimeEvent<?> o) {
		return executeTime > o.executeTime ? 1 : executeTime < o.executeTime ? -1 : 0;
	}

	/**
	 * ���̃C�x���g���ۗL���Ă���I�u�W�F�N�g���擾���܂�.
	 *
	 * @return ���̃C�x���g�ɂ���Ďg�p�����I�u�W�F�N�g��Ԃ��B<br>
	 */
	public final T getObject() {
		return object;
	}

	/**
	 * ���̃C�x���g�������^��Ԃ��܂�.
	 *
	 * @return ���̃C�x���g���g�p����^�B<br>
	 */
	public final Class<T> getType() {
		return type;
	}

	/**
	 * ���̃C�x���g�����s���܂�.
	 * ���s��A��Ԃ��ύX���ꂽ�I�u�W�F�N�g��߂��K�v������܂��B<br>
	 *
	 * @return ���̃C�x���g�ɂ���ď�Ԃ��ύX���ꂽ�I�u�W�F�N�g��߂��B<br>
	 */
	public abstract T execute();

	@Override
	public String toString() {
		return "Event{" + "id=" + id + ", executeTime=" + executeTime + ", type=" + type + '}';
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		@SuppressWarnings("unchecked")
		final TimeEvent<T> other = (TimeEvent<T>) obj;
		if (this.id != other.id) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 5;
		hash = 23 * hash + this.id;
		return hash;
	}
}