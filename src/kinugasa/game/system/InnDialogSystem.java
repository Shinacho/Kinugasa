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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import kinugasa.game.field4.FieldEventParser;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextStorage;
import kinugasa.object.Drawable;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2023/12/03_15:57:55<br>
 * @author Shinacho<br>
 */
public class InnDialogSystem implements Drawable {

	private static final InnDialogSystem INSTANCE = new InnDialogSystem();

	private InnDialogSystem() {
	}

	public static InnDialogSystem getInstance() {
		return INSTANCE;
	}

	private Inn inn;
	private InnPlan selectedPlan;
	private int nissu;
	private String paymentID;
	private String scriptName;
	//
	private TextStorage ts;
	private Stage stage = Stage.開始前;
	private MessageWindow mainWindow;
	private MessageWindow paymentWindow;

	public InnDialogSystem setInn(Inn inn) {
		this.inn = inn;
		init();
		return this;
	}

	public void unset() {
		stage = Stage.開始前;
		this.inn = null;
	}

	public enum Stage {
		開始前,
		記録or宿泊,
		記録モード,
		プラン検討中,
		日数検討中,
		支払い方法選択中,
		最終確認中,
		宿泊中,
		終了,
		終了済み,;

		public boolean is終了済み() {
			return this == Stage.終了済み;
		}
	}
	private static final float buffer = 12;
	private static final float x = buffer;
	private static final float y = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 2 + buffer * 2;
	private static final float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - (buffer * 2);
	private static final float h = GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3;

	public static class EventFiles {

		public static String GE_SAVE;
		public static String GE_SLEEP;
	}

	private void init() {
		stage = Stage.開始前;

		mainWindow = new MessageWindow(x, y, w, h);
		paymentWindow = new MessageWindow(x + y - 128, y, 128, 24);
		paymentWindow.setVisible(false);
		//TSの配置
		ts = new TextStorage(inn.getId());
		ts.add(createText("001", I18N.get(GameSystemI18NKeys.宿Xです, inn.getVisibleName()), false, "002"));
		ts.add(createChoice("002", I18N.get(GameSystemI18NKeys.記録ですか宿泊ですか),
				createText("002_1", I18N.get(GameSystemI18NKeys.記録したい), true, "004"),
				createText("002_2", I18N.get(GameSystemI18NKeys.宿泊したい), true, "006"),
				createText("002_3", I18N.get(GameSystemI18NKeys.用はない), true, "005")
		));
	}

	public void nextSelect() {
		mainWindow.nextSelect();
	}

	public void prevSelect() {
		mainWindow.prevSelect();
	}

