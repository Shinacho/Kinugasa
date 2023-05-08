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

import kinugasa.resource.FileNotFoundException;

/**
 * このインターフェースを実装したクラスは、
 * XMLファイルからデータをロードできます.
 * <br>
 * 必要であれば、Freeableを実装してください。<br>
 * <br>
 * @version 1.0.0 - 2013/04/28_22:06:10<br>
 * @author Shinacho<br>
 */
public interface XMLFileSupport {

	/**
	 * コンテンツをXMLからロードします.
	 * ほとんどの実装では、ストレージに対するデータの追加を行います。<br>
	 * @param filePath ロードするXMLファイルのパスを指定します。<br>
	 * @throws IllegalXMLFormatException XMLフォーマットがDTDに適合しない場合などに投げることができます。<br>
	 * @throws FileNotFoundException 指定されたファイルが存在しない場合に投げられます。<br>
	 * @throws FileIOException 指定されたファイルがロードできない場合に投げられます。<br>
	 */
	public void readFromXML(String filePath)
			throws IllegalXMLFormatException, FileNotFoundException,
			FileIOException;
}
