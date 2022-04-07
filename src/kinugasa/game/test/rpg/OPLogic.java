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
package kinugasa.game.test.rpg;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.input.InputState;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.graphics.ARGBColor;
import kinugasa.graphics.Animation;
import kinugasa.graphics.FadeCounter;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.object.ImagePainterStorage;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_16:32:14<br>
 * @author Dra211<br>
 */
public class OPLogic extends GameLogic {

	public OPLogic(String name, GameManager gm) {
		super(name, gm);
	}
	private OPLabel line1;
	private OPLabel line2;
	private OPLabel line3;
	private OPLabel line4;
	private OPLabel line5;
	private OPLabel line6;
	private OPLabel line7;
	private OPLabel line8;
	private OPLabel line9;
	private AnimationSprite yajirushi;

	@Override
	public void load() {
		TextLabelModel model = new SimpleTextLabelModel(FontModel.DEFAULT);
		line1 = new OPLabel("遥か彼方の世界において…", model, 40, 40, 0);
		line2 = new OPLabel("長く続いていた平和が今終わりを告げた。", model, 40, 40 * 2, 60);
		line3 = new OPLabel("フジサワ帝国の皇帝は、魔界から魔物を呼び出し、", model, 40, 40 * 3, 60 * 2);
		line4 = new OPLabel("ショウナン征服に乗り出したのである。", model, 40, 40 * 4, 60 * 3);
		line5 = new OPLabel("これに対し反乱軍は、カマクラ王国で立ち上がったが、", model, 40, 40 * 5, 60 * 4);
		line6 = new OPLabel("帝国の総攻撃にあい、城を奪われ、辺境の町ズシへと", model, 40, 40 * 6, 60 * 5);
		line7 = new OPLabel("撤退しなければならかった。", model, 40, 40 * 7, 60 * 6);
		line8 = new OPLabel("ここカマクラ王国に住むあなたも、敵の攻撃によって両親を失い、", model, 40, 40 * 8, 60 *7);
		line9 = new OPLabel("執拗な敵の追手から逃げ続けていた…", model, 40, 40 * 9, 60 * 8);
		yajirushi = new AnimationSprite(680, 440, 24, 24, new Animation(new FrameTimeCounter(40), new SpriteSheet("resource/selectIcon1.png").rows(0, 24, 24).images()), ImagePainterStorage.IMAGE_BOUNDS_XY);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update(GameTimeManager gtm) {
		line1.update();
		line2.update();
		line3.update();
		line4.update();
		line5.update();
		line6.update();
		line7.update();
		line8.update();
		line9.update();
		if (line9.getStage() == 5) {
			yajirushi.update();
		}
		InputState is = InputState.getInstance();
		
		if (line9.getStage() == 5 && is.getGamePadState().isAnyButtonInput()) {
			gls.setCurrent("FIELD");
		}
		if(is.getGamePadState().buttons.B){
			line1.setStage(5);
			line2.setStage(5);
			line3.setStage(5);
			line4.setStage(5);
			line5.setStage(5);
			line6.setStage(5);
			line7.setStage(5);
			line8.setStage(5);
			line9.setStage(5);
		}
		
	}

	@Override
	public void draw(GraphicsContext g) {
		line1.draw(g);
		line2.draw(g);
		line3.draw(g);
		line4.draw(g);
		line5.draw(g);
		line6.draw(g);
		line7.draw(g);
		line8.draw(g);
		line9.draw(g);
		if (line9.getStage() == 5) {
			yajirushi.draw(g);
		}
	}

}

class OPLabel extends TextLabelSprite {

	public OPLabel(CharSequence text, TextLabelModel labelModel, float x, float y, int c) {
		super(text, labelModel, x, y);
		inTime = new FrameTimeCounter(c);
	}

	private TimeCounter inTime;
	private FadeCounter in = FadeCounter.fadeIn(+1);
	private FadeCounter out = FadeCounter.fadeOut(-2);
	private TimeCounter outTime = new FrameTimeCounter(240);
	private Color color = ARGBColor.toAWTColor(0x00FFFFFF);
	private int stage = 0;

	@Override
	public void update() {
		switch (stage) {
			case 0:
				if (inTime.isReaching()) {
					stage = 2;
				}
				break;
			case 2:
				in.update();
				color = ARGBColor.toAWTColor(ARGBColor.toARGB(in.getValue(), 255, 255, 255));
				if (in.isEnded()) {
					stage = 3;
				}
				break;
			case 3:
				if (outTime.isReaching()) {
					stage = 4;
				}
				break;
			case 4:
				out.update();
				color = ARGBColor.toAWTColor(ARGBColor.toARGB(out.getValue(), 255, 255, 255));
				if (out.isEnded()) {
					stage = 5;
				}
				break;
			default:
				break;
		}
	}

	public int getStage() {
		return stage;
	}

	public void setStage(int stage) {
		this.stage = stage;
	}
	
	

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(color);
		g2.setFont(FontModel.DEFAULT.getFont());
		g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
		g2.drawString(getText(), (int) getX(), (int) (getY() + 16));
		g2.dispose();
	}

}
