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
 * @author Shinacho<br>
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
		GameLog.print("TempFileStorage : all temp file is deleted");
	}

}
