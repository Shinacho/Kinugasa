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
package kinugasa.game.test.rpg;

import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.ui.Action;
import kinugasa.game.ui.ActionTextSprite;
import kinugasa.game.ui.ActionTextSpriteGroup;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.graphics.ImageUtil;
import kinugasa.object.FourDirection;
import kinugasa.object.ImageSprite;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;
import kinugasa.game.I18N;

/**
 *
 * @vesion 1.0.0 - 2022/11/12_21:36:53<br>
 * @author Dra211<br>
 */
public class TitleLogic extends GameLogic {

	public TitleLogic(GameManager gm) {
		super(Const.LogicName.TITLE_LOGIC, gm);
	}
	private ImageSprite titleImage;
	private ActionTextSpriteGroup atsg;
	private FrameTimeCounter gamepadSticksDelay = new FrameTimeCounter(10);
	private TextLabelSprite operation;
	private int selected;

	@Override
	public void load() {
		titleImage = new ImageSprite(0, 0, Const.Screen.WIDTH, Const.Screen.HEIGHT, ImageUtil.load("resource/title.png"));

		atsg = new ActionTextSpriteGroup(540, 300,
				new ActionTextSprite("New Game", new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 0, 0, 18, 0, new Action() {
					@Override
					public void exec() {
						gls.changeTo(Const.LogicName.OP);
					}
				}),
				new ActionTextSprite("Load Game", new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 0, 0, 18, 0, new Action() {
					@Override
					public void exec() {
						gls.changeTo(Const.LogicName.LOAD_GAME);
					}
				}),
				new ActionTextSprite("Music Room", new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 0, 0, 18, 0, new Action() {
					@Override
					public void exec() {
						SoundStorage.getInstance().get("BGM").stopAll();
						gls.changeTo(Const.LogicName.MUSIC_ROOM);
					}
				}),
				new ActionTextSprite("Game Pad Test", new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 0, 0, 18, 0, new Action() {
					@Override
					public void exec() {
						gls.changeTo(Const.LogicName.GAMEPAD_TEST);
					}
				}),
				new ActionTextSprite("Game Exit", new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 0, 0, 18, 0, new Action() {
					@Override
					public void exec() {
						SoundStorage.getInstance().get("BGM").stopAll();
						gm.gameExit();
					}
				})
		);
		String operaionText = "(A)" + I18N.translate("SUBMIT") + " / " + "↑↓" + I18N.translate("MOVE");
		operation = new TextLabelSprite(operaionText, new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 420, 450);

		SoundStorage.getInstance().get("BGM").get("フィールド５.wav").load().play();
		atsg.setSelectedIdx(selected);
	}

	@Override
	public void dispose() {
	}

	@Override
	public void update(GameTimeManager gtm) {
		InputState is = InputState.getInstance();
		if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE) || (is.getGamePadState().sticks.LEFT.is(FourDirection.SOUTH)) && gamepadSticksDelay.isReaching()) {
			atsg.next();
			selected = atsg.getSelectedIdx();
			gamepadSticksDelay.reset();
		}
		if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE) || (is.getGamePadState().sticks.LEFT.is(FourDirection.NORTH)) && gamepadSticksDelay.isReaching()) {
			atsg.prev();
			selected = atsg.getSelectedIdx();
			gamepadSticksDelay.reset();
		}
		if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
			selected = atsg.getSelectedIdx();
			atsg.exec();
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		titleImage.draw(g);
		atsg.draw(g);
		operation.draw(g);
	}

}
