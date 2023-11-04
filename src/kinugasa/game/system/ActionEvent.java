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
import java.util.Map;
import kinugasa.game.I18N;
import kinugasa.game.field4.FieldMap;
import kinugasa.game.field4.FieldMapStorage;
import static kinugasa.game.system.ActionType.攻撃;
import static kinugasa.game.system.ActionType.魔法;
import kinugasa.game.ui.Text;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.Nameable;
import kinugasa.resource.sound.Sound;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2023/10/14_14:19:45<br>
 * @author Shinacho<br>
 */
public class ActionEvent implements Nameable, Comparable<ActionEvent> {

	public static class Term implements Nameable {

		public static enum Type {
			指定の状態異常を持っている,
			指定の状態異常を持っていない,
			指定の武器タイプの武器を装備している,
			指定のアイテムを持っている,
			指定のアイテムのいずれかを持っている,
			指定の名前のアイテムを持っている,
			指定のステータスの現在値が指定の割合以上,
		}
		public final String id;
		public final Type type;
		public final float value;
		public final String tgtName;

		public Term(String id, Type type, float value, String tgtName) {
			this.id = id;
			this.type = type;
			this.value = value;
			this.tgtName = tgtName;
		}

		public boolean canDo(Status a) {
			switch (type) {
				case 指定の状態異常を持っている: {
					return a.getCurrentConditions().containsKey(ConditionKey.valueOf(tgtName));
				}
				case 指定の状態異常を持っていない: {
					return !a.getCurrentConditions().containsKey(ConditionKey.valueOf(tgtName));
				}
				case 指定の武器タイプの武器を装備している: {
					if (a.getEqip().get(EqipSlot.右手) != null) {
						if (a.getEqip().get(EqipSlot.右手).getWeaponType() == WeaponType.valueOf(tgtName)) {
							return true;
						}
					}
					if (a.getEqip().get(EqipSlot.左手) != null) {
						if (a.getEqip().get(EqipSlot.左手).getWeaponType() == WeaponType.valueOf(tgtName)) {
							return true;
						}
					}
					return false;
				}
				case 指定のアイテムを持っている: {
					return a.getItemBag().contains(tgtName);
				}
				case 指定のアイテムのいずれかを持っている: {
					String[] ids = tgtName.contains(",") ? tgtName.split(",") : new String[]{tgtName};
					for (String id : ids) {
						if (a.getItemBag().contains(id)) {
							return true;
						}
					}
					return false;
				}
				case 指定の名前のアイテムを持っている: {
					return a.getItemBag().getItems().stream().anyMatch(p -> p.getVisibleName().contains(tgtName));
				}
				case 指定のステータスの現在値が指定の割合以上: {
					return a.getEffectedStatus().get(StatusKey.valueOf(tgtName)).get割合() >= value;
				}
				default:
					throw new AssertionError("undefined term type : " + type);
			}
		}

		@Deprecated
		@Override
		public String getName() {
			return id;
		}

		public String getId() {
			return id;
		}

	}

	public enum CalcMode {
		MUL,
		ADD,
		TO,
		TO_ZERO,
		TO_MAX,
		DC;

		public String getVisibleName() {
			return I18N.get(toString());
		}
	}

	private String id;
	private int sort;
	private ActionEventType type;
	private List<Term> terms = new ArrayList<>();
	private StatusKey tgtStatusKey;
	private float p;
	private ConditionKey tgtConditionKey;
	private int cndTime;
	private AttributeKey atkAttr;
	private AttributeKey tgtAttrKeyOut;
	private AttributeKey tgtAttrKeyIn;
	private ConditionKey tgtCndRegist;
	private String tgtId;
	private boolean noLimit;
	private float value;
	private CalcMode calcMode;
	private Sound successSound;
	private AnimationSprite tgtAnimation, otherAnimation;

	public ActionEvent(String id) {
		this.id = id;
	}

