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
package kinugasa.game.system;

/**
 *
 * @vesion 1.0.0 - 2022/11/16_18:43:05<br>
 * @author Shinacho<br>
 */
public class GameSystemException extends RuntimeException {

    /**
     * Creates a new instance of <code>GameSystemException</code> without detail message.
     */
    public GameSystemException() {
    }


    /**
     * Constructs an instance of <code>GameSystemException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public GameSystemException(String msg) {
        super(msg);
    }
}
