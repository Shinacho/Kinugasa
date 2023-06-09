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
import java.util.List;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import static kinugasa.game.system.BattleConfig.messageWindowY;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.Text;
import kinugasa.object.Drawable;

/**
 *
 * @vesion 1.0.0 - 2023/05/09_21:21:01<br>
 * @author Shinacho<br>
 */
public class BattleMessageWindowSystem implements Drawable {

	private BattleMessageWindowSystem() {
	}

	private static final BattleMessageWindowSystem INSTANCE = new BattleMessageWindowSystem();

	public static BattleMessageWindowSystem getInstance() {
		return INSTANCE;
	}
	//ステータス（上部）ペイン
	private BattleStatusWindows statusW;
	//コマンドウインドウ本体
	private BattleCommandMessageWindow cmdW;
	//ターゲット名表示ウインドウ(7)
	private ScrollSelectableMessageWindow tgtW;
	//アイテムChoiceUse(Choice
	private MessageWindow itemChoiceUseW;
	//アイテムコミット結果表示
	private MessageWindow itemCommitResultW;
	//移動後コマンドウインドウ
	private AfterMoveActionMessageWindow afterMoveW;
	//アクションリザルトウインドウ
	private MessageWindow actionResultW;
	//汎用INFOは別のウインドウと共存できる
	private MessageWindow infoW;
	//ステータス詳細ウインドウ
	private PCStatusWindow statusDescW;
	private int statusDescWPage = 0;
	//アイテム詳細ウインドウ
	private MessageWindow itemDescW;
	//バトルリザルト
	private MessageWindow battleResultW;

