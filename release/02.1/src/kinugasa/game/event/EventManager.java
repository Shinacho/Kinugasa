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
 * 時系列イベントを処理する仕組みの基本的な機能を定義します.
 * <br>
 * 実際に発生するイベント[ Eventクラス ]はマネージャの実装によって扱う型が異なるため、
 * イベントを追加する機能はこのクラスには定義されていません。<br>
 フレームワークでは、基本的な実装として、TimeEvent＜T＞を扱うMultiClassEventManagerと、
 単一の型のEventを扱うSIngleClassEventManagerを用意しています。<br>
 * <br>
 * <br>
 * マネージャが使用するイベントはリストまたはキューとして実装されます。<br>
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
	 * マネージャの更新回数です。イベントの発生タイミングの検査に使用されます.
	 */
	private transient long progressTime = 0L;
	/**
	 * ロードされているかどうかのフラグです.
	 */
	private transient boolean load = false;
	/**
	 * このマネージャの時刻とイベントの時刻を比較する最も一般的な実装です.
	 */
	protected final EntryModel TIME_BASE_ENTRY_MODEL = new EntryModel() {
		private static final long serialVersionUID = -3986406384609892814L;

		@Override
		public boolean isReaching(TimeEvent<?> evt) {
			return evt.getExecuteTime() > getProgressTime();
		}
	};

	/**
	 * マネージャを構築します. 通常は、イベントを格納するリストやキューを初期化します。<br>
	 */
	public EventManager() {
	}

	/**
	 * イベントマネージャの内部時間を更新します. このメソッドは毎ターン（もしくは何回かに1回）呼び出し続ける必要があります。<br>
	 * このメソッドによってこのイベントマネージャの内部時刻が更新され、 新しいイベントが起動できるようになります。<br>
	 */
	public void update() {
		progressTime++;
	}

	/**
	 * このイベントマネージャの更新回数を取得します. 更新回数はイベントが発生する"時刻"を表します。<bR>
	 * 通常、更新時刻が、イベントの実行時刻よりも大きくなったとき、イベントが起動されます。<br>
	 * この時刻はただのカウンタであり、updateを呼び出した回数が保管されます。<br>
	 * 毎フレーム更新することで、時刻のカウンタは総フレーム数と同期します。<br>
	 *
	 * @return このイベントマネージャの更新回数を返します。<br>
	 */
	public long getProgressTime() {
		return progressTime;
	}

	public EventManager load() {
		init();
		sort();
		if (GameLog.isUsingLog()) {
			GameLog.printInfo("EventManagerのロードが開始した");
			printAll();
			GameLog.printInfo("EventManagerのロードが完了した");
		}
		load = true;
		return this;
	}

	/**
	 * マネージャに追加されているイベントを初期化します. このメソッドはloadメソッドをコールすると自動的に呼ばれます。<br>
	 * 具象クラスでは、addメソッドを使用してイベントを追加する処理を記述する 必要があります。<br>
	 */
	protected abstract void init();

	public EventManager free() {
		progressTime = 0L;
		clear();
		load = false;
		return this;
	}

	/**
	 * ストリームにイベントの情報を発行します. このメソッドはデバッグ用です。loadメソッドによって実行されます。<br>
	 */
	public abstract void printAll();

	public boolean isLoaded() {
		return load;
	}

	/**
	 * マネージャに追加されているイベントを、時系列に沿ってソートします.
	 */
	public abstract void sort();

	/**
	 * マネージャに追加されており、まだ破棄されていないイベントの数を取得します.
	 *
	 * @return 追加ずみイベントの数を返します。<br>
	 */
	public abstract int size();

	/**
	 * マネージャの破棄されていないイベントの数が0個であるかを検査します.
	 *
	 * @return マネージャにイベントがない場合はtrue、1個以上のイベントが待機中である場合はfalseを返します。<br>
	 */
	public abstract boolean isEmpty();

	/**
	 * マネージャに追加されているすべてのイベントを破棄します.
	 */
	public abstract void clear();

	/**
	 * マネージャにイベントevtが含まれているかを調べます.
	 *
	 * @param evt 検査するイベントを送信します。<br>
	 *
	 * @return evtがイベントリストに含まれている場合はtrueを返します。<br>
	 */
	public abstract boolean contains(TimeEvent<?> evt);

	/**
	 * マネージャにイベントevtが含まれていれば削除します. このメソッドは実行したイベントを破棄するためにも使用されます。<br>
	 * evtが含まれていない場合は何も行いません。<br>
	 *
	 * @param evt 削除するイベントを送信します。<br>
	 */
	public abstract void remove(TimeEvent<?> evt);

	/**
	 * マネージャにイベントevtが含まれていれば削除します. このメソッドは実行したイベントを破棄するためにも使用されます。<br>
	 * evtが含まれていない場合は何も行いません。<br>
	 *
	 * @param evt 削除するイベントを送信します。<br>
	 */
	public abstract void removeAll(TimeEvent<?>... evt);

	@Override
	public String toString() {
		return "EventManager{" + "progressTime=" + progressTime + ", load=" + load + '}';
	}
}
