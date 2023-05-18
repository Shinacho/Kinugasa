/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
			String text = s.getName() + Text.getLineSep();
			int i = 1;
			for (String vs : BattleConfig.getVisibleStatus()) {
				text += "  " + StatusKeyStorage.getInstance().get(vs).getDesc() + ":" + Text.getLineSep();
				float xx = x + 56;
				float yy = y + 20 + (18 * i);
				ProgressBarSprite pp
						= new ProgressBarSprite(xx, yy,
								w - 70, 6,
								(int) s.getEffectedStatus().get(vs).getValue(),
								(int) s.getEffectedStatus().get(vs).getValue(),
								(int) s.getEffectedStatus().get(vs).getMax());
//				pp.setVal((int) s.getEffectedStatus().get(vs).getValue());
				p.add(pp);
				i++;
			}
			//座標の計算
			Text t = new Text(text);
			t.allText();
			MessageWindow window = new MessageWindow(x, y, w, h, t);
			window.allText();
			window.setModel(new SimpleMessageWindowModel().setNextIcon(""));
			mw.add(window);
			y += h + 4;
			pb.add(p);
		}
	}

	List<MessageWindow> getMw() {
		return mw;
	}

	public static final float h = 96;

	@Override
	public void update() {
		float x = getX();
		float y = 24;
		float w = 128;
		for (Status s : status) {
			List<ProgressBarSprite> p = new ArrayList<>();
			//表示文字列の生成
			String text = s.getName() + Text.getLineSep();
			int i = 1;
			for (String vs : BattleConfig.getVisibleStatus()) {
				text += "  " + StatusKeyStorage.getInstance().get(vs).getDesc() + ":" + Text.getLineSep();
				float xx = x + 56;
				float yy = y + 20 + (18 * i);
				ProgressBarSprite pp
						= new ProgressBarSprite(xx, yy,
								w - 70, 6,
								(int) s.getEffectedStatus().get(vs).getValue(),
								(int) s.getEffectedStatus().get(vs).getValue(),
								(int) s.getEffectedStatus().get(vs).getMax());
//				pp.setVal((int) s.getEffectedStatus().get(vs).getValue());
				p.add(pp);
				i++;
			}
			//座標の計算
			Text t = new Text(text);
			t.allText();
			MessageWindow window = new MessageWindow(x, y, w, h, t);
			window.allText();
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