	//Actorごとに呼び出される。このアクションイベントを実行してイベントリザルトを戻す
	public ActionResult.EventResult exec(Actor user, Action a, Actor tgt) {
		if (!Random.percent(p)) {
			return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿不発, this);
		}
		StatusValueSet tgtStatus = tgt.getStatus().getEffectedStatus();
		tgt.getStatus().saveBeforeDamageCalc();
		switch (type) {
			case ビームエフェクト:
			case 独自効果: {
				throw new GameSystemException("custom action event, but exec is not overrided : " + this);
			}
			case ステータス攻撃: {
				if (tgtStatusKey == null) {
					throw new GameSystemException("status damage, but status key is null : " + this);
				}
				float value = this.value;
				float prev = tgtStatus.get(tgtStatusKey).getValue();
				switch (calcMode) {
					case ADD: {
						if (noLimit) {
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
						}
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case MUL: {
						if (noLimit) {
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
						}
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case TO: {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case TO_MAX: {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case TO_ZERO: {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case DC: {
						if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
							throw new GameSystemException("damage calc is cant exec in field : " + this);
						}
						//攻撃タイプ調整
						DamageCalcSystem.ActionType actionType
								= switch (a.getType()) {
							case 攻撃 ->
								DamageCalcSystem.ActionType.物理攻撃;
							case 魔法 ->
								DamageCalcSystem.ActionType.魔法攻撃;
							default ->
								throw new AssertionError("damage calc cant exec : " + this);
						};

						//アイテムからDCS取得
						StatusKey dcs = null;
						if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
							Item i = user.getStatus().getEqip().get(EqipSlot.右手);
							if (i != null && i.getDcs() != null) {
								dcs = i.getDcs();
							}
						}
						if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
							Item i = user.getStatus().getEqip().get(EqipSlot.左手);
							if (i != null && i.getDcs() != null) {
								dcs = i.getDcs();
							}
						}
						if (dcs == null) {
							dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
						}
						//ダメージ計算実行
						DamageCalcSystem.Result r
								= DamageCalcSystem.calcDamage(
										new DamageCalcSystem.Param(
												user,
												tgt,
												atkAttr,
												actionType,
												value,
												tgtStatusKey,
												dcs)
								);

						//r評価
						return convert(r);
					}
					default:
						throw new AssertionError("undefined calc mode");
				}//switch
			}
			case ステータス回復: {
				if (tgtStatusKey == null) {
					throw new GameSystemException("status heal, but status key is null : " + this);
				}
				float value = this.value;
				float prev = tgtStatus.get(tgtStatusKey).getValue();
				switch (calcMode) {
					case ADD: {
						if (noLimit) {
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).addMax(value);
						}
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).add(value);
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case MUL: {
						if (noLimit) {
							tgt.getStatus().getBaseStatus().get(tgtStatusKey).mulMax(value);
						}
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).mul(value);
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case TO: {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).setValue(value);
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case TO_MAX: {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toMax();
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case TO_ZERO: {
						tgt.getStatus().getBaseStatus().get(tgtStatusKey).toZero();
						return getResult(prev != tgt.getStatus().getBaseStatus().get(tgtStatusKey).getValue(), tgt);
					}
					case DC: {
						if (GameSystem.getInstance().getMode() == GameMode.FIELD) {
							throw new GameSystemException("damage calc is cant exec in field : " + this);
						}
						//攻撃タイプ調整
						DamageCalcSystem.ActionType actionType
								= switch (a.getType()) {
							case 攻撃 ->
								DamageCalcSystem.ActionType.物理回復;
							case 魔法 ->
								DamageCalcSystem.ActionType.魔法回復;
							default ->
								throw new AssertionError("damage calc cant exec : " + this);
						};
						//アイテムからDCS取得
						StatusKey dcs = null;
						if (user.getStatus().getEqip().containsKey(EqipSlot.右手)) {
							Item i = user.getStatus().getEqip().get(EqipSlot.右手);
							if (i != null && i.getDcs() != null) {
								dcs = i.getDcs();
							}
						}
						if (user.getStatus().getEqip().containsKey(EqipSlot.左手)) {
							Item i = user.getStatus().getEqip().get(EqipSlot.左手);
							if (i != null && i.getDcs() != null) {
								dcs = i.getDcs();
							}
						}
						if (dcs == null) {
							dcs = actionType == DamageCalcSystem.ActionType.物理攻撃 ? StatusKey.筋力 : StatusKey.精神力;
						}

						//ダメージ計算実行
						DamageCalcSystem.Result r
								= DamageCalcSystem.calcDamage(
										new DamageCalcSystem.Param(
												user,
												tgt,
												atkAttr,
												actionType,
												value,
												tgtStatusKey,
												dcs)
								);

						//r評価
						return convert(r);
					}
					default:
						throw new AssertionError("undefined calc mode");
				}//switch
			}
			case CND_REGIST: {
				if (this.tgtCndRegist == null) {
					throw new GameSystemException("tgt cndRegist is null" + this);
				}
				float v = tgt.getStatus().getConditionRegist().get(tgtCndRegist);
				tgt.getStatus().getConditionRegist().put(tgtCndRegist, v + value);
				return getResult(true, tgt);
			}
			case ATTR_IN: {
				if (this.tgtAttrKeyIn == null) {
					throw new GameSystemException("tgt attr_in is null" + this);
				}
				tgt.getStatus().getAttrIn().get(tgtAttrKeyOut).add(value);
				return getResult(true, tgt);
			}
			case ATTR_OUT: {
				if (this.tgtAttrKeyOut == null) {
					throw new GameSystemException("tgt attr_out is null" + this);
				}
				tgt.getStatus().getAttrOut().get(tgtAttrKeyOut).add(value);
				return getResult(true, tgt);
			}
			case 状態異常付与: {
				if (this.tgtConditionKey == null) {
					throw new GameSystemException("tgt cnd is null" + this);
				}
				String msg = tgt.getStatus().addCondition(tgtConditionKey, cndTime);
				ActionResult.EventResult r = getResult(msg != null, tgt);
				r.msgI18Nd = msg;
				return r;
			}
			case 状態異常解除: {
				if (this.tgtConditionKey == null) {
					throw new GameSystemException("tgt cnd is null" + this);
				}
				String msg = tgt.getStatus().removeCondition(tgtConditionKey);
				ActionResult.EventResult r = getResult(msg != null, tgt);
				r.msgI18Nd = msg;
				return r;
			}
			case アイテム追加: {
				if (tgtId == null) {
					throw new GameSystemException("tgt item id is null : " + this);
				}
				Item i = ActionStorage.getInstance().itemOf(id);
				if (i == null) {
					throw new GameSystemException("tgt item is null : " + this);
				}
				if (noLimit || tgt.getStatus().getItemBag().canAdd()) {
					tgt.getStatus().getItemBag().add(i);
				}
				ActionResult.EventResult ae = new ActionResult.EventResult(tgt, ActionResultSummary.成功, this);
				if (this.otherAnimation != null) {
					ae.otherAnimation = this.otherAnimation.clone();
				}
				if (this.tgtAnimation != null) {
					ae.tgtAnimation = this.tgtAnimation.clone();
				}
				if (successSound != null) {
					successSound.load().stopAndPlay();
				}
				return ae;
			}
			case アイテムロスト: {
				if (tgtId == null) {
					throw new GameSystemException("tgt item id is null : " + this);
				}
				Item i = ActionStorage.getInstance().itemOf(id);
				if (i == null) {
					throw new GameSystemException("tgt item is null : " + this);
				}
				if (tgt.getStatus().getItemBag().contains(i)) {
					tgt.getStatus().getItemBag().drop(i);
				}
				ActionResult.EventResult ae = new ActionResult.EventResult(tgt, ActionResultSummary.成功, this);
				if (this.otherAnimation != null) {
					ae.otherAnimation = this.otherAnimation.clone();
				}
				if (this.tgtAnimation != null) {
					ae.tgtAnimation = this.tgtAnimation.clone();
				}
				if (successSound != null) {
					successSound.load().stopAndPlay();
				}
				return ae;
			}
			case 召喚: {
				String fileName = tgtId;
				if (user.isPlayer()) {
					Actor ac = new Actor(fileName);
					ac.setSummoned(true);
					ac.getSprite().setLocationByCenter(Random.randomLocation(user.getSprite().getCenter(), 75f));
					GameSystem.getInstance().getParty().add(ac);
					return getResult(true, ac);
				}
				Enemy e = new Enemy(fileName);
				e.setSummoned(true);
				e.getSprite().setLocationByCenter(Random.randomLocation(user.getSprite().getCenter(), 75f));
				BattleSystem.getInstance().getEnemies().add(e);
				return getResult(true, e);
			}
			case ユーザの武器をドロップしてドロップアイテムに追加: {
				//このアクションが紐づく武器タイプを逆引き検索
				WeaponType t = a.getWeaponType();
				if (t == null) {
					throw new GameSystemException("uneqip, but this action is not weapon action : " + this);
				}
				EqipSlot slot = null;
				Map<EqipSlot, Item> eqip = user.getStatus().getEqip();
				for (EqipSlot e : EqipSlot.values()) {
					if (!eqip.containsKey(e)) {
						continue;
					}
					if (eqip.get(e) == null) {
						continue;
					}
					if (eqip.get(e).getWeaponType() == t) {
						slot = e;
						break;
					}
				}
				if (slot == null) {
					throw new GameSystemException("uneqip, but item not found : " + this);
				}
				Item tgtItem = eqip.get(slot);
				user.getStatus().unEqip(slot);

				//アイテムドロップ
				user.getStatus().getItemBag().drop(tgtItem);

				//ドロップアイテムに追加
				if (tgt instanceof Enemy) {
					((Enemy) tgt).getDropItem().add(DropItem.itemOf(tgtItem, 1, 1f));
				} else {
					//PCの場合アイテムバッグに追加、追加できなかったらロストする
					if (tgt.getStatus().getItemBag().canAdd()) {
						tgt.getStatus().getItemBag().add(tgtItem);
					}
				}
				return getResult(true, tgt);
			}
			case TGTを逃げられる位置に転送: {
			}
			case TGTIDのマップIDのランダムな出口ノードに転送: {
			}
			case TGTの行動をVALUE回数この直後に追加: {
			}
			case TGTの行動をVALUE回数ターン最後に追加: {
			}
			case TGTの魔法詠唱を中断: {
			}
			case TGTの魔法詠唱完了をVALUEターン分ずらす: {
			}
			case 詠唱完了イベントをVALUEターン内で反転: {
			}
			case USERのクローンをパーティーまたはENEMYに追加: {
			}
			case TGTを一番近い敵対者の至近距離に転送: {
			}
			case USERをTGTの至近距離に転送: {
			}
			case このターンのTGTの行動を破棄: {
			}
			case このターンのTGTの行動をこの次にする: {
			}
			case このターンのTGTの行動を最後にする: {
			}
			case このターンの行動順を反転させる: {
			}
			case カレントマップのランダムな出口ノードに転送: {
			}
			case 中心位置からVALUEの場所に転送: {
			}
			case TGTを術者の近くに転送: {
			}
			case 逃走で戦闘終了: {
			}
			case ドロップアイテム追加: {
			}
			case ノックバック＿弱: {
			}
			case ノックバック＿中: {
			}
			case ノックバック＿強: {
			}
			case カレントセーブデータロスト: {
			}
			case カレント以外のセーブデータを１つロスト: {
			}
			case セーブデータ全ロスト: {
			}
			case クラッシュの術式: {
			}
			case DC_CPUのコア数: {
			}
			case DC_USERの持っているアイテムの重さ: {
			}
			case DC_ターン数が大きい: {
			}
			case DC_ターン数が小さい: {
			}
			case DC_ファイル選択からのハッシュ: {
			}
			case DC_ファイル選択からのサイズ: {
			}
			case DC_倒した敵の数: {
			}
			case DC_ランダム属性のランダムダメージ: {
			}
			case DC_減っている体力: {
			}
			case DC_減っている正気度: {
			}
			case DC_減っている魔力: {
			}
			case USERとTGTの位置を交換: {
			}
			case USERによる指定IDの魔法の詠唱完了をこのターンの最後にVALUE回数追加: {
			}
			case USERの指定スロットの装備品の価値をVALUE倍にする: {
			}
			case USERの指定スロットの装備品の攻撃回数をVALUE上げる: {
			}
			case マップIDと座標を入力させて移動する: {
			}
			case TGTIDのCSVにあるアイテムのいずれかをUSERに追加: {
			}
			case WEBサイト起動: {
			}
			default:
				throw new AssertionError("undefined event type : " + this);
		}

	}

	private ActionResult.EventResult getResult(boolean is成功, Actor tgt) {
		if (is成功) {
			tgt.getStatus().addWhen0Condition();
			ActionResult.EventResult er = new ActionResult.EventResult(tgt, ActionResultSummary.成功, this);
			//ERへのアニメーションなどのセット
			if (this.tgtAnimation != null) {
				er.tgtAnimation = this.tgtAnimation.clone();
				er.tgtAnimation.setLocationByCenter(tgt.getSprite().getCenter());
				er.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (this.otherAnimation != null) {
				er.otherAnimation = this.otherAnimation.clone();
				er.otherAnimation.setLocation(0, 0);
				er.otherAnimation.getAnimation().setRepeat(false);
			}
			StatusValueSet tgtVs = tgt.getStatus().getDamageFromSavePoint();
			if (tgtVs.contains(StatusKey.体力)) {
				er.tgtDamageHp = (int) tgtVs.get(StatusKey.体力).getValue();
			}
			if (tgtVs.contains(StatusKey.魔力)) {
				er.tgtDamageMp = (int) tgtVs.get(StatusKey.魔力).getValue();
			}
			if (tgtVs.contains(StatusKey.正気度)) {
				er.tgtDamageSAN = (int) tgtVs.get(StatusKey.正気度).getValue();
			}
			er.tgtIsDead = tgt.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊);
			if (successSound != null) {
				successSound.load().stopAndPlay();
			}
			return er;
		}
		return new ActionResult.EventResult(tgt, ActionResultSummary.失敗＿計算結果０, this);
	}

	private ActionResult.EventResult convert(DamageCalcSystem.Result r) {
		r.param.tgt.getStatus().addWhen0Condition();
		ActionResult.EventResult er = new ActionResult.EventResult(
				r.param.tgt,
				r.summary,
				this);
		//ERへのアニメーションなどのセット
		if (r.summary == ActionResultSummary.成功
				|| r.summary == ActionResultSummary.成功＿クリティカル
				|| r.summary == ActionResultSummary.成功＿ブロックされたが１以上) {
			if (this.tgtAnimation != null) {
				er.tgtAnimation = this.tgtAnimation.clone();
				er.tgtAnimation.setLocationByCenter(r.param.tgt.getSprite().getCenter());
				er.tgtAnimation.getAnimation().setRepeat(false);
			}
			if (this.otherAnimation != null) {
				er.otherAnimation = this.otherAnimation.clone();
				er.otherAnimation.setLocation(0, 0);
				er.otherAnimation.getAnimation().setRepeat(false);
			}
			StatusValueSet tgtVs = r.param.tgt.getStatus().getDamageFromSavePoint();
			if (tgtVs.contains(StatusKey.体力)) {
				er.tgtDamageHp = (int) tgtVs.get(StatusKey.体力).getValue();
			}
			if (tgtVs.contains(StatusKey.魔力)) {
				er.tgtDamageMp = (int) tgtVs.get(StatusKey.魔力).getValue();
			}
			if (tgtVs.contains(StatusKey.正気度)) {
				er.tgtDamageSAN = (int) tgtVs.get(StatusKey.正気度).getValue();
			}
			er.tgtIsDead = r.param.tgt.getStatus().hasAnyCondition(ConditionKey.解脱, ConditionKey.損壊);
			if (successSound != null) {
				successSound.load().stopAndPlay();
			}
		}
		return er;
	}

	//このイベントの情報を返す
	public String getDescI18Nd() {
		StringBuilder sb = new StringBuilder();
		sb.append(getEventType().getVisibleName()).append(":").append(type.getVisibleName());
		sb.append(type.getEventDescI18Nd(this));

		return sb.toString();
	}

	ActionEvent setEventType(ActionEventType type) {
		this.type = type;
		return this;
	}

	ActionEvent setTerms(List<Term> terms) {
		this.terms = terms;
		return this;
	}

	ActionEvent setAtkAttr(AttributeKey atkAttr) {
		this.atkAttr = atkAttr;
		return this;
	}

	ActionEvent setTgtStatusKey(StatusKey tgtStatusKey) {
		this.tgtStatusKey = tgtStatusKey;
		return this;
	}

	ActionEvent setP(float p) {
		this.p = p;
		return this;
	}

	ActionEvent setTgtID(String tgtID) {
		this.tgtId = tgtID;
		return this;
	}

	ActionEvent setTgtConditionKey(ConditionKey tgtConditionKey) {
		this.tgtConditionKey = tgtConditionKey;
		return this;
	}

	ActionEvent setTgtAttrKeyOut(AttributeKey tgtAttrKeyOut) {
		this.tgtAttrKeyOut = tgtAttrKeyOut;
		return this;
	}

	ActionEvent setTgtAttrKeyin(AttributeKey tgtAttrKeyin) {
		this.tgtAttrKeyIn = tgtAttrKeyin;
		return this;
	}

	ActionEvent setValue(float value) {
		this.value = value;
		return this;
	}

	ActionEvent setCalcMode(CalcMode calcMode) {
		this.calcMode = calcMode;
		return this;
	}

	ActionEvent setCndTime(int time) {
		this.cndTime = time;
		return this;
	}

	ActionEvent setSuccessSound(Sound successSound) {
		this.successSound = successSound;
		return this;
	}

	ActionEvent setTgtAnimation(AnimationSprite tgtAnimation) {
		this.tgtAnimation = tgtAnimation;
		return this;
	}

	ActionEvent setOtherAnimation(AnimationSprite otherAnimation) {
		this.otherAnimation = otherAnimation;
		return this;
	}

	ActionEvent setNoLimit(boolean s) {
		this.noLimit = s;
		return this;
	}

	ActionEvent setSort(int sort) {
		this.sort = sort;
		return this;
	}

	ActionEvent setCndRegist(ConditionKey r) {
		this.tgtCndRegist = r;
		return this;
	}

	public String getId() {
		return id;
	}

	public int getCndTime() {
		return cndTime;
	}

	public ConditionKey getTgtCndRegist() {
		return tgtCndRegist;
	}

	public ActionEventType getEventType() {
		return type;
	}

	public boolean isNoLimit() {
		return noLimit;
	}

	public AttributeKey getAtkAttr() {
		return atkAttr;
	}

	public String getTgtID() {
		return tgtId;
	}

	public List<Term> getTerms() {
		return terms;
	}

	public StatusKey getTgtStatusKey() {
		return tgtStatusKey;
	}

	public float getP() {
		return p;
	}

	public ConditionKey getTgtConditionKey() {
		return tgtConditionKey;
	}

	public AttributeKey getTgtAttrOut() {
		return tgtAttrKeyOut;
	}

	public AttributeKey getTgtAttrIn() {
		return tgtAttrKeyIn;
	}

	public float getValue() {
		return value;
	}

	public CalcMode getCalcMode() {
		return calcMode;
	}

	public Sound getSuccessSound() {
		return successSound;
	}

	public AnimationSprite getTgtAnimation() {
		return tgtAnimation;
	}

	public AnimationSprite getOtherAnimation() {
		return otherAnimation;
	}

	public int getSort() {
		return sort;
	}

	@Deprecated
	@Override
	public String getName() {
		return id;
	}

	@Override
	public String toString() {
		return "ActionEvent{" + "id=" + id + ", type=" + type + '}';
	}

	@Override
	public int compareTo(ActionEvent o) {
		return sort - o.sort;
	}

}
