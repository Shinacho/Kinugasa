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
package kinugasa.game.test.stgtest;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.event2.Event;
import kinugasa.game.event2.EventManager;
import kinugasa.game.input.InputState;
import kinugasa.game.ui.ActionImageSprite;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.BasicSprite;
import kinugasa.object.ImageSprite;
import kinugasa.object.Sprite;
import kinugasa.object.TVector;
import kinugasa.resource.sound.LoopPoint;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import kinugasa.util.TimeCounter;

/**
 *
 * @vesion 1.0.0 - 2021/11/24_5:11:19<br>
 * @author Dra211<br>
 */
public class Stage1Logic extends GameLogic {

	public Stage1Logic(String name, GameManager gm) {
		super(name, gm);
	}

	private Sound bgm;
	private TextLabelSprite stage1;
	private TimeCounter stage1counter;
	private boolean trMode = false;

	private List<Star> stares;

	private TextLabelSprite fps;

	private ActionImageSprite ship;

	private List<Sprite> deleteList = new ArrayList<>();

	private List<BasicSprite> tama = new ArrayList<>();
	private TimeCounter tamaTc = new FrameTimeCounter(4);

	private EventManager em;
	private List<BasicSprite> tekiList = new ArrayList<>();

	private  Rectangle DELETE_AREA;


	@Override
	public void load() {
		DELETE_AREA = new Rectangle(-9, -9, gm.getWindow().getWidth() + 18, gm.getWindow().getHeight() + 18);
		bgm = new SoundBuilder("resource/シューティング・ステージ１.wav").setLoopPoint(LoopPoint.END_TO_START).builde().load();
		bgm.play();

		stage1 = new TextLabelSprite("STAGE 1", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setFontSize(32)), 24, 24);
		stage1counter = new FrameTimeCounter(45);

		stares = new ArrayList<>();
		for (int i = 0; i < 32; i++) {
			stares.add(new Star(GraphicsUtil.randomColor(), Random.randomAbsInt(720), Random.randomAbsInt(480), Random.randomAbsInt(6), 2 + Random.randomFloat(8)));
		}

		fps = new TextLabelSprite("FPS:", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setFontSize(16)), 620, 12);

		ship = new MyShip(320, 300, 48, 48);

		em = new EventManager();
		em.readFromXML("resource/eventList.xml");
	}

	@Override
	public void dispose() {
		bgm.stop();
		bgm.dispose();
	}
	
	private boolean first = true;

	@Override
	public void update(GameTimeManager gtm) {
		// フレーム数リセット
		if(first){
			gtm.resetTotalFrame();
			first = false;
		}
		// ステージラベルの処理
		if (trMode) {
			stage1.getLabelModel().getFontConfig().addAlpha(-2);
			if (stage1.getLabelModel().getFontConfig().getColor().getAlpha() == 0) {
				stage1.setVisible(false);
			}
		}
		if (!trMode && stage1counter.isReaching()) {
			trMode = true;
		}

		//星の処理
		stares.forEach(Star::update);

		//FPSラベルの処理
		fps.setText("FPS:" + gtm.getFPSStr(2));

		//スティック入力と移動判定
		ship.setVector(new TVector(InputState.getInstance().getGamePadState().sticks.LEFT.getLocation(4.5f)));
		ship.move();
		if (ship.getX() <= 0) {
			ship.setX(1);
		}
		if (ship.getY() <= 0) {
			ship.setY(1);
		}
		if (ship.getX() + ship.getWidth() >= gm.getWindow().getInternalBounds().width) {
			ship.setX(gm.getWindow().getInternalBounds().width - ship.getWidth() - 1);
		}
		if (ship.getY() + ship.getHeight() >= gm.getWindow().getInternalBounds().height) {
			ship.setY(gm.getWindow().getInternalBounds().height - ship.getHeight() - 1);
		}

		//玉発射
		if (InputState.getInstance().getGamePadState().buttons.A) {
			if (tamaTc.isReaching()) {
				Tama t1 = new Tama(ship.getX() + 26, ship.getY() + 22, 8, 8, "resource/tama.png");
				Tama t2 = new Tama(ship.getX() + 12, ship.getY() + 22, 8, 8, "resource/tama.png");
				t1.setVector(new TVector(TVector.NORTH, 12));
				t2.setVector(new TVector(TVector.NORTH, 12));
				tama.add(t1);
				tama.add(t2);
				tamaTc.reset();
			}
		}

		//玉削除
		for (Sprite s : tama) {
			if (!s.isExist()) {
				deleteList.add(s);
			}
		}
		tama.removeAll(deleteList);
		deleteList.clear();

		//玉移動
		tama.forEach(BasicSprite::move);

		//イベント処理
		if (em.hasEvent(gtm.getTotalFrame())) {
			List<Event<?>> eList = em.getEvents(gtm.getTotalFrame());

			for (Event<?> e : eList) {
				Object o = e.exec();
				if (o instanceof BasicSprite) {
					tekiList.add((BasicSprite) o);
				}
			}
		}

		//玉と敵のあたり判定
		for( BasicSprite s: tama){
			for( BasicSprite t : tekiList){
				if(t.contains(s.getCenter())){
					t.setExist(false);
					deleteList.add(t);
				}
			}
		}
		
		//敵処理
		for (BasicSprite t : tekiList) {
			if( !t.isExist() ) continue;
			t.move();
			if (!DELETE_AREA.contains(t.getCenter())) {
				deleteList.add(t);
			}
		}
		tekiList.removeAll(deleteList);
		deleteList.clear();

	}

	@Override
	public void draw(GraphicsContext g) {
		stage1.draw(g);
		stares.forEach((s) -> s.draw(g));
		fps.draw(g);
		tekiList.forEach((s) -> s.draw(g));
		tama.forEach((s) -> s.draw(g));
		ship.draw(g);
	}

}

class Star extends BasicSprite {

	private Color c;

	public Star(Color c, int x, int y, int size, float speed) {
		super(x, y, size, size, new TVector(TVector.SOUTH, speed));
		this.c = c;
	}

	@Override
	public void draw(GraphicsContext g) {
		Graphics2D g2 = g.create();
		g2.setColor(c);
		g2.fillOval((int) getX(), (int) getY(), (int) getWidth(), (int) getHeight());
		g2.dispose();
	}

	@Override
	public void update() {
		move();
		if (getY() > 490) {
			setY(-10);
		}
	}

}

class MyShip extends ActionImageSprite {

	public MyShip(int x, int y, int w, int h) {
		super(x, y, w, h, ImageUtil.load("resource/ship.png"));

	}
}

class Tama extends ImageSprite {

	public Tama(float x, float y, int w, int h, String imageName) {
		super(x, y, w, h, ImageUtil.load(imageName));

	}

	@Override
	public void move() {
		super.move(); //To change body of generated methods, choose Tools | Templates.
		if (getY() < -10) {
			setExist(false);
		}
	}

}
