/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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

import java.awt.EventQueue;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import kinugasa.game.input.GamePadConnection;
import kinugasa.game.input.KeyConnection;
import kinugasa.game.input.MouseConnection;
import kinugasa.graphics.ImageUtil;
import kinugasa.resource.TempFileStorage;
import kinugasa.util.MathUtil;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_5:41:45<br>
 * @author Dra211<br>
 */
public abstract class GameManager {

	private GameOption option;
	private AWTGameWindow window;
	private GameLoop loop;
	private GameTimeManager gameTimeManager;
	private boolean updateIfNotActive;

	private Graphics2D g;
	private BufferStrategy graphicsBuffer;
	private Rectangle clippingRectangle;
	private RenderingHints renderingHints;
	private int fps;

	protected GameManager(GameOption option) throws IllegalStateException {
		this.option = option;
		updateOption();

	}

	protected final void updateOption() {
		MathUtil.init();
		if (option.isUseLog()) {
			GameLog.usingLog(option.getLogPath());
			try {
				FileHandler handler = new FileHandler(option.getLogPath());
				handler.setLevel(Level.ALL);
				handler.setFormatter(new Formatter() {

					final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");

					@Override
					public String format(LogRecord record) {
						StringBuilder line = new StringBuilder();
						line.append(DATE_FORMAT.format(new Date(record.getMillis()))).append(' ');
						line.append(record.getLevel().getName()).append(' ');
						if (record.getThrown() != null) {
							line.append("(exception)").append(record.getThrown().getMessage());
						} else {
							line.append(record.getMessage());
						}
						line.append(System.lineSeparator());
						return line.toString();
					}
				});

				Logger.getGlobal().addHandler(handler);
			} catch (IOException | SecurityException ex) {
				GameLog.print(Level.WARNING, ex);
			}
			GameLog.printInfo("this is " + option.getLogPath());
		}
		I18N.init(option.getLang());

		if (option.isLock()) {
			if (LockUtil.isExistsLockFile()) {
				throw new IllegalStateException(Arrays.toString(LockUtil.listLockFile()));
			}
			LockUtil.createLockFile();
		}

		window = new AWTGameWindow();
		renderingHints = option.getRenderingQuality().getRenderingHints();

		window.addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				if (option.getCloseEvent().isClose()) {
					gameExit();
				}
			}
		});

		window.setTitle(option.getTitle());
		window.setIconImage(option.getIcon().getImage());
		window.setBackground(option.getBackColor());
		window.setLocation(option.getWindowLocation());
		window.setSize(option.getWindowSize());
		window.setResizable(false);

		this.fps = option.getFps();
		this.updateIfNotActive = option.isUpdateIfNotActive();

		PlayerConstants playerConstants = PlayerConstants.getInstance();

		playerConstants.setUsingKeyboard(option.isUseKeyboard());
		if (playerConstants.isUsingKeyboard()) {
			KeyConnection.setListener(window);
		}

		playerConstants.setUsingMouse(option.isUseMouse());
		if (playerConstants.isUsingMouse()) {
			MouseConnection.setListener(window);
		} else {
			BufferedImage cursorImage = ImageUtil.newImage(16, 16);
			window.setCursor(Toolkit.getDefaultToolkit().createCustomCursor(cursorImage, new Point(), "game cursor"));
		}

		playerConstants.setUsingGamePad(option.isUseGamePad());
		if (playerConstants.isUsingGamePad()) {
			GamePadConnection.init();
		}

	}

	public GameWindow getWindow() {
		return window;
	}

	public final GameOption getOption() {
		return option;
	}

	private boolean started = false;

	protected final void gameStart(String... args) throws IllegalStateException {
		if (option == null) {
			throw new IllegalStateException("game option is null");
		}
		if (started) {
			throw new IllegalStateException("game is alredy started");
		}
//		CMDArgs.getInstance().setArgs(args);
		loop = new GameLoop(this, gameTimeManager = new GameTimeManager(fps), updateIfNotActive);
		EventQueue.invokeLater(() -> {
			try {
				startUp();
			} catch (Throwable ex) {
				GameLog.print(Level.WARNING, "ERROR : " + ex);
				System.exit(1);
			}
			window.setVisible(true);
			window.createBufferStrategy(2);
			graphicsBuffer = window.getBufferStrategy();
			clippingRectangle = window.getInternalBounds();
			started = true;
			GameLog.printInfo("gameStart is done.");
			loop.start();
		});
		GameLog.printInfoIfUsing(getWindow().getTitle() + " is start");
	}

	protected final void gameExit() throws IllegalStateException {
		if (!started) {
			throw new IllegalStateException("game is not started");
		}
		if (loop != null && loop.isStarted()) {
			loop.end();
		}
		try {
			dispose();
		} catch (Throwable ex) {
			GameLog.print(Level.WARNING, "ERROR : " + ex);
			System.exit(1);
		}
		if (PlayerConstants.getInstance().isUsingGamePad()) {
			// GamePadConnection.free(); //Ç»ÇÒÇ©ÉGÉâÅ[èoÇÈ
		}
		LockUtil.deleteLockFile();
		TempFileStorage.getInstance().deleteAll();
		window.dispose();
		System.exit(0);
	}

	protected abstract void startUp();

	protected abstract void dispose();

	protected abstract void update(GameTimeManager gtm);

	protected abstract void draw(GraphicsContext gc);

	final void repaint() {

		g = (Graphics2D) graphicsBuffer.getDrawGraphics();
		g.setClip(clippingRectangle);
		g.clearRect(clippingRectangle.x, clippingRectangle.y,
				clippingRectangle.width, clippingRectangle.height);
		g.setRenderingHints(renderingHints);
		draw(new GraphicsContext(g));
		g.dispose();
		if (graphicsBuffer.contentsRestored()) {
			repaint();
		}
		graphicsBuffer.show();
		if (graphicsBuffer.contentsLost()) {
			repaint();
		}
	}
}
