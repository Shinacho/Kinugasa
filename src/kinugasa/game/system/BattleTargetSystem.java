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
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GraphicsContext;
import kinugasa.game.LoopCall;
import kinugasa.game.OneceTime;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.object.Drawable;
import kinugasa.object.Sprite;
import kinugasa.util.FrameTimeCounter;
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

	private BattleCharacter currentUser;
	private CmdAction currentBA;
	private boolean selfTarget = false;
	//
	private BattleActionAreaSprite currentArea;
	private BattleActionAreaSprite initialArea;
	private Color currentAreaColor = Color.GREEN;
	private Color initialAreaColor = Color.BLUE;
	//キャッシュ
	private List<BattleCharacter> inArea = new ArrayList<>();
	private List<BattleCharacter> selected = new ArrayList<>();
	private boolean fieldSelect = false;
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

	@OneceTime
	void init() {
		iconBlinkTC = new FrameTimeCounter(blinkTime);
		//アイコンマスタをみえない位置に配置
		iconMaster = new TextLabelSprite("↓", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontStyle(Font.BOLD)), -123, -123, 12, 12);
		selectedIdx = 0;
		currentArea = new BattleActionAreaSprite(currentAreaColor);
		currentArea.setVisible(false);
		initialArea = new BattleActionAreaSprite(initialAreaColor);
		initialArea.setVisible(false);
		selected.clear();
		inArea.clear();
		icons.clear();
		selfTarget = false;
	}

	//
	//-------------------------------static-------------------------------------
	//
	//eから最も近いPCを返す。
	static BattleCharacter nearPCs(BattleCharacter e) {
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

	//カレントを設定せずに、ターゲットを分析する。
	//空のターゲットインスタンスを返す場合がある。
	static ActionTarget instantTarget(BattleCharacter user, CmdAction a) {
		Point2D.Float center = user.getSprite().getCenter();
		int area = a.getAreaWithEqip(user.getStatus());
		ActionTarget result = new ActionTarget(user, a);

		List<BattleCharacter> tgt = new ArrayList<>();
		for (ActionEvent e : a.getBattleEvent()) {
			switch (e.getTargetType()) {
				case FIELD:
					result.setFieldTarget(true);
					break;
				case SELF:
					//SELFの場合はフラグをONにする
					result.setSelfTarget(true);
					break;
				case ALL:
					tgt.addAll(getInstance().all(center, area));
					break;
				case RANDOM_ONE:
					List<BattleCharacter> l1 = getInstance().all(center, area);
					Collections.shuffle(l1);
					if (!l1.isEmpty()) {
						tgt.add(l1.get(0));
					}
				case TEAM_ENEMY:
					if (user.isPlayer()) {
						tgt.addAll(getInstance().allEnemies(center, area));
					} else {
						tgt.addAll(getInstance().allPCs(center, area));
					}
					break;
				case TEAM_PARTY:
					if (user.isPlayer()) {
						tgt.addAll(getInstance().allPCs(center, area));
					} else {
						tgt.addAll(getInstance().allEnemies(center, area));
					}
					break;
				case ONE_ENEMY:
					if (user.isPlayer()) {
						List<BattleCharacter> l = getInstance().allEnemies(center, area);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					} else {
						List<BattleCharacter> l = getInstance().allPCs(center, area);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					}
					break;
				case ONE_PARTY:
					if (user.isPlayer()) {
						List<BattleCharacter> l = getInstance().allPCs(center, area);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					} else {
						List<BattleCharacter> l = getInstance().allEnemies(center, area);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					}
					break;
				case RANDOM_ONE_ENEMY:
					if (user.isPlayer()) {
						List<BattleCharacter> l = getInstance().allEnemies(center, area);
						Collections.shuffle(l);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					} else {
						List<BattleCharacter> l = getInstance().allPCs(center, area);
						Collections.shuffle(l);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					}
					break;
				case RANDOM_ONE_PARTY:
					if (user.isPlayer()) {
						List<BattleCharacter> l = getInstance().allPCs(center, area);
						Collections.shuffle(l);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					} else {
						List<BattleCharacter> l = getInstance().allEnemies(center, area);
						Collections.shuffle(l);
						if (!l.isEmpty()) {
							tgt.add(l.get(0));
						}
					}
					break;
				default:
					throw new AssertionError("undefined targetType " + a);
			}
		}
		tgt = tgt.stream().distinct().collect(Collectors.toList());
		result.setTarget(tgt);

		if (GameSystem.isDebugMode()) {
			System.out.println("TS instantTarget : " + result);
		}

		return result;
	}

	//
	//-------------------------------non-static---------------------------------
	//
	public void next() {
		//inAreaを取ったときの順番は同一になるのでそれを利用する。inAreaはLoopCall
		selectedIdx++;
		if (selectedIdx >= inArea.size()) {
			selectedIdx = 0;
		}
		updateSelected();
		updateIcon();
	}

	public void prev() {
		//inAreaを取ったときの順番は同一になるのでそれを利用する。inAreaはLoopCall
		selectedIdx--;
		if (selectedIdx < 0) {
			selectedIdx = inArea.size() - 1;
		}
		updateSelected();
		updateIcon();
	}

	public ActionTarget getSelected() {
		return new ActionTarget(currentUser, currentBA)
				.setFieldTarget(fieldSelect)
				.setInField(false)
				.setTarget(selected)
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
		initialArea.draw(g);
		icons.forEach(p -> p.draw(g));
	}

	//
	//--------------------------pp----------------------------------------------
	//
	boolean isEmpty() {
		return selectedIdx < 0;
	}

	void setCurrent(CommandWindow w) {
		selectedIdx = 0;
		currentBA = w.getSelected();
		selfTarget = currentBA.getBattleEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.SELF);
		//カレントエリアの更新
		//アイテムの場合はMOV/2
		//防御、回避、状態はエリア表示しない
		int area = 0;
		if (currentBA.getName().equals(BattleConfig.ActionName.avoidance)
				|| currentBA.getName().equals(BattleConfig.ActionName.defence)
				|| currentBA.getName().equals(BattleConfig.ActionName.status)) {
			area = 0;
		} else if (currentBA.getType() == ActionType.ITEM_USE) {
			area = (int) (currentUser.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
		} else {
			area = currentBA.getAreaWithEqip(currentUser.getStatus());
		}
		currentArea.setArea(area);
		currentArea.setLocationByCenter(currentUser.getSprite().getCenter());
		currentArea.setVisible(true);
		updateInArea();
		updateSelected();
		updateIcon();
	}

	void setCurrent(BattleCharacter pc, CmdAction a) {
		selectedIdx = 0;
		currentUser = pc;
		currentBA = a;
		selfTarget = a.getBattleEvent().stream().anyMatch(p -> p.getTargetType() == TargetType.SELF);

		if (a.getName().equals(BattleConfig.ActionName.move)) {
			//カレントエリアの更新
			int area = (int) (pc.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue());
			currentArea.setArea(area);
			currentArea.setLocationByCenter(pc.getSprite().getCenter());
			currentArea.setVisible(true);

			//初期エリアの更新
			initialArea.setArea(area);
			initialArea.setLocationByCenter(pc.getSprite().getCenter());
			initialArea.setVisible(true);
		} else {
			//カレントエリアの更新
			int area = 0;
			if (currentBA.getName().equals(BattleConfig.ActionName.avoidance)
					|| currentBA.getName().equals(BattleConfig.ActionName.defence)
					|| currentBA.getName().equals(BattleConfig.ActionName.status)) {
				area = 0;
			} else if (currentBA.getType() == ActionType.ITEM_USE) {
				area = (int) (currentUser.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
			} else {
				area = currentBA.getAreaWithEqip(currentUser.getStatus());
			}
			currentArea.setArea(area);
			currentArea.setLocationByCenter(pc.getSprite().getCenter());
			currentArea.setVisible(true);

			//初期エリアの更新
			initialArea.setArea(area);
			initialArea.setLocationByCenter(pc.getSprite().getCenter());
			initialArea.setVisible(true);
		}
		updateInArea();
		updateSelected();
		updateIcon();
	}

	void unsetCurrent() {
		fieldSelect = false;
		selectedIdx = -1;
		selected.clear();
		selfTarget = false;
		inArea.clear();
		currentArea.setVisible(false);
		initialArea.setVisible(false);
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
		updateArea();
	}

	//
	//---------------------------------private----------------------------------
	//
	private void updateArea() {
		//カレントに基づいてエリアを更新する
		int area = 0;
		if (currentBA.getName().equals(BattleConfig.ActionName.avoidance)
				|| currentBA.getName().equals(BattleConfig.ActionName.defence)
				|| currentBA.getName().equals(BattleConfig.ActionName.status)) {
			area = 0;
		} else if (currentBA.getType() == ActionType.ITEM_USE) {
			area = (int) (currentUser.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.move).getValue() / 2);
		} else {
			area = currentBA.getAreaWithEqip(currentUser.getStatus());
		}
		currentArea.setArea(area);
		currentArea.setLocationByCenter(currentUser.getSprite().getCenter());
	}

	//INAREAには正しい対象が入っている。
	private void updateSelected() {
		//カレントに基づいてSELECTEDを更新する、先にINAREAを更新しておくこと		
		selected.clear();
		if (currentBA.getType() == ActionType.ITEM_USE || currentBA.getType() == ActionType.OTHER) {
			return;
		}
		//SELFの場合SELECTED必要なし
		if (currentBA.hasBattleTT(TargetType.SELF)) {
			selfTarget = true;
		}
		if (currentBA.battleEventIsOnly(TargetType.SELF)) {
			return;
		}
		//アンセットされている場合、selectedを空にしただけで戻る
		if (selectedIdx < 0) {
			return;
		}

		//TargetTypeがONEの場合だけ、SELECTEDは更新される。
		if (currentBA.hasBattleTT(TargetType.ONE_ENEMY) || currentBA.hasBattleTT(TargetType.ONE_PARTY)
				|| currentBA.hasBattleTT(TargetType.RANDOM_ONE_ENEMY) || currentBA.hasBattleTT(TargetType.RANDOM_ONE_PARTY)) {

			//INAREAがない場合、ターゲットもないので戻る
			if (inArea.isEmpty()) {
				return;
			}
			assert selectedIdx >= 0 && selectedIdx < inArea.size() : "BTS : selectedIDX is missmatch : " + selected.size() + " / " + inArea.size() + " / " + selectedIdx;
			//inAREAからTTとSELECTEDIDXに基づく対象をSELECTEDに追加
			//inAREAはただしく更新されているので、SELECTEDIDXだけ見ればよい
			selected.add(inArea.get(selectedIdx));

			selected = selected.stream().distinct().collect(Collectors.toList());
		}

	}

	private void updateInArea() {
		//カレントに基づいてINAREAを更新する
		inArea.clear();

		if (currentBA.getType() == ActionType.ITEM_USE || currentBA.getType() == ActionType.OTHER) {
			return;
		}

		List<BattleCharacter> list = new ArrayList<>();
		Point2D.Float center = currentUser.getSprite().getCenter();
		int area = currentBA.getAreaWithEqip(currentUser.getStatus());
		for (ActionEvent e : currentBA.getBattleEvent()) {
			switch (e.getTargetType()) {
				case ALL:
					inArea.addAll(all(center, area));
					break;
				case SELF:
				case FIELD:
					//INAREAなし
					break;
				case ONE_ENEMY:
					if (currentUser.isPlayer()) {
						List<BattleCharacter> l = allEnemies(center, area);
						if (!l.isEmpty()) {
							inArea.add(l.get(0));
						}
					} else {
						List<BattleCharacter> l = allPCs(center, area);
						if (!l.isEmpty()) {
							inArea.add(l.get(0));
						}
					}
					break;
				case ONE_PARTY:
					if (currentUser.isPlayer()) {
						List<BattleCharacter> l = allPCs(center, area);
						if (!l.isEmpty()) {
							inArea.add(l.get(0));
						}
					} else {
						List<BattleCharacter> l = allEnemies(center, area);
						if (!l.isEmpty()) {
							inArea.add(l.get(0));
						}
					}
					break;
				case RANDOM_ONE:
					List<BattleCharacter> l = all(center, area);
					Collections.shuffle(l);
					if (!l.isEmpty()) {
						inArea.add(l.get(0));
					}
				case RANDOM_ONE_ENEMY:
					if (currentUser.isPlayer()) {
						List<BattleCharacter> l2 = allEnemies(center, area);
						Collections.shuffle(l2);
						if (!l2.isEmpty()) {
							inArea.add(l2.get(0));
						}
					} else {
						List<BattleCharacter> l2 = allPCs(center, area);
						Collections.shuffle(l2);
						if (!l2.isEmpty()) {
							inArea.add(l2.get(0));
						}
					}
					break;
				case RANDOM_ONE_PARTY:
					if (currentUser.isPlayer()) {
						List<BattleCharacter> l2 = allPCs(center, area);
						Collections.shuffle(l2);
						if (!l2.isEmpty()) {
							inArea.add(l2.get(0));
						}
					} else {
						List<BattleCharacter> l2 = allEnemies(center, area);
						Collections.shuffle(l2);
						if (!l2.isEmpty()) {
							inArea.add(l2.get(0));
						}
					}
					break;
				case TEAM_ENEMY:
					if (currentUser.isPlayer()) {
						inArea.addAll(allEnemies(center, area));
					} else {
						inArea.addAll(allPCs(center, area));
					}
					break;
				case TEAM_PARTY:
					if (currentUser.isPlayer()) {
						inArea.addAll(allPCs(center, area));
					} else {
						inArea.addAll(allEnemies(center, area));
					}
					break;
				default:
					throw new AssertionError("undefined target type " + e);
			}
		}
		inArea = inArea.stream().distinct().collect(Collectors.toList());

	}

	private void updateIcon() {
		//カレントに基づいてアイコンを設定する
		icons.clear();
		if (fieldSelect) {
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(BattleFieldSystem.getInstance().getBattleFieldAllArea().getCenter());
			icons.add(i);
			return;
		}
		if (selected == null || selected.isEmpty()) {
			return;
		}
		for (BattleCharacter c : selected) {
			float x = c.getSprite().getCenterX();
			float y = c.getSprite().getCenterY() - iconMaster.getHeight() - 4;
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(new Point2D.Float(x, y));
			icons.add(i);
		}
		if (selfTarget) {
			Sprite i = iconMaster.clone();
			float x = currentUser.getSprite().getCenterX();
			float y = currentUser.getSprite().getCenterY() - iconMaster.getHeight() - 4;
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

	BattleActionAreaSprite getInitialArea() {
		return initialArea;
	}

}
