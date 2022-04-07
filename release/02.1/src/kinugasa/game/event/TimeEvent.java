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
 * 時系列で発生するイベントの処理と対象オブジェクトを格納するクラスです.
 * <br>
 * イベントは1つの「オブジェクト」と「実行時刻」を持ちます。<br>
 * 通常、実行時刻はゲームの更新回数と同期します。この値はイベントが所属する
 * EventManagerの更新回数をもとに判定します。<br>
 * オブジェクトはイベントの実行によって返されるアイテムです。<br>
 * オブジェクトは実行前に（登録時に）そのインスタンスが存在していなければなりません。<br>
 * 実行時には、オブジェクトに対する特別な操作を行った後、それを戻すことで、
 * ゲーム内で利用できるようにします。<br>
 * <br>
 * イベントが実行できるタイミングは、イベントに設定されたEntryModelによって判断されます。<br>
 * <br>
 * <br>
 * イベントが同一であるかどうかはIDのみが評価されます。<br>
 *
 * @param <T> このイベントが扱うオブジェクトの型を指定します。<br>
 *
 * @version 1.0.0 - 2012/10/19_18:14:25.<br>
 * @author Dra0211<br>
 * <br>
 */
public abstract class TimeEvent<T> implements Comparable<TimeEvent<?>>, Serializable {

	/** イベントの時刻にこの値を指定すると、そのイベントは最初のターンで実行されます. */
	public static final long TIME_INITIAL = 0;
	/** イベントの時刻にこの値を指定すると、そのイベントは実行されません.
	 * この値は-1Lが使用されているので、イベントの時刻に-1Lを使用することはできません。 */
	public static final long TIME_NOT_EXECUTE = -1L;
	/** イベントの合計数のカウンタです. */
	private static int idCounter = 0;
	/** このイベント固有のIDです.
	 * この値は決して重複せず、イベントが作成された順に連番が降られます。 */
	private int id = ++idCounter;
	/** このイベントの実行時刻です. */
	private long executeTime;
	/** このイベントの実行モデルです. */
	private EntryModel entryModel;
	/** このイベントが使用するオブジェクトです.
	 * サブクラスから使用できます。<br> */
	protected final T object;
	/** このイベントが扱う型情報です. */
	private Class<T> type;

	/**
	 * 新しいイベントを作成します.
	 * イベントが扱うオブジェクトは、複数のイベント間で共通のインスタンスを使用することができます。<br>
	 * イベントの起動時刻に-1(TIME_NOT_EXECUTE)を指定した場合、そのイベントは実行できなくなることに注意してください。<br>
	 * 最初に起動する必要のあるイベントは0を指定します。<br>
	 *
	 * @param executeTime イベントの起動時刻を指定します。この値は通常、マネージャの更新回数によって判定されます。<br>
	 *                       たとえば、FPSが60では、開始からおよそ1秒後に時刻60となります。<br>
	 * @param entryModel  このイベントの実行判定機能を定義します。このモデルを使用して、「フィールドに敵がいる場合は出現しない」
	 *                       等の条件を判定することができます。<br>
	 * @param obj         イベントが使用するオブジェクトを指定します。オブジェクトは解放可能な場合は、ロードせずに送信することができます。<br>
	 *
	 * @throws IllegalArgumentException 起動時刻がTIME_NOT_EXECUTE(-1)より小さい場合に投げられます。<br>
	 * @throws NullPointerException     使用するオブジェクトがnullの場合に投げられます。<br>
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
	 * このイベントのIDを取得します.
	 * IDはイベントの作成順に連番として割り当てられます。<br>
	 * 他のマネージャのイベントであっても重複することはありません。<br>
	 *
	 * @return このイベントのIDを返す。<br>
	 */
	public final int getId() {
		return id;
	}

	/**
	 * 作成されたイベントの合計数を取得します.
	 *
	 * @return 作成済みのイベントの数.<Br>
	 */
	public static int getEventsNum() {
		return idCounter;
	}

	/**
	 * このイベントのエントリモデルを設定します.
	 *
	 * @param entryModel 新しいエントリモデル。<br>
	 */
	public final void setEntryModel(EntryModel entryModel) {
		this.entryModel = entryModel;
	}

	/**
	 * このイベントに設定されているエントリモデルを取得します.
	 *
	 * @return エントリモデル。<br>
	 */
	public final EntryModel getEntryModel() {
		return entryModel;
	}

	/**
	 * このイベントが現時点で実行可能であるかをエントリモデルによって評価します.
	 *
	 * @return このイベントが現時点で実行できる場合はtrue、そうでない場合はfalseを返す。<br>
	 */
	public final boolean isReaching() {
		return executeTime == TIME_NOT_EXECUTE ? false : entryModel.isReaching(this);
	}

	/**
	 * このイベントの実行時刻を取得します.
	 *
	 * @return このイベントの実行時刻。<br>
	 */
	public final long getExecuteTime() {
		return executeTime;
	}

	/**
	 * イベントを時刻の昇順にソートするための比較機能です.
	 * このメソッドは別の型のイベントであっても比較できます。<br>
	 *
	 * @param o 比較するイベント。<br>
	 *
	 * @return Comparebleの実装に基づく値を返す。<br>
	 */
	@Override
	public final int compareTo(TimeEvent<?> o) {
		return executeTime > o.executeTime ? 1 : executeTime < o.executeTime ? -1 : 0;
	}

	/**
	 * このイベントが保有しているオブジェクトを取得します.
	 *
	 * @return このイベントによって使用されるオブジェクトを返す。<br>
	 */
	public final T getObject() {
		return object;
	}

	/**
	 * このイベントが扱う型を返します.
	 *
	 * @return このイベントが使用する型。<br>
	 */
	public final Class<T> getType() {
		return type;
	}

	/**
	 * このイベントを実行します.
	 * 実行後、状態が変更されたオブジェクトを戻す必要があります。<br>
	 *
	 * @return このイベントによって状態が変更されたオブジェクトを戻す。<br>
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