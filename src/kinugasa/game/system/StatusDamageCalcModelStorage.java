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
package kinugasa.game.system;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import kinugasa.game.GameLog;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageUtil;
import kinugasa.graphics.SpriteSheet;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Storage;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/11/15_21:31:55<br>
 * @author Shinacho<br>
 */
public class StatusDamageCalcModelStorage extends Storage<StatusDamageCalcModel> {

	private static final StatusDamageCalcModelStorage INSTANCE = new StatusDamageCalcModelStorage();

	private StatusDamageCalcModelStorage() {
		add(new StatusDamageCalcModel("DEFAULT") {
			@Override
			public ActionEventResult exec(BattleCharacter user, ActionEvent ba, BattleCharacter tgt) {

				switch (ba.getParameterType()) {
					case ADD_CONDITION:
					case ATTR_IN:
					case ITEM_ADD:
					case ITEM_LOST:
					case NONE:
					case REMOVE_CONDITION:
						throw new GameSystemException("damage calculation: Invalid damage calc type:" + ba);
					case STATUS:
						switch (ba.getDamageCalcType()) {
							case DIRECT:
							case PERCENT_OF_MAX:
							case PERCENT_OF_NOW:
								throw new GameSystemException("damage calculation: Invalid damage calc type:" + ba);
							case USE_DAMAGE_CALC:
								//OK
								break;
							default:
								throw new AssertionError("undefined damage calc type");
						}
						break;
					default:
						throw new AssertionError("undefined parameter type");
				}
				assert ba.getParameterType() == ParameterType.STATUS : "damage calculation: Invalid damage calc type:" + ba;
				assert ba.getDamageCalcType() == StatusDamageCalcType.USE_DAMAGE_CALC : "damage calculation: Invalid damage calc type:" + ba;

				//�����ւ̃_���[�W�̏ꍇ�G���[�Ƃ���
				if (StatusKeyStorage.getInstance().get(ba.getTgtName()).getMax() == 1f) {
					throw new GameSystemException("only non-float status can be used to calc damage.");
				}

//				//P�����ActionEvent�Ŏ��{�ς�
//				if (!Random.percent(ba.getP())) {
//					if (GameSystem.isDebugMode()) {
//						kinugasa.game.GameLog.printInfo("damage calculation, calceled by P.");
//					}
//					return new ActionEventResult(ActionResultType.MISS, null);
//				}
				//���@or�U��
				boolean isAtk = ba.getAttr().getOrder() < 10;
				StringBuilder desc = new StringBuilder();
				desc.append("SDCM : ").append(user.getName()).append("->").append(tgt.getName()).append(":");

				//�U��------------------------------------------------------------------------------------------------------------------------
				if (isAtk) {
					desc.append("ATK,");
					//ba��value * spread
					float spread = (ba.getValue() * ba.getSpread());
					float value = ba.getValue();
					if (Random.percent(0.5f)) {
						value += spread;
					} else {
						value -= spread;
					}

					//�A�N�V�����N���e�B�J������
					boolean critical = Random.percent(user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critAtk).getValue());
					if (critical) {
						desc.append("CRITICAL,");
						value *= (1 + user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critAtkVal).getValue());
					}

					//��𔻒�
					//�N���e�B�J���̏ꍇ�͉���ł��Ȃ�
					if (!critical) {
						if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.avoAtk).getValue())) {
							desc.append("AVO");
							//��𐬌�
							if (GameSystem.isDebugMode()) {
								kinugasa.game.GameLog.printInfo("damage calculation(ATK), calceled by AVO.");
							}
							return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
						}
					}

