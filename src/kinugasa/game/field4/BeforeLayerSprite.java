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
package kinugasa.game.field4;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.List;
import kinugasa.game.GraphicsContext;
import kinugasa.graphics.ImageEditor;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.ImageSprite;
import kinugasa.object.KVector;
import kinugasa.resource.Disposable;
import kinugasa.resource.KImage;
import kinugasa.resource.Nameable;
import kinugasa.resource.TempFile;
import kinugasa.resource.TempFileStorage;

/**
 * このクラスは、画面前面に雲のエフェクトを表示するための画像スプライトです。雲以外にも使えるかもしれません。
 * <br>
 * 雲画像はGIMPを使うと簡単に生成できます。<br>
 * このクラスに設定した画像はループされ、描画時に自動的に移動されます。また、フィールドマップへの速度や角度の変更の影響を受けません。
 *
 * @version 1.0.0 - 2015/06/16<br>
 * @author Shinacho<br>
 * <br>
 */
public class BeforeLayerSprite extends ImageSprite implements Disposable, Nameable {

	private String name;
	private TempFile t;

	public BeforeLayerSprite(String name, BufferedImage image, float tp, float mg, KVector v) throws IllegalArgumentException {
		super(0, 0, image.getWidth() * mg, image.getHeight() * mg, ImageEditor.transparent(ImageEditor.resize(image, mg), tp, null));
		this.name = name;
		setVector(v);
		t = TempFileStorage.getInstance().create();
		ImageUtil.save(t.getPath(), image);
		BeforeLayerSpriteStorage.getInstance().add(this);
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		int x = (int) getX();
		int y = (int) getY();
		int w = (int) getWidth();
		int h = (int) getHeight();

		while (x > 0) {
			x -= w;
		}
		while (y > 0) {
			y -= h;
		}

		BufferedImage image = getImage().get();
		g.drawImage(image, x, y);
		g.drawImage(image, x + w, y);
		g.drawImage(image, x, y + h);
		g.drawImage(image, x + w, y + h);

		super.move();
	}

	@Override
	public String getName() {
		return name;
	}

	public void load() {
		setImage(ImageUtil.load(t.getPath()));
	}

	@Override
	public void dispose() {
		setImage((BufferedImage) null);
	}

}
