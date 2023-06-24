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

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import kinugasa.resource.*;
import kinugasa.resource.text.*;
import kinugasa.graphics.*;

/**
 *
 * @vesion 1.0.0 - 2022/11/14_8:56:58<br>
 * @author Shinacho<br>
 */
public class FieldMapResourceUtil {

	private FieldMapResourceUtil() {
	}

	/**
	 * .
	 * <br>
	 * Type1かつ、パーツが16bitである必要があります。<br>
	 * <br>
	 *
	 * @param input
	 * @param output
	 * @param w イメージのタイル数。
	 * @param h イメージのタイル数。
	 * @throws ContentsIOException
	 */
	public static void platinumCsvType1ToKGCsv(File input, File output, int h, int w)
			throws ContentsIOException {
		if (!input.exists() | output.exists()) {
			throw new ContentsIOException("File is Already Exists");
		}

		CSVFile reader = new CSVFile(input);
		CSVFile writer = new CSVFile(output);

		String[] convertTable = new String[65536];
		for (int i = 0, k = 0; i < h; i++) {
			for (int j = 0; j < w; j++) {
				String y = Integer.toString(i);
				while (y.length() != 3) {
					y = "0" + y;
				}
				String x = Integer.toString(j);
				while (x.length() != 3) {
					x = "0" + x;
				}
				convertTable[k++] = y + x;
			}
		}
		List<String[]> inputData = reader.load().getData();

		for (String[] inputLine : inputData) {
			if (inputLine.length <= 0) {
				continue;
			}
			if (inputLine[0].equals("")) {
				writer.add(new String[]{"-"});
				continue;
			}
			String[] outputLine = new String[inputLine.length];
			for (int j = 0; j < inputLine.length; j++) {
				outputLine[j] = convertTable[Integer.parseInt(inputLine[j])];
			}
			writer.add(outputLine);
		}
		reader.dispose();
		writer.save();
		kinugasa.game.GameLog.print("変換は正常に終了しました");
	}

	/**
	 * splitAsMapN(3桁)で切り出せる画像のIDのセットを出力します。 これはチップセットXMLを構築する際に有用です。<br>
	 *
	 * @param filePath チップセット画像のパス<br>
	 */
	public static void printImageId(String filePath, int w, int h) {
		BufferedImage image = ImageUtil.load(filePath);

		Map<String, BufferedImage> map = ImageUtil.splitAsMapN(image, w, h, 3);
		List<String> keyList = new ArrayList<String>(map.keySet());
		Collections.sort(keyList);
		for (int i = 0, size = keyList.size(); i < size; i++) {
			kinugasa.game.GameLog.print(keyList.get(i));
		}
	}

	//outer16
	public static void main(String[] args) {
		String f1 = "D:/Project/FuzzyWorld/resource/data/map/raw/map06.csv";
		String f2 = "D:/Project/FuzzyWorld/resource/data/map/map06.csv";
		platinumCsvType1ToKGCsv(new File(f1), new File(f2), 48, 16);

	}
}
