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
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.BooleanControl;
import javax.sound.sampled.Clip;
import javax.sound.sampled.Control;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.FloatControl;
import kinugasa.game.GameLog;
import kinugasa.game.system.Counts;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.GameSystemI18NKeys;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.InputStatus;
import kinugasa.resource.NotYetLoadedException;
import kinugasa.resource.db.DBConnection;
import kinugasa.util.StopWatch;

/**
 * 内部キャッシュつきのサウンドの実装です.
 * <br>
 * WAVEファイルを再生する際の、最も一般的なサウンドの実装となります。<br>
 *
 * <br>
 *
 * @version 2.0.0 - 2013/01/13_18:46:43<br>
 * @author Shinacho<br>
 */
public class CachedSound implements Sound {

	/**
	 * 新しいキャッシュサウンドを構築します.
	 *
	 * @param b サウンドの構築に使用するビルダ.<br>
	 *
	 * @return ビルダの設定で作成されたキャッシュサウンド.<br>
	 */
	static CachedSound create(SoundBuilder b) {
		CachedSound sc = new CachedSound(b);
		return sc;
	}
	/**
	 * このサウンドを構築したビルダ.
	 */
	private SoundBuilder builder;
	/**
	 * このサウンドのストリーム.
	 */
	private transient Clip clip;
	/**
	 * このサウンドのループ設定.
	 */
	private LoopPoint lp;

	/**
	 * サウンドを作成. このコンストラクタでは、f.existの検査はしない.
	 *
	 * @param builder ファイルインスタンス.<br>
	 */
	private CachedSound(SoundBuilder builder) {
		this.builder = builder;
		if (Float.compare(builder.getMasterGain(), 1.0f) != 0) {
			setControl(FloatControl.Type.MASTER_GAIN, (float) Math.log10(builder.getMasterGain()) * 20);
		}
		if (Float.compare(builder.getVolume(), 1.0f) != 0) {
			setControl(FloatControl.Type.VOLUME, builder.getVolume());
		}
		if (Float.compare(builder.getPan(), 0f) != 0) {
			setControl(FloatControl.Type.PAN, builder.getPan());
		}
		if (!builder.getReverbModel().equals(ReverbModel.NO_USE)) {
			setControl(FloatControl.Type.REVERB_RETURN, builder.getReverbModel().getRet());
			setControl(FloatControl.Type.REVERB_SEND, builder.getReverbModel().getSend());
			setControl(BooleanControl.Type.APPLY_REVERB, builder.getReverbModel().isUse() ? 1f : 0f);
		}
		if (builder.getLoopPoint() != null && !builder.getLoopPoint().equals(LoopPoint.NO_USE)) {
			setLoopPoints(builder.getLoopPoint());
		}
		if (Float.compare(builder.getSampleRate(), 0f) != 0) {
			setControl(FloatControl.Type.SAMPLE_RATE, builder.getSampleRate());
		}
	}
	/**
	 * ロード時に適用するコントロール.
	 */
	private HashMap<Control.Type, Float> ctrls = new HashMap<Control.Type, Float>(8);

	/**
	 * コントロールをバッファリングする.
	 *
	 * @param t コントロールのタイプ.<br>
	 * @param val 値.<br>
	 */
	private void setControl(Control.Type t, float val) {
		ctrls.put(t, val);
	}

	/**
	 * ループ位置を設定します.
	 *
	 * @param p ループ位置.<br>
	 */
	private void setLoopPoints(LoopPoint p) {
		this.lp = p;
	}

	@Override
	public void setVolume(float vol) {
		FloatControl control = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
		control.setValue((float) Math.log10(vol) * 20);
	}

	private boolean playing = false;

	@Override
	public void play() throws NotYetLoadedException {
		if (!(getStatus() == InputStatus.LOADED)) {
			//暫定対応
//			throw new NotYetLoadedException("sound " + this + " is not yet loaded.");
			return;
		}
		if (lp != LoopPoint.NO_USE) {
			if (framePos > 0) {
				clip.setFramePosition(framePos);
				framePos = 0;
			}
			if (getType() == SoundType.BGM) {
				if (DBConnection.getInstance().isUsing()) {
					Counts.getInstance().add1count(GameSystemI18NKeys.CountKey.BGM再生回数);
				}
			}
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			if (framePos > 0) {
				clip.setFramePosition(framePos);
				framePos = 0;
				clip.start();
				if (getType() == SoundType.BGM) {
					if (DBConnection.getInstance().isUsing()) {
						Counts.getInstance().add1count(GameSystemI18NKeys.CountKey.BGM再生回数);
					}
				}
			} else {
				clip.start();
				if (getType() == SoundType.BGM) {
					if (DBConnection.getInstance().isUsing()) {
						Counts.getInstance().add1count(GameSystemI18NKeys.CountKey.BGM再生回数);
					}
				}
			}
		}
		playing = true;
	}

