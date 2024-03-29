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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import kinugasa.resource.Input;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Nameable;
import kinugasa.resource.Output;
import kinugasa.resource.OutputResult;

/**
 *
 * @vesion 1.0.0 - 2021/11/23_6:35:59<br>
 * @author Shinacho<br>
 */
public class TextFile implements Input<TextFile>, Output, Nameable, Iterable<String> {

	private File file;
	private Charset charset;

	public TextFile(File file) {
		this.file = file;
		charset = StandardCharsets.UTF_8;
	}

	public TextFile(String path) {
		this.file = new File(path);
		charset = StandardCharsets.UTF_8;
	}

	public TextFile(File file, Charset c) {
		this.file = file;
		this.charset = c;
	}

	public TextFile(String path, Charset c) {
		this.file = new File(path);
		this.charset = c;
	}

	public boolean exists() {
		return file.exists();
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

	public List<String> getData() {
		return data;
	}

	@Override
	public Iterator<String> iterator() {
		return data.iterator();
	}

	//--------------------------------------------------------------------------
	private List<String> data = new ArrayList<>();

	@Override
	public TextFile load() throws FileIOException {
		data = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(file.toPath(), charset)) {
				data.add(line);
			}
		} catch (IOException ex) {
			throw new FileIOException(ex);
		}
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
		return saveTo(file);
	}

	@Override
	public OutputResult saveTo(File f) throws FileIOException {
		try {
			Files.write(f.toPath(), data, charset, StandardOpenOption.WRITE, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ex) {
			throw new FileIOException(ex);
		}
		return OutputResult.OK;
	}

}
