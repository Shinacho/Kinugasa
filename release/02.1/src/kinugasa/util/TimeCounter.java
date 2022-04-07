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
package kinugasa.util;

import kinugasa.object.Model;
import kinugasa.object.Statable;


/**
 * 経過時間を計測し、条件判定をするための機能を定義します.
 * <br>
 * ほとんどのカウンタの実装では、isReachingがtrueを返す場合、resetを自動的にコールすることで、
 * 次の呼び出しに備えます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/11_17:07:17.<br>
 * @author Dra0211.<br>
 */
public abstract class TimeCounter extends Model implements Statable {

	private static final long serialVersionUID = -7448317548464780917L;

	/**
	 * 設定された時間が経過したかを検査します.
	 *
	 * @return 設定された時間が経過した場合にtrue、そうでない場合にfalseを返します.<br>
	 */
	public abstract boolean isReaching();

	@Override
	public TimeCounter clone() {
		return (TimeCounter) super.clone();
	}

	@Override
	public abstract boolean isEnded();

	@Override
	public abstract boolean isRunning();

	/**
	 * 検査の状態を初期化します.
	 * このメソッドを呼び出すことで、最初に設定された時間や状態を
	 * 復元し、再度検査可能にします。<br>
	 */
	public abstract void reset();
	/**
	 * このモデルは、常に"true"を返します.
	 * このモデルをクローニングする意味はありません。<br>
	 */
	public static final TimeCounter TRUE = new TimeCounter() {
		private static final long serialVersionUID = -8736923481731071986L;

		@Override
		public boolean isReaching() {
			return true;
		}

		@Override
		public boolean isEnded() {
			return false;
		}

		@Override
		public boolean isRunning() {
			return true;
		}

		@Override
		public void reset() {
		}
	};
	/**
	 * このモデルは、常に"false"を返します.
	 * このモデルをクローニングする意味はありません。<br>
	 */
	public static final TimeCounter FALSE = new TimeCounter() {
		private static final long serialVersionUID = -8736923481731071986L;

		@Override
		public boolean isReaching() {
			return false;
		}

		@Override
		public boolean isEnded() {
			return false;
		}

		@Override
		public boolean isRunning() {
			return true;
		}

		@Override
		public void reset() {
		}
	};
}
