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
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.util.Objects;
import static kinugasa.game.system.ParameterType.ADD_CONDITION;
import static kinugasa.game.system.ParameterType.ATTR_IN;
import static kinugasa.game.system.ParameterType.ITEM_LOST;
import static kinugasa.game.system.ParameterType.NONE;
import static kinugasa.game.system.ParameterType.REMOVE_CONDITION;
import static kinugasa.game.system.ParameterType.STATUS;
import static kinugasa.game.system.DamageCalcType.DIRECT;
import static kinugasa.game.system.DamageCalcType.PERCENT_OF_MAX;
import static kinugasa.game.system.DamageCalcType.PERCENT_OF_NOW;
import static kinugasa.game.system.DamageCalcType.USE_DAMAGE_CALC;
import kinugasa.graphics.Animation;
import kinugasa.graphics.ImageEditor;
import kinugasa.object.AnimationSprite;
import kinugasa.resource.KImage;
import kinugasa.resource.*;
import kinugasa.resource.db.*;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_21:23:55<br>
 * @author Shinacho<br>
 */
@DBRecord
public class ActionEvent implements Comparable<ActionEvent>, Nameable {

	private String id, desc;
	private TargetType targetType;
	private ParameterType parameterType;
	private String tgtName;
	private AttributeKey attr;
	private float value;
	private DamageCalcType damageCalcType;
	private float p;
	private float spread;
	private Animation animation;
	private AnimationMoveType animationMoveType = AnimationMoveType.TGT;
	private boolean battle, field;

	@Override
	public String getName() {
		return id;
	}

	public boolean isBattle() {
		return battle;
	}

	public void setBattle(boolean battle) {
		this.battle = battle;
	}

	public boolean isField() {
		return field;
	}

