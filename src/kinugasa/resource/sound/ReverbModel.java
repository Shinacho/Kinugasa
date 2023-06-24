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
package kinugasa.resource.sound;

import java.io.Serializable;

/**
 * サウンドのリバーブの設定をカプセル化します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:48:17<br>
 * @author Shinacho<br>
 */
public class ReverbModel implements Serializable {

	/**
	 * リバーブを使用しない設定です.
	 */
	public static ReverbModel NO_USE = new ReverbModel(false, 0, 0);
	//
	//
	/** リバーブを使用するかどうかのフラグ. */
	private boolean use;
	/** 送信側リバーブ. */
	private float send;
	/** 受信側リバーブ. */
	private float ret;

	public ReverbModel(boolean use, float send, float ret) {
		this.use = use;
		this.send = send;
		this.ret = ret;
	}

	/**
	 * 受信側のリバーブを取得します.
	 *
	 * @return 受信側のリバーブ値を返します。<br>
	 */
	public float getRet() {
		return ret;
	}

	/**
	 * 送信側のリバーブを取得します.
	 *
	 * @return 送信側のリバーブ値を返します。<br>
	 */
	public float getSend() {
		return send;
	}

	/**
	 * リバーブを使用するかどうかを決定するフラグを取得します.
	 * @return リバーブの有効フラグを返します。trueのとき使用します。<br>
	 */
	public boolean isUse() {
		return use;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ReverbModel other = (ReverbModel) obj;
		if (this.use != other.use) {
			return false;
		}
		if (Float.floatToIntBits(this.send) != Float.floatToIntBits(other.send)) {
			return false;
		}
		if (Float.floatToIntBits(this.ret) != Float.floatToIntBits(other.ret)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 29 * hash + (this.use ? 1 : 0);
		hash = 29 * hash + Float.floatToIntBits(this.send);
		hash = 29 * hash + Float.floatToIntBits(this.ret);
		return hash;
	}
}
