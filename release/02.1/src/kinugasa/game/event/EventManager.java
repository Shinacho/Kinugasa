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
import kinugasa.game.GameLog;
import kinugasa.object.Statable;

/**
 * ���n��C�x���g����������d�g�݂̊�{�I�ȋ@�\���`���܂�.
 * <br>
 * ���ۂɔ�������C�x���g[ Event�N���X ]�̓}�l�[�W���̎����ɂ���Ĉ����^���قȂ邽�߁A
 * �C�x���g��ǉ�����@�\�͂��̃N���X�ɂ͒�`����Ă��܂���B<br>
 �t���[�����[�N�ł́A��{�I�Ȏ����Ƃ��āATimeEvent��T��������MultiClassEventManager�ƁA
 �P��̌^��Event������SIngleClassEventManager��p�ӂ��Ă��܂��B<br>
 * <br>
 * <br>
 * �}�l�[�W�����g�p����C�x���g�̓��X�g�܂��̓L���[�Ƃ��Ď�������܂��B<br>
 * <br>
 *
 * @version 1.0.0 - 2012/11/15_8:10:37<br>
 * @author Dra0211<br>
 * <br>
 *
 */
public abstract class EventManager implements Statable, Serializable {

	private static final long serialVersionUID = -1656169201397066551L;
	/**
	 * �}�l�[�W���̍X�V�񐔂ł��B�C�x���g�̔����^�C�~���O�̌����Ɏg�p����܂�.
	 */
	private transient long progressTime = 0L;
	/**
	 * ���[�h����Ă��邩�ǂ����̃t���O�ł�.
	 */
	private transient boolean load = false;
	/**
	 * ���̃}�l�[�W���̎����ƃC�x���g�̎������r����ł���ʓI�Ȏ����ł�.
	 */
	protected final EntryModel TIME_BASE_ENTRY_MODEL = new EntryModel() {
		private static final long serialVersionUID = -3986406384609892814L;

		@Override
		public boolean isReaching(TimeEvent<?> evt) {
			return evt.getExecuteTime() > getProgressTime();
		}
	};

	/**
	 * �}�l�[�W�����\�z���܂�. �ʏ�́A�C�x���g���i�[���郊�X�g��L���[�����������܂��B<br>
	 */
	public EventManager() {
	}

	/**
	 * �C�x���g�}�l�[�W���̓������Ԃ��X�V���܂�. ���̃��\�b�h�͖��^�[���i�������͉��񂩂�1��j�Ăяo��������K�v������܂��B<br>
	 * ���̃��\�b�h�ɂ���Ă��̃C�x���g�}�l�[�W���̓����������X�V����A �V�����C�x���g���N���ł���悤�ɂȂ�܂��B<br>
	 */
	public void update() {
		progressTime++;
	}

	/**
	 * ���̃C�x���g�}�l�[�W���̍X�V�񐔂��擾���܂�. �X�V�񐔂̓C�x���g����������"����"��\���܂��B<bR>
	 * �ʏ�A�X�V�������A�C�x���g�̎��s���������傫���Ȃ����Ƃ��A�C�x���g���N������܂��B<br>
	 * ���̎����͂����̃J�E���^�ł���Aupdate���Ăяo�����񐔂��ۊǂ���܂��B<br>
	 * ���t���[���X�V���邱�ƂŁA�����̃J�E���^�͑��t���[�����Ɠ������܂��B<br>
	 *
	 * @return ���̃C�x���g�}�l�[�W���̍X�V�񐔂�Ԃ��܂��B<br>
	 */
	public long getProgressTime() {
		return progressTime;
	}

	public EventManager load() {
		init();
		sort();
		if (GameLog.isUsingLog()) {
			GameLog.printInfo("EventManager�̃��[�h���J�n����");
			printAll();
			GameLog.printInfo("EventManager�̃��[�h����������");
		}
		load = true;
		return this;
	}

	/**
	 * �}�l�[�W���ɒǉ�����Ă���C�x���g�����������܂�. ���̃��\�b�h��load���\�b�h���R�[������Ǝ����I�ɌĂ΂�܂��B<br>
	 * ��ۃN���X�ł́Aadd���\�b�h���g�p���ăC�x���g��ǉ����鏈�����L�q���� �K�v������܂��B<br>
	 */
	protected abstract void init();

	public EventManager free() {
		progressTime = 0L;
		clear();
		load = false;
		return this;
	}

	/**
	 * �X�g���[���ɃC�x���g�̏��𔭍s���܂�. ���̃��\�b�h�̓f�o�b�O�p�ł��Bload���\�b�h�ɂ���Ď��s����܂��B<br>
	 */
	public abstract void printAll();

	public boolean isLoaded() {
		return load;
	}

	/**
	 * �}�l�[�W���ɒǉ�����Ă���C�x���g���A���n��ɉ����ă\�[�g���܂�.
	 */
	public abstract void sort();

	/**
	 * �}�l�[�W���ɒǉ�����Ă���A�܂��j������Ă��Ȃ��C�x���g�̐����擾���܂�.
	 *
	 * @return �ǉ����݃C�x���g�̐���Ԃ��܂��B<br>
	 */
	public abstract int size();

	/**
	 * �}�l�[�W���̔j������Ă��Ȃ��C�x���g�̐���0�ł��邩���������܂�.
	 *
	 * @return �}�l�[�W���ɃC�x���g���Ȃ��ꍇ��true�A1�ȏ�̃C�x���g���ҋ@���ł���ꍇ��false��Ԃ��܂��B<br>
	 */
	public abstract boolean isEmpty();

	/**
	 * �}�l�[�W���ɒǉ�����Ă��邷�ׂẴC�x���g��j�����܂�.
	 */
	public abstract void clear();

	/**
	 * �}�l�[�W���ɃC�x���gevt���܂܂�Ă��邩�𒲂ׂ܂�.
	 *
	 * @param evt ��������C�x���g�𑗐M���܂��B<br>
	 *
	 * @return evt���C�x���g���X�g�Ɋ܂܂�Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	public abstract boolean contains(TimeEvent<?> evt);

	/**
	 * �}�l�[�W���ɃC�x���gevt���܂܂�Ă���΍폜���܂�. ���̃��\�b�h�͎��s�����C�x���g��j�����邽�߂ɂ��g�p����܂��B<br>
	 * evt���܂܂�Ă��Ȃ��ꍇ�͉����s���܂���B<br>
	 *
	 * @param evt �폜����C�x���g�𑗐M���܂��B<br>
	 */
	public abstract void remove(TimeEvent<?> evt);

	/**
	 * �}�l�[�W���ɃC�x���gevt���܂܂�Ă���΍폜���܂�. ���̃��\�b�h�͎��s�����C�x���g��j�����邽�߂ɂ��g�p����܂��B<br>
	 * evt���܂܂�Ă��Ȃ��ꍇ�͉����s���܂���B<br>
	 *
	 * @param evt �폜����C�x���g�𑗐M���܂��B<br>
	 */
	public abstract void removeAll(TimeEvent<?>... evt);

	@Override
	public String toString() {
		return "EventManager{" + "progressTime=" + progressTime + ", load=" + load + '}';
	}
}
