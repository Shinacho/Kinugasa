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

import java.time.Duration;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;
import kinugasa.util.StringUtil;

/**
 *
 * @vesion 1.0.0 - 2023/05/06_7:56:27<br>
 * @author Shinacho<br>
 */
public class InfoWindow extends BasicSprite {

	public InfoWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
		main = new ScrollSelectableMessageWindow(x, y, w, h, 23, false);
		main.setLoop(true);
		updateText();
	}

	public enum Mode {
		所持金,
		クエスト,
		統計,
		難易度
	}
	private Mode mode = Mode.所持金;
	private ScrollSelectableMessageWindow main;
	private LinkedList<Text> toukeiData = new LinkedList<>();
	private int sumS;

	public void nextMode() {
		mode = switch (mode) {
			case 所持金 ->
				Mode.クエスト;
			case クエスト ->
				Mode.統計;
			case 統計 ->
				Mode.難易度;
			case 難易度 ->
				Mode.所持金;
		};
		main.reset();
		updateText();
	}

	public void prevMode() {
		mode = switch (mode) {
			case 所持金 ->
				Mode.難易度;
			case クエスト ->
				Mode.所持金;
			case 統計 ->
				Mode.クエスト;
			case 難易度 ->
				Mode.統計;
		};
		main.reset();
		updateText();
	}

	public void nextSelect() {
		main.nextSelect();
	}

	public void prevSelect() {
		main.prevSelect();
	}

	private void updateText() {
		List<Text> t = new ArrayList<>();

		//line1
		t.add(Text.of("<---" + I18N.get(mode) + "--->"));

		//data
		switch (mode) {
			case 所持金:
				for (Money m : GameSystem.getInstance().getMoneySystem()) {
					t.add(Text.of(m.getVisibleText()));
				}
				break;
			case クエスト:
				//MAIN
				t.add(Text.of("--" + I18N.get(GameSystemI18NKeys.メインクエスト)));
				Set<Quest> q = CurrentQuest.getInstance().get();
				for (Quest qs : q.stream().filter(p -> p.getType() == Quest.Type.メイン).toList()) {
					t.add(Text.of("  " + qs.getVisibleName() + Text.getLineSep() + "   　　　 " + qs.getDesc().replaceAll("/", "/   　　　 ")));
				}

				//SUB
				t.add(Text.of("--" + I18N.get(GameSystemI18NKeys.サブクエスト)));
				for (Quest qs : q.stream().filter(p -> p.getType() == Quest.Type.サブ).toList()) {
					t.add(Text.of("  " + qs.getVisibleName() + Text.getLineSep() + "  " + qs.getDesc().replaceAll("/", "/   　　　 ")));
				}

				break;
			case 統計: {
				for (Counts.Value v : Counts.getInstance().selectAll().stream().sorted((c1, c2) -> {
					return c1.getVisibleName().compareTo(c2.getVisibleName());
				}).toList()) {
					if (!v.getName().contains("時間")) {
						t.add(Text.of("  " + v.getVisibleName() + " : " + v.num));
					}
				}
				総プレイ時間i18nd = I18N.get("総プレイ時間");
				起動からの経過時間i18nd = I18N.get("起動からの経過時間");
				t.add(Text.empty());
				Duration d = GameTimeManager.getInstance().get経過時間();
				int h = (int) (d.getSeconds() / 3600);
				int m = (int) ((d.getSeconds() % 3600) / 60);
				int s = (int) (d.getSeconds() % 60);

				{
					if (Counts.getInstance().select("総プレイ時間") == null) {
						sumS = 0;
					} else {
						sumS = (int) (Counts.getInstance().select("総プレイ時間").num);
					}
					int hh = (int) ((sumS + d.getSeconds()) / 3600);
					int mm = (int) (((sumS + d.getSeconds()) % 3600) / 60);
					int ss = (int) ((sumS + d.getSeconds()) % 60);
					t.add(Text.of("--" + 総プレイ時間i18nd + "：" + StringUtil.zeroUme(hh + "", 3) + ":" + StringUtil.zeroUme(mm + "", 2) + ":" + StringUtil.zeroUme(ss + "", 2)));
				}
				t.add(Text.of("--" + 起動からの経過時間i18nd + "：" + StringUtil.zeroUme(h + "", 3) + ":" + StringUtil.zeroUme(m + "", 2) + ":" + StringUtil.zeroUme(s + "", 2)));
				toukeiData.clear();
				toukeiData.addAll(t);

				//TODO：セーブデータの加算
				break;
			}
			case 難易度: {
				t.add(Text.of("  " + GameSystem.getDifficulty().getNameI18Nd()));
				for (String v : GameSystem.getDifficulty().getDescI18Nd().split("/")) {
					t.add(Text.of("    " + v));
				}
				t.add(Text.of(""));
				t.add(Text.i18nd(GameSystemI18NKeys.難易度を変更するには));
				break;
			}
			default:
				throw new AssertionError();
		}
		main.setText(t);
	}
	private String 総プレイ時間i18nd, 起動からの経過時間i18nd;

	@Override
	public void update() {
		if (mode == Mode.統計) {
			toukeiData.removeLast();
			toukeiData.removeLast();
			Duration d = GameTimeManager.getInstance().get経過時間();
			int h = (int) (d.getSeconds() / 3600);
			int m = (int) ((d.getSeconds() % 3600) / 60);
			int s = (int) (d.getSeconds() % 60);

			{
				int hh = (int) ((sumS + d.getSeconds()) / 3600);
				int mm = (int) (((sumS + d.getSeconds()) % 3600) / 60);
				int ss = (int) ((sumS + d.getSeconds()) % 60);
				toukeiData.add(Text.of("--" + 総プレイ時間i18nd + "：" + StringUtil.zeroUme(hh + "", 3) + ":" + StringUtil.zeroUme(mm + "", 2) + ":" + StringUtil.zeroUme(ss + "", 2)));
			}
			toukeiData.add(Text.of("--" + 起動からの経過時間i18nd + "：" + StringUtil.zeroUme(h + "", 3) + ":" + StringUtil.zeroUme(m + "", 2) + ":" + StringUtil.zeroUme(s + "", 2)));
			int selected = main.getSelectedIdx();
			main.setText(toukeiData);
			main.setSelectedIdx(selected);
		}
		main.update();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		main.draw(g);
	}

}
