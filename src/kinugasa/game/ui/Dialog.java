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
package kinugasa.game.ui;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import kinugasa.game.GameOption;
import kinugasa.game.I18N;
import kinugasa.graphics.ImageUtil;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:52:34<br>
 * @author Shinacho<br>
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

	public static void image(String title, BufferedImage image) {
		JOptionPane.showMessageDialog(null, new ImageIcon(image), title, JOptionPane.DEFAULT_OPTION);
	}
}
