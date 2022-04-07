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
package kinugasa.object;

import java.io.Serializable;
import kinugasa.resource.Storage;

/**
 * 複数のパラメータを1つのオブジェクトに関連付けるためのセットです.
 * <br>
 *
 * <br>
 *
 *
 * @version 1.0.0 - 2013/02/10_2:19:05<br>
 * @author Dra0211<br>
 */
public class Status extends Storage<Parameter> implements Serializable, Cloneable {


	public Status() {
	}

	public void toMinAll() {
		for (Parameter param : this) {
			param.toMin();
		}
	}

	public void toMaxAll() {
		for (Parameter param : this) {
			param.toMax();
		}
	}

	@Override
	public Status clone() {
		try {
			return (Status) super.clone();
		} catch (CloneNotSupportedException ex) {
			throw new InternalError("clone failed");
		}
	}
}
