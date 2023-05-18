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
package kinugasa.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import kinugasa.game.GameLog;

/**
 * セーブ可能なコンテンツのIO機能を提供します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/01/12_23:39:38<br>
 * @author Shinacho<br>
 */
public final class ContentsIO {

	private ContentsIO() {
	}

	/**
	 * コンテンツを指定されたファイルパスに発行します.
	 *
	 * @param <T> 発行可能なクラスを指定します。<br>
	 * @param obj 発行するオブジェクトを指定します。<br>
	 * @param filePath 発行するファイルパスを指定します。<br>
	 *
	 * @throws FileNotFoundException 発行できないファイルパスを指定した場合に投げられます。<br>
	 * @throws ContentsIOException その他の理由によって発行できなかった場合に投げられます。 <br>
	 */
	public static <T extends Serializable> void save(T obj, String filePath)
			throws FileNotFoundException, ContentsIOException {
		save(obj, new File(filePath));
	}

	/**
	 * コンテンツを指定されたファイルに発行します.
	 *
	 * @param <T> 発行可能なクラスを指定します。<br>
	 * @param obj 発行するオブジェクトを指定します。<br>
	 * @param file 発行するファイルを指定します。<br>
	 *
	 * @throws FileNotFoundException 発行できないファイルパスを指定した場合に投げられます。<br>
	 * @throws ContentsIOException その他の理由によって発行できなかった場合に投げられます。 <br>
	 */
	public static <T extends Serializable> void save(T obj, File file)
			throws FileNotFoundException, ContentsIOException {
		ObjectOutputStream stream = null;
		try {
			stream = new ObjectOutputStream(new FileOutputStream(file));
			stream.writeObject(obj);
		} catch (java.io.FileNotFoundException ex) {
			throw new FileNotFoundException(ex);
		} catch (IOException ex) {
			throw new ContentsIOException(ex);
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				throw new ContentsIOException(ex);
			} finally {
				GameLog.printInfoIfUsing("> ContentsIO : save : obj=[" + obj + "] file=[" + file.getPath() + "]");
			}
		}
	}

	/**
	 * コンテンツを指定されたファイルパスから読み込み、状態を復元します.
	 *
	 * @param <T> 発行可能なクラスを指定します。<br>
	 * @param type 読み込む型を指定します。<br>
	 * @param filePath 読み込むファイルパスを指定します。<br>
	 *
	 * @return 読み込まれたコンテンツを返します。<br>
	 *
	 * @throws FileNotFoundException 読み込めないファイルパスを指定した場合に投げられます。<br>
	 * @throws ContentsIOException その他の理由によって発行できなかった場合に投げられます。
	 * 型Tが読み込まれた実際の型と異なる場合も投げられます。<br>
	 */
	public static <T extends Serializable> T load(Class<T> type, String filePath)
			throws FileNotFoundException, ContentsIOException {
		return load(type, new File(filePath));
	}

	/**
	 * コンテンツを指定されたファイルパスから読み込み、状態を復元します.
	 *
	 * @param <T> 発行可能なクラスを指定します。<br>
	 * @param type 読み込む型を指定します。<br>
	 * @param file 読み込むファイルパスを指定します。<br>
	 *
	 * @return 読み込まれたコンテンツを返します。<br>
	 *
	 * @throws FileNotFoundException 読み込めないファイルパスを指定した場合に投げられます。<br>
	 * @throws ContentsIOException その他の理由によって発行できなかった場合に投げられます。
	 * 型Tが読み込まれた実際の型と異なる場合も投げられます。<br>
	 */
	public static <T extends Serializable> T load(Class<T> type, File file)
			throws FileNotFoundException, ContentsIOException {
		ObjectInputStream stream = null;
		try {
			stream = new ObjectInputStream(new FileInputStream(file));
			return type.cast(stream.readObject());
		} catch (java.io.FileNotFoundException ex) {
			throw new FileNotFoundException(ex);
		} catch (IOException ex) {
			throw new ContentsIOException(ex);
		} catch (ClassNotFoundException ex) {
			throw new ContentsIOException(ex);
		} finally {
			try {
				stream.close();
			} catch (IOException ex) {
				throw new ContentsIOException(ex);
			} finally {
				GameLog.printInfoIfUsing("> ContentsIO : load : type=[" + type + "] file=[" + file.getPath() + "]");
			}
		}
	}
}
