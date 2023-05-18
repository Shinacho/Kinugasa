/*
 * The MIT License
 *
 * Copyright 2015 Shinacho.
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
import java.io.IOException;
import kinugasa.game.GameLog;

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
		GameLog.printInfoIfUsing("temp file " + file.getPath() + " is created");
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
