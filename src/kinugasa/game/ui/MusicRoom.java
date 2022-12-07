/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
import kinugasa.object.Sprite;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundMap;
import kinugasa.resource.sound.SoundStorage;

/**
 * BGMの一覧を表示し、再生可能にする画面表示の実装です。アクションテキストスプライトグループを使用しています。
 *
 * @vesion 1.0.0 - 2022/11/13_0:36:02<br>
 * @author Dra211<br>
 */
public class MusicRoom extends ActionTextSpriteGroup {

	private float w, h;
	private String mapName;
	private float maxY;

	public MusicRoom(String mapName, float w, float h) {
		super(10, 40, 38, Collections.<ActionTextSprite>emptyList(), 0);
		this.w = w;
		this.h = h;
		this.mapName = mapName;
		this.maxY = h - 12 - 38 - 24;
		create(mapName);
		updateLocation(40, 38);
		updateSelectIcon();
	}

	private void create(String mapName) {
		SoundMap map = SoundStorage.getInstance().get(mapName);
		List<ActionTextSprite> list = new ArrayList<>();
		List<Sound> soundList = map.asList();
		Collections.sort(soundList, (Sound o1, Sound o2) -> o1.getName().compareTo(o2.getName()));
		for (Sound s : soundList) {
			list.add(new ActionTextSprite(s.getName().split("[.]")[0], new SimpleTextLabelModel(FontModel.DEFAULT.clone().setFontSize(14)), 0, 0, 200, 14, new PlaySoundAction(map, s)));
		};
		setList(list);
		updateSelectIcon();
	}

	@Override
	public void updateLocation(float x, float y) {
		List<ActionTextSprite> list = getList();
		if (list.isEmpty()) {
			return;
		}
		//要素の座標調整
		float xx = x;
		float yy = y;
		int c = -1;
		for (int i = 0; i < list.size(); i++, c++) {
			if (yy > maxY) {
				xx += list.get(0).getWidth() + (getBuffer() * 2);
				yy = y;
				c = -1;
			} else {
				yy = y + c * list.get(0).getHeight() + (getBuffer() * 2);
			}
			list.get(i).setLocation(xx, yy);
			if (c > max) {
				max = c;
			}
		}
		max += 2;
	}
	private int max = 0;

	public void nextColumn() {
		int v = getSelectedIdx() + max;
		if (v > getList().size()) {
			v = getList().size() - 1;
		}
		setSelectedIdx(v);
	}

	public void prevColumn() {
		int v = getSelectedIdx() - max;
		if (v < 0) {
			v = 0;
		}
		setSelectedIdx(v);
	}

	class PlaySoundAction extends Action {

		private SoundMap map;
		private Sound s;

		public PlaySoundAction(SoundMap map, Sound s) {
			this.map = map;
			this.s = s;
		}

		@Override
		public void exec() {
			map.forEach(p -> p.dispose());
			s.load();
			if (s.isPlaying()) {
				s.stop();
			} else {
				s.load().stopAndPlay();;
			}
		}
	}

}
