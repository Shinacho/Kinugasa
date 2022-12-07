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
package kinugasa.game.test;

import java.util.ArrayList;
import java.util.List;
import kinugasa.game.GameLogic;
import kinugasa.game.GameManager;
import kinugasa.game.GameTimeManager;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.field4.D2Idx;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FieldMapCameraMode;
import kinugasa.game.field4.PlayerCharacterSprite;
import kinugasa.game.field4.FieldMapStorage;
import kinugasa.game.field4.FieldMapTile;
import kinugasa.game.field4.FourDirAnimation;
import kinugasa.game.field4.MapChipAttributeStorage;
import kinugasa.game.field4.MapChipSetStorage;
import kinugasa.game.field4.VehicleStorage;
import kinugasa.game.input.GamePadButton;
import kinugasa.game.input.GamePadStick;
import kinugasa.game.input.InputState;
import kinugasa.game.input.InputType;
import kinugasa.game.input.Keys;
import kinugasa.game.system.Enemy;
import kinugasa.game.system.GameSystem;
import kinugasa.game.system.GameSystemXMLLoader;
import kinugasa.game.system.RaceStorage;
import kinugasa.game.system.SpeedCalcModelStorage;
import kinugasa.game.system.Status;
import kinugasa.game.ui.FPSLabel;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.game.ui.TextStorage;
import kinugasa.game.ui.TextStorageStorage;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ColorChanger;
import kinugasa.graphics.ColorTransitionModel;
import kinugasa.graphics.FadeCounter;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.FadeEffect;
import kinugasa.object.FourDirection;
import kinugasa.object.KVector;
import kinugasa.resource.KImage;
import kinugasa.resource.sound.Sound;
import kinugasa.resource.sound.SoundBuilder;
import kinugasa.resource.sound.SoundLoader;
import kinugasa.resource.sound.SoundStorage;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/22_6:33:19<br>
 * @author Dra211<br>
 */
public class FieldLogic extends GameLogic {

	FieldLogic(GameManager gm) {
		super("FIELD", gm);
	}

	private TextLabelSprite operation;

	@Override
	public void load() {
		fm = FieldMapStorage.getInstance().get("ズシ");
		c = FieldMap.getPlayerCharacter().get(0);
		fm.getCamera().updateToCenter();
		//
		//----------------------------------------------------------------------
		//
		screenShot = new SoundBuilder("resource/se/screenShot.wav").builde().load();
		String operaionText = "(LS) " + I18N.translate("MOVE");
		operation = new TextLabelSprite(operaionText, new SimpleTextLabelModel(FontModel.DEFAULT.clone()), 420, 450);
		//
		ts = fm.getTextStorage();
		battle = false;
//		if (!fm.getBgm().isPlaying()) {
//			SoundStorage.getInstance().get("BGM").stopAll();
//			fm.getBgm().load().play();
//		}
	}
	FieldMap fm;
	PlayerCharacterSprite c;
	TextStorage ts;
	MessageWindow mw;
	private Sound screenShot;
	int stage = 0;
	FadeEffect effect;
	boolean battle = false;

	@Override
	public void dispose() {
	}

