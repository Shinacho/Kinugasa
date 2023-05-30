package kinugasa.game.system;

/*
 * The MIT License
 *
 * Copyright 2022 Shinacho.
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
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.game.GameLog;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LoopCall;
import kinugasa.game.OneceTime;
import static kinugasa.game.system.TargetOption.DefaultTarget.ENEMY;
import static kinugasa.game.system.TargetOption.SelectType.IN_AREA;
import static kinugasa.game.system.TargetOption.SelectType.ONE;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.object.Drawable;
import kinugasa.object.Sprite;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;
import static kinugasa.game.system.TargetOption.DefaultTarget.PARTY;

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

	private BattleCharacter currentUser;
	private Action currentBA;
	private boolean selfTarget = false;
	//
	private BattleActionAreaSprite currentArea;
	private Color currentAreaColor = Color.BLUE;
	//キャッシュ
	private List<BattleCharacter> inArea = new ArrayList<>();
	private List<BattleCharacter> inAreaTeam = new ArrayList<>();
	private List<BattleCharacter> inAreaEnemy = new ArrayList<>();
	private List<BattleCharacter> selected = new ArrayList<>();
	//
	//選択中ターゲットの選択アイコン点滅時間
	private int blinkTime = 8;
	private FrameTimeCounter iconBlinkTC = new FrameTimeCounter(blinkTime);
	//選択中アイコンのマスタ
	private Sprite iconMaster;
	//選択中アイコンの実態
	private List<Sprite> icons = new ArrayList<>();
	//
	private int selectedIdx;
	//スイッチチーム状況
	private TargetOption.DefaultTarget selectedTeam;

	@OneceTime
	void init() {
		iconBlinkTC = new FrameTimeCounter(blinkTime);
		//アイコンマスタをみえない位置に配置
		iconMaster = new TextLabelSprite("↓", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontStyle(Font.BOLD)), -123, -123, 12, 12);
		selectedIdx = 0;
		currentArea = new BattleActionAreaSprite(currentAreaColor);
		currentArea.setVisible(false);
		selected.clear();
		inArea.clear();
		icons.clear();
		selfTarget = false;
	}

	//
	//-------------------------------static-------------------------------------
	//
	//eから最も近いPCを返す。
	static BattleCharacter nearPC(BattleCharacter e) {
		float distance = Integer.MAX_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : getInstance().allPCs(e.getSprite().getCenter(), Integer.MAX_VALUE)) {
			if (e.getSprite().getCenter().distance(c.getSprite().getCenter()) < distance) {
				distance = (float) e.getSprite().getCenter().distance(c.getSprite().getCenter());
				result = c;
			}
		}
		return result;
	}

	static BattleCharacter nearEnemy(BattleCharacter pc) {
		float distance = Integer.MAX_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : getInstance().allEnemies(pc.getSprite().getCenter(), Integer.MAX_VALUE)) {
			if (pc.getSprite().getCenter().distance(c.getSprite().getCenter()) < distance) {
				distance = (float) pc.getSprite().getCenter().distance(c.getSprite().getCenter());
				result = c;
			}
		}
		return result;
	}

	public BattleCharacter farPC(BattleCharacter e) {
		float distance = Integer.MIN_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : getInstance().allPCs(e.getSprite().getCenter(), Integer.MAX_VALUE)) {
			if (e.getSprite().getCenter().distance(c.getSprite().getCenter()) > distance) {
				distance = (float) e.getSprite().getCenter().distance(c.getSprite().getCenter());
				result = c;
			}
		}
		return result;

	}

	public BattleCharacter farEnemy(BattleCharacter pc) {
		float distance = Integer.MIN_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : getInstance().allEnemies(pc.getSprite().getCenter(), Integer.MAX_VALUE)) {
			if (pc.getSprite().getCenter().distance(c.getSprite().getCenter()) > distance) {
				distance = (float) pc.getSprite().getCenter().distance(c.getSprite().getCenter());
				result = c;
			}
		}
		return result;
	}

	//カレントを設定せずに、ターゲットを分析する。
	//空のターゲットインスタンスを返す場合がある。
	static ActionTarget instantTarget(BattleCharacter user, Action a) {
//		if (GameSystem.isDebugMode()) {
//			GameLog.print("TS intant Target start : " + a);
//		}
		Point2D.Float center = user.getSprite().getCenter();
		int area = a instanceof Item
				? (int) user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2
				: a.getAreaWithEqip(user.getStatus());
		ActionTarget result = new ActionTarget(user, a);

		List<BattleCharacter> tgt = new ArrayList<>();
		boolean isPC = GameSystem.getInstance().getPartyStatus().contains(user.getStatus());
		TargetOption o = a.getTargetOption();
		//セルフターゲット要否判定
		if (o.getSelfTarget() == TargetOption.SelfTarget.YES) {
			result.setSelfTarget(true);
		}
		//INAREAかつIFFOFFの場合全キャラを返す
		if (o.getSelectType() == TargetOption.SelectType.IN_AREA && o.getIff() == TargetOption.IFF.OFF) {
			tgt.addAll(getInstance().all(user.getCenter(), area));
		} else {
			if (!isPC) {
				switch (o.getDefaultTarget()) {
					case ENEMY:
						tgt.addAll(getInstance().allPCs(user.getCenter(), area));
						break;
					case PARTY:
						tgt.addAll(getInstance().allEnemies(user.getCenter(), area));
						break;
					default:
						throw new AssertionError();
				}
				switch (o.getSelectType()) {
					case IN_AREA:
						//処理なし
						break;
					case ONE:
						if (tgt.size() > 1) {
							tgt.subList(0, 1);
						}
						break;
					default:
						throw new AssertionError();
				}

			}
			if (isPC) {
				switch (o.getDefaultTarget()) {
					case ENEMY:
						tgt.addAll(getInstance().allEnemies(user.getCenter(), area));
						break;
					case PARTY:
						tgt.addAll(getInstance().allPCs(user.getCenter(), area));
						break;
					default:
						throw new AssertionError();
				}
				switch (o.getSelectType()) {
					case IN_AREA:
						//処理なし
						break;
					case ONE:
						if (tgt.size() > 1) {
							tgt.subList(0, 1);
						}
						break;
					default:
						throw new AssertionError();
				}
			}
		}

		tgt = tgt.stream().distinct().collect(Collectors.toList());
		//アンターゲット状態のキャラを除去
		List<BattleCharacter> removeList = new ArrayList<>();
		for (BattleCharacter c : tgt) {
			if (c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
				removeList.add(c);
			}
		}
		tgt.removeAll(removeList);
		result.setTarget(tgt);

//		if (GameSystem.isDebugMode()) {
//			kinugasa.game.GameLog.print("TS instantTarget " + result);
//		}
		return result;
	}
	//
	//-------------------------------non-static---------------------------------
	//

	public TargetOption.DefaultTarget switchTeam() {
		if (currentBA.getTargetOption().getSwitchTeam() == TargetOption.SwitchTeam.NG) {
			throw new GameSystemException("this action cant switch team : " + currentBA);
		}
		selectedTeam = selectedTeam == ENEMY ? PARTY : ENEMY;
		updateSelected();
		updateIcon();
		return selectedTeam;
	}

	public void next() {
		//inAreaを取ったときの順番は同一になるのでそれを利用する。inAreaはLoopCall
		int maxSize = selectedTeam == ENEMY
				? (int) inArea.stream().filter(p -> !p.isPlayer()).count()
				: (int) inArea.stream().filter(p -> p.isPlayer()).count();
		selectedIdx++;
		if (selectedIdx >= maxSize) {
			selectedIdx = 0;
		}
		updateSelected();
		updateIcon();
	}

	public void prev() {
		//inAreaを取ったときの順番は同一になるのでそれを利用する。inAreaはLoopCall
		int maxSize = selectedTeam == ENEMY
				? (int) inArea.stream().filter(p -> !p.isPlayer()).count()
				: (int) inArea.stream().filter(p -> p.isPlayer()).count();
		selectedIdx--;
		if (selectedIdx < 0) {
			selectedIdx = maxSize - 1;
		}
		updateSelected();
		updateIcon();
	}

	public ActionTarget getSelected() {
		return new ActionTarget(currentUser, currentBA)
				.setInField(false)
				.setTarget(selected)
				.setSelfTarget(selfTarget);
	}

	//これいる？
	public ActionTarget getSelectedInArea() {
		return new ActionTarget(currentUser, currentBA)
				.setInField(false)
				.setTarget(inArea)
				.setSelfTarget(selfTarget);
	}

	public void randomSelect() {
		selectedIdx = Random.randomAbsInt(inArea.size());
		updateSelected();
		updateIcon();
	}

	@LoopCall
	@Override
	public void draw(GraphicsContext g) {
		currentArea.draw(g);
		icons.forEach(p -> p.draw(g));
	}

	//
	//--------------------------pp----------------------------------------------
	//
	boolean isEmpty() {
		return selectedIdx < 0;
	}

	void setCurrent(CommandWindow w) {
		//CMDWindowから遷移したIFF=OFF
		if (currentUser == null) {
			throw new GameSystemException("TS setCurrent(Window), but user is null");
		}
		if (w.getSelectedCmd() == null) {
			//魔法やアイテムがない場合。
			//エリア初期化のみ
			currentArea.setArea(0);
			currentArea.setVisible(false);
			return;
		}
		selectedIdx = 0;
		currentBA = w.getSelectedCmd();
		selfTarget = currentBA.getTargetOption() == null ? false : currentBA.getTargetOption().getSelfTarget() == TargetOption.SelfTarget.YES;
		selectedTeam = currentBA.getTargetOption() == null ? null : currentBA.getTargetOption().getDefaultTarget();
		//カレントエリアの更新
		//アイテムの場合はMOV/2
		//防御、回避、状態はエリア表示しない
		int area = 0;
		if (currentBA.getName().equals(BattleConfig.ActionName.avoidance)
				|| currentBA.getName().equals(BattleConfig.ActionName.defence)
				|| currentBA.getName().equals(BattleConfig.ActionName.status)
				|| currentBA.getName().equals(BattleConfig.ActionName.commit)) {
			area = 0;
		} else if (currentBA.getType() == ActionType.ITEM) {
			area = (int) (currentUser.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
		} else if (currentBA.getName().equals(BattleConfig.ActionName.move)
				|| currentBA.getName().equals(BattleConfig.ActionName.escape)) {
			area = (int) currentUser.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue();
		} else {
			area = currentBA.getAreaWithEqip(currentUser.getStatus());
		}
		currentArea.setArea(area);
		currentArea.setLocationByCenter((Point2D.Float) currentUser.getSprite().getCenter().clone());
		currentArea.setVisible(true);
		updateInArea();
		updateSelected();
		updateIcon();
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("TS current:" + currentBA);
		}
	}

	List<BattleCharacter> getInAreaDirect() {
		return inArea;
	}

	List<BattleCharacter> getInAreaEnemy() {
		return inAreaEnemy;
	}

	List<BattleCharacter> getInAreaTeam() {
		return inAreaTeam;
	}

	void setCurrent(BattleCharacter pc, Action a) {
		selectedIdx = 0;
		currentUser = pc;
		currentBA = a;
		selfTarget = a.getBattleEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.SELF);
		selectedTeam = a.getTargetOption() == null ? null : a.getTargetOption().getDefaultTarget();

		if (a.getName().equals(BattleConfig.ActionName.move)) {
			//カレントエリアの更新
			int area = (int) (pc.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue());
			currentArea.setArea(area);
			currentArea.setLocationByCenter((Point2D.Float) pc.getSprite().getCenter().clone());
			currentArea.setVisible(true);
		} else {
			//カレントエリアの更新
			int area = 0;
			if (currentBA.getName().equals(BattleConfig.ActionName.avoidance)
					|| currentBA.getName().equals(BattleConfig.ActionName.defence)
					|| currentBA.getName().equals(BattleConfig.ActionName.status)) {
				area = 0;
			} else if (currentBA.getType() == ActionType.ITEM) {
				area = (int) (currentUser.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
			} else {
				area = currentBA.getAreaWithEqip(currentUser.getStatus());
			}
			currentArea.setArea(area);
			currentArea.setLocationByCenter((Point2D.Float) pc.getSprite().getCenter().clone());
			currentArea.setVisible(true);
		}
		//チーム選択の場合、初期状態を設定
		updateInArea();
		updateSelected();
		updateIcon();
		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("TS : " + this);
		}
	}

	void unsetCurrent() {
		selectedIdx = -1;
		selected.clear();
		selfTarget = false;
		inArea.clear();
		currentArea.setVisible(false);
		icons.clear();
		selectedTeam = ENEMY;
	}

	@LoopCall
	void update() {
		//点滅を制御
		if (iconBlinkTC.isReaching()) {
			iconBlinkTC = new FrameTimeCounter(blinkTime);
			icons.forEach(v -> v.switchVisible());
		}
		//カレントキャラの移動に合わせてinArea更新
		if (currentUser == null) {
			return;
		}
		updateInArea();
		updateSelected();
	}

	//
	//---------------------------------private----------------------------------
	//
	private void updateInArea() {
		//カレントに基づいてINAREAを更新する
		inArea.clear();
		inAreaTeam.clear();;
		inAreaEnemy.clear();

		if (currentBA.getType() == ActionType.OTHER) {
			return;
		}
		if (currentBA.getType() == ActionType.ITEM && !currentBA.isBattleUse()) {
			return;
		}

		List<BattleCharacter> list = new ArrayList<>();
		Point2D.Float center = currentUser.getSprite().getCenter();
		int area = currentBA.getAreaWithEqip(currentUser.getStatus());
		boolean isPC = currentUser.isPlayer();
		TargetOption o = currentBA.getTargetOption();
		//セルフターゲット要否判定
		if (o.getSelfTarget() == TargetOption.SelfTarget.YES) {
			selfTarget = true;
		} else {
			selfTarget = false;
		}
		inArea.addAll(getInstance().all(currentUser.getCenter(), area));

		//アンターゲットの人を削除
		List<BattleCharacter> removeList = new ArrayList<>();
		for (BattleCharacter c : inArea) {
			if (c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
				removeList.add(c);
			}
		}
		inArea.removeAll(removeList);
		inArea = inArea.stream().distinct().collect(Collectors.toList());

		//IFF_ONの場合、INAREAから選択しているチーム以外を削除
		removeList.clear();
		if (currentBA.getTargetOption().getIff() == TargetOption.IFF.ON) {
			if (selectedTeam == ENEMY) {
				if (currentUser.isPlayer()) {
					for (BattleCharacter c : inArea) {
						if (c.isPlayer()) {
							removeList.add(c);
						}
					}
				} else {
					for (BattleCharacter c : inArea) {
						if (!c.isPlayer()) {
							removeList.add(c);
						}
					}
				}
			} else {
				if (currentUser.isPlayer()) {
					for (BattleCharacter c : inArea) {
						if (!c.isPlayer()) {
							removeList.add(c);
						}
					}
				} else {
					for (BattleCharacter c : inArea) {
						if (c.isPlayer()) {
							removeList.add(c);
						}
					}
				}
			}
		}
		inArea.removeAll(removeList);
		inArea = inArea.stream().distinct().collect(Collectors.toList());
		//振り分け実施
		for (BattleCharacter c : inArea) {
			if (c.isPlayer()) {
				inAreaTeam.add(c);
			} else {
				inAreaEnemy.add(c);
			}
		}

	}

	//INAREAには正しい対象が入っている。
	private void updateSelected() {
		//カレントに基づいてSELECTEDを更新する、先にINAREAを更新しておくこと		
		selected.clear();
		if (currentBA.getType() == ActionType.OTHER) {
			return;
		}
		//SELFの場合SELECTED必要なし
		if (currentBA.battleEventIsOnly(TargetType.SELF)) {
			return;
		}
		//アンセットされている場合、selectedを空にしただけで戻る
		if (selectedIdx < 0) {
			return;
		}

		if (inArea.isEmpty()) {
			return;
		}
		//ターゲティングできない場合ランダム対象になる
		if (currentBA.getTargetOption().getTargeting() == TargetOption.Targeting.DISABLE) {
			if (currentBA.hasBattleTT(TargetType.RANDOM)) {
				Collections.shuffle(inArea);
				selected.add(inArea.get(0));
			} else {
				if (currentBA.getTargetOption().getSelectType() == IN_AREA) {
					selected.addAll(inArea);
				} else {
					selected.add(inArea.get(0));
				}
			}
		} else {
			//ENABLE
			if (currentBA.getTargetOption().getIff() == TargetOption.IFF.ON) {
				//IFF ON
				if (currentUser.isPlayer()) {
					selected = inArea.stream().filter(p -> !p.isPlayer()).collect(Collectors.toList());
				} else {
					selected = inArea.stream().filter(p -> p.isPlayer()).collect(Collectors.toList());
				}
				//チームの場合、何もしないが、ONEの場合はIDX軒目を取る
				if (currentBA.getTargetOption().getSelectType() == ONE) {
					if (selected.size() > 1) {
						BattleCharacter c = selected.get(selectedIdx);
						selected = new ArrayList<>();
						selected.add(c);
					}
				}
			} else {
				//IFF OFF・・INAREAはそのまま
				//チームの場合、何もしないが、ONの場合はIDX軒目を取る
				if (currentBA.getTargetOption().getSelectType() == IN_AREA) {
					selected.addAll(inArea);
				} else {
					//ONE
					if (selectedTeam == ENEMY) {
						selected = inArea.stream().filter(p -> !p.isPlayer()).collect(Collectors.toList());
					} else {
						selected = inArea.stream().filter(p -> p.isPlayer()).collect(Collectors.toList());
					}
					if (selected.size() > 1) {
						BattleCharacter c = selected.get(selectedIdx);
						selected = new ArrayList<>();
						selected.add(c);
					}
				}
			}

		}
		selected = selected.stream().distinct().collect(Collectors.toList());

	}

	private void updateIcon() {
		//カレントに基づいてアイコンを設定する
		icons.clear();
		if (selected == null || selected.isEmpty()) {
			return;
		}
		for (BattleCharacter c : selected) {
			float x = c.getSprite().getCenterX();
			float y = c.getSprite().getCenterY() - iconMaster.getHeight() - 14;
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(new Point2D.Float(x, y));
			icons.add(i);
		}
		if (selfTarget) {
			Sprite i = iconMaster.clone();
			float x = currentUser.getSprite().getCenterX();
			float y = currentUser.getSprite().getCenterY() - iconMaster.getHeight() - 14;
			i.setLocationByCenter(new Point2D.Float(x, y));
			icons.add(i);
		}
	}

	private List<BattleCharacter> allEnemies(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter e : GameSystem.getInstance().getBattleSystem().getEnemies()) {
			if (e.getSprite().getCenter().distance(center) <= area) {
				result.add(e);
			}
		}
		return result;
	}

	private List<BattleCharacter> allPCs(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter pc : GameSystem.getInstance().getParty()) {
			if (pc.getSprite().getCenter().distance(center) <= area) {
				result.add(pc);
			}
		}
		return result;
	}

	private List<BattleCharacter> all(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		result.addAll(allEnemies(center, area));
		result.addAll(allPCs(center, area));
		return result;
	}

	BattleActionAreaSprite getCurrentArea() {
		return currentArea;
	}

	@Override
	public String toString() {
		return "BattleTargetSystem{" + "currentUser=" + currentUser + ", currentBA=" + currentBA + ", selfTarget=" + selfTarget + ", inArea=" + inArea + ", selected=" + selected + ", selectedIdx=" + selectedIdx + '}';
	}

	private Map<BattleCharacter, ActionTarget> saveTargets = new HashMap<>();

	public void saveTarget(BattleCharacter c) {
		saveTargets.put(c, getSelected());
	}

	public void saveTarget(BattleCharacter c, ActionTarget tgt) {
		saveTargets.put(c, tgt);
	}

	public ActionTarget getTarget(BattleCharacter c) {
		ActionTarget r = saveTargets.get(c);
		saveTargets.remove(c);
		return r;
	}

}
