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

import kinugasa.resource.InputObjectStorage;
import kinugasa.resource.Input;
import kinugasa.resource.InputStatus;


/**
 * ロジックを跨いでサウンドを管理するための、唯一の保存領域を提供します.
 * <br>
 * サウンドマップには、全てのサウンドマップが含まれています。サウンドマップからサウンドを構築した場合は、
 * このストレージからすべてのサウンドにアクセスできます。<br>
 * <br>
 * Freeableの実装は、マップに追加されているすべてのサウンドに行われます。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_14:19:07<br>
 * @author Shinacho<br>
 */
public final class SoundStorage extends InputObjectStorage<SoundMap> implements Input{

	/** このクラスの唯一のインスタンスです . */
	private static final SoundStorage INSTANCE = new SoundStorage();

	/**
	 * サウンドストレージのインスタンスを取得します.
	 *
	 * @return 唯一のインスタンスを返します。<br>
	 */
	public static SoundStorage getInstance() {
		return INSTANCE;
	}

	/**
	 * シングルトンクラスです.
	 */
	private SoundStorage() {
	}

	@Override
	public void dispose() {
		super.dispose();
		System.gc();
	}

	@Override
	public InputStatus getStatus() {
		return isEmpty() ? InputStatus.NOT_LOADED : asList().get(0).getStatus();
	}

}
