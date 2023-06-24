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

module kinugasa {
	requires static com.h2database;
	requires java.base;
	requires transitive java.desktop;
	requires transitive java.sql;
	requires transitive java.logging;
	exports kinugasa.game.event.fb;
	exports kinugasa.game.field4;
	exports kinugasa.game.input;
	exports kinugasa.game.system;
	exports kinugasa.game.ui;
	exports kinugasa.game;
	exports kinugasa.graphics;
	exports kinugasa.object;
	exports kinugasa.object.movemodel;
	exports kinugasa.resource.db;
	exports kinugasa.resource.sound;
	exports kinugasa.resource.text;
	exports kinugasa.resource;
	exports kinugasa.util;	
}
