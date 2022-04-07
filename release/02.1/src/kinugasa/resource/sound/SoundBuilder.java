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
package kinugasa.resource.sound;

import java.io.File;
import java.io.Serializable;
import kinugasa.resource.Nameable;
import kinugasa.resource.text.FileNotFoundException;

/**
 * キャッシュつきサウンドのインスタンスを作成するためのビルダです.
 * <br>
 * WAVEファイルを使用する際の、最も一般的な構築クラスとなります。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:48:59<br>
 * @author Dra0211<br>
 */
public final class SoundBuilder implements Serializable, Nameable {

	/** ファイルインスタンス. */
	private File file;
	/** ループ設定. */
	private LoopPoint loopPoint = LoopPoint.NO_USE;
	/** リバーブ設定. */
	private ReverbModel reverbModel = ReverbModel.NO_USE;
	/** マスターゲインの値. */
	private float masterGain = 1f;
	/** ボリューム. */
	private float volume = 1f;
	/** パンの設定. */
	private float pan = 0f;
	/** 再生時のサンプルレート. */
	private float sampleRate = 0f;
	/** 新規インスタンスで作成するか. */
	private boolean newFile = false;
	private String name;

	/**
	 * 新しいCachedSoundを作成するためのビルダを構築します.
	 * 
	 * @param filePath ファイルパス.<br>
	 *
	 * @throws ContentsFileNotFoundException ファイルが存在しない場合に投げられる.<br>
	 */
	public SoundBuilder(String filePath) throws FileNotFoundException {
		File soundFile = new File(filePath);
		if (!soundFile.exists()) {
			throw new FileNotFoundException("not found : filePath=[" + filePath + "]");
		}
		this.file = soundFile;
		this.name = file.getName();
	}

	/**
	 * ループ位置を設定します.
	 * 
	 * @param loopPoint ループ位置.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setLoopPoint(LoopPoint loopPoint) {
		this.loopPoint = loopPoint;
		return this;
	}

	/**
	 * ループ位置を設定します.
	 * 
	 * @param from ループ位置.<br>
	 * @param to   ループ位置.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setLoopPoint(int from, int to) {
		this.loopPoint = new LoopPoint(from, to);
		return this;
	}

	/**
	 * サウンドのマスターゲインを設定します.
	 * これはボリュームがサポートされていない環境で音量を設定することができます.<br>
	 * 
	 * @param masterGain ゲインの値.0.0fで無音になる.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setMasterGain(float masterGain) {
		this.masterGain = masterGain;
		return this;
	}

	/**
	 * ステレオサウンドのパン位置を設定します.
	 * この機能はサポートされていない可能性があります.
	 * 
	 * @param pan 中心を0.0、左右を1.0とした場合のパン位置.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setPan(float pan) {
		this.pan = pan;
		return this;
	}

	/**
	 * サウンドの音量を設定します.
	 * この機能はサポートされていない可能性があります.音量の調節はマスターゲインを使用してください.<br>
	 * 
	 * @param volume 音量.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setVolume(float volume) {
		this.volume = volume;
		return this;
	}

	/**
	 * サウンドのリバーブを設定します.
	 * この機能はサポートされていない可能性があります.
	 * 
	 * @param reverbModel リバーブの設定.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setReverbModel(ReverbModel reverbModel) {
		this.reverbModel = reverbModel;
		return this;
	}

	/**
	 * サウンドの再生時のサンプルレートを設定します.
	 * この機能はサポートされていない可能性があります.
	 * 
	 * @param sampleRate 再生時のサンプルレート.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
		return this;
	}

	/**
	 * このメソッドを呼び出すとCachedSoundのキャッシュデータを使用せず、
	 * 新しいサウンドインスタンスを作成します.<br>
	 * 
	 * @return このビルダのインスタンス.<br>
	 */
	public SoundBuilder newFile() {
		newFile = true;
		return this;
	}

	public SoundBuilder setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 作成される予定のサウンドのファイルを取得します.
	 * このメソッドの戻り値のファイルは存在が保証されます.<br>
	 * 
	 * @return ファイルインスタンス.<br>
	 */
	public File getFile() {
		return file;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return ループ位置.<br>
	 */
	public LoopPoint getLoopPoint() {
		return loopPoint;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return ゲインの値.<br>
	 */
	public float getMasterGain() {
		return masterGain;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return キャッシュを使用せずに新しいインスタンスを作成する場合はTRUEを返す.<br>
	 */
	public boolean isNewFile() {
		return newFile;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return パンの設定.<br>
	 */
	public float getPan() {
		return pan;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return リバーブの設定.<br>
	 */
	public ReverbModel getReverbModel() {
		return reverbModel;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return 再生時のサンプルレート.<br>
	 */
	public float getSampleRate() {
		return sampleRate;
	}

	/**
	 * 設定されている値を返します.
	 * 
	 * @return 音量.<br>
	 */
	public float getVolume() {
		return volume;
	}

	/**
	 * 現在の設定で新しいCachedSoundを作成します.
	 * 
	 * @return AudioDataの実装を返す.<br>
	 */
	public CachedSound builde() {
		return CachedSound.create(this);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SoundBuilder other = (SoundBuilder) obj;
		if (this.file != other.file && (this.file == null || !this.file.equals(other.file))) {
			return false;
		}
		if (this.loopPoint != other.loopPoint && (this.loopPoint == null || !this.loopPoint.equals(other.loopPoint))) {
			return false;
		}
		if (this.reverbModel != other.reverbModel && (this.reverbModel == null || !this.reverbModel.equals(other.reverbModel))) {
			return false;
		}
		if (Float.floatToIntBits(this.masterGain) != Float.floatToIntBits(other.masterGain)) {
			return false;
		}
		if (Float.floatToIntBits(this.volume) != Float.floatToIntBits(other.volume)) {
			return false;
		}
		if (Float.floatToIntBits(this.pan) != Float.floatToIntBits(other.pan)) {
			return false;
		}
		if (Float.floatToIntBits(this.sampleRate) != Float.floatToIntBits(other.sampleRate)) {
			return false;
		}
		if (this.newFile != other.newFile) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 97 * hash + (this.file != null ? this.file.hashCode() : 0);
		hash = 97 * hash + (this.loopPoint != null ? this.loopPoint.hashCode() : 0);
		hash = 97 * hash + (this.reverbModel != null ? this.reverbModel.hashCode() : 0);
		hash = 97 * hash + Float.floatToIntBits(this.masterGain);
		hash = 97 * hash + Float.floatToIntBits(this.volume);
		hash = 97 * hash + Float.floatToIntBits(this.pan);
		hash = 97 * hash + Float.floatToIntBits(this.sampleRate);
		hash = 97 * hash + (this.newFile ? 1 : 0);
		return hash;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String toString() {
		return "SoundBuilder{" + "file=" + file + ", loopPoint=" + loopPoint + ", reverbModel=" + reverbModel + ", masterGain=" + masterGain + ", volume=" + volume + ", pan=" + pan + ", sampleRate=" + sampleRate + ", newFile=" + newFile + '}';
	}
}
