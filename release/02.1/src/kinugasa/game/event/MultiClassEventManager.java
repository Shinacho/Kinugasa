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
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.NotYetLoadedException;

/**
 * �����̃N���X�̃C�x���g�������C�x���g�}�l�[�W���ł�.
 * <br>
 * ���̃N���X�͕����̌^��Event�̎��������n��ɔ���������d�g�݂�񋟂��܂��B<br>
 * �N���X�C���X�^���X�ɂ���āA���ɔ�������C�x���g����������K�v�����邽�߁A�ʏ��SingleClassEventManager�����ᑬ�ł��B<br>
 * <br>
 * �C�x���g��o�^����ɂ́A���̃N���X�̋�ۃN���X���`���Ainit���\�b�h����add���\�b�h���g�p���ăC�x���g��ǉ����܂��B<br>
 * <br>
 * �C�x���g�����s����ɂ�hasNext���\�b�h��execute���\�b�h�𗘗p���܂��B<br>
 * {@code
 * while(manager.hasNext(Item.class)){
 * items.add(manager.execute(Item.class));
 * }
 * }
 * <br>
 *
 * @version 1.0.0 - 2012/11/15_8:42:48<br>
 * @author Dra0211<br>
 * <br>
 *
 */
public abstract class MultiClassEventManager extends EventManager {

	private static final long serialVersionUID = 5774295111597602465L;
	/** ���̃}�l�[�W���̃C�x���g���X�g�ł�. */
	private List<TimeEvent<?>> events;

	/**
	 * �V�����C�x���g�}�l�[�W�����\�z���܂�.
	 * ���̃��\�b�h�ł́A�C�x���g�̏����l��32�ƂȂ�܂��B<br>
	 */
	public MultiClassEventManager() {
		this(32);
	}

	/**
	 * �V�����C�x���g�}�l�[�W�����\�z���܂�.
	 *
	 * @param initialSize �C�x���g�̏����e�ʂ��w�肵�܂��B<br>
	 */
	public MultiClassEventManager(int initialSize) {
		events = new ArrayList<TimeEvent<?>>(initialSize);
	}

	@Override
	protected abstract void init();

	@Override
	public MultiClassEventManager load() {
		return (MultiClassEventManager) super.load();
	}

