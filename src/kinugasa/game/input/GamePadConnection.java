/*
 * The MIT License
 *
 * Copyright 2014 Shinacho.
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
package kinugasa.game.input;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2014/09/20<br>
 * @author Shinacho<br>
 * <br>
 */
public final class GamePadConnection{

	private GamePadConnection() {
	}

	public static void init() throws RuntimeException {
		if(load)throw new RuntimeException("dll is already loaded.");
		try {
			System.loadLibrary("KGP");
		} catch (Throwable t) {
			throw new RuntimeException(t);
		}
		load= true;
	}
	private static boolean load = false;

	public static native float[] getNativeState(int playerIndex);

	public static native void free();

	public static final int LENGTH = 21;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_A = 0;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_B = 1;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_X = 2;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_Y = 3;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_LB = 4;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_RB = 5;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_LEFT_STICK = 6;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_RIGHT_STICK = 7;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_POV_UP = 8;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_POV_DOWN = 9;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_POV_LEFT = 10;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_POV_RIGHT = 11;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_START = 12;
	/**
	 * これらの値はゲームパッドのデジタルボタンを識別します.
	 */
	public static final int BUTTON_BACK = 13;
	/**
	 * ゲームパッドのアナログトリガーを識別します.
	 */
	public static final int TRIGGER_LEFT = 14;
	/**
	 * ゲームパッドのアナログトリガーを識別します.
	 */
	public static final int TRIGGER_RIGHT = 15;
	/**
	 * ゲームパッドのアナログスティックを識別します.
	 */
	public static final int THUMB_STICK_LEFT_X = 16;
	/**
	 * ゲームパッドのアナログスティックを識別します.
	 */
	public static final int THUMB_STICK_LEFT_Y = 17;
	/**
	 * ゲームパッドのアナログスティックを識別します.
	 */
	public static final int THUMB_STICK_RIGHT_X = 18;
	/**
	 * ゲームパッドのアナログスティックを識別します.
	 */
	public static final int THUMB_STICK_RIGHT_Y = 19;
	/**
	 * ゲームパッドの識別状態が格納されている場所のインデックスです.
	 */
	public static final int CONNECTION = 20;
	//
	/**
	 * dllによって定義される、ネイティブな「FALSE」の状態です.
	 */
	public static final int NATIVE_FALSE = 0;
	/**
	 * dllによって使用される、トリガーの入力の最小値です.
	 */
	public static final int TRIGGER_MIN = 0;
	/**
	 * dllによって使用される、トリガーの入力の最大値です.
	 */
	public static final int TRIGGER_MAX = 255;
	/**
	 * dllによって使用される、スティックの入力の最小値です.
	 */
	public static final int THUMBSTICK_MIN = Short.MIN_VALUE;
	/**
	 * dllによって使用される、トリガーの入力のデフォルト値です.
	 */
	public static final int THUMBSTICK_CENTER = 0;
	/**
	 * dllによって使用される、スティックの入力の最大値です.
	 */
	public static final int THUMBSTICK_MAX = Short.MAX_VALUE;
	/**
	 * dllによって使用される、トリガーの入力の最小値からの最大値までの絶対値です.
	 */
	public static final int THUMSTICK_ABS_MAX = 65534;
}
