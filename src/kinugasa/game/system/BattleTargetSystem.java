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
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import kinugasa.game.GameLog;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LoopCall;
import kinugasa.game.NewInstance;
import kinugasa.game.Nullable;
import kinugasa.game.OneceTime;
import static kinugasa.game.system.Action.ターゲットモード.グループ_切替可能_初期選択味方;
import static kinugasa.game.system.Action.ターゲットモード.グループ_切替可能_初期選択味方_自身除く;
import static kinugasa.game.system.Action.ターゲットモード.グループ_切替可能_初期選択敵;
import static kinugasa.game.system.Action.ターゲットモード.グループ_切替可能_初期選択敵_自身除く;
import static kinugasa.game.system.Action.ターゲットモード.グループ_味方全員;
import static kinugasa.game.system.Action.ターゲットモード.グループ_味方全員_自身除く;
import static kinugasa.game.system.Action.ターゲットモード.グループ_敵全員;
import static kinugasa.game.system.Action.ターゲットモード.全員;
import static kinugasa.game.system.Action.ターゲットモード.全員_自身除く;
import static kinugasa.game.system.Action.ターゲットモード.単体_切替可能_自身含まない_初期選択味方;
import static kinugasa.game.system.Action.ターゲットモード.単体_切替可能_自身含まない_初期選択敵;
import static kinugasa.game.system.Action.ターゲットモード.単体_切替可能_自身含む_初期選択味方;
import static kinugasa.game.system.Action.ターゲットモード.単体_切替可能_自身含む_初期選択敵;
import static kinugasa.game.system.Action.ターゲットモード.単体_味方のみ_自身含まない;
import static kinugasa.game.system.Action.ターゲットモード.単体_味方のみ_自身含む;
import static kinugasa.game.system.Action.ターゲットモード.単体_敵のみ;
import static kinugasa.game.system.Action.ターゲットモード.自身のみ;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.object.Drawable;
import kinugasa.object.Sprite;
import kinugasa.util.FrameTimeCounter;
import kinugasa.game.NotNull;
import static kinugasa.game.system.Action.死亡者ターゲティング.気絶損壊解脱者を選択可能;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_19:16:01<br>
 * @author Shinacho<br>
 */
public class BattleTargetSystem implements Drawable {

	private static final BattleTargetSystem INSTANCE = new BattleTargetSystem();

	private BattleTargetSystem() {
	}

	static BattleTargetSystem getInstance() {
		return INSTANCE;
	}

	@NewInstance
	@NotNull
	static List<Actor> recalcDistance(List<Actor> tgt, Point2D.Float p, int area) {
		List<Actor> res = new ArrayList<>();
		for (Actor a : tgt) {
			if (a.getSprite().getCenter().distance(p) < area) {
				res.add(a);
			}
		}
		return res;
	}

	enum TeamSelect {
		未使用,
		味方選択中,
		敵選択中,;

		TeamSelect switchTeam() {
			return switch (this) {
				case 未使用 ->
					throw new GameSystemException("missing team switch(TS)");
				case 味方選択中 ->
					TeamSelect.敵選択中;
				case 敵選択中 ->
					TeamSelect.味方選択中;
			};
		}
	}

	//選択中PC
	private Actor currentUser;
	//選択中アクション
	private Action currentBA;
	//エリア表示
	//常に選択中のPCを中心にするエリア
	private BattleActionAreaSprite pcCenterArea;
	private static final Color PC_CENTER_AREA_COLOR = Color.GREEN;
	//移動時だけ出てPCの初期位置を中心にするエリア（注意：クローンの座標をセットせよ！
	private BattleActionAreaSprite initialCenterArea;
	private static final Color INITIAL_CENTER_AREA_COLOR = Color.BLUE;

	//選択中ターゲットの選択アイコン点滅時間
	private static final int ICON_BLINK_TIME = 8;
	private FrameTimeCounter iconBlinkTC = new FrameTimeCounter(ICON_BLINK_TIME);
	//選択中アイコンのマスタ
	private Sprite iconMaster;
	//選択中アイコンの実態
	private List<Sprite> icons = new ArrayList<>();
	//状態
	//INAREA内の選択されている標的のIDX
	private int selectedIdx;
	//スイッチチームの状況（初期選択によって最初はどちらかになる
	private TeamSelect teamSelect;
	//
	//キャッシュ
	//現在のINAREA（SELFも入る可能性あり
	private List<Actor> inArea候補者 = new ArrayList<>();
	//
	//魔法詠唱時の保存
	private Map<Actor, List<Actor>> castTgt = new HashMap<>();

