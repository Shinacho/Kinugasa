/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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

import hexwarsim.game.GameConstants;
import java.io.File;
import java.util.Arrays;
import java.util.logging.Level;
import kinugasa.contents.resource.Freeable;
import kinugasa.contents.resource.IllegalFormatException;
import kinugasa.contents.resource.Loadable;
import kinugasa.contents.resource.Storage;
import kinugasa.contents.text.CSVReader;
import kinugasa.game.GameLog;
import kinugasa.util.StringUtil;

/**
 * .
 * <br>
 *
 * @version 1.0.0 - 2015/10/04<br>
 * @author Dra<br>
 * <br>
 */
public final class HexFieldMapStorage extends Storage<HexFieldMap> implements Loadable, Freeable {

	private static final HexFieldMapStorage INSTANCE = new HexFieldMapStorage();
	//
	private CSVReader indexFileReader;

	private HexFieldMapStorage() {
	}

	public static HexFieldMapStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public HexFieldMapStorage load() {
		//indexファイルのロード
		indexFileReader = new CSVReader(GameConstants.FIELDMAP_INDEX_FILE).load();
		int row = 0;
		for (String[] line : indexFileReader) {
			if (line.length != 3) {
				throw new IllegalFormatException("不正なフォーマット：" + Arrays.toString(line));
			}
			String fileName = line[0];
			String mapName = line[1];
			String info = line[2];

			if (!new File(GameConstants.FIELDMAP_DIR + fileName).exists()) {
				System.out.println("ファイルが存在しません：" + fileName);
				//TODO
			}
			add(new HexFieldMap((row++) + 1, fileName, mapName, info));
		}
		indexFileReader.free();
		return this;
	}

	@Override
	public boolean isLoaded() {
		return !isEmpty();
	}

	@Override
	public HexFieldMapStorage free() {
		for (HexFieldMap map : this) {
			map.free();
		}
		clear();
		return this;
	}

}
