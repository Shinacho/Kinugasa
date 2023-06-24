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
