/*
 * Copyright (C) 2023 Shinacho
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package kinugasa.util;

import kinugasa.object.Model;
import kinugasa.object.Statable;

/**
 * 経過時間を計測し、条件判定をするための機能を定義します.
 * <br>
 * ほとんどのカウンタの実装では、isReachingがtrueを返す場合、resetを自動的にコールすることで、 次の呼び出しに備えます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/11_17:07:17.<br>
 * @author Shinacho.<br>
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

	public abstract int getCurrentTime();

	/**
	 * 検査の状態を初期化します. このメソッドを呼び出すことで、最初に設定された時間や状態を 復元し、再度検査可能にします。<br>
	 */
	public abstract void reset();
	/**
	 * このモデルは、常に"true"を返します. このモデルをクローニングする意味はありません。<br>
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

		@Override
		public int getCurrentTime() {
			return 0;
		}

	};
	/**
	 * このモデルは、常に"false"を返します. このモデルをクローニングする意味はありません。<br>
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

		@Override
		public int getCurrentTime() {
			return Integer.MAX_VALUE;
		}

	};

	public static TimeCounter oneCounter() {
		return new TimeCounter() {
			private int v = 0;

			@Override
			public boolean isReaching() {
				v--;
				return v < 0;
			}

			@Override
			public boolean isEnded() {
				return v < 0;
			}

			@Override
			public boolean isRunning() {
				return true;
			}

			@Override
			public void reset() {
				v = 1;
			}

			@Override
			public int getCurrentTime() {
				return v;
			}
			
		};
	}
}
