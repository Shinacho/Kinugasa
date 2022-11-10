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
package kinugasa.game;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * �Q�[���̐i�s���s���X���b�h�̎����ł�.
 * <br>
 * ���̃N���X���Q�[���쐬�҂��g�p���邱�Ƃ͂���܂���B<br>
 * <br>
 *
 * @version 1.0.0 - 2013/01/14_12:52:13<br>
 * @version 1.0.1 - 2013/04/27_21:11<br>
 * @author Dra0211<br>
 */
public final class GameLoop implements Runnable {

	/**
	 * ���̃X���b�h���X�V����Ώۂ̃Q�[��.
	 */
	private GameManager game;
	/**
	 * FPS����ёҋ@���Ԃ̊Ǘ�.
	 */
	private GameTimeManager gtm;
	/**
	 * �N�����郁�C���̃X���b�h.
	 */
	private Thread thread;
	/**
	 * �N�����.
	 */
	private volatile boolean exec = false;
	private boolean updateIfNotActive;

	/**
	 * �V�����Q�[���p�X���b�h���쐬���܂�. �쐬���������ł́A�N������܂���.<br>
	 *
	 * @param game �Ώۂ̃Q�[���C���X�^���X.<br>
	 * @param gtm FPS����ёҋ@���Ԃ��Ǘ�����GameTimeManager.<br>
	 */
	GameLoop(GameManager game, GameTimeManager gtm) {
		this.game = game;
		this.gtm = gtm;
		this.updateIfNotActive = false;
	}

	/**
	 * �V�����Q�[���p�X���b�h���쐬���܂�. �쐬���������ł́A�N������܂���.<br>
	 *
	 * @param game �Ώۂ̃Q�[���C���X�^���X.<br>
	 * @param gtm FPS����ёҋ@���Ԃ��Ǘ�����GameTimeManager.<br>
	 */
	GameLoop(GameManager game, GameTimeManager gtm, boolean updateIfNotActive) {
		this.game = game;
		this.gtm = gtm;
		this.updateIfNotActive = updateIfNotActive;
	}

	/**
	 * ���C�����[�v�̎����ł�.
	 */
	@Override
	public void run() {
		try {
			long startTime;
			if (updateIfNotActive) {
				while (exec) {
					startTime = System.nanoTime();
					game.update(gtm);
					game.repaint();
					gtm.sleep(startTime);
				}
			} else {
				while (exec) {
					if (game.getWindow().isActive()) {
						startTime = System.nanoTime();
						game.update(gtm);
						game.repaint();
						gtm.sleep(startTime);
					} else {
						startTime = System.nanoTime();
						try {//�_�~�[����
							Thread.sleep(1000 / gtm.getIdealFPS());
						} catch (InterruptedException ex) {
							Logger.getLogger(GameLoop.class.getName()).log(Level.SEVERE, null, ex);
						}
						gtm.sleep(startTime);
					}
				}
			}
		} catch (Throwable ex) {
			if (ex instanceof Exception) {
				((Exception) ex).printStackTrace();
			}
			Throwable t = ex;
			while (t.getCause() != null) {
				System.out.println(t.getCause());
				t = t.getCause();
			}
			GameLog.print(Level.WARNING, ex);
			System.exit(1);
		}
	}

	/**
	 * ���C�����[�v���I�����܂�.
	 */
	void end() {
		exec = false;
	}

	/**
	 * �X���b�h���N�����A�ĕ`���X�V���J�n���܂�.
	 *
	 * @throws IllegalStateException ���łɋN�����Ă���ꍇ�ɓ�������.<br>
	 */
	void start() throws IllegalStateException {
		if (isStarted()) {
			throw new IllegalStateException("");
		}
		exec = true;
		thread = new Thread(this, game.getOption().getTitle() + ":1");
		thread.start();
	}

	/**
	 * �X���b�h���N�����Ă��邩�ǂ������������܂�.
	 *
	 * @return start���Ăяo����A�N�����Ă���ꍇ��true��Ԃ��܂��B<br>
	 */
	boolean isStarted() {
		return thread != null;
	}
}
