/*
 * The MIT License
 *
 * Copyright 2013 Dra0211.
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
package kinugasa.game.rpgui;

import kinugasa.resource.Disposable;
import kinugasa.resource.NameNotFoundException;
import kinugasa.resource.Nameable;
import kinugasa.resource.NotYetLoadedException;
import kinugasa.resource.sound.SoundMap;
import kinugasa.resource.text.IllegalXMLFormatException;


/**
 * フィールドマップとその関連データをロードし、構築する機能を定義します.
 * <br>
 *
 * <br>
 *
 * @version 1.0.0 - 2013/05/05_0:09:15<br>
 * @author Dra0211<br>
 */
public interface FieldMapBuilder extends Disposable, Nameable, Comparable<FieldMapBuilder> {

	@Override
	public String getName();

	//この結果はnewを戻してはならない
	public FieldMap getFieldMap() throws NotYetLoadedException;

	public int getChipHeight();

	public int getChipWidth();

	public NodeMap getNodeMap();

	public SoundMap getSoundMap();

	public XMLFieldMapBuilder load() throws IllegalXMLFormatException, NumberFormatException, NameNotFoundException;

	public XMLFieldMapBuilder free();

	@Override
	public default void dispose() {
		free();
	}
	
	public XMLFieldMapBuilder freeSound();

	public boolean isLoaded();

	public TextStorage getTextStorage();

	public MessageWindowSprite getMessageWindowSprite();
}
