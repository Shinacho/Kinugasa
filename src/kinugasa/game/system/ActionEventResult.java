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
import kinugasa.object.AnimationSprite;
import kinugasa.object.Sprite;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_22:29:56<br>
 * @author Shinacho<br>
 */
public class ActionEventResult {

	private List<ActionResultType> resultTypePerTgt;
	private List<Sprite> animation;

	public ActionEventResult() {
		this(new ArrayList<>(), new ArrayList<>());
	}

	public ActionEventResult(ActionResultType resultTypePerTgt, AnimationSprite animation) {
		this(List.of(resultTypePerTgt), animation == null ? Collections.emptyList() : List.of(animation));
	}

	public ActionEventResult(List<ActionResultType> resultTypePerTgt, List<? extends Sprite> animation) {
		this.resultTypePerTgt = resultTypePerTgt;
		this.animation = new ArrayList<>(animation);
	}

	public ActionEventResult add(ActionEventResult a) {
		animation.addAll(a.animation);
		resultTypePerTgt.addAll(a.resultTypePerTgt);
		return this;
	}

	public void addAnimation(AnimationSprite a) {
		animation.add(a);
	}

	public void addResultTypePerTgt(ActionResultType t) {
		resultTypePerTgt.add(t);
	}

	public List<Sprite> getAnimation() {
		return animation;
	}

	public List<ActionResultType> getResultTypePerTgt() {
		return resultTypePerTgt;
	}

	@Override
	public String toString() {
		return "ActionEventResult{" + "resultTypePerTgt=" + resultTypePerTgt + ", animation=" + animation + '}';
	}

}
