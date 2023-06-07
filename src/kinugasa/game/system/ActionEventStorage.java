/*
 * The MIT License
 *
 * Copyright 2023 Shinacho.
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import kinugasa.graphics.Animation;
import kinugasa.graphics.SpriteSheet;
import kinugasa.resource.db.DBStorage;
import kinugasa.resource.db.*;
import kinugasa.util.FrameTimeCounter;

/**
 *
 * @vesion 1.0.0 - May 29, 2023_11:01:26 AM<br>
 * @author Shinacho<br>
 */
public class ActionEventStorage extends DBStorage<ActionEvent> {

	private static final ActionEventStorage INSTANCE = new ActionEventStorage();

	public static ActionEventStorage getInstance() {
		return INSTANCE;
	}

	@Override
	protected ActionEvent select(String id) throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select"
					+ " ActionEventID,"
					+ "battle,"
					+ "field,"
					+ "targetType,"
					+ "parameterType,"
					+ "targetName,"
					+ "attr,"
					+ "val,"
					+ "p,"
					+ "spread,"
					+ "dct,"
					+ "desc,"
					+ "animationID,"
					+ "animationMoveType"
					+ " from ActionEvent where ActionEventID='" + id + "';");
			if (kr.isEmpty()) {
				return null;
			}
			for (List<DBValue> v : kr) {
				String actionEventID = v.get(0).get();
				boolean battle = v.get(1).asBoolean();
				boolean field = v.get(2).asBoolean();
				if (!battle && !field) {
					throw new GameSystemException("this action is cant use in battle and field : " + v);
				}
				String tgtName = v.get(5).get();
				float val = v.get(7).asFloat();
				float p = v.get(8).asFloat();
				float spread = v.get(9).asFloat();
				String desc = v.get(11).get();
				String animationID = v.get(12).get();
				ActionEvent e = new ActionEvent(actionEventID);

				if (v.get(3).get() != null && !v.get(3).get().isEmpty()) {
					TargetType tt = v.get(3).of(TargetType.class);
					e.setTargetType(tt);
				}
				if (v.get(4).get() != null && !v.get(4).get().isEmpty()) {
					ParameterType pr = v.get(4).of(ParameterType.class);
					e.setParameterType(pr);
				}

				e.setTgtName(tgtName);
				if (v.get(6).get() != null && !v.get(6).get().isEmpty()) {
					AttributeKey attr = AttributeKeyStorage.getInstance().get(v.get(6).get());
					e.setAttr(attr);
				}
				e.setValue(val);
				e.setP(p);
				e.setSpread(spread);
				if (v.get(10) != null && !v.get(10).get().isEmpty()) {
					DamageCalcType dct = v.get(10).of(DamageCalcType.class);
					e.setDamageCalcType(dct);
				}
				e.setDesc(desc);
				if (v.get(13) != null && !v.get(13).get().isEmpty()) {
					AnimationMoveType amt = v.get(13).of(AnimationMoveType.class);
					e.setAnimationMoveType(amt);
				}
				//アニメーション取得
				KResultSet akr = DBConnection.getInstance().execDirect("select animationID, fileName, w, h, tc, mg from actionanimation where animationID='" + animationID + "';");
				if (!akr.isEmpty()) {
					for (List<DBValue> vv : akr) {
						//1件しかヒットしない
						String fileName = vv.get(1).get();
						int w = vv.get(2).asInt();
						int h = vv.get(3).asInt();
						int[] tc = vv.get(4).asIntArray(",");
						float mg = vv.get(5).asFloat();
						Animation a = new Animation(new FrameTimeCounter(tc),
								new SpriteSheet(fileName).rows(0, w, h).resizeAll(mg).images());
						e.setAnimation(a);
					}
				}
				return e;
			}
		}
		return null;
	}

	@Override
	protected List<ActionEvent> selectAll() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			KResultSet kr = DBConnection.getInstance().execDirect("select"
					+ " ActionEventID,"
					+ "battle,"
					+ "field,"
					+ "targetType,"
					+ "parameterType,"
					+ "targetName,"
					+ "attr,"
					+ "val,"
					+ "p,"
					+ "spread,"
					+ "dct,"
					+ "desc,"
					+ "animationID,"
					+ "animationMoveType"
					+ " from ActionEvent;");
			if (kr.isEmpty()) {
				return Collections.emptyList();
			}
			Map<String, Animation> animations = new HashMap<>();

			//アニメーション取得
			KResultSet akr = DBConnection.getInstance().execDirect("select animationID, fileName, w, h, tc, mg from actionanimation;");
			if (!akr.isEmpty()) {
				for (List<DBValue> vv : akr) {
					//1件しかヒットしない
					String animationID = vv.get(0).get();
					String fileName = vv.get(1).get();
					int w = vv.get(2).asInt();
					int h = vv.get(3).asInt();
					int[] tc = vv.get(4).asIntArray(",");
					float mg = vv.get(5).asFloat();
					Animation a = new Animation(new FrameTimeCounter(tc),
							new SpriteSheet(fileName).rows(0, w, h).resizeAll(mg).images());
					animations.put(animationID, a);
				}
			}

			List<ActionEvent> list = new ArrayList<>();
			for (List<DBValue> v : kr) {
				String actionEventID = v.get(0).get();
				boolean battle = v.get(1).asBoolean();
				boolean field = v.get(2).asBoolean();
				if (!battle && !field) {
					throw new GameSystemException("this action is cant use in battle and field : " + v);
				}

				String tgtName = v.get(5).get();
				float val = v.get(7).asFloat();
				float p = v.get(8).asFloat();
				float spread = v.get(9).asFloat();
				String desc = v.get(11).get();
				ActionEvent e = new ActionEvent(actionEventID);
				e.setBattle(battle);
				e.setField(field);
				if (v.get(3).get() != null && !v.get(3).get().isEmpty()) {
					TargetType tt = v.get(3).of(TargetType.class);
					e.setTargetType(tt);
				}
				if (v.get(4).get() != null && !v.get(4).get().isEmpty()) {
					ParameterType pr = v.get(4).of(ParameterType.class);
					e.setParameterType(pr);
				}

				e.setTgtName(tgtName);
				if (v.get(6).get() != null && !v.get(6).get().isEmpty()) {
					AttributeKey attr = AttributeKeyStorage.getInstance().get(v.get(6).get());
					e.setAttr(attr);
				}
				e.setValue(val);
				e.setP(p);
				e.setSpread(spread);
				if (v.get(10) != null && !v.get(10).get().isEmpty()) {
					DamageCalcType dct = v.get(10).of(DamageCalcType.class);
					e.setDamageCalcType(dct);
				}
				e.setDesc(desc);
				if (v.get(13) != null && !v.get(13).get().isEmpty()) {
					AnimationMoveType amt = v.get(13).of(AnimationMoveType.class);
					e.setAnimationMoveType(amt);
				}
				String animationID = v.get(12).get();
				if (animationID != null && !animationID.trim().isEmpty()) {
					e.setAnimation(animations.get(animationID));
				}

				list.add(e);
			}
			return list;
		}
		return Collections.emptyList();
	}

	@Override
	protected int count() throws KSQLException {
		if (DBConnection.getInstance().isUsing()) {
			return DBConnection.getInstance().execDirect("select count(*) from ActionEvent;").cell(0, 0).asInt();
		}
		return 0;
	}

	//BF振り分けは呼び出しもとで！
	public ActionEvents getActionEvents(Action a) {
		String actionID = a.getName();
		ActionEvents res = new ActionEvents();
		if (DBConnection.getInstance().isUsing()) {
			//アクションイベントIDを取得
			KResultSet ks = DBConnection.getInstance().execDirect("select ActionEventID from ACTION_ACTIONEVENT where actionID='" + actionID + "';");
			if (ks.isEmpty()) {
				return res;
			}
			List<String> actionEventIDs = ks.flatMap(p -> p.get());
			List<ActionEvent> all = asList().stream().filter(p -> actionEventIDs.contains(p.getName())).collect(Collectors.toList());
			for (var v : all) {
				if (v.isBattle()) {
					res.battle.add(v);
				}
				if (v.isField()) {
					res.field.add(v);
				}
			}
			return res;
		}
		return res;
	}

	public static class ActionEvents {

		List<ActionEvent> battle = new ArrayList<>();
		List<ActionEvent> field = new ArrayList<>();

	}

}
