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
package kinugasa.game;

/**
 * �Q�[���̐i�s���Ԃ��Ǘ����AFPS�����ɕۂ��߂̋@�\��񋟂��܂�.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_12:33:16<br>
 * @author Shinacho<br>
 */
public final class GameTimeManager {

	/**
	 * �P�ʎ��ԓ�����̍X�V��.
	 */
	private int updateNum;
	/**
	 * �O�񌟍����̎���.
	 */
	private long prevTime;
	/**
	 * ���݂̎���.
	 */
	private long nowTime;
	/**
	 * ���݂�FPS.
	 */
	private float fps;
	/**
	 * �X���[�v�ł̑҂�����.
	 */
	private long waitTime;
	/**
	 * �J�n����̌o�߃t���[��.
	 */
	private long totalFrame = 0L;
	/**
	 * �X���[�v�̏I������.
	 */
	private long endTime;
	private long startTime;

	/**
	 * FPS�̍ő�l��60�̐V����TimeManager���쐬���܂�.
	 */
	GameTimeManager() {
		this(60);
	}

	/**
	 * �V����TimeManager���쐬���܂�.
	 *
	 * @param idealFPS FPS�̍ő�l.<Br>
	 */
	GameTimeManager(int idealFPS) {
		waitTime = 1000 / idealFPS * 1000000;
		updateNum = idealFPS;
		prevTime = System.nanoTime() - 1000000000;
	}

	void setStartTime(long startTime) {
		this.startTime = startTime;
	}

	/**
	 * �Q�[���J�n�������擾���܂��B���̃Q�[�����J�n���ꂽ�����ł��B
	 * @return �Q�[���J�n�����BSystem.currentTimeMillis�ł��B
	 */
	public long getStartTime() {
		return startTime;
	}

	/**
	 * FPS�̍ő�l���擾���܂�. ���̃��\�b�h�̖߂�l�̓~���b���x�Ɋۂ߂���.
	 *
	 * @return FPS�̍ő�l.<br>
	 */
	public long getIdealFPS() {
		return 1000000000 / waitTime;
	}

	/**
	 * FPS�̍ő�l��ݒ肵�܂�.
	 *
	 * @param idealFPS FPS�̍ő�l.<br>
	 */
	public void setIdealFPS(int idealFPS) {
		waitTime = 1000 / idealFPS * 1000000;
		updateNum = idealFPS;
	}

	/**
	 * ���݂�FPS���擾���܂�.
	 *
	 * @return ���݂�FPS.<br>
	 */
	public float getFPS() {
		return fps;
	}

	/**
	 * FPS�̂ݎ���\�L���擾���܂�. ���̃��\�b�h�̖߂�l�͒P���x�ł�.<Br>
	 *
	 * @return FPS�̕�����\�L.�ʏ�͏����ȉ���6�����x�ɂȂ�.<br>
	 */
	public String getFPSStr() {
		return Float.toString(getFPS());
	}

	/**
	 * �w�肵��������FPS�̕�����\�L���擾���܂�.
	 *
	 * @param d �����_�ȉ��̌���.<Br>
	 *
	 * @return �w�肵�������̐؂�̂Ă�ꂽFPS�\�L.<Br>
	 *
	 * @throws IllegalArgumentException d�������̏ꍇ�ɓ�������.<Br>
	 */
	public String getFPSStr(int d) throws IllegalArgumentException {
		if (d < 0) {
			throw new IllegalArgumentException("disit is minus");
		}
		String s = getFPSStr();
		int dotPosition = s.indexOf('.');
		if (dotPosition <= 0) {
			return s;
		}
		if (d == 0) {
			return s.substring(0, dotPosition);
		}
		dotPosition += d;
		if (s.length() <= dotPosition) {
			return s;
		}
		return s.substring(0, dotPosition + 1);
	}

	/**
	 * �J�n����̌o�߃t���[�����擾���܂�.
	 *
	 * @return �J�n����̌o�߃t���[����.<br>
	 */
	public long getTotalFrame() {
		return totalFrame;
	}

	/**
	 * �J�n����̌o�߃t���[�������������܂�.
	 */
	public void resetTotalFrame() {
		totalFrame = 0L;
	}

	/**
	 * ���̃��\�b�h���ĂԂƁA���炩���ߐݒ肳�ꂽFPS���ێ��ł��鎞�Ԃ������s�����X���b�h��sleep���܂�.
	 *
	 * @param startTime �����J�n�O�̎�����nano�b���x�ő��M���܂��B<br>
	 */
	void sleep(long startTime) {
		nowTime = System.nanoTime();
		endTime = nowTime + (waitTime - (nowTime - startTime));//�I������(ns
		while (System.nanoTime() < endTime) {//System.nanoTime���C���N�������g�����̂𗘗p���Ă���
			try {
				Thread.sleep(1);
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
		nowTime = System.nanoTime();//�X���[�v���Ă���̂Ō��ݎ������X�V
		totalFrame++;
		updateNum++;
		if (nowTime - prevTime > 1000000000) {//1s
			fps = (float) updateNum / ((nowTime - prevTime) / 1000000000f);
			updateNum = 0;
			prevTime = nowTime;
		}
	}
}
