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

/**
 * コンテンツを破棄する機能を定義します.
 * <br>
 * このインターフェースは「再ロード」することがないクラスに実装されます。<Br>
 * それらのクラスは必要になったときだけインスタンス化され、不要になったときに
 * disposeメソッドを使用して再帰的に開放されます。<br>
 * 「一時的にメモリを開放し、再ロードするときに備える」Freeableとは
 * 別の使用方法である点に注意してください。<br>
 * <br>
 * @version 1.0.0 - 2013/05/05_17:54:26<br>
 * @author Shinacho<br>
 */
public interface Disposable {

	/**
	 * コンテンツをメモリから破棄します.
	 */
	public void dispose();
}
