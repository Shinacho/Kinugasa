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

import java.io.File;
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

	public File getFile();

	public String getDesc();

	public String getFileName();

	public SoundType getType();

}
