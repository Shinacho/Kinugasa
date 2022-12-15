package kinugasa.game.system;

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
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.List;
import kinugasa.graphics.Animation;
import kinugasa.object.AnimationSprite;
import kinugasa.object.KVector;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_21:23:55<br>
 * @author Dra211<br>
 */
public class ActionEvent implements Comparable<ActionEvent> {

	private TargetType targetType;
	private ParameterType parameterType;
	private String tgtName;
	private AttributeKey attr;
	private float value;
	private StatusDamageCalcType damageCalcType;
	private float p;
	private float spread;
	private Animation animation;
	private AnimationMoveType animationMoveType = AnimationMoveType.NONE;

	public ActionEvent(TargetType tt, ParameterType pt) {
		this.targetType = tt;
		this.parameterType = pt;
	}

	public ActionEvent setTgtName(String tgtName) {
		this.tgtName = tgtName;
		return this;
	}

	public ActionEvent setValue(float value) {
		this.value = value;
		return this;
	}

	public ActionEvent setAttr(AttributeKey attr) {
		this.attr = attr;
		return this;
	}

	public AttributeKey getAttr() {
		return attr;
	}

	public ActionEvent setDamageCalcType(StatusDamageCalcType damageCalcType) {
		this.damageCalcType = damageCalcType;
		return this;
	}

	public ActionEvent setP(float p) {
		this.p = p;
		return this;
	}

	public ActionEvent setSpread(float spread) {
		this.spread = spread;
		return this;
	}

	public ActionEvent setAnimation(Animation animation) {
		this.animation = animation;
		return this;
	}

	public ActionEvent setAnimationMoveType(AnimationMoveType animationMoveType) {
		this.animationMoveType = animationMoveType;
		return this;
	}

	//この実行はダメージ計算式を使用しない
	//ターゲットシステムによりAREA内の正しい敵が入っている前提。
	//FIELDターゲットの成否は、RESULTから取れる。アクションのDESCを表示できる。
	public ActionEventResult exec(BattleActionTarget tgt) {
		//すべてのtgtに対して行う。その結果をTypeとして返す
		//フィールドアクションの場合
		if (tgt.isFieldTarget() && targetType == TargetType.FIELD) {
			List<ActionResultType> resultTypePerTgt = new ArrayList<>();
			List<AnimationSprite> ani = new ArrayList<>();
			assert tgt.isInField() : "field action, but not in field";
			if (!Random.percent(p)) {
				resultTypePerTgt.add(ActionResultType.MISS);
				return new ActionEventResult(resultTypePerTgt, ani);
			}
			resultTypePerTgt.add(ActionResultType.SUCCESS);
			AnimationSprite a = new AnimationSprite(getAnimationClone());
			a.setLocationByCenter(GameSystem.getInstance().getBattleSystem().getBattleFieldSystem().getBattleFieldAllArea().getCenter());
			ani.add(a);
			return new ActionEventResult(resultTypePerTgt, ani);
		}
		//tt != FIELD,ターゲットタイプに基づくターゲットが引数に入っている前提。
		ActionEventResult result = new ActionEventResult();
		for (BattleCharacter c : tgt) {
			//P判定
			if (!Random.percent(p)) {
				if (GameSystem.isDebugMode()) {
					System.out.println(this + " is no exec");
				}
				result.addResultTypePerTgt(ActionResultType.MISS);
				continue;
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
							c.getStatus().getBaseAttrIn().get(tgtName).set(value);
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
					c.getStatus().getItemBag().drop(tgtName);
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case REMOVE_CONDITION:
					c.getStatus().removeCondition(tgtName);
					result.addResultTypePerTgt(ActionResultType.SUCCESS);
					break;
				case STATUS:
					switch (damageCalcType) {
						case DIRECT:
							c.getStatus().getBaseStatus().get(tgtName).set(value);
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
		if (animationMoveType == AnimationMoveType.NONE) {
			s.setLocationByCenter(tgt);
			return s;
		}
		KVector v = new KVector();
		if (animationMoveType.toString().startsWith("TGT_TO")) {
			v.setAngle(tgt, user);
		} else if (animationMoveType.toString().startsWith("USER_TO")) {
			v.setAngle(user, tgt);
		}
		v.setSpeed(animationMoveType.getSpeed());
		s.setVector(v);
		return s;
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

	public float getValue() {
		return value;
	}

	public StatusDamageCalcType getDamageCalcType() {
		return damageCalcType;
	}

	public float getP() {
		return p;
	}

	public float getSpread() {
		return spread;
	}

	@Override
	public String toString() {
		return "ActionEvent{" + "targetType=" + targetType + ", parameterType=" + parameterType + ", tgtName=" + tgtName + ", value=" + value + '}';
	}

	@Override
	public int compareTo(ActionEvent o) {
		return parameterType.getValue() - o.parameterType.getValue();
	}

}
