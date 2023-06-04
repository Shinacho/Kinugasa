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
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.InputStatus;
import kinugasa.resource.NotYetLoadedException;
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
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			if (framePos > 0) {
				clip.setFramePosition(framePos);
				framePos = 0;
				clip.start();
			} else {
				clip.start();
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
			} else {
				//lp null
				CachedSound s = this;
				new Thread(() -> {
					while (true) {
						try {
							Thread.sleep(2000);
						} catch (InterruptedException ex) {
							Logger.getLogger(CachedSound.class.getName()).log(Level.SEVERE, null, ex);
						}
						if (s.clip.getFramePosition() >= s.clip.getFrameLength() || !s.playing) {
							s.dispose();
							break;
						}
					}
				}, getFileName() + "_DISPOSER").start();
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
		GameLog.print("CachedSound is loaded name=[" + getName() + "](" + watch.getTime() + " ms)");
		return this;
	}

	@Override
	public void dispose() {
		if (getStatus() == InputStatus.NOT_LOADED) {
			return;
		}
		stop();
		if (clip != null) {
			clip.flush();
			clip.close();
		}
		clip = null;
		GameLog.print("CachedSound : [" + getName() + "] : is disposed");
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
