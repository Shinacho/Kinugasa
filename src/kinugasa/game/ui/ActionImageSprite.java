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

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kinugasa.object.ImagePainter;
import kinugasa.object.ImageSprite;
import kinugasa.object.MovingModel;
import kinugasa.object.KVector;
import kinugasa.resource.KImage;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/09/27<br>
 * @author Shinacho<br>
 * <br>
 */
public class ActionImageSprite extends ImageSprite implements ActionSupport {

	private List<UIAction> actions;

	public ActionImageSprite(float x, float y, float w, float h, UIAction... actions) {
		super(x, y, w, h);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float x, float y, float w, float h, BufferedImage image, UIAction... actions) {
		super(x, y, w, h, image);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float x, float y, float w, float h, KImage image, UIAction... actions) {
		super(x, y, w, h, image);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float w, float h, KVector vector, MovingModel mm, BufferedImage image, ImagePainter dm, UIAction... actions) {
		super(w, h, vector, mm, image, dm);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float w, float h, KVector vector, MovingModel mm, KImage image, ImagePainter dm, UIAction... actions) {
		super(w, h, vector, mm, image, dm);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float x, float y, float w, float h, BufferedImage image, ImagePainter model, UIAction... actions) {
		super(x, y, w, h, image, model);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float x, float y, float w, float h, KImage image, ImagePainter model, UIAction... actions) {
		super(x, y, w, h, image, model);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float x, float y, float w, float h, KVector vector, MovingModel mm, BufferedImage image, ImagePainter dm, UIAction... actions) {
		super(x, y, w, h, vector, mm, image, dm);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	public ActionImageSprite(float x, float y, float w, float h, KVector vector, MovingModel mm, KImage image, ImagePainter dm, UIAction... actions) {
		super(x, y, w, h, vector, mm, image, dm);
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public void addAction(UIAction... actions) {
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public UIAction[] getActions() {
		return (UIAction[]) actions.toArray();
	}

	@Override
	public UIAction getAction() {
		return actions.isEmpty() ? null : actions.get(0);
	}

	@Override
	public void setActions(UIAction... actions) {
		this.actions = new ArrayList<UIAction>();
		this.actions.addAll(Arrays.asList(actions));
	}

	@Override
	public void exec() {
		for (int i = 0, size = actions.size(); i < size; i++) {
			actions.get(i).exec();
		}
	}

	@Override
	public void clearActions() {
		this.actions.clear();
	}

	private boolean selected = false;

	@Override
	public void setSelected(boolean selected) {
		this.selected = selected;
	}

	@Override
	public void selected() {
		selected = true;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

}
