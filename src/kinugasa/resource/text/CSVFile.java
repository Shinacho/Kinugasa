/*
 * The MIT License
 *
 * Copyright 2021 Shinacho.
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
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
 * @vesion 1.0.0 - 2021/11/23_6:23:42<br>
 * @author Shinacho<br>
 */
public class CSVFile implements Input<CSVFile>, Output, Nameable, Iterable<String[]> {

	private File file;
	private Charset charset;

	public CSVFile(File file) {
		this.file = file;
		charset = Charset.forName("UTF-8");
	}

	public CSVFile(String path) {
		this.file = new File(path);
		charset = Charset.forName("UTF-8");

	}

	public CSVFile(File file, Charset c) {
		this.file = file;
		this.charset = c;
	}

	public CSVFile(String path, Charset c) {
		this.file = new File(path);
		this.charset = c;
	}

	public boolean exists() {
		return file.exists();
	}

	@Override
	public String getName() {
		return file.getName();
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	public List<String[]> getData() {
		return data;
	}

	@Override
	public Iterator<String[]> iterator() {
		return data.iterator();
	}

	public void add(String[] data) {
		this.data.add(data);
	}

	//--------------------------------------------------------------------------
	private List<String[]> data = new ArrayList<>();

	@Override
	public CSVFile load() throws FileIOException {
		data = new ArrayList<>();
		try {
			for (String line : Files.readAllLines(file.toPath(), charset)) {
				data.add(line.split(","));
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
		List<String> lines = new ArrayList<>();
		String line = "";
		for (String[] l : data) {
			for (String v : l) {
				line += v + ",";
			}
			lines.add(line.substring(0, line.length() - 1));
			line = "";
		}
		try {
			Files.write(f.toPath(), lines, charset, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ex) {
			throw new FileIOException(ex);
		}
		return OutputResult.OK;
	}

}
