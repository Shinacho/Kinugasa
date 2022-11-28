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
	//�G�����̃X�v���C�g�̃��X�g
	private List<PlayerCharacter> pcList;
	private List<Enemy> enemyList;
	//�I�𒆑Ώۃ��X�g
	private List<BattleTarget> selected = new ArrayList<>();
	private List<BattleCharacter> inArea = new ArrayList<>();
	private static final int ALL_SELECTED = -1;
	private int selectedIdxInArea = ALL_SELECTED;
	//
	private BattleAction currentBA;

	//�I�𒆃^�[�Q�b�g�̑I���A�C�R���_�Ŏ���
	private int blinkTime = 8;
	private FrameTimeCounter blinkTC = new FrameTimeCounter(blinkTime);
	//�I�𒆃A�C�R���̃}�X�^
	private Sprite iconMaster;

	{
		iconMaster = new TextLabelSprite("��", new SimpleTextLabelModel(FontModel.DEFAULT.clone().setColor(Color.BLACK).setFontStyle(Font.BOLD)), -123, -123, 12, 12);
	}
	//�I�𒆃A�C�R���̎���
	private List<Sprite> icons = new ArrayList<>();
	//�I���\�G���A��`
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
		//�G���A�A���W
		setCurrentLocation(location);
		setArea(area);
		updateInAreaTarget();
		//selected�Z�b�g
		selectedIdxInArea = 0;
		updateSelected(selectedIdxInArea);
		//Inarea�AIcon�Z�b�g
		updateIcon();
		setIconVisible(true);
	}

	//selected�̏����ݒ�Ɏg�����\�b�h�A�O��Ƃ��āAInArea�������Ă��邱�ƁiFIELD,SELF�ȊO
	private void updateSelected(int idx) {
		selected.clear();
		for (BattleActionEvent e : currentBA.getEvents()) {
			BattleActionTargetType batt = e.getBatt();
			switch (batt) {
				case FIELD:
					selected.add(new BattleTarget(batt));
					continue;
				case SELF:
					//�J�����g�R�}���h�̎g�p�҂�ݒ�
					selected.add(new BattleTarget(batt, GameSystem.getInstance().getBattleSystem().getCurrentCmd().getUser()));
					break;
				case ALL:
					//INArea���̂��ׂẴ^�[�Q�b�g��ݒ�A������INAREA����̏ꍇ�͉����i�L�[���j�ǉ����Ȃ�
					if (!inArea.isEmpty()) {
						selected.add(new BattleTarget(batt, inArea));
					}
					break;
				case ONE_ENEMY:
					//inArea�̈��ǉ��i�I��������
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
					//inArea�̈��ǉ��i�I��������
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
					//inArea�̈��ǉ��i�I�������Ȃ�
					for (BattleCharacter c : inArea) {
						selectedIdxInArea = 0;
						selected.add(new BattleTarget(batt, c));
						break;
					}
					break;
				case RANDOM_ONE_ENEMY:
					//inArea�̈��ǉ��i�I�������Ȃ�
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
					//inArea�̈��ǉ��i�I�������Ȃ�
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
					//inArea���̃v���C���[����Ȃ��L���������ׂĒǉ�
					for (BattleCharacter c : inArea) {
						if (!c.isPlayer()) {
							selected.add(new BattleTarget(batt, c));
						}
					}
					break;
				case TEAM_PARTY:
					//inArea���̃v���C���[�L���������ׂĒǉ�
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
		//crrentLocation����area���̑S�Ώۂ�I���i�^�[�Q�b�g�ɂȂ肦��X�v���C�g
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
			//���̑�
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
		//SELECTED�̏ꏊ�ɃA�C�R����ݒu����
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
	//--------------------------------------------------�ΏۑI��
	//
	//ONE_ENEMY/ONE_PARTY�̏ꍇ�����C���f�b�N�X�𓮂�����B�C���f�b�N�X��inArea�̃C���f�b�N�X
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
		//�_�ł𐧌�
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
		//�G���A�̓o�g���V�X�e�����`�悷��
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
	//���[�h�ɂ�����炸�擾����
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
								//CPU�̃A�N�V�����̏ꍇ�A�G��PARTY
								if (c.isPlayer()) {
									result.add(c);
								}
								break;
							case PC:
								//PC�̃A�N�V�����̏ꍇ�A�G��ENEMY
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
								//CPU�̃A�N�V�����̏ꍇ�A�G��PARTY
								if (!c.isPlayer()) {
									result.add(c);
								}
								break;
							case PC:
								//PC�̃A�N�V�����̏ꍇ�A�G��ENEMY
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
								//CPU�̃A�N�V�����̏ꍇ�A�G��PARTY
								if (c.isPlayer()) {
									result.add(c);
									break L1;
								}
								break;
							case PC:
								//PC�̃A�N�V�����̏ꍇ�A�G��ENEMY
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
								//CPU�̃A�N�V�����̏ꍇ�A�G��PARTY
								if (!c.isPlayer()) {
									result.add(c);
									break L2;
								}
								break;
							case PC:
								//PC�̃A�N�V�����̏ꍇ�A�G��ENEMY
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
								//CPU�̃A�N�V�����̏ꍇ�A�G��PARTY
								if (c.isPlayer()) {
									result.add(c);
									break L3;
								}
								break;
							case PC:
								//PC�̃A�N�V�����̏ꍇ�A�G��ENEMY
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
								//CPU�̃A�N�V�����̏ꍇ�A�G��PARTY
								if (!c.isPlayer()) {
									result.add(c);
									break L4;
								}
								break;
							case PC:
								//PC�̃A�N�V�����̏ꍇ�A�G��ENEMY
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
