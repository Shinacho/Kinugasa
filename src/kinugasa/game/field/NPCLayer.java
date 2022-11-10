/*
 * The MIT License
 *
 * Copyright 2021 Dra.
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
package kinugasa.game.field;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;

/**
 *
 * @vesion 1.0.0 - 2021/12/04_16:01:04<br>
 * @author Dra211<br>
 */
public class NPCLayer extends BasicSprite {
	
	private FieldMap fm;
	public NPCLayer(FieldMap fm, int npcY, int npcX) {
		super(fm.getX(), fm.getY(), fm.getWidth(), fm.getHeight());
		npcs = new NPC[npcY][];
		for (int i = 0; i < npcs.length; i++) {
			npcs[i] = new NPC[npcX];
		}
		this.fm = fm;
	}

	public void add(NPC n) {
		int x = n.getInitialP().x;
		int y = n.getInitialP().y;
		npcs[y][x] = n;
	}

	public NPC get(int x, int y) {
		return npcs[y][x];
	}

	private NPC[][] npcs;

	@Override
	public void draw(GraphicsContext g) {
		for( int y = 0; y < npcs.length; y ++ ){
			for( int x = 0; x < npcs[y].length; x ++ ){
				if(npcs[y][x] != null){
					npcs[y][x].draw(g);
				}
			}
		}
	}

}
