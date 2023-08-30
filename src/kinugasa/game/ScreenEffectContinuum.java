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
package kinugasa.game;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * スクリーンエフェクト連続体は、複数のスクリーンエフェクトを終わり次第順番に適用するためのスクリーンエフェクトの拡張です。
 *
 * @vesion 1.0.0 - 2023/07/20_12:37:25<br>
 * @author Shinacho<br>
 */
public class ScreenEffectContinuum implements ScreenEffect {

	private int idx = 0;
	private List<ScreenEffect> effects = new ArrayList<>();
	private boolean ended = false;

	public ScreenEffectContinuum() {
	}

	public ScreenEffectContinuum(ScreenEffect... v) {
		this(Arrays.asList(v));

	}

	public ScreenEffectContinuum(List<ScreenEffect> v) {
		effects.addAll(v);
	}

	public ScreenEffectContinuum add(ScreenEffect e) {
		effects.add(e);
		return this;
	}

	public List<ScreenEffect> getEffects() {
		return effects;
	}

	@Override
	public BufferedImage doIt(BufferedImage src) {
		if (ended) {
			return src;
		}
		BufferedImage res = effects.get(idx).doIt(src);
		if (effects.get(idx).isEnded()) {
			idx++;
			if (idx >= effects.size()) {
				ended = true;
			}
		}
		return res;
	}

	@Override
	public boolean isEnded() {
		return ended;
	}

	@Override
	public boolean isRunning() {
		return !ended;
	}

}