	@Nullable
	public static Actor random(Actor user, Action a) {
		if (a.getType() == ActionType.行動) {
			return null;
		}
		int area;
		if (a.getType() == ActionType.アイテム) {
			area = (int) (user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() / 2);
		} else {
			area = user.getStatus().getEffectedArea(a);
		}
		List<Actor> tgt = new ArrayList<>();
		tgt.addAll(getInstance().allEnemyOf(user.getSprite().getCenter(), area));
		tgt.addAll(getInstance().allPartyOf(user.getSprite().getCenter(), area));
		if (tgt.isEmpty()) {
			return null;
		}
		return Random.randomChoice(tgt);
	}

	public void saveNowTgt(Actor a) {
		castTgt.put(a, getSelected());
	}

	public void saveTgt(Actor a, List<Actor> tgt) {
		castTgt.put(a, tgt);
	}

	@Nullable
	public List<Actor> getSavedTarget(Actor a) {
		return castTgt.get(a);
	}

	@OneceTime
	void init() {
		iconBlinkTC = new FrameTimeCounter(ICON_BLINK_TIME);
		//アイコンマスタをみえない位置に配置
		iconMaster = new TextLabelSprite("↓", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontStyle(Font.BOLD)), -123, -123, 12, 12);
		selectedIdx = 0;
		pcCenterArea = new BattleActionAreaSprite(PC_CENTER_AREA_COLOR);
		pcCenterArea.setVisible(false);
		initialCenterArea = new BattleActionAreaSprite(INITIAL_CENTER_AREA_COLOR);
		initialCenterArea.setVisible(false);
		inArea候補者.clear();
		icons.clear();
	}

	public void setAreaVisible(boolean ini, boolean cur) {
		initialCenterArea.setVisible(ini);
		pcCenterArea.setVisible(cur);
	}

	public void resetArea() {

	}

	@LoopCall
	void update() {
		//アイコン点滅制御
		if (!icons.isEmpty()) {
			if (iconBlinkTC.isReaching()) {
				iconBlinkTC = new FrameTimeCounter(ICON_BLINK_TIME);
				icons.forEach(p -> p.switchVisible());
			}
		}
	}

	void setInitialAreaLocation(Point2D.Float p) {
		initialCenterArea.setLocationByCenter(p);
		initialCenterArea.setArea((int) currentUser.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue());
		initialCenterArea.setVisible(true);
	}

	void setCurrentLocation() {
		pcCenterArea.setLocationByCenter(currentUser.getSprite().getCenter());
		pcCenterArea.setArea(
				(int) currentUser.getStatus().getEffectedStatus().get(StatusKey.残行動力).getValue());
		pcCenterArea.setVisible(true);
	}

	void setCurrent(Actor a) {
		if (a == null) {
			throw new GameSystemException("current user is null : " + this);
		}
		this.currentUser = a;
		//各種フラグの初期化
		initTeamSelect();
		selectedIdx = 0;
	}

	void setCurrent(Action a) {
		if (a == null) {
			//アイテムを持っていない等で何も実施することがない
			pcCenterArea.setVisible(false);
			return;
		}
		this.currentBA = a;
		if (currentUser.isPlayer()) {
			pcCenterArea.setLocationByCenter(currentUser.getSprite().getCenter());
			if (a.getType() == ActionType.行動) {
				if (BattleConfig.ActionID.移動.equals(a.getId()) || BattleConfig.ActionID.逃走.equals(a.getId())) {
					pcCenterArea.setArea((int) currentUser.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue());
					pcCenterArea.setVisible(true);
				} else {
					pcCenterArea.setVisible(false);
				}
			} else if (a.getType() == ActionType.アイテム) {
				pcCenterArea.setArea((int) currentUser.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() / 2);
				pcCenterArea.setVisible(true);
			} else {
				pcCenterArea.setArea(currentUser.getStatus().getEffectedArea(a));
				pcCenterArea.setVisible(true);
			}
		} else {
			pcCenterArea.setVisible(false);
		}
		initTeamSelect();
		selectedIdx = 0;
	}

	void setCurrent(CommandWindow w) {
		setCurrent(w.getSelectedCmd());
	}

	void setIconVisible(boolean f) {
		if (f) {
			updateIcons();
		} else {
			icons.clear();
		}
	}

	void unset() {
		inArea候補者.clear();
		icons.clear();
		selectedIdx = 0;
		pcCenterArea.setVisible(false);
		initialCenterArea.setVisible(false);
		teamSelect = TeamSelect.未使用;
	}

	void nextTgt() {
		if (inArea候補者.isEmpty()) {
			return;
		}
		selectedIdx++;
		if (selectedIdx > inArea候補者.size()) {
			selectedIdx = 0;
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print("TS : " + selectedIdx + " of " + inArea候補者);
		}
		updateIcons();
	}

	void prevTgt() {
		if (inArea候補者.isEmpty()) {
			return;
		}
		selectedIdx--;
		if (selectedIdx < 0) {
			selectedIdx = inArea候補者.size() - 1;
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print("TS : " + selectedIdx + " of " + inArea候補者);
		}
		updateIcons();
	}

	//グループで切替可能な時だけ発動する。グループを切り替える。これはgetSElectedのリストに影響する
	void switchTeam() {
		if (currentBA.getTgtType().isチーム切替可能()) {
			teamSelect = teamSelect.switchTeam();
			updateInArea();
			updateIcons();
		}
		if (GameSystem.isDebugMode()) {
			GameLog.print("TS : " + selectedIdx + " of " + inArea候補者);
		}
	}

	//init
	private void initTeamSelect() {
		if (currentBA == null) {
			if (GameSystem.isDebugMode()) {
				GameLog.print(" TS init team select : currentBA is null");
			}
			return;
		}
		//ターゲットタイプのないアクション（移動等）の場合は何もしない
		if (currentBA.getType() == ActionType.行動) {
			if (GameSystem.isDebugMode()) {
				GameLog.print(" TS init team select : currentBA is 行動");
			}
			return;
		}
		if (currentBA.getType() == ActionType.アイテム) {
			if (!currentBA.hasEvent()) {
				if (GameSystem.isDebugMode()) {
					GameLog.print(" TS init team select : currentBA is no event アイテム");
				}
				return;
			}
		}

		//カレントに基づいてTEAM＿SELECTの初期値設定
		this.teamSelect = switch (currentBA.getTgtType()) {
			case グループ_切替可能_初期選択味方 ->
				TeamSelect.味方選択中;
			case 単体_切替可能_自身含まない_初期選択味方 ->
				TeamSelect.味方選択中;
			case 単体_切替可能_自身含む_初期選択味方 ->
				TeamSelect.味方選択中;
			case グループ_切替可能_初期選択敵 ->
				TeamSelect.敵選択中;
			case 単体_切替可能_自身含まない_初期選択敵 ->
				TeamSelect.敵選択中;
			case 単体_切替可能_自身含む_初期選択敵 ->
				TeamSelect.敵選択中;
			case グループ_味方全員 ->
				TeamSelect.未使用;
			case グループ_敵全員 ->
				TeamSelect.未使用;
			case 全員 ->
				TeamSelect.未使用;
			case 単体_味方のみ_自身含まない ->
				TeamSelect.未使用;
			case 単体_味方のみ_自身含む ->
				TeamSelect.未使用;
			case 単体_敵のみ ->
				TeamSelect.未使用;
			case 自身のみ ->
				TeamSelect.未使用;
			case グループ_切替可能_初期選択味方_自身除く ->
				TeamSelect.味方選択中;
			case グループ_切替可能_初期選択敵_自身除く ->
				TeamSelect.敵選択中;
			case グループ_味方全員_自身除く ->
				TeamSelect.味方選択中;
			case 全員_自身除く ->
				TeamSelect.未使用;
		};
		if (GameSystem.isDebugMode()) {
			GameLog.print(" TS init team select : " + teamSelect);
		}
	}

	List<Actor> allPartyOf(Point2D.Float center, int area) {
		List<Actor> result = new ArrayList<>();
		for (Actor pc : GameSystem.getInstance().getParty()) {
			if (pc.getSprite().getCenter().distance(center) <= area) {
				result.add(pc);
			}
		}
		return result;
	}

	List<Actor> allEnemyOf(Point2D.Float center, int area) {
		List<Actor> result = new ArrayList<>();
		for (Actor e : BattleSystem.getInstance().getEnemies()) {
			if (e.getSprite().getCenter().distance(center) <= area) {
				result.add(e);
			}
		}
		return result;
	}

	List<Actor> itemPassTarget(Actor user) {
		int area = (int) (user.getStatus().getEffectedStatus().get(StatusKey.行動力).getValue() / 2);
		Point2D.Float center = user.getSprite().getCenter();
		List<Actor> result = new ArrayList<>();
		for (Actor e : BattleSystem.getInstance().getEnemies()) {
			if (e.getSprite().getCenter().distance(center) <= area) {
				if (!e.isSummoned()) {
					if (e.getStatus().getItemBag().canAdd()) {
						result.add(e);
					}
				}
			}
		}
		return result;
	}

	@NotNull
	@NewInstance
	public List<Actor> getSelected() {
		if (inArea候補者.isEmpty()) {
			return Collections.emptyList();
		}
		switch (currentBA.getTgtType()) {
			case グループ_味方全員:
			case グループ_敵全員:
			case 全員:
			case グループ_切替可能_初期選択味方:
			case グループ_切替可能_初期選択敵:
			case グループ_味方全員_自身除く:
			case 全員_自身除く:
			case グループ_切替可能_初期選択味方_自身除く:
			case グループ_切替可能_初期選択敵_自身除く:
				return new ArrayList<>(inArea候補者);
			case 自身のみ:
				return List.of(currentUser);
			case 単体_切替可能_自身含む_初期選択味方:
			case 単体_切替可能_自身含む_初期選択敵:
			case 単体_味方のみ_自身含む:
			case 単体_切替可能_自身含まない_初期選択味方:
			case 単体_切替可能_自身含まない_初期選択敵:
			case 単体_味方のみ_自身含まない:
			case 単体_敵のみ: {
				//inareaはソートされているし、自身も含む
				return List.of(inArea候補者.get(selectedIdx));
			}
			default:
				throw new GameSystemException("teamSelect mismatch (TS)");
		}
	}

	//getSElectedにアイコンを設置する
	private void updateIcons() {
		icons.clear();
		if (!currentUser.isPlayer()) {
			return;
		}
		List<Actor> selected = getSelected();
		if (selected.isEmpty()) {
			return;
		}
		for (Actor c : selected) {
			float x = c.getSprite().getCenterX();
			float y = c.getSprite().getCenterY() - iconMaster.getHeight() - 14;
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(new Point2D.Float(x, y));
			icons.add(i);
			i.setVisible(true);
		}
	}

	void updateInArea() {
		//カレントに基づいてINAREAを更新する]
		//INAREAに入るのは以下の条件
		//1:アクション＋ユーザのエリア内であること
		//2:アクションのターゲット候補であること（＝敵のみアクションでは味方は入らない
		//3:ユーザアクションを持っている場合、ユーザも入る
		//
		//INAREAに追加する順序はTGTTYPの初期選択より異なる。
		inArea候補者.clear();

		//行動アクションのときはINAREAなし
		if (currentBA.getType() == ActionType.行動) {
			return;
		}
		//アイテムで戦闘中使えない場合もINAREAなし
		if (currentBA.getType() == ActionType.アイテム && !currentBA.isBattle()) {
			return;
		}

		Point2D.Float center = currentUser.getSprite().getCenter();
		int area = currentUser.getStatus().getEffectedArea(currentBA);
		if (area == 0) {
			return;
		}
		boolean isPC = currentUser.isPlayer();

		//INAREA 更新
		switch (currentBA.getTgtType()) {
			case グループ_切替可能_初期選択味方:
			case グループ_切替可能_初期選択敵: {
				if (isPC) {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allPartyOf(center, area));
							inArea候補者.add(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
						case 未使用:
							break;
					}
				} else {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
							inArea候補者.add(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allPartyOf(center, area));
						case 未使用:
							break;
					}
				}
				break;
			}
			case グループ_切替可能_初期選択味方_自身除く:
			case グループ_切替可能_初期選択敵_自身除く: {
				if (isPC) {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allPartyOf(center, area));
							inArea候補者.remove(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
							inArea候補者.remove(currentUser);
						case 未使用:
							break;
					}
				} else {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
							inArea候補者.remove(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allPartyOf(center, area));
							inArea候補者.remove(currentUser);
						case 未使用:
							break;
					}
				}
				inArea候補者.remove(currentUser);
				break;
			}
			case グループ_味方全員_自身除く: {
				if (isPC) {
					inArea候補者.addAll(allPartyOf(center, area));
					inArea候補者.remove(currentUser);
				} else {
					inArea候補者.addAll(allEnemyOf(center, area));
					inArea候補者.remove(currentUser);
				}
				break;
			}
			case グループ_味方全員: {
				inArea候補者.add(currentUser);
				if (isPC) {
					inArea候補者.addAll(allPartyOf(center, area));
				} else {
					inArea候補者.addAll(allEnemyOf(center, area));
				}
				break;
			}
			case グループ_敵全員: {
				if (isPC) {
					inArea候補者.addAll(allEnemyOf(center, area));
				} else {
					inArea候補者.addAll(allPartyOf(center, area));
				}
				break;
			}
			case 全員: {
				inArea候補者.addAll(allPartyOf(center, area));
				inArea候補者.addAll(allEnemyOf(center, area));
				inArea候補者.add(currentUser);
				break;
			}
			case 全員_自身除く: {
				inArea候補者.addAll(allPartyOf(center, area));
				inArea候補者.addAll(allEnemyOf(center, area));
				inArea候補者.remove(currentUser);
				break;
			}
			case 単体_切替可能_自身含まない_初期選択敵:
			case 単体_切替可能_自身含まない_初期選択味方: {
				if (isPC) {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allPartyOf(center, area));
							inArea候補者.remove(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
							inArea候補者.remove(currentUser);
						case 未使用:
							break;
					}
				} else {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
							inArea候補者.remove(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allPartyOf(center, area));
							inArea候補者.remove(currentUser);
						case 未使用:
							break;
					}
				}
				inArea候補者.remove(currentUser);
				break;
			}
			case 単体_切替可能_自身含む_初期選択味方:
			case 単体_切替可能_自身含む_初期選択敵: {
				if (isPC) {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allPartyOf(center, area));
							inArea候補者.add(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
						case 未使用:
							break;
					}
				} else {
					switch (teamSelect) {
						case 味方選択中:
							inArea候補者.addAll(allEnemyOf(center, area));
							inArea候補者.add(currentUser);
						case 敵選択中:
							inArea候補者.addAll(allPartyOf(center, area));
						case 未使用:
							break;
					}
				}
				break;
			}
			case 単体_味方のみ_自身含まない: {
				if (isPC) {
					inArea候補者.addAll(allPartyOf(center, area));
				} else {
					inArea候補者.addAll(allEnemyOf(center, area));
				}
				inArea候補者.remove(currentUser);
				break;
			}
			case 単体_味方のみ_自身含む: {
				if (isPC) {
					inArea候補者.addAll(allPartyOf(center, area));
				} else {
					inArea候補者.addAll(allEnemyOf(center, area));
				}
				inArea候補者.add(currentUser);
				break;
			}
			case 単体_敵のみ: {
				if (isPC) {
					inArea候補者.addAll(allEnemyOf(center, area));
				} else {
					inArea候補者.addAll(allPartyOf(center, area));
				}
				break;
			}
			case 自身のみ: {
				inArea候補者.add(currentUser);
				break;
			}
			default:
				throw new AssertionError("undefined tgt mode : " + this);
		}
		inArea候補者 = inArea候補者.stream().distinct().toList();

		List<Actor> remove = new ArrayList<>();
		//死亡者ターゲティング可否の判定
		assert currentBA.getDeadTgt() != null : "currentBA s dead tgt is null (TS) : " + this;
		switch (currentBA.getDeadTgt()) {
			case 気絶損壊解脱者は選択不可能: {
				remove.addAll(inArea候補者.stream()
						.filter(p -> p.getStatus().hasCondition(ConditionKey.解脱)
						|| p.getStatus().hasCondition(ConditionKey.損壊)
						|| p.getStatus().hasCondition(ConditionKey.気絶)
						).toList());
				break;
			}
			case 損壊者を選択可能:
				remove.addAll(inArea候補者.stream()
						.filter(p -> p.getStatus().hasCondition(ConditionKey.解脱)
						|| p.getStatus().hasCondition(ConditionKey.気絶)
						).toList());
				break;
			case 気絶者を選択可能:
				remove.addAll(inArea候補者.stream()
						.filter(p -> p.getStatus().hasCondition(ConditionKey.解脱)
						|| p.getStatus().hasCondition(ConditionKey.損壊)
						).toList());
				break;
			case 解脱者を選択可能:
				remove.addAll(inArea候補者.stream()
						.filter(p -> p.getStatus().hasCondition(ConditionKey.損壊)
						|| p.getStatus().hasCondition(ConditionKey.気絶)
						).toList());
				break;
			case 気絶損壊解脱者を選択可能:
		}
		inArea候補者 = new ArrayList<>(inArea候補者);
		inArea候補者.removeAll(remove);

		if (GameSystem.isDebugMode()) {
			GameLog.print("TS inArea : " + inArea候補者);
		}
	}

	@LoopCall
	@Override
	public void draw(GraphicsContext g) {
		pcCenterArea.draw(g);
		initialCenterArea.draw(g);
		icons.forEach(p -> p.draw(g));
	}

	@Override
	public String toString() {
		return "BattleTargetSystem{" + "currentUser=" + currentUser + ", currentBA=" + currentBA + ", pcCenterArea=" + pcCenterArea + ", initialCenterArea=" + initialCenterArea + ", iconBlinkTC=" + iconBlinkTC + ", iconMaster=" + iconMaster + ", icons=" + icons + ", selectedIdx=" + selectedIdx + ", teamSelect=" + teamSelect + ", inArea\u5019\u88dc\u8005=" + inArea候補者 + '}';
	}

}
