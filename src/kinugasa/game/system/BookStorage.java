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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.List;
import kinugasa.resource.FileNotFoundException;
import kinugasa.resource.Storage;
import kinugasa.resource.text.FileIOException;
import kinugasa.resource.text.IllegalXMLFormatException;
import kinugasa.resource.text.XMLElement;
import kinugasa.resource.text.XMLFile;
import kinugasa.resource.text.XMLFileSupport;

/**
 *
 * @vesion 1.0.0 - 2022/11/23_18:56:31<br>
 * @author Shinacho<br>
 */
public class BookStorage extends Storage<Book> implements XMLFileSupport {

	private static final BookStorage INSTANCE = new BookStorage();

	private BookStorage() {
	}

	public static BookStorage getInstance() {
		return INSTANCE;
	}

	@Override
	public void readFromXML(String filePath) throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		XMLFile file = new XMLFile(filePath);
		if (!file.exists()) {
			throw new FileNotFoundException(file.getFile());
		}
		XMLElement root = file.load().getFirst();
		//ページ単体のロード
		for (XMLElement e : root.getElement("page")) {
			MagicCompositeType t = e.getAttributes().get("mct").of(MagicCompositeType.class);
			String name = e.getAttributes().get("name").getValue();
			String tgtName = null;
			if (e.hasAttribute("tgtName")) {
				tgtName = e.getAttributes().get("tgtName").getValue();
			}
			float value = 0f;
			if (e.hasAttribute("value")) {
				value = e.getAttributes().get("value").getFloatValue();
			}
			BookPageStorage.getInstance().add(new BookPage(t, name, tgtName, value));
		}
		BookPageStorage.getInstance().printAll(System.out);

		//本のロード
		for (XMLElement e : root.getElement("book")) {
			String name = e.getAttributes().get("name").getValue();
			String desc = e.getAttributes().get("desc").getValue();
			Book b = new Book(name, desc);
			if (e.hasAttribute("value")) {
				b.setValue(e.getAttributes().get("value").getIntValue());
			}
			if (e.hasElement("page")) {
				List<BookPage> pages = new ArrayList<>();
				for (XMLElement ee : e.getElement("page")) {
					MagicCompositeType t = ee.getAttributes().get("mct").of(MagicCompositeType.class);
					String name2 = ee.getAttributes().get("name").getValue();
					String tgtName = null;
					if (ee.hasAttribute("tgtName")) {
						tgtName = ee.getAttributes().get("tgtName").getValue();
					}
					float value = 0f;
					if (ee.hasAttribute("value")) {
						value = ee.getAttributes().get("value").getFloatValue();
					}
					BookPage p = new BookPage(t, name2, tgtName, value);
					pages.add(p);//本に設定するだけでストレージには入れない。ストレージは販売や宝箱ゲットを想定している。
				}
				b.setPages(pages);
			}
			getInstance().add(b);
		}
		printAll(System.out);
		file.dispose();
	}

}
