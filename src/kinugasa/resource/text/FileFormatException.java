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

/**
 *
 * @vesion 1.0.0 - 2021/08/17_9:33:12<br>
 * @author Shinacho<br>
 */
public class FileFormatException extends FileIOException {

	public FileFormatException(File f, String msg) {
		super(f.getPath() + "/" + f.getName() + " " + msg);
	}

	public FileFormatException(String msg) {
		super(msg);
	}

	public FileFormatException(Exception ex) {
		super(ex);
	}
	
	

}
