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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;
import kinugasa.game.GameOption;
import kinugasa.game.GraphicsContext;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLACK;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLACK_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLUE;
import static kinugasa.game.system.AnimationMoveType.BEAM_BLUE_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_GREN;
import static kinugasa.game.system.AnimationMoveType.BEAM_GREN_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_RED;
import static kinugasa.game.system.AnimationMoveType.BEAM_RED_THICK;
import static kinugasa.game.system.AnimationMoveType.BEAM_WHITE;
import static kinugasa.game.system.AnimationMoveType.BEAM_WHITE_THICK;
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
import static kinugasa.game.system.ParameterType.ITEM_ADD;
import kinugasa.graphics.Animation;
import kinugasa.graphics.GraphicsUtil;
import kinugasa.object.AnimationSprite;
import kinugasa.object.BasicSprite;
import kinugasa.object.EmptySprite;
import kinugasa.object.ImagePainterStorage;
import kinugasa.object.Sprite;
import kinugasa.resource.*;
import kinugasa.resource.db.*;
import kinugasa.util.FrameTimeCounter;
import kinugasa.util.MathUtil;
import kinugasa.util.Random;

/**
 *
 * @vesion 1.0.0 - 2022/12/01_21:23:55<br>
 * @author Shinacho<br>
 */
@DBRecord
public class ActionEvent implements Comparable<ActionEvent>, Nameable, Cloneable {

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
			Actor c = tgt.getUser();
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
				case ITEM_ADD:
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
		for (Actor c : tgt) {
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
				case ITEM_ADD:
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
		return animationMoveType != null;
	}

	public AnimationSprite createAnimationSprite(Point2D.Float user, Point2D.Float tgt) {
		if (animationMoveType.toString().contains("BEAM")) {
			final float widthBase = switch (animationMoveType) {
				case BEAM_WHITE, BEAM_BLACK, BEAM_BLUE, BEAM_GREN, BEAM_RED ->
					3f;
				case BEAM_WHITE_THICK, BEAM_BLACK_THICK, BEAM_BLUE_THICK, BEAM_GREN_THICK, BEAM_RED_THICK ->
					8f;
				default ->
					throw new AssertionError("ActionEvent " + this + " undefined animation move type : " + animationMoveType);
			};
			final Color color = switch (animationMoveType) {
				case BEAM_WHITE, BEAM_WHITE_THICK ->
					GraphicsUtil.transparent(Color.WHITE, 128);
				case BEAM_BLACK, BEAM_BLACK_THICK ->
					GraphicsUtil.transparent(Color.BLACK, 128);
				case BEAM_BLUE, BEAM_BLUE_THICK ->
					GraphicsUtil.transparent(Color.BLUE, 128);
				case BEAM_GREN, BEAM_GREN_THICK ->
					GraphicsUtil.transparent(Color.GREEN, 128);
				case BEAM_RED, BEAM_RED_THICK ->
					GraphicsUtil.transparent(Color.RED, 128);
				default ->
					throw new AssertionError("ActionEvent " + this + " undefined animation move type : " + animationMoveType);
			};

			List<Sprite> sp = new ArrayList<>();
			final int max = 64;
			for (int i = 0; i < max; i++) {
				final int w = i;
				sp.add(new BasicSprite(
						0, 0,
						GameOption.getInstance().getWindowSize().width / GameOption.getInstance().getDrawSize(),
						GameOption.getInstance().getWindowSize().height / GameOption.getInstance().getDrawSize()) {

					Point2D.Float p1 = (Point2D.Float) user.clone();
					Point2D.Float p2 = (Point2D.Float) tgt.clone();

					@Override
					public void draw(GraphicsContext g) {
						Graphics2D g2 = g.create();
						g2.setColor(color);
						g2.setStroke(new BasicStroke(Random.randomFloat() + widthBase));
						g2.drawLine((int) p1.x, (int) p1.y, (int) p2.x, (int) p2.y);
						g2.dispose();
					}
				});
			}
			Animation a = Animation.of(new FrameTimeCounter(4), sp);
			a.setRepeat(false);
			return new AnimationSprite(a);//0の場合出ない。
		}
		if (animation == null) {
			return new AnimationSprite();
		}
		Animation a = animation.clone();
		AnimationSprite s = new AnimationSprite(animation) {
			final Rectangle2D.Float tgtR = new EmptySprite(tgt.x, tgt.y, 32, 32).getBounds();

			@Override
			public void update() {
				super.update();
				if (getSpeed() != 0) {
					if (tgtR.contains(getCenter())) {
						getAnimation().setStop(true);
						setVisible(false);
					}
				} else {
					if (a.isEnded()) {
						setVisible(false);
					}
				}
			}

		};
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

	@Override
	protected ActionEvent clone() {
		try {
			return (ActionEvent) super.clone(); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/OverriddenMethodBody
		} catch (CloneNotSupportedException ex) {
			throw new InternalError(ex);
		}
	}

}
