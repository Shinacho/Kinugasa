/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.ui;

import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 * テキストストレージのパスを保管する唯一の場所です。
 *
 * @vesion 1.0.0 - 2022/11/08_21:29:42<br>
 * @author Dra211<br>
 */
public class TextStorageStorage extends Storage<TextStorage> implements XMLFileSupport {

	private static final TextStorageStorage INSTANCE = new TextStorageStorage();

	public static TextStorageStorage getInstance() {
		return INSTANCE;
	}

	private TextStorageStorage() {
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile data = new XMLFile(filePath);
		if (!data.exists()) {
			throw new FileNotFoundException(filePath + " is not found");
		}
		XMLElement root = data.load().getFirst();

		for (XMLElement e : root.getElement("textFile")) {
			super.add(new TextStorage(e.getAttributes().get("name").getValue(),
					new XMLFile(e.getAttributes().get("data").getValue())));
		}
		data.dispose();

		printAll(System.out);

	}

}
