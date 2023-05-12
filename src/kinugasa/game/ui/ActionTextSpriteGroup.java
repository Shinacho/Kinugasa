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
package kinugasa.game.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.Arrays;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.object.Drawable;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 * アクションテキストスプライトをグループ化し、さらに選択中アイコンも設置することで、単一のアクションテキストスプライトを選択できるようにするＵＩです。
 * 選択中アイコンは要素の左に表示されます。選択できるのは1つだけです。つまりラジオボタン的なＵＩとなります。 選択中アイコンの座標は自動設定されます。
 * 要素は、追加されている順に並び替えらえます。もっとも上の選択肢の座標はコンストラクタで指定する必要があります。
 *
 * @vesion 1.0.0 - 2022/11/12_22:35:09<br>
 * @author Shinacho<br>
 */
public class ActionTextSpriteGroup implements Drawable {

	private List<ActionTextSprite> list;
	private int selectedIdx;
	private TimeCounter iconCounter = new FrameTimeCounter(16);

	public ActionTextSpriteGroup(float x, float y, ActionTextSprite... list) {
		this(12, x, y, Arrays.asList(list), 0);
	}

	public ActionTextSpriteGroup(int b, float x, float y, List<ActionTextSprite> list, int selectedIdx) {
		this.list = list;
		this.selectedIdx = selectedIdx;
		this.buffer = b;
		updateLocation(x, y);
		updateSelectIcon();
	}

	public void updateLocation(float x, float y) {
		//要素の座標調整
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setLocation(x, y + i * list.get(0).getHeight() + i * (buffer * 2));
		}
	}

	public void add(ActionTextSprite a) {
		list.add(a);
		updateSelectIcon();
	}

	public void setList(List<ActionTextSprite> list) {
		this.list = list;
		updateSelectIcon();
	}

	// リストを変更した場合は選択中アイコンの座標更新を実行すること
	public List<ActionTextSprite> getList() {
		return list;
	}

	public ActionTextSprite getSelected() {
		return list.get(selectedIdx);
	}

	public int getSelectedIdx() {
		return selectedIdx;
	}

	public void setSelectedIdx(int selectedIdx) {
		this.selectedIdx = selectedIdx;
		updateSelectIcon();
	}

	public void exec() {
		getSelected().exec();
	}

	public void next() {
		selectedIdx++;
		if (selectedIdx >= list.size()) {
			selectedIdx = 0;
		}
		updateSelectIcon();
	}

	public void prev() {
		selectedIdx--;
		if (selectedIdx < 0) {
			selectedIdx = list.size() - 1;
		}
		updateSelectIcon();
	}

	private int buffer;

	public int getBuffer() {
		return buffer;
	}

	public void setBuffer(int buffer) {
		this.buffer = buffer;
	}

	public void updateSelectIcon() {
		if (list.isEmpty()) {
			return;
		}
		list.forEach(t -> t.setText(t.getText().replaceAll("> ", "")));
		list.get(selectedIdx).setText("> " + list.get(selectedIdx).getText());
	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setFont(FontModel.DEFAULT.clone().getFont());
		list.forEach(t -> t.draw(g2));
	}

}
