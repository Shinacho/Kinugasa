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

import java.util.List;
import java.util.stream.Collectors;
import kinugasa.resource.sound.SoundMap;

/**
 * BGMの一覧を表示し、再生可能にする画面表示の実装です。アクションテキストスプライトグループを使用しています。
 *
 * @vesion 1.0.0 - 2022/11/13_0:36:02<br>
 * @author Shinacho<br>
 */
public class MusicRoom extends ScrollSelectableMessageWindow {

	private SoundMap map;

	public MusicRoom(SoundMap map, int x, int y, int w, int h, int line) {
		super(x, y, w, h, line);
		this.map = map;
		setLoop(true);
		setLine1select(false);
		List<Text> t = map.stream().map(p -> p.getName()).sorted().map(p -> new Text(p)).collect(Collectors.toList());
		t.add(0, new Text("--" + map.getName()));
		setText(t);
	}

	public void play() {
		map.dispose();
		map.get(getSelected().getText()).load().stopAndPlay();
	}

}
