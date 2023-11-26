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

import java.io.File;
import java.io.IOException;
import kinugasa.game.GameLog;
import kinugasa.game.system.GameSystem;

/**
 * 一時ファイルです.
 * <br>
 *
 * @version 1.0.0 - 2015/01/04<br>
 * @author Shinacho<br>
 * <br>
 */
public final class TempFile implements Nameable {

	private File file;

	protected TempFile() throws ContentsIOException {
		this(
				TempFileStorage.getInstance().getPrefix(),
				TempFileStorage.getInstance().getSuffix());
	}

	protected TempFile(String prefix, String suffix) throws ContentsIOException {
		try {
			file = File.createTempFile(
					prefix,
					suffix);
		} catch (IOException e) {
			throw new ContentsIOException(e);
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print("temp file " + file.getPath() + " is created");
		}
	}

	public String getPath() {
		return file.getPath();
	}

	public File getFile() {
		return file;
	}

	public boolean exists() {
		return file.exists();
	}

	public void delete() {
		file.delete();
	}

	@Override
	public String getName() {
		return file.getName();
	}

}
