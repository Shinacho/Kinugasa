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

import java.util.HashMap;
import java.util.Set;
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
 * �����L���b�V�����̃T�E���h�̎����ł�.
 * <br>
 * WAVE�t�@�C�����Đ�����ۂ́A�ł���ʓI�ȃT�E���h�̎����ƂȂ�܂��B<br>
 *
 * <br>
 *
 * @version 2.0.0 - 2013/01/13_18:46:43<br>
 * @author Dra0211<br>
 */
public class CachedSound implements Sound {

	/**
	 * �T�E���h�̃L���b�V���f�[�^.
	 */
	private static final HashMap<SoundBuilder, CachedSound> CACHE = new HashMap<SoundBuilder, CachedSound>(16);

	/**
	 * �V�����L���b�V���T�E���h���\�z���܂�.
	 *
	 * @param b �T�E���h�̍\�z�Ɏg�p����r���_.<br>
	 *
	 * @return �r���_�̐ݒ�ō쐬���ꂽ�L���b�V���T�E���h.<br>
	 */
	static CachedSound create(SoundBuilder b) {
		if (CACHE.containsKey(b) && !b.isNewFile()) {
			return CACHE.get(b);
		}
		CachedSound sc = new CachedSound(b);
		CACHE.put(b, sc);
		return sc;
	}
	/**
	 * ���̃T�E���h���\�z�����r���_.
	 */
	private SoundBuilder builder;
	/**
	 * ���̃T�E���h�̃X�g���[��.
	 */
	private transient Clip clip;
	/**
	 * ���̃T�E���h�̃��[�v�ݒ�.
	 */
	private LoopPoint lp;

	/**
	 * �T�E���h���쐬. ���̃R���X�g���N�^�ł́Af.exist�̌����͂��Ȃ�.
	 *
	 * @param builder �t�@�C���C���X�^���X.<br>
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
	 * ���[�h���ɓK�p����R���g���[��.
	 */
	private HashMap<Control.Type, Float> ctrls = new HashMap<Control.Type, Float>(8);

	/**
	 * �R���g���[�����o�b�t�@�����O����.
	 *
	 * @param t �R���g���[���̃^�C�v.<br>
	 * @param val �l.<br>
	 */
	private void setControl(Control.Type t, float val) {
		ctrls.put(t, val);
	}

	/**
	 * ���[�v�ʒu��ݒ肵�܂�.
	 *
	 * @param p ���[�v�ʒu.<br>
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
			throw new NotYetLoadedException("sound " + this + " is not yet loaded.");
		}
		if (lp != LoopPoint.NO_USE) {
			clip.loop(Clip.LOOP_CONTINUOUSLY);
		} else {
			clip.start();
			playing = true;
		}
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

	@Override
	public void pause() {
		if (clip != null) {
			clip.stop();
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
				float val = ctrls.get(t);
				try {
					FloatControl control = (FloatControl) clip.getControl(t);
					control.setValue(val);
				} catch (IllegalArgumentException i) {
					GameLog.printInfoIfUsing("! > CachedSound : [" + getName() + "] : UN SUPPORTED CONTROL : Type=[" + t + "]");
				}
			}
			if (lp != null) {
				clip.setLoopPoints(lp.getTo(), lp.getFrom());
			}
		} catch (Exception ex) {
			throw new SoundStreamException(ex);
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
		GameLog.printInfoIfUsing("CachedSound is loaded name=[" + getName() + "](" + watch.getTime() + " ms)");
		return this;
	}

	@Override
	public void dispose() {
		stop();
		if (clip != null) {
			clip.flush();
			clip.close();
		}
		clip = null;
		GameLog.printInfoIfUsing("CachedSound : [" + getName() + "] : is disposed");
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
	 * ���̃T�E���h���\�z�����r���_���擾���܂�. �r���_�ւ̐ݒ�́A�Ӗ�������܂���B<br>
	 *
	 * @return ���̃T�E���h���\�z���邽�߂ɍ쐬���ꂽ�r���_�̃C���X�^���X��Ԃ��܂��B<br>
	 */
	public SoundBuilder getBuilder() {
		return builder;
	}
}
