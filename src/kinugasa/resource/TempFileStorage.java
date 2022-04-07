/*
 * The MIT License
 *
 * Copyright 2015 Dra.
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

import kinugasa.game.GameLog;

/**
 * 一時ファイルを管理します.
 * <br>
 * 一時ファイルの読み込み・書き出しにはテキストリーダを使用します。<br>
 * 一時ファイルは、ゲームの終了時にすべて削除される点に注意してください。<br>
 * <br>
 *
 * @version 1.0.0 - 2015/01/04<br>
 * @author Dra<br>
 * <br>
 */
public class TempFileStorage extends Storage<TempFile> {

	private static final TempFileStorage INSTANCE = new TempFileStorage();

	private TempFileStorage() {
	}

	public static TempFileStorage getInstance() {
		return INSTANCE;
	}

	public TempFile create() throws ContentsIOException {
		TempFile file = new TempFile();
		add(file);
		return file;
	}

	public TempFile create(String prefix, String suffix) throws ContentsIOException {
		TempFile file = new TempFile(prefix, suffix);
		add(file);
		return file;
	}

	private String prefix = "ktf_";
	private String suffix = ".tmp";

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getSuffix() {
		return suffix;
	}

	public void setSuffix(String suffix) {
		this.suffix = suffix;
	}

	public void deleteAll() {
		for (TempFile file : this) {
			if (file.exists()) {
				file.delete();
			}
		}
		GameLog.printInfoIfUsing("TempFileStorage : all temp file is deleted");
	}

}