	public void commit() {
		switch (stage) {
			case 開始前: {
				mainWindow.next();
				stage = Stage.記録or宿泊;
				break;
			}
			case 記録or宿泊: {
				ts.clear();
				int selected = mainWindow.getSelect();
				switch (selected) {
					case 1: {
						//記録
						mainWindow.setText(createTextWEvent("001", I18N.get(GameSystemI18NKeys.記録しますね), false, "005", EventFiles.GE_SAVE));
						stage = Stage.記録モード;
						return;
					}
					case 2: {
						List<Text> plans = new ArrayList<>();
						int i = 1;
						InnPlan def = null;
						for (var v : inn.getPlans().stream().sorted().toList()) {
							if (def == null) {
								def = v;
							}
							plans.add(createText("006_" + i++, v.getVisibleName(), true, "007"));
						}
						paymentWindow.setText(I18N.get(GameSystemI18NKeys.一泊X金, def.getBaseValue() * GameSystem.getInstance().getParty().size()));
						plans.add(createText("006_" + i++, I18N.get(GameSystemI18NKeys.やめることにする), true, "005"));
						mainWindow.setText(createChoice("006", I18N.get(GameSystemI18NKeys.プランをお選びください), plans));
						return;
					}
					case 3: {
						//おわる
						mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
						stage = Stage.終了;
						return;
					}
				}
			}
			case 記録モード: {
				mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
				stage = Stage.終了;
				return;
			}
			case プラン検討中: {
				int selected = mainWindow.getSelect();
				if (selected >= inn.getPlans().stream().sorted().toList().size()) {
					//おわる
					mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
					stage = Stage.終了;
					return;
				}
				selectedPlan = inn.getPlans().stream().sorted().toList().get(selected);
				List<Text> nissu = new ArrayList<>();
				int i = 1;
				for (var v : List.of(1, 2, 3)) {
					nissu.add(createText("007_" + i++, I18N.get(GameSystemI18NKeys.X泊, v), true, "007"));
				}
				paymentWindow.setText(I18N.get(GameSystemI18NKeys.X金, selectedPlan.getSumPrice(GameSystem.getInstance().getParty().size())));
				nissu.add(createText("006_" + i++, I18N.get(GameSystemI18NKeys.やめることにする), true, "005"));
				mainWindow.setText(createChoice("007", I18N.get(GameSystemI18NKeys.何泊されますか), nissu));
				stage = Stage.日数検討中;
				return;
			}
			case 日数検討中: {
				int selected = mainWindow.getSelect();
				if (selected >= inn.getPlans().stream().sorted().toList().size()) {
					//おわる
					mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
					stage = Stage.終了;
					return;
				}
				paymentWindow.setText(I18N.get(GameSystemI18NKeys.X金, selectedPlan.getSumPrice(GameSystem.getInstance().getParty().size())));
				nissu = selected + 1;
				List<Text> payment = new ArrayList<>();
				int i = 1;
				for (var v : inn.getPaymentName().stream().sorted().toList()) {
					payment.add(createText("007" + i++, I18N.get(v), true, null));
				}
				payment.add(createText("006_" + i++, I18N.get(GameSystemI18NKeys.やめることにする), true, "005"));
				mainWindow.setText(createChoice("007", I18N.get(GameSystemI18NKeys.お支払いはどうされますか), payment));
				stage = Stage.支払い方法選択中;
				return;
			}
			case 支払い方法選択中: {
				int selected = mainWindow.getSelect();
				if (selected >= inn.getPlans().stream().sorted().toList().size()) {
					//おわる
					mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
					stage = Stage.終了;
					return;
				}
				paymentID = inn.getPaymentName().stream().sorted().toList().get(mainWindow.getSelect());
				mainWindow.setText(createChoice("007",
						I18N.get(GameSystemI18NKeys.確認となります) + Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.Xプラン, selectedPlan.getVisibleName()) + " " + I18N.get(GameSystemI18NKeys.X泊, nissu) + Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.お支払い方法XでX, I18N.get(paymentID), selectedPlan.getSumPrice(GameSystem.getInstance().getParty().size())) + Text.getLineSep()
						+ I18N.get(GameSystemI18NKeys.でよろしいですか),
						createText("008", I18N.get(GameSystemI18NKeys.ああ), true, null),
						createText("008", I18N.get(GameSystemI18NKeys.やめておく), true, null)));
				stage = Stage.最終確認中;
				return;
			}
			case 最終確認中: {
				int selected = mainWindow.getSelect();
				switch (selected) {
					case 1: {
						//金額が支払えるか確認
						int val = selectedPlan.getSumPrice(GameSystem.getInstance().getParty().size());
						if (GameSystem.getInstance().getMoneySystem().get(paymentID).getValue() < val) {
							//支払えない
							mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.お客様失礼ですがお金が足りないようです)
									+ I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
							stage = Stage.終了;
							return;
						}

						return;
					}
					case 2: {
						//おわる
						mainWindow.setText(createText("005", I18N.get(GameSystemI18NKeys.またのご利用お待ちしております), false, null));
						stage = Stage.終了;
						return;
					}
				}
			}
			case 宿泊中: {
			}
			case 終了: {
				stage = Stage.終了済み;
				return;
			}
			case 終了済み: {
				//処理なし
			}
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		if (stage != Stage.終了済み) {
			mainWindow.draw(g);
		}
		if (stage == Stage.日数検討中 || stage == Stage.最終確認中) {
			paymentWindow.draw(g);
		}
	}

	public Stage getStage() {
		return stage;
	}

	private Text createTextWEvent(String id, String textI18Nd, boolean allText, String next, String eventFile) {
		Text t = Text.noI18N(id, textI18Nd);
		if (allText) {
			t.setTc(new FrameTimeCounter(0));
			t.allText();
		} else {
			t.setTc(new FrameTimeCounter(1));
		}
		t.setNextId(next);
		t.setEvents(FieldEventParser.parse(id, eventFile));
		return t;
	}

	private Text createText(String id, String textI18Nd, boolean allText, String next) {
		Text t = Text.noI18N(id, textI18Nd);
		if (allText) {
			t.setTc(new FrameTimeCounter(0));
			t.allText();
		} else {
			t.setTc(new FrameTimeCounter(1));
		}
		t.setNextId(next);
		return t;
	}

	private Choice createChoice(String id, String textI18Nd, List<Text> options) {
		Choice c = Choice.noI18N(id, textI18Nd, options);
		return c;
	}

	private Choice createChoice(String id, String textI18Nd, Text... options) {
		Choice c = Choice.noI18N(id, textI18Nd, Arrays.asList(options));
		return c;
	}

}
