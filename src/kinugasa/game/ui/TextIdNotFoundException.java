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
package kinugasa.game.ui;

import kinugasa.resource.NotFoundException;


/**
 * テキストセットで定義されたNextIDが同じテキストセット内に存在しない場合の例外です.
 * <br>
 *
 * @version 1.0.0 - 2015/03/21<br>
 * @author Shinacho<br>
 * <br>
 */
public class TextIdNotFoundException extends NotFoundException {

    /**
     * Creates a new instance of <code>TextIdNotFoundException</code> without detail message.
     */
    public TextIdNotFoundException() {
    }


    /**
     * Constructs an instance of <code>TextIdNotFoundException</code> with the specified detail message.
     * @param msg the detail message.
     */
    public TextIdNotFoundException(String msg) {
        super(msg);
    }
}
