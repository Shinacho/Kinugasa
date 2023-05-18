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

import kinugasa.resource.Input;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.NotYetLoadedException;

/**
 * サウンドファイルの再生や停止などの機能を抽象化するためのインターフェースです.
 * <br>
 * 全てのサウンドデータは、このインターフェースを実装する必要があります。<br>
 * 通常の実装では、ロードされていないサウンドに対する操作は、何も行いません。<br>
 * また、すでにロードされている場合に再度ロードすることはありません。<br>
 * <br>
 * サウンドの名前とは、通常は、パスを除いたファイル名となります。<br>
 * たとえば、hoge/piyo/fuga.wavの場合はfuga.wavが名前となります。<br>
 * <br>
 *
 *
 * @version 1.0.0 - 2013/01/13_18:44:36<br>
 * @author Shinacho<br>
 */
public interface Sound extends Input<Sound>, Nameable {

	@Override
	public String getName();

	/**
	 * サウンドの再生を開始します. サウンドがループ機能をサポートしている場合は、設定によってはループ再生を開始します.<Br>
	 * サウンドが既に再生されている場合は何もしません.<br>
	 *
	 * @throws NotYetLoadedException サウンドがロードされていない場合に投げることができます。<br>
	 */
	public void play() throws NotYetLoadedException;
	
	public void nonLoopPlay();

	public void stopAndPlay();

	/**
	 * サウンドの現在のフレーム位置を返します.
	 *
	 * @return 現在のフレーム位置.<br>
	 */
	public long getFramePosition();

	/**
	 * このサウンドのフレームの最大長を返します.
	 *
	 * @return フレームの最大長.<br>
	 */
	public long getFrameLength();

	/**
	 * サウンドの再生を停止して最初まで巻き戻します.
	 */
	public void stop();

	/**
	 * サウンドの再生を一時停止します. 次回playが呼び出されたとき、pauseが呼ばれた位置から再生します.<br>
	 */
	public void pause();

	public default void switchPause() {
		if (isPlaying()) {
			pause();
		} else {
			play();
		}
	}

	@Override
	public InputStatus getStatus();

	public boolean isPlaying();

	/**
	 * サウンドの音量を設定します.
	 *
	 * @param vol 新しい音量.0fで無音になる.<br>
	 */
	public void setVolume(float vol);

	@Override
	public Sound load();

	@Override
	public void dispose();

}
