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
package kinugasa.game;

import java.awt.Component;
import java.awt.Dimension;
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
import java.util.Date;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;
import kinugasa.game.input.GamePadConnection;
import kinugasa.game.input.InputState;
import kinugasa.game.input.KeyConnection;
import kinugasa.game.input.MouseConnection;
import kinugasa.game.system.GameSystem;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;
import kinugasa.resource.TempFileStorage;
import kinugasa.resource.db.DBConnection;
import kinugasa.util.MathUtil;

/**
 * ゲームのスーパークラスです.
 *
 * @vesion 1.0.0 - 2021/08/17_5:41:45<br>
 * @author Shinacho<br>
 */
public abstract class GameManager {

	/**
	 * 起動時設定.
	 */
	private GameOption option;
	/**
	 * ウインドウ.
	 */
	private AWTGameWindow window;
	private GameLoop loop;
	private GameTimeManager gameTimeManager;
	private boolean updateIfNotActive;

	private Graphics2D g;
	private BufferStrategy graphicsBuffer;
	private Rectangle clippingRectangle;
	private RenderingHints renderingHints;
	private int fps;
	private float drawSize = 0;
	private BufferedImage image;
	private List<ScreenEffect> effects = new ArrayList<>();

	protected GameManager(GameOption option) throws IllegalStateException {
		//DBドライバロード
		try {
			org.h2.Driver.load();
		} catch (Throwable a) {
			a.printStackTrace();
		}
		this.option = option;
		updateOption();
	}

	@OneceTime
	protected final void updateOption() {
		MathUtil.init();
		if (option.isUseLog()) {
			GameLog.usingLog(option.getLogPath() + option.getLogName());
			try {
				FileHandler handler = new FileHandler(option.getLogPath() + option.getLogName());
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
				GameLog.print(ex);
			}
			GameLog.print("this is " + option.getLogPath() + option.getLogName());
		}
		CMDargs.init(option.getArgs());
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
		this.drawSize = option.getDrawSize();

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

		GameSystem.setDebugMode(option.isDebugMode());

	}

	public List<ScreenEffect> getEffects() {
		return effects;
	}

	public void setEffects(List<ScreenEffect> effects) {
		this.effects = effects;
	}

	public void clearEffects() {
		effects.clear();
	}

	public void addEffect(ScreenEffect e) {
		effects.add(e);
	}

	public void clearEndedEffects() {
		List<ScreenEffect> remove = new ArrayList<>();
		for (ScreenEffect e : effects) {
			if (e.isEnded()) {
				remove.add(e);
			}
		}
		effects.removeAll(remove);
	}
//
//	public void reloadInputListener() {
//		if (PlayerConstants.getInstance().isUsingKeyboard()) {
//			window.removeKeyListener(KeyConnection.getInstance());
//			KeyConnection.setListener(window);
//		}
//		if (PlayerConstants.getInstance().isUsingMouse()) {
//			window.removeMouseListener(MouseConnection.getInstance());
//			window.removeMouseMotionListener(MouseConnection.getInstance());
//			window.removeMouseWheelListener(MouseConnection.getInstance());
//			MouseConnection.setListener(window);
//		}
//	}

	public GameWindow getWindow() {
		return window;
	}

	public Component getAWTComponent() {
		return window;
	}

	public final GameOption getOption() {
		return option;
	}

	private boolean started = false;

	@OneceTime
	public final void gameStart() throws IllegalStateException {
		GameLog.print("GAME START");
		if (option == null) {
			throw new IllegalStateException("game option is null");
		}
		if (started) {
			throw new IllegalStateException("game is alredy started");
		}
		loop = new GameLoop(this, gameTimeManager = new GameTimeManager(fps), updateIfNotActive);
		gameTimeManager.setStartTime(System.currentTimeMillis());
		EventQueue.invokeLater(() -> {
			try {
				startUp();
			} catch (Throwable ex) {
				GameLog.print(ex);
				System.exit(1);
			}
			window.setVisible(true);
			window.createBufferStrategy(drawSize == 1 ? 2 : 1);
			graphicsBuffer = window.getBufferStrategy();
			clippingRectangle = window.getInternalBounds();
			started = true;
			loop.start();
			image = ImageUtil.newImage((int) (window.getInternalBounds().getWidth() / drawSize), (int) (window.getInternalBounds().getHeight() / drawSize));
		});
		GameLog.print(getWindow().getTitle() + " is start");
	}

	@OneceTime
	public final void gameExit() throws IllegalStateException {
		if (!started) {
			throw new IllegalStateException("game is not started");
		}
		if (loop != null && loop.isStarted()) {
			loop.end();
		}
		DBConnection.getInstance().close();
		try {
			dispose();
		} catch (Throwable ex) {
			GameLog.print("ERROR : " + ex);
			System.exit(1);
		}
		if (PlayerConstants.getInstance().isUsingGamePad()) {
			// GamePadConnection.free(); //なんかエラー出る
		}
		LockUtil.deleteLockFile();
		GameLog.close();
		TempFileStorage.getInstance().deleteAll();
		window.dispose();
		System.exit(0);
	}

	/**
	 * ゲームを開始する手順を記述します.
	 */
	@OneceTime
	protected abstract void startUp();

	/**
	 * ゲームを破棄する手順を記述します.
	 */
	@OneceTime
	protected abstract void dispose();

	/**
	 * ゲームワールドを更新する処理を記述します.
	 *
	 * @param gtm ゲームタイムマネージャーの唯一のインスタンス.
	 */
	@LoopCall
	protected abstract void update(GameTimeManager gtm, InputState is);

	/**
	 * 画面を描画する処理を記述します.
	 *
	 * @param gc ウインドウの内部領域に対応するグラフィックスコンテキスト.
	 */
	@LoopCall
	protected abstract void draw(GraphicsContext gc);

	/**
	 * 画面をリペイントします. このメソッドは内部用です。呼び出さないでください。
	 */
	@LoopCall
	final void repaint() {
		g = ImageUtil.createGraphics2D(image, RenderingQuality.NOT_USE);
		g.setBackground(window.getBackground());
		g.setClip(clippingRectangle);
		g.clearRect(clippingRectangle.x, clippingRectangle.y,
				clippingRectangle.width, clippingRectangle.height);
		g.setRenderingHints(renderingHints);
		draw(new GraphicsContext(g));
		g.dispose();
		
		final Dimension imageSize = new Dimension(image.getWidth(), image.getHeight());
		for(ScreenEffect e : effects){
			image = e.doIt(image);
			if(!imageSize.equals(new Dimension(image.getWidth(), image.getHeight()))){
				throw new ScreenEffectException("screen effect " + e + " s size is missmatch");
			}
		}
		
		Graphics2D g2 = (Graphics2D) graphicsBuffer.getDrawGraphics();
		g2.drawImage(image, 0, 0, (int) (image.getWidth() * drawSize), (int) (image.getHeight() * drawSize), null);
		g2.dispose();

		if (graphicsBuffer.contentsRestored()) {
			repaint();
		}
		graphicsBuffer.show();
		if (graphicsBuffer.contentsLost()) {
			repaint();
		}
		
	}
}