	void init() {
		List<Status> statusList = GameSystem.getInstance().getPartyStatus();
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - 6;
		float h = (float) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3.66f);
		cmdW = new BattleCommandMessageWindow(3, (int) messageWindowY, (int) w, (int) h);
		afterMoveW = new AfterMoveActionMessageWindow(3, (int) messageWindowY, (int) w, (int) h);
		tgtW = new ScrollSelectableMessageWindow(3, (int) messageWindowY, (int) w, (int) h, 7, false) {
			@Override
			public void setText(List<? extends Text> text) {
				if (text.size() <= 1) {
					throw new GameSystemException("BMWS target window contents is 0");
				}
				super.setText(text); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
			}

		};
		infoW = new MessageWindow(480, messageWindowY, w - 480, h, new SimpleMessageWindowModel().setNextIcon(""));
		statusW = new BattleStatusWindows(statusList);
		itemChoiceUseW = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		itemCommitResultW = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		actionResultW = new MessageWindow(3, messageWindowY, w, h, new SimpleMessageWindowModel().setNextIcon(""));
		statusDescW = new StatusDescWindow(
				24 + 8,
				(int) (BattleStatusWindows.h + 1),
				(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
				(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
				GameSystem.getInstance().getPartyStatus()
		);
		itemDescW = new MessageWindow(
				24 + 8,
				BattleStatusWindows.h + 1,
				(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
				(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60)
		);
		battleResultW = new MessageWindow(
				24 + 8,
				BattleStatusWindows.h + 1,
				(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
				(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60)
		);
		statusDescWPage = 0;

		setVisible(
				StatusVisible.ON,
				Mode.ACTION,
				InfoVisible.OFF);

	}

	void statusDescWindowNextPage() {
		statusDescWPage++;
		if (statusDescWPage >= 5) {
			statusDescWPage = 0;
		}
		int pcIdx = statusDescW.getPcIdx();
		switch (statusDescWPage) {
			case 0:
				statusDescW = new StatusDescWindow(
						24 + 8,
						(int) (BattleStatusWindows.h + 1),
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			case 1:
				statusDescW = new AttrDescWindow(
						24 + 8,
						(int) (BattleStatusWindows.h + 1),
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			case 2:
				statusDescW = new EqipItemWindow(
						24 + 8,
						(int) (BattleStatusWindows.h + 1),
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			case 3:
				statusDescW = new ActionDescWindow(
						24 + 8,
						(int) (BattleStatusWindows.h + 1),
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			case 4:
				statusDescW = new ConditionDescWindow(
						24 + 8,
						(int) (BattleStatusWindows.h + 1),
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
						GameSystem.getInstance().getPartyStatus()
				);
				break;
			default:
				throw new AssertionError();
		}
		statusDescW.setPcIdx(pcIdx);
	}

	void setItemDesc(Status user, Item i) {
		//アイテムの詳細をサブに表示
		StringBuilder sb = new StringBuilder();
		sb.append(i.getVisibleName()).append(Text.getLineSep());

		//DESC
		String desc = i.getDesc();
		if (desc.contains(Text.getLineSep())) {
			String[] sv = desc.split(Text.getLineSep());
			for (String v : sv) {
				sb.append(" ").append(v);
				sb.append(Text.getLineSep());
			}
		} else {
			sb.append(" ").append(i.getDesc());
			sb.append(Text.getLineSep());
		}
		//価値
		sb.append(" ").append(I18N.get(GameSystemI18NKeys.価値)).append(":").append(i.getValue());
		sb.append(Text.getLineSep());
		//装備スロット
		sb.append(" ").append(I18N.get(GameSystemI18NKeys.装備スロット)).append(":").append(i.getEqipmentSlot() != null
				? i.getEqipmentSlot().getName()
				: I18N.get(GameSystemI18NKeys.なし));
		sb.append(Text.getLineSep());
		//WMT
		if (i.getWeaponMagicType() != null) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.武器種別)).append(":").append(i.getWeaponMagicType().getName());
			sb.append(Text.getLineSep());
		}
		//area
		int area = 0;
		if (i.isEqipItem()) {
			//範囲表示するのは武器だけ
			if (i.getWeaponMagicType() != null) {
				area = i.getArea();
			}
		} else {
			if (i.getBattleEvent() != null && !i.getBattleEvent().isEmpty()) {
				area = (int) (user.getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
			}
		}
		if (area != 0) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.範囲)).append(":").append(area);
			sb.append(Text.getLineSep());
		}
		//DCS
		if (i.getDamageCalcStatusKey() != null && !i.getDamageCalcStatusKey().isEmpty()) {
			String dcs = "";
			for (StatusKey s : i.getDamageCalcStatusKey()) {
				dcs += s.getDesc() + ",";
			}
			dcs = dcs.substring(0, dcs.length() - 1);
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.ダメージ計算方式)).append(":").append(dcs);
			sb.append(Text.getLineSep());
		}
		//戦闘中アクション
		if (i.getBattleEvent() != null && !i.getBattleEvent().isEmpty()) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムは戦闘中使える));
			sb.append(Text.getLineSep());
		}
		if (i.isEqipItem()) {
			//攻撃回数
			if (i.getActionCount() > 1) {
				sb.append(" ").append(I18N.get(GameSystemI18NKeys.攻撃回数).replaceAll("n", i.getActionCount() + ""));
				sb.append(Text.getLineSep());
			}
			//eqStatus
			if (i.getEqStatus() != null && !i.getEqStatus().isEmpty()) {
				for (StatusValue s : i.getEqStatus()) {
					if (StatusDescWindow.getUnvisibleStatusList().contains(s.getName())) {
						continue;
					}
					String v;
					if (s.getKey().getMax() <= 1f) {
						v = (float) (s.getValue() * 100) + "%";//1%単位
					} else {
						v = (int) s.getValue() + "";
					}
					if (!v.startsWith("0")) {
						sb.append(" ");
						sb.append(s.getKey().getDesc()).append(":").append(v);
						sb.append(Text.getLineSep());
					}
				}
			}
			//eqAttr
			if (i.getEqAttr() != null && !i.getEqAttr().isEmpty()) {
				for (AttributeValue a : i.getEqAttr()) {
					String v = (float) (a.getValue() * 100) + "%";
					if (!v.startsWith("0")) {
						sb.append(" ");
						sb.append(a.getKey().getDesc()).append(":").append(v);
						sb.append(Text.getLineSep());
					}
				}
			}
		}
		itemDescW.setText(sb.toString());
		itemDescW.allText();
	}

	void statusDescWindowNextSelect() {
		switch (statusDescWPage) {
			case 0:
			case 2:
				break;
			case 1:
			case 3:
			case 4:
				statusDescW.next();
				break;
		}
	}

	void statusDescWindowPrevSelect() {
		switch (statusDescWPage) {
			case 0:
			case 2:
				break;
			case 1:
			case 3:
			case 4:
				statusDescW.prev();
				break;
		}
	}

	void saveVisible() {
		prevSv = sv;
		prevMode = mode;
		prevIv = iv;
	}

	void setVisibleFromSave() {
		setVisible(prevSv, prevMode, prevIv);
	}

	enum Mode {
		TGT_SELECT,
		CMD_SELECT,
		ITEM_USE_SELECT,
		ITEM_COMMIT,
		AFTER_MOVE,
		ACTION,
		NOTHING,
		SHOW_STATUS_DESC,
		SHOW_ITEM_DESC,
		BATTLE_RESULT,
	}

	enum StatusVisible {
		ON,
		OFF
	}

	enum InfoVisible {
		ON,
		OFF
	}
	private Mode mode, prevMode;
	private StatusVisible sv, prevSv;
	private InfoVisible iv, prevIv;

	void setStatusDescPCIDX(int i) {
		statusDescW.setPcIdx(i);
	}

	void setVisible(Mode m) {
		setVisible(StatusVisible.ON, m, InfoVisible.OFF);
	}

	void setVisible(StatusVisible sv, Mode m) {
		setVisible(sv, m, InfoVisible.OFF);
	}

	void setVisible(StatusVisible sv, Mode m, InfoVisible iv) {
		if (m == null) {
			throw new GameSystemException("BMWS : mode is null");
		}
		this.sv = sv;
		this.mode = m;
		this.iv = iv;
		statusW.setVisible(sv == StatusVisible.ON);
		infoW.setVisible(iv == InfoVisible.ON);
		cmdW.setVisible(false);
		tgtW.setVisible(false);
		afterMoveW.setVisible(false);
		itemChoiceUseW.setVisible(false);
		itemCommitResultW.setVisible(false);
		actionResultW.setVisible(false);
		statusDescW.setVisible(false);
		itemDescW.setVisible(false);
		battleResultW.setVisible(false);
		switch (m) {
			case NOTHING:
				break;
			case CMD_SELECT:
				cmdW.setVisible(true);
				break;
			case TGT_SELECT:
				tgtW.setVisible(true);
				break;
			case AFTER_MOVE:
				afterMoveW.reset();
				afterMoveW.setVisible(true);
				break;
			case ITEM_USE_SELECT:
				itemChoiceUseW.setVisible(true);
				break;
			case ITEM_COMMIT:
				itemCommitResultW.setVisible(true);
				break;
			case ACTION:
				actionResultW.setVisible(true);
				break;
			case SHOW_STATUS_DESC:
				statusDescW = new StatusDescWindow(
						24 + 8,
						(int) (BattleStatusWindows.h + 1),
						(int) (GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() / 1.5),
						(int) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() - 32 - 60),
						GameSystem.getInstance().getPartyStatus()
				);
				cmdW.setVisible(true);
				statusDescW.setVisible(true);
				break;
			case SHOW_ITEM_DESC:
				itemChoiceUseW.setVisible(true);
				itemDescW.setVisible(true);
				break;
			case BATTLE_RESULT:
				battleResultW.setVisible(true);
				break;
			default:
				throw new AssertionError("undefined BMWS case : " + m);
		}
	}

	MessageWindow getActionResultW() {
		return actionResultW;
	}

	BattleCommandMessageWindow getCmdW() {
		return cmdW;
	}

	ScrollSelectableMessageWindow getTgtW() {
		return tgtW;
	}

	MessageWindow getItemChoiceUseW() {
		return itemChoiceUseW;
	}

	MessageWindow getItemCommitResultW() {
		return itemCommitResultW;
	}

	BattleStatusWindows getStatusW() {
		return statusW;
	}

	AfterMoveActionMessageWindow getAfterMoveW() {
		return afterMoveW;
	}

	PCStatusWindow getStatusDescW() {
		return statusDescW;
	}

	MessageWindow getInfoW() {
		return infoW;
	}

	MessageWindow getBattleResultW() {
		return battleResultW;
	}

	MessageWindow getItemDescW() {
		return itemDescW;
	}

	int getStatusDescWPage() {
		return statusDescWPage;
	}
	public static final int ITEM_CHOICE_USE_CHECK = 0;
	public static final int ITEM_CHOICE_USE_USE = 1;
	public static final int ITEM_CHOICE_USE_EQIP = 2;
	public static final int ITEM_CHOICE_USE_PASS = 3;
	private int itemChoiceUseSelected = -1;

	void openItemChoiceUse() {
		if (!(cmdW.getSelectedCmd() instanceof Item)) {
			throw new GameSystemException("requested open item choice use window, but selected action is not item");
		}
		Item i = (Item) cmdW.getSelectedCmd();
		List<Text> options = new ArrayList<>();
		options.add(new Text(I18N.get(GameSystemI18NKeys.調べる)));
		options.add(new Text(I18N.get(GameSystemI18NKeys.使う)));
		options.add(new Text(I18N.get(GameSystemI18NKeys.装備)));
		options.add(new Text(I18N.get(GameSystemI18NKeys.渡す)));
		itemChoiceUseW.setText(new Choice(options, "BATTLE_MW_SYSTEM_IUC", I18N.get(GameSystemI18NKeys.Xを, i.getVisibleName())));
		setVisible(StatusVisible.ON, mode.ITEM_USE_SELECT, InfoVisible.OFF);
	}

	void itemChoiceUseNextSelect() {
		if (mode != Mode.ITEM_USE_SELECT) {
			throw new GameSystemException("item choice use select, but mode is not item choice use");
		}
		itemChoiceUseW.nextSelect();
	}

	void itemChoiceUsePrevSelect() {
		if (mode != Mode.ITEM_USE_SELECT) {
			throw new GameSystemException("item choice use select, but mode is not item choice use");
		}
		itemChoiceUseW.prevSelect();
	}

	int itemChoiceUseCommit() {
		if (mode != Mode.ITEM_USE_SELECT) {
			throw new GameSystemException("item choice use select, but mode is not item choice use");
		}
		return itemChoiceUseSelected = itemChoiceUseW.getSelect();
	}

	void update() {
		if (statusW.isVisible()) {
			statusW.update();
		}
		//
		if (cmdW.isVisible()) {
			cmdW.update();
		}
		//
		if (afterMoveW.isVisible()) {
			afterMoveW.update();
		}
		if (actionResultW.isVisible()) {
			actionResultW.update();
		}
		if (tgtW.isVisible()) {
			tgtW.update();
		}
		//
		if (itemChoiceUseW.isVisible()) {
			itemChoiceUseW.update();
		}
		if (itemCommitResultW.isVisible()) {
			itemCommitResultW.update();
		}
		//
		if (infoW.isVisible()) {
			infoW.update();
		}
		if (statusDescW.isVisible()) {
			statusDescW.update();
		}
		if (itemDescW.isVisible()) {
			itemDescW.update();
		}
		if (battleResultW.isVisible()) {
			battleResultW.update();
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		statusW.draw(g);
		//
		cmdW.draw(g);
		//
		afterMoveW.draw(g);
		actionResultW.draw(g);
		tgtW.draw(g);
		//
		itemChoiceUseW.draw(g);
		itemCommitResultW.draw(g);
		//
		infoW.draw(g);
		statusDescW.draw(g);
		itemDescW.draw(g);
		//
		battleResultW.draw(g);
	}

}
