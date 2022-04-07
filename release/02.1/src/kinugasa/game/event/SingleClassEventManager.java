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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import kinugasa.game.GameLog;
import kinugasa.resource.NotYetLoadedException;

/**
 * �P��̃N���X�̃C�x���g�������C�x���g�}�l�[�W���ł�.
 * <br>
 * ���̃N���X�͒P��̌^��Event�̎��������n��ɔ���������d�g�݂�񋟂��܂��B<br>
 * �N���X�C���X�^���X�ɂ���Ď��ɔ�������C�x���g����������K�v���Ȃ����߁A�ʏ��MultiClassEventManager����
 * �����ɓ��삵�܂��B<br>
 * <br>
 * <br>
 * �C�x���g��o�^����ɂ́A���̃N���X�̋�ۃN���X���`���Ainit���\�b�h����add���\�b�h���g�p���ăC�x���g��ǉ����܂��B<br>
 * <br>
 * �C�x���g�����s����ɂ�hasNext���\�b�h��execute���\�b�h�𗘗p���܂��B<br>  {@code
 * while(manager.hasNext()){
 * items.add(manager.execute());
 * }
 * }
 *
 * @param <T> ���̃}�l�[�W���������C�x���g�̌^���w�肵�܂��B<br>
 *
 * @version 1.0.0 - 2012/11/15_8:04:20<br>
 * @author Dra0211<br>
 * <br>
 *
 */
public abstract class SingleClassEventManager<T extends Serializable> extends EventManager {

	private static final long serialVersionUID = 576975168711401996L;
	/**
	 * ���̃}�l�[�W���̃C�x���g���X�g�ł�.
	 */
	private List<TimeEvent<T>> events;

	/**
	 * �V�����C�x���g�}�l�[�W�����\�z���܂�. ���̃��\�b�h�ł́A�C�x���g�̏����l��32�ƂȂ�܂��B<br>
	 */
	public SingleClassEventManager() {
		this(32);
	}

	/**
	 * �V�����C�x���g�}�l�[�W�����\�z���܂�.
	 *
	 * @param initialSize �C�x���g�̏����e�ʂ��w�肵�܂��B<br>
	 */
	public SingleClassEventManager(int initialSize) {
		events = new ArrayList<TimeEvent<T>>(initialSize);
	}

	@Override
	protected abstract void init();

	@Override
	public SingleClassEventManager<T> load() {
		return (SingleClassEventManager<T>) super.load();
	}

	@Override
	public SingleClassEventManager<T> free() {
		return (SingleClassEventManager<T>) super.free();
	}

	@Override
	public void printAll() {
		for (int i = 0, size = events.size(); i < size; i++) {
			GameLog.printInfo("-" + events.get(i));
		}
	}

	@Override
	public void sort() {
		Collections.sort(events);
	}

	@Override
	public int size() {
		return events.size();
	}

	@Override
	public boolean isEmpty() {
		return events.isEmpty();
	}

	@Override
	public void clear() {
		events.clear();
	}

	@Override
	public boolean contains(TimeEvent<?> evt) {
		return events.contains(evt);
	}

	@Override
	public void remove(TimeEvent<?> evt) {
		events.remove(evt);
	}

	@Override
	public void removeAll(TimeEvent<?>... evt) {
		events.removeAll(Arrays.asList(evt));
	}

	/**
	 * ���̃}�l�[�W���ɐV�����C�x���g��ǉ����܂�. ���̃��\�b�h�́A�}�l�[�W�������[�h�ς݂ł��g�p�ł��܂��B<br>
	 * ���������̏ꍇ�̓C�x���g���\�[�g����܂���B<br>
	 * ���[�h�O�̏ꍇ�́A���[�h���Ƀ\�[�g����܂��B<br>
	 * �\�[�g������ꍇ��sort���\�b�h���g�p���Ă��������B<br>
	 *
	 * @param evt �ǉ�����C�x���g�𑗐M���܂��B<br>
	 */
	public void add(TimeEvent<T> evt) {
		events.add(evt);
	}

	/**
	 * ���̃}�l�[�W���ɐV�����C�x���g��ǉ����܂�. ���̃��\�b�h�́A�}�l�[�W�������[�h�ς݂ł��g�p�ł��܂��B<br>
	 * ���������̏ꍇ�̓C�x���g���\�[�g����܂���B<br>
	 * ���[�h�O�̏ꍇ�́A���[�h���Ƀ\�[�g����܂��B<br>
	 * �\�[�g������ꍇ��sort���\�b�h���g�p���Ă��������B<br>
	 *
	 * @param evt �ǉ�����C�x���g�𑗐M���܂��B<br>
	 */
	public void addAll(TimeEvent<T>... evt) {
		events.addAll(Arrays.asList(evt));
	}

