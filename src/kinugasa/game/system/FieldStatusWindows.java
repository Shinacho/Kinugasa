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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ProgressBarSprite;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_7:56:32<br>
 * @author Shinacho<br>
 */
public class FieldStatusWindows extends BasicSprite {

	private List<MessageWindow> mw = new ArrayList<>();
	private List<Status> status;
	private List<List<ProgressBarSprite>> pb = new ArrayList<>();

	public FieldStatusWindows(float x, List<Status> status) {
		setX(x);
		this.status = status;
		init();
	}

	private void init() {
		float x = getX();
		float y = 24;
		float w = 128;
		for (Status s : status) {
			List<ProgressBarSprite> p = new ArrayList<>();
			//表示文字列の生成
			String text = GameSystem.getInstance().getPCbyID(s.getId()).getVisibleName() + Text.getLineSep();
			int i = 1;
			for (StatusKey k : List.of(StatusKey.体力, StatusKey.魔力, StatusKey.正気度)) {
				text += " " + k.getVisibleName() + ":" + Text.getLineSep();
				float xx = x + 60;
				float yy = y + 16  + (16 * i);
				ProgressBarSprite pp
						= new ProgressBarSprite(xx, yy,
								w - 70, 6,
								(int) s.getEffectedStatus().get(k).getValue(),
								(int) s.getEffectedStatus().get(k).getValue(),
								(int) s.getEffectedStatus().get(k).getMax());
//				pp.setVal((int) s.getEffectedStatus().get(vs).getValue());
				p.add(pp);
				i++;
			}
			//座標の計算
			Text t = Text.of(text);
			t.allText();
			MessageWindow window = new MessageWindow(x, y, w, h, t);
			window.showAllNow();
			window.setModel(new SimpleMessageWindowModel().setNextIcon(""));
			mw.add(window);
			y += h + 4;
			pb.add(p);
		}
	}

	List<MessageWindow> getMw() {
		return mw;
	}

	public static final float h = 86;

	@Override
	public void update() {
		float x = getX();
		float y = 24;
		float w = 128;
		for (Status s : status) {
			List<ProgressBarSprite> p = new ArrayList<>();
			//表示文字列の生成
			String text = GameSystem.getInstance().getPCbyID(s.getId()).getVisibleName() + Text.getLineSep();
			int i = 1;
			for (StatusKey k : StatusKey.values()) {
				text += "  " + k.getVisibleName() + ":" + Text.getLineSep();
				float xx = x + 56;
				float yy = y + 20 + (18 * i);
				ProgressBarSprite pp
						= new ProgressBarSprite(xx, yy,
								w - 70, 6,
								(int) s.getEffectedStatus().get(k).getValue(),
								(int) s.getEffectedStatus().get(k).getValue(),
								(int) s.getEffectedStatus().get(k).getMax());
//				pp.setVal((int) s.getEffectedStatus().get(vs).getValue());
				p.add(pp);
				i++;
			}
			//座標の計算
			Text t = Text.of(text);
			t.allText();
			MessageWindow window = new MessageWindow(x, y, w, h, t);
			window.showAllNow();
			window.setModel(new SimpleMessageWindowModel().setNextIcon(""));
			mw.add(window);
			y += h + 4;
			pb.add(p);
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.forEach(v -> v.draw(g));
		pb.forEach(p -> p.forEach(b -> b.draw(g)));
	}
}
