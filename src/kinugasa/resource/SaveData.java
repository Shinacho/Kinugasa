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
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * ゲームの途中経過を保存するためのクラスです.
 * <br>
 * このクラスを拡張して、必要なフィールドを定義してください。<br>
 * <br>
 * セーブデータの持つメタ情報としては、名前、作成時刻、更新日時があります。<br>
 * 名前と更新日時は自由に使用することができます。名前はNameableのキーとして
 * 使用されます。名前と作成日時は、変更できません。<br>
 *
 * @version 1.0.0 - 2013/01/13_0:10:27<br>
 * @author Shinacho<br>
 */
public abstract class SaveData implements Nameable, Serializable {

	private static final long serialVersionUID = 5051478173983169607L;
	/** セーブデータの名前です. */
	private String name;
	/** セーブデータが作成された日時です. */
	private long createTime;
	/** 最終更新などを保管するDateです. */
	private Date date;

	/**
	 * 新しいセーブデータを作成します.
	 * このコンストラクタでは、名前は空の文字列に設定されます。<br>
	 */
	public SaveData() {
		this("");
	}

	/**
	 * 新しいセーブデータを作成します.
	 * 作成日時および更新日時は現在の時刻に設定されます。<br>
	 *
	 * @param name セーブデータの名前を指定します。一意的である必要があります。<br>
	 */
	public SaveData(String name) {
		this.name = name;
		createTime = System.currentTimeMillis();
		date = new Date();
	}

	@Override
	public final String getName() {
		return name;
	}

	/**
	 * このセーブデータの更新日時を取得します.
	 *
	 * @return このセーブデータに設定された更新日時を返します。<br>
	 */
	public final Date getDate() {
		return date;
	}

	/**
	 * このセーブデータの更新日時を設定します.
	 *
	 * @param date このセーブデータに設定する更新日時を送信します。<br>
	 */
	public final void setDate(Date date) {
		this.date = date;
	}

	/**
	 * このセーブデータが最初に作成された時刻を取得します.
	 * この値は、通常は一意的です。ただし、この値はミリ秒単位なので、
	 * 短時間に複数のセーブデータを生成した場合は、重複する可能性があります。<br>
	 *
	 * @return このセーブデータが作成された時刻を返します。<br>
	 */
	public final long getCreateTime() {
		return createTime;
	}

	/**
	 * 指定されたファイルに、このセーブデータを保存します.
	 * 保存したファイルはContentsIOを使用して復元できます。<Br>
	 *
	 * @param file 発行するファイルを指定します。上書きの確認は行われません。<br>
	 *
	 * @throws ContentsFileNotFoundException ファイルパスが不正な場合に投げられます。<br>
	 * @throws ContentsIOException コンテンツのIOに失敗した場合に投げられます。<br>
	 */
	public final void save(File file) throws FileNotFoundException, ContentsIOException {
		ContentsIO.save(this, file);
	}

	/**
	 * 指定されたファイルから、セーブデータを読み込みます.
	 * このメソッドは、ContentsIOを使用したロードと同じ動作をします。<br>
	 *
	 * @param file 読み込むファイルを指定します。<br>
	 *
	 * @return 指定されたファイルから復元されたセーブデータを返します。<br>
	 *
	 * @throws ContentsFileNotFoundException ファイルパスが不正な場合に投げられます。<br>
	 * @throws ContentsIOException コンテンツのIOに失敗した場合に投げられます。<br>
	 */
	public static SaveData load(File file) throws FileNotFoundException, ContentsIOException {
		return ContentsIO.load(SaveData.class, file);
	}

	/**
	 * 指定されたパスにあるすべての読み込み可能なファイルをセーブデータとしてロードし、リストとして返します.
	 *
	 * @param dir 読み込むディレクトリのルートとなるディレクトリのパスを指定します。入れ子になったディレクトリは
	 * 再帰的に処理されます。<br>
	 *
	 * @return 指定されたディレクトリ以下にある、セーブデータの発行済みデータを復元し、リストとして返します。<br>
	 *
	 * @throws ContentsFileNotFoundException ファイルパスが不正な場合に投げられます。<br>
	 * @throws ContentsIOException コンテンツのIOに失敗した場合に投げられます。<br>
	 * @throws IllegalArgumentException dirがディレクトリでない場合に投げられます。<br>
	 */
	public static List<SaveData> loadAll(File dir) throws FileNotFoundException, ContentsIOException, IllegalArgumentException {
		if (!dir.isDirectory()) {
			throw new IllegalArgumentException("dir is not directory");
		}
		File[] files = dir.listFiles();
		List<SaveData> result = new ArrayList<SaveData>(files.length);
		for (File file : files) {
			if (file.isDirectory()) {
				result.addAll(loadAll(file));
			} else {
				try {
					result.add(ContentsIO.load(SaveData.class, file));
				} catch (FileNotFoundException ex) {
					continue;
				} catch (ContentsIOException ex) {
					continue;
				}
			}
		}
		return result;
	}

	@Override
	public String toString() {
		return "SaveData{" + "name=" + name + ", createTime=" + createTime + ", date=" + date + '}';
	}
}
