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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/21_10:42:47<br>
 * @author Shinacho<br>
 */
public class StatusDescWindow extends PCStatusWindow {

	private List<Status> s;
	private MessageWindow main;
	private BufferedImage faceImage;

	public StatusDescWindow(int x, int y, int w, int h, List<Status> s) {
		super(x, y, w, h);
		this.s = s;

		main = new MessageWindow(x, y, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		pcIdx = 0;
		faceImage = GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId()).getFaceImage();
		resizeFaceImage();
		update();
	}
	private int pcIdx;

	@Override
	public void setPcIdx(int pcIdx) {
		this.pcIdx = pcIdx;
		update();
	}

	@Override
	public MessageWindow getWindow() {
		return main;
	}

	@Override
	public void next() {
	}

	@Override
	public void prev() {
	}

	@Override
	public void nextPc() {
		pcIdx++;
		if (pcIdx >= s.size()) {
			pcIdx = 0;
		}
		faceImage = GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId()).getFaceImage();
		resizeFaceImage();
	}

	@Override
	public void prevPc() {
		pcIdx--;
		if (pcIdx < 0) {
			pcIdx = s.size() - 1;
		}
		faceImage = GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId()).getFaceImage();
		resizeFaceImage();
	}

	private void resizeFaceImage() {
		if (faceImage == null) {
			return;
		}
		faceImage = ImageUtil.resize(faceImage, 128 / faceImage.getWidth(), 128 / faceImage.getHeight());
	}

	@Override
	public int getPcIdx() {
		return pcIdx;
	}

	@Override
	public void update() {
		StringBuilder sb = new StringBuilder();
		sb.append("<---");
		sb.append(GameSystem.getInstance().getPCbyID(s.get(pcIdx).getId()).getVisibleName());
		sb.append("--->").append(Text.getLineSep());
		for (StatusValue v : s.get(pcIdx).getEffectedStatus().stream().sorted().collect(Collectors.toList())) {
			if (!v.getKey().isVisible()) {
				continue;
			}
			sb.append(" ");
			if (v.isUseMax()) {
				if (v.getKey().isPercent()) {
					float val = v.getValue() * 100;
					sb.append(v.getKey().getVisibleName()).append(":").append(val).append('%').append(Text.getLineSep());
				} else {
					int val = (int) v.getValue();
					int max = (int) v.getMax();
					sb.append(v.getKey().getVisibleName()).append(":").append(val).append('／').append(max).append(Text.getLineSep());
				}
			} else {
				if (v.getKey() == StatusKey.魔術使用可否) {
					sb.append(v.getKey().getVisibleName()).append(":")
							.append(
									v.getValue() == StatusKey.魔術使用可否＿使用可能
									? I18N.get(GameSystemI18NKeys.魔術利用可能) : I18N.get(GameSystemI18NKeys.魔術利用不可));
				} else if (v.getKey().isPercent()) {
					float val = v.getValue() * 100;
					sb.append(v.getKey().getVisibleName()).append(":").append(val).append('%').append(Text.getLineSep());
				} else {
					int val = (int) v.getValue();
					sb.append(v.getKey().getVisibleName()).append(":").append(val).append(Text.getLineSep());
				}
			}
			if (s.get(pcIdx).getAbility() != null) {
				sb.append(Text.getLineSep());
				sb.append(I18N.get(GameSystemI18NKeys.特性)).append(":");
				sb.append(s.get(pcIdx).getAbility().getDescI18Nd());
			}
		}

		main.setText(new Text(sb.toString()));
		main.allText();

	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		main.draw(g);
		if (faceImage != null) {
			int x = (int) (main.getX() + main.getWidth() - 128);
			int y = (int) (main.getY() + main.getHeight() - 128);
			g.drawImage(faceImage, x, y);
		}
	}

}
