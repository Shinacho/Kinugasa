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
import kinugasa.resource.ContentsIOException;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_9:36:38<br>
 * @author Shinacho<br>
 */
public class FileIOException extends ContentsIOException {

	public FileIOException(String msg) {
		super(msg);
	}

	public FileIOException(File f) {
		super(f.getPath());
	}
	
	public FileIOException(Exception ex){
		super(ex);
	}

}
