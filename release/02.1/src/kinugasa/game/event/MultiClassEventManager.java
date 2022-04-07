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
 * 複数のクラスのイベントを扱うイベントマネージャです.
 * <br>
 * このクラスは複数の型のEventの実装を時系列に発生させる仕組みを提供します。<br>
 * クラスインスタンスによって、次に発生するイベントを検索する必要があるため、通常はSingleClassEventManagerよりも低速です。<br>
 * <br>
 * イベントを登録するには、このクラスの具象クラスを定義し、initメソッド内でaddメソッドを使用してイベントを追加します。<br>
 * <br>
 * イベントを実行するにはhasNextメソッドとexecuteメソッドを利用します。<br>
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
	/** このマネージャのイベントリストです. */
	private List<TimeEvent<?>> events;

	/**
	 * 新しいイベントマネージャを構築します.
	 * このメソッドでは、イベントの初期値は32となります。<br>
	 */
	public MultiClassEventManager() {
		this(32);
	}

	/**
	 * 新しいイベントマネージャを構築します.
	 *
	 * @param initialSize イベントの初期容量を指定します。<br>
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
	 * このマネージャに新しいイベントを追加します.
	 * 追加するイベントの型は任意です。<br>
	 * このメソッドは、マネージャがロード済みでも使用できます。<br>
	 * ただしその場合はイベントがソートされません。<br>
	 * ロード前の場合は、ロード時にソートされます。<br>
	 * ソートをする場合はsortメソッドを使用してください。<br>
	 *
	 * @param e 追加するイベントを送信します。<br>
	 */
	public final void add(TimeEvent<?> e) {
		events.add(e);
	}

	/**
	 * このマネージャに新しいイベントを追加します.
	 * 追加するイベントの型は任意です。<br>
	 * このメソッドは、マネージャがロード済みでも使用できます。<br>
	 * ただしその場合はイベントがソートされません。<br>
	 * ロード前の場合は、ロード時にソートされます。<br>
	 * ソートをする場合はsortメソッドを使用してください。<br>
	 *
	 * @param e 追加するイベントを送信します。<br>
	 */
	public final void addAll(TimeEvent<?>... e) {
		events.addAll(Arrays.asList(e));
	}

	/**
	 * 指定した型の、最初に見つかったイベントを返します.
	 * このメソッドでは発見されたイベントを実行／削除せずに返します。<br>
	 * 通常はhasNextおよびexecuteを使用してください。<br>
	 *
	 * @param <T> 検索するイベントの型を指定します。<br>
	 * @param type 検索するイベントの型を指定します。<br>
	 *
	 * @return 指定した型のイベントで最初に見つかったものをそのまま返します。<br>
	 *
	 * @throws NotYetLoadedException マネージャがロードされていない場合に投げられます。<br>
	 * @throws NameNotFoundException 指定された型のイベントがこのマネージャに含まれていない場合に投げられます。<br>
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
	 * 指定した型のイベントが、実行可能な状態で待機中であるかを検査します.
	 * このメソッドでは、このマネージャに登録されているイベントのうち、指定した型の最初に見つかったイベントの
	 * isReachingがtrueを返すかどうかを検査します。<br>
	 *
	 * @param <T> 検索するイベントの型を指定します。<br>
	 * @param type 検索するイベントの型を指定します。<br>
	 *
	 * @return 指定した型の最初に発見されたイベントのisReachingの結果を返します。<br>
	 *
	 * @throws NotYetLoadedException マネージャがロードされていない場合に投げられます。<br>
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
	 * 指定した型の、最初に見つかったイベントが実行可能であれば実行してイベントのアイテムを返します.
	 * このメソッドで返されるオブジェクトは、発見されたイベントのexecuteの結果です。<br>
	 * このメソッドは、実行できるイベントが存在しない場合にnullを返す点に注意してください。<br>
	 * 通常は、以下のようにhasNextを組み合わせて使用します。<br>
	 * <br>
	 * while (eventManager.hasNext(TextLabel.class)) {<br>
	 * labels.add(eventManager.execute(TextLabel.class));<br>
	 * }<br>
	 * <br>
	 *
	 * @param <T> 検索するイベントの型を指定します。<br>
	 * @param type 検索するイベントの型を指定します。<br>
	 *
	 * @return イベントを実行した場合は、そのイベントのexecuteの結果を返します。実行するイベントがない場合はnullを返します。<br>
	 *
	 * @throws NotYetLoadedException マネージャがロードされていない場合に投げられます。<br>
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
	 * このマネージャに含まれているすべてのイベントを取得します.
	 * このメソッドは参照を保持します。戻り値に対する操作はこのマネージャに反映されます。<br>
	 *
	 * @return マネージャに現在格納されているイベントのリストを返します。<br>
	 */
	public List<TimeEvent<?>> getEvents() {
		return events;
	}

	@Override
	public String toString() {
		return "> MultiClassEventManager{" + "load=" + isLoaded() + ", events=" + size() + ", progressTime=" + getProgressTime() + '}';
	}
}
