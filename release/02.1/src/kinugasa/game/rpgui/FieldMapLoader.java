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

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import kinugasa.resource.ContentsIOException;
import kinugasa.resource.DuplicateNameException;
import kinugasa.resource.text.FileNotFoundException;
import kinugasa.resource.text.IllegalXMLFormatException;

/**
 * フィールドマップに関する様々なファイルをロードします.
 * <br>
 * このクラスを利用して、様々なXMLファイルを正しい順番でロードできます。<br>
 * <br>
 * @version 1.0.0 - 2013/04/29_10:57:37<br>
 * @author Dra0211<br>
 */
public final class FieldMapLoader {

	/**
	 * 新しいFieldMapLoaderを作成します.
	 */
	public FieldMapLoader() {
		attributeFileList = new ArrayList<String>();
		vehicleFileList = new ArrayList<String>();
		chipSetFileList = new ArrayList<String>();
		fieldMapBuiderFileList = new ArrayList<String>();
	}
	/** チップ属性ファイルのパス. */
	private List<String> attributeFileList;
	/** 移動手段ファイルのパス. */
	private List<String> vehicleFileList;
	/** チップセットファイルのパス. */
	private List<String> chipSetFileList;
	/** フィールドマップビルだのファイルパス. */
	private List<String> fieldMapBuiderFileList;

	/**
	 * チップ属性ファイルを追加します.
	 * @param filePath ファイルパスを指定します。<br>
	 * @return thisインスタンスを返します。<br>
	 */
	public FieldMapLoader attribute(String filePath) {
		attributeFileList.add(filePath);
		return this;
	}

	/**
	 * 移動手段ファイルを追加します.
	 * @param filePath ファイルパスを指定します。<br>
	 * @return thisインスタンスを返します。<br>
	 */
	public FieldMapLoader vehicle(String filePath) {
		vehicleFileList.add(filePath);
		return this;
	}

	/**
	 * チップセットファイルを追加します.
	 * @param filePath ファイルパスを指定します。<br>
	 * @return thisインスタンスを返します。<br>
	 */
	public FieldMapLoader chipSet(String filePath) {
		chipSetFileList.add(filePath);
		return this;
	}

	/**
	 * フィールドマップビルダファイルを追加します.
	 * @param filePath ファイルパスを指定します。<br>
	 * @return thisインスタンスを返します。<br>
	 */
	public FieldMapLoader fieldMapBuilder(String filePath) {
		fieldMapBuiderFileList.add(filePath);
		return this;
	}

	/**
	 * 全てのファイルをロードします.
	 * @throws IllegalXMLFormatException XMLファイルフォーマットに関する例外です。<br>
	 * @throws ContentsFileNotFoundException 指定されたファイルが存在しない場合に投げられます。<br>
	 * @throws ContentsIOException ファイルがロードできない場合に投げられます。<br>
	 * @throws NumberFormatException 数値が変換できない場合に投げられます。<br>
	 * @throws DuplicateNameException 一意的でなければならない名前が重複した際に投げられます。<br>
	 */
	public void load()
			throws IllegalXMLFormatException, FileNotFoundException,
			ContentsIOException, NumberFormatException,
			DuplicateNameException {
		load(null);
	}

	/**
	 * 全てのファイルをロードします.
	 * @param stream nullでない場合、追加されたオブジェクトの情報が送信されます。<br>
	 * @throws IllegalXMLFormatException XMLファイルフォーマットに関する例外です。<br>
	 * @throws ContentsFileNotFoundException 指定されたファイルが存在しない場合に投げられます。<br>
	 * @throws ContentsIOException ファイルがロードできない場合に投げられます。<br>
	 * @throws NumberFormatException 数値が変換できない場合に投げられます。<br>
	 * @throws DuplicateNameException 一意的でなければならない名前が重複した際に投げられます。<br>
	 */
	public void load(PrintStream stream)
			throws IllegalXMLFormatException,FileNotFoundException,
			ContentsIOException, NumberFormatException,
			DuplicateNameException {

		for (int i = 0, size = attributeFileList.size(); i < size; i++) {
			ChipAttributeStorage.getInstance().readFromXML(attributeFileList.get(i));
		}
		for (int i = 0, size = vehicleFileList.size(); i < size; i++) {
			VehicleStorage.getInstance().readFromXML(vehicleFileList.get(i));
		}
		for (int i = 0, size = chipSetFileList.size(); i < size; i++) {
			ChipSetStorage.getInstance().readFromXML(chipSetFileList.get(i));
		}
		for (int i = 0, size = fieldMapBuiderFileList.size(); i < size; i++) {
			FieldMapBuilderStorage.getInstance().readFromXML(fieldMapBuiderFileList.get(i));
		}

		if (stream != null) {
			ChipAttributeStorage.getInstance().printAll(stream, true);
			VehicleStorage.getInstance().printAll(stream, true);
			ChipSetStorage.getInstance().printAll(stream, true);
			FieldMapBuilderStorage.getInstance().printAll(stream, true);
			stream.println("> " + FieldMapBuilderStorage.getInstance());
		}
	}

	@Override
	public String toString() {
		return "FieldMapLoader{" + "attributeFileList=" + attributeFileList
				+ ", vehicleFileList=" + vehicleFileList
				+ ", chipSetFileList=" + chipSetFileList + '}';
	}
}
