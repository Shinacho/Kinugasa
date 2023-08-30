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
package kinugasa.game;

import java.awt.image.BufferedImage;
import kinugasa.object.Statable;

/**
 *
 * エフェクトはダブルバッファリングイメージに対する最終的な処理を定義します。 <br>
 * 例えば、反転や色の変換、シェイク、暗転等いろいろな処理が適用できます。
 *
 * @vesion 1.0.0 - 2023/07/19_20:00:02<br>
 * @author Shinacho
 *
 * <br>
 */
@FunctionalInterface
public interface ScreenEffect extends Statable {

	public BufferedImage doIt(BufferedImage src);

	@Override
	public default boolean isRunning() {
		return true;
	}

	@Override
	public default boolean isEnded() {
		return false;
	}

}
