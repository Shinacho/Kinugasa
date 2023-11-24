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

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import kinugasa.game.I18N;
import static kinugasa.game.system.BattleConfig.messageWindowY;
import kinugasa.game.ui.Choice;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.MessageWindow;
import kinugasa.game.ui.ScrollSelectableMessageWindow;
import kinugasa.game.ui.SimpleMessageWindowModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.Text;
import kinugasa.game.ui.TextLabelSprite;
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
	private MessageWindow afterMoveActionListW;
	//ステータス詳細ウインドウ
	private PCStatusWindow statusDescW;
	private int statusDescWPage = 0;
	//アイテム詳細ウインドウ
	private MessageWindow itemDescW;
	//バトルリザルト
	private MessageWindow battleResultW;
	private TextLabelSprite turnOrder;

	void init() {
		List<Actor> statusList = GameSystem.getInstance().getParty();
		float w = GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize() - 6;
		float h = (float) (GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize() / 3.62f);
		cmdW = new BattleCommandMessageWindow(3, (int) messageWindowY, (int) w, (int) h);
		afterMoveW = new AfterMoveActionMessageWindow(3, (int) messageWindowY, (int) w, (int) h);
		tgtW = new ScrollSelectableMessageWindow(3, (int) messageWindowY, (int) w, (int) h, 7, false) {
			@Override
			public void setText(List<? extends Text> text) {
				if (text.size() <= 1) {
					throw new GameSystemException("BMWS target window contents is 0");
				}
				super.setText(text);
			}

		};
		turnOrder = new TextLabelSprite("", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontSize(12)),
				3, messageWindowY - 14, w, 24);
		infoW = new MessageWindow(560, messageWindowY, w - 560, 38, new SimpleMessageWindowModel().setNextIcon("")) {
			@Override
			public void setText(String text) {
				super.setText(text); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
				allText();
			}

			@Override
			public void setText(Text text) {
				super.setText(text); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
				allText();
			}

		};
		afterMoveActionListW = new MessageWindow(3, messageWindowY, 557, h, new SimpleMessageWindowModel().setNextIcon(""));
		statusW = new BattleStatusWindows(statusList.stream().filter(p -> !p.isSummoned()).toList());
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

	void setItemDesc(Actor user, Item i) {
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
		//装備品の場合、スタイルとエンチャを表示
		if (i.getSlot() != null) {
			//スタイル
			if (i.getStyle() != null) {
				sb.append(I18N.get(GameSystemI18NKeys.様式))
						.append(":")
						.append(i.getStyle().getVisibleName())
						.append("(")
						.append(i.getStyle().descI18N())
						.append(")")
						.append(Text.getLineSep());
			}
			//エンチャント
			if (i.getEnchant() != null) {
				sb.append(I18N.get(GameSystemI18NKeys.エンチャント))
						.append(":")
						.append(i.getEnchant().getVisibleName())
						.append("(")
						.append(i.getEnchant().descI18N())
						.append(")")
						.append(Text.getLineSep());
			}
		}
		//価値
		if (i.canSale()) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.価値))
					.append(":").append(i.getPrice());
			sb.append(Text.getLineSep());
		}
		//装備スロット
		if (i.getSlot() != null) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.装備スロット))
					.append(":").append(i.getSlot().getVisibleName());
			sb.append(Text.getLineSep());
		}
		//WMT
		if (i.getWeaponType() != null) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.武器種別))
					.append(":").append(i.getWeaponType().getVisibleName());
			sb.append(Text.getLineSep());
		}
		//area
		int area = 0;
		if (i.isWeapon()) {
			//範囲表示するのは武器だけ
			area = i.getArea();
		}
		if (area != 0) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.範囲)).append(":").append(area);
			sb.append(Text.getLineSep());
		}
		//キーアイテム属性
		if (!i.canSale()) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムは売ったり捨てたり解体したりできない));
			sb.append(Text.getLineSep());
		}
		//DCS
		if (i.getDcs() != null) {
			String dcs = i.getDcs().getVisibleName();
			dcs = dcs.substring(0, dcs.length() - 1);
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.ダメージ計算ステータス)).append(":").append(dcs);
			sb.append(Text.getLineSep());
		}
		//戦闘中アクション
		if (i.isBattle()) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムは戦闘中使える));
			sb.append(Text.getLineSep());
		}
		//フィールドアクション
		if (i.isField()) {
			sb.append(" ").append(I18N.get(GameSystemI18NKeys.このアイテムはフィールドで使える));
			sb.append(Text.getLineSep());
		}
		if (i.getSlot() != null) {
			if (user.getStatus().getEqip().values().contains(i)) {
				sb.append("[").append(I18N.get(GameSystemI18NKeys.装備中)).append("]");
				sb.append(Text.getLineSep());
			}
			//攻撃回数
			if (i.isWeapon() && i.getEffectedATKCount() > 1) {
				sb.append(" ").append(I18N.get(GameSystemI18NKeys.攻撃回数)).append(":")
						.append(i.getEffectedATKCount() + "");
				sb.append(Text.getLineSep());
			}
			//eqStatus
			sb.append(I18N.get(GameSystemI18NKeys.ステータス));
			if (i.getEffectedStatus() != null && !i.getEffectedStatus().isEmpty()) {
				for (StatusValue s : i.getEffectedStatus()) {
					if (!s.getKey().isVisible()) {
						continue;
					}
					String v;
					if (s.getKey().isPercent()) {
						v = (float) (s.getValue() * 100) + "%";//1%単位
					} else {
						v = (int) s.getValue() + "";
					}
					if (!v.startsWith("0")) {
						sb.append(" ");
						sb.append(s.getKey().getVisibleName()).append(":").append(v);
						sb.append(Text.getLineSep());
					}
				}
			}
			//eqAttr
			sb.append(I18N.get(GameSystemI18NKeys.被属性));
			sb.append(Text.getLineSep());
			if (i.getEffectedAttrIn() != null && !i.getEffectedAttrIn().isEmpty()) {
				for (AttributeValue a : i.getEffectedAttrIn()) {
					String v = (float) (a.getValue() * 100) + "%";
					if (!v.startsWith("0")) {
						sb.append(" ");
						sb.append(a.getKey().getVisibleName()).append(":").append(v);
						sb.append(Text.getLineSep());
					}
				}
			}
			sb.append(I18N.get(GameSystemI18NKeys.与属性));
			sb.append(Text.getLineSep());
			if (i.getEffectedAttrOut() != null && !i.getEffectedAttrOut().isEmpty()) {
				for (AttributeValue a : i.getEffectedAttrOut()) {
					String v = (float) (a.getValue() * 100) + "%";
					if (!v.startsWith("0")) {
						sb.append(" ");
						sb.append(a.getKey().getVisibleName()).append(":").append(v);
						sb.append(Text.getLineSep());
					}
				}
			}
			sb.append(I18N.get(GameSystemI18NKeys.状態異常耐性));
			sb.append(Text.getLineSep());
			if (i.getEffectedConditionRegist() != null && !i.getEffectedConditionRegist().isEmpty()) {
				for (Map.Entry<ConditionKey, Float> e : i.getEffectedConditionRegist().entrySet()) {
					String v = (float) (e.getValue() * 100) + "%";
					if (!v.startsWith("0")) {
						sb.append(" ");
						sb.append(e.getKey().getVisibleName()).append(":").append(v);
						sb.append(Text.getLineSep());
					}
				}
			}
			sb.append(Text.getLineSep());
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
		AFTERMOVE_ACTIONLIST,
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
		afterMoveActionListW.setVisible(false);
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
				turnOrder.setVisible(false);
				battleResultW.setVisible(true);
				break;
			case AFTERMOVE_ACTIONLIST:
				afterMoveActionListW.setVisible(true);
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

	MessageWindow getAfterMoveActionListW() {
		return afterMoveActionListW;
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
	private int itemChoiceUseSelected = -1;

	void openItemChoiceUse() {
		if (!(cmdW.getSelectedCmd() instanceof Item)) {
			throw new GameSystemException("requested open item choice use window, but selected action is not item");
		}
		Item i = (Item) cmdW.getSelectedCmd();
		List<Text> options = new ArrayList<>();
		Actor user = BattleSystem.getInstance().getCurrentCmd().getUser();
		options.add(new Text(I18N.get(GameSystemI18NKeys.調べる)));
		options.add(new Text(I18N.get(GameSystemI18NKeys.使う)));
		if (user.getStatus().getEqip().values().contains(i)) {
			options.add(new Text(I18N.get(GameSystemI18NKeys.装備解除)));
		} else {
			options.add(new Text(I18N.get(GameSystemI18NKeys.装備)));
		}
		for (Actor a : BattleSystem.getInstance().getTargetSystem().itemPassTarget(user)) {
			options.add(new Text(I18N.get(GameSystemI18NKeys.Xに, a.getVisibleName()) + I18N.get(GameSystemI18NKeys.渡す)));
		}
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
		if (afterMoveActionListW.isVisible()) {
			afterMoveActionListW.update();
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		statusW.draw(g);
		//
		cmdW.draw(g);
		//
		afterMoveW.draw(g);
		tgtW.draw(g);
		//
		itemChoiceUseW.draw(g);
		itemCommitResultW.draw(g);
		//
		turnOrder.draw(g);
		afterMoveActionListW.draw(g);
		infoW.draw(g);
		statusDescW.draw(g);
		itemDescW.draw(g);
		//
		battleResultW.draw(g);
		actionResultW.draw(g);
	}

	public TextLabelSprite getTurnOrder() {
		return turnOrder;
	}

	@Override
	public String toString() {
		return "BattleMessageWindowSystem{" + "statusW=" + statusW + ", cmdW=" + cmdW + ", tgtW=" + tgtW + ", itemChoiceUseW=" + itemChoiceUseW + ", itemCommitResultW=" + itemCommitResultW + ", afterMoveW=" + afterMoveW + ", actionResultW=" + actionResultW + ", infoW=" + infoW + ", statusDescW=" + statusDescW + ", statusDescWPage=" + statusDescWPage + ", itemDescW=" + itemDescW + ", battleResultW=" + battleResultW + ", mode=" + mode + ", prevMode=" + prevMode + ", sv=" + sv + ", prevSv=" + prevSv + ", iv=" + iv + ", prevIv=" + prevIv + ", itemChoiceUseSelected=" + itemChoiceUseSelected + '}';
	}

}