					//ATTR�v�Z
					value *= tgt.getStatus().getEffectedAttrIn().get(ba.getAttr().getName()).getValue();
					//DCS
					//���[�U�̑����i�Ɍv�Z�X�e�[�^�X�L�[�����邩����
					Item weapon = user.getStatus().getEqipment().get(ItemEqipmentSlotStorage.getInstance().get(BattleConfig.weaponSlotName));
					if (weapon != null) {
						float ave = (float) user.getStatus()
								.getEffectedStatus()
								.stream()
								.filter(p -> weapon.getDamageCalcStatusKey().contains(p.getKey()))
								.mapToDouble(p -> p.getValue() / p.getKey().getMax())
								.average()
								.getAsDouble();
						//�_���[�W�v�Z�X�e�[�^�X%���З͂��グ��
						value *= (1 + ave);
						desc.append("DCS,");
					}

					//�U���́F�h��͔���
					float atk = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.atk).getValue()
							/ user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.atk).getKey().getMax();
					float def = tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defAtk).getValue()
							/ tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defAtk).getKey().getMax();
					//�U���̊���-�h��̊���
					float atkSubDef = atk - def * BattleConfig.atkDefPercent;
					if (atkSubDef < 0) {
						atkSubDef = 0;
					}
					desc.append("atk-def[" + atkSubDef + "],");
					value *= atkSubDef;

					//�U���J�b�g����
					//�N���e�B�J���̏ꍇ�̓J�b�g�ł��Ȃ�
					if (!critical) {
						if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutAtk).getValue())) {
							//�J�b�g����
							desc.append("CUT,");
							value *= tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutAtkVal).getValue();
						}
					}

					//�^�[�Q�b�g�X�e�[�^�X����desc�ɒǉ�
					desc.append(ba.getTgtName() + ",");

					//value�ݒ�
					value *= BattleConfig.damageMul;
					value = (int) value;
					if (value == 0) {
						desc.append("VALUE is 0");
						if (GameSystem.isDebugMode()) {
							kinugasa.game.GameLog.printInfo(desc.toString());
						}
						return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
					}
					desc.append("result[" + value + "]");
					tgt.getStatus().getBaseStatus().get(ba.getTgtName()).add(value);

					//�A�j���[�V��������
					AnimationSprite sprite = null;
					if (ba.hasAnimation()) {
						//�A�j���[�V�����X�v���C�g
						sprite = new AnimationSprite(ba.getAnimationClone());
						sprite.update();
						sprite.setSizeByImage();
						sprite.getAnimation().setRepeat(false);//�d�v�A�A�j���[�V�����������Ȃ��Ȃ�
						switch (ba.getAnimationMoveType()) {
							case NONE:
								sprite.setVisible(false);
								sprite.setExist(false);
								break;
							case TGT:
								sprite.setLocationByCenter(tgt.getSprite().getCenter());
								break;
							case USER:
								sprite.setLocationByCenter(user.getSprite().getCenter());
								break;
							default:
								sprite.setVector(ba.getAnimationMoveType().createVector(user.getCenter(), tgt.getCenter()));
								break;
						}
					}

					//���U���g�ԋp
					if (GameSystem.isDebugMode()) {
						GameLog.printInfo("DamageCalcResult:" + desc.toString());
					}
					return new ActionEventResult(ActionResultType.SUCCESS, sprite);

				}
				//���@----------------------------------------------------------------------------------------------
				desc.append("MGK,");
				//ba��value * spread
				float spread = (ba.getValue() * ba.getSpread());
				float value = ba.getValue();
				if (Random.percent(0.5f)) {
					value += spread;
				} else {
					value -= spread;
				}

				//�A�N�V�����N���e�B�J������
				boolean critical = Random.percent(user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critMgk).getValue());
				if (critical) {
					desc.append("CRITICAL,");
					value *= (1 + user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.critMgkVal).getValue());
				}

				//��𔻒�
				//�N���e�B�J���̏ꍇ�͉���ł��Ȃ�
				if (!critical) {
					if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.avoMgk).getValue())) {
						desc.append("AVO");
						//��𐬌�
						if (GameSystem.isDebugMode()) {
							kinugasa.game.GameLog.printInfo("damage calculation(MGK), calceled by AVO.");
						}
						return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
					}
				}

				//ATTR�v�Z
				value *= tgt.getStatus().getEffectedAttrIn().get(ba.getAttr().getName()).getValue();

				//DCS
				//���[�U�̑����i�Ɍv�Z�X�e�[�^�X�L�[�����邩����
				Item weapon = user.getStatus().getEqipment().get(ItemEqipmentSlotStorage.getInstance().get(BattleConfig.weaponSlotName));
				if (weapon != null) {
					float ave = (float) user.getStatus()
							.getEffectedStatus()
							.stream()
							.filter(p -> weapon.getDamageCalcStatusKey().contains(p.getKey()))
							.mapToDouble(p -> p.getValue() / p.getKey().getMax())
							.average()
							.getAsDouble();
					//�_���[�W�v�Z�X�e�[�^�X%���З͂��グ��
					value *= (1 + ave);
					desc.append("DCS,");
				}

				//�U���́F�h��͔���
				float atk = user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.mgk).getValue()
						/ user.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.mgk).getKey().getMax();
				float def = tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defMgk).getValue()
						/ tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.defMgk).getKey().getMax();
				//�U���̊���-�h��̊���
				float atkSubDef = atk - def * BattleConfig.atkDefPercent;
				if (atkSubDef < 0) {
					atkSubDef = 0;
				}
				desc.append("atk-def[" + atkSubDef + "],");
				value *= atkSubDef;

				//�U���J�b�g����
				//�N���e�B�J���̏ꍇ�̓J�b�g�ł��Ȃ�
				if (!critical) {
					if (Random.percent(tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutMgk).getValue())) {
						//�J�b�g����
						desc.append("CUT,");
						value *= tgt.getStatus().getEffectedStatus().get(BattleConfig.StatusKey.cutMgkVal).getValue();
					}
				}

				//�^�[�Q�b�g�X�e�[�^�X����desc�ɒǉ�
				desc.append(ba.getTgtName() + ",");

				//value�ݒ�
				value *= BattleConfig.damageMul;
				value = (int) value;
				if (value == 0) {
					desc.append("VALUE is 0");
					if (GameSystem.isDebugMode()) {
						kinugasa.game.GameLog.printInfo(desc.toString());
					}
					return new ActionEventResult(ActionResultType.MISS, new AnimationSprite());
				}
				desc.append("result[" + value + "]");
				tgt.getStatus().getBaseStatus().get(ba.getTgtName()).add(value);

				//�A�j���[�V��������
				AnimationSprite sprite = null;
				if (ba.hasAnimation()) {
					//�A�j���[�V�����X�v���C�g
					sprite = new AnimationSprite(ba.getAnimationClone());
					sprite.update();
					sprite.setSizeByImage();
					sprite.getAnimation().setRepeat(false);//�d�v�A�A�j���[�V�����������Ȃ��Ȃ�
					switch (ba.getAnimationMoveType()) {
						case NONE:
							sprite.setVisible(false);
							sprite.setExist(false);
							break;
						case TGT:
							sprite.setLocationByCenter(tgt.getSprite().getCenter());
							break;
						case USER:
							sprite.setLocationByCenter(user.getSprite().getCenter());
							break;
						default:
							sprite.setVector(ba.getAnimationMoveType().createVector(user.getCenter(), tgt.getCenter()));
							break;
					}
				}

				//���U���g�ԋp
				if (GameSystem.isDebugMode()) {
					GameLog.printInfo("DamageCalcResult:" + desc.toString());
				}
				return new ActionEventResult(ActionResultType.SUCCESS, sprite);

			}
		});
		setCurrent("DEFAULT");
	}

	public static StatusDamageCalcModelStorage getInstance() {
		return INSTANCE;
	}
	private StatusDamageCalcModel current;

	public void setCurrent(String name) {
		this.current = get(name);
	}

	public StatusDamageCalcModel getCurrent() {
		return current;
	}

	private static String noneAttrKey = "NONE";

	public static String getNoneAttrKey() {
		return noneAttrKey;
	}

	public static void setNoneAttrKey(String noneAttrKey) {
		StatusDamageCalcModelStorage.noneAttrKey = noneAttrKey;
	}

}