	@Override
	public void update(GameTimeManager gtm) {
		InputState is = InputState.getInstance();
		switch (stage) {
			case 0:
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					FieldMapTile t = fm.getCurrentTile();
					if (t.hasInNode()) {
						effect = new FadeEffect(gm.getWindow().getInternalBounds().width, gm.getWindow().getInternalBounds().height,
								new ColorChanger(
										ColorTransitionModel.valueOf(0),
										ColorTransitionModel.valueOf(0),
										ColorTransitionModel.valueOf(0),
										new FadeCounter(0, +6)
								));
						if (mw != null) {
							mw.setVisible(false);
						}
						nextStage();
					}
				}
				//MW処理
				//会話開始
				// 会話送り、選択の処理
				if (fm.canTalk()) {
					String operaionText = "(A)" + I18N.translate("TALK") + " / " + "(LS)" + I18N.translate("MOVE");
					operation.setText(operaionText);
				} else {
					String operaionText = "(LS)" + I18N.translate("MOVE");
					operation.setText(operaionText);
				}
				if (is.isPressed(GamePadButton.A, InputType.SINGLE)) {
					if (mw != null && mw.isVisible()) {
						if (!mw.isAllVisible()) {
							mw.allText();
						} else if (mw.isChoice()) {
							if (mw.getChoiceOption().hasNext()) {
								mw.choicesNext();
							} else {
								fm.closeMessagWindow();
							}
						} else if (mw.hasNext()) {
							mw.next();
						} else {
							fm.closeMessagWindow();
						}
					} else if (fm.canTalk()) {
						mw = fm.talk();
					}
				}
				if (mw != null && mw.isChoice()) {
					if (is.isPressed(GamePadButton.POV_DOWN, InputType.SINGLE)) {
						mw.nextSelect();
					}
					if (is.isPressed(GamePadButton.POV_UP, InputType.SINGLE)) {
						mw.prevSelect();
					}
				}
				//テスト用カメラ処理
				if (is.isPressed(GamePadButton.LB, InputType.SINGLE)) {
					fm.getCamera().setMode(FieldMapCameraMode.FOLLOW_TO_CENTER);
				}
				if (is.isPressed(GamePadButton.RB, InputType.SINGLE)) {
					fm.getCamera().setMode(FieldMapCameraMode.FREE);
				}
				if (is.isPressed(GamePadButton.BACK, InputType.SINGLE)) {
					if (fm.getBackgroundLayerSprite() != null) {
						fm.getBackgroundLayerSprite().getAnimation().setRepeat(!fm.getBackgroundLayerSprite().getAnimation().isRepeat());
					}
				}

				//フィールドマップのカメラ移動・・・メッセージウインドウが表示されている間は移動不可とする
				if (mw == null || !mw.isVisible()) {
					float speed = VehicleStorage.getInstance().getCurrentVehicle().getSpeed();
					fm.setVector(new KVector(is.getGamePadState().sticks.LEFT.getLocation(speed)));
					fm.move();
				}
				//NPC/MW更新
				fm.update();
				//プレイヤーキャラクターの向き更新
				if (mw == null || !mw.isVisible()) {
					if (fm.getCamera().getMode() == FieldMapCameraMode.FOLLOW_TO_CENTER) {
						if (!is.getGamePadState().sticks.LEFT.getLocation().equals(GamePadStick.NOTHING)) {
							if (is.getGamePadState().sticks.LEFT.is(FourDirection.EAST)) {
								c.to(FourDirection.EAST);
							} else if (is.getGamePadState().sticks.LEFT.is(FourDirection.WEST)) {
								c.to(FourDirection.WEST);
							}
							if (is.getGamePadState().sticks.LEFT.is(FourDirection.NORTH)) {
								c.to(FourDirection.NORTH);
							} else if (is.getGamePadState().sticks.LEFT.is(FourDirection.SOUTH)) {
								c.to(FourDirection.SOUTH);
							}
						}
					}
				}
				//スクリーンショット系の処理
				if (is.isPressed(Keys.M, InputType.SINGLE)) {
					KImage image = fm.createMiniMap(0.25f, true);
					ImageUtil.save("resource/test/miniMap.png", image.get());
					screenShot.stopAndPlay();
				}
				if (is.isPressed(Keys.F12, InputType.SINGLE)) {
					ImageUtil.screenShot("resource/test/screenShot.png", gm.getWindow().getBounds());
					screenShot.stopAndPlay();
				}
				//エンカウント処理
				if (fm.isEncount()) {
					SoundStorage.getInstance().get("SE").get("効果音＿戦闘開始.wav").load().stopAndPlay();
					battle = true;
					effect = new FadeEffect(gm.getWindow().getInternalBounds().width, gm.getWindow().getInternalBounds().height,
							new ColorChanger(
									ColorTransitionModel.valueOf(0),
									ColorTransitionModel.valueOf(0),
									ColorTransitionModel.valueOf(0),
									new FadeCounter(0, +6)
							));
					nextStage();
				}
				break;
			case 1:
				effect.update();
				if (effect.isEnded()) {
					effect = new FadeEffect(gm.getWindow().getInternalBounds().width, gm.getWindow().getInternalBounds().height,
							new ColorChanger(
									ColorTransitionModel.valueOf(0),
									ColorTransitionModel.valueOf(0),
									ColorTransitionModel.valueOf(0),
									ColorTransitionModel.valueOf(255)
							));
					nextStage();
				}
				break;
			case 2:
				if (!battle) {
					fm = fm.changeMap(fm.getCurrentTile().getNode());
				} else {
					GameSystem.getInstance().battleStart(fm.createEncountInfo());
					gls.changeTo("BATTLE");
				}
				nextStage();
				break;
			case 3:
				effect = new FadeEffect(gm.getWindow().getInternalBounds().width, gm.getWindow().getInternalBounds().height,
						new ColorChanger(
								ColorTransitionModel.valueOf(0),
								ColorTransitionModel.valueOf(0),
								ColorTransitionModel.valueOf(0),
								new FadeCounter(255, -6)
						));
				nextStage();
				break;
			case 4:
				effect.update();
				if (effect.isEnded()) {
					nextStage();
				}
				break;

		}

	}

	private void nextStage() {
		System.out.println("MAIN STAGE : " + stage + " -> " + (stage + 1));
		stage++;
		if (stage == 5) {
			stage = 0;
		}
	}

	@Override
	public void draw(GraphicsContext gc) {
		fm.draw(gc);
		if (mw != null) {
			mw.draw(gc);
		}
		operation.draw(gc);
		if (effect != null) {
			effect.draw(gc);
		}
	}

}
