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
		pcList = pc;
		enemyList = enemy;
	}
	//敵味方のスプライトのリスト
	private List<PlayerCharacter> pcList;
	private List<Enemy> enemyList;
	//選択中対象リスト
	private List<BattleTarget> selected = new ArrayList<>();
	private List<BattleCharacter> inArea = new ArrayList<>();
	private static final int ALL_SELECTED = -1;
	private int selectedIdxInArea = ALL_SELECTED;
	//
	private BattleAction currentBA;

	//選択中ターゲットの選択アイコン点滅時間
	private int blinkTime = 8;
	private FrameTimeCounter blinkTC = new FrameTimeCounter(blinkTime);
	//選択中アイコンのマスタ
	private Sprite iconMaster;

	{
		iconMaster = new TextLabelSprite("↓", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontStyle(Font.BOLD)), -123, -123, 12, 12);
	}
	//選択中アイコンの実態
	private List<Sprite> icons = new ArrayList<>();
	//選択可能エリア定義
	private Point2D.Float currentLocation;
	private int area;
	private boolean iconVisible = false;

	public void setIconVisible(boolean iconVisible) {
		this.iconVisible = iconVisible;
	}

	public boolean isIconVisible() {
		return iconVisible;
	}

	public void setArea(int area) {
		this.area = area;
	}

	private void setCurrentLocation(Point2D.Float currentLocation) {
		this.currentLocation = currentLocation;
	}

	public void setTarget(BattleAction ba, Point2D.Float location, int area) {
		currentBA = ba;
		//エリア、座標
		setCurrentLocation(location);
		setArea(area);
		updateInAreaTarget();
		//selectedセット
		selectedIdxInArea = 0;
		updateSelected(selectedIdxInArea);
		//Inarea、Iconセット
		updateIcon();
		setIconVisible(true);
	}

	//selectedの初期設定に使うメソッド、前提として、InAreaが入っていること（FIELD,SELF以外
	private void updateSelected(int idx) {
		selected.clear();
		for (BattleActionEvent e : currentBA.getEvents()) {
			BattleActionTargetType batt = e.getBatt();
			switch (batt) {
				case FIELD:
					selected.add(new BattleTarget(batt));
					continue;
				case SELF:
					//カレントコマンドの使用者を設定
					selected.add(new BattleTarget(batt, GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser()));
					break;
				case ALL:
					//INArea内のすべてのターゲットを設定、ただしINAREAが空の場合は何も（キーを）追加しない
					if (!inArea.isEmpty()) {
						selected.add(new BattleTarget(batt, inArea));
					}
					break;
				case ONE_ENEMY:
					//inAreaの一つを追加（選択させる
					int i4 = 0;
					for (BattleCharacter c : inArea) {
						if (!c.isPlayer() && i4 == idx) {
							selectedIdxInArea = i4;
							selected.add(new BattleTarget(batt, c));
							break;
						}
						i4++;
					}
					break;
				case ONE_PARTY:
					//inAreaの一つを追加（選択させる
					int i5 = 0;
					for (BattleCharacter c : inArea) {
						if (c.isPlayer() && i5 == idx) {
							selectedIdxInArea = i5;
							selected.add(new BattleTarget(batt, c));
							break;
						}
						i5++;
					}
					break;
				case RANDOM_ONE:
					//inAreaの一つを追加（選択させない
					for (BattleCharacter c : inArea) {
						selectedIdxInArea = 0;
						selected.add(new BattleTarget(batt, c));
						break;
					}
					break;
				case RANDOM_ONE_ENEMY:
					//inAreaの一つを追加（選択させない
					int i7 = 0;
					for (BattleCharacter c : inArea) {
						if (!c.isPlayer()) {
							selectedIdxInArea = i7;
							selected.add(new BattleTarget(batt, c));
							break;
						}
						i7++;
					}
					break;
				case RANDOM_ONE_PARTY:
					//inAreaの一つを追加（選択させない
					int i8 = 0;
					for (BattleCharacter c : inArea) {
						if (c.isPlayer()) {
							selectedIdxInArea = i8;
							selected.add(new BattleTarget(batt, c));
							break;
						}
						i8++;
					}
					break;
				case TEAM_ENEMY:
					//inArea内のプレイヤーじゃないキャラをすべて追加
					for (BattleCharacter c : inArea) {
						if (!c.isPlayer()) {
							selected.add(new BattleTarget(batt, c));
						}
					}
					break;
				case TEAM_PARTY:
					//inArea内のプレイヤーキャラをすべて追加
					for (BattleCharacter c : inArea) {
						if (c.isPlayer()) {
							selected.add(new BattleTarget(batt, c));
						}
					}
					break;
				default:
					throw new AssertionError();
			}
		}
	}

	public void unset() {
		selected.clear();
		icons.clear();
	}

	public boolean hasAnyTarget() {
		return !selected.isEmpty();
	}

	public List<BattleCharacter> getInArea() {
		return inArea;
	}

	private void updateInAreaTarget() {
		//crrentLocationからarea内の全対象を選択（ターゲットになりえるスプライト
		if (currentLocation == null) {
			throw new GameSystemException("BTS : current location is null");
		}
		assert GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleFieldAllArea().contains(currentLocation) : "BTS : current location is not in area";
		if (area == 0) {
			return;
		}
		inArea.clear();
		for (BattleActionEvent e : currentBA.getEvents()) {
			//FIELD
			if (e.getBatt() == BattleActionTargetType.FIELD) {
				continue;
			}
			//SELF
			if (e.getBatt() == BattleActionTargetType.SELF) {
				continue;
			}
			//その他
			if (e.getBatt() == BattleActionTargetType.TEAM_ENEMY
					|| e.getBatt() == BattleActionTargetType.RANDOM_ONE_ENEMY
					|| e.getBatt() == BattleActionTargetType.ONE_ENEMY
					|| e.getBatt() == BattleActionTargetType.RANDOM_ONE
					|| e.getBatt() == BattleActionTargetType.ALL) {
				//ENEMY
				for (Enemy enemy : enemyList) {
					if (currentLocation.distance(enemy.getSprite().getCenter()) < area) {
						inArea.add(enemy);
					}
				}
			}
			if (e.getBatt() == BattleActionTargetType.TEAM_PARTY
					|| e.getBatt() == BattleActionTargetType.RANDOM_ONE_PARTY
					|| e.getBatt() == BattleActionTargetType.ONE_PARTY
					|| e.getBatt() == BattleActionTargetType.RANDOM_ONE
					|| e.getBatt() == BattleActionTargetType.ALL) {
				//PARTY
				for (PlayerCharacter pc : pcList) {
					if (currentLocation.distance(pc.getSprite().getCenter()) < area) {
						inArea.add(pc);
					}
				}
			}
		}
		inArea = inArea.stream().distinct().collect(Collectors.toList());
	}

	private void updateIcon() {
		//SELECTEDの場所にアイコンを設置する
		icons.clear();
		for (BattleTarget t : selected) {
			if (t.getTargetType() == BattleActionTargetType.FIELD) {
				Sprite i = iconMaster.clone();
				i.setLocationByCenter(BattleFieldSystem.getInstance().getBattleFieldAllArea().getCenter());
				icons.add(i);
				continue;
			}
			for (BattleCharacter c : t.getTarget()) {
				float x = c.getSprite().getCenterX();
				float y = c.getSprite().getCenterY() - iconMaster.getHeight() - 4;
				Sprite i = iconMaster.clone();
				i.setLocationByCenter(new Point2D.Float(x, y));
				icons.add(i);
			}
		}
	}

	//
	//--------------------------------------------------対象選択
	//
	//ONE_ENEMY/ONE_PARTYの場合だけインデックスを動かせる。インデックスはinAreaのインデックス
	public void prev() {
		if (contains(BattleActionTargetType.ONE_ENEMY) && contains(BattleActionTargetType.ONE_PARTY)) {
			throw new GameSystemException("BTS error, this conbination is cant exec [ONE_ENEMY, ONE_PARTY]");
		}
		if (inArea.size() == 1) {
			return;
		}
		for (BattleActionEvent e : currentBA.getEvents()) {
			switch (e.getBatt()) {
				case ONE_ENEMY:
					for (int i = selectedIdxInArea - 1; true; i--) {
						if (i < 0) {
							i = inArea.size() - 1;
						}
						BattleCharacter c = inArea.get(i);
						if (!c.isPlayer()) {
							selectedIdxInArea = i;
							break;
						}
					}
					break;
				case ONE_PARTY:
					for (int i = selectedIdxInArea - 1; true; i--) {
						if (i < 0) {
							i = inArea.size() - 1;
						}
						BattleCharacter c = inArea.get(i);
						if (c.isPlayer()) {
							selectedIdxInArea = i;
							break;
						}
					}
					break;
				default:
					break;
			}
		}
		updateSelected(selectedIdxInArea);
		updateIcon();
	}

	public void next() {
		if (contains(BattleActionTargetType.ONE_ENEMY) && contains(BattleActionTargetType.ONE_PARTY)) {
			throw new GameSystemException("BTS error, this conbination is cant exec [ONE_ENEMY, ONE_PARTY]");
		}
		if (inArea.size() == 1) {
			return;
		}
		for (BattleActionEvent e : currentBA.getEvents()) {
			switch (e.getBatt()) {
				case ONE_ENEMY:
					for (int i = selectedIdxInArea + 1; true; i++) {
						if (i >= inArea.size()) {
							i = 0;
						}
						BattleCharacter c = inArea.get(i);
						if (!c.isPlayer()) {
							selectedIdxInArea = i;
							break;
						}
					}
					break;
				case ONE_PARTY:
					for (int i = selectedIdxInArea + 1; true; i++) {
						if (i >= inArea.size()) {
							i = 0;
						}
						BattleCharacter c = inArea.get(i);
						if (c.isPlayer()) {
							selectedIdxInArea = i;
							break;
						}
					}
					break;
				default:
					break;
			}
		}
		updateSelected(selectedIdxInArea);
		updateIcon();
	}

	void update() {
		//点滅を制御
		if (blinkTC.isReaching()) {
			blinkTC = new FrameTimeCounter(blinkTime);
			icons.forEach(v -> v.switchVisible());
		}
	}

	@Override
	public void draw(GraphicsContext g) {
		if (!iconVisible) {
			return;
		}
		icons.forEach(v -> v.draw(g));
		//エリアはバトルシステムが描画する
	}

	public void setBlinkTime(int blinkTime) {
		this.blinkTime = blinkTime;
	}

	public List<Enemy> getEnemyList() {
		return enemyList;
	}

	public List<PlayerCharacter> getPcList() {
		return pcList;
	}

	//
	//-----------------------------------------------ENEMY TARET SYSTEM
	//
	//モードにかかわらず取得する
	public List<BattleCharacter> getPartyTarget(Point2D.Float center, int a) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : getPcList()) {
			if (c.getSprite().getCenter().distance(center) < a) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		return result;
	}

	public List<BattleCharacter> getAllTarget(Point2D.Float center, int a) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : getPcList()) {
			if (c.getSprite().getCenter().distance(center) < a) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		for (BattleCharacter c : getEnemyList()) {
			if (c.getSprite().getCenter().distance(center) < a) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result.add(c);
				}
			}
		}
		return result;
	}

	public BattleCharacter nearPlayer(Point2D.Float center) {
		float distance = Float.MAX_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : getPcList()) {
			if (c.getSprite().getCenter().distance(center) < distance) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result = c;
				}
			}
		}
		assert result != null : "nearPlayer but all player is dead";
		return result;
	}

	public BattleCharacter nearEnemy(Point2D.Float center) {
		float distance = Float.MAX_VALUE;
		BattleCharacter result = null;
		for (BattleCharacter c : getEnemyList()) {
			if (c.getSprite().getCenter().distance(center) < distance) {
				if (!c.getStatus().hasConditions(false, BattleConfig.getUntargetConditionNames())) {
					result = c;
				}
			}
		}
		assert result != null : "nearEnemy but all player is dead";
		return result;
	}

	public List<BattleCharacter> nearPlayer(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : getPcList()) {
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

	public List<BattleCharacter> nearEnemy(Point2D.Float center, int area) {
		List<BattleCharacter> result = new ArrayList<>();
		for (BattleCharacter c : getEnemyList()) {
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
					result.clear();
					break;
				case ALL:
					result.addAll(getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea()));
					break;
				case SELF:
					result.add(s.getUser());
					break;
				case TEAM_ENEMY:
					for (BattleCharacter c : getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea())) {
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
					for (BattleCharacter c : getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea())) {
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
					for (BattleCharacter c : getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea())) {
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
					for (BattleCharacter c : getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea())) {
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
					List<BattleCharacter> list1 = getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea());
					Collections.shuffle(list1);
					for (BattleCharacter c : list1) {
						result.add(c);
						break;
					}
					break;
				case RANDOM_ONE_ENEMY:
					List<BattleCharacter> list2 = getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea());
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
					List<BattleCharacter> list3 = getAllTarget(s.getUser().getSprite().getCenter(), s.getMagic().getArea());
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
					throw new AssertionError();
			}
		}
		return result;
	}
	//
	//--------------------------OTHER
	//

	public List<BattleTarget> getSelected() {
		return selected;
	}

	public boolean contains(BattleActionTargetType t) {
		return selected.stream().anyMatch(p -> p.getTargetType() == t);
	}

	public boolean isFieldOnly() {
		return selected.stream().allMatch(p -> p.getTargetType() == BattleActionTargetType.FIELD);
	}

	@Override
	public String toString() {
		return "BattleTargetSystem{" + "selected=" + selected + ", inArea=" + inArea + '}';
	}

}
