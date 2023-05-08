/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
