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
import kinugasa.resource.text.FileIOException;

/**
 *
 * @vesion 1.0.0 - 2021/08/17_9:45:32<br>
 * @author Shinacho<br>
 */
public interface Output {

	public OutputResult save() throws FileIOException;

	public OutputResult saveTo(File f) throws FileIOException;
}
