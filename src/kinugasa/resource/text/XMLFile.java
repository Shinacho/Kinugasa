/*
 * The MIT License
 *
 * Copyright 2013 Shinacho.
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
package kinugasa.resource.text;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import kinugasa.game.GameLog;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.Input;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.Output;
import kinugasa.resource.OutputResult;
import kinugasa.resource.FileNotFoundException;

/**
 * XMLファイルの展開とデータの管理を行います.
 * <br>
 * XMLデータはXMLElementクラスを使用し、木構造で表現されます。 ロードされたXMLReaderが持つノードは、ルートの1つのみです。<br>
 * <br>
 *
 * @version 1.0.0 - 2013/03/15_11:48:01.<br>
 * @author Shinacho
 * <a href="mailto:d0211@live.jp">d0211@live.jp</a>&nbsp;).<br>
 * <br>
 */
public final class XMLFile implements Input<XMLFile>, Output, Nameable, Iterable<XMLElement> {

	private File file;

	public XMLFile(File file) throws FileNotFoundException {
		this.file = file;
	}

	public XMLFile(String filePath) throws FileNotFoundException {
		this(new File(filePath));
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public String getName() {
		return file.getName();
	}

	private static DocumentBuilderFactory openBuilderFactory() {
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setValidating(true);
		builderFactory.setIgnoringComments(true);
		builderFactory.setIgnoringElementContentWhitespace(true);
		return builderFactory;
	}

	private List<XMLElement> data;

	@Override
	public XMLFile load() throws IllegalXMLFormatException, FileNotFoundException, FileIOException {
		data = new ArrayList<>();
		DocumentBuilderFactory builderFactory = openBuilderFactory();

		Document document = null;
		try {
			DocumentBuilder builder = builderFactory.newDocumentBuilder();
			builder.setErrorHandler(null);
			document = builder.parse(file);
		} catch (SAXException | ParserConfigurationException ex) {
			throw new IllegalXMLFormatException(ex);
		} catch (java.io.FileNotFoundException ex) {
			throw new FileNotFoundException(ex);
		} catch (java.io.IOException ex) {
			throw new ContentsIOException(ex);
		}
		assert document != null : "document is null";

		data.add(XMLParserUtil.createElement(document.getLastChild()));
		GameLog.printInfoIfUsing("XMLFile [" + file.getName() + "] is loaded");
		return this;
	}

	@Override
	public void dispose() {
		data.clear();
		data = null;
	}

	@Override
	public InputStatus getStatus() {
		return data == null ? InputStatus.NOT_LOADED : InputStatus.LOADED;
	}

	@Override
	public OutputResult save() throws FileIOException {
		//　XML出力はまだできません。
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public OutputResult saveTo(File f) throws FileIOException {
		//　XML出力はまだできません。
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	public XMLFile add(XMLElement... e) {
		data.addAll(Arrays.asList(e));
		return this;
	}

	public XMLFile remove(XMLElement... e) {
		data.removeAll(Arrays.asList(e));
		return this;
	}

	public boolean contains(XMLElement e) {
		return data.contains(e);
	}

	public boolean contains(String e) {
		return data.stream().anyMatch(xe -> (xe.getName().equals(e)));
	}

	public XMLElement getFirst() {
		if (getStatus() != InputStatus.LOADED) {
			throw new IllegalStateException("this file is not loaded : " + getName());
		}
		return data.get(0);
	}

	@Override
	public Iterator<XMLElement> iterator() {
		return data.iterator();
	}

	public boolean exists() {
		return file.exists();
	}

	@Override
	public String toString() {
		return "XMLFile{" + "file=" + file.getName() + '}';
	}

}
