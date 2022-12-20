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
package kinugasa.game.ui;

import java.awt.Event;
import java.awt.event.ActionEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import kinugasa.game.GameOption;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:52:34<br>
 * @author Dra211<br>
 */
public class Dialog {

	public static DialogOption info(String msg) {
		int r = JOptionPane.showConfirmDialog(null, msg, GameOption.getInstance().getTitle(), JOptionPane.DEFAULT_OPTION);
		return DialogOption.of(r);
	}

	public static class InputResult {

		public InputResult(String value, DialogOption result) {
			this.value = value;
			this.result = result;
		}

		public String value;
		public DialogOption result;
	}

	public static InputResult input(String title, int max) {
		JTextField jt = new JTextField();
		jt.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				JTextField f = (JTextField) e.getSource();
				String s = f.getText();
				if (s.length() > max) {
					f.setText(s.substring(0, max));
				}
			}
		});
		jt.grabFocus();
		int r = JOptionPane.showConfirmDialog(null, jt, title, JOptionPane.DEFAULT_OPTION);
		return new InputResult(jt.getText(), DialogOption.of(r));
	}

	public static DialogOption yesOrNo(String title, DialogIcon icon, String msg) {
		int r = JOptionPane.showConfirmDialog(null, msg, title, JOptionPane.YES_NO_OPTION, icon.getOption());
		return DialogOption.of(r);
	}
}
