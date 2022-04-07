/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.game.rpgui;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameLog;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.RenderingQuality;

/**
 * CSV形式のマップデータと画像を格納します.
 * <br>
 * このクラスはFreeableではありません。必要になったときインスタンス化されます。
 * <br>
 * このクラスはXML内のmapタグに相当します。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/05/04_17:06:00<br>
 * @author Dra0211<br>
 */
public class CSVMapData implements MapData {

	private static final long serialVersionUID = -4149183153471102531L;
	private ChipSet chipSet;
	public String[] csvValue;
	private MapChip[][] chips;
	private BufferedImage image;
	private int chipWidth, chipHeight;

	public CSVMapData(ChipSet chipSet, String[] values, int chipWidth, int chipHeight) {
		this.chipSet = chipSet;
		this.csvValue = values;
		this.chipWidth = chipWidth;
		this.chipHeight = chipHeight;
		builde();
	}

	@Override
	public void dispose() {
		chipSet = null;
		csvValue = null;
		chips = null;
		image = null;
		GameLog.printInfo("MapDataがdisposeされました chipSet=[" + chipSet + "]");
	}

	private void builde() {
		List<String[]> csvData = new ArrayList<String[]>(csvValue.length);
		int yChipNum = csvValue.length;
		int xChipNum = -1;
		for (String line : csvValue) {
			String[] lineData = line.split(",");
			if (xChipNum < lineData.length) {
				xChipNum = lineData.length;
			}
			csvData.add(lineData);
		}
		image = ImageUtil.newImage(xChipNum * (chipSet.getCutWidth() * (chipWidth / chipSet.getCutWidth())),
				yChipNum * (chipSet.getCutHeight() * (chipHeight / chipSet.getCutHeight())));
		Graphics2D g = ImageUtil.createGraphics2D(image, RenderingQuality.SPEED);
		chips = new MapChip[yChipNum][xChipNum];
		for (int y = 0; y < yChipNum; y++) {
			String[] line = csvData.get(y);
			chips[y] = new MapChip[line.length];
			for (int x = 0; x < xChipNum; x++) {
				chips[y][x] = chipSet.get(line[x]);
				g.drawImage(chips[y][x].getImage(),
						x * (chips[y][x].getImage().getWidth() * (chipWidth / chips[y][x].getImage().getWidth())),
						y * (chips[y][x].getImage().getHeight() * (chipHeight / chips[y][x].getImage().getHeight())),
						chips[y][x].getImage().getWidth() * (chipWidth / chips[y][x].getImage().getWidth()),
						chips[y][x].getImage().getHeight() * (chipHeight / chips[y][x].getImage().getHeight()),
						null);
			}
		}
		g.dispose();
	}

	@Override
	public ChipSet getChipSet() {
		return chipSet;
	}

	public String[] getCsvValue() {
		return csvValue;
	}

	@Override
	public MapChip[][] getChips() {
		return chips;
	}

	@Override
	public BufferedImage getImage() {
		return image;
	}

	@Override
	public String toString() {
		return "CSVMapData{" + "chipSet=" + chipSet + ", csvValue=" + csvValue
				+ ", chips=" + chips + ", image=" + image + '}';
	}
}
