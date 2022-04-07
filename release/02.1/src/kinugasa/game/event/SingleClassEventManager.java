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
 * 単一のクラスのイベントを扱うイベントマネージャです.
 * <br>
 * このクラスは単一の型のEventの実装を時系列に発生させる仕組みを提供します。<br>
 * クラスインスタンスによって次に発生するイベントを検索する必要がないため、通常はMultiClassEventManagerよりも
 * 高速に動作します。<br>
 * <br>
 * <br>
 * イベントを登録するには、このクラスの具象クラスを定義し、initメソッド内でaddメソッドを使用してイベントを追加します。<br>
 * <br>
 * イベントを実行するにはhasNextメソッドとexecuteメソッドを利用します。<br>  {@code
 * while(manager.hasNext()){
 * items.add(manager.execute());
 * }
 * }
 *
 * @param <T> このマネージャが扱うイベントの型を指定します。<br>
 *
 * @version 1.0.0 - 2012/11/15_8:04:20<br>
 * @author Dra0211<br>
 * <br>
 *
 */
public abstract class SingleClassEventManager<T extends Serializable> extends EventManager {

	private static final long serialVersionUID = 576975168711401996L;
	/**
	 * このマネージャのイベントリストです.
	 */
	private List<TimeEvent<T>> events;

	/**
	 * 新しいイベントマネージャを構築します. このメソッドでは、イベントの初期値は32となります。<br>
	 */
	public SingleClassEventManager() {
		this(32);
	}

	/**
	 * 新しいイベントマネージャを構築します.
	 *
	 * @param initialSize イベントの初期容量を指定します。<br>
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
	 * このマネージャに新しいイベントを追加します. このメソッドは、マネージャがロード済みでも使用できます。<br>
	 * ただしその場合はイベントがソートされません。<br>
	 * ロード前の場合は、ロード時にソートされます。<br>
	 * ソートをする場合はsortメソッドを使用してください。<br>
	 *
	 * @param evt 追加するイベントを送信します。<br>
	 */
	public void add(TimeEvent<T> evt) {
		events.add(evt);
	}

	/**
	 * このマネージャに新しいイベントを追加します. このメソッドは、マネージャがロード済みでも使用できます。<br>
	 * ただしその場合はイベントがソートされません。<br>
	 * ロード前の場合は、ロード時にソートされます。<br>
	 * ソートをする場合はsortメソッドを使用してください。<br>
	 *
	 * @param evt 追加するイベントを送信します。<br>
	 */
	public void addAll(TimeEvent<T>... evt) {
		events.addAll(Arrays.asList(evt));
	}

	/**
	 * このマネージャに含まれているすべてのイベントを取得します.
	 * このメソッドは参照を保持します。戻り値に対する操作はこのマネージャに反映されます。<br>
	 *
	 * @return マネージャに現在格納されているイベントのリストを返します。<br>
	 */
	public List<TimeEvent<T>> getEvents() {
		return events;
	}

	/**
	 * イベントリストの先頭のイベントを返します. このメソッドでは発見されたイベントを実行／削除せずに返します。<br>
	 * 通常はhasNextおよびexecuteを使用してください。<br>
	 *
	 * @return イベントリストの先頭の要素をそのまま返します。isEmptyがtrueの場合はnullを返します。<br>
	 *
	 * @throws NotYetLoadedException マネージャがロードされていない場合に投げられます。<br>
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
	 * イベントリストの先頭のイベントが実行可能であれば実行してイベントのアイテムを返します.
	 * このメソッドで返されるオブジェクトは、発見されたイベントのexecuteの結果です。<br>
	 * このメソッドは、実行できるイベントが存在しない場合にnullを返す点に注意してください。<br>
	 * 通常は、以下のようにhasNextを組み合わせて使用します。<br>
	 * <br>
	 *
	 * @return
	 * イベントを実行した場合は、そのイベントのexecuteの結果を返します。実行するイベントがない場合(hasNext==false)はnullを返します。<br>
	 *
	 * @throws NotYetLoadedException マネージャがロードされていない場合に投げられます。<br>
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
	 * イベントリストの先頭のイベントが実行可能な状態で待機中であるかを検査します.
	 * このメソッドでは、このマネージャに登録されているイベントリストの最初のイベントの
	 * isReachingがtrueを返すかどうかを検査します。<br>
	 *
	 * @return イベントリストの先頭のイベントのisReachingの結果を返します。<br>
	 *
	 * @throws NotYetLoadedException マネージャがロードされていない場合に投げられます。<br>
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
