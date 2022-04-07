/*
 * The MIT License
 *
 * Copyright 2016 Dra.
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
package hexwarsim.fieldmap;

import hexwarsim.game.Team;
import java.awt.image.BufferedImage;
import kinugasa.contents.graphics.ARGBColor;
import kinugasa.contents.graphics.ImageEditor;
import kinugasa.contents.graphics.ImageUtil;
import kinugasa.contents.graphics.SerializableImage;
import kinugasa.fieldObject.ChipAttributeStorage;
import kinugasa.fieldObject.MapChip;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2016/07/15<br>
 * @author Dra<br>
 * <br>
 */
public class Building {

	private Team currentTeam;
	private MapChip mapChip;

	public Building(Team team) {

	}

	public Building(MapChip mapChip, Team currentTeam) {
		this.mapChip = mapChip;
		this.currentTeam = currentTeam;
		updateImage();
	}

	public final void updateImage() {
		if (Team.NEUTRAL.equals(this.currentTeam)) {
			return;
		}
		BufferedImage image = mapChip.getImage();
		ImageEditor.replaceColor(mapChip.getImage(), ARGBColor.WHITE, ARGBColor.toARGB(currentTeam.getColor()), image);
		this.mapChip.setImage(new SerializableImage(image));
	}

	//null‹–‰Â
	public MapChip getMapChip() {
		return mapChip;
	}

	public BufferedImage getChipImage() {
		return mapChip == null ? null : mapChip.getImage();
	}

	public Team getTeam() {
		return currentTeam;
	}

	public static Building createDummyChip(int w, int h) {
		return new Building(new MapChip("BUILDING_DUMMMY", new SerializableImage(ImageUtil.newImage(w, h)), ChipAttributeStorage.getInstance().get("VOID")), Team.NOTHING);
	}
}
