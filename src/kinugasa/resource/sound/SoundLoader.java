/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
package kinugasa.resource.sound;

import java.util.Arrays;
import kinugasa.game.GameLog;
import kinugasa.game.system.GameSystem;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.text.CSVFile;
import kinugasa.resource.FileNotFoundException;

/**
 * BGMをフォルダからサウンドマップに一括設定するクラスです。. 指定したファイルに記載されているBGMを一括設定します。
 * ファイル形式は、CSVで、ファイル名、マスターゲイン、ループポイントFROM、TOの順で1レコード1件の形式です。
 * サウンドマップの名前は「BGM」になります。<br>
 * 作成されたサウンドマップはサウンドストレージに追加されます。<br>
 *
 * @vesion 1.0.0 - 2022/11/07_10:06:46<br>
 * @author Shinacho<br>
 */
public class SoundLoader {

	public static final String MAP_NAME = "BGM";

	private SoundLoader() {
	}
	private CSVFile file;

	public static SoundMap loadList(String fileName, float volume) throws FileNotFoundException {
		return loadList(new CSVFile(fileName), volume);
	}

	public static SoundMap loadList(CSVFile file, float volume) throws FileNotFoundException {
		if (!file.exists()) {
			throw new FileNotFoundException(file + " is not exists");
		}

		SoundMap map = new SoundMap(file.getName().split("[.]")[0]);
		file.load().getData().stream().map(line -> {
			if (line.length != 4 && line.length != 2) {
				throw new ContentsIOException(Arrays.toString(line) + " length is not 4 or 2");
			}
			return line;
		}).forEachOrdered(line -> {
			String name = file.getFile().getParent() + "/" + line[0];
			float mg = Float.parseFloat(line[1]) * volume;
			if (line.length == 4) {
				int from = line[2].equals("EOF") ? -1 : Integer.parseInt(line[2]);
				int to = line[3].equals("START") ? 0 : Integer.parseInt(line[3]);
				LoopPoint p = new LoopPoint(from, to);
				map.createCachedSound(new SoundBuilder(name).setMasterGain(mg).setLoopPoint(p));
			} else {
				map.createCachedSound(new SoundBuilder(name).setMasterGain(mg));
			}
		});

		SoundStorage.getInstance().add(map);
		return map;
	}

}
