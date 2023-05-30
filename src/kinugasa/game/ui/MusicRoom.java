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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.resource.sound.*;

/**
 * BGMの一覧を表示し、再生可能にする画面表示の実装です。アクションテキストスプライトグループを使用しています。
 *
 * @vesion 1.0.0 - 2022/11/13_0:36:02<br>
 * @author Shinacho<br>
 */
public class MusicRoom extends ScrollSelectableMessageWindow {

	private List<Sound> list = new ArrayList<>();

	public MusicRoom(int x, int y, int w, int h, int line) {
		super(x, y, w, h, line);
		setLoop(true);
		setLine1select(false);

		list.addAll(SoundStorage.getInstance().filter(p -> p.getType() == SoundType.BGM));
		Collections.sort(list, (Sound o1, Sound o2) -> o1.getName().compareTo(o2.getName()));
		List<Text> t = list
				.stream()
				.map(p -> ((CachedSound) p).getBuilder().getVisibleName())
				.map(p -> new Text(p))
				.collect(Collectors.toList());
		t.add(0, new Text("--" + "BGM"));
		setText(t);
	}

	public void play() {
		list.forEach(p -> p.dispose());
		SoundStorage.getInstance().dispose();
		//play
		list.get(getSelectedIdx() - 1).load().stopAndPlay();
	}

	public Sound getSelectedSound() {
		return list.get(getSelectedIdx() - 1);
	}

	public CachedSound getSelectedCachedSound() {
		return (CachedSound) list.get(getSelectedIdx() - 1);
	}

}
