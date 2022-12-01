/*
 * The MIT License
 *
 * Copyright 2022 Dra.
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
package kinugasa.game.system;

import java.awt.Color;
import java.awt.Graphics2D;
import kinugasa.game.GraphicsContext;
import kinugasa.object.BasicSprite;
import kinugasa.object.Sprite;
import kinugasa.util.TimeCounter;

/**
 * �o�g���A�N�V�����G���A�X�v���C�g�́A�^�[�Q�b�g�V�X�e���Ŏg�p�����A���S�_���狗�������ꂽ�T�[�N�����`����N���X�ł��B
 * ���̃X�v���C�g�́A�X�v���C�g�̋@�\���������߂�����Sprite�̃T�u�N���X�ɂȂ��Ă��܂��B���W�y�уT�C�Y�͖�������܂��B
 *
 * @vesion 1.0.0 - 2022/11/28_21:36:31<br>
 * @author Dra211<br>
 */
public class BattleActionAreaSprite extends BasicSprite {

	private int area = 0;
	private Color color;

	public BattleActionAreaSprite(Color color) {
		this.color = color;
	}

	public void setArea(int area) {
		this.area = area;
	}

	public void setColor(Color color) {
		this.color = color;
	}

	public int getArea() {
		return area;
	}

	public Color getColor() {
		return color;
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!isVisible() || !isExist()) {
			return;
		}
		if (area == 0) {
			return;
		}
		Graphics2D g2 = g.create();
		g2.setColor(color);
		int x = (int) (getCenterX() - area);
		int y = (int) (getCenterY() - area);
		g2.drawOval(x, y, area * 2, area * 2);
		g2.dispose();
	}

}
