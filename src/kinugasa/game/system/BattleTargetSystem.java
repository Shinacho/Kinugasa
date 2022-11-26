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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
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

	public enum TargetTeam {
		// 敵のみ
		ENEMY,
		// 味方のみ
		PARTY,
		// 敵味方両方
		ALL,
		// フィールド
		FIELD,
	}

	public enum SelectNumMode {
		SELF,
		ONE,
		ALL,
	}

	void init(List<PlayerCharacter> pc, List<Enemy> enemy) {
		pcList = pc;
		enemyList = enemy;
	}
	//敵味方のスプライトのリスト
	private List<PlayerCharacter> pcList;
	private List<Enemy> enemyList;
	//選択中対象リスト
	private List<BattleCharacter> selected = new ArrayList<>();
	private List<BattleCharacter> inArea = new ArrayList<>();
	private static final int ALL_SELECTED = -1;
	private int selectedIdx = ALL_SELECTED;

	//モード
	private SelectNumMode selectNumMode = SelectNumMode.ONE;
	private TargetTeam targetTeam = TargetTeam.ENEMY;
	//選択中ターゲットの選択アイコン点滅時間
	private int blinkTime = 10;
	private FrameTimeCounter blinkTC = new FrameTimeCounter(blinkTime);
	//選択中アイコンのマスタ
	private Sprite iconMaster;

	{
		iconMaster = new TextLabelSprite("↓", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK)), -123, -123, 12, 12);
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
		updateInAreaTarget();
		updateIcon();
	}

	public void setCurrentLocation(Point2D.Float currentLocation) {
		this.currentLocation = currentLocation;
		updateInAreaTarget();
		updateIcon();
	}

	public void switchToOneOfEnemy() {
		targetTeam = TargetTeam.ENEMY;
		selectNumMode = SelectNumMode.ONE;
		updateInAreaTarget();
		selected.clear();
		if (!inArea.isEmpty()) {
			selected.add(inArea.get(0));
		}
		updateIcon();
	}

	public void switchToOneOfParty() {
		targetTeam = TargetTeam.PARTY;
		selectNumMode = SelectNumMode.ONE;
		updateInAreaTarget();
		selected.clear();
		if (!inArea.isEmpty()) {
			selected.add(inArea.get(0));
		}
		updateIcon();
	}

	public void switchToField() {
		targetTeam = TargetTeam.FIELD;
		selected.clear();
		inArea.clear();
		updateIcon();
	}

	public void switchToOneOfAll() {
		targetTeam = TargetTeam.ALL;
		selectNumMode = SelectNumMode.ONE;
		updateInAreaTarget();
		selected.clear();
		if (!inArea.isEmpty()) {
			selected.add(inArea.get(0));
		}
		updateIcon();
	}

	public void switchToAllOfAll() {
		targetTeam = TargetTeam.ALL;
		selectNumMode = SelectNumMode.ALL;
		updateInAreaTarget();
		selected.clear();
		selected.addAll(inArea);
		updateIcon();
	}

	public void switchToAllOfEnemy() {
		targetTeam = TargetTeam.ENEMY;
		selectNumMode = SelectNumMode.ALL;
		updateInAreaTarget();
		selected.clear();
		selected.addAll(inArea);
		updateIcon();
	}

	public void switchToAllOfParty() {
		targetTeam = TargetTeam.PARTY;
		selectNumMode = SelectNumMode.ALL;
		updateInAreaTarget();
		selected.clear();
		selected.addAll(inArea);
		updateIcon();
	}

	public void switchToSelf() {
		targetTeam = TargetTeam.PARTY;
		selectNumMode = SelectNumMode.SELF;
		updateInAreaTarget();
		selected.clear();
		selected.add(GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser());
		updateIcon();
	}

	public void switchTeamOrEnemy() {
		if (targetTeam == TargetTeam.ENEMY) {
			targetTeam = TargetTeam.PARTY;
		}
		if (targetTeam == TargetTeam.PARTY) {
			targetTeam = TargetTeam.ENEMY;
		}
		updateInAreaTarget();
		selected.clear();
		if (!inArea.isEmpty()) {
			if (selectNumMode == SelectNumMode.ALL) {
				selected.addAll(inArea);
			} else {
				selected.add(inArea.get(0));
			}
		}
		updateIcon();
	}

	// 設定中のプロパティに基づいて次の選択しに移動する
	public void next() {
		if (targetTeam == TargetTeam.FIELD) {
			return;
		}
		if (targetTeam == TargetTeam.ALL) {
			return;
		}
		//PARTY or ENEMY
		if (selectNumMode == SelectNumMode.ALL) {
			switchTeamOrEnemy();
		} else {
			//ONEの場合、インデックスを更新
			selectedIdx++;
			if (selectedIdx >= inArea.size()) {
				selectedIdx = 0;
			}
		}
	}

	public void prev() {
		if (targetTeam == TargetTeam.FIELD) {
			return;
		}
		if (targetTeam == TargetTeam.ALL) {
			return;
		}
		//PARTY or ENEMY
		if (selectNumMode == SelectNumMode.ALL) {
			switchTeamOrEnemy();
		} else {
			//ONEの場合、インデックスを更新
			selectedIdx--;
			if (selectedIdx < 0) {
				selectedIdx = inArea.size() - 1;
			}
		}
	}

	private void updateInAreaTarget() {
		//crrentLocationからarea内の全対象を選択（ターゲットになりえるスプライト
		inArea.clear();
		if (targetTeam == TargetTeam.FIELD) {
			return;
		}
		if (currentLocation == null) {
			throw new GameSystemException("BCS : current location is null");
		}
		if (area == 0) {
			return;
		}
		//SELFだったらなにも追加せずに終了
		if (targetTeam == TargetTeam.ENEMY || targetTeam == TargetTeam.ALL) {
			//ENEMY
			for (Enemy e : enemyList) {
				if (currentLocation.distance(e.getSprite().getCenter()) < area) {
					inArea.add(e);
				}
			}
		}
		if (targetTeam == TargetTeam.PARTY || targetTeam == TargetTeam.ALL) {
			for (PlayerCharacter pc : pcList) {
				if (currentLocation.distance(pc.getSprite().getCenter()) < area) {
					inArea.add(pc);
				}
			}
		}
	}

	private void updateIcon() {
		icons.clear();
		if (targetTeam == TargetTeam.FIELD) {
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(BattleFieldSystem.getInstance().getBattleFieldAllArea().getCenter());
			icons.add(i);
			return;
		}
		//選択中の対象者の頭上にアイコンを設定
		for (BattleCharacter c : selected) {
			float x = c.getSprite().getCenterX() - iconMaster.getWidth() / 2;
			float y = c.getSprite().getCenterY() - iconMaster.getHeight();
			Sprite i = iconMaster.clone();
			i.setLocationByCenter(new Point2D.Float(x, y));
			icons.add(i);
		}

	}

	public boolean hasTargetInArea() {
		return !inArea.isEmpty();
	}

	public boolean isSelected() {
		return targetTeam == TargetTeam.FIELD ? true : !selected.isEmpty();
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

	public int getBlinkTime() {
		return blinkTime;
	}

	public List<Enemy> getEnemyList() {
		return enemyList;
	}

	public Sprite getIconMaster() {
		return iconMaster;
	}

	public List<PlayerCharacter> getPcList() {
		return pcList;
	}

	public SelectNumMode getSelectNumMode() {
		return selectNumMode;
	}

	public List<BattleCharacter> getSelected() {
		return selected;
	}

	public List<BattleCharacter> getInArea() {
		return inArea;
	}

	public TargetTeam getTargetTeam() {
		return targetTeam;
	}

	public List<Sprite> getIcons() {
		return icons;
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

	@Override
	public String toString() {
		return "BattleTargetSystem{" + "selected=" + selected + ", inArea=" + inArea + ", selectedIdx=" + selectedIdx + ", selectNumMode=" + selectNumMode + ", targetTeam=" + targetTeam + ", blinkTC=" + blinkTC + ", iconMaster=" + iconMaster + ", iconVisible=" + iconVisible + '}';
	}

}
