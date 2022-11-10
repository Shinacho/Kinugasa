/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import kinugasa.game.GameLog;
import kinugasa.resource.Input;
import kinugasa.resource.InputStatus;
import kinugasa.resource.Output;
import kinugasa.resource.OutputResult;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_6:55:06<br>
 * @author Dra211<br>
 */
public class IniFile implements Input<IniFile>, Output {

	private final File file;
	private final Map<String, Value> map;
	private InputStatus status = InputStatus.NOT_LOADED;

	public IniFile(String fileName) {
		this(new File(fileName));
	}

	public IniFile(File file) {
		this.file = file;
		map = new HashMap<>();
	}

	@Override
	public IniFile load() throws FileNotFoundException, FileFormatException {
		this.addAll(this.file);
		return this;
	}

	private IniFile addAll(File file) {

		if (file == null || !file.exists()) {
			throw new FileNotFoundException(file);
		}
		try {
			for (String line : Files.readAllLines(file.toPath())) {
				line = line.trim();
				if (line.isEmpty()) {
					continue;
				}
				if (line.startsWith("#")) {
					continue;
				}
				if (line.startsWith("[")) {
					if (!line.endsWith("]")) {
						throw new FileFormatException(file, line);
					}
					continue;
				}
				if (!line.contains("=")) {
					throw new FileFormatException(file, line);
				}
				String[] val = line.split("=");
				map.put(val[0], new Value(val[1]));
			}
		} catch (IOException ex) {
			throw new FileIOException(ex);

		}
		status = InputStatus.LOADED;

		GameLog.printInfoIfUsing(file.getPath());
		map.entrySet().forEach(e -> {
			GameLog.printInfoIfUsing(e.getKey() + "=" + e.getValue());
		});

		return this;
	}

	@Override
	public void dispose() {
		map.clear();
		status = InputStatus.NOT_LOADED;
	}

	@Override
	public InputStatus getStatus() {
		return status;
	}

	@Override
	public OutputResult save() throws FileIOException {
		return saveTo(file);
	}

	@Override
	public OutputResult saveTo(File f) throws FileIOException {
		try {
			List<String> list = new ArrayList<>();
			map.entrySet().forEach(e -> {
				list.add(e.getKey() + e.getValue());
			});
			Files.write(f.toPath(), list, StandardOpenOption.CREATE_NEW, StandardOpenOption.TRUNCATE_EXISTING);
		} catch (IOException ex) {
			throw new FileIOException(ex);
		}
		return OutputResult.OK;
	}

	public boolean containsKey(String key) {
		return map.containsKey(key);
	}

	public Value getValue(String key) {
		return map.get(key);
	}

	public Optional<Value> get(String key) {
		return Optional.of(getValue(key));
	}

	public void remove(String key) {
		map.remove(key);
	}

	public void put(String key, Value v) {
		map.put(key, v);
	}

	public IniFile addAll(String filePath) {
		return this.addAll(new File(filePath));
	}

	public static final class Value {

		private final String value;

		public Value(String value) {
			this.value = value;
		}

		public String value() {
			return value;
		}

		public int asInt() throws NumberFormatException {
			return Integer.parseInt(value);
		}

		public long asLong() throws NumberFormatException {
			return Long.parseLong(value);
		}

		public float asFloat() throws NumberFormatException {
			return Float.parseFloat(value);
		}

		public List<String> asCsv() {
			if (value.contains(",")) {
				return Arrays.asList(value.split(","));
			}
			return Collections.EMPTY_LIST;
		}

		public int[] asCsvInt() {
			if (value.contains(",")) {
				List<String> csv = asCsv();
				return csv.stream().mapToInt(i -> Integer.parseInt(i)).toArray();
			}
			return new int[0];
		}

		public Value asCsvOf(int i) {
			return new Value(asCsv().get(i));
		}

		public boolean isTrue() {
			return "true".equals(value.toLowerCase());
		}

		public boolean is(String val) {
			return val.equals(value);
		}

		public boolean is(int val) {
			return asInt() == val;
		}

		public boolean is(long val) {
			return asLong() == val;
		}

		public boolean is(float val) {
			return asFloat() == val;
		}

		@Override
		public String toString() {
			return value;
		}
	}

}
