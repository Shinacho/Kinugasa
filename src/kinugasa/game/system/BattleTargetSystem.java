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
package kinugasa.game.system;

import java.awt.Color;
import java.awt.Font;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import javax.swing.JColorChooser;
import javax.swing.JFileChooser;
import kinugasa.game.GraphicsContext;
import kinugasa.game.ui.FontModel;
import kinugasa.game.ui.SimpleTextLabelModel;
import kinugasa.game.ui.TextLabelSprite;
import kinugasa.object.Drawable;
import kinugasa.object.Sprite;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - 2022/11/24_22:01:37<br>
 * @author Dra211<br>
 */
public class BattleTargetSystem implements Drawable {

	private static final BattleTargetSystem INSTANCE = new BattleTargetSystem();

	private BattleTargetSystem() {

	}

	static BattleTargetSystem getInstance() {
		return INSTANCE;
	}

	void init(List<PlayerCharacter> pc, List<Enemy> enemy) {
		pcList = pc.stream().collect(Collectors.toList());
		enemyList = enemy.stream().collect(Collectors.toList());
		//
		iconBlinkTC = new FrameTimeCounter(blinkTime);
		iconMaster = new TextLabelSprite("↓", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontStyle(Font.BOLD)), -123, -123, 12, 12);
	}
	//敵味方のスプライトのリスト
	private List<BattleCharacter> pcList;
	private List<BattleCharacter> enemyList;
	//選択中対象リスト
	private List<BattleCharacter> selected = new ArrayList<>();
	private List<BattleCharacter> inArea = new ArrayList<>();
	private int selectedIdx = -1;
	//選択中BAとエリア
	private BattleAction currentBA;
	private BattleActionAreaSprite currentBAArea;
	private BattleActionAreaSprite afterMoveActionArea;
	//選択中ターゲットの選択アイコン点滅時間
	private int blinkTime = 8;
	private FrameTimeCounter iconBlinkTC = new FrameTimeCounter(blinkTime);
	//選択中アイコンのマスタ
	private Sprite iconMaster;
	//選択中アイコンの実態
	private List<Sprite> icons = new ArrayList<>();
	//
	private boolean fieldSelect = false;

	void setAfterMoveActionArea(Point2D.Float center, int area) {
		this.afterMoveActionArea.setLocationByCenter(center);
		this.afterMoveActionArea.setArea(area);
		this.afterMoveActionArea.setVisible(true);
	}

	BattleActionAreaSprite getAfterMoveActionArea() {
		return afterMoveActionArea;
	}

	//
	void unsetPCsTarget() {
		currentBA = null;
		currentBAArea = new BattleActionAreaSprite(Color.GREEN);
		currentBAArea.setVisible(false);
		afterMoveActionArea = new BattleActionAreaSprite(Color.BLUE);
		afterMoveActionArea.setVisible(false);
		iconBlinkTC = new FrameTimeCounter(blinkTime);
		icons.clear();
		selected.clear();
		inArea.clear();
		fieldSelect = false;
	}

	//このメソッドを実行すると、カレントBAとユーザに基づくSelected、InArea、Iconsが設定され、表示されるようになる
	void setPCsTarget(BattleAction ba, BattleCharacter user) {
		unsetPCsTarget();
		currentBA = ba;
		currentBAArea.setLocationByCenter(user.getSprite().getCenter());

		if (ba.getName().equals(BattleConfig.ActionName.move)) {
			currentBAArea.setArea((int) user.getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue());
		} else if (ba.getName().equals(BattleConfig.ActionName.escape)) {
			currentBAArea.setArea((int) user.getStatus().getEffectedStatus().get(BattleConfig.moveStatusKey).getValue());
		} else if (ba.getArea() <= 0) {
			currentBAArea.setArea(0);
		} else {
			currentBAArea.setArea(ba.getAreaWithEqip(user.getStatus()));
		}
		currentBAArea.setVisible(currentBAArea.getArea() != 0);
		updateSelect();
	}

	void updatePCsTarget(BattleAction ba) {
		setPCsTarget(ba, GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser());
	}

	private void updateSelect() {
		if (currentBA == null) {
			return;
		}
		//フィールドは範囲が0でも使える。
		if (currentBAArea.getArea() <= 0 && !currentBA.isOnlyBatt(BattleActionTargetType.FIELD)) {
			return;
		}
		//カレントBAに基づくカレントBAAREA内の対象をinAreaとSelectとiconsに設定
		//この時点でカレントBAに基づくBAAREAは入っている
		//ユーザーは

		//inarea更新
		BattleCharacter currentChara = GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser();
		for (BattleActionEvent e : currentBA.getEvents()) {
			switch (e.getBatt()) {
				case FIELD:
					fieldSelect = true;
					break;
				case ALL:
					inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), allList()));
					selected.addAll(inArea);
					break;
				case TEAM_ENEMY:
					//カレントキャラの所属によって対象を変える
					if (currentChara instanceof Enemy) {
						//EnemyのEnemyはPlayer
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), pcList()));
					} else {
						//PlayerのEnemyはEnemy
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), enemyList()));
					}
					selected.addAll(inArea);
					break;
				case TEAM_PARTY:
					//カレントキャラの所属によって対象を変える
					if (currentChara instanceof Enemy) {
						//EnemyのPartyはEnemy
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), enemyList()));
					} else {
						//PlayerのPartyはPlaer
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), pcList()));
					}
					selected.addAll(inArea);
					break;
				case ONE_ENEMY:
					//カレントキャラの所属によって対象を変える
					if (currentChara instanceof Enemy) {
						//EnemyのPartyはEnemy
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), pcList()));
					} else {
						//PlayerのPartyはPlaer
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), enemyList()));
					}
					if (!inArea.isEmpty()) {
						selected.add(inArea.get(0));
					}
					break;
				case ONE_PARTY:
					//カレントキャラの所属によって対象を変える
					if (currentChara instanceof Enemy) {
						//EnemyのPartyはEnemy
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), enemyList()));
					} else {
						//PlayerのPartyはPlaer
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), pcList()));
					}
					if (!inArea.isEmpty()) {
						selected.add(inArea.get(0));
					}
					break;
				case RANDOM_ONE:
					inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), allList()));
					Collections.shuffle(inArea);
					if (!inArea.isEmpty()) {
						selected.add(inArea.get(0));
					}
					break;
				case RANDOM_ONE_ENEMY:
					//カレントキャラの所属によって対象を変える
					if (currentChara instanceof Enemy) {
						//EnemyのPartyはEnemy
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), pcList()));
					} else {
						//PlayerのPartyはPlaer
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), enemyList()));
					}
					Collections.shuffle(inArea);
					if (!inArea.isEmpty()) {
						selected.add(inArea.get(0));
					}
					break;
				case RANDOM_ONE_PARTY:
					//カレントキャラの所属によって対象を変える
					if (currentChara instanceof Enemy) {
						//EnemyのPartyはEnemy
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), enemyList()));
					} else {
						//PlayerのPartyはPlaer
						inArea.addAll(inDistance(currentChara.getSprite().getCenter(), currentBAArea.getArea(), pcList()));
					}
					Collections.shuffle(inArea);
					if (!inArea.isEmpty()) {
						selected.add(inArea.get(0));
					}
					break;
				case SELF:
					inArea.add(currentChara);
					selected.addAll(inArea);
					break;
				default:
					throw new AssertionError();
			}
		}
		if (!selected.isEmpty()) {
			selectedIdx = 0;
		} else {
			selectedIdx = -1;
		}
		selected = selected.stream().distinct().collect(Collectors.toList());
		inArea = inArea.stream().distinct().collect(Collectors.toList());
		updateIcon();

	}

	private List<BattleCharacter> inDistance(Point2D.Float center, int distance, List<BattleCharacter> target) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : target) {
			if (center.distance(c.getSprite().getCenter()) <= distance) {
				result.add(c);
			}
		}
		return result;
	}

	private void updateIcon() {
		//SELECTEDの場所にアイコンを設置する
		icons.clear();
		if (fieldSelect) {
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(BattleFieldSystem.getInstance().getBattleFieldAllArea().getCenter());
			icons.add(i);
			return;
		}
		for (BattleCharacter c : selected) {
			float x = c.getSprite().getCenterX();
			float y = c.getSprite().getCenterY() - iconMaster.getHeight() - 4;
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(new Point2D.Float(x, y));
			icons.add(i);
		}
	}

	public void prev() {
		if (inArea.isEmpty()) {
			return;
		}
		BattleCharacter currentChara = GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser();
		int prev = selectedIdx;
		Object current = getSelected();
		selectedIdx--;
		if (selectedIdx < 0) {
			selectedIdx = inArea.size() - 1;
		}
		if (prev != selectedIdx) {
			selected.remove(current);
			selected.add(inArea.get(selectedIdx));
			selected = selected.stream().distinct().collect(Collectors.toList());
			updateIcon();
		}
		//行動エリアの更新
		currentBAArea.setArea(currentChara.getStatus().getBattleActionArea(currentBA));
	}

	public void next() {
		if (inArea.isEmpty()) {
			return;
		}
		BattleCharacter currentChara = GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser();
		int prev = selectedIdx;
		Object current = getSelected();
		selectedIdx++;
		if (selectedIdx >= inArea.size()) {
			selectedIdx = 0;
		}
		if (prev != selectedIdx) {
			selected.remove(current);
			selected.add(inArea.get(selectedIdx));
			selected = selected.stream().distinct().collect(Collectors.toList());
			updateIcon();
		}
		currentBAArea.setArea(currentChara.getStatus().getBattleActionArea(currentBA));
	}

	void update() {
		//点滅を制御
		if (iconBlinkTC.isReaching()) {
			iconBlinkTC = new FrameTimeCounter(blinkTime);
			icons.forEach(v -> v.switchVisible());
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		icons.forEach(v -> v.draw(g));
		currentBAArea.draw(g);
		afterMoveActionArea.draw(g);
	}

	private List<BattleCharacter> enemyList() {
		return enemyList;
	}

	private List<BattleCharacter> pcList() {
		return pcList;
	}

	private List<BattleCharacter> allList() {
		List<BattleCharacter> result = new ArrayList<>();
		result.addAll(enemyList);
		result.addAll(pcList);
		return result;
	}

	//
	//-----------------------------------------------ENEMY TARET SYSTEM
	//
	//選択されたアクションとユーザのエリアに基づき、ターゲットを取得する
	List<BattleCharacter> getNPCTarget(BattleAction ba, BattleCharacter npcUser) {
		Point2D.Float center = npcUser.getSprite().getCenter();
		int area = ba.getAreaWithEqip(npcUser.getStatus());
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleActionEvent e : ba.getEvents()) {
			BattleActionTargetType batt = e.getBatt();
			switch (batt) {
				case FIELD:
					break;
				case ALL:
					result.addAll(getAllTarget(center, area));
					break;
				case SELF:
					result.add(npcUser);
					break;
				case TEAM_ENEMY:
					//ENEMYのENEMYなのでPC
					for (BattleCharacter c : getAllTarget(center, area)) {
						if (c.isPlayer()) {
							result.add(c);
						}
					}
					break;
				case TEAM_PARTY:
					//ENEMYのPARTYなのでENEMY
					for (BattleCharacter c : getAllTarget(center, area)) {
						if (!c.isPlayer()) {
							result.add(c);
						}
					}
					break;
				case ONE_ENEMY:
				case RANDOM_ONE_ENEMY:
					//ENEMYのENEMYなのでPC
					for (BattleCharacter c : getAllTarget(center, area)) {
						if (c.isPlayer()) {
							result.add(c);
						}
					}
					if (!result.isEmpty()) {
						Collections.shuffle(result);
						BattleCharacter c1 = result.get(0);
						result.clear();
						result.add(c1);
					}
					break;
				case ONE_PARTY:
				case RANDOM_ONE_PARTY:
					//ENEMYのPARTYなのでENEMY
					for (BattleCharacter c : getAllTarget(center, area)) {
						if (!c.isPlayer()) {
							result.add(c);
						}
					}
					if (!result.isEmpty()) {
						Collections.shuffle(result);
						BattleCharacter c2 = result.get(0);
						result.clear();
						result.add(c2);
					}
					break;
				case RANDOM_ONE:
					result.addAll(getAllTarget(center, area));
					if (!result.isEmpty()) {
						Collections.shuffle(result);
						BattleCharacter c3 = result.get(0);
						result.clear();
						result.add(c3);
					}
					break;
				default:
					throw new AssertionError("undefine case : " + batt);
			}
		}
		result = result.stream().distinct().collect(Collectors.toList());
		return result;
	}

	public ActionResult collect(CommandWindow cmd) {
		return GameSystem.getInstance().getBattleSystem().execAction(cmd.getSelected(), true);
	}

	//モードにかかわらず取得する
	List<BattleCharacter> getPartyTarget(Point2D.Float center, int a) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : pcList()) {
			if (c.getSprite().getCenter().distance(center) < a) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		return result;
	}

	List<BattleCharacter> getAllTarget(Point2D.Float center, int a) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : pcList()) {
			if (c.getSprite().getCenter().distance(center) < a) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		for (BattleCharacter c : enemyList()) {
			if (c.getSprite().getCenter().distance(center) < a) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		return result;
	}

	BattleCharacter nearPlayer(Point2D.Float center) {
		float distance = Float.MAX_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : pcList()) {
			if (c.getSprite().getCenter().distance(center) < distance) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result = c;
				}
			}
		}
		assert result != null : "nearPlayer but all player is dead";
		return result;
	}

	BattleCharacter nearEnemy(Point2D.Float center) {
		float distance = Float.MAX_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : enemyList()) {
			if (c.getSprite().getCenter().distance(center) < distance) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result = c;
				}
			}
		}
		assert result != null : "nearEnemy but all player is dead";
		return result;
	}

	List<BattleCharacter> nearPlayer(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : pcList()) {
			if (c.getSprite().getCenter().distance(center) < area) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		Collections.sort(result, (BattleCharacter o1, BattleCharacter o2)
				-> center.distance(o1.getSprite().getCenter()) < center.distance(o2.getSprite().getCenter()) ? - 1 : 1);
		return result;
	}

	List<BattleCharacter> nearEnemy(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : enemyList()) {
			if (c.getSprite().getCenter().distance(center) < area) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		Collections.sort(result, (BattleCharacter o1, BattleCharacter o2)
				-> center.distance(o1.getSprite().getCenter()) < center.distance(o2.getSprite().getCenter()) ? - 1 : 1);
		return result;
	}

	//
	//-----------------------MAGIC
	//
	List<BattleCharacter> getMagicTarget(MagicSpell s) {
		Point2D.Float center = s.getUser().getSprite().getCenter();
		int area = s.getMagic().getArea();
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleActionEvent e : s.getMagic().getEvents()) {
			BattleActionTargetType batt = e.getBatt();
			switch (batt) {
				case FIELD:
					break;
				case ALL:
					result.addAll(getAllTarget(center, area));
					break;
				case SELF:
					result.add(s.getUser());
					break;
				case TEAM_ENEMY:
					for (BattleCharacter c : getAllTarget(center, area)) {
						switch (s.getMode()) {
							case CPU:
								//CPUのアクションの場合、敵はPARTY
								if (c.isPlayer()) {
									result.add(c);
								}
								break;
							case PC:
								//PCのアクションの場合、敵はENEMY
								if (!c.isPlayer()) {
									result.add(c);
								}
								break;
						}
					}
					break;
				case TEAM_PARTY:
					for (BattleCharacter c : getAllTarget(center, area)) {
						switch (s.getMode()) {
							case CPU:
								//CPUのアクションの場合、敵はPARTY
								if (!c.isPlayer()) {
									result.add(c);
								}
								break;
							case PC:
								//PCのアクションの場合、敵はENEMY
								if (c.isPlayer()) {
									result.add(c);
								}
								break;
						}
					}
					break;
				case ONE_ENEMY:
					L1:
					for (BattleCharacter c : getAllTarget(center, area)) {
						switch (s.getMode()) {
							case CPU:
								//CPUのアクションの場合、敵はPARTY
								if (c.isPlayer()) {
									result.add(c);
									break L1;
								}
								break;
							case PC:
								//PCのアクションの場合、敵はENEMY
								if (!c.isPlayer()) {
									result.add(c);
									break L1;
								}
								break;
						}
					}
					break;
				case ONE_PARTY:
					L2:
					for (BattleCharacter c : getAllTarget(center, area)) {
						switch (s.getMode()) {
							case CPU:
								//CPUのアクションの場合、敵はPARTY
								if (!c.isPlayer()) {
									result.add(c);
									break L2;
								}
								break;
							case PC:
								//PCのアクションの場合、敵はENEMY
								if (c.isPlayer()) {
									result.add(c);
									break L2;
								}
								break;
						}
					}
					break;
				case RANDOM_ONE:
					List<BattleCharacter> list1 = getAllTarget(center, area);
					Collections.shuffle(list1);
					for (BattleCharacter c : list1) {
						result.add(c);
						break;
					}
					break;
				case RANDOM_ONE_ENEMY:
					List<BattleCharacter> list2 = getAllTarget(center, area);
					Collections.shuffle(list2);
					L3:
					for (BattleCharacter c : list2) {
						switch (s.getMode()) {
							case CPU:
								//CPUのアクションの場合、敵はPARTY
								if (c.isPlayer()) {
									result.add(c);
									break L3;
								}
								break;
							case PC:
								//PCのアクションの場合、敵はENEMY
								if (!c.isPlayer()) {
									result.add(c);
									break L3;
								}
								break;
						}
					}
					break;
				case RANDOM_ONE_PARTY:
					List<BattleCharacter> list3 = getAllTarget(center, area);
					Collections.shuffle(list3);
					L4:
					for (BattleCharacter c : list3) {
						switch (s.getMode()) {
							case CPU:
								//CPUのアクションの場合、敵はPARTY
								if (!c.isPlayer()) {
									result.add(c);
									break L4;
								}
								break;
							case PC:
								//PCのアクションの場合、敵はENEMY
								if (c.isPlayer()) {
									result.add(c);
									break L4;
								}
								break;
						}
					}
					break;
				default:
					throw new AssertionError("undefine case : " + s.getMode());
			}
		}
		return result;
	}
	//
	//--------------------------OTHER
	//

	public List<BattleCharacter> getSelected() {
		return selected;
	}

	public BattleAction getCurrentBA() {
		return currentBA;
	}

	@Override
	public String toString() {
		return "BattleTargetSystem{" + "selected=" + selected + ", inArea=" + inArea + '}';
	}

}