	@Override
	public void nonLoopPlay() {
		if (!(getStatus() == InputStatus.LOADED)) {
			throw new NotYetLoadedException("sound " + this + " is not yet loaded.");
		}
		clip.start();
		playing = true;
	}

	@Override
	public long getFramePosition() {
		return clip == null ? -1 : clip.getLongFramePosition();
	}

	@Override
	public long getFrameLength() {
		return clip == null ? -1 : clip.getFrameLength();
	}

	@Override
	public void stop() {
		if (clip != null) {
			clip.stop();
			clip.setFramePosition(0);
			playing = false;
		}
	}

	@Override
	public boolean isPlaying() {
		if (clip == null) {
			return false;
		}
		if (clip.getFramePosition() <= 0 || clip.getFramePosition() > clip.getFrameLength()) {
			return false;
		}
		return playing;
	}

	private int framePos = 0;

	@Override
	public void pause() {
		if (clip != null) {
			framePos = clip.getFramePosition();
			clip.stop();
			playing = false;
		}
	}

	@Override
	public InputStatus getStatus() {
		return clip == null ? InputStatus.NOT_LOADED : InputStatus.LOADED;
	}

	@Override
	public CachedSound load() throws ContentsIOException, SoundStreamException {
		if (getStatus() == InputStatus.LOADED) {
			return this;
		}
		StopWatch watch = new StopWatch().start();
		AudioInputStream stream = null;
		try {
			stream = AudioSystem.getAudioInputStream(builder.getFile());
			DataLine.Info dInfo = new DataLine.Info(Clip.class, stream.getFormat());
			clip = (Clip) AudioSystem.getLine(dInfo);
			clip.open(stream);
			Set<Control.Type> types = ctrls.keySet();
			for (Control.Type t : types) {
				try {
					float val = ctrls.get(t);
					FloatControl control = (FloatControl) clip.getControl(t);
					control.setValue(val);
				} catch (IllegalArgumentException i) {
					GameLog.print("! > CachedSound : [" + getName() + "] : UN SUPPORTED CONTROL : Type=[" + t + "]");
				}
			}
			if (lp != null) {
				clip.setLoopPoints(lp.getTo(), lp.getFrom());
			}
		} catch (Exception ex) {
//			throw new SoundStreamException(ex);

		} finally {
			try {
				if (stream != null) {
					stream.close();
				}
			} catch (java.io.IOException ex) {
				throw new ContentsIOException(ex);
			}
		}
		watch.stop();
		if (GameSystem.isDebugMode()) {
			GameLog.print("CachedSound is loaded name=[" + getName() + "](" + watch.getTime() + " ms)");
		}
		return this;
	}

	public Clip getClip() {
		return clip;
	}

	@Override
	public synchronized void dispose() {
		if (getStatus() == InputStatus.NOT_LOADED) {
			return;
		}
		stop();
		if (clip != null) {
			clip.flush();
			clip.close();
		}
		clip = null;
		if (GameSystem.isDebugMode()) {
			GameLog.print("CachedSound : [" + getName() + "] : is disposed");
		}
	}

	@Override
	public String toString() {
		return "CachedSound{" + "name=" + getName() + ", lp=" + lp + ", run=" + getStatus() + '}';
	}

	@Override
	public String getName() {
		return builder.getName();
	}

	@Override
	public void stopAndPlay() {
		stop();
		play();
	}

	/**
	 * このサウンドを構築したビルダを取得します. ビルダへの設定は、意味がありません。<br>
	 *
	 * @return このサウンドを構築するために作成されたビルダのインスタンスを返します。<br>
	 */
	public SoundBuilder getBuilder() {
		return builder;
	}

	@Override
	public File getFile() {
		return builder.getFile();
	}

	@Override
	public String getDesc() {
		return builder.getDesc();
	}

	@Override
	public String getFileName() {
		return getFile().getName();
	}

	@Override
	public SoundType getType() {
		return builder.getType();
	}

}
