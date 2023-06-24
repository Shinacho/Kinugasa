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

import kinugasa.game.input.InputState;
import kinugasa.resource.Disposable;
import kinugasa.resource.Nameable;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_4:35:02<br>
 * @author Shinacho<br>
 */
public abstract class GameLogic implements Nameable, Disposable{

	protected final  String name ;
	protected final GameManager gm;
	protected final GameLogicStorage gls = GameLogicStorage.getInstance();

	protected GameLogic(String name, GameManager gm) {
		this.name = name;
		this.gm = gm;
	}
	
	@Override
	public final String getName(){
		return name;
	}
	
	public abstract void load();
	
	@Override
	public abstract void dispose();
	
	public abstract void update(GameTimeManager gtm, InputState is);
	
	public abstract void draw(GraphicsContext g);
	
}
