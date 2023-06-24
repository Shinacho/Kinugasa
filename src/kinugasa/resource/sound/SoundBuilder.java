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
import java.io.Serializable;
import java.util.Objects;
import kinugasa.resource.Nameable;
import kinugasa.resource.FileNotFoundException;

/**
 * キャッシュつきサウンドのインスタンスを作成するためのビルダです.
 * <br>
 * WAVEファイルを使用する際の、最も一般的な構築クラスとなります。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/13_18:48:59<br>
 * @author Shinacho<br>
 */
public final class SoundBuilder implements Serializable, Nameable {

	/**
	 * ファイルインスタンス.
	 */
	private File file;
	/**
	 * ループ設定.
	 */
	private LoopPoint loopPoint = LoopPoint.NO_USE;
	/**
	 * リバーブ設定.
	 */
	private ReverbModel reverbModel = ReverbModel.NO_USE;
	/**
	 * マスターゲインの値.
	 */
	private float masterGain = 1f;
	/**
	 * ボリューム.
	 */
	private float volume = 1f;
	/**
	 * パンの設定.
	 */
	private float pan = 0f;
	/**
	 * 再生時のサンプルレート.
	 */
	private float sampleRate = 0f;
	private String id;
	private String desc;
	private SoundType type;

	public String getVisibleName() {
		return getFile().getName();
	}

	public String getDesc() {
		return desc;
	}

	@Deprecated
	public void setDesc(String desc) {
		this.desc = desc;
	}

	public SoundType getType() {
		return type;
	}

	public static SoundBuilder create(String id, String filePath, String desc, int lpf, int lpt, float mg, SoundType type) throws FileNotFoundException {
		SoundBuilder s = new SoundBuilder();
		File soundFile = new File(filePath);
		if (!soundFile.exists()) {
			throw new FileNotFoundException("not found : filePath=[" + filePath + "]");
		}
		s.file = soundFile;
		s.id = id;
		s.desc = desc;
		s.type = type;
		if (lpf == - 1) {
			s.loopPoint = new LoopPoint(lpf, lpt);
		} else if (lpf > 0) {
			s.loopPoint = new LoopPoint(lpf, lpt);
		}
		s.masterGain = mg;
		return s;
	}

	/**
	 * ループ位置を設定します.
	 *
	 * @param loopPoint ループ位置.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setLoopPoint(LoopPoint loopPoint) {
		this.loopPoint = loopPoint;
		return this;
	}

	/**
	 * ループ位置を設定します.
	 *
	 * @param from ループ位置.<br>
	 * @param to ループ位置.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setLoopPoint(int from, int to) {
		this.loopPoint = new LoopPoint(from, to);
		return this;
	}

	/**
	 * サウンドのマスターゲインを設定します. これはボリュームがサポートされていない環境で音量を設定することができます.<br>
	 *
	 * @param masterGain ゲインの値.0.0fで無音になる.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setMasterGain(float masterGain) {
		this.masterGain = masterGain;
		return this;
	}

	/**
	 * ステレオサウンドのパン位置を設定します. この機能はサポートされていない可能性があります.
	 *
	 * @param pan 中心を0.0、左右を1.0とした場合のパン位置.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setPan(float pan) {
		this.pan = pan;
		return this;
	}

	/**
	 * サウンドの音量を設定します. この機能はサポートされていない可能性があります.音量の調節はマスターゲインを使用してください.<br>
	 *
	 * @param volume 音量.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setVolume(float volume) {
		this.volume = volume;
		return this;
	}

	/**
	 * サウンドのリバーブを設定します. この機能はサポートされていない可能性があります.
	 *
	 * @param reverbModel リバーブの設定.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setReverbModel(ReverbModel reverbModel) {
		this.reverbModel = reverbModel;
		return this;
	}

	/**
	 * サウンドの再生時のサンプルレートを設定します. この機能はサポートされていない可能性があります.
	 *
	 * @param sampleRate 再生時のサンプルレート.<br>
	 *
	 * @return このビルダのインスタンス.<br>
	 */
	@Deprecated
	public SoundBuilder setSampleRate(float sampleRate) {
		this.sampleRate = sampleRate;
		return this;
	}

	/**
	 * 作成される予定のサウンドのファイルを取得します. このメソッドの戻り値のファイルは存在が保証されます.<br>
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
	public String getName() {
		return id;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 13 * hash + Objects.hashCode(this.file);
		hash = 13 * hash + Objects.hashCode(this.loopPoint);
		hash = 13 * hash + Objects.hashCode(this.reverbModel);
		hash = 13 * hash + Float.floatToIntBits(this.masterGain);
		hash = 13 * hash + Float.floatToIntBits(this.volume);
		hash = 13 * hash + Float.floatToIntBits(this.pan);
		hash = 13 * hash + Float.floatToIntBits(this.sampleRate);
		hash = 13 * hash + Objects.hashCode(this.id);
		hash = 13 * hash + Objects.hashCode(this.desc);
		hash = 13 * hash + Objects.hashCode(this.type);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SoundBuilder other = (SoundBuilder) obj;
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
		if (!Objects.equals(this.id, other.id)) {
			return false;
		}
		if (!Objects.equals(this.desc, other.desc)) {
			return false;
		}
		if (!Objects.equals(this.file, other.file)) {
			return false;
		}
		if (!Objects.equals(this.loopPoint, other.loopPoint)) {
			return false;
		}
		if (!Objects.equals(this.reverbModel, other.reverbModel)) {
			return false;
		}
		return this.type == other.type;
	}

	@Override
	public String toString() {
		return "SoundBuilder{" + "file=" + file + ", loopPoint=" + loopPoint + ", reverbModel=" + reverbModel + ", masterGain=" + masterGain + ", volume=" + volume + ", pan=" + pan + ", sampleRate=" + sampleRate + ", id=" + id + ", desc=" + desc + ", type=" + type + '}';
	}

}