	/**
	 * ���̃}�l�[�W���Ɋ܂܂�Ă��邷�ׂẴC�x���g���擾���܂�.
	 * ���̃��\�b�h�͎Q�Ƃ�ێ����܂��B�߂�l�ɑ΂��鑀��͂��̃}�l�[�W���ɔ��f����܂��B<br>
	 *
	 * @return �}�l�[�W���Ɍ��݊i�[����Ă���C�x���g�̃��X�g��Ԃ��܂��B<br>
	 */
	public List<TimeEvent<T>> getEvents() {
		return events;
	}

	/**
	 * �C�x���g���X�g�̐擪�̃C�x���g��Ԃ��܂�. ���̃��\�b�h�ł͔������ꂽ�C�x���g�����s�^�폜�����ɕԂ��܂��B<br>
	 * �ʏ��hasNext�����execute���g�p���Ă��������B<br>
	 *
	 * @return �C�x���g���X�g�̐擪�̗v�f�����̂܂ܕԂ��܂��BisEmpty��true�̏ꍇ��null��Ԃ��܂��B<br>
	 *
	 * @throws NotYetLoadedException �}�l�[�W�������[�h����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public TimeEvent<T> getNext() throws NotYetLoadedException {
		if (!isLoaded()) {
			throw new NotYetLoadedException("not yet loaded : load=[" + isLoaded() + "]");
		}
		if (isEmpty()) {
			return null;
		}
		return events.get(0);
	}

	/**
	 * �C�x���g���X�g�̐擪�̃C�x���g�����s�\�ł���Ύ��s���ăC�x���g�̃A�C�e����Ԃ��܂�.
	 * ���̃��\�b�h�ŕԂ����I�u�W�F�N�g�́A�������ꂽ�C�x���g��execute�̌��ʂł��B<br>
	 * ���̃��\�b�h�́A���s�ł���C�x���g�����݂��Ȃ��ꍇ��null��Ԃ��_�ɒ��ӂ��Ă��������B<br>
	 * �ʏ�́A�ȉ��̂悤��hasNext��g�ݍ��킹�Ďg�p���܂��B<br>
	 * <br>
	 *
	 * @return
	 * �C�x���g�����s�����ꍇ�́A���̃C�x���g��execute�̌��ʂ�Ԃ��܂��B���s����C�x���g���Ȃ��ꍇ(hasNext==false)��null��Ԃ��܂��B<br>
	 *
	 * @throws NotYetLoadedException �}�l�[�W�������[�h����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public T execute() throws NotYetLoadedException {
		if (!isLoaded()) {
			throw new NotYetLoadedException("not yet loaded : load=[" + isLoaded() + "]");
		}
		if (!hasNext()) {
			return null;
		}
		TimeEvent<T> evt = events.get(0);
		GameLog.printIfUsing(Level.INFO, "> SingleClassEventManager : execute : now=[" + getProgressTime() + "] event=[" + evt + "]");
		remove(evt);
		return evt.execute();
	}

	/**
	 * �C�x���g���X�g�̐擪�̃C�x���g�����s�\�ȏ�Ԃőҋ@���ł��邩���������܂�.
	 * ���̃��\�b�h�ł́A���̃}�l�[�W���ɓo�^����Ă���C�x���g���X�g�̍ŏ��̃C�x���g��
	 * isReaching��true��Ԃ����ǂ������������܂��B<br>
	 *
	 * @return �C�x���g���X�g�̐擪�̃C�x���g��isReaching�̌��ʂ�Ԃ��܂��B<br>
	 *
	 * @throws NotYetLoadedException �}�l�[�W�������[�h����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public boolean hasNext() throws NotYetLoadedException {
		if (!isLoaded()) {
			throw new NotYetLoadedException("not yet loaded : load=[" + isLoaded() + "]");
		}
		if (isEmpty()) {
			return false;
		}
		return events.get(0).isReaching();
	}

	@Override
	public String toString() {
		return "SingleClassEventManager{" + "load=" + isLoaded() + ", events=" + size() + ", progressTime=" + getProgressTime() + '}';
	}
}