	public void setField(boolean field) {
		this.field = field;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public ActionEvent(String id) {
		this.id = id;
	}

	public ActionEvent(String id, TargetType tt, ParameterType pt) {
		this.id = id;
		this.targetType = tt;
		this.parameterType = pt;
	}

	public void setTargetType(TargetType targetType) {
		this.targetType = targetType;
	}

	public void setParameterType(ParameterType parameterType) {
		this.parameterType = parameterType;
	}

	public void setTgtName(String tgtName) {
		this.tgtName = tgtName;
	}

	public void setAttr(AttributeKey attr) {
		this.attr = attr;
	}

	public void setValue(float value) {
		this.value = value;
	}

	public void setDamageCalcType(DamageCalcType damageCalcType) {
		this.damageCalcType = damageCalcType;
	}

	public void setP(float p) {
		this.p = p;
	}

	public void setSpread(float spread) {
		this.spread = spread;
	}

	public void setAnimation(Animation animation) {
		this.animation = animation;
	}

	public void setAnimationMoveType(AnimationMoveType animationMoveType) {
		this.animationMoveType = animationMoveType;
	}

	public TargetType getTargetType() {
		return targetType;
	}

	public ParameterType getParameterType() {
		return parameterType;
	}

	public String getTgtName() {
		return tgtName;
	}

	public AttributeKey getAttr() {
		return attr;
	}

	public float getValue() {
		return value;
	}

	public DamageCalcType getDamageCalcType() {
		return damageCalcType;
	}

	public float getP() {
		return p;
	}

	public float getSpread() {
		return spread;
	}

	public AnimationMoveType getAnimationMoveType() {
		return animationMoveType;
	}

	//この実行はダメージ計算式を使用しない
	//ターゲットシステムによりAREA内の正しい敵が入っている前提。
	//FIELDターゲットの成否は、RESULTから取れる。アクションのDESCを表示できる。
	public ActionEventResult exec(ActionTarget tgt) {
		//フィールドモードの場合、アニメーションは無視される。
		//すべてのtgtに対して行う。その結果をTypeとして返す

		ActionEventResult result = new ActionEventResult();
		//セルフイベントの場合
		if (targetType == TargetType.SELF) {			//P判定
			if (!Random.percent(p)) {
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print(this + " is no exec(P)");
				}
				result.addResultTypePerTgt(ActionResultType.MISS);
				return result;
			}
			BattleCharacter c = tgt.getUser();
			//実行可能
			switch (parameterType) {
				case NONE:
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case ADD_CONDITION:
					c.getStatus().addCondition(tgtName);
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case ATTR_IN:
					switch (damageCalcType) {
						case DIRECT:
							c.getStatus().getBaseAttrIn().get(tgtName).add(value);
							break;
						case PERCENT_OF_MAX:
							float v1 = c.getStatus().getBaseAttrIn().get(tgtName).getMax() * value;
							c.getStatus().getBaseAttrIn().get(tgtName).set(v1);
							break;
						case PERCENT_OF_NOW:
							float v2 = c.getStatus().getBaseAttrIn().get(tgtName).getValue() * value;
							c.getStatus().getBaseAttrIn().get(tgtName).set(v2);
							break;
						//ATTR_INではダメージ計算式は使えない
						case USE_DAMAGE_CALC:
							throw new GameSystemException("cant user damage calc model " + this);
						default:
							throw new AssertionError("undefined damageCalcType " + this);
					}
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					if (hasAnimation()) {
						result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
					}
					break;
				case ITEM_LOST:
					break;
				case REMOVE_CONDITION:
					c.getStatus().removeCondition(tgtName);
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case STATUS:
					switch (damageCalcType) {
						case DIRECT:
							c.getStatus().getBaseStatus().get(tgtName).add(value);
							result.addResultTypePerTgt(ActionResultType.SUCCESS);
							if (hasAnimation()) {
								result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
							}
							break;
						case PERCENT_OF_MAX:
							float v3 = c.getStatus().getBaseStatus().get(tgtName).getMax() * value;
							c.getStatus().getBaseStatus().get(tgtName).set(v3);
							result.addResultTypePerTgt(ActionResultType.SUCCESS);
							if (hasAnimation()) {
								result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
							}
							break;
						case PERCENT_OF_NOW:
							float v4 = c.getStatus().getBaseStatus().get(tgtName).getValue() * value;
							c.getStatus().getBaseStatus().get(tgtName).set(v4);
							result.addResultTypePerTgt(ActionResultType.SUCCESS);
							if (hasAnimation()) {
								result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
							}
							break;
						case USE_DAMAGE_CALC:
							result.add(StatusDamageCalcModelStorage.getInstance().getCurrent().exec(tgt.getUser(), this, c));
							break;
						default:
							throw new AssertionError("undefined damageCalcType " + this);
					}
					break;
				default:
					throw new AssertionError("indefined parameter type " + this);

			}
			return result;
		}

		//tt != FIELD,ターゲットタイプに基づくターゲットが引数に入っている前提。
		for (BattleCharacter c : tgt) {
			//P判定
			if (!Random.percent(p)) {
				if (GameSystem.isDebugMode()) {
					kinugasa.game.GameLog.print(this + " is no exec(P)");
				}
				result.addResultTypePerTgt(ActionResultType.MISS);
				continue;
			}
			if (GameSystem.isDebugMode()) {
				kinugasa.game.GameLog.print("ACTION:" + c.getName() + ":" + parameterType + ":" + damageCalcType + ":" + tgtName + ":" + value);
			}

			//実行可能
			switch (parameterType) {
				case NONE:
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case ADD_CONDITION:
					c.getStatus().addCondition(tgtName);
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case ATTR_IN:
					switch (damageCalcType) {
						case DIRECT:
							c.getStatus().getBaseAttrIn().get(tgtName).add(value);
							break;
						case PERCENT_OF_MAX:
							float v1 = c.getStatus().getBaseAttrIn().get(tgtName).getMax() * value;
							c.getStatus().getBaseAttrIn().get(tgtName).set(v1);
							break;
						case PERCENT_OF_NOW:
							float v2 = c.getStatus().getBaseAttrIn().get(tgtName).getValue() * value;
							c.getStatus().getBaseAttrIn().get(tgtName).set(v2);
							break;
						//ATTR_INではダメージ計算式は使えない
						case USE_DAMAGE_CALC:
							throw new GameSystemException("cant user damage calc model " + this);
						default:
							throw new AssertionError("undefined damageCalcType " + this);
					}
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					if (hasAnimation()) {
						result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
					}
					break;
				case ITEM_LOST:
					//アイテムロストは使用側で実施すること
//					if (targetType == TargetType.SELF) {
//						tgt.getUser().getStatus().getItemBag().drop(tgtName);
//					} else {
//						c.getStatus().getItemBag().drop(tgtName);
//					}
//					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case REMOVE_CONDITION:
					c.getStatus().removeCondition(tgtName);
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case STATUS:
					switch (damageCalcType) {
						case DIRECT:
							c.getStatus().getBaseStatus().get(tgtName).add(value);
							result.addResultTypePerTgt(ActionResultType.SUCCESS);
							if (hasAnimation()) {
								result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
							}
							break;
						case PERCENT_OF_MAX:
							float v3 = c.getStatus().getBaseStatus().get(tgtName).getMax() * value;
							c.getStatus().getBaseStatus().get(tgtName).set(v3);
							result.addResultTypePerTgt(ActionResultType.SUCCESS);
							if (hasAnimation()) {
								result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
							}
							break;
						case PERCENT_OF_NOW:
							float v4 = c.getStatus().getBaseStatus().get(tgtName).getValue() * value;
							c.getStatus().getBaseStatus().get(tgtName).set(v4);
							result.addResultTypePerTgt(ActionResultType.SUCCESS);
							if (hasAnimation()) {
								result.addAnimation(createAnimationSprite(tgt.getUser().getCenter(), c.getCenter()));
							}
							break;
						case USE_DAMAGE_CALC:
							result.add(StatusDamageCalcModelStorage.getInstance().getCurrent().exec(tgt.getUser(), this, c));
							break;
						default:
							throw new AssertionError("undefined damageCalcType " + this);
					}
					break;
				default:
					throw new AssertionError("indefined parameter type " + this);

			}
		}

		if (GameSystem.isDebugMode()) {
			kinugasa.game.GameLog.print("ACTION RESULT:" + result);
		}
		return result;
	}

