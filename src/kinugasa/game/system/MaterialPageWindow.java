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
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.Text;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/25_16:31:21<br>
 * @author Shinacho<br>
 */
public class MaterialPageWindow extends BasicSprite {

	public MaterialPageWindow(int x, int y, int w, int h) {
		super(x, y, w, h);
		mw = new ScrollSelectableMessageWindow(x, y, w, h, 23, false);
		mw.setLoop(true);
		updateText();
	}
	private ScrollSelectableMessageWindow mw;

	public enum Mode {
		素材,
		術式,
	}
	private Mode mode = Mode.素材;

	private void updateText() {
		List<String> list = new ArrayList<>();
		switch (mode) {
			case 素材:
				Map<Material, Integer> map1 = GameSystem.getInstance().getMaterialBag().getMap();
				for (Map.Entry<Material, Integer> e : map1.entrySet()) {
					list.add(e.getKey().getName() + "、" + (I18N.get(GameSystemI18NKeys.価値) + ":" + e.getKey().getVisibleName()) + "×" + e.getValue());
				}
				break;
			case 術式:
				Map<BookPage, Integer> map2 = GameSystem.getInstance().getPageBag().getMap();
				for (Map.Entry<BookPage, Integer> e : map2.entrySet()) {
					list.add(e.getKey().getVisibleName() + "、" + (I18N.get(GameSystemI18NKeys.価値) + ":" + e.getKey().getPrice()) + "×" + e.getValue());
				}
				break;
		}
		Collections.sort(list);
		if (list.isEmpty()) {
			list.add(I18N.get(GameSystemI18NKeys.何も持っていない));
		}

		list.add(0, "<----" + I18N.get(mode) + "---->");

		mw.setText(list.stream().map(p -> Text.noI18N(p)).collect(Collectors.toList()));
	}

	@Override
	public void update() {
		mw.update();
	}

	public void switchMode() {
		switch (mode) {
			case 素材:
				mode = Mode.術式;
				break;
			case 術式:
				mode = Mode.素材;
				break;
		}
		updateText();
		mw.reset();
	}

	public void prev() {
		mw.prevSelect();
	}

	public void next() {
		mw.nextSelect();
	}

	public MessageWindow getWindow() {
		return mw.getWindow();
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		mw.draw(g);
	}

}
