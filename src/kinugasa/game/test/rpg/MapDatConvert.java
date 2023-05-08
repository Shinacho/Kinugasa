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
package kinugasa.game.test.rpg;

import java.io.File;
import javax.swing.JFileChooser;
import kinugasa.game.field4.FieldMapResourceUtil;

/**
 *
 * @vesion 1.0.0 - 2022/12/15_20:16:47<br>
 * @author Shinacho<br>
 */
public class MapDatConvert {

	public static void main(String[] args) {
		JFileChooser f = new JFileChooser("D:/Project/FuzzyWorld1/resource/data/map/raw/");
		f.showOpenDialog(null);
		File file = f.getSelectedFile();
		File out = new File("D:/Project/FuzzyWorld1/resource/data/map/" + file.getName());
		if (out.exists()) {
			out.delete();
		}
		//inner
		//*
		FieldMapResourceUtil.platinumCsvType1ToKGCsv(file, out, 8, 8);
		//outer 
		/*/
		FieldMapResourceUtil.platinumCsvType1ToKGCsv(file, out, 28, 16);
		//*/
	}
}