	@Deprecated
	public Animation getAnimation() {
		return animation;
	}

	public boolean hasAnimation() {
		return animation != null;
	}

	public Animation getAnimationClone() {
		if (animation == null) {
			return null;
		}
		return animation.clone();
	}

	private AnimationSprite createAnimationSprite(Point2D.Float user, Point2D.Float tgt) {
		Animation a = getAnimationClone();
		AnimationSprite s = new AnimationSprite(animation);
		if (animationMoveType == AnimationMoveType.ROTATE_TGT_TO_USER) {
			//回転の場合
			KImage[] images = a.getImages();

			float kakudo = animationMoveType.createVector(user, tgt).angle + 90f;
			BufferedImage[] newImages = new BufferedImage[images.length];
			for (int i = 0; i < images.length; i++) {
				newImages[i] = ImageEditor.rotate(images[i].get(), kakudo, null);
			}

			a.setImages(images);
		}
		if (animationMoveType == AnimationMoveType.NONE) {
			s.setVisible(false);
			return s;
		}
		if (animationMoveType == AnimationMoveType.TGT) {
			s.setLocationByCenter(tgt);
			return s;
		}
		if (animationMoveType == AnimationMoveType.USER) {
			s.setLocationByCenter(user);
			return s;
		}
		s.setVector(animationMoveType.createVector(user, tgt));
		return s;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 37 * hash + Objects.hashCode(this.id);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ActionEvent other = (ActionEvent) obj;
		return Objects.equals(this.id, other.id);
	}

	@Override
	public String toString() {
		return "ActionEvent{" + "id=" + id + ", targetType=" + targetType + ", parameterType=" + parameterType + ", tgtName=" + tgtName + ", attr=" + attr + ", value=" + value + ", damageCalcType=" + damageCalcType + ", p=" + p + ", spread=" + spread + ", animation=" + animation + ", animationMoveType=" + animationMoveType + '}';
	}

	@Override
	public int compareTo(ActionEvent o) {
		return parameterType.getValue() - o.parameterType.getValue();
	}

}
