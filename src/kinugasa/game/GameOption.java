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

import kinugasa.graphics.RenderingQuality;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import javax.swing.ImageIcon;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.resource.text.IniFile;
import kinugasa.game.ui.Dialog;
import kinugasa.game.ui.DialogIcon;
import kinugasa.game.ui.DialogOption;

/**
 * ゲーム起動時の画面サイズなどの設定です。ゲーム起動後にこのクラスを変更しても、反映されません。
 *
 * @vesion 1.0.0 - 2021/08/17_5:42:55<br>
 * @author Dra211<br>
 */
public class GameOption {

	private String title;
	private Color backColor;
	private Dimension windowSize;
	private Point windowLocation;
	private boolean lock;
	private boolean useMouse;
	private boolean useKeyboard;
	private boolean useGamePad;
	private boolean useLog;
	private String logPath;
	private int fps;
	private RenderingQuality rq;
	private String lang;
	private boolean updateIfNotActive;

	private String logName = "log_" + new SimpleDateFormat("yyyyMMddHHmmssSSS") + ".log";
	private ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
	private CloseEvent closeEvent
			= () -> Dialog.yesOrNo(I18N.translate(I18N.Key.CONFIRM), DialogIcon.QUESTION, I18N.translate(I18N.Key.ARE_YOU_SURE_EXIT)).is(DialogOption.YES);

	public static final class Key {

		public static final String TITLE = "TITLE";
		public static final String BG_COLOR = "BG_COLOR";
		public static final String SIZE = "SIZE";
		public static final String LOCATION = "LOCATION";
		public static final String LOCK = "LOCK";
		public static final String MOUSE = "MOUSE";
		public static final String KEY = "KEY";
		public static final String GAMEPAD = "GAMEPAD";
		public static final String LOG = "LOG";
		public static final String LOG_PATH = "LOG_PATH";
		public static final String FPS = "FPS";
		public static final String RENDERING_Q = "RENDERING_Q";
		public static final String RENDERING_M = "RENDERING_M";
		public static final String LANG = "LANG";
		public static final String UPDATE_IF_NOT_ACTIVE = "UPDATE_IF_NOT_ACTIVE";
	}

	public static GameOption fromIni(String filename) {
		IniFile ini = new IniFile(filename).load();
		GameOption go = new GameOption(ini.get(Key.TITLE).get().value());
		go.backColor = GraphicsUtil.createColor(ini.get(Key.BG_COLOR).get().asCsv());
		{
			int w = ini.get(Key.SIZE).get().asCsvOf(0).asInt();
			int h = ini.get(Key.SIZE).get().asCsvOf(1).asInt();
			go.windowSize = new Dimension(w, h);
		}
		{

			int x = ini.get(Key.LOCATION).get().asCsvOf(0).asInt();
			int y = ini.get(Key.LOCATION).get().asCsvOf(1).asInt();
			go.windowLocation = new Point(x, y);
		}
		go.lock = ini.get(Key.LOCK).get().isTrue();

		go.useMouse = ini.get(Key.MOUSE).get().isTrue();
		go.useKeyboard = ini.get(Key.KEY).get().isTrue();
		go.useGamePad = ini.get(Key.GAMEPAD).get().isTrue();

		go.useLog = ini.get(Key.LOG).get().isTrue();

		go.logPath = ini.get(Key.LOG_PATH).get().value();
		go.fps = ini.get(Key.FPS).get().asInt();
		go.rq = RenderingQuality.valueOf(ini.get(Key.RENDERING_Q).get().value());
		go.lang = ini.get(Key.LANG).get().value();
		go.updateIfNotActive = ini.get(Key.UPDATE_IF_NOT_ACTIVE).get().isTrue();

		return go;
	}

	public static GameOption defaultOption() {
		return fromIni("default.ini");
	}

	protected GameOption(String name) {
		this.title = name;
		INSTANCE = this;
	}
	private static GameOption INSTANCE;