	@Override
	public MultiClassEventManager free() {
		return (MultiClassEventManager) super.free();
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
	 * ���̃}�l�[�W���ɐV�����C�x���g��ǉ����܂�.
	 * �ǉ�����C�x���g�̌^�͔C�ӂł��B<br>
	 * ���̃��\�b�h�́A�}�l�[�W�������[�h�ς݂ł��g�p�ł��܂��B<br>
	 * ���������̏ꍇ�̓C�x���g���\�[�g����܂���B<br>
	 * ���[�h�O�̏ꍇ�́A���[�h���Ƀ\�[�g����܂��B<br>
	 * �\�[�g������ꍇ��sort���\�b�h���g�p���Ă��������B<br>
	 *
	 * @param e �ǉ�����C�x���g�𑗐M���܂��B<br>
	 */
	public final void add(TimeEvent<?> e) {
		events.add(e);
	}

	/**
	 * ���̃}�l�[�W���ɐV�����C�x���g��ǉ����܂�.
	 * �ǉ�����C�x���g�̌^�͔C�ӂł��B<br>
	 * ���̃��\�b�h�́A�}�l�[�W�������[�h�ς݂ł��g�p�ł��܂��B<br>
	 * ���������̏ꍇ�̓C�x���g���\�[�g����܂���B<br>
	 * ���[�h�O�̏ꍇ�́A���[�h���Ƀ\�[�g����܂��B<br>
	 * �\�[�g������ꍇ��sort���\�b�h���g�p���Ă��������B<br>
	 *
	 * @param e �ǉ�����C�x���g�𑗐M���܂��B<br>
	 */
	public final void addAll(TimeEvent<?>... e) {
		events.addAll(Arrays.asList(e));
	}

	/**
	 * �w�肵���^�́A�ŏ��Ɍ��������C�x���g��Ԃ��܂�.
	 * ���̃��\�b�h�ł͔������ꂽ�C�x���g�����s�^�폜�����ɕԂ��܂��B<br>
	 * �ʏ��hasNext�����execute���g�p���Ă��������B<br>
	 *
	 * @param <T> ��������C�x���g�̌^���w�肵�܂��B<br>
	 * @param type ��������C�x���g�̌^���w�肵�܂��B<br>
	 *
	 * @return �w�肵���^�̃C�x���g�ōŏ��Ɍ����������̂����̂܂ܕԂ��܂��B<br>
	 *
	 * @throws NotYetLoadedException �}�l�[�W�������[�h����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 * @throws NameNotFoundException �w�肳�ꂽ�^�̃C�x���g�����̃}�l�[�W���Ɋ܂܂�Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	@SuppressWarnings("unchecked")
	public final <T extends Serializable> TimeEvent<T> getNext(Class<T> type) throws NotYetLoadedException, NameNotFoundException {
		if (!isLoaded()) {
			throw new NotYetLoadedException("not yet loaded : load=[" + isLoaded() + "]");
		}
		for (int i = 0, size = events.size(); i < size; i++) {
			if (events.get(i).getType().equals(type) || type.isInstance(events.get(i).getObject())) {
				return (TimeEvent<T>) events.get(i);
			}
		}
		throw new NameNotFoundException("not found : type=[" + type.getName() + "]");
	}

	/**
	 * �w�肵���^�̃C�x���g���A���s�\�ȏ�Ԃőҋ@���ł��邩���������܂�.
	 * ���̃��\�b�h�ł́A���̃}�l�[�W���ɓo�^����Ă���C�x���g�̂����A�w�肵���^�̍ŏ��Ɍ��������C�x���g��
	 * isReaching��true��Ԃ����ǂ������������܂��B<br>
	 *
	 * @param <T> ��������C�x���g�̌^���w�肵�܂��B<br>
	 * @param type ��������C�x���g�̌^���w�肵�܂��B<br>
	 *
	 * @return �w�肵���^�̍ŏ��ɔ������ꂽ�C�x���g��isReaching�̌��ʂ�Ԃ��܂��B<br>
	 *
	 * @throws NotYetLoadedException �}�l�[�W�������[�h����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public final <T> boolean hasNext(Class<T> type) throws NotYetLoadedException {
		if (!isLoaded()) {
			throw new NotYetLoadedException("not yet loaded : load=[" + isLoaded() + "]");
		}
		TimeEvent<?> evt;
		for (int i = 0, size = events.size(); i < size; i++) {
			evt = events.get(i);
			if (evt.getType().equals(type) || type.isInstance(evt.getObject())) {
				if (evt.isReaching()) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * �w�肵���^�́A�ŏ��Ɍ��������C�x���g�����s�\�ł���Ύ��s���ăC�x���g�̃A�C�e����Ԃ��܂�.
	 * ���̃��\�b�h�ŕԂ����I�u�W�F�N�g�́A�������ꂽ�C�x���g��execute�̌��ʂł��B<br>
	 * ���̃��\�b�h�́A���s�ł���C�x���g�����݂��Ȃ��ꍇ��null��Ԃ��_�ɒ��ӂ��Ă��������B<br>
	 * �ʏ�́A�ȉ��̂悤��hasNext��g�ݍ��킹�Ďg�p���܂��B<br>
	 * <br>
	 * while (eventManager.hasNext(TextLabel.class)) {<br>
	 * labels.add(eventManager.execute(TextLabel.class));<br>
	 * }<br>
	 * <br>
	 *
	 * @param <T> ��������C�x���g�̌^���w�肵�܂��B<br>
	 * @param type ��������C�x���g�̌^���w�肵�܂��B<br>
	 *
	 * @return �C�x���g�����s�����ꍇ�́A���̃C�x���g��execute�̌��ʂ�Ԃ��܂��B���s����C�x���g���Ȃ��ꍇ��null��Ԃ��܂��B<br>
	 *
	 * @throws NotYetLoadedException �}�l�[�W�������[�h����Ă��Ȃ��ꍇ�ɓ������܂��B<br>
	 */
	public final <T extends Serializable> T execute(Class<T> type) throws NotYetLoadedException {
		if (!isLoaded()) {
			throw new NotYetLoadedException("not yet loaded : load=[" + isLoaded() + "]");
		}
		if (!hasNext(type)) {
			return null;
		}
		TimeEvent<T> evt = getNext(type);
		GameLog.printIfUsing(Level.INFO, "> MultiClassEventManager : execute : now=[" + getProgressTime() + "] event=[" + evt + "]");
		remove(evt);
		return evt.execute();
	}

	/**
	 * ���̃}�l�[�W���Ɋ܂܂�Ă��邷�ׂẴC�x���g���擾���܂�.
	 * ���̃��\�b�h�͎Q�Ƃ�ێ����܂��B�߂�l�ɑ΂��鑀��͂��̃}�l�[�W���ɔ��f����܂��B<br>
	 *
	 * @return �}�l�[�W���Ɍ��݊i�[����Ă���C�x���g�̃��X�g��Ԃ��܂��B<br>
	 */
	public List<TimeEvent<?>> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		return "> MultiClassEventManager{" + "load=" + isLoaded() + ", events=" + size() + ", progressTime=" + getProgressTime() + '}';
	}
}
