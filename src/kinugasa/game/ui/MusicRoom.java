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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.I18N;
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
		Collections.sort(list, (Sound o1, Sound o2) -> o1.getFileName().compareTo(o2.getFileName()));
		List<Text> t = list
				.stream()
				.map(p -> ((CachedSound) p).getBuilder().getVisibleName().replaceAll(".wav", ""))
				.map(p -> Text.i18nd(p))
				.collect(Collectors.toList());
		t.add(0, Text.of("--" + "BGM"));
		setText(t);
	}

	public void dispose() {
		list.forEach(p -> p.dispose());
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