	public static GameOption getInstance() {
		return INSTANCE;
	}

	public GameOption setTitle(String title) {
		this.title = title;
		return this;
	}

	public GameOption setBackColor(Color backColor) {
		this.backColor = backColor;
		return this;
	}

	public GameOption setWindowSize(Dimension windowSize) {
		this.windowSize = windowSize;
		return this;
	}

	public GameOption setWindowLocation(Point windowLocation) {
		this.windowLocation = windowLocation;
		return this;
	}

	public GameOption setCenterOfScreen() {
		Point centerOfScreen = GraphicsEnvironment.getLocalGraphicsEnvironment().getCenterPoint();
		this.windowLocation.x = centerOfScreen.x - windowSize.width / 2;
		this.windowLocation.y = centerOfScreen.y - windowSize.height / 2;
		if (windowLocation.x < 0) {
			windowLocation.x = 0;
		}
		if (windowLocation.y < 0) {
			windowLocation.y = 0;
		}
		return this;
	}

	public GameOption setLock(boolean lock) {
		this.lock = lock;
		return this;
	}

	public GameOption setUseMouse(boolean useMouse) {
		this.useMouse = useMouse;
		return this;
	}

	public GameOption setUseKeyboard(boolean useKeyboard) {
		this.useKeyboard = useKeyboard;
		return this;
	}

	public GameOption setUseGamePad(boolean useGamePad) {
		this.useGamePad = useGamePad;
		return this;
	}

	public GameOption setUseLog(boolean useLog) {
		this.useLog = useLog;
		return this;
	}

	public GameOption setLogPath(String logPath) {
		this.logPath = logPath;
		return this;
	}

	public GameOption setFps(int fps) {
		this.fps = fps;
		return this;
	}

	public GameOption setRenderingQuality(RenderingQuality rq) {
		this.rq = rq;
		return this;
	}

	public GameOption setLang(Locale l) {
		this.lang = l.getLanguage();
		return this;
	}

	public GameOption setLogName(String logName) {
		this.logName = logName;
		return this;
	}

	public void setIcon(ImageIcon icon) {
		this.icon = icon;
	}

	public GameOption setCloseEvent(CloseEvent closeEvent) {
		this.closeEvent = closeEvent;
		return this;
	}

	public String getTitle() {
		return title;
	}

	public Color getBackColor() {
		return backColor;
	}

	public Dimension getWindowSize() {
		return windowSize;
	}

	public Point getWindowLocation() {
		return windowLocation;
	}

	public boolean isLock() {
		return lock;
	}

	public boolean isUseMouse() {
		return useMouse;
	}

	public boolean isUseKeyboard() {
		return useKeyboard;
	}

	public boolean isUseGamePad() {
		return useGamePad;
	}

	public boolean isUseLog() {
		return useLog;
	}

	public String getLogPath() {
		return logPath;
	}

	public int getFps() {
		return fps;
	}

	public RenderingQuality getRenderingQuality() {
		return rq;
	}

	public String getLang() {
		return lang;
	}

	public String getLogName() {
		return logName;
	}

	public ImageIcon getIcon() {
		return icon;
	}

	public CloseEvent getCloseEvent() {
		return closeEvent;
	}

	boolean isUpdateIfNotActive() {
		return updateIfNotActive;
	}

	@Override
	public String toString() {
		return "GameOption{" + "title=" + title + ", backColor=" + backColor + ", windowSize=" + windowSize + ", windowLocation=" + windowLocation + ", lock=" + lock + ", useMouse=" + useMouse + ", useKeyboard=" + useKeyboard + ", useGamePad=" + useGamePad + ", useLog=" + useLog + ", logPath=" + logPath + ", fps=" + fps + ", rq=" + rq + ", lang=" + lang + ", updateIfNotActive=" + updateIfNotActive + ", logName=" + logName + ", icon=" + icon + ", closeEvent=" + closeEvent + '}';
	}

}
